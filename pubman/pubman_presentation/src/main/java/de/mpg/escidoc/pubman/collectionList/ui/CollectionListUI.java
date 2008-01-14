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

package de.mpg.escidoc.pubman.collectionList.ui;

import java.util.List;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeListener;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.collectionList.PubCollectionVOWrapper;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.ListUI;
import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * UI for viewing a list of collections.
 *
 * @author: Thomas Diebäcker, created 12.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class CollectionListUI extends ListUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CollectionListUI.class);

    /**
     * Public constructor.
     * @param allCollections the Collections that should be shown in this list
     */
    public CollectionListUI(List<PubCollectionVOWrapper> allCollections)
    {
        // call constructor of super class
        super(allCollections);                

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
        ViewCollectionPanelUI viewCollectionPanelUI = new ViewCollectionPanelUI((PubCollectionVOWrapper)valueObjectWrapper, this);
        this.addToContainer(viewCollectionPanelUI);

        return viewCollectionPanelUI;
    }

    /**
     * Selects/Deselects one item and deselects/selects the others 
     */
    public void selectOneItem(PubCollectionVOWrapper pubCollectionVOWrapper, boolean selected)
    {
        for (int i = 0; i < this.getAllObjects().size(); i++)
        {
            if (this.getAllObjects().get(i).equals(pubCollectionVOWrapper))
            {
                this.getAllObjects().get(i).setSelected(selected);
            }
            else
            {
                this.getAllObjects().get(i).setSelected(false);
            }
        }

        // redisplay the visible items
        this.displayObjects();
    }

    /**
     * Selects all items.
     */
    public void selectAllItems(boolean selected)
    {
        // set the selected attribute in the PubItemVOWrapper for all items
        this.selectItems(this.getAllObjects(), selected);
    }

    /**
     * Sets the selected attribute for every item in the given list.
     * @param list the list with items
     * @param selected the value for the selected attribute
     */
    private void selectItems(List<PubCollectionVOWrapper> list, boolean selected)
    {
        // set the selected attribute in the PubItemVOWrapper for the given items
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setSelected(selected);
        }

        // redisplay the visible items
        this.displayObjects();
    }

    /**
     * Casts all objects stored in the super class to PubCollectionVOWrapper, so we don't have to do this every time.
     * @return all PubCollectionVOWrappers in this list
     */
    protected List<PubCollectionVOWrapper> getAllObjects()
    {
        return (List<PubCollectionVOWrapper>) super.getAllObjects();
    }

    /**
     * Casts all displayed objects stored in the super class to PubCollectionVOWrapper, so we don't have to do this every time.
     * @return all displayed PubCollectionVOWrapper in this list
     */
    @Override
    protected List<PubCollectionVOWrapper> getObjectsToDisplay()
    {
        return (List<PubCollectionVOWrapper>) super.getObjectsToDisplay();
    }
    
    /**
     * Casts all selected objects stored in the super class to PubCollectionVO, so we don't have to do this every time.
     * @return all selected PubCollectionVOs in this list
     */
    @Override
    public List<PubCollectionVO> getSelectedObjects()
    {
        return (List<PubCollectionVO>) super.getSelectedObjects();
    }

    /**
     * As only one collection is selectable this is a convenience method for getting the first (and only) on out of
     * the selection list. 
     * @return the selected PubCollectionVO in this list
     */
    public PubCollectionVO getSelectedCollection()
    {
        if (this.getSelectedObjects().size() > 0)
        {
            return this.getSelectedObjects().get(0);
        }
        
        logger.warn("No collection selected.");
        
        return null;
    }
}
