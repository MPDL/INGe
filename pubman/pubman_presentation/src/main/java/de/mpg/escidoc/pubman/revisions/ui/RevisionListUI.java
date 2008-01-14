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

package de.mpg.escidoc.pubman.revisions.ui;

import java.util.List;
import javax.faces.event.ActionListener;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.revisions.RelationVOWrapper;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.ListUI;
import de.mpg.escidoc.pubman.util.ValueObjectWrapper;

/**
 * UI for viewing a list of revisions.
 *
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class RevisionListUI extends ListUI implements ActionListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RevisionListUI.class);

    /**
     * Public constructor.
     * @param allRevisions the Revisions that should be shown in this list
     */
    public RevisionListUI(List<RelationVOWrapper> allRevisions)
    {
        // call constructor of super class
        super(allRevisions);                

        // display the first collections
        this.displayObjects();
    }

    /**
     * Instanciates a new single collection and adds it to the container for display.
     * @param valueObjectWrapper the wrapper with the ValueObject which should be displayed in the UI
     * @return the ContainerPanelUI in which the new Collection is displayed
     */
    protected ContainerPanelUI displayObject(ValueObjectWrapper valueObjectWrapper)
    {
        ViewRevisionPanelUI viewRevisionPanelUI = new ViewRevisionPanelUI((RelationVOWrapper)valueObjectWrapper);
        this.addToContainer(viewRevisionPanelUI);

        return viewRevisionPanelUI;
    }

    /**
     * Casts all objects stored in the super class to PubCollectionVOWrapper, so we don't have to do this every time.
     * @return all PubCollectionVOWrappers in this list
     */
    protected List<RelationVOWrapper> getAllObjects()
    {
        return (List<RelationVOWrapper>) super.getAllObjects();
    }

    /**
     * Casts all displayed objects stored in the super class to PubCollectionVOWrapper, so we don't have to do this every time.
     * @return all displayed PubCollectionVOWrapper in this list
     */
    @Override
    protected List<RelationVOWrapper> getObjectsToDisplay()
    {
        return (List<RelationVOWrapper>) super.getObjectsToDisplay();
    }
}
