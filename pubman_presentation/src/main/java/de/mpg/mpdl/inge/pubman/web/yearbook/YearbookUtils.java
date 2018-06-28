package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

public class YearbookUtils {

  public static BoolQueryBuilder getCandidateQuery() throws Exception {
    final YearbookItemSessionBean yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    BoolQueryBuilder candidateBoolQuery = QueryBuilders.boolQuery();

    candidateBoolQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED"));

    // Genres
    BoolQueryBuilder genreQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(genreQuery);

    String genreProperties = PropertyReader.getProperty(PropertyReader.INGE_YEARBOOK_ALLOWED_GENRES);

    for (String genre : genreProperties.split(",")) {
      genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE, genre.trim()));
    }


    // Exclude items which are already members

    if (yisb.getNumberOfMembers() > 0) {

      candidateBoolQuery.mustNot(getMemberQuery(yisb.getYearbook()));
    }

    // Dates

    String year = String.valueOf((yisb.getYearbook().getYear()));
    String roundedYear = DateSearchCriterion.roundDateString(year);
    BoolQueryBuilder dateBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(dateBoolQuery);
    dateBoolQuery
        .should(QueryBuilders.rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT).gte(roundedYear).lte(roundedYear));
    dateBoolQuery
        .should(QueryBuilders.rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE).gte(roundedYear).lte(roundedYear));

    BoolQueryBuilder thesisDateBoolQuery = QueryBuilders.boolQuery();
    dateBoolQuery.should(thesisDateBoolQuery);
    thesisDateBoolQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE, Genre.THESIS.name()));
    thesisDateBoolQuery.must(QueryBuilders.rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED).gte(roundedYear).lte(roundedYear));


    // Organizations
    String orgId = yisb.getYearbook().getOrganization().getObjectId();
    BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(ouBoolQuery);
    ouBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH, orgId));
    ouBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH, orgId));



    // Contexts
    BoolQueryBuilder contextBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(contextBoolQuery);
    for (String contextId : yisb.getYearbook().getContextIds()) {
      contextBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID, contextId));
    }


    return candidateBoolQuery;


  }

  public static QueryBuilder getMemberQuery(YearbookDbVO yearbookItem) throws Exception {
    if (yearbookItem.getItemIds().size() > 0) {
      return QueryBuilders.termsQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, yearbookItem.getItemIds());
    }
    return null;

    /*
    BoolQueryBuilder bq = QueryBuilders.boolQuery();
    if (yearbookItem.getItemIds().size() > 0) {
      for (final String rel : yearbookItem.getItemIds()) {
        bq.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, rel));
      }
      return bq;
    }
    return null;
    */
  }

  public static List<PubItemVOPresentation> retrieveAllMembers(YearbookDbVO yearbook, String authenticationToken) throws Exception {

    QueryBuilder qb = YearbookUtils.getMemberQuery(yearbook);

    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
    SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, authenticationToken);


    List<ItemVersionVO> resultList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());

    return CommonUtils.convertToPubItemVOPresentationList(resultList);

    /*
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    final MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getYearbookItem());
    final ItemContainerSearchResult result = SearchService.searchForItemContainer(mdQuery);
    pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
    return pubItemList;
    */
  }

  public static List<String> getYearbookOrganizationIds(AccountUserDbVO user) {
    List<String> orgIds = new ArrayList<>();

    for (GrantVO grant : user.getGrantList()) {
      if (grant.getRole().equals(GrantVO.PredefinedRoles.YEARBOOK_EDITOR.frameworkValue())) {
        orgIds.add(grant.getObjectRef());

        break;
      }
    }
    return orgIds;
  }
}
