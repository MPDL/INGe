package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.checkbox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.standard.FlexibleStandardSearchCriterion;

@SuppressWarnings("serial")
public class ItemStateListSearchCriterion extends MapListSearchCriterion<String> {

  public ItemStateListSearchCriterion() {

    super(getItemStateMap(), getItemStatePreSelectionMap());
  }

  private static Map<String, String> getItemStateMap() {
    Map<String, String> itemStateMap = new LinkedHashMap<String, String>();

    itemStateMap.put("PENDING", "pending");
    itemStateMap.put("SUBMITTED", "submitted");
    itemStateMap.put("IN_REVISION", "in-revision");
    itemStateMap.put("RELEASED", "released");
    itemStateMap.put("WITHDRAWN", "withdrawn");


    return itemStateMap;
  }

  private static Map<String, Boolean> getItemStatePreSelectionMap() {
    Map<String, Boolean> itemStateMap = new LinkedHashMap<String, Boolean>();

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
    List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();

    if (!"withdrawn".equals(searchValue)) {
      scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    }

    SearchCriterionBase flexSc =
        new FlexibleStandardSearchCriterion(getCqlIndexes(indexName, searchValue), searchValue);
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
  public ElasticSearchIndexField[] getElasticIndexes() {
    return new ElasticSearchIndexField[] {new ElasticSearchIndexField("publicStatus")};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }



}
