/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.valueobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.types.Coordinates;

/**
 * A MPG unit or lower level of organizational unit within an MPG unit; includes also external affiliations. (Dependent
 * on internal organizational structure: Institute, Department, project groups, working groups, temporary working
 * groups, etc.)
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 07-Sep-2007 13:27:29
 */
public class AffiliationVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    
    /**
     * Additional name or name used in the community.
     */
//    private String abbreviation;
//    private String address;
    private java.util.List<AffiliationRO> childAffiliations = new java.util.ArrayList<AffiliationRO>();
    private String city;
    
    private Coordinates coordinates;
    
    /**
     * These codes are the upper-case, two-letter codes as defined by ISO-3166. You can find a full list of these codes
     * at a number of sites, such as: http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-
     * en1.html
     */
    private String countryCode;
    private List<String> descriptions = new ArrayList<String>();
//    private String email;
    /**
     * Identifier of an external resource.
     */
    private List<String> identifiers = new ArrayList<String>();
//    private String fax;
//    private java.net.URL homepageUrl;
    /**
     * The unique name of the affiliation in the organizational structure.
     */
    private String name;
    private List<String> alternativeNames = new ArrayList<String>();
    private java.util.List<AffiliationRO> parentAffiliations = new java.util.ArrayList<AffiliationRO>();
//    private String pid;
//    private String postcode;
    private AffiliationRO reference;
//    private String region;
//    private String telephone;
    private java.util.Date creationDate;
    private java.util.Date lastModificationDate;
    private AccountUserRO creator;
    private boolean hasChildren;
    private String publicStatus;
    
    private Date startDate;
    private Date endDate;
    
    /**
     * Default constructor.
     */
    public AffiliationVO()
    {
        
    }
    
    /**
     * Clone constructor.
     */
    public AffiliationVO(AffiliationVO affiliation)
    {

        this.childAffiliations = affiliation.childAffiliations;
        this.city = affiliation.city;
        this.countryCode = affiliation.countryCode;
        this.descriptions = affiliation.descriptions;
        this.name = affiliation.name;
        this.parentAffiliations = affiliation.parentAffiliations;
        this.reference = affiliation.reference;
        this.creationDate = affiliation.creationDate;
        this.lastModificationDate = affiliation.lastModificationDate;
        this.creator = affiliation.creator;
        this.hasChildren = affiliation.hasChildren;
        this.publicStatus = affiliation.publicStatus;
        this.alternativeNames = affiliation.alternativeNames;
        this.identifiers = affiliation.identifiers;
        this.coordinates = affiliation.coordinates;
        this.endDate = affiliation.endDate;
        this.startDate = affiliation.startDate;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new AffiliationVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     */
    boolean alreadyExistsInFramework()
    {
        return (this.reference != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "parent-ous" XML structure has
     * to be created during marshalling.
     */
    boolean hasParentAffiliations()
    {
        return (this.parentAffiliations.size() >= 1);
    }

    /**
     * Delivers the list of the affiliations' child affiliations.
     */
    public java.util.List<AffiliationRO> getChildAffiliations()
    {
        return childAffiliations;
    }

    /**
     * Delivers the city of the affiliation.
     */
    public String getCity()
    {
        return city;
    }

    /**
     * Delivers the country code of the affiliation. These codes are the upper-case, two-letter codes as defined by
     * ISO-3166. You can find a full list of these codes at a number of sites, such as:
     * http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
     */
    public String getCountryCode()
    {
        return countryCode;
    }

    /**
     * Delivers the creation date of the affiliation, i. e. a timestamp from the system when the organizational unit is
     * created.
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Delivers the creator of the affiliation, i. e. the account user that created the affiliation in the system.
     */
    public AccountUserRO getCreator()
    {
        return creator;
    }

    /**
     * Delivers the date if the last modification of the affiliation in the system.
     */
    public java.util.Date getLastModificationDate()
    {
        return lastModificationDate;
    }

    /**
     * The unique name of the affiliation in the organizational structure.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Delivers the list of the affiliations' parent affiliations.
     */
    public java.util.List<AffiliationRO> getParentAffiliations()
    {
        return parentAffiliations;
    }

    /**
     * Delivers the affiliations' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     */
    public AffiliationRO getReference()
    {
        return reference;
    }

    /**
     * Sets the city of the affiliation.
     * 
     * @param newVal newVal
     */
    public void setCity(String newVal)
    {
        this.city = newVal;
    }

    /**
     * Sets the country code of the affiliation. These codes are the upper-case, two-letter codes as defined by
     * ISO-3166. You can find a full list of these codes at a number of sites, such as:
     * http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
     * 
     * @param newVal newVal
     */
    public void setCountryCode(String newVal)
    {
        this.countryCode = newVal;
    }

    /**
     * Sets the creation date of the affiliation, i. e. a timestamp from the system when the organizational unit is
     * created.
     * 
     * @param newVal
     */
    public void setCreationDate(java.util.Date newVal)
    {
        this.creationDate = newVal;
    }

    /**
     * Sets the creator of the affiliation, i. e. the account user that created the affiliation in the system.
     * 
     * @param newVal
     */
    public void setCreator(AccountUserRO newVal)
    {
        this.creator = newVal;
    }

    /**
     * Sets the date if the last modification of the affiliation in the system.
     * 
     * @param newVal
     */
    public void setLastModificationDate(java.util.Date newVal)
    {
        this.lastModificationDate = newVal;
    }

    /**
     * The unique name of the affiliation in the organizational structure.
     * 
     * @param newVal newVal
     */
    public void setName(String newVal)
    {
        this.name = newVal;
    }

    /**
     * Sets the affiliations' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     * @param newVal newVal
     */
    public void setReference(AffiliationRO newVal)
    {
        this.reference = newVal;
    }

    /**
     * Delivers the publicly visible status of the affiliation. The public status can only be changed by the system.
     */
    public String getPublicStatus()
    {
        return publicStatus;
    }

    /**
     * Sets the publicly visible status of the affiliation. The public status can only be changed by the system.
     * 
     * @param newVal
     */
    public void setPublicStatus(String newVal)
    {
        publicStatus = newVal;
    }

    /**
     * Sets the flag indicating whether the affiliation has child affiliations or not.
     * 
     * @param newVal
     */
    public void setHasChildren(boolean newVal)
    {
        hasChildren = newVal;
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "parent-ous" XML structure has
     * to be created during marshalling.
     */
    boolean hasParents()
    {
        return (this.parentAffiliations.size() >= 1);
    }

    /**
     * Delivers true if the affiliation has child affiliations. The idiosyncratic method name is chosen to support JSF
     * backing beans.
     * 
     * @return true if the affiliation has child affiliations.
     */
    public boolean getHasChildren()
    {
        return hasChildren;
    }

    public List<String> getDescriptions()
    {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions)
    {
        this.descriptions = descriptions;
    }

    public List<String> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers)
    {
        this.identifiers = identifiers;
    }

    public List<String> getAlternativeNames()
    {
        return alternativeNames;
    }

    public void setAlternativeNames(List<String> alternativeNames)
    {
        this.alternativeNames = alternativeNames;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    public void setCoordinates(String coordinates) throws Exception
    {
        this.coordinates = new Coordinates(coordinates);
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
    
}