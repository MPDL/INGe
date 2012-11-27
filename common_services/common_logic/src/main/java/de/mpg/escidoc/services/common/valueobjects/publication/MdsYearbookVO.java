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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.common.valueobjects.publication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

/**
 * The metadata of a Yearbook.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 4429 $ $LastChangedDate: 2012-05-29 16:20:07 +0200 (Di, 29 Mai 2012) $ by $Author: mfranke $
 * @updated 21-Nov-2007 11:48:44
 */
public class MdsYearbookVO extends MetadataSetVO implements Cloneable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc serialVersionUID
     * = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after JiBX enhancement of
     * VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once for the Application
     * Server, once for the local test).
     * 
     * @author Matthias Walter
     */
    private static final long serialVersionUID = 1L;
    /**
     * Persons and organizations who essentially participated in creating the content with a specific task, e.g. author,
     * translator, editor.
     */
    private List<CreatorVO> creators = new ArrayList<CreatorVO>();
    private String year;
    private String startDate;
    private String endDate;
    private List<String> includedContexts = new ArrayList<String>();

    /**
     * Creates a new instance.
     */
    public MdsYearbookVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param other The instance to copy.
     */
    public MdsYearbookVO(MdsYearbookVO other)
    {
        super(other);
        for (CreatorVO creator : other.getCreators())
        {
            getCreators().add((CreatorVO)creator.clone());
        }
        if (other.getStartDate() != null)
        {
            this.setStartDate(other.getStartDate());
        }
        if (other.getEndDate() != null) 
        {
            this.setEndDate(other.getEndDate());
        }
        if (other.getYear() != null)
        {
            this.setYear(other.getYear());
        }
        if (!other.getIncludedContexts().isEmpty())
        {
            this.setIncludedContexts(other.getIncludedContexts());
        }
    }

    /**
     * @return the list of creators of the item, i. e. any person or organization who essentially participated in
     *         creating the content with a specific task, e. g. author, translator, editor.
     */
    public java.util.List<CreatorVO> getCreators()
    {
        return this.creators;
    }

    /**
     * @return the year of the Yearbook.
     */
    public String getYear()
    {
        return this.year;
    }

    /**
     * @return the start date from when items are included as yearbook candidates.
     */
    public String getStartDate()
    {
        return this.startDate;
    }

    /**
     * @return the end date until when items are included as yearbook candidates.
     */
    public String getEndDate()
    {
        return this.endDate;
    }

    /**
     * @return the contexts which are taken into account for the candidates.
     */
    public List<String> getIncludedContexts()
    {
        return includedContexts;
    }

    /**
     * @param newYear (yyyy) the year of the yearbook.
     */
    public void setYear(String newYear)
    {
        if (newYear == null || newYear.equals(""))
        {
            this.year = null;
        }
        else
        {
            this.year = newYear;
        }
    }

    /**
     * @param newStartDate (yyyy-MM-dd) the date from when items will be taken into account for the candidates list.
     */
    public void setStartDate(String newStartDate)
    {
        if (newStartDate == null || newStartDate.equals(""))
        {
            this.startDate = null;
        }
        else
        {
            this.startDate = newStartDate;
        }
    }

    /**
     * @param newEndDate (yyyy-MM-dd) the date until when items will be taken into account for the candidates list.
     */
    public void setEndDate(String newEndDate)
    {
        if (newEndDate == null || newEndDate.equals(""))
        {
            this.endDate = null;
        }
        else
        {
            this.endDate = newEndDate;
        }
    }

    /**
     * @param newIncludedContexts the contexts which are taken into account for the candidates.
     */
    public void setIncludedContexts(List<String> newIncludedContexts)
    {
        this.includedContexts = newIncludedContexts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        MdsYearbookVO other = (MdsYearbookVO)obj;
        return equals(this.getTitle(), other.getTitle())
                && equals(this.getCreators(), other.getCreators())
                && equals(this.getYear(), other.getYear())
                && equals(this.getStartDate(), other.getStartDate())
                && equals(this.getEndDate(), other.getEndDate())
                && equals(this.getIncludedContexts(), other.getIncludedContexts());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MdsYearbookVO clone()
    {
        super.clone();
        return new MdsYearbookVO(this);
    }
}