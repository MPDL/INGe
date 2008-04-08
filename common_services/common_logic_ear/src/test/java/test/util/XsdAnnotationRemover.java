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

package test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to remove all XSD annotations from all .xsd files in a certain folder structure. Although XSD
 * annotations/documentation is supposed to clear things up, they sometimes cause the opposite: The overview on the
 * structure of an XSD gets lost. This class helps against this undesired effect.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 */
public class XsdAnnotationRemover
{
    private static final String XSD_INPUT_FOLDER = "xsd";
    private static final String XSD_OUTPUT_FOLDER = "xsd_without_annotations";

    private static List<File> fileList;

    private static final void treeWalk(File root)
    {        
        File[] files = root.listFiles();
        if (files == null || files.length < 1)
            return;
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                treeWalk(files[i]);
            }
            else if (files[i].getName().toLowerCase().endsWith(".xsd"))
            {                
                fileList.add(files[i]);
            }
        }
    }

    private static List<File> getAllFilesInDirectory(String root)
    {
        fileList = new ArrayList<File>();
        treeWalk(new File(root));
        return fileList;
    }

    /**
     * The main class with the purpose described in the class comment. 
     * 
     * @param args The command line arguments are ignored
     */
    public static void main(String[] args)
    {
        List<File> xsds = getAllFilesInDirectory(XSD_INPUT_FOLDER);

        for (File inFile : xsds)
        {
            try
            {
                FileInputStream fis = new FileInputStream(inFile);
                FileChannel fc = fis.getChannel();

                ByteBuffer buff = ByteBuffer.allocate((int)fc.size());
                fc.read(buff);

                byte[] byteBuff = buff.array();
                String xml = new String(byteBuff);

                String annotationBegin = "<xs:annotation>";
                String annotationEnd = "</xs:annotation>";
                int firstAnnotationBeginPos;
                int firstAnnotationEndPos;
                while ((firstAnnotationBeginPos = xml.indexOf(annotationBegin)) != -1)
                {
                    firstAnnotationEndPos = xml.indexOf(annotationEnd);
                    // remove linebreak before the removed annotation
                    int previousElementEnd = xml.substring(0, firstAnnotationBeginPos).lastIndexOf('>');
                    xml = xml.substring(0, previousElementEnd + 1) + xml.substring(firstAnnotationEndPos + annotationEnd.length(), xml.length());

                }

                String subfolder = inFile.getParent().substring(XSD_INPUT_FOLDER.length());
                String outFileName = XSD_OUTPUT_FOLDER + subfolder + System.getProperty("file.separator") + inFile.getName();
                System.out.println(outFileName);
                File outFile = new File(outFileName);
                File outFilePath = new File(XSD_OUTPUT_FOLDER + subfolder);
                if (outFilePath.mkdirs() || outFilePath.exists())
                {
                    FileWriter fw = new FileWriter(outFile);
                    fw.write(xml);
                    fw.close();
                }
            }
            catch (IOException ioe)
            {
                System.out.println("Error: " + ioe.toString());
            }
        }

    }

}
