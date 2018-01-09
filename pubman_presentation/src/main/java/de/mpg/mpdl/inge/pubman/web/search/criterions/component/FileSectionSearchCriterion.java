package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.pubman.web.search.SearchParseException;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.ComponentAvailableSearchCriterion.ComponentAvailability;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;

public class FileSectionSearchCriterion extends SearchCriterionBase {


  private Storage storageType = Storage.INTERNAL_MANAGED;

  private ComponentAvailability selectedAvailability = ComponentAvailability.WHATEVER;

  private ComponentContentCategoryListSearchCriterion contentCategoryListSearchCriterion =
      new ComponentContentCategoryListSearchCriterion();

  private DateSearchCriterion embargoDateSearchCriterion = new DateSearchCriterion(SearchCriterion.COMPONENT_EMBARGO_DATE);

  private ComponentVisibilityListSearchCriterion visibilityListSearchCriterion = new ComponentVisibilityListSearchCriterion();


  public FileSectionSearchCriterion(SearchCriterion type) {
    super();
    if (SearchCriterion.FILE_SECTION.equals(type)) {
      storageType = Storage.INTERNAL_MANAGED;
    } else if (SearchCriterion.LOCATOR_SECTION.equals(type)) {
      storageType = Storage.EXTERNAL_URL;
    }
  }


  @Override
  public String toCqlString(Index indexName) throws SearchParseException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public QueryBuilder toElasticSearchQuery() throws SearchParseException {

    BoolQueryBuilder bq = QueryBuilders.boolQuery();
    switch (this.selectedAvailability) {


      case YES: {
        bq.must(this.baseElasticSearchQueryBuilder(new String[] {"files.storage"}, storageType.name()));

        if (!visibilityListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(visibilityListSearchCriterion.toElasticSearchQuery());
        }
        if (!embargoDateSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(embargoDateSearchCriterion.toElasticSearchQuery());
        }
        if (!contentCategoryListSearchCriterion.isEmpty(QueryType.CQL)) {
          bq.must(contentCategoryListSearchCriterion.toElasticSearchQuery());
        }
        break;
      }

      case NO: {
        bq.mustNot(SearchCriterionBase.baseElasticSearchQueryBuilder("files.storage", storageType.name()));
        break;
      }

      case WHATEVER:
        return null;
    }
    return QueryBuilders.nestedQuery("files", (QueryBuilder) bq, ScoreMode.Avg);
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

    return sb.toString();

  }

  @Override
  public void parseQueryStringContent(String content) throws SearchParseException {
    String[] parts = content.split("\\|\\|", -1);
    this.selectedAvailability = ComponentAvailability.valueOf(parts[0]);
    this.visibilityListSearchCriterion.parseQueryStringContent(parts[1]);
    this.embargoDateSearchCriterion.parseQueryStringContent(parts[2]);
    this.contentCategoryListSearchCriterion.parseQueryStringContent(parts[3]);

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
