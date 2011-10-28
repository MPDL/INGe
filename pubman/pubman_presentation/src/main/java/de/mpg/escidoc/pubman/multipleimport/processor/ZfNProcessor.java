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

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.net.ftp.*;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * This class handles the multiple import for the zfn format
 *
 * @author kleinfercher (initial creation)
 *
 */
public class ZfNProcessor extends FormatProcessor
{
    private static final Logger logger = Logger.getLogger(ZfNProcessor.class);
    
    private boolean init = false;
    private String[] items = null;
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    FTPClient f = new FTPClient();
    boolean ftpOpen = false;
    private ArrayList<String> fileNames = new ArrayList<String>();
    private Map<String, String> config;
    private String currentFile = "";


    private void initialize()
    {
        this.init = true;
        
        InputStream in = getSource();
        ArrayList<String> itemList = new ArrayList<String>();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        String item = null;
        final int bufLength = 1024;
        char[] buffer = new char[ bufLength ];
        int readReturn;
        int count = 0;
        
        try
        {
            ZipEntry zipentry;
            ZipInputStream zipinputstream = new ZipInputStream(in);
            
            while ((zipentry = zipinputstream.getNextEntry()) != null)
            {
                count++;                
                StringWriter sw = new StringWriter();
                Reader reader = new BufferedReader(new InputStreamReader(zipinputstream, "UTF-8"));
                        
                while ((readReturn = reader.read(buffer)) != -1)
                    {
                        sw.write(buffer, 0, readReturn);
                    }
                        
                item = new String(sw.toString()); 
                itemList.add(item);
                this.fileNames.add(zipentry.getName());
                
                zipinputstream.closeEntry();
            }
            this.logger.debug("Zip file contains " + count + "elements");
            zipinputstream.close();
            this.counter = 0;
                       
            this.originalData = byteArrayOutputStream.toByteArray();           
            this.items = itemList.toArray(new String[]{});           
            this.length = this.items.length;  
        }
        catch (Exception e)
        {
            this.logger.error("Could not read zip File: " + e.getMessage());
            throw new RuntimeException("Error reading input stream", e);
        }       
    }

    /**
     * Fetches a file from a ftp server, using the information in config
     * @param config
     * @param user
     * @return
     * @throws Exception
     */
    public FileVO getFileforImport(Map<String, String> config, AccountUserVO user) throws Exception
    {
        this.setConfig(config);
        this.setCurrentFile(processZfnFileName(this.fileNames.get(0)));
        
        if (this.getConfig() != null)
        {
            InputStream in = this.fetchFile();
            return createPubFile(in,user);
        }
        return null;
    }
    
    /**
     * Converts an inputstream into a FileVO.
     * @param file
     * @param name
     * @param user
     * @return FileVO
     * @throws Exception
     */
    private FileVO createPubFile (InputStream in, AccountUserVO user)
        throws Exception
    {
        this.logger.debug("Creating PubFile: " + this.getCurrentFile());
        
        MdsFileVO mdSet = new MdsFileVO();
        FileVO fileVO = new FileVO();

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(this.getCurrentFile());

        URL fileURL = this.uploadFile(in, mimeType, user.getHandle());

        if (fileURL != null && !fileURL.toString().trim().equals(""))
        {
            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            fileVO.setVisibility(FileVO.Visibility.PUBLIC);
            fileVO.setDefaultMetadata(mdSet);
            fileVO.getDefaultMetadata().setTitle(new TextVO(this.getCurrentFile()));
            fileVO.setMimeType(mimeType);
            fileVO.setName(this.getCurrentFile());
            fileVO.setContent(fileURL.toString());
            fileVO.getDefaultMetadata().setSize(in.available());
            fileVO.setContentCategory(PubFileVOPresentation.ContentCategory.PUBLISHER_VERSION.getUri());
            fileVO.getDefaultMetadata().setLicense(this.getConfig().get("License"));

            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(mimeType);
            fileVO.getDefaultMetadata().getFormats().add(formatVO);
        }
        this.setCurrentFile("");
        this.fileNames.remove(0);
        return fileVO;
    }
    
    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    private URL uploadFile(InputStream in, String mimetype, String userHandle)
        throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in, -1));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        XmlTransformingBean ctransforming = new XmlTransformingBean(); 
        return ctransforming.transformUploadResponseToFileURL(response);
    }
    
    private void openFtpServer()throws Exception
    {
        String username = this.getConfig().get("ftpUser");
        String password = this.getConfig().get("ftpPwd");
        String server = this.getConfig().get("ftpServer");
        String dir = this.getConfig().get("ftpDirectory");
               
        this.f.connect(server);
        this.f.login(username, password);
        this.f.enterLocalActiveMode();
        this.f.changeWorkingDirectory(dir);
        this.logger.debug("Connection to ftp server established.");
        this.logger.debug("Mode: Active ftp");
        this.logger.debug("Dir: " + dir);
        
        this.ftpOpen = true;
    }
    
    private void closeFtpServer() throws Exception
    {
        this.f.logout();
        this.f.disconnect();        
        this.ftpOpen = false;
        this.logger.debug("Connection to ftp server closed.");
    }
    
    /**
     * Fetches a file from a given URL
     */
    private InputStream fetchFile()throws Exception
    {
        InputStream input = null;
        ByteArrayOutputStream  out = new ByteArrayOutputStream();

        if (!this.ftpOpen)
        {
            try{this.openFtpServer();}
            catch(Exception e)
            {
                this.logger.error("Could not open ftp server " + e.getCause());
                throw new Exception();
            }
        }
        
        if(this.f.retrieveFile(this.getCurrentFile(), out))
        {
            input = new ByteArrayInputStream(out.toByteArray());           
        }
        
        return input;
    }
    
    /**
     * Change a filename like:ZNC-1988-43c-0029.header.tei.xml in smth. like ZNC-1988-43c-0029.pdf
     * so we can fetch the corresponding full text to a metadata file
     * @param name
     * @return
     */
    private String processZfnFileName (String name) throws Exception
    {
        String[] nameArr = name.split("\\.");
        String nameNew = nameArr[0] + ".pdf";
        
        return nameNew;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (!init)
        {
            initialize();
        }
        boolean next = this.items != null && this.counter < this.items.length;
        if (!init && !next)
        {
            try
            {
                this.closeFtpServer();
            }
            catch (Exception e)
            {
                this.logger.error("Could not close ftp server connection");
            }
        }
        
        return (next);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public String next() throws NoSuchElementException
    {
        if (!init)
        {
            initialize();
        }
        if (this.items != null && this.counter < this.items.length)
        {
            this.counter++;
            return items[counter - 1];
        }
        else
        {
            throw new NoSuchElementException("No more entries left");
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new RuntimeException("Method not implemented");
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getLength()
     */
    @Override
    public int getLength()
    {
        return length;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getDataAsBase64()
     */
    @Override
    public String getDataAsBase64()
    {
        if (this.originalData == null)
        {
            return null;
        }
        else
        {
            return Base64.encode(this.originalData);
        }
    }
    
    public Map<String, String> getConfig()
    {
        return this.config;
    }

    public void setConfig(Map<String, String> config)
    {
        this.config = config;
    }

    public String getCurrentFile()
    {
        return this.currentFile;
    }

    public void setCurrentFile(String currentFile)
    {
        this.currentFile = currentFile;
    }

}
