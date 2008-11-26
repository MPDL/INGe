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

package de.mpg.escidoc.pubman.viewItem;

import java.util.ArrayList;

/**
 * ViewItemSource.java Keeps structural information about the source(s) in the pubitem like nesting etc.
 * 
 * @author: Tobias Schraut, created 20.02.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 22.08.2007
 */
public class ViewItemSource
{
    private String sourceID;
    private boolean creatorsCollapsed;
    private boolean alternativeTitlesCollapsed;
    private boolean sourcesOfSourceCollapsed;
    // is this the last source node in the source's structure
    private boolean lastSource;
    private int paddingLeft;
    // the next node of the source's structure with all the sources in it
    private ArrayList<ViewItemSource> viewItemSourceEmbedded;

    /**
     * Public constructor
     */
    public ViewItemSource()
    {
    }

    /**
     * Public constructor with parameters
     * 
     * @param sourceID the ID of the source object
     * @param isCreatorsCollapsed flag if the creators should be displayed as collapsed (false if expanded)
     * @param isAbstractTitlesCollapsed flag if the abstract(s) should be displayed as collapsed (false if expanded)
     * @param isSourcesOfSourceCollapsed flag if the sources of this source should be displayed as collapsed (false if
     *            expanded)
     * @param isLastSource flag if this source is the last source in the structure
     */
    public ViewItemSource(String sourceID, boolean isCreatorsCollapsed, boolean isAbstractTitlesCollapsed,
            boolean isSourcesOfSourceCollapsed, boolean isLastSource)
    {
        this.sourceID = sourceID;
        this.alternativeTitlesCollapsed = isAbstractTitlesCollapsed;
        this.creatorsCollapsed = isCreatorsCollapsed;
        this.sourcesOfSourceCollapsed = isSourcesOfSourceCollapsed;
        this.lastSource = isLastSource;
        this.viewItemSourceEmbedded = null;
    }

    // Getters and Setters
    public boolean isCreatorsCollapsed()
    {
        return creatorsCollapsed;
    }

    public void setCreatorsCollapsed(boolean creatorsCollapsed)
    {
        this.creatorsCollapsed = creatorsCollapsed;
    }

    public String getSourceID()
    {
        return sourceID;
    }

    public void setSourceID(String sourceID)
    {
        this.sourceID = sourceID;
    }

    public boolean isSourcesOfSourceCollapsed()
    {
        return sourcesOfSourceCollapsed;
    }

    public void setSourcesOfSourceCollapsed(boolean sourcesOfSourceCollapsed)
    {
        this.sourcesOfSourceCollapsed = sourcesOfSourceCollapsed;
    }

    public boolean isAlternativeTitlesCollapsed()
    {
        return alternativeTitlesCollapsed;
    }

    public void setAlternativeTitlesCollapsed(boolean alternativeTitlesCollapsed)
    {
        this.alternativeTitlesCollapsed = alternativeTitlesCollapsed;
    }

    public int getPaddingLeft()
    {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft)
    {
        this.paddingLeft = paddingLeft;
    }

    public boolean isLastSource()
    {
        return lastSource;
    }

    public void setLastSource(boolean lastSource)
    {
        this.lastSource = lastSource;
    }

    public ArrayList<ViewItemSource> getViewItemSourceEmbedded()
    {
        return viewItemSourceEmbedded;
    }

    public void setViewItemSourceEmbedded(ArrayList<ViewItemSource> viewItemSourceEmbedded)
    {
        this.viewItemSourceEmbedded = viewItemSourceEmbedded;
    }
}
