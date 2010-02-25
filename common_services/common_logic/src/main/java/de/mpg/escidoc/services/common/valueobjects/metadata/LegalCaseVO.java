/**
 * 
 */
package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * @author gerga
 * 
 * JUS-specific VO
 *
 */
public class LegalCaseVO extends ValueObject implements Cloneable{

	/**
	 * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
	 * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
	 * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
	 * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
	 * for the Application Server, once for the local test).
	 * 
	 * @author Johannes Mueller
	 */

	private static final long serialVersionUID = 1L;


	private String title;
	private String courtName;
	private String identifier;
	private String datePublished;

	public String getTitle() {
		return title;
	}
	public void setTitle(String newVal) {
		this.title = newVal;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String newVal) {
		this.identifier = newVal;
	}
	public String getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(String newVal) {
		this.datePublished = newVal;
	}
	public String getCourtName() {
		return courtName;
	}
	public void setCourtName(String newVal) {
		this.courtName = newVal;
	}
	
	/*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
	@Override
	public Object clone(){
		LegalCaseVO clone = new LegalCaseVO();
		if(getTitle() != null){
			
			clone.setTitle(getTitle());
		}
		if(getIdentifier() != null){
			
			clone.setIdentifier(getIdentifier());
		}
		if(getDatePublished() != null){
			
			clone.setDatePublished(getDatePublished());
		}
		if(getCourtName() != null){
			
			clone.setCourtName(getCourtName());
		}
		return clone;
		
	}
	
	/*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        LegalCaseVO other = new LegalCaseVO();
        return equals(getTitle(), other.getTitle()) && 
               equals(getIdentifier(), other.getIdentifier()) && 
               equals(getDatePublished(), other.getDatePublished()) && 
               equals(getCourtName(), other.getCourtName());
    }


}
