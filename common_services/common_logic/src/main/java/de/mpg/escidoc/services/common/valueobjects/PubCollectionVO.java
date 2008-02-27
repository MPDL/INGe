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

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.PubCollectionRO;

/**
 * Special type of container of data with specific workflow (i.e. the publication management workflow). A set of
 * publication objects which have some common denominator. Collection may contain one or more subcollections.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 11:14:08
 */
public class PubCollectionVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */


    /**
     * The possible submission methods of a collection.
     * 
     * @updated 05-Sep-2007 11:14:08
     */
    public enum SubmissionMethod
    {
        INGESTION, SINGLE_SUBMISSION, MULTIPLE_SUBMISSION
    }

    /**
     * The possible states of a collection.
     * 
     * @updated 05-Sep-2007 11:14:08
     */
    public enum State
    {
        CREATED, CLOSED, OPENED, DELETED
    }

    /**
     * The refence object identifying this pubCollection.
     */
    private PubCollectionRO reference;
    /**
     * A unique name of the collection within the system.
     */
    private String name;
    /**
     * The state of the PubCollection.
     */
    private State state;
    /**
     * A short description of the collection and the collection policy.
     */
    private String description;
    /**
     * The default visibility for files of items of the collection.
     */
    private PubFileVO.Visibility defaultFileVisibility;
    /**
     * The possible genre values for items of the collection.
     */
    private java.util.List<MdsPublicationVO.Genre> allowedGenres = new java.util.ArrayList<MdsPublicationVO.Genre>();
    /**
     * The default metadata.
     */
    private MdsPublicationVO defaultMetadata;
    /**
     * The possible submission method values for items of the collection.
     */
    private java.util.List<SubmissionMethod> allowedSubmissionMethods = new java.util.ArrayList<SubmissionMethod>();
    /**
     * The creator of the collection.
     */
    private AccountUserRO creator;
    /**
     * The set union of validation points for items in this collection.
     */
    private java.util.List<ValidationPointVO> validationPoints = new java.util.ArrayList<ValidationPointVO>();
    /**
     * The list of responsible affiliations for this collection.
     */
    private java.util.List<AffiliationRO> responsibleAffiliations = new java.util.ArrayList<AffiliationRO>();

    /**
     * Default constructor.
     */
    public PubCollectionVO()
    {
    	
    }
    
    /**
     * Clone constructor.
     *
     * @param collection The collection to be cloned.
     */
    public PubCollectionVO(PubCollectionVO collection)
    {
    	this.allowedGenres = collection.allowedGenres;
    	this.allowedSubmissionMethods = collection.allowedSubmissionMethods;
    	this.creator = collection.creator;
    	this.defaultFileVisibility = collection.defaultFileVisibility;
    	this.defaultMetadata = collection.defaultMetadata;
    	this.description = collection.description;
    	this.name = collection.name;
    	this.reference = collection.reference;
    	this.responsibleAffiliations = collection.responsibleAffiliations;
    	this.state = collection.state;
    	this.validationPoints = collection.validationPoints;
    }
    
    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     */
    public boolean alreadyExistsInFramework()
    {
        return (this.reference != null);
    }

    /**
     * Delivers the default file visibility for files of items of the collection.
     * 
     * @see PubFileVO.Visibility
     */
    public PubFileVO.Visibility getDefaultFileVisibility()
    {
        return defaultFileVisibility;
    }

    /**
     * Sets the default file visibility for files of items of the collection.
     * 
     * @see PubFileVO.Visibility
     * @param newVal
     */
    public void setDefaultFileVisibility(PubFileVO.Visibility newVal)
    {
        defaultFileVisibility = newVal;
    }

    /**
     * Delivers the description of the collection, i. e. a short description of the collection and the collection
     * policy.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Delivers the state of the collection.
     */
    public PubCollectionVO.State getState()
    {
        return state;
    }

    /**
     * Sets the description of the collection, i. e. a short description of the collection and the collection policy.
     * 
     * @param newVal
     */
    public void setDescription(String newVal)
    {
        description = newVal;
    }

    /**
     * Sets the state of the collection.
     * 
     * @param newVal
     */
    public void setState(PubCollectionVO.State newVal)
    {
        state = newVal;
    }

    /**
     * Delivers the name of the collection, i. e. a unique name of the collection within the system.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the collection, i. e. a unique name of the collection within the system.
     * 
     * @param newVal
     */
    public void setName(String newVal)
    {
        name = newVal;
    }

    /**
     * Delivers the refence object identifying this pubCollection.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     */
    public PubCollectionRO getReference()
    {
        return reference;
    }

    /**
     * Sets the refence object identifying this pubCollection.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     * @param newVal
     */
    public void setReference(PubCollectionRO newVal)
    {
        reference = newVal;
    }

    /**
     * Delivers the default metadata for items of the collection.
     */
    public MdsPublicationVO getDefaultMetadata()
    {
        return defaultMetadata;
    }

    /**
     * Delivers the list of allowed submission methods for the collection, e. g. ingestion, single submission, multiple
     * submission.
     */
    public java.util.List<SubmissionMethod> getAllowedSubmissionMethods()
    {
        return allowedSubmissionMethods;
    }

    /**
     * Sets the default metadata for items of the collection.
     * 
     * @param newVal
     */
    public void setDefaultMetadata(MdsPublicationVO newVal)
    {
        defaultMetadata = newVal;
    }

    /**
     * Delivers the validation points of this collection.
     */
    public java.util.List<ValidationPointVO> getValidationPoints()
    {
        return validationPoints;
    }

    /**
     * Delivers the reference of the creator of the collection.
     */
    public AccountUserRO getCreator()
    {
        return creator;
    }

    /**
     * Delivers the list of allowed genres in the collection, i. e. the allowed genres for items of the collection.
     */
    public java.util.List<MdsPublicationVO.Genre> getAllowedGenres()
    {
        return allowedGenres;
    }

    /**
     * Sets the reference of the creator of the collection.
     * 
     * @param newVal
     */
    public void setCreator(AccountUserRO newVal)
    {
        creator = newVal;
    }

    /**
     * Delivers the list of affiliations which are responsible for this collection.
     */
    public java.util.List<AffiliationRO> getResponsibleAffiliations()
    {
        return responsibleAffiliations;
    }
}