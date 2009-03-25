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

package de.mpg.escidoc.services.cone.util;

/**
 * A triple consisting of subject,, predicate and object.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Triple
{
    private String subject;
    private String predicate;
    private String object;
    
    /**
     * Default constructor.
     */
    public Triple()
    {
        
    }
    
    /**
     * Constructor with fields.
     * 
     * @param subject The identifier
     * @param predicate The attribute
     * @param object The value
     */
    public Triple(String subject, String predicate, String object)
    {
        this.object = object;
        this.predicate = predicate;
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    public String getPredicate()
    {
        return predicate;
    }
    public void setPredicate(String predicate)
    {
        this.predicate = predicate;
    }
    public String getObject()
    {
        return object;
    }
    public void setObject(String object)
    {
        this.object = object;
    }
}