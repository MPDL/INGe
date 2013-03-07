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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * Identifiers can be internal or external.
 * 
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:59:09
 */
public class IdentifierVO extends ValueObject implements Cloneable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;

    /**
     * The possible types of the identifier.
     * 
     * @updated 05-Sep-2007 12:59:09
     */
    public enum IdType
    {
        CONE ("http://purl.org/escidoc/metadata/terms/0.1/CONE"), 
        URI ("http://purl.org/escidoc/metadata/terms/0.1/URI"), 
        ISBN ("http://purl.org/escidoc/metadata/terms/0.1/ISBN"), 
        ISSN ("http://purl.org/escidoc/metadata/terms/0.1/ISSN"), 
        DOI ("http://purl.org/escidoc/metadata/terms/0.1/DOI"), 
        URN ("http://purl.org/escidoc/metadata/terms/0.1/URN"),
        PII ("http://purl.org/escidoc/metadata/terms/0.1/PII"),
        EDOC ("http://purl.org/escidoc/metadata/terms/0.1/EDOC"), 
        ESCIDOC ("http://purl.org/escidoc/metadata/terms/0.1/ESCIDOC"), 
        ISI ("http://purl.org/escidoc/metadata/terms/0.1/ISI"), 
        PND("http://purl.org/escidoc/metadata/terms/0.1/PND"),
        ZDB ("http://purl.org/escidoc/metadata/terms/0.1/ZDB"),
        PMID ("http://purl.org/escidoc/metadata/terms/0.1/PMID"),
        ARXIV ("http://purl.org/escidoc/metadata/terms/0.1/ARXIV"),
        PMC ("http://purl.org/escidoc/metadata/terms/0.1/PMC"),
        BMC ("http://purl.org/escidoc/metadata/terms/0.1/BMC"),
        BIBTEX_CITEKEY ("http://purl.org/escidoc/metadata/terms/0.1/BIBTEX-CITEKEY"),
        REPORT_NR ("http://purl.org/escidoc/metadata/terms/0.1/REPORT-NR"),
        SSRN ("http://purl.org/escidoc/metadata/terms/0.1/SSRN"),
        /*SFX should not be in here*/
        //SFX ("http://purl.org/escidoc/metadata/terms/0.1/SFX"),
        PATENT_NR ("http://purl.org/escidoc/metadata/terms/0.1/PATENT-NR"),
        PATENT_APPLICATION_NR ("http://purl.org/escidoc/metadata/terms/0.1/PATENT-APPLICATION-NR"),
        PATENT_PUBLICATION_NR("http://purl.org/escidoc/metadata/terms/0.1/PATENT-PUBLICATION-NR"),
        OTHER ("http://purl.org/escidoc/metadata/terms/0.1/OTHER");
        
        
        private String uri;
        
        private IdType(String uri)
        {
        	this.uri=uri;
        }
        
        public String getUri()
        {
        	return uri;
        }
    }

    private String id;
    private IdType type;

    /**
     * Creates a new instance.
     */
    public IdentifierVO()
    {
        super();
    }

    /**
     * Creates a new instance with the given type and the given identifier.
     * 
     * @param type
     * @param id
     */
    public IdentifierVO(IdType type, String id)
    {
        super();
        this.type = type;
        this.id = id;
    }

    /**
     * Delivers the identifier.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Delivers the type of the identifier.
     */
    public IdType getType()
    {
        return type;
    }

    /**
     * Sets the identifier.
     * 
     * @param newVal
     */
    public void setId(String newVal)
    {
        id = newVal;
    }

    /**
     * Sets the type of the identifier.
     * 
     * @param newVal
     */
    public void setType(IdType newVal)
    {
        type = newVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        IdentifierVO clone = new IdentifierVO();
        clone.setId(getId());
        clone.setType(getType());
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        IdentifierVO other = (IdentifierVO)obj;
        return equals(getId(), other.getId()) && equals(getType(), other.getType());
    }

    /**
     * Returns the value of the type Enum as a String. If the Enum is not set, an empty String is returned.
     * 
     * @return the value of the type Enum
     */
    public String getTypeString()
    {
        if (getType() == null || getType().toString() == null)
        {
            return "";
        }
        return getType().toString();
    }

    /**
     * Sets the value of the type Enum by a String.
     * 
     * @param newValString
     */
    public void setTypeString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            setType(null);
        }
        else
        {
            IdentifierVO.IdType newVal = IdentifierVO.IdType.valueOf(newValString);
            setType(newVal);
        }
    }
}