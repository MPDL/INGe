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
    
    private ItemRO templateItem;
    
    private String validationSchema;
    
    private String visibilityOfReferences;
    
    private Workflow workflow;

    public List<MdsPublicationVO.Genre> getAllowedGenres()
    {
        return allowedGenres;
    }

    public void setAllowedGenres(List<MdsPublicationVO.Genre> allowedGenres)
    {
        this.allowedGenres = allowedGenres;
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
    
}
