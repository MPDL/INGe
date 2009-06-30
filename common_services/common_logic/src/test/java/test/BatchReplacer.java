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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BatchReplacer
{
    private static final String REPLACE_MAP_FILENAME = "C:/repository/common_services/common_logic/src/test/resources/replace-map.properties";
    private static final String EQUALS_TOKEN = "<<<EQUALS>>>";
    private static Map<String, String> replaceMap = new LinkedHashMap<String, String>();
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        File file = new File(REPLACE_MAP_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null)
        {
            line = line.replace("\\=", EQUALS_TOKEN);
            int firstEquals = line.indexOf("=");
            if (firstEquals != -1)
            {
                String key = line.substring(0, firstEquals).replace(EQUALS_TOKEN, "=");
                String value = line.substring(firstEquals + 1).replace(EQUALS_TOKEN, "=");
                replaceMap.put(key, value);
            }
        }
        for (String arg : args)
        {
            new BatchReplacer(new File(arg));
        }
    }
    
    public BatchReplacer(File file) throws Exception
    {
        if (file.isHidden() || file.getName().startsWith("."))
        {
            System.out.println("Ignoring file " + file.getAbsolutePath());
        }
        else if (file.isDirectory())
        {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles)
            {
                new BatchReplacer(subFile);
            }
        }
        else
        {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            StringWriter writer = new StringWriter();
            String line;
            while ((line = reader.readLine()) != null)
            {
                writer.write(line);
                writer.write("\n");
            }
            reader.close();
            isr.close();
            fis.close();
            
            String result = writer.toString();
            String newValue = result;
            
            for (String key : replaceMap.keySet())
            {
                String value = replaceMap.get(key);
                newValue = newValue.replace(key, value);
            }
            
            if (result.equals(newValue))
            {
                System.out.println("Not changed: " + file.getAbsolutePath());
            }
            else
            {
                System.out.println("Saving " + file.getAbsolutePath());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
                outputStreamWriter.write(newValue);
                outputStreamWriter.close();
            }
        }
    }
}
