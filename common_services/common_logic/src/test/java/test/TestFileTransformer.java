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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestFileTransformer
{
    public static final String STYLESHEET = "../../src/test/resources/transformTestFiles.xsl";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        for (String arg : args)
        {

            File source = new File(arg);
            File tmp = File.createTempFile(source.getName(), ".tmp", source.getParentFile());
            File stylesheet = new File(STYLESHEET);
            InputStream in = new FileInputStream(source);
            
            OutputStream out = new FileOutputStream(tmp);
            
            TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", TestFileTransformer.class.getClassLoader());
            Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(stylesheet)));

            transformer.transform(new StreamSource(in), new StreamResult(out));
            
            in.close();
            out.close();
            
            source.delete();
            tmp.renameTo(source);
            
        }
    }
}
