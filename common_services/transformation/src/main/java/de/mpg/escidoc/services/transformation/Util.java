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

package de.mpg.escidoc.services.transformation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatsType;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Helper methods for the transformation service.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Util
{
    /**
     * Converts a simpleLiteral Objects into a String Object.
     * @param sl as SimpleLiteral
     * @return String
     */
    public String simpleLiteralTostring(SimpleLiteral sl)
    {
        return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
    }
    
    /**
     * Creates a format xml out of a format array.
     * @param formats as Format[]
     * @return xml as String
     */
    public String createFormatsXml(Format[] formats)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
            FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
            for (int i = 0; i < formats.length; i++)
            {
                Format format = formats[i];
                FormatType xmlFormat = xmlFormats.addNewFormat();
                SimpleLiteral name = xmlFormat.addNewName();
                XmlString formatName = XmlString.Factory.newInstance();
                formatName.setStringValue(format.getName());
                name.set(formatName);
                SimpleLiteral type = xmlFormat.addNewType();
                XmlString formatType = XmlString.Factory.newInstance();
                formatType.setStringValue(format.getType());
                type.set(formatType);
                SimpleLiteral enc = xmlFormat.addNewEncoding();
                XmlString formatEnc = XmlString.Factory.newInstance();
                formatEnc.setStringValue(format.getEncoding());
                enc.set(formatEnc);
    
            }
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xOpts.setUseDefaultNamespace();
            xmlFormatsDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            //TODO
            e.printStackTrace();
        }
        return baos.toString();
    }

    /**
     * Checks if two Format Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isFormatEqual(Format src1, Format src2)
    {
        if (!src1.getName().toLowerCase().trim().equals(src2.getName().toLowerCase().trim())) {return false;}
        if (!src1.getType().toLowerCase().trim().equals(src2.getType().toLowerCase().trim())) {return false;}
        if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*"))
        {
            return true;
        }
        else 
        {
            if (!src1.getEncoding().toLowerCase().trim().equals(src2.getEncoding().toLowerCase().trim())) {return false;}
            else {return true;}
        }
    }
    
    /**
     * Converts a Aormat Vector into a Format Array.
     * @param formatsV as Vector
     * @return Format[]
     */
    public Format[] formatVectorToFormatArray(Vector<Format> formatsV)
    {
        Format[] formatsA = new Format[formatsV.size()];     
        for (int i = 0; i < formatsV.size(); i++)
        {
            formatsA[i] = (Format) formatsV.get(i);
        }     
        return formatsA;
    }
    
    /**
     * Eliminates duplicates in a Vector.
     * @param dirtyVector as Vector<Format>
     * @return Vector with unique entries
     */
    public Vector<Format> getRidOfDuplicatesInVector(Vector<Format> dirtyVector)
    {
        Vector<Format> cleanVector = new Vector<Format>();
        Format format1;
        Format format2;
        
        
        for (int i = 0; i < dirtyVector.size(); i++)
        {
            boolean duplicate = false;
            format1 = (Format) dirtyVector.get(i);
            for (int x = i + 1; x < dirtyVector.size(); x++)
            {
                format2 = (Format) dirtyVector.get(x);
                if (this.isFormatEqual(format1, format2))
                {
                    duplicate = true;
                }
            }
            if (!duplicate)
            {
                cleanVector.add(format1);  
            }
        }
        
        return cleanVector;
    }
    
    /**
     * Checks if a array contains a specific format object.
     * @param formatArray
     * @param format
     * @return true if the array contains the format object, else false
     */
    public boolean containsFormat(Format[] formatArray, Format format)
    {
        if (formatArray == null || format == null)
        {
            return false;
        }
        for (int i = 0; i < formatArray.length; i++) 
        {
            Format tmp = formatArray[i];
            if (this.isFormatEqual(format, tmp))
            {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Merges a Vector of Format[] into Format[].
     * @param allFormatsV as Vector<Format[]>
     * @return Format[]
     */
    public Format[] mergeFormats(Vector<Format[]> allFormatsV)
    {
        Vector<Format> tmpV = new Vector<Format>();
        Format[] tmpA;
        
        for (int i = 0; i < allFormatsV.size(); i++)
        {
            tmpA = allFormatsV.get(i);
            for (int x = 0; x < tmpA.length; x++)
            {
                tmpV.add(tmpA[x]);
                //System.out.println(tmpA[x].getName());
            }
        }
        tmpV = this.getRidOfDuplicatesInVector(tmpV);
        return this.formatVectorToFormatArray(tmpV);
    }
    
    /**
     * Normalizes a given mimetype
     * @param mimetype
     * @return
     */
    public String normalizeMimeType(String mimetype)
    {
        String thisMimetype = mimetype;
        if (mimetype.toLowerCase().equals("text/xml"))
        {
            thisMimetype = "application/xml";
        }
        if (mimetype.toLowerCase().equals("text/rtf"))
        {
            thisMimetype = "application/rtf";
        }
        if (mimetype.toLowerCase().equals("text/richtext"))
        {
            thisMimetype = "application/rtf";
        }
        return thisMimetype;
    }
}
