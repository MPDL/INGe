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
package de.mpg.escidoc.pubman.util;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AccessException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Class to handle the file upload of locators. 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class FileLocatorUploadBean extends FacesBean
{
    private static final long serialVersionUID = 1L;
    
    private Logger logger = Logger.getLogger(FileLocatorUploadBean.class);    
    private String type;                                                // File MimeType
    protected String name;                                                // File Name
    protected String locator;                                             // File Location
    private int size; 
    String error = null;                                                // Error Message

    /**
     * Executes a HEAD request to the locator.
     * @param locator
     * @return true if locator is accessible
     */
    public boolean ckeckLocator(String locator)
    {
        this.locator = locator;
        URLConnection conn = null;
        byte[] input = null;
        String mimeType = null;
        String fileName = null;
        URL locatorURL = null;
        
        try
        {
            locatorURL = new URL(locator);         
            conn = locatorURL.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
             case 503:
                 this.error = getMessage("errorLocatorServiceUnavailable");
                 return false;
             case 302:
                 this.error = getMessage("errorLocatorServiceUnavailable");
                 return false;
             case 200:
                 this.logger.info("Source responded with 200.");
                 break;
             case 403:
                 this.error = getMessage("errorLocatorAccessDenied");
                 this.logger.warn("Access to url " + locator + " is restricted.");
                 return false;
             default:
                 this.error = getMessage("errorLocatorTechnicalException");
                 this.logger.warn("An error occurred during importing from external system: "
                                + responseCode + ": " + httpConn.getResponseMessage() + ".");
                 return false;
             }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            this.error = getMessage("errorLocatorAccessDenied");
            return false;
        }
        catch (MalformedURLException e)
        {
            this.error = getMessage("errorLocatorInvalidURL");
            this.logger.warn("Invalid locator URL:" + locator, e);
            return false;
        }
        catch (Exception e)
        {
            this.error = getMessage("errorLocatorTechnicalException");
            return false;          
        }

        //Get Content Type
        mimeType = conn.getHeaderField("Content-Type");
        if (mimeType.contains(";"))
        {
            mimeType = mimeType.substring(0, mimeType.indexOf(";"));
        }
        if (mimeType != null)
        {
            this.setType(mimeType);
        }
        //Get File Name
        fileName = conn.getHeaderField("file-name");
        if (fileName != null)
        {
            this.setName(fileName);
        }        
        else
        {
            this.setName(locatorURL.toString());
        }
        //Get File Length
        try
        {
            this.setSize(Integer.parseInt(conn.getHeaderField("Content-Length")));
        }
        catch (NumberFormatException e)
        {
            input = this.fetchLocator(locatorURL);
            if (input != null)
            {
                this.setSize(input.length);
            }
        }       
        return true;
    }
    
    /**
     * Executes a GET request to the locator.
     * @param locato
     * @return byte[]
     */
    private byte[] fetchLocator(URL locator)
    {   
        byte[] input = null;
        URLConnection conn = null;
        
        try
        {
            conn = locator.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch(responseCode)
            {
                case 200:
                    this.logger.info("Source responded with 200.");
                    
                    // Fetch file
                    GetMethod method = new GetMethod(locator.toString());
                    HttpClient client = new HttpClient();
                    client.executeMethod(method);
                    input = method.getResponseBody();
                    httpConn.disconnect();
                    break;
            }
        }
        catch (Exception e)
        {
            this.error = getMessage("errorLocatorTechnicalException");
            return null;           
        }
        
        return input;
    }
    
    /**
     * Populates the FileVO.
     */
    public abstract void locatorUploaded();
    
    public abstract void removeEmptyFile();
    
    /**
     * Removes the last added locator from the locator list.
     */
    public abstract void removeLocator();
    
    /**
     * 
     * @param item
     * @return
     */
    public Vector<FileVO> getLocators (PubItemVO item)
    {
        Vector<FileVO> locators = new Vector<FileVO>();
        
        List <FileVO> files = item.getFiles();
        for (int i =0; i< files.size(); i++)
        {
            FileVO currentFile = files.get(i);
            if (currentFile.getStorage() == FileVO.Storage.EXTERNAL_URL)
            {
                locators.add(currentFile);
            }
        }
        
        return locators;
    }
    
    /**
     * 
     * @return
     */
    public FileVO uploadLocatorAsFile (FileVO locator)
    {
        FileVO fileVO = null;
        
        boolean check = this.ckeckLocator(locator.getName());

        if (check)
        {           
            try
            {
                    fileVO = new FileVO();
                    fileVO.getMetadataSets().add(new MdsFileVO());
                    fileVO.getDefaultMetadata().setSize(this.getSize());
                    fileVO.getDefaultMetadata().setTitle(new TextVO(this.name));
                    fileVO.setMimeType(this.getType());
                    fileVO.setName(this.getLocator());

                    FormatVO formatVO = new FormatVO();
                    formatVO.setType("dcterms:IMT");
                    formatVO.setValue(this.getType());
                    fileVO.getDefaultMetadata().getFormats().add(formatVO);
                    fileVO.setContent(this.getLocator());
                    fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);

            }
            catch (Exception e)
            {
                this.logger.error(e);
                this.error = getMessage("errorLocatorUploadFW");
            }        
        }
        
        if (this.getError()!= null)
        {
            error(getMessage("errorLocatorMain").replace("$1", this.getError()));
            return null;
        }
        
        return fileVO;
    }
    
    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getError()
    {
        return this.error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
    
    public int getSize()
    {
        return this.size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }
    public String getLocator()
    {
        return this.locator;
    }

    public void setLocator(String locator)
    {
        this.locator = locator;
    }

}
