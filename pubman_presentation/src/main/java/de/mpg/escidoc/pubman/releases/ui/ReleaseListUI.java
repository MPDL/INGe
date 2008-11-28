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

package de.mpg.escidoc.pubman.releases.ui;

import java.util.List;

import javax.faces.event.ActionListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.releases.PubItemVersionVOWrapper;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.ListUI;
import de.mpg.escidoc.pubman.util.ValueObjectWrapper;

/**
 * UI for viewing a list of release entries.
 *
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ReleaseListUI extends ListUI implements ActionListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReleaseListUI.class);

    /**
     * Public constructor.
     * @param allCollections the Collections that should be shown in this list
     */
    public ReleaseListUI(List<PubItemVersionVOWrapper> allReleases)
    {
        // call constructor of super class
        super(allReleases);                

        // display the first collections
        this.displayObjects();
    }

    /**
     * Instanciates a new single release and adds it to the container for display.
     * @param valueObjectWrapper the wrapper with the ValueObject which should be displayed in the UI
     * @return the ContainerPanelUI in which the new Release is displayed
     */
    protected ContainerPanelUI displayObject(ValueObjectWrapper valueObjectWrapper)
    {
        ViewReleasePanelUI viewReleasePanelUI = new ViewReleasePanelUI((PubItemVersionVOWrapper)valueObjectWrapper, this, 0);
        this.addToContainer(viewReleasePanelUI);

        return viewReleasePanelUI;
    }
    
    /**
     * Instanciates a new single release and adds it to the container for display.
     * @param valueObjectWrapper the wrapper with the ValueObject which should be displayed in the UI
     * @param position the position of the release entry
     * @return the ContainerPanelUI in which the new Release is displayed
     */
    protected ContainerPanelUI displayObject(ValueObjectWrapper valueObjectWrapper, int position)
    {
        ViewReleasePanelUI viewReleasePanelUI = new ViewReleasePanelUI((PubItemVersionVOWrapper)valueObjectWrapper, this, position);
        this.addToContainer(viewReleasePanelUI);

        return viewReleasePanelUI;
    }
}
