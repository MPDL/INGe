package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.ElasticSearchIndexField;
import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;

@SuppressWarnings("serial")
public class AffiliatedContextListSearchCriterion extends
    MapListSearchCriterion<PubContextVOPresentation> {



  public AffiliatedContextListSearchCriterion() {
    super(AffiliatedContextListSearchCriterion.getItemStateMap(),
        AffiliatedContextListSearchCriterion.getItemStatePreSelectionMap());
    // TODO Auto-generated constructor stub
  }

  private static Map<String, PubContextVOPresentation> getItemStateMap() {

    final ContextListSessionBean clsb =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    final Map<String, PubContextVOPresentation> contextMap =
        new LinkedHashMap<String, PubContextVOPresentation>();


    for (final PubContextVOPresentation context : clsb.getDepositorContextList()) {
      contextMap.put(context.getReference().getObjectId(), context);
    }

    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
      contextMap.put(context.getReference().getObjectId(), context);
    }

    return contextMap;
  }

  private static Map<String, Boolean> getItemStatePreSelectionMap() {

    final ContextListSessionBean clsb =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    final Map<String, Boolean> preSelectionMap = new LinkedHashMap<String, Boolean>();

    for (final PubContextVOPresentation context : clsb.getDepositorContextList()) {
      preSelectionMap.put(context.getReference().getObjectId(), true);
    }

    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
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
      final boolean anySelected = this.getEnumMap().containsValue(true);
      return !anySelected;
    }

    if (queryType == QueryType.INTERNAL) {
      return false;
    }

    return false;
  }

  @Override
  public ElasticSearchIndexField[] getElasticIndexes() {
    return new ElasticSearchIndexField[] {new ElasticSearchIndexField("context.objectId")};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}