package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.search.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.FlexibleStandardSearchCriterion;
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

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}
