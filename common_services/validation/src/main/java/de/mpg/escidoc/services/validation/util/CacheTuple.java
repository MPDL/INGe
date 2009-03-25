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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.validation.util;

/**
 *
 * Identifier class for XSLT transformer cache.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CacheTuple
{
    protected String schemaName;
    protected String contentModel;
    protected int hash = 1;
    
    public CacheTuple(String contentModel, String schemaName)
    {
        super();
        this.contentModel = contentModel;
        this.schemaName = schemaName;
        this.hash = (schemaName + contentModel).length();
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        this.schemaName = schemaName;
    }

    public String getContentModel()
    {
        return contentModel;
    }

    public void setContentModel(String contentModel)
    {
        this.contentModel = contentModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other)
    {
        if (this.schemaName != null
                && this.contentModel != null
                && other instanceof CacheTuple)
        {
            return (this.schemaName.equals(((CacheTuple) other).schemaName)
                    && this.contentModel.equals(((CacheTuple) other).contentModel));
        }
        else
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "[" + schemaName + "|" + contentModel + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return hash;
    }
    
    
}