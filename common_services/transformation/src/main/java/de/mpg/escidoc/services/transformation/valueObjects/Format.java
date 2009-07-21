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

package de.mpg.escidoc.services.transformation.valueObjects;

import java.io.Serializable;

/**
 * This class describes a format object.
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Format implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String type;
    private String encoding;
    
    /**
     * Formats object constructor.
     * @param name
     * @param type
     * @param encoding
     */
    public Format(String name, String type, String encoding)
    {
        this.name = name;
        this.type = type;
        this.encoding = encoding;
    }
    
    /**
     * Get the formats name.
     * @return String  name of the format
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Set the formats name.
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Get the formats type.
     * @return String  type of the format
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Set the formats name.
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /**
     * Get the formats encoding.
     * @return String  encoding of the format
     */
    public String getEncoding()
    {
        return this.encoding;
    }
    
    /**
     * Set the formats encoding.
     * @param encoding
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    public String toString()
    {
        return "Format[" + this.name + "," + this.type + "," + this.encoding + "]";
    }
    
    /**     
     * * {@inheritDoc}     
     * */    
    @Override    
    public boolean equals(Object other)    
    {        
        if (other == null)        
        {            
            return false;        
        }        
        else if (!(other instanceof Format))        
        {            
            return false;        
        }        
        else        
        {            
            return (this.name == null ? ((Format) other).name == null : this.name.equalsIgnoreCase(((Format) other).name))                    
                && (this.type == null ? ((Format) other).type == null : this.type.equalsIgnoreCase(((Format) other).type))                    
                && (this.encoding == null ? ((Format) other).encoding == null : this.encoding.equalsIgnoreCase(((Format) other).encoding));        
         }
    }
    
    /**     
     * Returns true if this format is contained in the other format.
     */   
    public boolean matches(Format other)
    {        
        if (other == null)        
        {            
            return false;        
        }       
        else if ((this.name == null ? other.name == null : this.name.equalsIgnoreCase(other.name))                    
            && (this.type == null ? other.type == null : this.type.equalsIgnoreCase(other.type))) 
        {            
            if (this.encoding == null)
            {
                return false;
            }
            else if (this.encoding.equalsIgnoreCase(other.encoding) || "*".equals(other.encoding))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
