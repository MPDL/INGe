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

package run;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Executable class for transformations.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Transformation
{
    private static final Logger logger = Logger.getLogger(Transformation.class);
    
    /**
     * Give the following arguments:
     * * source-filename: where the the source is located.
     * * source-format: the name of the format, e.g. "edoc"
     * * source-type: the mimetype, e.g. "application/xml"
     * * source-encoding: the character encoding scheme, e.g. "UTF-8"
     * * target-filename: where the the result should be stored.
     * * target-format: the name of the format, e.g. "edoc"
     * * target-type: the mimetype, e.g. "application/xml"
     * * target-encoding: the character encoding scheme, e.g. "UTF-8"
     * * transformation-service (optional): not yet implemented, use "escidoc".
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 8)
        {
            new Transformation(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], "escidoc");
        }
        else if (args.length == 9)
        {
            new Transformation(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        }
        else
        {
            logger.error("Wrong number of arguments");
        }
    }
    
    public Transformation(String srcFilename, String srcFormatName, String srcType, String srcEncoding, String trgFilename, String trgFormatName, String trgType, String trgEncoding, String service) throws Exception
    {
        File srcFile = new File(srcFilename);
        File trgFile = new File(trgFilename);
        
        InputStream srcStream = new FileInputStream(srcFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[2048];
        int read;
        while ((read = srcStream.read(buffer)) != -1)
        {
            byteArrayOutputStream.write(buffer, 0, read);
        }
        
        de.mpg.escidoc.services.transformationLight.Transformation transformation = new de.mpg.escidoc.services.transformationLight.TransformationBean(true);
        byte[] result = transformation.transform(byteArrayOutputStream.toByteArray(), srcFormatName, srcType, srcEncoding, trgFormatName, trgType, trgEncoding, service);
        
        FileOutputStream trgStream = new FileOutputStream(trgFile);
        trgStream.write(result);
        trgStream.close();
    }
}
