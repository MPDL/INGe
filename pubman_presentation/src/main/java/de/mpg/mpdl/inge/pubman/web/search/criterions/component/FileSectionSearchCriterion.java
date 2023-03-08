package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentAvailableSearchCriterion.ComponentAvailability;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class FileSectionSearchCriterion extends SearchCriterionBase {


  private Storage storageType = Storage.INTERNAL_MANAGED;

  private ComponentAvailability selectedAvailability = ComponentAvailability.WHATEVER;

  private ComponentContentCategoryListSearchCriterion contentCategoryListSearchCriterion =
      new ComponentContentCategoryListSearchCriterion();

  private ComponentOaStatusListSearchCriterion oaStatusListSearchCriterion = new ComponentOaStatusListSearchCriterion();

  private DateSearchCriterion embargoDateSearchCriterion = new DateSearchCriterion(SearchCriterion.COMPONENT_EMBARGO_DATE);

  private ComponentVisibilityListSearchCriterion visibilityListSearchCriterion = new ComponentVisibilityListSearchCriterion();


  public FileSectionSearchCriterion(SearchCriterion type) {
    super(type);
    if (SearchCriterion.FILE_SECTION.equals(type)) {
      storageType = Storage.INTERNAL_MANAGED;
    } else if (SearchCriterion.LOCATOR_SECTION.equals(type)) {
      storageType = Storage.EXTERNAL_URL;
    }
  }


  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //    // TODO Auto-generated method stub
  //    return null;
  //  }

  @Override
  public Query toElasticSearchQuery() throws SearchParseException, IngeTechnicalException {

    BoolQuery.Builder bq = new BoolQuery.Builder();
    switch (this.selectedAvailability) {


      case YES: {
        bq.must(
            SearchCriterionBase.baseElasticSearchQueryBuilder(new String[] {PubItemServiceDbImpl.INDEX_FILE_STORAGE}, storageType.name()));

        if (!visibilityListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(visibilityListSearchCriterion.toElasticSearchQuery());
        }
        if (!embargoDateSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(embargoDateSearchCriterion.toElasticSearchQuery());
        }
        if (!contentCategoryListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(contentCategoryListSearchCriterion.toElasticSearchQuery());
        }
        if (!oaStatusListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(oaStatusListSearchCriterion.toElasticSearchQuery());
        }
        break;
      }

      case NO: {
        bq.mustNot(SearchCriterionBase.baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_FILE_STORAGE, storageType.name()));
        return bq.build()._toQuery();
        //break;
      }

      case WHATEVER:
        return null;
    }
    return NestedQuery.of(n -> n.path("files").query(bq.build()._toQuery()).scoreMode(ChildScoreMode.Avg))._toQuery();
    //return QueryBuilders.nestedQuery("files", (QueryBuilder) bq, ScoreMode.Avg);
  }

  @Override
  public String getElasticSearchNestedPath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getQueryStringContent() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getSelectedAvailability());
    sb.append("||");
    if (!visibilityListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(visibilityListSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!embargoDateSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(embargoDateSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!contentCategoryListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(contentCategoryListSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!oaStatusListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(oaStatusListSearchCriterion.getQueryStringContent());
    }

    return sb.toString();

  }

  @Override
  public void parseQueryStringContent(String content) throws SearchParseException {
    String[] parts = content.split("\\|\\|", -1);
    this.selectedAvailability = ComponentAvailability.valueOf(parts[0]);
    this.visibilityListSearchCriterion.parseQueryStringContent(parts[1]);
    this.embargoDateSearchCriterion.parseQueryStringContent(parts[2]);
    this.contentCategoryListSearchCriterion.parseQueryStringContent(parts[3]);
    this.oaStatusListSearchCriterion.parseQueryStringContent(parts[4]);

  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return ComponentAvailability.WHATEVER.equals(this.selectedAvailability);
  }

  public ComponentAvailability getSelectedAvailability() {
    return selectedAvailability;
  }

  public void setSelectedAvailability(ComponentAvailability selectedAvailability) {
    this.selectedAvailability = selectedAvailability;
  }

  public ComponentContentCategoryListSearchCriterion getContentCategoryListSearchCriterion() {
    return contentCategoryListSearchCriterion;
  }

  public void setContentCategoryListSearchCriterion(ComponentContentCategoryListSearchCriterion contentCategoryListSearchCriterion) {
    this.contentCategoryListSearchCriterion = contentCategoryListSearchCriterion;
  }

  public ComponentOaStatusListSearchCriterion getOaStatusListSearchCriterion() {
    return oaStatusListSearchCriterion;
  }

  public void setOaStatusListSearchCriterion(ComponentOaStatusListSearchCriterion oaStatusListSearchCriterion) {
    this.oaStatusListSearchCriterion = oaStatusListSearchCriterion;
  }

  public DateSearchCriterion getEmbargoDateSearchCriterion() {
    return embargoDateSearchCriterion;
  }

  public void setEmbargoDateSearchCriterion(DateSearchCriterion embargoDateSearchCriterion) {
    this.embargoDateSearchCriterion = embargoDateSearchCriterion;
  }

  public ComponentVisibilityListSearchCriterion getVisibilityListSearchCriterion() {
    return visibilityListSearchCriterion;
  }

  public void setVisibilityListSearchCriterion(ComponentVisibilityListSearchCriterion visibilityListSearchCriterion) {
    this.visibilityListSearchCriterion = visibilityListSearchCriterion;
  }

}
