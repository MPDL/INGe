package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;

@SuppressWarnings("serial")
public class AffiliatedContextListSearchCriterion extends
    MapListSearchCriterion<PubContextVOPresentation> {



  public AffiliatedContextListSearchCriterion() {
    super(getItemStateMap(), getItemStatePreSelectionMap());
    // TODO Auto-generated constructor stub
  }

  private static Map<String, PubContextVOPresentation> getItemStateMap() {

    ContextListSessionBean clsb =
        (ContextListSessionBean) FacesBean.getSessionBean(ContextListSessionBean.class);
    Map<String, PubContextVOPresentation> contextMap =
        new LinkedHashMap<String, PubContextVOPresentation>();


    for (PubContextVOPresentation context : clsb.getDepositorContextList()) {
      contextMap.put(context.getReference().getObjectId(), context);
    }

    for (PubContextVOPresentation context : clsb.getModeratorContextList()) {
      contextMap.put(context.getReference().getObjectId(), context);
    }

    return contextMap;
  }

  private static Map<String, Boolean> getItemStatePreSelectionMap() {

    ContextListSessionBean clsb =
        (ContextListSessionBean) FacesBean.getSessionBean(ContextListSessionBean.class);
    Map<String, Boolean> preSelectionMap = new LinkedHashMap<String, Boolean>();

    for (PubContextVOPresentation context : clsb.getDepositorContextList()) {
      preSelectionMap.put(context.getReference().getObjectId(), true);
    }

    for (PubContextVOPresentation context : clsb.getModeratorContextList()) {
      preSelectionMap.put(context.getReference().getObjectId(), true);
    }

    return preSelectionMap;
  }



  @Override
  public String[] getCqlIndexes(Index indexName, String value) {
    switch (indexName) {

      case ESCIDOC_ALL: {
        return new String[] {"escidoc.context.objid"};
      }
      case ITEM_CONTAINER_ADMIN: {
        return new String[] {"\"/properties/context/id\""};
      }
    }

    return null;
  }



  @Override
  public String getCqlValue(Index indexName, PubContextVOPresentation value) {

    return value.getReference().getObjectId();
  }

  /**
   * List is empty if only if all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {

    if (queryType == QueryType.CQL) {
      boolean anySelected = getEnumMap().containsValue(true);
      return !anySelected;
    } else if (queryType == QueryType.INTERNAL) {
      return false;
    }

    return false;

  }

}
