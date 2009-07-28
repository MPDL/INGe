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

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.ErrorLevel;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemPublishing;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SubmitProcess extends Thread
{
    private static final Logger logger = Logger.getLogger(SubmitProcess.class);
    
    private ImportLog log;
    private PubItemDepositing pubItemDepositing;
    private PubItemPublishing pubItemPublishing;
    private XmlTransforming xmlTransforming;
    private ItemHandler itemHandler;
    private ContextHandler contextHandler;
    private AccountUserVO user;
    private boolean alsoRelease;
    
    private Map<String, ContextVO> contexts = new HashMap<String, ContextVO>();
    
    public SubmitProcess(ImportLog log, boolean alsoRelease)
    {
        this.log = log;
        this.alsoRelease = alsoRelease;
        
        this.log.reopen();
        this.log.setPercentage(5);
        this.log.startItem("import_process_submit_items");
        this.log.addDetail(ErrorLevel.FINE, "import_process_initialize_submit_process");
        try
        {
            
            user = new AccountUserVO();
            user.setHandle(log.getUserHandle());
            user.setUserid(log.getUser());

            InitialContext context = new InitialContext();
            this.pubItemDepositing = (PubItemDepositing) context.lookup(PubItemDepositing.SERVICE_NAME);
            this.pubItemPublishing = (PubItemPublishing) context.lookup(PubItemPublishing.SERVICE_NAME);
            this.xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
            this.itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
            this.contextHandler = ServiceLocator.getContextHandler(this.user.getHandle());
        }
        catch (Exception e) {
            this.log.addDetail(ErrorLevel.FATAL, "import_process_initialize_submit_process_error");
            this.log.addDetail(ErrorLevel.FATAL, e);
            this.log.close();
            throw new RuntimeException(e);
        }
        this.log.finishItem();
        this.log.setPercentage(5);

    }
    
    
    public void run()
    {
        int itemCount = 0;
        for (ImportLogItem item : log.getItems())
        {
            if (item.getItemId() != null && !"".equals(item.getItemId()))
            {
                itemCount++;
                log.activateItem(item);
                log.addDetail(ErrorLevel.FINE, "import_process_schedule_submit");
                log.suspendItem();
            }
        }
        
        this.log.setPercentage(10);
        int counter = 0;

        for (ImportLogItem item : log.getItems())
        {
            if (item.getItemId() != null && !"".equals(item.getItemId()))
            {
                log.activateItem(item);

                try
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_retrieve_item");
                    
                    String itemXml = itemHandler.retrieve(item.getItemId());
                    PubItemVO itemVO = xmlTransforming.transformToPubItem(itemXml);
//                    ContextRO contextRO = itemVO.getContext();
//                    ContextVO contextVO;
//                    if (this.contexts.containsKey(contextRO.getObjectId()))
//                    {
//                        contextVO = this.contexts.get(contextRO.getObjectId());
//                    }
//                    else
//                    {
//                        log.addDetail(ErrorLevel.FINE, "import_process_retrieve_context");
//                        String contextXml = contextHandler.retrieve(contextRO.getObjectId());
//                        contextVO = xmlTransforming.transformToContext(contextXml);
//                        this.contexts.put(contextVO.getReference().getObjectId(), contextVO);
//                    }
//                    log.addDetail(ErrorLevel.FINE, "import_process_choose_workflow");
//                    Workflow workflow = contextVO.getAdminDescriptor().getWorkflow();
//                    
//                    log.addDetail(ErrorLevel.FINE, "Workflow is " + workflow.toString());
                    
                    if (this.alsoRelease)
                    {
                        log.addDetail(ErrorLevel.FINE, "import_process_submit_release_item");
                        pubItemDepositing.submitAndReleasePubItem(itemVO, "Batch submit/release from import " + log.getMessage(), user);
                        log.addDetail(ErrorLevel.FINE, "import_process_submit_release_successful");
                        
                    }
                    else
                    {
                        log.addDetail(ErrorLevel.FINE, "import_process_submit_item");
                        pubItemDepositing.submitPubItem(itemVO, "Batch submit from import " + log.getMessage(), user);
                        log.addDetail(ErrorLevel.FINE, "import_process_submit_successful");
                    }

                    log.finishItem();
                }
                catch (Exception e)
                {
                    log.addDetail(ErrorLevel.WARNING, "import_process_submit_failed");
                    log.addDetail(ErrorLevel.WARNING, e);
                    log.finishItem();
                }
                counter++;
                log.setPercentage(85 * counter / itemCount + 10);
            }
        }

        log.startItem("import_process_submit_finished");
        log.finishItem();
        log.close();
    }
}
