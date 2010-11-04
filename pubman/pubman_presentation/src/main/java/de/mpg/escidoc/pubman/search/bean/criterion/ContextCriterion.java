package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;

public class ContextCriterion extends Criterion{
    // boolean flag for the full text search

	private String collection;

	public String getCollection() {
		return collection;
	}


	public void setCollection(String collection) {
		this.collection = collection;
	}


	public ContextCriterion()
	{
		super();
	}


    /**
     * {@inheritDoc}
     */
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
	    ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
	    MetadataSearchCriterion criterion = null;
	    criterion = new MetadataSearchCriterion( CriterionType.CONTEXT_OBJECTID, getSearchString() );
	    criterions.add( criterion );
	    return criterions;

    }

}
