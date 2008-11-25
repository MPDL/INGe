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

package de.mpg.escidoc.pubman.revisions;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.RelationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Keeps all attributes that are used for the whole session by the RevisionList.
 * @author:  Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1599 $ $LastChangedDate: 2007-11-21 20:51:24 +0100 (Mi, 21 Nov 2007) $
 */
public class RelationListSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "RelationListSessionBean";
    private static Logger logger = Logger.getLogger(RelationListSessionBean.class);

    private List<RelationVOPresentation> relationList = null;
    private PubItemVO pubItemVO = null;
    private String revisionDescription = new String();

    /**
     * Public constructor.
     */
    public RelationListSessionBean()
    {
        this.init();
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    public List<RelationVOPresentation> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<RelationVOPresentation> relationList) {
		this.relationList = relationList;
	}

	public PubItemVO getPubItemVO()
    {
        return pubItemVO;
    }

    public void setPubItemVO(PubItemVO pubItemVO)
    {
        // re-init the lists as this is a new PubItem
        this.setRevisionDescription(null);
        
        this.pubItemVO = pubItemVO;
    }

    public String getRevisionDescription()
    {
        return revisionDescription;
    }

    public void setRevisionDescription(String revisionDescription)
    {
        this.revisionDescription = revisionDescription;
    }
    
    public boolean getShowRelations()
    {
        boolean showRelations = false;
        if(this.relationList != null && this.relationList.size() > 0)
        {
            showRelations = true;
        }
        return showRelations;
    }
}
