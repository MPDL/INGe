package de.mpg.escidoc.pubman.sword;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;
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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.purl.sword.base.Collection;
import org.purl.sword.base.ServiceDocumentRequest;

import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.depositing.PubItemLockedException;
import de.mpg.escidoc.services.pubman.depositing.PubItemMandatoryAttributesMissingException;
import de.mpg.escidoc.services.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;

public class SwordUtil extends FacesBean
{

    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "SwordUtil";
    
    public static String LOGIN_URL = "/aa/login";
    private static final int NUMBER_OF_URL_TOKENS = 2;
    
    private static Logger logger = Logger.getLogger(SwordUtil.class);
    
    public SwordUtil()
    {
        this.init();
    }
    
    public void init()
    {
        super.init();
    }
    
    /**
     * 
     * @return
     */
    public AccountUserVO checkUser (ServiceDocumentRequest sdr)
    {
        AccountUserVO userVO = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        String username;
        String pwd;
        
        //Forward http authentification
        if (sdr.getUsername()!= null && sdr.getPassword() != null)
        {
            username = sdr.getUsername();
            pwd = sdr.getPassword();
            System.out.println("HTTP AUTH: " + username);
            try
            {
                String handle = this.loginUser(username, pwd);
                loginHelper.setESciDocUserHandle(handle);
                userVO = loginHelper.getAccountUser();
                
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //Get user from escidoc Login
        else
        {           
            try
            {
                if (loginHelper.getESciDocUserHandle() == null || loginHelper.getESciDocUserHandle().equals(""))
                {
                    fc.getExternalContext().redirect(getLoginUrl());
                }
                userVO = loginHelper.getAccountUser();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return userVO;
    }
    
    /**
     * Returns the Framework URL to LogIn
     * @return FrameworkUrl as String
     * @throws IOException
     * @throws URISyntaxException
     * @throws ServiceException
     */
    private String getLoginUrl() throws IOException, URISyntaxException, ServiceException
    {
        
        String pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
        if(!pubmanUrl.endsWith("/")) pubmanUrl = pubmanUrl + "/";
        
        String url =  ServiceLocator.getFrameworkUrl() + LOGIN_URL + "?target=" + pubmanUrl + "faces/sword/servicedocument";
        return url;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    public Vector<Collection> getDepositCollection (AccountUserVO user)
    {
        Vector<Collection> allCol = new Vector<Collection>();
        List<PubContextVOPresentation> contextList = null;
        ContextListSessionBean contextListBean = new ContextListSessionBean();
        contextList = contextListBean.getDepositorContextList();
        
        for (int i = 0; i< contextList.size(); i++)
        {
            PubContextVOPresentation pubContext = contextList.get(i);
            //Create collection object for all PubContextVOPresentation objects
            Collection col = new Collection();
            col.setTitle(pubContext.getName());
            col.setAbstract(pubContext.getDescription());
            //standard value for start.
            col.setMediation(false);
            //TODO: get workflow type
            col.setCollectionPolicy("todo");
            //TODO: get location link
            col.setLocation("todo");
            //static value
            col.setTreatment("Zip archives recognised as content packages are opened and the individual files contained in them are stored. All other files are stored as is.");
            //static value
            col.setFormatNamespace("http://www.loc.gov/METS/");
            //static value
            col.addAccepts("application/zip");
            
            allCol.add(col);
        }
        return allCol;
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
        StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
        if( tokens.countTokens() != NUMBER_OF_URL_TOKENS ) {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        
        if( hostPort.countTokens() != NUMBER_OF_URL_TOKENS ) {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt( hostPort.nextToken() );
        
        HttpClient client = new HttpClient();

        client.getHostConfiguration().setHost( host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        
        client.executeMethod(login);
                
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(
                host, port, "/", false, 
                client.getState().getCookies());
        
        Cookie sessionCookie = logoncookies[0];
        
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
      
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                //System.out.println("location: "+location);
                //System.out.println("handle: "+userHandle);
            }
        }
        
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }
    
    /**
     * 
     * @param user
     * @param pwd
     * @return
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return loginHelper.getAccountUser();
    }
    
    /**
     * This method takes a zip file and reads out the entries.
     * @param in
     * @throws TechnicalException 
     * @throws NamingException 
     */
    public PubItemVO readZipFile(InputStream in) throws NamingException, TechnicalException
    {
        String item = null;
        Vector<byte[]> attachements = new Vector<byte[]>();
        int size = 0;
        final int bufLength = 1024;
        byte[] buffer = new byte[ bufLength ];
        int readReturn;
        
        try
        {
            ZipEntry zipentry;
            ZipInputStream zipinputstream = new ZipInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
          
            while ((zipentry = zipinputstream.getNextEntry()) != null) 
            { 
                logger.debug("Processing zip entry file: " + zipentry.getName());
                baos = new ByteArrayOutputStream();
                while((readReturn = zipinputstream.read( buffer ))!= -1)
                {
                   baos.write( buffer, 0, readReturn );
                } 

                //Retrieve the metadata
                if (zipentry.getName().toLowerCase().endsWith(".xml"))
                {
                    size = (int) zipentry.getSize();
                    item = new String(baos.toByteArray(), 0, size, "UTF8");
                    logger.debug("Provided Metadata:" + item);
                }
                else
                {
                    attachements.add(baos.toByteArray());
                }               
                zipinputstream.closeEntry();
            }

            zipinputstream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return this.processFiles(item, attachements);
    }
    
    /**
     * 
     * @param item
     * @param files
     * @return
     * @throws NamingException
     * @throws TechnicalException
     */
    private PubItemVO processFiles(String item, Vector<byte[]> files) throws NamingException, TechnicalException
    {
        PubItemVO itemVO = null;
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming)initialContext.lookup(XmlTransforming.SERVICE_NAME);
        
        if (item == null)
        {
            logger.error("No metadata file was not provided");
            throw new RuntimeException();
        }
        
        itemVO = xmlTransforming.transformToPubItem(item);
        logger.debug("Item successfully created.");
        
        //TODO: Attach files
        
        return itemVO;
    }
    
    /**
     * 
     * @param user
     * @param item
     * @return Saved pubItem
     * @throws NamingException 
     * @throws AuthorizationException
     * @throws PubItemMandatoryAttributesMissingException
     * @throws PubItemLockedException
     * @throws PubCollectionNotFoundException
     * @throws PubItemNotFoundException
     * @throws PubItemStatusInvalidException
     * @throws PubItemAlreadyReleasedException
     * @throws SecurityException
     * @throws TechnicalException
     * @throws URISyntaxException
     * @throws NamingException 
     */
    public PubItemVO doDeposit (AccountUserVO user, PubItemVO item) throws NamingException, AuthorizationException, PubItemMandatoryAttributesMissingException, PubItemLockedException, PubCollectionNotFoundException, PubItemNotFoundException, PubItemStatusInvalidException, PubItemAlreadyReleasedException, SecurityException, TechnicalException, URISyntaxException 
    {
        PubItemVO depositedItem = null;
        InitialContext initialContext = new InitialContext();
        PubItemDepositing depositBean = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
        
        //TODO: workflow dependend
        depositedItem = depositBean.savePubItem(item, user);
        
        return depositedItem;
    }
}
