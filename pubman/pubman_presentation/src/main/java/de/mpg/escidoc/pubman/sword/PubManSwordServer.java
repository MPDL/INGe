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

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.ServiceDocumentRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
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
         * Process the deposit.
         * @param deposit
         * @param collection
         * @return DepositResponse
         * @throws Exception
         * @throws ItemInvalidException
         * @throws PubItemStatusInvalidException
         */
        public DepositResponse doDeposit(Deposit deposit, String collection)
            throws PubItemStatusInvalidException, ItemInvalidException,
            ContentStreamNotFoundException, Exception
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
                         this.setVerbose("Escidoc Publication Item successfully deposited " +
                         		"(state: "+ depositItem.getPublicStatus() +").");
                    }
                    else
                    {
                        dr = new DepositResponse(Deposit.ACCEPTED);
                        this.setVerbose("Escidoc Publication Item successfully deposited " +
                        		"(state: "+ depositItem.getPublicStatus() +").");
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

        /**
         * Provides Service Document.
         * @param ServiceDocumentRequest
         * @return ServiceDocument
         * @throws SWORDAuthenticationException
         * @throws ParserConfigurationException
         * @throws TransformerException
         * @throws URISyntaxException
         * @throws IOException
         */
        public String doServiceDocument(ServiceDocumentRequest sdr)
            throws SWORDAuthenticationException, ParserConfigurationException,
                   TransformerException
        {
            SwordUtil util = new SwordUtil();
            List < PubContextVOPresentation > contextList = null;
            ContextListSessionBean contextListBean = new ContextListSessionBean();
            contextList = contextListBean.getDepositorContextList();
            DocumentBuilder documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

            // Create and return the PubMan ServiceDocument
            Document document = documentBuilder.newDocument();
            Element service = document.createElementNS("http://www.w3.org/2007/app", "service");
            Element version = document.createElementNS("http://purl.org/net/sword/", "version");
            version.setPrefix("sword");
            version.setTextContent("1.3");
            Element workspace = document.createElement("workspace");
            Element wsTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
            wsTitle.setPrefix("atom");
            wsTitle.setTextContent("PubMan SWORD Workspace");
            workspace.appendChild(wsTitle);

            //Add all collections to workspace
            for (int i = 0; i < contextList.size(); i++)
            {
                    PubContextVOPresentation pubContext = contextList.get(i);

                    Element collection = document.createElement("collection");
                    collection.setAttribute("href", pubContext.getReference().getObjectId());
                    Element colTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
                    colTitle.setPrefix("atom");
                    colTitle.setTextContent(pubContext.getName());
                    Element abst = document.createElementNS("http://purl.org/dc/terms/", "abstract");
                    abst.setPrefix("dcterms");
                    abst.setTextContent(pubContext.getDescription());
                    Element med = document.createElementNS("http://purl.org/net/sword/", "mediation");
                    med.setPrefix("sword");
                    med.setTextContent("false");
                    Element policy = document.createElementNS("http://purl.org/net/sword/", "collectionPolicy");
                    policy.setPrefix("sword");
                    policy.setTextContent(util.getWorkflowAsString(pubContext));
                    //static value
                    Element treat = document.createElementNS("http://purl.org/net/sword/", "treatment");
                    treat.setPrefix("sword");
                    treat.setTextContent(util.getTreatmentText());
                    //static value
                    Element format1 = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
                    format1.setPrefix("sword");
                    format1.setTextContent("http://www.tei-c.org/ns/1.0");
                    Element format2 = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
                    format2.setPrefix("sword");
                    format2.setTextContent("http://purl.org/escidoc/metadata/schemas/0.1/publication");
                    Element format3 = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
                    format3.setPrefix("sword");
                    format3.setTextContent("bibTex");
                    Element format4 = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
                    format4.setPrefix("sword");
                    format4.setTextContent("EndNote");

                    collection.appendChild(colTitle);
                    collection.appendChild(abst);
                    collection.appendChild(med);
                    collection.appendChild(policy);
                    collection.appendChild(treat);
                    collection.appendChild(format1);
                    collection.appendChild(format2);
                    collection.appendChild(format3);
                    collection.appendChild(format4);

                    workspace.appendChild(collection);
            }

            service.appendChild(version);
            service.appendChild(workspace);

            document.appendChild(service);

            //Transform to xml
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();

            return xmlString;
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
