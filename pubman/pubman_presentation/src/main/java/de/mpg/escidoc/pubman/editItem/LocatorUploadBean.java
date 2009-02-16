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
package de.mpg.escidoc.pubman.editItem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AccessException;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Class to handle the file upload of locators. 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class LocatorUploadBean extends FacesBean
{
    private static final long serialVersionUID = 1L;
    
    private String[] fileTypes = new String[]{".pdf", ".rtf", ".doc", ".xml", ".jpg", ".bmp", ".png"};
    private Logger logger = Logger.getLogger(LocatorUploadBean.class);
    private String type;
    private String name;
    private String locator;
    public EditItem editItem = new EditItem();
    String error = null;

    /**
     * Check if a locator is a valid URL referring to a valid file type.
     * @param locator
     * @return true if valid, else false
     */
    public boolean ckeckLocator(String locator)
    {
        boolean check = false;
        this.locator = locator;
        
        try
        {
            URL locatorURL = new URL(locator);
            for (int i = 0; i< this.fileTypes.length; i++)
            {
                String fileType = this.fileTypes[i];
                if (locator.toLowerCase().endsWith(fileType))
                {
                    this.type = fileType;
                    String[] splitArr = locator.split(new String("\u002F"));
                    this.name = splitArr[splitArr.length-1];
                    return true;
                }
            }
        }
        catch (MalformedURLException e)
        {
            this.error = getMessage("errorLocatorInvalidURL");
            this.logger.warn("Invalid locator URL:" + locator, e);
            return false;
        }
        
        String types = "";
        for (int i = 0; i< this.fileTypes.length; i++)
        {
            types += this.fileTypes[i] + " "; 
        }
        this.error = getMessage("errorLocatorInvalidType").replace("$1", types);
        return check;
    }
    
    /**
     * Fetch the file, the locator URL points to.
     * @param locator
     * @throws IOException
     */
    public void fetchLocator(URL locator) throws Exception
    {   
        byte[] input= null;
        URLConnection conn = null;
        
        try
        {
            conn = locator.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 503:
                    this.error = getMessage("errorLocatorServiceUnavailable");
                    break;
                case 302:
                    this.error = getMessage("errorLocatorServiceUnavailable");
                    break;
                case 200:
                    this.logger.info("Source responded with 200.");
                    
                    // Fetch file
                    GetMethod method = new GetMethod(locator.toString());
                    HttpClient client = new HttpClient();
                    client.executeMethod(method);
                    input = method.getResponseBody();
                    httpConn.disconnect();
                    break;
                case 403:
                    this.error = getMessage("errorLocatorAccessDenied");
                    throw new AccessException("Access to url " + locator + " is restricted.");
                default:
                    this.error = getMessage("errorLocatorTechnicalException");
                    throw new RuntimeException("An error occurred during importing from external system: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }
        }
        catch (AccessException e)
        {
            this.logger.error("Access denied.", e);
            this.error = getMessage("errorLocatorAccessDenied");
            throw new AccessException(locator.toString());
        }
        catch (Exception e)
        {
            this.error = getMessage("errorLocatorTechnicalException");
            throw new RuntimeException(e);           
        }
        
        this.locatorUploaded(input);
    }
    
    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param Inputstream The file to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    private URL uploadLocator(InputStream inputStr, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(inputStr));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransforming ctransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        return ctransforming.transformUploadResponseToFileURL(response);        
    }
    
    /**
     * Converts a inputStream into a FileVO and updates the context.
     * @param input
     */
    //private void locatorUploaded(InputStream input)
    private void locatorUploaded(byte[] input)
    {
      
      LoginHelper loginHelper = (LoginHelper)this.getBean(LoginHelper.class);
        
      String contentURL;
      if (input != null )
      {
        try
        {
            ByteArrayInputStream inStream = new ByteArrayInputStream(input);
            contentURL = uploadLocator(inStream, this.getMimetype(this.type), loginHelper.getESciDocUserHandle()).toString();
            if(contentURL != null && !contentURL.trim().equals(""))
            {    
                FileVO fileVO = new FileVO();
                fileVO.getMetadataSets().add(new MdsFileVO());
                fileVO.getDefaultMetadata().setSize(input.length);
                fileVO.setName(this.name);
                fileVO.getDefaultMetadata().setTitle(new TextVO(this.name));
                fileVO.setMimeType(this.getMimetype(this.type));

                FormatVO formatVO = new FormatVO();
                formatVO.setType("dcterms:IMT");
                formatVO.setValue(this.getMimetype(this.type));
                fileVO.getDefaultMetadata().getFormats().add(formatVO);
                fileVO.setContent(contentURL);
                fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                //TODO
                fileVO.setDescription("TODO");
                
                //The initinally created empty file has to be deleted
                this.removeEmptyFile();
                int index = this.editItem.getEditItemSessionBean().getFiles().size();
                
                List <PubFileVOPresentation> list = this.editItem.getEditItemSessionBean().getFiles();
                PubFileVOPresentation pubFile = new PubFileVOPresentation(index, fileVO, false);
                pubFile.setShowProperties(true);
                list.add(pubFile);
                this.editItem.getEditItemSessionBean().setFiles(list);

                this.removeLocator();
            }
        }
        catch (Exception e)
        {
            this.logger.error(e);
            this.error= getMessage("errorLocatorUploadFW");
        }
      }
      else 
      {
          this.logger.error("Empty component.");
          this.error = getMessage("errorLocatorTechnicalException");
      }

    }
    
    private void removeEmptyFile()
    {
        List <PubFileVOPresentation> list = this.editItem.getEditItemSessionBean().getFiles();
        for (int i =0; i< list.size(); i++)
        {
            PubFileVOPresentation file = list.get(i);
            if (file.getFile().getContent() == null || file.getFile().getContent().equals(""))
            {
                List <PubFileVOPresentation> listClean = this.editItem.getEditItemSessionBean().getFiles();
                listClean.remove(i);
                this.editItem.getEditItemSessionBean().setFiles(listClean);
            }
        }
    }
    
    /**
     * Removes the last added locator from the locator list.
     */
    public void removeLocator()
    {
        List <PubFileVOPresentation> list = this.editItem.getEditItemSessionBean().getLocators();
        for (int i =0; i< list.size(); i++)
        {
            PubFileVOPresentation locator = list.get(i);
            if (locator.getFile().getContent().equals(this.locator))
            {
                List <PubFileVOPresentation> listClean = this.editItem.getEditItemSessionBean().getLocators();
                listClean.remove(i);
                this.editItem.getEditItemSessionBean().setLocators(listClean);
                
                //Make sure at least one locator exists
                if (listClean.size() == 0)
                {
                    FileVO newLocator = new FileVO();
                    newLocator.getMetadataSets().add(new MdsFileVO());
                    newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
                    this.editItem.getEditItemSessionBean().getLocators().add(new PubFileVOPresentation(0, newLocator, true));  
                }
            }
        }
    }
    
    private String getMimetype(String fileEnding)
    {
        if (fileEnding.toLowerCase().equals(".pdf")) return "application/pdf";
        if (fileEnding.toLowerCase().equals(".rtf")) return "application/rtf";
        if (fileEnding.toLowerCase().equals(".doc")) return "application/msword";
        if (fileEnding.toLowerCase().equals(".xml")) return "application/xml";
        if (fileEnding.toLowerCase().equals(".jpg")) return "image/jpeg";
        if (fileEnding.toLowerCase().equals(".tiff")) return "image/tiff";
        if (fileEnding.toLowerCase().equals(".png")) return "image/png";       
        return "";
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
    
    public String[] getFileTypes()
    {
        return this.fileTypes;
    }

    public void setFileTypes(String[] fileTypes)
    {
        this.fileTypes = fileTypes;
    }
}
