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

package de.mpg.escidoc.pubman.sword;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.purl.sword.base.Collection;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;
import org.purl.sword.base.ServiceLevel;
import org.purl.sword.base.Workspace;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.validation.ItemInvalidException;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Main class to provide SWORD Server functionality.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PubManSwordServer
{
        private Logger log = Logger.getLogger(PubManSwordServer.class);
        private AccountUserVO currentUser;
        private String verbose = "";


        /**
         * Provides Service Document.
         * @param ServiceDocumentRequest
         * @return ServiceDocument
         * @throws SWORDAuthenticationException 
         * @throws URISyntaxException 
         * @throws IOException 
         */
        public ServiceDocument doServiceDocument(ServiceDocumentRequest sdr) throws SWORDAuthenticationException
        {
            SwordUtil util = new SwordUtil();
            Vector <Collection> collections = new Vector <Collection>();

            //Get collections due to logged in user
            collections = util.getDepositCollection(this.currentUser);

            // Create and return the PubMan ServiceDocument
            ServiceDocument document = new ServiceDocument();
            Service service = new Service(ServiceLevel.ZERO, true, false);
            document.setService(service);

            Workspace workspace = new Workspace();
            workspace.setTitle("PubMan SWORD Workspace");

            //Add all collections to workspace
            for (int i = 0; i < collections.size(); i++)
            {
                workspace.addCollection(collections.get(i));
            }

            service.addWorkspace(workspace);

            return document;
        }

        
        /**
         * Process the deposit.
         * @param deposit
         * @param collection
         * @return DepositResponse
         * @throws Exception 
         * @throws ItemInvalidException 
         * @throws PubItemStatusInvalidException 
         */
        public DepositResponse doDeposit(Deposit deposit, String collection) throws PubItemStatusInvalidException, ItemInvalidException, ContentStreamNotFoundException, Exception 
        {
            SwordUtil util = new SwordUtil();
            PubItemVO depositItem = null;
            DepositResponse dr = new DepositResponse(Deposit.ACCEPTED);
            boolean valid = false;

            this.setVerbose("Start depositing process ... ");
            
                //Create item
                depositItem = util.readZipFile(deposit.getFile(), this.currentUser); 
                this.setVerbose("Escidoc Publication Item successfully created.");
                ContextRO context = new ContextRO();
                context.setObjectId(collection);
                depositItem.setContext(context);

                //Validate Item
                util.getItemControllerSessionBean().setCurrentPubItem(new PubItemVOPresentation (depositItem));
                ValidationReportVO validationReport = util.validateItem(depositItem);
                if (validationReport.isValid())
                {
                    this.setVerbose("Escidoc Publication Item successfully validated.");
                    valid = true;
                }
                else
                {
                    this.setVerbose("Following validation error(s) occurred: " + validationReport);
                    valid = false;
                    throw new ItemInvalidException(validationReport);
                }
                
                //Deposit item                             
                if (!deposit.isNoOp() && valid)
                {
                    depositItem = util.doDeposit(this.currentUser, depositItem);   
                    if (depositItem.getVersion().getState().equals(State.RELEASED))
                    {
                         dr = new DepositResponse(Deposit.CREATED);
                         this.setVerbose("Escidoc Publication Item successfully deposited (state: "+ depositItem.getPublicStatus() +").");
                    }
                    else
                    {
                        dr = new DepositResponse(Deposit.ACCEPTED);
                        this.setVerbose("Escidoc Publication Item successfully deposited (state: "+ depositItem.getPublicStatus() +").");
                    }
                }
                else 
                {
                    if (valid)
                    {
                        this.setVerbose("Escidoc Publication Item not deposited due to X_NO_OP=true.");
                    }
                    else
                    {
                        this.setVerbose("Escidoc Publication Item not deposited due to validation errors.");
                    }
                }

            SWORDEntry se = util.createResponseAtom(depositItem, deposit, valid);
            if (deposit.isVerbose())
            {
                se.setVerboseDescription(this.getVerbose());
            }
            dr.setEntry(se);
            return dr;
        }

        
        public AccountUserVO getCurrentUser()
        {
            return this.currentUser;
        }

        public void setCurrentUser(AccountUserVO currentUser)
        {
            this.currentUser = currentUser;
        }

        public String getVerbose()
        {
            return this.verbose;
        }

        public void setVerbose(String verbose)
        {
            this.verbose += verbose + "\n";
        }

        public String getBaseURL()
        {
            try
            {
                return PropertyReader.getProperty("escidoc.pubman.instance.url");
            }
            catch (Exception e)
            {
                this.log.warn("Base URL could not be read from property file.", e);
            }
            return "";
        }   
}
