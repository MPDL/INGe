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

package de.mpg.escidoc.services.test.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.w3c.dom.Document;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Base class for pubman logic tests.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class TestBase
{
    private static final Logger logger = Logger.getLogger(TestBase.class);
    
    protected static final String PUBMAN_TEST_COLLECTION_ID = "escidoc:persistent3";
    protected static final String PUBMAN_TEST_COLLECTION_NAME = "PubMan Test Collection";
    protected static final String PUBMAN_TEST_COLLECTION_DESCRIPTION = "This is the sample collection description of the PubMan Test\n"
            + "collection. Any content can be stored in this collection, which is of relevance\n" + "for the users of the system. You can submit " + "relev" + "ant bibliographic information\n"
            + "for your publication (metadata) and all relevant files. The MPS is the\n" + "responsible affiliation for this collection. Please contact\n" + "u.tschida@zim.mpg.de for any questions.";
    protected static final String MPG_TEST_AFFILIATION = "escidoc:persistent13";

    private static final int NUMBER_OF_URL_TOKENS = 2;
    
    static
    {
        System.setProperty("com.sun.xml.namespace.QName.useCompatibleSerialVersionUID", "1.0");
    }

    /**
     * Reads contents from text file and returns it as String.
     * 
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     * @throws FileNotFoundException
     */
    public static String readFile(String fileName)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
                File file = new File(fileName);
                FileReader in = new FileReader(file);
                BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
               fileBuffer = new StringBuffer();
                while ((line = dis.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                in.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }

    /**
     * Finds a given file name in the classpath and returns the file.
     * 
     * @param fileName The name of the file to lookup in the classpath.
     * @return The found file.
     * @throws FileNotFoundException
     */
    public File findFileInClasspath(String fileName) throws FileNotFoundException
    {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
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
    protected static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
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
     * Logs the user test_dep_scientist in and returns the corresponding user handle.
     * 
     * @return userhandle
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     */
    protected static String loginScientist() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        return loginUser(PropertyReader.getProperty("framework.scientist.username"), PropertyReader.getProperty("framework.scientist.password"));
    }

    /**
     * Logs the user test_dep_lib in and returns the corresponding user handle.
     * 
     * @return userhandle
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     */
    protected static String loginDepositorLibrary() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        return loginUser(PropertyReader.getProperty("framework.librarian.username"), PropertyReader.getProperty("framework.librarian.password"));
    }

    /**
     * Logs the user roland in and returns the corresponding user handle.
     * 
     * @return userhandle
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     */
    protected static String loginSystemAdministrator() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        return loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }
    

    /**
     * Logs in the user test_dep_scientist and returns the corresponding AccountUserVO
     * 
     * @return The account user test_dep_scientist with handle set.
     * @throws Exception
     */
    protected AccountUserVO getUserTestDepScientistWithHandle() throws Exception
    {
        return getUserWithHandle(loginScientist());
    }

    /**
     * Logs in the user test_dep_lib and returns the corresponding AccountUserVO
     * 
     * @return The account user test_dep_lib with handle set.
     * @throws Exception
     */
    protected AccountUserVO getUserTestDepLibWithHandle() throws Exception
    {
        return getUserWithHandle(loginDepositorLibrary());
    }
    
    /**
     * Logs in the user roland and returns the corresponding AccountUserVO
     * 
     * @return The account user roland with handle set.
     * @throws Exception
     */
    protected AccountUserVO getUserSystemAdministratorWithHandle() throws Exception
    {
        return getUserWithHandle(loginSystemAdministrator());
    }

    private AccountUserVO getUserWithHandle(String userHandle) throws Exception
    {
        String userXML = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
        XmlTransforming xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
        AccountUserVO user = xmlTransforming.transformToAccountUser(userXML);
        String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(user.getReference().getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = user.getGrants();
        for (GrantVO grant : grants)
        {
            userGrants.add(grant);
        }
        user.setHandle(userHandle);
        return user;
    }
    /**
     * Get a new pub item.
     * 
     * @return pub item 
     */
    protected PubItemVO getNewPubItemWithoutFiles()
    {
        PubItemVO item = new PubItemVO();
        
        // Metadata
        MdsPublicationVO mds = new MdsPublicationVO();
        TextVO title = new TextVO();
        title.setLanguage("en");
        title.setValue("PubMan: The first of all.");
        mds.setTitle(title);
        mds.setGenre(Genre.BOOK);
        // Add a creator[person] that is affiliated to one organization
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.AUTHOR);
        PersonVO person = new PersonVO();
        person.setGivenName("Hans");
        person.setFamilyName("Meier");
        person.setCompleteName("Hans Meier");
        OrganizationVO organizationVO = new OrganizationVO();
        TextVO name = new TextVO();
        name.setValue("Max Planck Society");
        organizationVO.setName(name);
        organizationVO.setAddress("Max-Planck-Str. 1");
        organizationVO.setIdentifier("escidoc:persistent26");
        person.getOrganizations().add(organizationVO);
        creator.setPerson(person);
        mds.getCreators().add(creator);
        // Provide a Date
        mds.setDateCreated("2007");
        creator.setPerson(person);
        mds.getCreators().add(creator);
        item.setMetadata(mds);
        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        item.setContext(collectionRef);
        return item;
    }

    protected PubItemVO getComplexPubItemWithoutFiles()
    {
        PubItemVO item = new PubItemVO();

        // Metadata
        MdsPublicationVO mds = getMdsPublication();
        item.setMetadata(mds);
        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        item.setContext(collectionRef);
        return item;
    }

    protected MdsPublicationVO getMdsPublication()
    {
        // Metadata
        MdsPublicationVO mds = new MdsPublicationVO();
        CreatorVO creator;
        OrganizationVO organization;
        PublishingInfoVO pubInfo;

        // Genre
        mds.setGenre(Genre.BOOK);

        // Creator
        creator = new CreatorVO();
        // Creator.Role
        creator.setRole(CreatorRole.AUTHOR);
        // Creator.Person
        PersonVO person = new PersonVO();
        // Creator.Person.CompleteName
        person.setCompleteName("Hans Meier");
        // Creator.Person.GivenName
        person.setGivenName("Hans");
        // Creator.Person.FamilyName
        person.setFamilyName("Meier");
        // Creator.Person.AlternativeName
        person.getAlternativeNames().add("Werner");
        person.getAlternativeNames().add("These tokens are escaped and must stay escaped: \"&amp;\", \"&gt;\", \"&lt;\", \"&quot;\", \"&apos;\"");
        person.getAlternativeNames().add("These tokens are escaped and must stay escaped, too: &auml; &Auml; &szlig;");
        // Creator.Person.Title
        person.getTitles().add("Dr. (?)");
        // Creator.Person.Pseudonym
        person.getPseudonyms().add("<b>Shorty</b>");
        person.getPseudonyms().add("<'Dr. Short'>");
        // Creator.Person.Organization
        organization = new OrganizationVO();
        // Creator.Person.Organization.Name
        TextVO name = new TextVO();
        name.setValue("Vinzenzmurr");
        name.setLanguage("de");
        organization.setName(name);
        // Creator.Person.Organization.Address
        organization.setAddress("<a href=\"www.buxtehude.de\">Irgendwo in Deutschland</a>");
        // Creator.Person.Organization.Identifier
        organization.setIdentifier("ED-84378462846");
        person.getOrganizations().add(organization);
        // Creator.Person.Identifier
        person.setIdentifier(new IdentifierVO(IdType.PND, "HH-XY-2222"));
        creator.setPerson(person);
        mds.getCreators().add(creator);
        creator = new CreatorVO();
        // Creator.Role
        creator.setRole(CreatorRole.CONTRIBUTOR);
        // Source.Creator.Organization
        organization = new OrganizationVO();
        // Creator.Organization.Name
        name.setValue("MPDL");
        name.setLanguage("en");
        organization.setName(name);
        // Creator.Organization.Address
        organization.setAddress("Amalienstraße");
        // Creator.Organization.Identifier
        organization.setIdentifier("1a");
        creator.setOrganization(organization);
        mds.getCreators().add(creator);

        // Title
        mds.setTitle(new TextVO("Über den Wölken. The first of all. Das Maß aller Dinge.", "en"));

        // Language
        mds.getLanguages().add("de");
        mds.getLanguages().add("en");
        mds.getLanguages().add("fr");

        // Alternative Title
        mds.getAlternativeTitles().add(new TextVO("Die Erste von allen.", "de"));
        mds.getAlternativeTitles().add(new TextVO("Wulewu", "fr"));

        // Identifier
        mds.getIdentifiers().add(new IdentifierVO(IdType.ISI, "0815"));
        mds.getIdentifiers().add(new IdentifierVO(IdType.ISSN, "issn"));

        // Publishing Info
        pubInfo = new PublishingInfoVO();
        pubInfo.setPublisher("O'Reilly Media Inc., 1005 Gravenstein Highway North, Sebastopol");
        pubInfo.setEdition("One and a half");
        pubInfo.setPlace("Garching-Itzehoe-Capreton");
        mds.setPublishingInfo(pubInfo);

        // Date
        mds.setDateCreated("2005-02");
        mds.setDateSubmitted("2005-08-31");
        mds.setDateAccepted("2005");
        mds.setDatePublishedInPrint("2006-02-01");
        mds.setDateModified("2007-02-28");

        // Review method
        mds.setReviewMethod(ReviewMethod.INTERNAL);

        // Source
        SourceVO source = new SourceVO();
        // Source.Title
        source.setGenre(SourceVO.Genre.BOOK);
        source.setTitle(new TextVO("Dies ist die Wurzel allen Übels.", "jp"));
        // Source.AlternativeTitle
        source.getAlternativeTitles().add(new TextVO("This is the root of all ???.", "en"));
        source.getAlternativeTitles().add(new TextVO("< and & are illegal characters in XML and therefore have to be escaped.", "en"));
        source.getAlternativeTitles().add(new TextVO("> and ' and ? are problematic characters in XML and therefore should be escaped.", "en"));
        source.getAlternativeTitles().add(new TextVO("What about `, $, §, \", @ and the good old % (not to forget the /, the !, -, the _, the ~, the @ and the #)?", "en"));
        source.getAlternativeTitles().add(new TextVO("By the way, the Euro sign looks like this: €", "en"));
        // Source.Creator
        creator = new CreatorVO();
        // Source.Creator.Role
        creator.setRole(CreatorRole.AUTHOR);
        // Source.Creator.Organization
        organization = new OrganizationVO();
        // Source.Creator.Organization.Name
        name.setValue("murrrmurr");
        name.setLanguage("de");
        organization.setName(name);
        // Source.Creator.Organization.Address
        organization.setAddress("Ümläüte ßind schön. à bientôt!");
        // Source.Creator.Organization.Identifier
        organization.setIdentifier("BLA-BLU-BLÄ");
        creator.setOrganization(organization);
        source.getCreators().add(creator);
        // Source.Volume
        source.setVolume("8a");
        // Source.Issue
        source.setIssue("13b");
        // Source.StartPage
        source.setStartPage("-12");
        // Source.EndPage
        source.setEndPage("131313");
        // Source.SequenceNumber
        source.setSequenceNumber("1-3-6");
        // Source.PublishingInfo
        pubInfo = new PublishingInfoVO();
        // Source.PublishingInfo.Publisher
        pubInfo.setPublisher("Martas Druckerei");
        // Source.PublishingInfo.Edition
        pubInfo.setEdition("III");
        // Source.PublishingInfo.Place
        pubInfo.setPlace("Hamburg-München");
        source.setPublishingInfo(pubInfo);
        // Source.Identifier
        source.getIdentifiers().add(new IdentifierVO(IdType.ISBN, "XY-347H-112"));
        // Source.Source
        source.getSources().add(new SourceVO(new TextVO("The source of the source.", "en")));
        CreatorVO sourceSourceCreator = new CreatorVO(new OrganizationVO(), CreatorRole.ARTIST);
        name.setValue("Creator of the Source of the source");
        name.setLanguage("en");
        sourceSourceCreator.getOrganization().setName(name);
        sourceSourceCreator.getOrganization().setIdentifier("ID-4711-0815");
        source.getSources().get(0).getCreators().add(sourceSourceCreator);
        mds.getSources().add(source);

        // Event
        EventVO event = new EventVO();
        // Event.Title
        event.setTitle(new TextVO("Weekly progress meeting", "en"));
        // Event.AlternativeTitle
        event.getAlternativeTitles().add(new TextVO("Wöchentliches Fortschrittsmeeting", "de"));
        // Event.StartDate
        event.setStartDate("2004-11-11");
        // Event.EndDate
        event.setEndDate("2005-02-19");
        // Event.Place
        name.setValue("Köln");
        name.setLanguage("de");
        event.setPlace(name);
        // Event.InvitationStatus
        event.setInvitationStatus(InvitationStatus.INVITED);
        mds.setEvent(event);

        // Total Numeber of Pages
        mds.setTotalNumberOfPages("999");

        // Degree
        mds.setDegree(DegreeType.MASTER);

        // Abstract
        mds.getAbstracts().add(new TextVO("Dies ist die Zusammenfassung der Veröffentlichung.", "de"));
        mds.getAbstracts().add(new TextVO("This is the summary of the publication.", "en"));

        // Subject
        name.setValue("wichtig,wissenschaftlich,spannend");
        name.setLanguage("de");
        mds.setSubject(name);

        // Table of Contents
        name.setValue("1.Einleitung 2.Inhalt");
        name.setLanguage("de");
        mds.setTableOfContents(name);

        // Location
        mds.setLocation("IPP, Garching");

        return mds;
    }

    /**
     * Helper method to retrieve a EJB service instance. The name to be passed to the method is normally
     * 'ServiceXY.SERVICE_NAME'.
     * 
     * @return instance of the EJB service
     * @throws NamingException
     */
    protected static Object getService(String serviceName) throws NamingException
    {
        InitialContext context = new InitialContext();
        Object serviceInstance = context.lookup(serviceName);
        assertNotNull(serviceInstance);
        return serviceInstance;
    }

    /**
     * Helper: Retrieves the item from the Framework ItemHandler and transforms it to a PubItemVO.
     */
    protected PubItemVO getPubItemFromFramework(ItemRO pubItemRef, AccountUserVO accountUser) throws Exception
    {
        XmlTransforming xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        String retrievedItem = ServiceLocator.getItemHandler(accountUser.getHandle()).retrieve(pubItemRef.getObjectId());
        
        logger.debug("retrieved item: " + retrievedItem);
        
        assertNotNull(retrievedItem);
        PubItemVO retrievedPubItem = xmlTransforming.transformToPubItem(retrievedItem);
        assertNotNull(retrievedPubItem);

        return retrievedPubItem;
    }

    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param filename The file to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file
     * @throws Exception
     */
    protected URL uploadFile(String filename, String mimetype, String userHandle) throws Exception
    {
        XmlTransforming xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        // Prepare the HttpMethod.
        String fwUrl = ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");

        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(filename)));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());

        return xmlTransforming.transformUploadResponseToFileURL(response);

    }

    /**
     * Parse the given xml String into a org.w3c.dom.Document.
     * 
     * @param xml The xml String
     * @param namespaceAwareness Enable/disable namespace awareness (default is false)
     * @return The Document
     * @throws Exception
     */
    protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception
    {
        String charset = "UTF-8";
        Document result = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(namespaceAwareness);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        result = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
        result.getDocumentElement().normalize();
        return result;
    }

    /**
     * Returns the current date in a valid format.
     * 
     * @return String that contains the actual date
     */
    public final String getActualDateString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
