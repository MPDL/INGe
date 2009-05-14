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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.purl.sword.base.Collection;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.ServiceDocumentRequest;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.Source;
import org.w3.atom.Summary;
import org.w3.atom.Title;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.depositing.DepositingException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.exceptions.PubManException;
import de.mpg.escidoc.services.validation.ItemInvalidException;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * This class provides helper method for the SWORD Server implementation.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SwordUtil extends FacesBean
{

    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "SwordUtil";

    public static String LOGIN_URL = "/aa/login";
    public static String LOGOUT_URL = "/clear.jsp";
    private static final int NUMBER_OF_URL_TOKENS = 2;

    private Logger logger = Logger.getLogger(SwordUtil.class);
    private PubManDepositServlet depositServlet;

    private Vector<String> filenames = new Vector<String>();
    private String format = "";
    
    //Constants
    private final String acceptedFormat = "application/zip";
    private final String mdFormatTEI = ".tei";
    private final String mdFormatEscidoc = ".xml";
    private final String itemPath = "/pubman/item/";
    private final String serviceDocUrl = "faces/sword/servicedocument";
    private final String treatmentText = "Zip archives recognised as content packages are opened and the individual files contained in them are stored.";

    /**
     * Public constructor
     */
    public SwordUtil()
    {
        this.init();
    }

    public void init()
    {
        this.depositServlet = new PubManDepositServlet();
        super.init();
    }

    /**
     * Logs in a user.
     * @return AccountUserVO
     */
    public AccountUserVO checkUser (ServiceDocumentRequest sdr)
    {
        AccountUserVO userVO = null;
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        String username;
        String pwd;

        //Forward http authentification
        if (sdr.getUsername() != null && sdr.getPassword() != null)
        {
            username = sdr.getUsername();
            pwd = sdr.getPassword();
            try
            {
                String handle = this.loginUser(username, pwd);
                loginHelper.setESciDocUserHandle(handle);
                userVO = loginHelper.getAccountUser();                
            }
            catch (Exception e)
            {
                this.logger.error(e);
                return null;
            }
        }
        return userVO;
    }
    
    /**
     * Retrieves all collections a user may deposit to.
     * @param user
     * @return Vector with all collections 
     */
    public Vector <Collection> getDepositCollection (AccountUserVO user)
    {
        Vector <Collection> allCol = new Vector<Collection>();
        List <PubContextVOPresentation> contextList = null;
        ContextListSessionBean contextListBean = new ContextListSessionBean();
        contextList = contextListBean.getDepositorContextList();

        for (int i = 0; i < contextList.size(); i++)
        {
            PubContextVOPresentation pubContext = contextList.get(i);
            //Create collection object for all PubContextVOPresentation objects
            Collection col = new Collection();
            col.setTitle(pubContext.getName());
            col.setAbstract(pubContext.getDescription());
            //standard value for start.
            col.setMediation(false);
            col.setCollectionPolicy(this.getWorkflowAsString(pubContext));
            //Collection identifier
            col.setLocation(pubContext.getReference().getObjectId());
            //static value
            col.setTreatment(this.treatmentText);
            //static value
            col.setFormatNamespace("http://www.loc.gov/METS/");
            //static value
            col.addAccepts(this.acceptedFormat);

            allCol.add(col);
        }
        return allCol;
    }

    /**
     * Checks if a user has depositing rights for a collection.
     * @param collection
     * @param user
     * @return true if the user has depositing rights, else false
     */
    public boolean checkCollection (String collection, AccountUserVO user)
    {
        List <PubContextVOPresentation> contextList = null;
        ContextListSessionBean contextListBean = new ContextListSessionBean();
        contextList = contextListBean.getDepositorContextList();
        for (int i =0; i < contextList.size(); i++)
        {
            String context = contextList.get(i).getReference().getObjectId();
            if (context.toLowerCase().equals(collection.toLowerCase().trim()))
            {
                return true;
            }
        }      
        return false;
    }

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    public String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//" );
        if (tokens.countTokens() != NUMBER_OF_URL_TOKENS ) 
        {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

        if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS ) 
        {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt(hostPort.nextToken() );
        
        HttpClient client = new HttpClient();

        client.getHostConfiguration().setHost(host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        PostMethod login = new PostMethod (frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);

        client.executeMethod(login);

        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, 
                client.getState().getCookies());

        Cookie sessionCookie = logoncookies[0];

        PostMethod postMethod = new PostMethod(LOGIN_URL);
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);

        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }

        String userHandle = null;
        Header[] headers = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }
    
    /**
     * @param fc
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public void logoutUser() throws IOException, ServiceException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();

        fc.getExternalContext().redirect(
                ServiceLocator.getFrameworkUrl() + LOGOUT_URL + "?target="
                + URLEncoder.encode(PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + "?logout=true", "UTF-8"));   
    }
    
    /**
     * Creates a Account User.
     * @param user
     * @param pwd
     * @return AccountUserVO
     */
    public AccountUserVO getAccountUser (String user, String pwd)
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        String handle = "";
        
        try
        {
            handle = this.loginUser(user, pwd);
            loginHelper.fetchAccountUser(handle);
        }
        catch (Exception e)
        {
            return null;
        }
        return loginHelper.getAccountUser();
    }

    /**
     * This method takes a zip file and reads out the entries.
     * @param in
     * @throws TechnicalException 
     * @throws NamingException 
     * @throws SWORDContentTypeException 
     */
    public PubItemVO readZipFile(InputStream in, AccountUserVO user) throws SWORDContentTypeException, ContentStreamNotFoundException, Exception
    {
        String item = null;
        Vector <byte[]> attachements = new Vector<byte[]>();
        Vector <String> attachementsNames = new Vector<String>();
        PubItemVO pubItem = null;
        int size = 0;
        final int bufLength = 1024;
        byte[] buffer = new byte[ bufLength ];
        int readReturn;
        this.filenames.clear();
        int count = 0;

        try
        {
            ZipEntry zipentry;
            ZipInputStream zipinputstream = new ZipInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            while ((zipentry = zipinputstream.getNextEntry()) != null) 
            { 
                count++;
                this.logger.debug("Processing zip entry file: " + zipentry.getName());
                baos = new ByteArrayOutputStream();
                while ((readReturn = zipinputstream.read( buffer ))!= -1)
                {
                   baos.write(buffer, 0, readReturn );
                } 

                this.filenames.add(zipentry.getName());
                //Retrieve the metadata
                if (zipentry.getName().toLowerCase().endsWith(this.mdFormatEscidoc))
                {
                    size = (int) zipentry.getSize();
                    item = new String(baos.toByteArray(), 0, size, "UTF8");
                    this.logger.debug("Provided Metadata:" + item);
                    this.format=this.mdFormatEscidoc;
                }
                if (zipentry.getName().toLowerCase().endsWith(this.mdFormatTEI))
                {
                    size = (int) zipentry.getSize();
                    item = new String(baos.toByteArray(), 0, size, "UTF8");
                    this.logger.debug("Provided Metadata:" + item);
                    this.format=this.mdFormatTEI;
                }
                else
                {
                    attachements.add(baos.toByteArray());
                    attachementsNames.add(zipentry.getName());
                    }               
                zipinputstream.closeEntry();
            }
            zipinputstream.close();            

        }
        catch (Exception e)
        {
            //TODO exception handling
            e.printStackTrace();
            }        
        if (count == 0)
        {
            this.logger.info("No zip file was provided.");
            this.depositServlet.setError("No zip file was provided.");
            throw new SWORDContentTypeException();
        }
        pubItem = this.processFiles(item, attachements, attachementsNames, user);

        return pubItem;
    }

    /**
     * 
     * @param item
     * @param files
     * @return
     * @throws NamingException
     * @throws TechnicalException
     * @throws SWORDContentTypeException 
     */
    private PubItemVO processFiles(String item, Vector<byte[]> files, Vector<String> names, AccountUserVO user) throws SWORDContentTypeException, ContentStreamNotFoundException, Exception
    {
        PubItemVO itemVO = null;

        if (item == null)
        {
            throw new ContentStreamNotFoundException();
        } 

        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
            
            if (this.format.equals(this.mdFormatEscidoc))
            {
              //Transform to escidoc-publication-item
              if (this.format.equals(this.mdFormatTEI))
              {
                  //Start tei transformation
              }
            }
            
            //Create item
            itemVO = xmlTransforming.transformToPubItem(item);
            this.logger.debug("Item successfully created.");
        }
        catch (Exception e)
        {
           this.logger.error("Transformation to PubItem failed.", e);
           throw new SWORDContentTypeException();
        }       
        
        //Attach files to the item
        for (int i = 0; i< files.size(); i++)
        {
            byte[] file = files.get(i);   
            String name = names.get(i);
            FileVO fileVO = this.convertToFile(file, name, user);
            itemVO.getFiles().add(fileVO);
        }
        
        return itemVO;
    }
    
    /**
     * 
     * @param user
     * @param item
     * @return Saved pubItem
     * @throws NamingException 
     * @throws AuthorizationException
     * @throws SecurityException
     * @throws TechnicalException
     * @throws URISyntaxException
     * @throws NamingException 
     * @throws ItemInvalidException 
     * @throws PubManException 
     * @throws DepositingException 
     */
    public PubItemVO doDeposit (AccountUserVO user, PubItemVO item) throws ItemInvalidException, PubItemStatusInvalidException, Exception
    {
        PubItemVO depositedItem = null;
        InitialContext initialContext = new InitialContext();
        PubItemDepositing depositBean = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
        String method = this.getMethod(item);

        if (method == null)
        {
            throw new PubItemStatusInvalidException(null, null);
        }
        if (method.equals("SAVE"))
        {
            depositedItem = depositBean.savePubItem(item, user);
        }
        if (method.equals("SAVE_SUBMIT"))
        {
            depositedItem = depositBean.savePubItem(item, user);
            depositedItem = depositBean.submitPubItem(depositedItem, "", user);
        }
        if (method.equals("RELEASE"))
        {
            depositedItem = depositBean.savePubItem(item, user);
            depositedItem = depositBean.submitAndReleasePubItem(depositedItem, "", user);
        }

        return depositedItem;
    }

    /**
     * Returns the Workflow of the current context.
     */
    public PublicationAdminDescriptorVO.Workflow getWorkflow()
    {

        if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD))
        {
            return Workflow.STANDARD;
        }
        if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE))
        {
            return Workflow.SIMPLE;
        }
        return null;
    }

    public String getMethod (PubItemVO item)
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

        boolean isWorkflowStandard = false;
        boolean isWorkflowSimple = true;

        boolean isStatePending = true;
        boolean isStateSubmitted = false;
        boolean isStateReleased = false;
        boolean isStateInRevision = false;

        if (item != null && item.getVersion() != null && item.getVersion().getState() != null)
        {
            isStatePending = item.getVersion().getState().equals(PubItemVO.State.PENDING);
            isStateSubmitted = item.getVersion().getState().equals(PubItemVO.State.SUBMITTED);
            isStateReleased = item.getVersion().getState().equals(PubItemVO.State.RELEASED);
            isStateInRevision = item.getVersion().getState().equals(PubItemVO.State.IN_REVISION);
        }
        
        isWorkflowStandard = getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD;
        isWorkflowSimple = getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE;

        boolean isModerator = loginHelper.getAccountUser().isModerator(item.getContext());
        boolean isOwner = true;
        if (item.getOwner() != null)
        {
            isOwner = (loginHelper.getAccountUser().getReference() != null ? loginHelper.getAccountUser().getReference().getObjectId().equals(item.getOwner().getObjectId()) : false);
        }

        if ((isStatePending || isStateSubmitted) && isWorkflowSimple && isOwner)
        {
            return "RELEASE";
        }
        if ((isStatePending || isStateInRevision) &&  isWorkflowStandard && isOwner)
        {
            return "SAVE_SUBMIT";
        }
        if (((isStatePending || isStateInRevision) && isOwner) || (isStateSubmitted && isModerator))
        {
            return "SUBMIT";
        }
        return null;
    }

    /**
     * Returns the Workflow for a given context.
     * @param pubContext
     * @return workflow type as string
     */
    private String getWorkflowAsString(PubContextVOPresentation pubContext)
    {
        boolean isWorkflowStandard = pubContext.getAdminDescriptor().getWorkflow() == 
            PublicationAdminDescriptorVO.Workflow.STANDARD;
        boolean isWorkflowSimple = pubContext.getAdminDescriptor().getWorkflow() == 
            PublicationAdminDescriptorVO.Workflow.SIMPLE;


        if (isWorkflowStandard)
        {
            return "Standard Workflow";
        }
        if (isWorkflowSimple)
        {
            return "Simple Workflow";
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (de.mpg.escidoc.pubman.ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    public Vector<String> getFileNames ()
    {
        return this.filenames;
    }

    /**
     * Converts a byte[] into a FileVO.
     * @param file
     * @param name
     * @param user
     * @return FileVO
     * @throws Exception
     */
    private FileVO convertToFile (byte[] file, String name, AccountUserVO user) throws Exception
    {
        FileVO fileVO = new FileVO();

        ByteArrayInputStream in = new ByteArrayInputStream(file);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(name);

        URL fileURL = this.uploadFile(in, mimeType, user.getHandle());    
        if (fileURL != null && !fileURL.toString().trim().equals(""))
        {                           
            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            fileVO.setVisibility(FileVO.Visibility.PUBLIC);
            fileVO.setDefaultMetadata(new MdsFileVO());
            fileVO.getDefaultMetadata().setTitle(new TextVO(name));
            fileVO.setMimeType(mimeType);
            fileVO.setName(name);

            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(mimeType);

            fileVO.getDefaultMetadata().getFormats().add(formatVO);
            fileVO.setContent(fileURL.toString());
            fileVO.getDefaultMetadata().setSize(file.length);
            fileVO.setContentCategory(PubFileVOPresentation.ContentCategory.ANY_FULLTEXT.toString());
        }

        return fileVO;
    }
    
    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in));
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

    public SWORDEntry createResponseAtom (PubItemVO item, Deposit deposit)
    {
        SWORDEntry se = new SWORDEntry();
        PubManSwordServer server = new PubManSwordServer();

        //This info can only be filled if item was successfully created
        if (item != null)
        {
            Title title = new Title();
            title.setContent(item.getMetadata().getTitle().getValue());
            se.setTitle(title);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(utc);
            String milliFormat = sdf.format(new Date());
            se.setUpdated(milliFormat);          

            //Add the author names
            for (int i=0; i< item.getMetadata().getCreators().size(); i++)
            {
                Author author = new Author();
                if (item.getMetadata().getCreators().get(i).getPerson().getCompleteName() != null && 
                        !item.getMetadata().getCreators().get(i).getPerson().getCompleteName().equals(""))
                {
                    author.setName(item.getMetadata().getCreators().get(i).getPerson().getCompleteName());
                }
                else
                {
                    String name = item.getMetadata().getCreators().get(i).getPerson().getGivenName() + ", " +
                    item.getMetadata().getCreators().get(i).getPerson().getFamilyName();
                    author.setName(name);
                }
                se.addAuthors(author);     
            }   
        }

        Summary s = new Summary();
        Vector <String> filenames = this.getFileNames();
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

        //Only set content if item was really created
        if (! deposit.isNoOp() && item != null)
        {
            Content content = new Content();
            content.setSource(server.getBaseURL() + this.itemPath + item.getVersion().getObjectId());
            se.setContent(content);
        }
        
        Source source = new Source();
        Generator generator = new Generator();
        generator.setContent(server.getBaseURL());
        source.setGenerator(generator);
        se.setSource(source);

        se.setTreatment(this.treatmentText);           
        se.setNoOp(deposit.isNoOp());            
        se.setFormatNamespace("http://www.loc.gov/METS/");   

        return se;
    }

    public String validateItem(PubItemVO item) throws NamingException
    {
        InitialContext initialContext = new InitialContext();
        ItemValidating itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
        String error = "";

        ValidationReportItemVO itemReport = null;
        ValidationReportVO report = new ValidationReportVO();

        try
        {
            report = itemValidating.validateItemObject(item);
            if (!report.isValid())
            {
                for (int i = 0; i < report.getItems().size(); i++)
                {
                    itemReport = report.getItems().get(i);                   
                    if (itemReport.isRestrictive())
                    {
                        error +=  itemReport.getContent() + "\n";
                    }
                }
            }
            else 
            {
                error = null;
            }
        }
        catch (Exception e)
        {
            this.logger.error("Validation error", e);
        }
        return error;
    }
}
