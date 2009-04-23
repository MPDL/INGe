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

package de.mpg.escidoc.pubman.multipleimport;

import java.io.InputStream;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.ErrorLevel;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.Status;
import de.mpg.escidoc.pubman.multipleimport.processor.BibtexProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.EndnoteProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;
import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportProcess extends Thread
{
    private static final Logger logger = Logger.getLogger(ImportProcess.class);
    
    public enum DuplicateStrategy
    {
        NO_CHECK, CHECK, ROLLBACK
    }
    
    private ImportLog log;
    private InputStream inputStream;
    private Transformation transformation;
    private ItemValidating itemValidating;
    private ValidationTransforming validationTransforming;
    private XmlTransforming xmlTransforming;
    private PubItemDepositing pubItemDepositing;
    private Format format;
    private ContextRO escidocContext;
    private AccountUserVO user;
    private FormatProcessor formatProcessor;
    private String fileName;
    
    private boolean rollback;
    private DuplicateStrategy duplicateStrategy;
    
    private boolean failed = false;
    
    
    private static final Format ESCIDOC_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "utf-8");
    
    private static final Format ENDNOTE_FORMAT = new Format("endnote", "text/plain", "utf-8");
    private static final Format BIBTEX_FORMAT = new Format("bibtex", "text/plain", "utf-8");
    private static final Format EDOC_FORMAT = new Format("edoc", "application/xml", "utf-8");
    private static final Format RIS_FORMAT = new Format("ris", "text/plain", "utf-8");
    private static final Format WOS_FORMAT = new Format("wos", "text/plain", "utf-8");
    
    private String name;
    
    public ImportProcess(
            String name,
            String fileName,
            InputStream inputStream,
            Format format,
            ContextRO escidocContext,
            AccountUserVO user,
            boolean rollback,
            int duplicateStrategy)
    {
        
        log = new ImportLog("import", user.getReference().getObjectId(), format.getName());
        
        log.setPercentage(5);
        
        // Say Hello
        log.startItem("import_process_started");
        log.finishItem();
        
        DuplicateStrategy strategy;
        if (duplicateStrategy == 1)
        {
            strategy = DuplicateStrategy.NO_CHECK;
        }
        else if (duplicateStrategy == 2)
        {
            strategy = DuplicateStrategy.CHECK;
        }
        else if (duplicateStrategy == 3)
        {
            strategy = DuplicateStrategy.ROLLBACK;
        }
        else
        {
            throw new RuntimeException("Invalid value " + duplicateStrategy + " for DuplicateStrategy");
        }
            
        
        // Initialize
        initialize(name, fileName, inputStream, format, escidocContext, user, rollback, strategy);
        
        log.setPercentage(7);
        
        if (log.isDone())
        {
            return;
        }
        
        // Validate
        if (!validate(inputStream, format))
        {
            return;
        }
        log.setPercentage(10);
    }

    /**
     * @param inputStream
     * @param format
     */
    private void initialize(
            String name,
            String fileName,
            InputStream inputStream,
            Format format,
            ContextRO escidocContext,
            AccountUserVO user,
            boolean rollback,
            DuplicateStrategy duplicateStrategy)
    {
        log.startItem("import_process_initialize");
        try
        {
            log.setMessage(name);
            log.setContext(escidocContext.getObjectId());
            log.setFormat(format.getName());
            
            this.name = name;
            this.fileName = fileName;
            this.format = format;
            this.escidocContext = escidocContext;
            this.user = user;
            this.transformation = new TransformationBean();
            this.inputStream = inputStream;
            this.rollback = rollback;
            this.duplicateStrategy = duplicateStrategy;
            
            InitialContext context = new InitialContext();
            this.itemValidating = (ItemValidating) context.lookup(ItemValidating.SERVICE_NAME);
            this.validationTransforming = (ValidationTransforming) context.lookup(ValidationTransforming.SERVICE_NAME);
            this.xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
            this.pubItemDepositing = (PubItemDepositing) context.lookup(PubItemDepositing.SERVICE_NAME);
        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_initialization_failed");
            log.addDetail(ErrorLevel.FATAL, e);
            fail();
        }
        log.finishItem();
    }

    /**
     * @param inputStream
     * @param format
     */
    private boolean validate(InputStream inputStream, Format format)
    {
        log.startItem("import_process_validate");
        if (inputStream == null)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_inputstream_unavailable");
            fail();
            return false;
        }
        else
        {
            log.addDetail(ErrorLevel.FINE, "import_process_inputstream_available");
        }
        
        if (format == null)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_unavailable");
            fail();
            return false;
        }
        else
        {
            log.addDetail(ErrorLevel.FINE, "import_process_format_available");
        }
        
        
        Format[] allSourceFormats = transformation.getSourceFormats(ESCIDOC_FORMAT);
        
        boolean found = false;
        for (Format sourceFormat : allSourceFormats)
        {
            if (format.matches(sourceFormat))
            {
                found = true;
                
                if (setProcessor(format))
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_format_valid");
                }
                else
                {
                    log.addDetail(ErrorLevel.FATAL, "import_process_format_not_supported");
                    fail();
                }
                break;
            }
        }
        if (!found)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_invalid");
            fail();
            return false;
        }
        log.finishItem();
        return true;
    }
    
    private boolean setProcessor(Format format)
    {
        try
        {
            if (format == null)
            {
                return false;
            }
            else if (ENDNOTE_FORMAT.matches(format))
            {
                this.formatProcessor = new EndnoteProcessor();
            }
            else if (BIBTEX_FORMAT.matches(format))
            {
                this.formatProcessor = new BibtexProcessor();
            }
            else
            {
                return false;
            }
        }
        catch (Exception e) {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_error");
            log.addDetail(ErrorLevel.FATAL, e);
            fail();
        }
        this.formatProcessor.setEncoding(format.getEncoding());
        return true;
    }

    private void fail()
    {
        this.failed = true;
        
        log.finishItem();
        log.startItem(ErrorLevel.FATAL, "import_process_failed");
        log.finishItem();
        
        if (this.rollback)
        {
            log.setStatus(Status.ROLLBACK);
            rollback();
        }
        
        log.close();
    }
    
    private void rollback()
    {
        log.startItem(ErrorLevel.FINE, "import_process_rollback");
        log.finishItem();
        DeleteProcess deleteProcess = new DeleteProcess(log);
        deleteProcess.start();
        log.startItem(ErrorLevel.FINE, "import_process_rollback_successful");
        log.finishItem();
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if (!failed)
        {
            log.startItem("import_process_import_item");
           
            this.formatProcessor.setSource(inputStream);
            
            int itemCount = 1;
            if (this.formatProcessor.hasNext())
            {
                itemCount = this.formatProcessor.getLength();
            }
            
            int counter = 0;
            
            while (this.formatProcessor.hasNext() && !failed)
            {
                try
                {
                    if (log.getCurrentItem() == null)
                    {
                        log.startItem("import_process_import_item");
                    }
    
                    String singleItem = this.formatProcessor.next();
                    
                    if (failed)
                    {
                        return;
                    }
                    
                    if (singleItem != null && !"".equals(singleItem.trim()))
                    {
                        prepareItem(singleItem);
                    }
                    counter++;
                    
                    log.setPercentage(30 * counter / itemCount + 10);
                }
                catch (Exception e)
                {
                    logger.error("Error during import", e);
                    
                    log.finishItem();
                    log.startItem(ErrorLevel.ERROR, "import_process_item_error");
                    log.addDetail(ErrorLevel.ERROR, e);
                    log.finishItem();
                    
                    if (this.rollback)
                    {
                        fail();
                    }
                }
            }
            
            if (failed)
            {
                return;
            }
            
            log.startItem("import_process_preparation_finished");
            log.addDetail(ErrorLevel.FINE, "import_process_no_more_items");
            log.finishItem();
            
            log.setPercentage(40);
            counter = 0;
            
            for (ImportLogItem item : log.getItems())
            {
                if (item.getStatus() == Status.SUSPENDED && item.getItemVO() != null && !failed)
                {
                    try
                    {
                        log.activateItem(item);
                        log.addDetail(ErrorLevel.FINE, "import_process_save_item");
                        
                        PubItemVO savedPubItem = pubItemDepositing.savePubItem(item.getItemVO(), user);
                        
                        String objid = savedPubItem.getVersion().getObjectId();
                        
                        log.setItemId(objid);
                        
                        log.addDetail(ErrorLevel.FINE, "import_process_item_imported");
                        
                        log.finishItem();
                        
                        counter++;
                        
                        log.setPercentage(55 * counter / itemCount + 40);
                    }
                    catch (Exception e)
                    {
                        logger.error("Error during import", e);
                        
                        log.finishItem();
                        log.startItem(ErrorLevel.ERROR, "import_process_item_error");
                        log.addDetail(ErrorLevel.ERROR, e);
                        log.finishItem();
                        
                        if (this.rollback)
                        {
                            fail();
                        }
                    }
                }
            }
            
            if (!failed)
            {
                log.startItem("import_process_finished");
                log.addDetail(ErrorLevel.FINE, "import_process_import_finished");
                log.finishItem();
                
                try
                {     
                    log.startItem("import_process_archive_log");
                    log.addDetail(ErrorLevel.FINE, "import_process_build_task_item");
                    
                    // Store log in repository
                    String taskItemXml = createTaskItemXml();
                    ItemHandler itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
                    String savedTaskItemXml = itemHandler.create(taskItemXml);
                    
                    log.setPercentage(100);
                    
                    logger.debug(savedTaskItemXml);
                    
                }
                catch (Exception e)
                {
                    logger.error("Error during import", e);
                    
                    log.finishItem();
                    log.startItem(ErrorLevel.ERROR, "import_process_error");
                    log.addDetail(ErrorLevel.ERROR, e);
                    fail();
                }
            }
        }
    }

    private String createTaskItemXml()
    {
        try
        {
            String taskItemXml = ResourceUtil.getResourceAsString("multipleImport/ImportTaskTemplate.xml");
            
            taskItemXml = taskItemXml.replaceAll("\\$1", escape(this.escidocContext.getObjectId()));
            taskItemXml = taskItemXml.replaceAll("\\$2", escape(PropertyReader.getProperty("escidoc.import.task.content-model")));
            taskItemXml = taskItemXml.replaceAll("\\$4", escape(this.name));
            taskItemXml = taskItemXml.replaceAll("\\$5", escape(this.fileName));
            taskItemXml = taskItemXml.replaceAll("\\$6", escape(this.formatProcessor.getDataAsBase64()));
            taskItemXml = taskItemXml.replaceAll("\\$7", escape(log.getStoredId() + ""));
            taskItemXml = taskItemXml.replaceAll("\\$8", escape(this.format.toString()));
            taskItemXml = taskItemXml.replaceAll("\\$9", escape(this.formatProcessor.getLength() + ""));
            
            log.finishItem();
            
            log.close();
    
            taskItemXml = taskItemXml.replaceAll("\\$3", log.toXML());

            return taskItemXml;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String escape(String string)
    {
        if (string == null)
        {
            return null;
        }
        else
        {
            return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
        }
    }

    /**
     * @param writer
     * @param singleItem
     * @return
     */
    private void prepareItem(String singleItem)
    {
        log.addDetail(ErrorLevel.FINE, "import_process_source_data_found");
        
        log.addDetail(ErrorLevel.FINE, "import_process_start_transformation");
        String esidocXml = null;
        try
        {
            esidocXml = new String(
                    transformation.transform(
                            singleItem.getBytes(this.format.getEncoding()),
                            this.format,
                            ESCIDOC_FORMAT,
                            "escidoc"),
                    ESCIDOC_FORMAT.getEncoding());
        
            log.addDetail(ErrorLevel.FINE, "import_process_transformation_done");
            
            PubItemVO pubItemVO = xmlTransforming.transformToPubItem(esidocXml);
            pubItemVO.setContext(escidocContext);
            pubItemVO.getLocalTags().add("multiple_import");
            pubItemVO.getLocalTags().add(log.getMessage() + " " + log.getStartDateFormatted());
            
            // Default validation
            log.addDetail(ErrorLevel.FINE, "import_process_default_validation");
            ValidationReportVO validationReportVO = this.itemValidating.validateItemObject(pubItemVO);
            
            if (validationReportVO.isValid())
            {
                if (!validationReportVO.hasItems())
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_default_validation_successful");
                }
                else
                {
                    log.addDetail(
                            ErrorLevel.WARNING,
                            "import_process_default_validation_successful_with_warnings");
                    for (ValidationReportItemVO item : validationReportVO.getItems())
                    {
                        log.addDetail(ErrorLevel.WARNING, item.getContent());
                    }
                }
                
                // Release validation
                log.addDetail(ErrorLevel.FINE, "import_process_release_validation");
                validationReportVO = this.itemValidating.validateItemObject(pubItemVO, "submit_item");

                if (validationReportVO.isValid())
                {
                    if (!validationReportVO.hasItems())
                    {
                        log.addDetail(ErrorLevel.FINE, "import_process_release_validation_successful");
                    }
                    else
                    {
                        log.addDetail(
                                ErrorLevel.WARNING,
                                "import_process_release_validation_successful_with_warnings");
                        for (ValidationReportItemVO item : validationReportVO.getItems())
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                    }
                
                }
                else
                {
                    log.addDetail(ErrorLevel.WARNING, "import_process_release_validation_failed");
                    for (ValidationReportItemVO item : validationReportVO.getItems())
                    {
                        if (item.isRestrictive())
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                        else
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                    }
                }
                
                log.addDetail(ErrorLevel.FINE, "import_process_generate_item");
                
                log.setItemVO(pubItemVO);
                
                if (this.duplicateStrategy != DuplicateStrategy.NO_CHECK)
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_check_duplicates");
                    boolean duplicatesDetected = checkDuplicates();
                    if (duplicatesDetected && this.duplicateStrategy == DuplicateStrategy.ROLLBACK)
                    {
                        this.rollback = true;
                        fail();
                    }
                    else if (duplicatesDetected)
                    {
                        log.addDetail(ErrorLevel.WARNING, "import_process_no_import");
                        log.finishItem();
                    }
                    else
                    {
                        log.suspendItem();
                    }
                        
                }
                else
                {
                    log.suspendItem();
                }

            }
            else
            {
                log.addDetail(ErrorLevel.PROBLEM, "import_process_default_validation_failed");
                for (ValidationReportItemVO item : validationReportVO.getItems())
                {
                    if (item.isRestrictive())
                    {
                        log.addDetail(ErrorLevel.PROBLEM, item.getContent());
                    }
                    else
                    {
                        log.addDetail(ErrorLevel.WARNING, item.getContent());
                    }
                }
                log.addDetail(ErrorLevel.PROBLEM, "import_process_item_not_imported");
                log.finishItem();
            }

        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.ERROR, e);
            log.addDetail(ErrorLevel.ERROR, "import_process_item_not_imported");
            log.finishItem();
        }
        
    }

    // TODO: Implementation
    private boolean checkDuplicates()
    {
        return false;
    }

}
