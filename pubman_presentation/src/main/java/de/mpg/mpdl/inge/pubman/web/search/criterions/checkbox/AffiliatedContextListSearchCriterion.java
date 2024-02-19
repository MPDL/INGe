package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class AffiliatedContextListSearchCriterion extends MapListSearchCriterion<PubContextVOPresentation> {



  public AffiliatedContextListSearchCriterion() {
    super(AffiliatedContextListSearchCriterion.getItemStateMap(), AffiliatedContextListSearchCriterion.getItemStatePreSelectionMap());
    // TODO Auto-generated constructor stub
  }

  private static Map<String, PubContextVOPresentation> getItemStateMap() {

    final ContextListSessionBean clsb = FacesTools.findBean("ContextListSessionBean");
    final Map<String, PubContextVOPresentation> contextMap = new LinkedHashMap<>();


    for (final PubContextVOPresentation context : clsb.getDepositorContextList()) {
      contextMap.put(context.getObjectId(), context);
    }

    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
      contextMap.put(context.getObjectId(), context);
    }

    return contextMap;
  }

  private static Map<String, Boolean> getItemStatePreSelectionMap() {

    final ContextListSessionBean clsb = FacesTools.findBean("ContextListSessionBean");
    final Map<String, Boolean> preSelectionMap = new LinkedHashMap<>();

    for (final PubContextVOPresentation context : clsb.getDepositorContextList()) {
      preSelectionMap.put(context.getObjectId(), true);
    }

    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
      preSelectionMap.put(context.getObjectId(), true);
    }

    return preSelectionMap;
  }



  //  @Override
  //  public String[] getCqlIndexes(Index indexName, String value) {
  //    switch (indexName) {
  //
  //      case ESCIDOC_ALL: {
  //        return new String[] {"escidoc.context.objid"};
  //      }
  //      case ITEM_CONTAINER_ADMIN: {
  //        return new String[] {"\"/properties/context/id\""};
  //      }
  //    }
  //
  //    return null;
  //  }

  @Override
  public String getCqlValue(Index indexName, PubContextVOPresentation value) {
    return value.getObjectId();
  }

  /**
   * List is empty if only if all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {
    if (QueryType.CQL == queryType) {
      final boolean anySelected = this.getEnumMap().containsValue(true);
      return !anySelected;
    }

    if (QueryType.INTERNAL == queryType) {
      return false;
    }

    return false;
  }

  @Override
  public String[] getElasticIndexes(String value) {
    return new String[] {PubItemServiceDbImpl.INDEX_CONTEXT_OBJECT_ID};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}
