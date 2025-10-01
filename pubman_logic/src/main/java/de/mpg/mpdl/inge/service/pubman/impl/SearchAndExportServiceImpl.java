package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.exception.PubManException;
import de.mpg.mpdl.inge.model.valueobjects.*;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerWrapper;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.CitationTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

import static de.mpg.mpdl.inge.service.util.SearchUtils.baseElasticSearchQueryBuilder;
import static de.mpg.mpdl.inge.service.util.SearchUtils.buildDateRangeQuery;

@Service
@Primary
public class SearchAndExportServiceImpl implements SearchAndExportService {

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private ItemTransformingService itemTransformingService;

  public SearchAndExportServiceImpl() {}


  @Override
  public SearchAndExportResultVO exportItems(ExportFormatVO exportFormat, List<ItemVersionVO> itemList, String token)
      throws IngeTechnicalException {

    byte[] result = this.itemTransformingService.getOutputForExport(exportFormat, itemList);
    return getSearchAndExportResult(result, exportFormat, null, itemList, null);
  }

  @Override
  public SearchAndExportResultVO exportItemsWrapped(ExportFormatVO exportFormat, List<ItemVersionVO> itemList, String token)
      throws IngeTechnicalException {

    TransformerWrapper tw = this.itemTransformingService.getTransformationForExport(exportFormat, itemList);
    return getSearchAndExportResult(null, exportFormat, null, itemList, tw);
  }

  @Override
  public SearchAndExportResultVO searchAndExportItems(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);
    //saerrVO.setSearchRetrieveReponseVO(srrVO);

    byte[] result = this.itemTransformingService.getOutputForExport(saerrVO.getExportFormat(), srrVO);

    return getSearchAndExportResult(result, saerrVO.getExportFormat(), srrVO, null, null);
  }

  @Override
  public SearchAndExportResultVO searchAndExportItemsWrapped(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);
    //saerrVO.setSearchRetrieveReponseVO(srrVO);

    TransformerWrapper tw = this.itemTransformingService.getTransformationForExport(saerrVO.getExportFormat(), srrVO);

    return getSearchAndExportResult(null, saerrVO.getExportFormat(), srrVO, null, tw);
  }

  private SearchAndExportResultVO getSearchAndExportResult(byte[] result, ExportFormatVO exportFormat, SearchRetrieveResponseVO srrVO,
      List<ItemVersionVO> itemList, TransformerWrapper tw) {
    String fileName;
    String targetMimeType;
    int totalNumberOfRecords = srrVO != null ? srrVO.getNumberOfRecords() : itemList.size();

    TransformerFactory.FORMAT format = TransformerFactory.getFormat(exportFormat.getFormat());

    fileName = exportFormat.getFormat() + "." + format.getFileFormat().getExtension();
    targetMimeType = format.getFileFormat().getMimeType();

    if (tw != null) {
      ExtendedSearchAndExportResultVO saervo = new ExtendedSearchAndExportResultVO(tw, fileName, targetMimeType, totalNumberOfRecords);
      saervo.setSearchRetrieveResponseVO(srrVO);
      return saervo;
    } else {
      SearchAndExportResultVO saervo = new SearchAndExportResultVO(result, fileName, targetMimeType, totalNumberOfRecords);
      saervo.setSearchRetrieveResponseVO(srrVO);
      return saervo;
    }


  }

  /*
  Only valid for Reports JUS_HTML_XML and JUS_INDESIGN_HTML
   */
  public SearchAndExportResultVO exportJusReport(ExportFormatVO exportFormat, String ouId, String year, String token)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException {

    this.aaService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.REPORTER.frameworkValue());

    String from = year;
    String to = year;

    BoolQuery.Builder dateOrGenreQueryBuilder = QueryBuilders.bool();
    dateOrGenreQueryBuilder.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT, from, to),
        buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE, from, to),
        buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED, from, to),
        buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED, from, to),
        buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_MODIFIED, from, to),
        buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_CREATED, from, to),

        baseElasticSearchQueryBuilder(this.pubItemService.getElasticSearchIndexFields(), PubItemServiceDbImpl.INDEX_METADATA_GENRE,
            MdsPublicationVO.Genre.JOURNAL.name(), MdsPublicationVO.Genre.SERIES.name()));
    Query dateOrGenreQuery = dateOrGenreQueryBuilder.build()._toQuery();

    Query stateQuery = baseElasticSearchQueryBuilder(this.pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_PUBLIC_STATE, ItemVersionRO.State.RELEASED.name());

    BoolQuery.Builder ouQueryBuilder = new BoolQuery.Builder();
    ouQueryBuilder.should(baseElasticSearchQueryBuilder(this.pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH, ouId));
    ouQueryBuilder.should(baseElasticSearchQueryBuilder(this.pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH, ouId));
    ouQueryBuilder.should(baseElasticSearchQueryBuilder(this.pubItemService.getElasticSearchIndexFields(),
        PubItemServiceDbImpl.INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATIONS_IDENTIFIERPATH, ouId));
    Query ouQuery = ouQueryBuilder.build()._toQuery();

    BoolQuery bq = BoolQuery.of(b -> b.must(dateOrGenreQuery, stateQuery, ouQuery));

    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(bq._toQuery(), -2, 0); // unbegrenzte Suche
    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(srr, null);

    List<AffiliationDbVO> childAffs = this.organizationService.searchAllChildOrganizations(new String[] {ouId}, null);
    String affs = childAffs.stream().map(aff -> aff.getObjectId()).collect(Collectors.joining(" "));

    Transformer trans = null;
    try {
      trans = TransformerFactory.newTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO,
          TransformerFactory.getFormat(exportFormat.getFormat()));
      trans.getConfiguration().put("institutsId", affs);
    } catch (TransformationException e) {
      throw new IngeTechnicalException(e.getMessage(), e, PubManException.Reason.TRANSFORMATION_ERROR);
    }

    TransformerWrapper tw = new TransformerWrapper(trans, new TransformerVoSource(srrVO));

    return getSearchAndExportResult(null, exportFormat, srrVO, null, tw);

  }



  private List<ItemVersionVO> getSearchResult(SearchRetrieveResponseVO<ItemVersionVO> srrVO) {
    List<ItemVersionVO> searchResult = new ArrayList<>();
    for (SearchRetrieveRecordVO<ItemVersionVO> record : srrVO.getRecords()) {
      searchResult.add(record.getData());
    }

    return searchResult;
  }

  public class ExtendedSearchAndExportResultVO extends SearchAndExportResultVO {

    TransformerWrapper transformerWrapper;

    public ExtendedSearchAndExportResultVO(TransformerWrapper tw, String fileName, String targetMimeType, int totalNumberOfRecords) {
      super(null, fileName, targetMimeType, totalNumberOfRecords);
      this.transformerWrapper = tw;
    }

    public TransformerWrapper getTransformerWrapper() {
      return transformerWrapper;
    }

    public void setTransformerWrapper(TransformerWrapper transformerWrapper) {
      this.transformerWrapper = transformerWrapper;
    }


  }


}
