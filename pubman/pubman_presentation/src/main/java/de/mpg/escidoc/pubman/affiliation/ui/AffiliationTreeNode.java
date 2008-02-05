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

package de.mpg.escidoc.pubman.affiliation.ui;

import javax.faces.event.ActionListener;

import de.mpg.escidoc.pubman.affiliation.YuiTreeNode;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * TreeNode component that can store an affiliation. 
 * 
 * @author: Hugo Niedermaier, created 31.05.2007
 * @version: $Revision: 11 $ $LastChangedDate: 2007-11-21 19:07:30 +0100 (Mi, 21 Nov 2007) $
 * Revised by NiH: 13.08.2007
 */
public class AffiliationTreeNode extends YuiTreeNode implements ActionListener
{
    private AffiliationVO affiliationVO = null;
    
    /** reference to treeNode factory, to create children on demand */
    private AffiliationTreeNodeUI treeNodeUI = null;
    
    /** flag if children have been fetched */
    boolean haveChildrenFetched;
    
    AffiliationTreeNode( AffiliationTreeNodeUI node ) 
    {
        haveChildrenFetched = false;
        this.treeNodeUI = node;
    }
    
    public AffiliationVO getAffiliationVO()
    {
        return affiliationVO;
    }

    public void setAffiliationVO(AffiliationVO affiliationVO)
    {
        this.affiliationVO = affiliationVO;
    }    
    
    
    public synchronized void processAction(javax.faces.event.ActionEvent event)
    {   
       if( haveChildrenFetched == false ) {
           haveChildrenFetched = true;
           treeNodeUI.addChildren(this);
       }
    }
}
