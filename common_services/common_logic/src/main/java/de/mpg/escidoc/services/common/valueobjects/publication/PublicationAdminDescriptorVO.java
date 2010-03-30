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

package de.mpg.escidoc.services.common.valueobjects.publication;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.types.Validatable;
import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;

/**
 * Implementation of an admin descriptor for PubMan publications.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PublicationAdminDescriptorVO extends AdminDescriptorVO implements Validatable
{
    
    public enum Workflow
    {
        STANDARD, SIMPLE
    }
    
    private List<MdsPublicationVO.Genre> allowedGenres = new ArrayList<MdsPublicationVO.Genre>();
    
    private List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications = new ArrayList<MdsPublicationVO.SubjectClassification>();
    
    private ItemRO templateItem;
    
    private String validationSchema;
    
    private String visibilityOfReferences;
    
    private Workflow workflow;
    
    private String contactEmail;

    public List<MdsPublicationVO.Genre> getAllowedGenres()
    {
        return allowedGenres;
    }

    public void setAllowedGenres(List<MdsPublicationVO.Genre> allowedGenres)
    {
        this.allowedGenres = allowedGenres;
    }

    /**
     * @return the allowedSubjectClassifications
     */
    public List<MdsPublicationVO.SubjectClassification> getAllowedSubjectClassifications()
    {
        return allowedSubjectClassifications;
    }

    /**
     * @param allowedSubjectClassifications the allowedSubjectClassifications to set
     */
    public void setAllowedSubjectClassifications(List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications)
    {
        this.allowedSubjectClassifications = allowedSubjectClassifications;
    }

    public ItemRO getTemplateItem()
    {
        return templateItem;
    }

    public void setTemplateItem(ItemRO templateItem)
    {
        this.templateItem = templateItem;
    }

    public String getValidationSchema()
    {
        return validationSchema;
    }

    public void setValidationSchema(String validationSchema)
    {
        this.validationSchema = validationSchema;
    }

    public String getVisibilityOfReferences()
    {
        return visibilityOfReferences;
    }

    public void setVisibilityOfReferences(String visibilityOfReferences)
    {
        this.visibilityOfReferences = visibilityOfReferences;
    }

    public Workflow getWorkflow()
    {
        return workflow;
    }

    public void setWorkflow(Workflow workflow)
    {
        this.workflow = workflow;
    }

    /**
     * @return the contactEmail
     */
    public String getContactEmail()
    {
        return contactEmail;
    }

    /**
     * @param contactEmail the contactEmail to set
     */
    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        else if (!(other instanceof PublicationAdminDescriptorVO))
        {
            return false;
        }
        else 
        {
            PublicationAdminDescriptorVO otherPublicationAdminDescriptorVO = (PublicationAdminDescriptorVO) other;
            if (otherPublicationAdminDescriptorVO.allowedGenres.containsAll(this.allowedGenres)
                && this.allowedGenres.containsAll(otherPublicationAdminDescriptorVO.allowedGenres)
                && otherPublicationAdminDescriptorVO.allowedSubjectClassifications.containsAll(this.allowedSubjectClassifications)
                && this.allowedSubjectClassifications.containsAll(otherPublicationAdminDescriptorVO.allowedSubjectClassifications)
                && ((otherPublicationAdminDescriptorVO.contactEmail == null && this.contactEmail == null)
                    || otherPublicationAdminDescriptorVO.contactEmail.equals(this.contactEmail)
                    )
                && ((otherPublicationAdminDescriptorVO.templateItem == null && this.templateItem == null)
                        || otherPublicationAdminDescriptorVO.templateItem.equals(this.templateItem)
                    )
                && ((otherPublicationAdminDescriptorVO.validationSchema == null && this.validationSchema == null)
                        || otherPublicationAdminDescriptorVO.validationSchema.equals(this.validationSchema)
                    )
                && ((otherPublicationAdminDescriptorVO.visibilityOfReferences == null && this.visibilityOfReferences == null)
                        || otherPublicationAdminDescriptorVO.visibilityOfReferences.equals(this.visibilityOfReferences)
                    )
                && otherPublicationAdminDescriptorVO.workflow == this.workflow
                )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
            
        
    }
    
    
}
