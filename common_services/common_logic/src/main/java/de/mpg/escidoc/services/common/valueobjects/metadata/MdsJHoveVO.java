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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.Date;

import org.dom4j.Element;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MdsJHoveVO extends MetadataSetVO
{

    // Attributes
    private String name;
    private String release;
    private Date dateAttribute;
    
    // Elements
    private Date date;
    private Element repInfo;
    
    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
    public Element getRepInfo()
    {
        return repInfo;
    }
    public void setRepInfo(Element repInfo)
    {
        this.repInfo = repInfo;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getRelease()
    {
        return release;
    }
    public void setRelease(String release)
    {
        this.release = release;
    }
    public Date getDateAttribute()
    {
        return dateAttribute;
    }
    public void setDateAttribute(Date dateAttribute)
    {
        this.dateAttribute = dateAttribute;
    }

}
