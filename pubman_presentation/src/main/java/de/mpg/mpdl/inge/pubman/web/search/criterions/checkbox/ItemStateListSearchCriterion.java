package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.Index;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.QueryType;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.FlexibleStandardSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class ItemStateListSearchCriterion extends MapListSearchCriterion<String> {

  public ItemStateListSearchCriterion() {

    super(ItemStateListSearchCriterion.getItemStateMap(), ItemStateListSearchCriterion
        .getItemStatePreSelectionMap());
  }

  private static Map<String, String> getItemStateMap() {
    final Map<String, String> itemStateMap = new LinkedHashMap<String, String>();

    itemStateMap.put("PENDING", "PENDING");
    itemStateMap.put("SUBMITTED", "SUBMITTED");
    itemStateMap.put("IN_REVISION", "IN_REVISION");
    itemStateMap.put("RELEASED", "RELEASED");
    itemStateMap.put("WITHDRAWN", "WITHDRAWN");


    return itemStateMap;
  }

  private static Map<String, Boolean> getItemStatePreSelectionMap() {
    final Map<String, Boolean> itemStateMap = new LinkedHashMap<String, Boolean>();

    itemStateMap.put("PENDING", true);
    itemStateMap.put("SUBMITTED", true);
    itemStateMap.put("IN_REVISION", true);
    itemStateMap.put("RELEASED", true);
    itemStateMap.put("WITHDRAWN", false);

    return itemStateMap;
  }

  @Override
  public String[] getCqlIndexes(Index indexName, String value) {
    switch (indexName) {
      case ITEM_CONTAINER_ADMIN: {
        if ("withdrawn".equals(value)) {
          return new String[] {"\"/properties/public-status\""};
        } else {
          return new String[] {"\"/properties/version/status\""};
        }
      }
    }

    return null;
  }

  @Override
  public String getCqlValue(Index indexName, String value) {
    return value;
  }

  @Override
  public List<SearchCriterionBase> getSearchCriterionsForValue(Index indexName, String searchValue) {
    final List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();

    if (!"withdrawn".equals(searchValue)) {
      scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    }

    final SearchCriterionBase flexSc =
        new FlexibleStandardSearchCriterion(this.getCqlIndexes(indexName, searchValue), searchValue);
    scList.add(flexSc);


    // exclude public status withdrawn
    if (!"withdrawn".equals(searchValue)) {
      scList.add(new LogicalOperator(SearchCriterion.NOT_OPERATOR));
      scList.add(new FlexibleStandardSearchCriterion(
          new String[] {"\"/properties/public-status\""}, "withdrawn"));
      scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
    }

    return scList;
  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    if (queryType == QueryType.CQL) {
      return super.isEmpty(queryType);
    } else if (queryType == QueryType.INTERNAL) {
      return false;
    }

    return false;
  }

  @Override
  public String[] getElasticIndexes(String value) {
    if ("WITHDRAWN".equals(value)) {
      return new String[] {PubItemServiceDbImpl.INDEX_PUBLIC_STATE};
    } else {
      return new String[] {PubItemServiceDbImpl.INDEX_VERSION_STATE};
    }

  }


  /**
   * Creates a query which filters out duplicate items when an item's latest release is not the same
   * as its latest version
   * 
   * @param user
   * @return
   */
  public static QueryBuilder filterOut(AccountUserVO user, ItemVO.State s) {
    BoolQueryBuilder filterOutQuery = QueryBuilders.boolQuery();


    filterOutQuery.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE,
        ItemVO.State.RELEASED.name()));
    
//      filterOutQuery.must(QueryBuilders.scriptQuery(new Script("doc['" +
//      PubItemServiceDbImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']!=doc['" +
//      PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER + "']")));
     

    filterOutQuery.must(baseElasticSearchQueryBuilder(
        PubItemServiceDbImpl.INDEX_LATESTVERSION_STATE, s.name()));

    // Filter out released items where user is owner
    BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
    filterOutQuery.must(subQuery);
    subQuery.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_OWNER_OBJECT_ID,
        user.getReference().getObjectId()));

    // Filter out released items where user is moderator
    if (user.isModerator()
        && (ItemVO.State.SUBMITTED.equals(s) || ItemVO.State.IN_REVISION.equals(s))) {
      BoolQueryBuilder contextModeratorQuery = QueryBuilders.boolQuery();
      subQuery.should(contextModeratorQuery);
      for (GrantVO grant : user.getGrants()) {
        if (grant.getRole().equals(GrantVO.PredefinedRoles.MODERATOR.frameworkValue())) {
          contextModeratorQuery.should(baseElasticSearchQueryBuilder(
              PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID, grant.getObjectRef()));
        }
      }

    }
    return filterOutQuery;
  }


  public QueryBuilder toElasticSearchQuery() {


    if (!this.isEmpty(QueryType.CQL)) {

      LoginHelper loginHelper = FacesTools.findBean("LoginHelper");

      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      for (final Entry<String, Boolean> entry : this.getEnumMap().entrySet()) {


        if (entry.getValue()) {

          switch (ItemVO.State.valueOf(entry.getKey())) {
            case RELEASED: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE,
                  entry.getKey()));
              break;
            }
            case SUBMITTED: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE,
                  entry.getKey()));
              bq.mustNot(filterOut(loginHelper.getAccountUser(), ItemVO.State.SUBMITTED));

              break;
            }
            case PENDING: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE,
                  entry.getKey()));
              bq.mustNot(filterOut(loginHelper.getAccountUser(), ItemVO.State.PENDING));

              break;
            }
            case IN_REVISION: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE,
                  entry.getKey()));
              bq.mustNot(filterOut(loginHelper.getAccountUser(), ItemVO.State.IN_REVISION));
              break;
            }
            case WITHDRAWN: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_PUBLIC_STATE,
                  entry.getKey()));
              break;
            }
          }

        }

      }


      return bq;

    }


    return null;
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}
