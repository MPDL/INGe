package de.mpg.mpdl.inge.pubman.web.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.mpdl.inge.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;

public class ContextCriterion extends Criterion {
  // boolean flag for the full text search


  public ContextCriterion() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    MetadataSearchCriterion criterion = null;
    criterion = new MetadataSearchCriterion(CriterionType.CONTEXT_OBJECTID, getSearchString());
    criterions.add(criterion);
    return criterions;

  }

}
