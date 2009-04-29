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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.purl.sword.base.Collection;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;
import org.purl.sword.base.ServiceLevel;
import org.purl.sword.base.Workspace;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.Source;
import org.w3.atom.Summary;
import org.w3.atom.Title;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.validation.ItemInvalidException;

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
        private String baseURL = "";


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
            Vector<Collection> collections = new Vector<Collection>();
//            
//            this.currentUser = util.checkUser(sdr);
//            
//            if (this.currentUser==null)
//            {
//                throw new SWORDAuthenticationException("Bad credentials");
//            }
            
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
         * @throws SWORDAuthenticationException
         * @throws SWORDException
         * @throws URISyntaxException 
         * @throws IOException 
         * @throws SWORDContentTypeException 
         */
        public DepositResponse doDeposit(Deposit deposit, String collection) throws SWORDAuthenticationException, SWORDException, IOException, URISyntaxException, SWORDContentTypeException 
        {
            SwordUtil util = new SwordUtil();
            PubItemVO depositItem = null;
            DepositResponse dr = new DepositResponse(Deposit.ACCEPTED);
            PubManDepositServlet depositServlet = new PubManDepositServlet();
            SWORDEntry se = new SWORDEntry();
            this.baseURL = PropertyReader.getProperty("escidoc.pubman.instance.url");

            try
            {
                //generated item
                depositItem = util.readZipFile(deposit.getFile(), this.currentUser);                
                //deposit item
                ContextRO context = new ContextRO();
                context.setObjectId(collection);
                depositItem.setContext(context);
                util.getItemControllerSessionBean().setCurrentPubItem(new PubItemVOPresentation (depositItem));
                if (!deposit.isNoOp())
                {
                    depositItem = util.doDeposit(this.currentUser, depositItem);
                }
                if (depositItem == null)
                {
                    throw new SWORDException("Creation of Publication Item failed.");
                }
                if (!deposit.isNoOp() && depositItem.getVersion().getState().equals(State.RELEASED))
                {
                     dr = new DepositResponse(Deposit.CREATED);
                }
                else
                {
                    dr = new DepositResponse(Deposit.ACCEPTED);
                }
            }
            catch (ContentStreamNotFoundException e)
            {
                this.log.error("No metadata File was found");
                depositServlet.setError("No metadata File was found.");
                dr.setHttpResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return dr;
            }
            catch (SWORDContentTypeException e)
            {
                this.log.error(e);
                depositServlet.setError("Unsupported File Format.");
                dr.setHttpResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return dr;
            }
            catch (ItemInvalidException e)
            {
                this.log.error(e);
                depositServlet.setError("Invalis Item: " + e.getMessage());
                dr.setHttpResponse(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return dr;
            }
            catch (Exception e)
            {
                this.log.error(e);
                dr.setHttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return dr;
            }


            Title title = new Title();
            title.setContent(depositItem.getMetadata().getTitle().getValue());
            se.setTitle(title);
             
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone (utc);
            String milliFormat = sdf.format(new Date());
            se.setUpdated(milliFormat);
                
            Summary s = new Summary();
            Vector <String> filenames = util.getFileNames();
            String filename = "";
            for (int i = 0; i< filenames.size(); i++)
            {
                if (filename.equals(""))
                {
                    filename = filenames.get(i);    
                }
                else
                {
                    filename = filename + " ," + filenames.get(i);    
                }                           
            }   
            s.setContent(filename);
            se.setSummary(s);
            
            //Add the author names
            for (int i=0; i< depositItem.getMetadata().getCreators().size(); i++)
            {
                Author author = new Author();
                if (depositItem.getMetadata().getCreators().get(i).getPerson().getCompleteName() != null)
                {
                    author.setName(depositItem.getMetadata().getCreators().get(i).getPerson().getCompleteName());
                }
                else
                {
                    String name = depositItem.getMetadata().getCreators().get(i).getPerson().getGivenName() + ", " +
                        depositItem.getMetadata().getCreators().get(i).getPerson().getFamilyName();
                    author.setName(name);
                }
                se.addAuthors(author);     
            }           
            
            Source source = new Source();
            Generator generator = new Generator();
            generator.setContent(this.baseURL);
            source.setGenerator(generator);
            se.setSource(source);

            //Only set content if item was really created
            if (! deposit.isNoOp())
            {
                Content content = new Content();
                content.setSource(this.baseURL + "/pubman/item/" + depositItem.getVersion().getObjectId());
                se.setContent(content);
            }

            se.setTreatment("Zip archives recognised as content packages are opened and the individual files contained in them are stored. All other files are stored as is.");
            
//            if (deposit.isVerbose()) 
//            {
//                se.setVerboseDescription("I've done a lot of hard work to get this far!");
//            }
            
            se.setNoOp(deposit.isNoOp());            
            se.setFormatNamespace("http://www.loc.gov/METS/");      
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
    
}
