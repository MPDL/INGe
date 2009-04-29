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

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.FileLocatorUploadBean;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
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
public class LocatorUploadBean extends FileLocatorUploadBean
{
    private static final long serialVersionUID = 1L;
    
    private Logger logger = Logger.getLogger(LocatorUploadBean.class);  
    public EditItem editItem = new EditItem();
    String error = null;                                                // Error Message

    
    
    /**
     * Populates the FileVO.
     */
    public void locatorUploaded()
    {
        try
        {
                FileVO fileVO = new FileVO();
                fileVO.getMetadataSets().add(new MdsFileVO());
                fileVO.getDefaultMetadata().setSize(this.getSize());
                fileVO.getDefaultMetadata().setTitle(new TextVO(super.name));
                fileVO.setMimeType(this.getType());
                fileVO.setName(this.getLocator());

                FormatVO formatVO = new FormatVO();
                formatVO.setType("dcterms:IMT");
                formatVO.setValue(this.getType());
                fileVO.getDefaultMetadata().getFormats().add(formatVO);
                fileVO.setContent(this.getLocator());
                fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                
                //The initinally created empty file has to be deleted
                this.removeEmptyFile();
                int index = this.editItem.getEditItemSessionBean().getFiles().size();
                
                List <PubFileVOPresentation> list = this.editItem.getEditItemSessionBean().getFiles();
                PubFileVOPresentation pubFile = new PubFileVOPresentation(index, fileVO, false);
                list.add(pubFile);
                this.editItem.getEditItemSessionBean().setFiles(list);
        }
        catch (Exception e)
        {
            this.logger.error(e);
            this.error = getMessage("errorLocatorUploadFW");
        }
    }
    
    public void removeEmptyFile()
    {
        List <PubFileVOPresentation> list = this.editItem.getEditItemSessionBean().getFiles();
        for (int i = 0; i < list.size(); i++)
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
        for (int i =0; i < list.size(); i++)
        {
            PubFileVOPresentation locatorPres = list.get(i);
            if (locatorPres.getFile().getContent().equals(super.locator))
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
    
    
}
