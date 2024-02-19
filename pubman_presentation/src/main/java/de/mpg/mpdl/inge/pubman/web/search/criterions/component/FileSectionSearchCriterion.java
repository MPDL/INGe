package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class FileSectionSearchCriterion extends SearchCriterionBase {


  private FileDbVO.Storage storageType = FileDbVO.Storage.INTERNAL_MANAGED;

  private ComponentAvailableSearchCriterion.ComponentAvailability selectedAvailability =
      ComponentAvailableSearchCriterion.ComponentAvailability.WHATEVER;

  private ComponentContentCategoryListSearchCriterion contentCategoryListSearchCriterion =
      new ComponentContentCategoryListSearchCriterion();

  private ComponentOaStatusListSearchCriterion oaStatusListSearchCriterion = new ComponentOaStatusListSearchCriterion();

  private DateSearchCriterion embargoDateSearchCriterion = new DateSearchCriterion(SearchCriterion.COMPONENT_EMBARGO_DATE);

  private ComponentVisibilityListSearchCriterion visibilityListSearchCriterion = new ComponentVisibilityListSearchCriterion();


  public FileSectionSearchCriterion(SearchCriterion type) {
    super(type);
    if (SearchCriterion.FILE_SECTION.equals(type)) {
      this.storageType = FileDbVO.Storage.INTERNAL_MANAGED;
      this.oaStatusListSearchCriterion = new ComponentOaStatusListSearchCriterion(true);
    } else if (SearchCriterion.LOCATOR_SECTION.equals(type)) {
      this.storageType = FileDbVO.Storage.EXTERNAL_URL;
      this.oaStatusListSearchCriterion = new ComponentOaStatusListSearchCriterion(false);
    }
  }


  //  @Override
  //  public String toCqlString(Index indexName) throws SearchParseException {
  //    // TODO Auto-generated method stub
  //    return null;
  //  }

  @Override
  public Query toElasticSearchQuery() throws IngeTechnicalException {

    BoolQuery.Builder bq = new BoolQuery.Builder();
    switch (this.selectedAvailability) {


      case YES: {
        bq.must(SearchCriterionBase.baseElasticSearchQueryBuilder(new String[] {PubItemServiceDbImpl.INDEX_FILE_STORAGE},
            this.storageType.name()));

        if (!this.visibilityListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(this.visibilityListSearchCriterion.toElasticSearchQuery());
        }
        if (!this.embargoDateSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(this.embargoDateSearchCriterion.toElasticSearchQuery());
        }
        if (!this.contentCategoryListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(this.contentCategoryListSearchCriterion.toElasticSearchQuery());
        }
        if (!this.oaStatusListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(this.oaStatusListSearchCriterion.toElasticSearchQuery());
        }
        break;
      }

      case NO: {
        bq.mustNot(SearchCriterionBase.baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_FILE_STORAGE, this.storageType.name()));
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
    if (!this.visibilityListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(this.visibilityListSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!this.embargoDateSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(this.embargoDateSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!this.contentCategoryListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(this.contentCategoryListSearchCriterion.getQueryStringContent());
    }
    sb.append("||");
    if (!this.oaStatusListSearchCriterion.isEmpty(QueryType.INTERNAL)) {
      sb.append(this.oaStatusListSearchCriterion.getQueryStringContent());
    }

    return sb.toString();

  }

  @Override
  public void parseQueryStringContent(String content) {
    String[] parts = content.split("\\|\\|", -1);
    this.selectedAvailability = ComponentAvailableSearchCriterion.ComponentAvailability.valueOf(parts[0]);
    this.visibilityListSearchCriterion.parseQueryStringContent(parts[1]);
    this.embargoDateSearchCriterion.parseQueryStringContent(parts[2]);
    this.contentCategoryListSearchCriterion.parseQueryStringContent(parts[3]);
    this.oaStatusListSearchCriterion.parseQueryStringContent(parts[4]);

  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return ComponentAvailableSearchCriterion.ComponentAvailability.WHATEVER.equals(this.selectedAvailability);
  }

  public ComponentAvailableSearchCriterion.ComponentAvailability getSelectedAvailability() {
    return this.selectedAvailability;
  }

  public void setSelectedAvailability(ComponentAvailableSearchCriterion.ComponentAvailability selectedAvailability) {
    this.selectedAvailability = selectedAvailability;
  }

  public ComponentContentCategoryListSearchCriterion getContentCategoryListSearchCriterion() {
    return this.contentCategoryListSearchCriterion;
  }

  public void setContentCategoryListSearchCriterion(ComponentContentCategoryListSearchCriterion contentCategoryListSearchCriterion) {
    this.contentCategoryListSearchCriterion = contentCategoryListSearchCriterion;
  }

  public ComponentOaStatusListSearchCriterion getOaStatusListSearchCriterion() {
    return this.oaStatusListSearchCriterion;
  }

  public void setOaStatusListSearchCriterion(ComponentOaStatusListSearchCriterion oaStatusListSearchCriterion) {
    this.oaStatusListSearchCriterion = oaStatusListSearchCriterion;
  }

  public DateSearchCriterion getEmbargoDateSearchCriterion() {
    return this.embargoDateSearchCriterion;
  }

  public void setEmbargoDateSearchCriterion(DateSearchCriterion embargoDateSearchCriterion) {
    this.embargoDateSearchCriterion = embargoDateSearchCriterion;
  }

  public ComponentVisibilityListSearchCriterion getVisibilityListSearchCriterion() {
    return this.visibilityListSearchCriterion;
  }

  public void setVisibilityListSearchCriterion(ComponentVisibilityListSearchCriterion visibilityListSearchCriterion) {
    this.visibilityListSearchCriterion = visibilityListSearchCriterion;
  }

}
