/*
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
import de.mpg.escidoc.services.common.valueobjects.interfaces.Searchable;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;

/**
 * A MPG unit or lower level of organizational unit within an MPG unit; includes also external affiliations. (Dependent
 * on internal organizational structure: Institute, Department, project groups, working groups, temporary working
 * groups, etc.)
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 07-Sep-2007 13:27:29
 */
public class AffiliationVO extends ValueObject implements Searchable
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

    private java.util.List<AffiliationRO> childAffiliations = new java.util.ArrayList<AffiliationRO>();
    
    private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();

    private java.util.List<AffiliationRO> parentAffiliations = new java.util.ArrayList<AffiliationRO>();
    
    private java.util.List<AffiliationRO> predecessorAffiliations = new java.util.ArrayList<AffiliationRO>();

    private AffiliationRO reference;

    private java.util.Date creationDate;
    private java.util.Date lastModificationDate;
    private AccountUserRO creator;
    private AccountUserRO modifiedBy;
    private boolean hasChildren;
    private String publicStatus;
    
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

        this.parentAffiliations = affiliation.parentAffiliations;
        this.reference = affiliation.reference;
        this.creationDate = affiliation.creationDate;
        this.lastModificationDate = affiliation.lastModificationDate;
        this.creator = affiliation.creator;
        this.modifiedBy = affiliation.modifiedBy;
        this.hasChildren = affiliation.hasChildren;
        this.publicStatus = affiliation.publicStatus;
        this.metadataSets = affiliation.metadataSets;
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
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML structure has
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
     * Convenience method to retrieve escidoc metadat set.
     * 
     * 
     * @return A {@link MdsOrganizationalUnitDetailsVO}.
     */
    public MdsOrganizationalUnitDetailsVO getDefaultMetadata()
    {
        if (metadataSets.size() > 0 && metadataSets.get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return (MdsOrganizationalUnitDetailsVO) metadataSets.get(0);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Convenience method to set escidoc metadata set.
     * 
     * @param detailsVO A {@link MdsOrganizationalUnitDetailsVO} containing the default metadata.
     */
    public void setDefaultMetadata(MdsOrganizationalUnitDetailsVO detailsVO)
    {
        if (metadataSets.size() == 0)
        {
            metadataSets.add(detailsVO);
        }
        else
        {
            metadataSets.set(0, detailsVO);
        }
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
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML structure has
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

    public List<MetadataSetVO> getMetadataSets()
    {
        return metadataSets;
    }

    public AccountUserRO getModifiedBy()
    {
        return modifiedBy;
    }

    public void setModifiedBy(AccountUserRO modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the predecessorAffiliations
     */
    public java.util.List<AffiliationRO> getPredecessorAffiliations()
    {
        return predecessorAffiliations;
    }

    /**
     * @param predecessorAffiliations the predecessorAffiliations to set
     */
    public void setPredecessorAffiliations(java.util.List<AffiliationRO> predecessorAffiliations)
    {
        this.predecessorAffiliations = predecessorAffiliations;
    }

    
    
}