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

package de.mpg.escidoc.services.common.util.namespaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReplaceNamespacesWithMavenTokens
{
    
    private static String POM_PATH = "../pom.xml";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            System.out.println("Usage: java ReplaceNamespacesWithMavenTokens xml1 [[,xml2] ...]");
            System.out.println("Optional: -Dpom=path_to_parent_pom");
            System.out.println("This will replace all namespaces that can be found in the input xml file with available tokens from the pom.xml.");
        }
        else
        {
            if (System.getProperty("pom") != null)
            {
                POM_PATH = System.getProperty("pom");
            }
            
            File pom = new File(POM_PATH);
            Map<String, String> namespaces = new HashMap<String, String>();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(pom));
            String line;
            Pattern pattern = Pattern.compile("\\s*<(xsd.[^>]+)>([^<]+)</xsd.[^>]+>");
            while ((line = bufferedReader.readLine()) != null)
            {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    namespaces.put(matcher.group(2), matcher.group(1));
                }
            }

            for (int i = 0; i < args.length; i++)
            {
                File file = new File(args[i]);
                String fileName = file.getName();
                File folder = file.getParentFile();
                File tempFile = File.createTempFile(fileName, ".tmp", folder);
                FileWriter fileWriter = new FileWriter(tempFile);
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((line = bufferedReader.readLine()) != null)
                {
                    for (String namespace : namespaces.keySet())
                    {
                        line = line.replace("=\"" + namespace + "\"", "=\"${" + namespaces.get(namespace) + "}\"");
                    }
                    fileWriter.write(line);
                    fileWriter.write("\n");
                }
                fileWriter.close();
                bufferedReader.close();
                File backup = new File(folder, "_" + fileName);
                file.renameTo(backup);
                tempFile.renameTo(file);
                backup.delete();
            }
        }
    }
}
