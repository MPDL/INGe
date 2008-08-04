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

/**
 * Object representing a Toc  (http://www.escidoc.de/schemas/table-of-content/0.1)
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TocVO extends ValueObject
{
   
    private String tocId;
    
    private String tocType;
    
    private String tocLabel;
    
    private TocDivVO tocDiv;
    
    private String tocBase;
    
    
    
    public String getTocId()
    {
        return tocId;
    }

    public void setTocId(String tocId)
    {
        this.tocId = tocId;
    }

    public String getTocType()
    {
        return tocType;
    }

    public void setTocType(String tocType)
    {
        this.tocType = tocType;
    }

    public String getTocLabel()
    {
        return tocLabel;
    }

    public void setTocLabel(String tocLabel)
    {
        this.tocLabel = tocLabel;
    }

    public TocDivVO getTocDiv()
    {
        return tocDiv;
    }

    public void setTocDiv(TocDivVO tocDiv)
    {
        this.tocDiv = tocDiv;
    }

    public String getTocBase()
    {
        return tocBase;
    }

    public void setTocBase(String tocBase)
    {
        this.tocBase = tocBase;
    }

    
}
