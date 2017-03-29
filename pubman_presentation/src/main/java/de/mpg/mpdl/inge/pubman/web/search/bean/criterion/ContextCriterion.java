package de.mpg.mpdl.inge.pubman.web.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;

public class ContextCriterion extends Criterion {
  public ContextCriterion() {}

  /**
   * {@inheritDoc}
   */
  @Override
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    final ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    MetadataSearchCriterion criterion = null;
    criterion = new MetadataSearchCriterion(CriterionType.CONTEXT_OBJECTID, this.getSearchString());
    criterions.add(criterion);

    return criterions;
  }
}
