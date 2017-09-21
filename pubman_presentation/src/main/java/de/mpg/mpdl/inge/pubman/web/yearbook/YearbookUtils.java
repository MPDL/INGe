package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

public class YearbookUtils {

  public static BoolQueryBuilder getCandidateQuery() throws Exception {
    final YearbookItemSessionBean yisb =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    BoolQueryBuilder candidateBoolQuery = QueryBuilders.boolQuery();

    // Genres
    BoolQueryBuilder genreQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(genreQuery);

    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.JOURNAL.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.ARTICLE.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.ISSUE.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.BOOK.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.BOOK_ITEM.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.PROCEEDINGS.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONFERENCE_PAPER.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.PAPER.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.THESIS.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.SERIES.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONTRIBUTION_TO_HANDBOOK.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONTRIBUTION_TO_FESTSCHRIFT.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONTRIBUTION_TO_COLLECTED_EDITION.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.MONOGRAPH.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.HANDBOOK.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.COLLECTED_EDITION.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.FESTSCHRIFT.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.NEWSPAPER_ARTICLE.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONFERENCE_REPORT.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.EDITORIAL.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CONTRIBUTION_TO_COMMENTARY.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.BOOK_REVIEW.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.OPINION.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CASE_STUDY.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.CASE_NOTE.name()));
    genreQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.COMMENTARY.name()));

    // Exclude items which are already members
    if (yisb.getNumberOfMembers() > 0) {
      BoolQueryBuilder memberQuery = QueryBuilders.boolQuery();
      candidateBoolQuery.must(memberQuery);
      for (final String member : yisb.getYearbook().getItemIds()) {
        memberQuery.mustNot(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID,
            member));
      }
    }

    // Dates
    String year = String.valueOf((yisb.getYearbook().getYear()));
    String roundedYear = DateSearchCriterion.roundDateString(year);
    BoolQueryBuilder dateBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(dateBoolQuery);
    dateBoolQuery.should(QueryBuilders
        .rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT).gte(roundedYear)
        .lte(roundedYear));
    dateBoolQuery.should(QueryBuilders
        .rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE).gte(roundedYear)
        .lte(roundedYear));

    BoolQueryBuilder thesisDateBoolQuery = QueryBuilders.boolQuery();
    dateBoolQuery.should(thesisDateBoolQuery);
    thesisDateBoolQuery.must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_METADATA_GENRE,
        Genre.THESIS.name()));
    thesisDateBoolQuery.must(QueryBuilders
        .rangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED).gte(roundedYear)
        .lte(roundedYear));


    // Organizations
    String orgId = yisb.getYearbook().getOrganization().getObjectId();
    List<String> orgWithChildren = new ArrayList<>();
    OrganizationSearchCriterion.fillWithChildOus(orgWithChildren, orgId);
    BoolQueryBuilder ouBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(ouBoolQuery);
    for (String ouId : orgWithChildren) {
      ouBoolQuery.should(QueryBuilders.termQuery(
          PubItemServiceDbImpl.INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER, ouId));
      ouBoolQuery.should(QueryBuilders.termQuery(
          PubItemServiceDbImpl.INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER, ouId));
    }


    // Contexts
    BoolQueryBuilder contextBoolQuery = QueryBuilders.boolQuery();
    candidateBoolQuery.must(contextBoolQuery);
    for (String contextId : yisb.getYearbook().getContextIds()) {
      contextBoolQuery.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID,
          contextId));
    }


    return candidateBoolQuery;


  }

  public static BoolQueryBuilder getMemberQuery(YearbookDbVO yearbookItem) throws Exception {

    BoolQueryBuilder bq = QueryBuilders.boolQuery();
    int i = 0;
    for (final String rel : yearbookItem.getItemIds()) {
      bq.should(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, rel));
    }
    return bq;
  }

  public static List<PubItemVOPresentation> retrieveAllMembers(YearbookDbVO yearbook, String authenticationToken) throws Exception {
    
    QueryBuilder qb = YearbookUtils.getMemberQuery(yearbook);
    
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, Integer.MAX_VALUE, 0, null);
    SearchRetrieveResponseVO<PubItemVO> resp = ApplicationBean.INSTANCE.getPubItemService()
        .search(srr, authenticationToken);


    List<PubItemVO> resultList = resp.getRecords().stream().map(SearchRetrieveRecordVO::getData)
        .collect(Collectors.toList());

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

  public static String getYearbookOrganizationId(AccountUserVO user) {
    String orgId = null;

    for (GrantVO grant : user.getGrants()) {
      if (grant.getRole().equals(GrantVO.PredefinedRoles.YEARBOOK_EDITOR.frameworkValue())) {
        orgId = grant.getObjectRef();

        break;
      }
    }
    return orgId;
  }
}
