/*
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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.dataacquisition;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AccessException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.mets.METSTransformation;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;

/**
 * This class provides the ejb implementation of the {@link DataHandler} interface.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
@Remote
@RemoteBinding(jndiBinding = DataHandler.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors({ LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class DataHandlerBean implements DataHandler
{
    private final Logger logger = Logger.getLogger(DataHandlerBean.class);
    private final String fetchTypeMETADATA = "METADATA";
    private final String fetchTypeFILE = "FILE";
    private final String fetchTypeCITATION = "CITATION";
    private final String fetchTypeLAYOUT = "LAYOUT";
    private final String fetchTypeUNKNOWN = "UNKNOWN";
    private final String regex = "GETID";
    // This is tmp till clarification of new transformation design
    private final String fetchTypeENDNOTE = "ENDNOTE";
    private final String fetchTypeBIBTEX = "BIBTEX";
    private final String fetchTypeAPA = "APA";
    private final String fetchTypeAJP = "AJP";
    private final String fetchTypeMETS = "METS";
    private DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
    private String contentType;
    private String fileEnding;
    private String contentCategorie;
    private String visibility = "PRIVATE";

    /**
     * public constructor for DataHandlerBean class.
     */
    public DataHandlerBean()
    {
    }

    /**
     * {@inheritDoc}
     */
    public byte[] doFetch(String sourceName, String identifier) throws SourceNotAvailableException, AccessException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, RuntimeException
    {
        DataSourceVO source = this.sourceHandler.getSourceByName(sourceName);
        MetadataVO md = this.sourceHandler.getDefaultMdFormatFromSource(source);
        return this.doFetch(sourceName, identifier, md.getMdLabel());
    }

    /**
     * {@inheritDoc}
     */
    public byte[] doFetch(String sourceName, String identifier, String format) throws SourceNotAvailableException,
            AccessException, IdentifierNotRecognisedException, FormatNotRecognisedException, RuntimeException
    {
        byte[] fetchedData = null;
        try
        {
            identifier = this.trimIdentifier(sourceName, identifier);
            DataSourceVO importSource = new DataSourceVO();
            importSource = this.sourceHandler.getSourceByName(sourceName);
            String fetchType = this.getFetchingType(importSource, format);
            
            //Hack for natasa, will be deleted when transformation service is impelemented
            if ((fetchType.equals(this.fetchTypeENDNOTE) || fetchType.equals(this.fetchTypeBIBTEX) 
                    || fetchType.equals(this.fetchTypeAPA) || fetchType.equals(this.fetchTypeAJP)) & sourceName.toLowerCase().equals("arxiv"))
            {
                // Temp
                fetchedData = this.fetchArxivHack(identifier, format, importSource);
                return fetchedData;
            }
            if (fetchType.equals(this.fetchTypeMETADATA))
            {
                fetchedData = this.fetchMetadata(importSource, identifier, format).getBytes();
            }
            if (fetchType.equals(this.fetchTypeFILE))
            {
                fetchedData = this.fetchData(importSource, identifier, new String[] {format });
            }
            if (fetchType.equals(this.fetchTypeCITATION))
            {
                // TODO
            }
            if (fetchType.equals(this.fetchTypeLAYOUT))
            {
                // TODO
            }
            if (fetchType.equals(this.fetchTypeENDNOTE))
            {
                // Temp
                fetchedData = this.fetchEndnoteTemp(identifier);
            }
            if (fetchType.equals(this.fetchTypeBIBTEX))
            {
                // Temp
                fetchedData = this.fetchBibtexTemp(identifier);
            }
            if (fetchType.equals(this.fetchTypeAPA))
            {
                // Temp
                fetchedData = this.fetchApaTemp(identifier);
            }
            if (fetchType.equals(this.fetchTypeAJP))
            {
                // Temp
                fetchedData = this.fetchAjpTemp(identifier);
            }
            if (fetchType.equals(this.fetchTypeMETS))
            {
                // Temp
                fetchedData = this.fetchMETSTemp(identifier);
            }
            if (fetchType.equals(this.fetchTypeUNKNOWN))
            {
                throw new FormatNotRecognisedException();
            }
            this.logger.info("Fetched file type: " + fetchType + ".");
        }
        catch (AccessException e)
        {
            throw new AccessException(sourceName);
        }
        catch (IdentifierNotRecognisedException e)
        {
            throw new IdentifierNotRecognisedException();
        }
        catch (SourceNotAvailableException e)
        {
            throw new SourceNotAvailableException();
        }
        catch (RuntimeException e)
        {
            throw new RuntimeException();
        }
        return fetchedData;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] doFetch(String sourceName, String identifier, String[] formats) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, RuntimeException
    {
        identifier = this.trimIdentifier(sourceName, identifier);
        DataSourceVO importSource = new DataSourceVO();
        importSource = this.sourceHandler.getSourceByName(sourceName);
        return this.fetchData(importSource, identifier, formats);
    }

    /**
     * {@inheritDoc}
     */
    public String explainSources() throws RuntimeException
    {
        String explainXML = "";
        try
        {
            explainXML = ResourceUtil.getResourceAsString("resources/sources.xml");
        }
        catch (IOException e)
        {
            this.logger.error("An error occurred while accessing sources.xml.", e);
            throw new RuntimeException();
        }
        return explainXML;
    }

    /**
     * Operation for fetching data of type METADATA.
     * 
     * @param importSource
     * @param identifier
     * @param format
     * @return itemXML
     * @throws IdentifierNotRecognisedException
     * @throws SourceNotAvailableException
     * @throws AccessException
     */
    private String fetchMetadata(DataSourceVO importSource, String identifier, String format)
        throws IdentifierNotRecognisedException, AccessException, SourceNotAvailableException
    {
        String itemXML = null;
        boolean supportedProtocol = false;
        InitialContext initialContext = null;
        MetadataHandler mdHandler;
        MetadataVO md = this.getMdObjectToFetch(importSource, format);
        // Replace regex with identifier
        try
        {
            String decoded = java.net.URLDecoder.decode(md.getMdUrl().toString(), importSource.getEncoding());
            md.setMdUrl(new URL(decoded));
            md.setMdUrl(new URL(md.getMdUrl().toString().replaceAll(this.regex, identifier.trim())));
            importSource = this.sourceHandler.updateMdEntry(importSource, md);
            // Select harvesting method
            if (importSource.getHarvestProtocol().toLowerCase().equals("oai-pmh"))
            {
                this.logger.debug("Fetch OAI record from URL: " + md.getMdUrl());
                itemXML = fetchOAIRecord(importSource, md);
                supportedProtocol = true;
            }
            if (importSource.getHarvestProtocol().toLowerCase().equals("ejb"))
            {
                this.logger.debug("Fetch record via EJB.");
                itemXML = this.fetchEsciDocRecord(identifier);
                supportedProtocol = true;
            }
            if (!supportedProtocol)
            {
                this.logger.warn("Harvesting protocol " + importSource.getHarvestProtocol() + " not supported.");
                throw new RuntimeException();
            }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            throw new AccessException(importSource.getName());
        }
        catch (IdentifierNotRecognisedException e)
        {
            this.logger.error("The Identifier " + identifier + "was not recognized by source " + importSource.getName()
                    + ".", e);
            throw new IdentifierNotRecognisedException();
        }
        catch (MalformedURLException e)
        {
            this.logger.error("An error occurred while retrieving the source URL ( " + md.getMdUrl() + " ).", e);
            throw new RuntimeException();
        }
        catch (UnsupportedEncodingException e)
        {
            this.logger.error("An error occurred while decoding the source URL ( " + md.getMdUrl() + " ).", e);
            throw new RuntimeException();
        }
        // Transform the itemXML if necessary
        if (itemXML != null && !itemXML.trim().equals("")
                && !format.trim().toLowerCase().equals(md.getMdLabel().toLowerCase()))
        {
            try
            {
                initialContext = new InitialContext();
                mdHandler = (MetadataHandler) initialContext.lookup(MetadataHandler.SERVICE_NAME);
                itemXML = mdHandler.transform(md.getMdLabel(), format, itemXML);
            }
            catch (NamingException e)
            {
                this.logger.error("Unable to initialize Metadata Handler.", e);
                throw new RuntimeException();
            }
            catch (Exception e)
            {
                this.logger.error("An error occured while transforming the metadata.", e);
                throw new RuntimeException();
            }
        }
        this.setContentType(md.getMdFormat());
        this.setFileEnding(md.getFileType());
        return itemXML;
    }

    /**
     * fetch data from a given url.
     * 
     * @param url
     * @return byte[]
     * @throws SourceNotAvailableException
     * @throws RuntimeException
     * @throws AccessException
     */
    public byte[] fetchMetadatafromURL(URL url) throws SourceNotAvailableException, RuntimeException, AccessException
    {
        byte[] input = null;
        URLConnection conn = null;
        Date retryAfter = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try
        {
            conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 503:
                    String retryAfterHeader = conn.getHeaderField("Retry-After");
                    if (retryAfterHeader != null)
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                        retryAfter = dateFormat.parse(retryAfterHeader);
                        this.logger.debug("Source responded with 503, retry after " + retryAfter + ".");
                        throw new SourceNotAvailableException(retryAfter);
                    }
                    break;
                case 302:
                    String alternativeLocation = conn.getHeaderField("Location");
                    return fetchMetadatafromURL(new URL(alternativeLocation));
                case 200:
                    this.logger.info("Source responded with 200.");
                    // Fetch file
                    GetMethod method = new GetMethod(url.toString());
                    HttpClient client = new HttpClient();
                    client.executeMethod(method);
                    input = method.getResponseBody();
                    httpConn.disconnect();
                    // Create zip file with fetched file
                    ZipEntry ze = new ZipEntry("unapi");
                    ze.setSize(input.length);
                    ze.setTime(this.currentDate());
                    CRC32 crc321 = new CRC32();
                    crc321.update(input);
                    ze.setCrc(crc321.getValue());
                    zos.putNextEntry(ze);
                    zos.write(input);
                    zos.flush();
                    zos.closeEntry();
                    zos.close();
                    this.setContentType("application/zip");
                    this.setFileEnding(".zip");
                    break;
                case 403:
                    throw new AccessException("Access to url " + url + " is restricted.");
                default:
                    throw new RuntimeException("An error occurred during importing from external system: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            throw new AccessException(url.toString());
        }
        catch (MalformedURLException e)
        {
            this.logger.error("An error occurred while trying to set alternative server location.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("An error ocurred while creating the zip file.", e);
            throw new RuntimeException();
        }
        catch (ParseException e)
        {
            this.logger.error("Response Header could not be parsed.", e);
            throw new RuntimeException();
        }
        return baos.toByteArray();
    }

    /**
     * Operation for fetching data of type FILE.
     * 
     * @param importSource
     * @param identifier
     * @param listOfFormats
     * @return byte[] of the fetched file, zip file if more than one record was fetched
     * @throws RuntimeException
     * @throws SourceNotAvailableException
     */
    private byte[] fetchData(DataSourceVO importSource, String identifier, String[] listOfFormats) 
        throws SourceNotAvailableException, RuntimeException
    {
        byte[] in = null;
        FullTextVO fulltext = new FullTextVO();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try
        {
            // Call fetch file for every selected format
            for (int i = 0; i < listOfFormats.length; i++)
            {
                String format = listOfFormats[i];
                fulltext = this.getFtObjectToFetch(importSource, format);
                // Replace regex with identifier
                String decoded = java.net.URLDecoder.decode(fulltext.getFtUrl().toString(), importSource.getEncoding());
                fulltext.setFtUrl(new URL(decoded));
                fulltext.setFtUrl(new URL(fulltext.getFtUrl().toString().replaceAll(this.regex, identifier.trim())));
                this.logger.debug("Fetch file from URL: " + fulltext.getFtUrl());
                in = this.fetchFile(importSource, fulltext);
                this.setVisibility(fulltext.getVisibility());
                this.setContentCategorie(fulltext.getContentCategorie());
                this.setContentType(fulltext.getFtFormat());
                this.setFileEnding(fulltext.getFileType());
                // If only one file => return it in fetched format
                if (listOfFormats.length == 1)
                {
                    return in;
                }
                // If more than one file => add it to zip
                else
                {
                    ZipEntry ze = new ZipEntry(identifier + fulltext.getFileType());
                    ze.setSize(in.length);
                    ze.setTime(this.currentDate());
                    CRC32 crc321 = new CRC32();
                    crc321.update(in);
                    ze.setCrc(crc321.getValue());
                    zos.putNextEntry(ze);
                    zos.write(in);
                    zos.flush();
                    zos.closeEntry();
                }
            }
            this.setContentType("application/zip");
            this.setFileEnding(".zip");
            zos.close();
        }
        catch (SourceNotAvailableException e)
        {
            this.logger.error("Import Source " + importSource + " not available.", e);
            throw new SourceNotAvailableException();
        }
        catch (RuntimeException e)
        {
            this.logger.error("Technical problems occurred when communication with import source.", e);
            throw new RuntimeException();
        }
        catch (MalformedURLException e)
        {
            this.logger.error("Error when replacing regex in fetching URL.");
            throw new RuntimeException();
        }
        catch (UnsupportedEncodingException e)
        {
            this.logger.error("Error when decoding source url.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("An error ocurred while creating the zip file.", e);
            throw new RuntimeException();
        }
        return baos.toByteArray();
    }

    /**
     * Handlers the http request to fetch a file from an external source.
     * 
     * @param importSource
     * @param fulltext
     * @return byte[] of the fetched file
     * @throws SourceNotAvailableException
     * @throws RuntimeException
     */
    private byte[] fetchFile(DataSourceVO importSource, FullTextVO fulltext) throws SourceNotAvailableException,
            RuntimeException, AccessException
    {
        URLConnection conn = null;
        Date retryAfter = null;
        byte[] input = null;
        try
        {
            conn = fulltext.getFtUrl().openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 503:
                    String retryAfterHeader = conn.getHeaderField("Retry-After");
                    if (retryAfterHeader != null)
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                        retryAfter = dateFormat.parse(retryAfterHeader);
                        this.logger.debug("Source responded with 503, retry after " + retryAfter + ".");
                        throw new SourceNotAvailableException(retryAfter);
                    }
                    else
                    {
                        this.logger.debug("Source responded with 503, retry after " 
                                + importSource.getRetryAfter() + ".");
                        throw new SourceNotAvailableException(importSource.getRetryAfter());
                    }
                case 302:
                    String alternativeLocation = conn.getHeaderField("Location");
                    fulltext.setFtUrl(new URL(alternativeLocation));
                    return fetchFile(importSource, fulltext);
                case 200:
                    this.logger.info("Source responded with 200.");
                    GetMethod method = new GetMethod(fulltext.getFtUrl().toString());
                    HttpClient client = new HttpClient();
                    client.executeMethod(method);
                    input = method.getResponseBody();
                    httpConn.disconnect();
                    break;
                case 403:
                    throw new AccessException("Access to url " + importSource.getName() + " is restricted.");
                default:
                    throw new RuntimeException("An error occurred during importing from external system: "
                            + responseCode + ": " + httpConn.getResponseMessage());
            }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            throw new AccessException(importSource.getName());
        }
        catch (MalformedURLException e)
        {
            this.logger.error("An error occurred while trying to set alternative server location.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("File could not be downloaded from.", e);
            throw new RuntimeException();
        }
        catch (ParseException e)
        {
            this.logger.error("Response Header could not be parsed.", e);
            throw new RuntimeException();
        }
        return input;
    }

    /**
     * Fetches an OAI record for given record identifier.
     * 
     * @param sourceURL
     * @return itemXML
     * @throws IdentifierNotRecognisedException
     * @throws SourceNotAvailableException
     * @throws RuntimeException
     */
    private String fetchOAIRecord(DataSourceVO importSource, MetadataVO md) throws SourceNotAvailableException,
            AccessException, IdentifierNotRecognisedException, RuntimeException
    {
        String itemXML = "";
        URLConnection conn;
        Date retryAfter;
        String charset = importSource.getEncoding();
        InputStreamReader isReader;
        BufferedReader bReader;
        try
        {
            conn = md.getMdUrl().openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 503:
                    String retryAfterHeader = conn.getHeaderField("Retry-After");
                    if (retryAfterHeader != null)
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                        retryAfter = dateFormat.parse(retryAfterHeader);
                        this.logger.debug("Source responded with 503, retry after " + retryAfter + ".");
                        throw new SourceNotAvailableException(retryAfter);
                    }
                    else
                    {
                        this.logger.debug("Source responded with 503, retry after " 
                                + importSource.getRetryAfter() + ".");
                        throw new SourceNotAvailableException(importSource.getRetryAfter());
                    }
                case 302:
                    String alternativeLocation = conn.getHeaderField("Location");
                    md.setMdUrl(new URL(alternativeLocation));
                    importSource = this.sourceHandler.updateMdEntry(importSource, md);
                    return fetchOAIRecord(importSource, md);
                case 200:
                    this.logger.info("Source responded with 200");
                    break;
                case 403:
                    throw new AccessException("Access to url " + importSource.getName() + " is restricted.");
                default:
                    throw new RuntimeException("An error occurred during importing from external system: "
                            + responseCode + ": " + httpConn.getResponseMessage());
            }
            String contentTypeHeader = conn.getHeaderField("Content-Type");
            String contentType = contentTypeHeader;
            if (contentType.contains(";"))
            {
                contentType = contentType.substring(0, contentType.indexOf(";"));
                if (contentTypeHeader.contains("encoding="))
                {
                    charset = contentTypeHeader.substring(contentTypeHeader.indexOf("encoding=") + 9);
                    this.logger.debug("Charset found: " + charset);
                }
            }
            // Get itemXML
            isReader = new InputStreamReader(md.getMdUrl().openStream(), charset);
            bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
                itemXML += line + "\n";
            }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            throw new AccessException(importSource.getName());
        }
        catch (MalformedURLException e)
        {
            this.logger.error("An error occurred while trying to set alternative server location.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("File could not be downloaded.", e);
            throw new RuntimeException();
        }
        catch (ParseException e)
        {
            this.logger.error("Response Header could not be parsed.", e);
            throw new RuntimeException();
        }
        return itemXML;
    }

    /**
     * Fetches a eSciDoc Record from eSciDoc system.
     * 
     * @param identifier of the item
     * @return itemXML as String
     * @throws IdentifierNotRecognisedException
     * @throws RuntimeException
     */
    private String fetchEsciDocRecord(String identifier) throws IdentifierNotRecognisedException, RuntimeException
    {
        try
        {
            return ServiceLocator.getItemHandler().retrieve(identifier);
        }
        catch (ItemNotFoundException e)
        {
            this.logger.error("Item with identifier " + identifier + " was not found.", e);
            throw new IdentifierNotRecognisedException(e);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while retrieving the item " + identifier + ".", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * For a more flexible interface for handling user input. This is the only source specific method, which should be
     * updated when a new source is specified for import
     * 
     * @param sourceName
     * @param identifier
     * @return a trimed identifier
     */
    public String trimIdentifier(String sourceName, String identifier)
    {
        // Trim the identifier arXiv
        if (sourceName.trim().toLowerCase().equals("arxiv") || sourceName.trim().toLowerCase().equals("arxiv(oai_dc)"))
        {
            if (identifier.toLowerCase().startsWith("oai:arxiv.org:", 0))
            {
                identifier = identifier.substring(14);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("arxiv:", 0))
            {
                identifier = identifier.substring(6);
                return identifier.trim();
            }
        }
        // Trim identifier for PubMedCentral
        if (sourceName.trim().toLowerCase().equals("pubmedcentral"))
        {
            if (identifier.toLowerCase().startsWith("pmcid:pmc", 0))
            {
                identifier = identifier.substring(9);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("pmcid:", 0))
            {
                identifier = identifier.substring(6);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("pmc", 0))
            {
                identifier = identifier.substring(3);
                return identifier.trim();
            }
        }
        return identifier.trim();
    }

    private byte[] fetchEndnoteTemp(String identifier) throws IdentifierNotRecognisedException, RuntimeException
    {
        byte[] cite = null;
        String item = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            StructuredExportHandler structExport = (StructuredExportHandler) initialContext
                    .lookup(StructuredExportHandler.SERVICE_NAME);
            item = this.fetchEsciDocRecord(identifier);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(item);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            cite = structExport.getOutput(itemList, "ENDNOTE");
            this.setContentType("text/plain");
            this.setFileEnding(".enl");
        }
        catch (IdentifierNotRecognisedException e)
        {
            this.logger.error("Item with identifier " + identifier + " was not found.", e);
            throw new IdentifierNotRecognisedException(e);
        }
        catch (NamingException e)
        {
            this.logger.error("An error occurred while initializing the context.", e);
            throw new RuntimeException();
        }
        catch (StructuredExportManagerException e)
        {
            this.logger.error("StructuredExportManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (StructuredExportXSLTNotFoundException e)
        {
            this.logger.error("StructuredExportManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (TechnicalException e)
        {
            this.logger.error("An error occurred while transforming the record " + identifier + " to a PubItem.", e);
            throw new RuntimeException();
        }
        return cite;
    }

    private byte[] fetchBibtexTemp(String identifier) throws IdentifierNotRecognisedException, RuntimeException
    {
        byte[] bib = null;
        String item = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            StructuredExportHandler structExport = (StructuredExportHandler) initialContext
                    .lookup(StructuredExportHandler.SERVICE_NAME);
            item = this.fetchEsciDocRecord(identifier);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(item);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            bib = structExport.getOutput(itemList, "BIBTEX");
            this.setContentType("text/plain");
            this.setFileEnding(".bib");
        }
        catch (IdentifierNotRecognisedException e)
        {
            this.logger.error("Item with identifier " + identifier + " was not found.", e);
            throw new IdentifierNotRecognisedException(e);
        }
        catch (NamingException e)
        {
            this.logger.error("An error occurred while initializing the context.", e);
            throw new RuntimeException();
        }
        catch (StructuredExportManagerException e)
        {
            this.logger.error("StructuredExportManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (StructuredExportXSLTNotFoundException e)
        {
            this.logger.error("StructuredExportManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (TechnicalException e)
        {
            this.logger.error("An error occurred while transforming the record " + identifier + " to a PubItem.", e);
            throw new RuntimeException();
        }
        return bib;
    }

    private byte[] fetchApaTemp(String identifier) throws IdentifierNotRecognisedException, RuntimeException
    {
        byte[] apa = null;
        String item = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext
                    .lookup(CitationStyleHandler.SERVICE_NAME);
            item = this.fetchEsciDocRecord(identifier);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(item);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            apa = citeHandler.getOutput("APA", "html", itemList);
            this.setContentType("text/html");
            this.setFileEnding(".html");
        }
        catch (IdentifierNotRecognisedException e)
        {
            this.logger.error("Item with identifier " + identifier + " was not found.", e);
            throw new IdentifierNotRecognisedException(e);
        }
        catch (NamingException e)
        {
            this.logger.error("An error occurred while initializing the context.", e);
            throw new RuntimeException();
        }
        catch (CitationStyleManagerException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (JRException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (TechnicalException e)
        {
            this.logger.error("An error occurred while transforming the record " + identifier + " to a PubItem.", e);
            throw new RuntimeException();
        }
        return apa;
    }

    private byte[] fetchAjpTemp(String identifier) throws IdentifierNotRecognisedException, RuntimeException
    {
        byte[] ajp = null;
        String item = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext
                    .lookup(CitationStyleHandler.SERVICE_NAME);
            item = this.fetchEsciDocRecord(identifier);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(item);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            String itemList = xmlTransforming.transformToItemList(pubitemList);
            ajp = citeHandler.getOutput("AJP", "html", itemList);
            this.setContentType("text/html");
            this.setFileEnding(".html");
        }
        catch (IdentifierNotRecognisedException e)
        {
            this.logger.error("Item with identifier " + identifier + " was not found.", e);
            throw new IdentifierNotRecognisedException(e);
        }
        catch (NamingException e)
        {
            this.logger.error("An error occurred while initializing the context.", e);
            throw new RuntimeException();
        }
        catch (CitationStyleManagerException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (JRException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            this.logger.error("CitationStyleManager threw an exception.", e);
            throw new RuntimeException();
        }
        catch (TechnicalException e)
        {
            this.logger.error("An error occurred while transforming the record " + identifier + " to a PubItem.", e);
            throw new RuntimeException();
        }
        return ajp;
    }
    
    /**
     * This method enables the fetching of arXiv data and the transformation in all available eSciDoc formats.
     * @param identifier
     * @param format
     * @param importSource
     * @return fetched item in requested format
     */
    private byte[] fetchArxivHack(String identifier, String format, DataSourceVO importSource) 
        throws AccessException, IdentifierNotRecognisedException, SourceNotAvailableException, RuntimeException
    {
        byte[] fetchedFormat = null;
        String eSciDocItem = null;

        
        //byte array of arXiv metadata in eSciDoc format
        eSciDocItem = this.fetchMetadata(importSource, identifier, "pubitem");
        System.out.println("Escidoc item: " + eSciDocItem);
        
        try
        {
            if (format.toLowerCase().equals("endnote"))
            {
                InitialContext initialContext = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
                StructuredExportHandler structExport = (StructuredExportHandler) initialContext
                        .lookup(StructuredExportHandler.SERVICE_NAME);
                PubItemVO itemVO = xmlTransforming.transformToPubItem(eSciDocItem);
                List<PubItemVO> pubitemList = Arrays.asList(itemVO);
                String itemList = xmlTransforming.transformToItemList(pubitemList);
                fetchedFormat = structExport.getOutput(itemList, "ENDNOTE");
                this.setContentType("text/plain");
                this.setFileEnding(".enl");
            }
            
            if (format.toLowerCase().equals("bibtex"))
            {
                InitialContext initialContext = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
                StructuredExportHandler structExport = (StructuredExportHandler) initialContext
                        .lookup(StructuredExportHandler.SERVICE_NAME);
                PubItemVO itemVO = xmlTransforming.transformToPubItem(eSciDocItem);
                List<PubItemVO> pubitemList = Arrays.asList(itemVO);
                String itemList = xmlTransforming.transformToItemList(pubitemList);
                fetchedFormat = structExport.getOutput(itemList, "BIBTEX");
                this.setContentType("text/plain");
                this.setFileEnding(".bib");
            }
            
            if (format.toLowerCase().equals("apa"))
            {
                InitialContext initialContext = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
                CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext
                        .lookup(CitationStyleHandler.SERVICE_NAME);
                PubItemVO itemVO = xmlTransforming.transformToPubItem(eSciDocItem);
                List<PubItemVO> pubitemList = Arrays.asList(itemVO);
                String itemList = xmlTransforming.transformToItemList(pubitemList);
                fetchedFormat = citeHandler.getOutput("APA", "html", itemList);
                this.setContentType("text/html");
                this.setFileEnding(".html");
            }
            
            if (format.toLowerCase().equals("ajp"))
            {
                InitialContext initialContext = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
                CitationStyleHandler citeHandler = (CitationStyleHandler) initialContext
                        .lookup(CitationStyleHandler.SERVICE_NAME);
                PubItemVO itemVO = xmlTransforming.transformToPubItem(eSciDocItem);
                List<PubItemVO> pubitemList = Arrays.asList(itemVO);
                String itemList = xmlTransforming.transformToItemList(pubitemList);
                fetchedFormat = citeHandler.getOutput("AJP", "html", itemList);
                this.setContentType("text/html");
                this.setFileEnding(".html");
            }
        }
        catch (Exception e) { throw new RuntimeException(); }
        
        return fetchedFormat;
    }
    
    private byte[] fetchMETSTemp(String identifier) throws RuntimeException
    {
        byte[] mets = null;
        METSTransformation metsTransform = new METSTransformation();
        mets = metsTransform.transformToMETS(identifier);
        return mets;
    }

    /**
     * This operation return the Metadata Object of the format to fetch from the source.
     * 
     * @param source
     * @param format
     * @return Metadata Object of the format to fetch
     */
    private MetadataVO getMdObjectToFetch(DataSourceVO source, String format)
    {
        MetadataVO sourceMd = null;
        MetadataVO transformMd = null;
        // First: check if format can be fetched directly
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            sourceMd = source.getMdFormats().get(i);
            if (sourceMd.getMdLabel().trim().toLowerCase().equals(format.trim().toLowerCase()))
            {
                return sourceMd;
            }
        }
        // Second: check which format can be transformed into the given format
        Vector<String> possibleFormats = this.sourceHandler.getFormatsForTransformation(format);
        Vector<MetadataVO> possibleMds = new Vector<MetadataVO>();
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            transformMd = source.getMdFormats().get(i);
            for (int x = 0; x < possibleFormats.size(); x++)
            {
                String possibleFormat = possibleFormats.get(x);
                if (transformMd.getMdLabel().trim().toLowerCase().equals(possibleFormat))
                {
                    possibleMds.add(transformMd);
                }
            }
        }
        // More than one format from this source can be transformed into the requested format
        if (possibleMds.size() > 1)
        {
            for (int y = 0; y < possibleMds.size(); y++)
            {
                transformMd = possibleMds.get(y);
                if (transformMd.isMdDefault())
                {
                    return sourceMd = this.sourceHandler.getMdObjectfromSource(source, transformMd.getMdLabel());
                }
                // If no default format was declared, one random like metadata set is returned
                else
                {
                    sourceMd = this.sourceHandler.getMdObjectfromSource(source, transformMd.getMdLabel());
                }
            }
        }
        if (possibleMds.size() == 1)
        {
            sourceMd = possibleMds.get(0);
        }
        if (possibleMds.size() == 0)
        {
            sourceMd = null;
        }
        return sourceMd;
    }

    /**
     * This operation return the Fulltext Object of the format to fetch from the source.
     * 
     * @param source
     * @param format
     * @return Fulltext Object of the format to fetch
     */
    private FullTextVO getFtObjectToFetch(DataSourceVO source, String format)
    {
        FullTextVO ft = null;
        for (int i = 0; i < source.getFtFormats().size(); i++)
        {
            ft = source.getFtFormats().get(i);
            if (ft.getFtLabel().trim().toLowerCase().equals(format.trim().toLowerCase()))
            {
                return ft;
            }
            else
            {
                ft = null;
            }
        }
        return ft;
    }

    /**
     * Decide which kind of data has to be fetched.
     * 
     * @param source
     * @param format
     * @return type of data to be fetched {METADATA, FILE, CITATION, LAYOUTFORMAT}
     */
    private String getFetchingType(DataSourceVO source, String format)
    {
        // tmp, till we clearify the new transformation service design
        if (format.toLowerCase().equals("endnote"))
        {
            return this.fetchTypeENDNOTE;
        }
        if (format.toLowerCase().equals("bibtex"))
        {
            return this.fetchTypeBIBTEX;
        }
        if (format.toLowerCase().equals("apa"))
        {
            return this.fetchTypeAPA;
        }
        if (format.toLowerCase().equals("ajp"))
        {
            return this.fetchTypeAJP;
        }
        if (format.toLowerCase().equals("mets"))
        {
            return this.fetchTypeMETS;
        }
        if (this.getMdObjectToFetch(source, format) != null)
        {
            return this.fetchTypeMETADATA;
        }
        if (this.getFtObjectToFetch(source, format) != null)
        {
            return this.fetchTypeFILE;
        }
        return this.fetchTypeUNKNOWN;
    }

    /**
     * method for retrieving the current sys date.
     * @return current date
     */
    public long currentDate()
    {
        Date today = new Date();
        return today.getTime();
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getFileEnding()
    {
        return this.fileEnding;
    }

    public void setFileEnding(String fileEnding)
    {
        this.fileEnding = fileEnding;
    }

    public String getContentCategorie()
    {
        return this.contentCategorie;
    }

    public void setContentCategorie(String contentCategorie)
    {
        this.contentCategorie = contentCategorie;
    }

    public String getVisibility()
    {
        return this.visibility;
    }

    public void setVisibility(String visibility)
    {
        this.visibility = visibility;
    }
}
