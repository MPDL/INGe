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

package de.mpg.escidoc.services.dataacquisition;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import noNamespace.SourceType;
import noNamespace.SourcesDocument;
import noNamespace.SourcesType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;



/**
 * 
 * Helper methods for the DataAcquisition Service.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Util
{

    private TransformationBean transformer;
    private final Logger logger = Logger.getLogger(Util.class);
    private final String internalFormat = "eSciDoc-publication-item";
    private final String internalListFormat = "eSciDoc-publication-item-list";
    private final String transformationService = "escidoc";
    private final String dummyFormat = "unknown";

    //Cone
    private final String coneMethod = "rdf/escidocmimetypes";
    private final String coneRel = "urn:cone:escidocmimetype:";
    

    /**
     * Public constructor.
     */
    public Util()
    {
        this.transformer = new TransformationBean();
    }
    
    /**
     * Retrieves the default encoding ("UTF-8").
     * @param formatName
     * @return default encoding
     */
    public String getDefaultEncoding(String formatName)
    {
        if (formatName.equalsIgnoreCase(this.getInternalFormat())) 
        { return "UTF-8"; }
        return "*";
    }
    
    /**
     * @param formatName
     * @return default mimetype
     */
    public String getDefaultMimeType(String formatName)
    {
        if (formatName.equalsIgnoreCase("apa")) 
        { return "text/html"; }      
        if (formatName.equalsIgnoreCase("ajp")) 
        { return "text/html"; }       
        if (formatName.equalsIgnoreCase("endnote")) 
        { return "text/plain"; } 
        if (formatName.equalsIgnoreCase("bibtex")) 
        { return "text/plain"; } 
        if (formatName.equalsIgnoreCase("coins")) 
        { return "text/plain"; } 
        if (formatName.equalsIgnoreCase("pdf")) 
        { return "application/pdf"; } 
        if (formatName.equalsIgnoreCase("ps")) 
        { return "application/gzip"; } 
        
        return "application/xml";
    }
    
    /**
     * This operation return the Metadata Object of the format to fetch from the source.
     * 
     * @param source
     * @param trgFormatName
     * @param trgFormatType
     * @param trgFormatEndcoding
     * @return Metadata Object of the format to fetch
     * @throws FormatNotAvailableException 
     */
    public MetadataVO getMdObjectToFetch(DataSourceVO source, String trgFormatName, 
            String trgFormatType, String trgFormatEndcoding)
    {
        MetadataVO sourceMd = null;
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        
        // First: check if format can be fetched directly
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            sourceMd = source.getMdFormats().get(i);
            boolean fetchMd = true;
            
            if (!sourceMd.getName().equalsIgnoreCase(trgFormatName))
            { fetchMd = false; }
            if (!sourceMd.getMdFormat().equalsIgnoreCase(trgFormatType))
            { fetchMd = false; }
            if ((!sourceMd.getEncoding().equals("*")) && (!trgFormatEndcoding.equals("*")))
            {
                if (!sourceMd.getEncoding().equalsIgnoreCase(trgFormatEndcoding)) 
                {
                    fetchMd = false;
                }
            }
            
            if (fetchMd)
            { return sourceHandler.getMdObjectfromSource(source, sourceMd.getName()); }
        }
        
        // Second: check which format can be transformed into the given format
        Format[] possibleFormats = this.transformer.getSourceFormats(
                new Format(trgFormatName, trgFormatType, trgFormatEndcoding));
        
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            sourceMd = source.getMdFormats().get(i);
            for (int x = 0; x < possibleFormats.length; x++)
            {
                Format possibleFormat = possibleFormats[x];
                boolean fetchMd = true;
                    
                if (!sourceMd.getName().equalsIgnoreCase(possibleFormat.getName()))
                { fetchMd = false; }
                if (!sourceMd.getMdFormat().equalsIgnoreCase(possibleFormat.getType()))
                { fetchMd = false; }
                if ((!sourceMd.getEncoding().equals("*")) && (!possibleFormat.getEncoding().equals("*")))
                {
                    if (!sourceMd.getEncoding().equalsIgnoreCase(possibleFormat.getEncoding())) 
                    {
                        fetchMd = false;
                    }
                }
                    
                if (fetchMd)
                { return sourceHandler.getMdObjectfromSource(source, sourceMd.getName()); }
            }
        }
        return null;
    }
    
    /**
     * Checks if a target format can be transformed from escidoc format.
     * Will be more dynamic in future when transformation service can handle transformation queuing
     * @param trgFormatName
     * @param trgFormatType
     * @param trgFormatEncoding
     * @return true if transformation is provided, else false
     */
    public boolean checkEscidocTransform(String trgFormatName, String trgFormatType, String trgFormatEncoding)
    {
        Format target = new Format(trgFormatName, trgFormatType, trgFormatEncoding);
        Format escidoc = new Format(this.getInternalFormat(), this.getDefaultMimeType(this.getInternalFormat()), 
                this.getDefaultEncoding(this.getInternalFormat()));
        Format[] formats;

        formats = this.transformer.getTargetFormats(escidoc);
        
        for (int i = 0; i < formats.length; i++)
        {
            if (this.isFormatEqual(target, formats[i]))
            {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * This operation return the Fulltext Object of the format to fetch from the source.
     * 
     * @param source
     * @param formatName
     * @param formatType
     * @param formatEncoding
     * @return Fulltext Object of the format to fetch
     */
    public FullTextVO getFtObjectToFetch(DataSourceVO source, String formatName, 
            String formatType, String formatEncoding)
    {
        FullTextVO ft = null;
        
        for (int i = 0; i < source.getFtFormats().size(); i++)
        {
            ft = source.getFtFormats().get(i);
            boolean fetchMd = true;
            
            if (!ft.getName().equalsIgnoreCase(formatName))
            { fetchMd = false; }
            if (!ft.getFtFormat().equalsIgnoreCase(formatType))
            { fetchMd = false; }
            if ((!ft.getEncoding().equals("*")) && (!formatEncoding.equals("*")))
            {
                if (!ft.getEncoding().equalsIgnoreCase(formatEncoding)) 
                {
                    fetchMd = false;
                }
            }
            
            if (fetchMd)
            { return ft; }
            else 
            { ft = null; }
        }
        return ft;
    }
    
    /**
     * Trims the given identifier according to description in source.xml, for a more flexible user input handling.
     * @param source
     * @param identifier
     * @return a trimed identifier
     */
    public String trimIdentifier(DataSourceVO source, String identifier)
    {
        Vector <String> idPrefVec = source.getIdentifier();
        
        for (int i = 0; i < idPrefVec.size(); i++)
        {
            String idPref = idPrefVec.get(i);
            if (identifier.toLowerCase().startsWith(idPref))
            {
                //Plus one because of the delimiter (:)
                identifier = identifier.substring(idPref.length() + 1);
            }
        }

        // SPIRES is special case
        if (source.getName().equalsIgnoreCase("spires"))
        {
            //If identifier is DOI, the identifier has to be enhanced
            if ((!identifier.toLowerCase().startsWith("arxiv")) && (!identifier.toLowerCase().startsWith("hep")) 
                    && (!identifier.toLowerCase().startsWith("cond")))
            {
                identifier = "FIND+DOI+" + identifier;
            }
        }
        
        return identifier;
    }
    
    /**
     * This method retrieves all formats a given format can be transformed into.
     * @param fetchFormats
     * @return Vector of Metadata Value Objects
     */
    public Vector<MetadataVO> getTransformFormats(Vector<MetadataVO> fetchFormats)
    {
        Vector<MetadataVO> allFormats = new Vector<MetadataVO>();

        for (int i = 0; i < fetchFormats.size(); i++)
        {
            MetadataVO md = fetchFormats.get(i);
            Format format = new Format(md.getName(), md.getMdFormat(), md.getEncoding());
            Format [] formats = this.transformer.getTargetFormats(format);
            formats = this.handleDuplicateFormatNames(formats);
            //Create MetadataVO
            for (int x = 0; x < formats.length; x++)
            {
                Format formatTrans = formats[x];
                MetadataVO mdTrans = new MetadataVO(); 
                mdTrans.setName(formatTrans.getName());
                mdTrans.setMdFormat(formatTrans.getType());
                mdTrans.setEncoding(formatTrans.getEncoding());                 
                allFormats.add(mdTrans);
            }
        }
        return allFormats;
    }
    
    /**
     * Checks if a format can use escidoc as transition format.
     * @param metadataV
     * @return true if escidoc format can be transition format, else false
     */
    public boolean checkEscidocTransition(Vector<MetadataVO> metadataV, String identifier)
    {
        if (identifier.toLowerCase().contains(this.getInternalFormat()))
        {
            //Transition not possible for escidoc source
            return false;
        }
        else
        {
            for (int i = 0; i < metadataV.size(); i++)
            {
                MetadataVO md = metadataV.get(i);
                Format format = new Format(md.getName(), md.getMdFormat(), md.getEncoding());
                Format[] trgFormats = this.transformer.getTargetFormats(format);
                for (int x = 0; x < trgFormats.length; x++)
                {
                    Format trgFormat = trgFormats[x];
                    if (trgFormat.getName().equals(this.getInternalFormat()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Eliminates duplicates in a Vector.
     * @param dirtyVector as MetadataVO Vector
     * @return Vector with unique entries
     */
    public Vector<MetadataVO> getRidOfDuplicatesInVector(Vector<MetadataVO> dirtyVector)
    {
        Vector<MetadataVO> cleanVector = new Vector<MetadataVO>();
        MetadataVO format1;
        MetadataVO format2;
        
        
        for (int i = 0; i < dirtyVector.size(); i++)
        {
            boolean duplicate = false;
            format1 = (MetadataVO) dirtyVector.get(i);
            for (int x = i + 1; x < dirtyVector.size(); x++)
            {
                format2 = (MetadataVO) dirtyVector.get(x);
                if (this.isMdFormatEqual(format1, format2))
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
     * Checks if the format of two MetadataVO Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isMdFormatEqual(MetadataVO src1, MetadataVO src2)
    {
        if (!src1.getName().equalsIgnoreCase(src2.getName())) 
        { return false; }
        if (!src1.getMdFormat().equalsIgnoreCase(src2.getMdFormat())) 
        { return false; }
        
        if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*")) 
        { return true; }
        else
        {
            if (!src1.getEncoding().equalsIgnoreCase(src2.getEncoding())) 
            { return false; }
        }
        
        return true;
    }
    
    /**
     * Checks if two Format Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isFormatEqual(Format src1, Format src2)
    {
        if (!src1.getName().equalsIgnoreCase(src2.getName())) 
        { return false; }
        if (!src1.getType().equalsIgnoreCase(src2.getType())) 
        { return false; }
        if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*"))
        {
            return true;
        }
        else 
        {
            if (!src1.getEncoding().equalsIgnoreCase(src2.getEncoding())) 
            { return false; }
            else 
            { return true; }
        }
    }
    
    /**
     * Creates the source description xml.
     * @return xml as byte[]
     */
    public byte[] createUnapiSourcesXml()
    {
        byte[] xml = null;
        
        Vector<DataSourceVO> sources;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        
        try
        {
            sources = sourceHandler.getSources();
            SourcesDocument xmlSourceDoc = SourcesDocument.Factory.newInstance();
            SourcesType xmlSources = xmlSourceDoc.addNewSources();
            for (int i = 0; i < sources.size(); i++)
            {
                DataSourceVO source = sources.get(i);
                SourceType xmlSource = xmlSources.addNewSource();
                //Name
                SimpleLiteral name = xmlSource.addNewName();
                XmlString sourceName = XmlString.Factory.newInstance();
                sourceName.setStringValue(source.getName());
                name.set(sourceName);
                //Base url
                SimpleLiteral url = xmlSource.addNewIdentifier();
                XmlString sourceUrl = XmlString.Factory.newInstance();
                sourceUrl.setStringValue(source.getUrl().toExternalForm());
                url.set(sourceUrl);
                //Description
                SimpleLiteral desc = xmlSource.addNewDescription();
                XmlString sourceDesc = XmlString.Factory.newInstance();
                sourceDesc.setStringValue(source.getDescription());
                desc.set(sourceDesc);
                //Identifier prefix
                Vector <String> idPreVec = source.getIdentifier();
                for (int x = 0; x < idPreVec.size(); x++)
                {
                    SimpleLiteral idPreSimp = xmlSource.addNewIdentifierPrefix();
                    XmlString sourceidPre = XmlString.Factory.newInstance();
                    sourceidPre.setStringValue(idPreVec.get(x));
                    idPreSimp.set(sourceidPre);
                }               
                //Identifier delimiter
                SimpleLiteral idDel = xmlSource.addNewIdentifierDelimiter();
                XmlString sourceidDel = XmlString.Factory.newInstance();
                sourceidDel.setStringValue(":");
                idDel.set(sourceidDel);
                //Identifier example
                Vector<String> examples = source.getIdentifierExample();
                if (examples != null)
                {
                    for (String example : examples)
                    {                   
                        SimpleLiteral idEx = xmlSource.addNewIdentifierExample();
                        XmlString sourceidEx = XmlString.Factory.newInstance();
                        sourceidEx.setStringValue(example);
                        idEx.set(sourceidEx);
                    }   
                }
            }
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xOpts.setUseDefaultNamespace();
            xmlSourceDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            this.logger.error("Error when creating outputXml.", e);
            throw new RuntimeException(e);
        }
        
        xml = baos.toByteArray();        
        return xml;
    }
    
    /**
     * Extracts out of a url the escidoc import source name.
     * @param sourceName
     * @param identifier
     * @return trimmed sourceName as String
     */
    public String trimSourceName(String sourceName, String identifier)
    {
        if (identifier.startsWith("http://dev-pubman"))
        {
            sourceName = "escidocdev";
        }
        if (identifier.startsWith("http://qa-pubman"))
        {
            sourceName = "escidocqa";
        }
        if (identifier.startsWith("http://test-pubman"))
        {
            sourceName = "escidoctest";
        }
        if (identifier.startsWith("http://pubman"))
        {
            sourceName = "escidocprod";
        }        
        return sourceName;
    }
    
    /**
     * EsciDoc Identifier can consist of the citation URL, like:
     * http://pubman.mpdl.mpg.de:8080/pubman/item/escidoc:1048:3. This method extracts the identifier from the URL
     * @param identifier
     * @return escidoc identifier as String
     */
    public String setEsciDocIdentifier(String identifier)
    {
        if (identifier.contains("/"))
        {
            String[] extracts = identifier.split("/");
            return extracts[extracts.length - 1];
        }
        else             
        {
            if (!identifier.toLowerCase().startsWith("escidoc:"))
            {
                return "escidoc" + ":" + identifier;
            }
            else
            {
                return identifier;
            }
        }
    }
    
    /**
     * This methods gets a vector of formats, checks the formats names and adds
     * the format type to the name, if the name occurs more than once in the list.
     * @param formats
     * @return Vector of FormatVOs
     */
    private Format[] handleDuplicateFormatNames(Format[] formats)
    {       
        for (int i = 0; i < formats.length; i++)
        {
            Format currentFormat = formats[i];
            Format[] currentVector = formats;
            for (int x = i + 1; x < currentVector.length; x++)
            {
                Format compareFormat = currentVector[x];
                if (currentFormat.getName().equalsIgnoreCase(compareFormat.getName()))
                {
                    Format updatedFormat = new Format(currentFormat.getName() + "_" + currentFormat.getType(),
                            currentFormat.getType(), currentFormat.getEncoding());
                    formats[i] = updatedFormat;
                }
            }
        }
        
        return formats;
    }
    
    /**
     * Retrieves the fileending for a given mimetype from the cone service.
     * @param mimeType
     * @return fileending as String
     */
    public String retrieveFileEndingFromCone(String mimeType) 
    {
        String suffix = null;
        URLConnection conn;
        InputStreamReader isReader;
        BufferedReader bReader;
        
        try
        {

            URL coneUrl = new URL(PropertyReader.getProperty("escidoc.cone.service.url") + "/" 
                    + this.coneMethod + "/" + this.coneRel + mimeType);
            conn = coneUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    this.logger.info("Cone Service responded with 200.");
                    break;
                default:
                    throw new RuntimeException("An error occurred while calling Cone Service: "
                                + responseCode);
            }
            isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
            bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
                if (line.contains("suffix"))
                {
                    suffix = line.substring(line.indexOf("<suffix>") + "<suffix>".length(), line.indexOf("</suffix>"));
                }
            }
        }
        catch (Exception e)
        {
            this.logger.warn("Suffix could not be retrieved from cone service (mimetype: " + mimeType + ")", e);
            return null;
        }
        
        return suffix;
    }
    
    public String getInternalFormat()
    {
        return this.internalFormat;
    }

    public String getTransformationService()
    {
        return this.transformationService;
    }
    
    public String getDummyFormat()
    {
        return this.dummyFormat;
    }
}
