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

package de.mpg.escidoc.tools;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Validate
{
    private DocumentBuilder parser;
    
    public Validate(File rootDir) throws Exception
    {
        parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        File[] files = rootDir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                new Validate(file);
            }
            else
            {
                validate(file);
            }
        }
    }
    private void validate(File file)
    {
        try
        {
            parser.parse(file);
            //System.out.println("Valid: " + file);
        }
        catch (Exception e)
        {
            System.out.println("Invalid: " + file);
        }
    }
    

}
