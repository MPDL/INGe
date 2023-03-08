package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.GrantUtil;

@SuppressWarnings("serial")
public class ItemStateListSearchCriterion extends MapListSearchCriterion<String> {

  public ItemStateListSearchCriterion() {
    super(ItemStateListSearchCriterion.getItemStateMap(), ItemStateListSearchCriterion.getItemStatePreSelectionMap());
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

  //  @Override
  //  public String[] getCqlIndexes(Index indexName, String value) {
  //    switch (indexName) {
  //      case ITEM_CONTAINER_ADMIN: {
  //        if ("withdrawn".equals(value)) {
  //          return new String[] {"\"/properties/public-status\""};
  //        } else {
  //          return new String[] {"\"/properties/version/status\""};
  //        }
  //      }
  //
  //      default:
  //        return null;
  //    }
  //  }

  @Override
  public String getCqlValue(Index indexName, String value) {
    return value;
  }

  //  @Override
  //  public List<SearchCriterionBase> getSearchCriterionsForValue(Index indexName, String searchValue) {
  //    final List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();
  //
  //    if (!"withdrawn".equals(searchValue)) {
  //      scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
  //    }
  //
  //    final SearchCriterionBase flexSc = new FlexibleStandardSearchCriterion(this.getCqlIndexes(indexName, searchValue), searchValue);
  //    scList.add(flexSc);
  //
  //
  //    // exclude public status withdrawn
  //    if (!"withdrawn".equals(searchValue)) {
  //      scList.add(new LogicalOperator(SearchCriterion.NOT_OPERATOR));
  //      scList.add(new FlexibleStandardSearchCriterion(new String[] {"\"/properties/public-status\""}, "withdrawn"));
  //      scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
  //    }
  //
  //    return scList;
  //  }

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
    }

    return new String[] {PubItemServiceDbImpl.INDEX_VERSION_STATE};
  }


  /**
   * Creates a query which filters out duplicate items when an item's latest release is not the same
   * as its latest version
   * 
   * @param user
   * @return
   * @throws IngeTechnicalException
   */
  public static Query filterOut(AccountUserDbVO user, State s) throws IngeTechnicalException {
    BoolQuery.Builder filterOutQuery = new BoolQuery.Builder();

    filterOutQuery.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE, State.RELEASED.name()));

    // filterOutQuery.must(QueryBuilders.scriptQuery(new Script("doc['" +
    // PubItemServiceDbImpl.INDEX_LATESTVERSION_VERSIONNUMBER + "']!=doc['" +
    // PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER + "']")));

    filterOutQuery.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_LATESTVERSION_STATE, s.name()));

    // Filter out released items where user is owner
    BoolQuery.Builder subQuery = new BoolQuery.Builder();

    subQuery.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_OWNER_OBJECT_ID, user.getObjectId()));

    // Filter out released items where user is moderator
    if (GrantUtil.hasRole(user, PredefinedRoles.MODERATOR) && (State.SUBMITTED.equals(s) || State.IN_REVISION.equals(s))) {
      BoolQuery.Builder contextModeratorQuery = new BoolQuery.Builder();

      for (GrantVO grant : user.getGrantList()) {
        if (GrantVO.PredefinedRoles.MODERATOR.frameworkValue().contentEquals(grant.getRole())) {
          contextModeratorQuery.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID, grant.getObjectRef()));
        }
      }
      subQuery.should(contextModeratorQuery.build()._toQuery());
    }

    filterOutQuery.must(subQuery.build()._toQuery());
    return filterOutQuery.build()._toQuery();
  }

  public Query toElasticSearchQuery() throws IngeTechnicalException {
    if (!this.isEmpty(QueryType.CQL)) {
      LoginHelper loginHelper = FacesTools.findBean("LoginHelper");
      BoolQuery.Builder bq = new BoolQuery.Builder();
      for (final Entry<String, Boolean> entry : this.getEnumMap().entrySet()) {
        if (entry.getValue()) {
          switch (ItemVersionRO.State.valueOf(entry.getKey())) {
            case RELEASED: {
              BoolQuery.Builder subBuilder = new BoolQuery.Builder();
              subBuilder.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE, entry.getKey()));
              subBuilder.mustNot(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, State.WITHDRAWN.name()));
              bq.should(subBuilder.build()._toQuery());
              break;
            }
            case SUBMITTED:
            case PENDING:
            case IN_REVISION: {
              BoolQuery.Builder subBuilder = new BoolQuery.Builder();
              subBuilder.must(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_VERSION_STATE, entry.getKey()));
              subBuilder.mustNot(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, State.WITHDRAWN.name()));
              bq.should(subBuilder.build()._toQuery());
              bq.mustNot(filterOut(loginHelper.getAccountUser(), State.valueOf(entry.getKey())));

              break;
            }
            case WITHDRAWN: {
              bq.should(baseElasticSearchQueryBuilder(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, entry.getKey()));
              break;
            }
          }
        }
      }

      return bq.build()._toQuery();
    }

    return null;
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}
