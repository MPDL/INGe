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
package de.mpg.escidoc.services.common.valueobjects;

public class TocPtrVO extends ValueObject
{
    
    private String id;
    
    private String loctype = "URL";
    
    private String use;
    
    private String mimetype;
    
    private String linkType;
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLoctype()
    {
        return loctype;
    }

    public void setLoctype(String loctype)
    {
        this.loctype = loctype;
    }

    public String getUse()
    {
        return use;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public String getMimetype()
    {
        return mimetype;
    }

    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }

    public String getLinkType()
    {
        return linkType;
    }

    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }

    public String getLinkRef()
    {
        return linkRef;
    }

    public void setLinkRef(String linkRef)
    {
        this.linkRef = linkRef;
    }

    public String getLinkTitle()
    {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle)
    {
        this.linkTitle = linkTitle;
    }

    private String linkRef;
    
    private String linkTitle;
}
