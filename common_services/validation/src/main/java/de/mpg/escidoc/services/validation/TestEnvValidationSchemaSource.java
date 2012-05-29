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

package de.mpg.escidoc.services.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.validation.util.CacheTuple;

/**
 * This is a implementation of {@link ValidationSchemaSource}. It provides validation belonging to the content-model created by the the TestEnvConfiguration.
 * Only used for JUnit testing.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 *
 */
public class TestEnvValidationSchemaSource implements ValidationSchemaSource
{
    /**
     * {@inheritDoc}
     */
    public Map<CacheTuple, String> retrieveNewSchemas(Date lastUpdate)
    {  
        Map<CacheTuple, String> map = new HashMap<CacheTuple, String>();
        CacheTuple id = null;
        String schematron = null;
                
        try
        {            
            id = new CacheTuple(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"), "publication");  
            schematron = this.readFile("testenv/pubman_test_collection.xml");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        
        map.put(id, schematron);
        return map;
    }
    
    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public String readFile(final String fileName)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(ResourceUtil.getResourceAsStream(fileName));
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }
}
