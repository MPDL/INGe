package de.mpg.escidoc.pubman.sword;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.naming.NamingException;

import org.purl.sword.base.Collection;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;
import org.purl.sword.base.ServiceLevel;
import org.purl.sword.base.Workspace;
import org.purl.sword.server.SWORDServer;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.InvalidMediaTypeException;
import org.w3.atom.Source;
import org.w3.atom.Title;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

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

        /** A counter to count submissions, so the response to a deposito can increment */
        private static int counter = 0;
        private AccountUserVO currentUser;


        /**
         * Provides Service Document
         * @throws SWORDAuthenticationException 
         */
        public ServiceDocument doServiceDocument(ServiceDocumentRequest sdr) throws SWORDAuthenticationException 
        {
            SwordUtil util = new SwordUtil();
            Vector<Collection> collections = new Vector<Collection>();
            
            this.currentUser = util.checkUser(sdr);
            
            if (this.currentUser==null)
            {
                throw new SWORDAuthenticationException("Bad credentials");
            }
            
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
         * 
         */
        public DepositResponse doDeposit(Deposit deposit) throws SWORDAuthenticationException, SWORDException 
        {
            SwordUtil util = new SwordUtil();
            PubItemVO depositItem = null;

            try
            {
                //generated item
                depositItem = util.readZipFile(deposit.getFile());
                //deposited
                depositItem = util.doDeposit(this.currentUser, depositItem);
            }
            catch (NamingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (TechnicalException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // Handle the deposit
            if (!deposit.isNoOp()) 
            {
                counter++;
            }
            DepositResponse dr = new DepositResponse(Deposit.ACCEPTED);
            SWORDEntry se = new SWORDEntry();
            
            Title title = new Title();
            title.setContent(depositItem.getMetadata().getTitle().getValue());
            se.setTitle(title);
            
            //TODO: not sure what category is
            se.addCategory("Category");
            
            se.setId(depositItem.getPid());
            
             
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone (utc);
            String milliFormat = sdf.format(new Date());
            se.setUpdated(milliFormat);
                
//            Summary s = new Summary();
//            s.setContent(filenames.toString());
//            se.setSummary(s);
            
            Author author = new Author();
            //TODO
            author.setName(depositItem.getMetadata().getCreators().get(0).getPerson().getCompleteName());
            se.addAuthors(author);     
            
            Source source = new Source();
            Generator generator = new Generator();
            //TODO
            generator.setContent("PubMan");
            source.setGenerator(generator);
            se.setSource(source);
            
            Content content = new Content();
            try 
            {
                content.setType("application/zip");
            } 
            catch (InvalidMediaTypeException e1) 
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            content.setSource("TODO: Source");
            se.setContent(content);
            
            se.setTreatment("TODO: Treatment");
            
//            if (deposit.isVerbose()) 
//            {
//                se.setVerboseDescription("I've done a lot of hard work to get this far!");
//            }
            
            se.setNoOp(deposit.isNoOp());
            
            se.setFormatNamespace("TODO: FormatNamespace");
            
            //If a user has multiple contexts to deposit how do i know which???
            
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
