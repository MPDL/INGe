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

package de.mpg.escidoc.pubman.multipleimport;

import java.util.TreeMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.model.UploadedFile;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.createItem.CreateItem.SubmissionMethod;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Session bean to hold data needed for an import of multiple items.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class MultipleImport extends FacesBean
{
    
    private static final Logger logger = Logger.getLogger(MultipleImport.class);
    
    public static final String BEAN_NAME = "MultipleImport";
    
    public static final String LOAD_MULTIPLE_IMPORT = "loadMultipleImport";
    public static final String LOAD_MULTIPLE_IMPORT_FORM = "loadMultipleImportForm";
    
    public static final Format ESCIDOC_FORMAT = new Format("escidoc-publication-item", "application/xml", "UTF-8");
    
    public static final Format ENDNOTE_FORMAT = new Format("endnote", "text/plain", "UTF-8");
    public static final Format BIBTEX_FORMAT = new Format("bibtex", "text/plain", "UTF-8");
    public static final Format EDOC_FORMAT = new Format("edoc", "application/xml", "UTF-8");
    public static final Format RIS_FORMAT = new Format("ris", "text/plain", "UTF-8");
    public static final Format WOS_FORMAT = new Format("wos", "text/plain", "UTF-8");
    
    private TreeMap<String, Object> importFormats = new TreeMap<String, Object>();
    private UploadedFile uploadedImportFile;
    
    private ImportProcess importProcess = null;
    
    private ContextVO context;
    private Format format;
    private String name;
    
    private boolean rollback = true;
    private int duplicateStrategy = 3;
    
    private Converter formatConverter = new Converter()
    {
        public Object getAsObject(FacesContext arg0, javax.faces.component.UIComponent arg1, String value)
        {
            if (value != null && !"".equals(value))
            {
                String[] parts = value.split("[\\[\\,\\]]");
                if (parts.length > 3)
                {
                    return new Format(parts[1], parts[2], parts[3]);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        
        public String getAsString(FacesContext arg0, UIComponent arg1, Object format)
        {
            if (format instanceof Format)
            {
                return ((Format) format).toString();
            }
            else
            {
                return null;
            }
        }
    };
    
    
    public MultipleImport()
    {
        
//        Transformation transformation = new TransformationBean();
//        Format[] formats = transformation.getSourceFormats(ESCIDOC_FORMAT);
//        for (Format format : formats)
//        {
//            format.setEncoding("UTF-8");
//            importFormats.put(format.getName(), format);
//        }
        importFormats.put("Endnote", ENDNOTE_FORMAT);
        importFormats.put("BibTeX", BIBTEX_FORMAT);
        importFormats.put("eDoc", EDOC_FORMAT);
        importFormats.put("RIS", RIS_FORMAT);
        importFormats.put("WoS", WOS_FORMAT);
        importFormats.put("eSciDoc", ESCIDOC_FORMAT);
    }
        
    public String uploadFile()
    {
        return LOAD_MULTIPLE_IMPORT_FORM;
    }
    
    public String getFileSize()
    {
        if (this.uploadedImportFile != null)
        {
            long size = uploadedImportFile.getLength();
            if (size < 1024)
            {
                return size + "B";
            }
            else if (size < 1024 * 1024)
            {
                return Math.round(size / 1024) + "KB";
            }
            else
            {
                return Math.round(size / (1024 * 1024)) + "MB";
            }
                
        }
        else
        {
            return null;
        }
    }
    
    public String startImport() throws Exception
    {

        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InternationalizationHelper i18nHelper = (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);
        
        importProcess = new ImportProcess(name, uploadedImportFile.getFilename(), uploadedImportFile.getInputStream(), format, context.getReference(), loginHelper.getAccountUser(), rollback, duplicateStrategy);
        importProcess.start();
            
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().redirect("ImportWorkspace.jsp");
        return null;
    }
    
    /**
     * JSF action that is triggered from the submission menu.
     * 
     * @return Depending on the contexts the user is allowed to create items in, either createItemPage or multipleImport
     */
    public String newImport()
    {
     // deselect the selected context
        ContextListSessionBean contextListSessionBean
            = (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
        if (contextListSessionBean.getDepositorContextList() != null)
        {
            for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++)
            {
                contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
            }
        }
        
        // set the current submission step to step2
        if (contextListSessionBean.getDepositorContextList() != null
                && contextListSessionBean.getDepositorContextList().size() > 1)
        {
            CreateItem createItem = (CreateItem) getSessionBean(CreateItem.class);
            createItem.setTarget(LOAD_MULTIPLE_IMPORT);
            createItem.setMethod(SubmissionMethod.MULTIPLE_IMPORT);
            return CreateItem.LOAD_CREATEITEM;
        }
        // Skip Collection selection for Import & Easy Sub if only one Collection
        else if (contextListSessionBean.getDepositorContextList() != null
                && contextListSessionBean.getDepositorContextList().size() == 1)
        {
            setContext(contextListSessionBean.getDepositorContextList().get(0));
            return LOAD_MULTIPLE_IMPORT;
        }
        else
        {
            logger.warn("No context for this user, therefore no import mask");
            return null;
        }
    }

    /**
     * @return the context
     */
    public ContextVO getContext()
    {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(ContextVO context)
    {
        this.context = context;
    }

    /**
     * @return the importFormats
     */
    public TreeMap<String, Object> getImportFormats()
    {
        return importFormats;
    }

    /**
     * @param importFormats the importFormats to set
     */
    public void setImportFormats(TreeMap<String, Object> importFormats)
    {
        this.importFormats = importFormats;
    }

    /**
     * @return the format
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format)
    {
        this.format = format;
    }

    /**
     * @return the uploadedImportFile
     */
    public UploadedFile getUploadedImportFile()
    {
        return uploadedImportFile;
    }

    /**
     * @param uploadedImportFile the uploadedImportFile to set
     */
    public void setUploadedImportFile(UploadedFile uploadedImportFile)
    {
        this.uploadedImportFile = uploadedImportFile;
    }

    /**
     * @return the rollback
     */
    public boolean getRollback()
    {
        return rollback;
    }

    /**
     * @param rollback the rollback to set
     */
    public void setRollback(boolean rollback)
    {
        this.rollback = rollback;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the duplicateStrategy
     */
    public int getDuplicateStrategy()
    {
        return duplicateStrategy;
    }

    /**
     * @param duplicateStrategy the duplicateStrategy to set
     */
    public void setDuplicateStrategy(int duplicateStrategy)
    {
        this.duplicateStrategy = duplicateStrategy;
    }

    /**
     * @return the formatConverter
     */
    public Converter getFormatConverter()
    {
        return formatConverter;
    }

    /**
     * @param formatConverter the formatConverter to set
     */
    public void setFormatConverter(Converter formatConverter)
    {
        this.formatConverter = formatConverter;
    }

}
