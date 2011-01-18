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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Utf8ToUtf16Converter
{
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        File fileIn = new File(args[0]);
        File[] filesIn;
        if (fileIn.isDirectory())
        {
            filesIn = fileIn.listFiles();
        }
        else
        {
            filesIn = new File[]{fileIn};
        }
        
        for (File file : filesIn)
        {
            if (file.isFile())
            {
                File fileOut = File.createTempFile(file.getName(), ".tmp", fileIn);
                
                System.out.println(file.getName());
                
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                FileWriter fileWriter = new FileWriter(fileOut);
                
                String line;
                
                while ((line = bufferedReader.readLine()) != null)
                {
                    for (int i = 0; i < line.length(); i++)
                    {
                        String input = line.substring(i, i + 1);
                        byte[] bytes = input.getBytes("UTF-8");
                        if (bytes.length > 1)
                        {
                            input = getUnicodeExpression(input);
        
                        }
                        fileWriter.append(input);
                    }
                    fileWriter.append("\n");
                }
                bufferedReader.close();
                bufferedReader = null;
                fileWriter.close();
                fileWriter = null;
                String fName = file.getAbsolutePath();
                System.out.println(fName);
                System.out.println(file.renameTo(new File(fName + ".old")));
                System.out.println(fileOut.renameTo(new File(fName)));
            }
        }
    }

    private static String getUnicodeExpression(String input) throws UnsupportedEncodingException
    {
        byte[] bytes;
        bytes = input.getBytes("UTF-16");

        String hex0 = "0" + (bytes[2] < 0 ? Integer.toHexString(bytes[2] + 256) : Integer.toHexString(bytes[2]));
        if (hex0.length() == 3)
        {
            hex0 = hex0.substring(1);
        }
        
        String hex1 = "0" + (bytes[3] < 0 ? Integer.toHexString(bytes[3] + 256) : Integer.toHexString(bytes[3]));
        if (hex1.length() == 3)
        {
            hex1 = hex1.substring(1);
        }
        return "\\u" + hex0 + hex1;
    }
}
