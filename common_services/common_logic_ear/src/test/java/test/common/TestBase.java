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

package test.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
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
 * Base Class for tests in common_logic.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TestBase
{
    private static final String TEST_FILE_ROOT = "src/test/resources/";
    protected static final String ITEM_FILE = TEST_FILE_ROOT + "schindlmayr-springer.xml";
    protected static final String COMPONENT_FILE = TEST_FILE_ROOT + "schindlmayr-springer.pdf";
    protected static final String MIME_TYPE = "application/pdf";
    protected static final String PUBMAN_TEST_COLLECTION_ID = "escidoc:persistent3";
    protected static final String PUBMAN_TEST_COLLECTION_NAME = "PubMan Test Collection";
    protected static final String PUBMAN_TEST_COLLECTION_DESCRIPTION = "This is the sample collection description of the PubMan Test\n"
            + "collection. Any content can be stored in this collection, which is of relevance\n"
            + "for the users of the system. You can submit relevant bibliographic information\n"
            + "for your publication (metadata) and all relevant files. The MPS is the\n"
            + "responsible affiliation for this collection. Please contact\n"
            + "u.tschida@zim.mpg.de for any questions.";

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TestBase.class);

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
        String protocol = frameworkUrl.substring(0, frameworkUrl.indexOf(":"));
        String host = frameworkUrl.substring(frameworkUrl.indexOf("://") + 3);
        int port = 80;
        if (host.contains("/"))
        {
            host = host.substring(0, host.indexOf("/"));
        }
        if (host.contains(":"))
        {
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
            host = host.substring(0, host.indexOf(":"));
        }
        
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(host, port, protocol);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        PostMethod login = new PostMethod(ServiceLocator.getFrameworkUrl() + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        
        client.executeMethod(login);
        System.out.println("Login form post: " + login.getStatusLine().toString());
                
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(
                host, port, "/", false, 
                client.getState().getCookies());
        
        System.out.println("Logon cookies:");
        Cookie sessionCookie = logoncookies[0];
        
        if (logoncookies.length == 0) {
            
            System.out.println("None");
            
        } else {
            for (int i = 0; i < logoncookies.length; i++) {
                System.out.println("- " + logoncookies[i].toString());
            }
        }
        
        PostMethod postMethod = new PostMethod(frameworkUrl + "/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        System.out.println("Login second post: " + postMethod.getStatusLine().toString());
      
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode() && HttpServletResponse.SC_MOVED_TEMPORARILY != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + postMethod.getStatusCode());
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
                System.out.println("location: "+location);
                System.out.println("handle: "+userHandle);
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
     * @return userHandle
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     */
    protected static String loginScientist() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        return loginUser("test_dep_scientist", "escidoc");
    }

    /**
     * Logs the user test_dep_lib in and returns the corresponding user handle.
     * 
     * @return userHandle
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     */
    protected static String loginLibrarian() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        return loginUser("test_dep_lib", "pubman");
    }

    /**
     * Logs the user roland in who is a system administrator and returns the corresponding user handle.
     * 
     * @return userHandle
     * @throws Exception
     */
    protected static String loginSystemAdministrator() throws Exception
    {
        return loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }

    /**
     * Logs the user with the given userHandle out from the system.
     * 
     * @param userHandle
     * @throws WebserverSystemException
     * @throws SqlDatabaseSystemException
     * @throws AuthenticationException
     * @throws RemoteException
     * @throws ServiceException
     */
    protected static void logout(String userHandle) throws RemoteException, ServiceException, URISyntaxException
    {
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
    }

    /**
     * @param userHandle
     * @return The AccountUserVO
     * @throws Exception
     */
    protected AccountUserVO getAccountUser(String userHandle) throws Exception
    {
        AccountUserVO accountUser = new AccountUserVO();
        String xmlUser = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
        accountUser = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToAccountUser(xmlUser);
        // add the user handle to the transformed account user
        accountUser.setHandle(userHandle);
        String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(
                accountUser.getReference().getObjectId());
        List<GrantVO> grants = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME))
                .transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = accountUser.getGrants();
        for (GrantVO grant : grants)
        {
            userGrants.add(grant);
        }
        return accountUser;
    }

    /**
     * Creates a well-defined PubItemVO without any files attached.
     * 
     * @return pubItem
     */
    protected PubItemVO getPubItemWithoutFiles()
    {
        PubItemVO item = new PubItemVO();

        // Metadata
        MdsPublicationVO mds = getMdsPublication1();
        item.setMetadata(mds);

        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        item.setContext(collectionRef);
        return item;
    }

    /**
     * Creates anpther well-defined PubItemVO.
     * 
     * @return pubItem
     */
    protected PubItemVO getPubItem2()
    {
        PubItemVO item = new PubItemVO();

        // (1) metadata
        MdsPublicationVO mds = getMdsPublication2();
        item.setMetadata(mds);

        // (2) pubCollection
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId("escidoc:persistent3");
        item.setContext(collectionRef);

        return item;
    }

    /**
     * Creates a well-defined PubItemVO named "PubMan: The first of all.".
     * 
     * @return pubItem
     */
    protected PubItemVO getPubItemNamedTheFirstOfAll()
    {
        PubItemVO item = new PubItemVO();

        // properties of the item
        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        item.setContext(collectionRef);

        // item metadata
        MdsPublicationVO mds = new MdsPublicationVO();
        // title
        TextVO title = new TextVO("PubMan: The first of all.", "en");
        mds.setTitle(title);
        // genre
        mds.setGenre(Genre.BOOK);
        // creator(s)
        // Add a creator[person] that is affiliated to one organization
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.AUTHOR);
        PersonVO person = new PersonVO();
        person.setGivenName("Hans");
        person.setFamilyName("Meier");
        person.setCompleteName("Hans Meier");
        OrganizationVO organizationVO = new OrganizationVO();
        TextVO name = new TextVO("Test Organization", "en");
        organizationVO.setName(name);
        organizationVO.setAddress("Max-Planck-Str. 1");
        person.getOrganizations().add(organizationVO);
        creator.setPerson(person);
        mds.getCreators().add(creator);
        mds.getCreators().add(creator);
        // dates
        mds.setDateCreated("2007");
        mds.setDatePublishedInPrint("2007-01-02");
        mds.setDatePublishedOnline("2007-03-04");
        // source(s)
        SourceVO source = new SourceVO();
        source.setTitle(new TextVO("The title of the source", "en"));
        source.setGenre(SourceVO.Genre.JOURNAL);
        // event
        EventVO event = new EventVO();
        event.setStartDate("2007-10-31");
        event.setEndDate("2007-12-31");
        event.setInvitationStatus(InvitationStatus.INVITED);
        event.setPlace(new TextVO("Füssen (nicht Füßen) im schwäbischen Landkreis Ostallgäu.", "jp"));
        event.setTitle(new TextVO("Un bôn vín fràn\uc3a7ais", "fr"));
        // subject
        TextVO subject = new TextVO("This is the subject. Betreffs fußen auf Gerüchten für Äonen.", "de");
        mds.setSubject(subject);
        // table of contents
        TextVO toc = new TextVO("I like to test with umlauts. Es grünt ßo grün, wenn Spániäns Blümälain blühn.", "it");
        mds.setTableOfContents(toc);

        item.setMetadata(mds);

        return item;
    }

    /**
     * Creates a well-defined PubItemVO named "PubMan: The first of all.".
     * 
     * @return pubItem
     */
    protected PubItemResultVO getPubItemResultNamedTheFirstOfAll()
    {
        PubItemResultVO itemResult = new PubItemResultVO();

        // properties of the item
        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        itemResult.setContext(collectionRef);

        // item metadata
        MdsPublicationVO mds = new MdsPublicationVO();
        // title
        TextVO title = new TextVO();
        title.setLanguage("en");
        title.setValue("PubMan: The first of all.");
        mds.setTitle(title);
        // genre
        mds.setGenre(Genre.BOOK);
        // creator(s)
        // Add a creator[person] that is affiliated to one organization
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.AUTHOR);
        PersonVO person = new PersonVO();
        person.setGivenName("Hans");
        person.setFamilyName("Meier");
        person.setCompleteName("Hans Meier");
        OrganizationVO organizationVO = new OrganizationVO();
        TextVO name = new TextVO();
        title.setLanguage("en");
        title.setValue("Test Organization");
        organizationVO.setName(name);
        organizationVO.setAddress("Max-Planck-Str. 1");
        person.getOrganizations().add(organizationVO);
        creator.setPerson(person);
        mds.getCreators().add(creator);
        mds.getCreators().add(creator);
        // dates
        mds.setDateCreated("2007");
        mds.setDatePublishedInPrint("2007-01-02");
        mds.setDatePublishedOnline("2007-03-04");
        // source(s)
        SourceVO source = new SourceVO();
        source.setTitle(new TextVO("The title of the source", "en"));
        source.setGenre(SourceVO.Genre.JOURNAL);
        itemResult.setMetadata(mds);

        return itemResult;
    }

    /**
     * Creates a well-defined, complex PubItemVO without files.
     * 
     * @return pubItem
     */
    protected PubItemVO getComplexPubItemWithoutFiles()
    {
        PubItemVO item = new PubItemVO();

        // Metadata
        MdsPublicationVO mds = getMdsPublication1();
        item.setMetadata(mds);

        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        item.setContext(collectionRef);

        return item;
    }

    /**
     * Creates a well-defined, complex MdsPublicationVO.
     * 
     * @return The generated MdsPublicationVO.
     */
    protected MdsPublicationVO getMdsPublication1()
    {
        // Metadata
        MdsPublicationVO mds = new MdsPublicationVO();

        // Genre
        mds.setGenre(Genre.BOOK);

        // Creator
        CreatorVO creator;
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
        person
                .getAlternativeNames()
                .add(
                        "These tokens are escaped and must stay escaped: \"&amp;\", \"&gt;\", \"&lt;\", \"&quot;\", \"&apos;\"");
        person.getAlternativeNames().add("These tokens are escaped and must stay escaped, too: &auml; &Auml; &szlig;");
        // Creator.Person.Title
        person.getTitles().add("Dr. (?)");
        // Creator.Person.Pseudonym
        person.getPseudonyms().add("<b>Shorty</b>");
        person.getPseudonyms().add("<'Dr. Short'>");
        // Creator.Person.Organization
        OrganizationVO organization;
        organization = new OrganizationVO();
        // Creator.Person.Organization.Name
        TextVO name = new TextVO();
        name.setLanguage("en");
        name.setValue("Vinzenzmurr");
        organization.setName(name);
        // Creator.Person.Organization.Address
        organization.setAddress("<a ref=\"www.buxtehude.de\">Irgendwo in Deutschland</a>");
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
        name = new TextVO();
        name.setLanguage("en");
        name.setValue("MPDL");
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
        PublishingInfoVO pubInfo;
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
        mds.setDateModified("2007-02-27");

        // Review method
        mds.setReviewMethod(ReviewMethod.INTERNAL);

        // Source
        SourceVO source = new SourceVO();
        // Source.Title
        source.setTitle(new TextVO("Dies ist die Wurzel allen Übels.", "jp"));
        // Source.Genre
        // source.setGenre(SourceVO.Genre.SERIES);
        // Source.AlternativeTitle
        source.getAlternativeTitles().add(new TextVO("This is the root of all ???.", "en"));
        source.getAlternativeTitles().add(
                new TextVO("< and & are illegal characters in XML and therefore have to be escaped.", "en"));
        source.getAlternativeTitles().add(
                new TextVO("> and ' and ? are problematic characters in XML and therefore should be escaped.", "en"));
        source.getAlternativeTitles().add(
                new TextVO(
                        "What about `, ´, äöüÄÖÜß, áàéèô, and the good old % (not to forget the /, the \\, -, the _, the\n"
                                + "~, the @ and the #)?", "en"));
        source.getAlternativeTitles().add(new TextVO("By the way, the Euro sign looks like this: €", "en"));
        // Source.Creator
        creator = new CreatorVO();
        // Source.Creator.Role
        creator.setRole(CreatorRole.AUTHOR);
        // Source.Creator.Organization
        organization = new OrganizationVO();
        // Source.Creator.Organization.Name
        name = new TextVO();
        name.setLanguage("de");
        name.setValue("murrrmurr");
        organization.setName(name);
        // Source.Creator.Organization.Address
        organization.setAddress("Ümläüte ßind ßchön. à bientôt!");
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
        name = new TextVO();
        name.setLanguage("en");
        name.setValue("Creator of the Source of the source");
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
        TextVO place = new TextVO();
        place.setLanguage("de");
        place.setValue("Köln");
        event.setPlace(place);
        // Event.InvitationStatus
        event.setInvitationStatus(InvitationStatus.INVITED);
        mds.setEvent(event);

        // Total Numeber of Pages
        mds.setTotalNumberOfPages("999");

        // Degree
        mds.setDegree(DegreeType.MASTER);

        // Abstracts
        mds.getAbstracts().add(new TextVO("Dies ist die Zusammenfassung der Veröffentlichung.", "de"));
        mds.getAbstracts().add(new TextVO("This is the summary of the publication.", "en"));

        // Subject
        TextVO subject = new TextVO();
        subject.setLanguage("de");
        subject.setValue("wichtig,wissenschaftlich,spannend");
        mds.setSubject(subject);

        // Table of Contents
        TextVO tableOfContents = new TextVO();
        tableOfContents.setLanguage("de");
        tableOfContents.setValue("1.Einleitung 2.Inhalt");
        mds.setTableOfContents(tableOfContents);

        // Location
        mds.setLocation("IPP, Garching");

        return mds;
    }

    /**
     * Creates a well-defined, complex MdsPublicationVO.
     * 
     * @return The generated MdsPublicationVO.
     */
    protected MdsPublicationVO getMdsPublication2()
    {
        // Metadata
        MdsPublicationVO mds = new MdsPublicationVO();

        // Title
        TextVO title = new TextVO();
        title.setLanguage("en");
        title.setValue("The title");
        mds.setTitle(title);

        // Genre
        mds.setGenre(Genre.BOOK);

        // Creators
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.AUTHOR);
        PersonVO person = new PersonVO();
        person.setGivenName("Hans");
        person.setFamilyName("Meier");
        person.setCompleteName("Hans Meier");
        creator.setPerson(person);
        mds.getCreators().add(creator);

        // Dates
        mds.setDateCreated("2005-2");
        mds.setDateSubmitted("2005-8-31");
        mds.setDateAccepted("2005");
        mds.setDatePublishedInPrint("2006-2-1");
        mds.setDateModified("2007-2-29");

        // Identifiers
        List<IdentifierVO> identifierList = mds.getIdentifiers();
        IdentifierVO identifierVO = new IdentifierVO();
        identifierVO.setId("id1");
        identifierVO.setType(IdType.ESCIDOC);
        for (int i = 0; i < 2; i++)
        {
            identifierList.add(identifierVO);
        }

        // Publishing info
        PublishingInfoVO publishingInfoVO = new PublishingInfoVO();
        publishingInfoVO.setEdition("Edition 123");
        publishingInfoVO.setPlace("Place 5");
        publishingInfoVO.setPublisher("Publisher XY");
        mds.setPublishingInfo(publishingInfoVO);

        // build the List of SourceVOs...
        List<SourceVO> sourcesList = mds.getSources();

        // build one SourceVO instance...
        SourceVO sourceVO = new SourceVO();
        sourceVO.setTitle(title);
        List<TextVO> alternativeTitleList = sourceVO.getAlternativeTitles();
        for (int i = 0; i < 2; i++)
        {
            alternativeTitleList.add(title);
        }
        List<CreatorVO> creatorList = sourceVO.getCreators();
        for (int i = 0; i < 2; i++)
        {
            creatorList.add(creator);
        }
        sourceVO.setVolume("Volume 100");
        sourceVO.setIssue("Issue 99");
        sourceVO.setStartPage("StartPage 23");
        sourceVO.setEndPage("Endpage is 54");
        sourceVO.setSequenceNumber("SequenceNumber 12");
        sourceVO.setPublishingInfo(publishingInfoVO);
        List<IdentifierVO> sourceIdentifierList = sourceVO.getIdentifiers();
        for (IdentifierVO id : identifierList)
        {
            sourceIdentifierList.add(id);
        }

        // build another SourceVO instance...
        SourceVO sourceVO2 = new SourceVO();
        sourceVO2.setTitle(title);
        List<TextVO> alternativeTitleList2 = sourceVO2.getAlternativeTitles();
        for (int i = 0; i < 2; i++)
        {
            alternativeTitleList2.add(title);
        }
        List<CreatorVO> creatorList2 = sourceVO2.getCreators();
        for (int i = 0; i < 2; i++)
        {
            creatorList2.add(creator);
        }
        sourceVO2.setVolume("Volume 100");
        sourceVO2.setIssue("Issue 99");
        sourceVO2.setStartPage("StartPage 23");
        sourceVO2.setEndPage("Endpage is 54");
        sourceVO2.setSequenceNumber("SequenceNumber 12");
        sourceVO2.setPublishingInfo(publishingInfoVO);
        List<IdentifierVO> sourceIdentifierList2 = sourceVO2.getIdentifiers();
        for (IdentifierVO id : identifierList)
        {
            sourceIdentifierList2.add(id);
        }

        // add several of the "other" SourceVO instances to the first SourceVO instance
        List<SourceVO> sourceSourcesList = sourceVO.getSources();
        for (int i = 0; i < 2; i++)
        {
            sourceSourcesList.add(sourceVO2);
        }

        // add SourceVO several times
        for (int i = 0; i < 2; i++)
        {
            sourcesList.add(sourceVO);
        }

        // Event
        EventVO event = new EventVO();
        // Event.Title
        event.setTitle(new TextVO("Länderübergreifende Änderungsüberlegungen", "jp"));
        // Event.AlternativeTitle
        event.getAlternativeTitles().add(new TextVO("Änderungen gibt's immer, auch länderübergreifend", "es"));
        // Event.StartDate
        event.setStartDate("2000-02-29");
        // Event.EndDate
        event.setEndDate("2001-02-28");
        // Event.Place
        TextVO place = new TextVO();
        title.setLanguage("de");
        title.setValue("Grevenbröich");
        event.setPlace(place);
        // Event.InvitationStatus
        event.setInvitationStatus(InvitationStatus.INVITED);
        mds.setEvent(event);

        return mds;
    }

    /**
     * Searches the Java classpath for a given file name and gives back the file (or a FileNotFoundException).
     * 
     * @param fileName
     * @return The file
     * @throws FileNotFoundException
     */
    public static File findFileInClasspath(String fileName) throws FileNotFoundException
    {
        URL url = TestBase.class.getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
    }

    /**
     * Reads contents from text file and returns it as String.
     * 
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     * @throws IOException
     */
    protected static String readFile(String fileName) throws IOException
    {
        if (fileName == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":readFile:fileName is null");
        }
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        File file = new File(fileName);
        if (!file.exists())
        {
        	URL fileUrl = TestBase.class.getClassLoader().getResource(fileName);
        	file = new File(fileUrl.getFile());
        }
        BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        fileBuffer = new StringBuffer();
        while ((line = dis.readLine()) != null)
        {
            fileBuffer.append(line + "\n");
        }
        fileString = fileBuffer.toString();
        return fileString;
    }

    /**
     * Search the given String for the first occurence of "objid" and return its value.
     * 
     * @param item A (XML) String
     * @return The objid value
     */
    protected static String getObjid(String item)
    {
        String result = "";
        String searchString = "objid=\"";
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf('\"');
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    }

    /**
     * Search the given String for the first occurence of "last-modification-date" and return its value.
     * 
     * @param item A (XML) String
     * @return The last-modification-date value
     */
    protected static String getLastModificationDate(String item)
    {
        String result = "";
        String searchString = "last-modification-date=\"";
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf('\"');
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     * 
     * @param message The message printed if assertion fails.
     * @param node The Node.
     * @param xPath The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXMLExist(final String message, final Node node, final String xPath) throws Exception
    {
        if (message == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:message is null");
        }
        if (node == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:node is null");
        }
        if (xPath == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLExist:xPath is null");
        }
        NodeList nodes = selectNodeList(node, xPath);
        assertTrue(message, nodes.getLength() > 0);
    }

    /**
     * Assert that the XML is valid to the schema.
     * 
     * @param xmlData
     * @param schemaFileName
     * @throws Exception
     */
    public static void assertXMLValid(final String xmlData, final String schemaFileName) throws Exception
    {
        if (xmlData == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":assertXMLValid:xmlData is null");
        }
        if (schemaFileName == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName()
                    + ":assertXMLValid:schemaFileName is null");
        }
        Schema schema = getSchema(schemaFileName);
        try
        {
            Validator validator = schema.newValidator();
            InputStream in = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
            validator.validate(new SAXSource(new InputSource(in)));
        }
        catch (SAXParseException e)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("XML invalid at line:" + e.getLineNumber() + ", column:" + e.getColumnNumber() + "\n");
            sb.append("SAXParseException message: " + e.getMessage() + "\n");
            sb.append("Affected XML: \n" + xmlData);
            fail(sb.toString());
        }
    }

    /**
     * Gets the <code>Schema</code> object for the provided <code>File</code>.
     * 
     * @param schemaStream The file containing the schema.
     * @return Returns the <code>Schema</code> object.
     * @throws Exception If anything fails.
     */
    private static Schema getSchema(final String schemaFileName) throws Exception
    {
        if (schemaFileName == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getSchema:schemaFileName is null");
        }
        File schemaFile = new File(schemaFileName);

        if (schemaFile.exists())
        {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sf.newSchema(schemaFile);
            return theSchema;
        }
        else
        {
        	URL schemaUrl = TestBase.class.getClassLoader().getResource(schemaFileName);
        	Source schemaSource = new StreamSource(schemaUrl.getFile());
        	SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sf.newSchema(schemaSource);
            return theSchema;
        }
    }

    /**
     * Delivers the value of one distinct node in an <code>org.w3c.dom.Document</code>.
     * 
     * @param document The <code>org.w3c.dom.Document</code>.
     * @param xpath The xPath describing the node position.
     * @return The value of the node.
     * @throws TransformerException
     */
    protected String getValue( Document document, String xpathExpression ) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return xPath.evaluate(xpathExpression, document);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Parse the given xml String into a Document.
     * 
     * @param xml The xml String.
     * @param namespaceAwareness namespace awareness (default is false)
     * @return The Document.
     * @throws Exception If anything fails.
     */
    protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception
    {
        if (xml == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getDocument:xml is null");
        }
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
     * Return the child of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static Node selectSingleNode(final Node node, final String xpathExpression) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return (Node)xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Return the list of children of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath.
     * @return The list of children of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static NodeList selectNodeList(final Node node, final String xpathExpression) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return (NodeList)xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Return the text value of the selected attribute.
     * 
     * @param node The node.
     * @param xPath The xpath to select the node containint the attribute,
     * @param attributeName The name of the attribute.
     * @return The text value of the selected attribute.
     * @throws Exception If anything fails.
     */
    public static String getAttributeValue(final Node node, final String xPath, final String attributeName)
            throws Exception
    {
        if (node == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getAttributeValue:node is null");
        }
        if (xPath == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":getAttributeValue:xPath is null");
        }
        if (attributeName == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName()
                    + ":getAttributeValue:attributeName is null");
        }
        String result = null;
        Node attribute = selectSingleNode(node, xPath);
        if (attribute.hasAttributes())
        {
            result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
        }
        return result;
    }

    /**
     * Gets the value of the specified attribute of the root element from the document.
     * 
     * @param document The document to retrieve the value from.
     * @param attributeName The name of the attribute whose value shall be retrieved.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     * @throws TransformerException
     */
    public static String getRootElementAttributeValue(final Document document, final String attributeName)
            throws Exception
    {
        if (document == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName()
                    + ":getRootElementAttributeValue:document is null");
        }
        if (attributeName == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName()
                    + ":getRootElementAttributeValue:attributeName is null");
        }
        String xPath;
        if (attributeName.startsWith("@"))
        {
            xPath = "/*/" + attributeName;
        }
        else
        {
            xPath = "/*/@" + attributeName;
        }
        assertXMLExist("Attribute not found [" + attributeName + "]. ", document, xPath);
        String value = selectSingleNode(document, xPath).getTextContent();
        return value;
    }

    /**
     * Serialize the given Dom Object to a String.
     * 
     * @param xml The Xml Node to serialize.
     * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
     * @return The String representation of the Xml Node.
     * @throws Exception If anything fails.
     */
    protected static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception
    {
        if (xml == null)
        {
            throw new IllegalArgumentException(TestBase.class.getSimpleName() + ":toString:xml is null");
        }
        String result = null;
        if (xml instanceof AttrImpl)
        {
            result = xml.getTextContent();
        }
        else if (xml instanceof Document)
        {
            StringWriter stringOut = new StringWriter();
            // format
            OutputFormat format = new OutputFormat((Document) xml);
            format.setIndenting(true);
            format.setPreserveSpace(false);
            format.setOmitXMLDeclaration(omitXMLDeclaration);
            format.setEncoding("UTF-8");
            // serialize
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();

            serial.serialize((Document) xml);
            result = stringOut.toString();
        }
        else
        {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding("UTF-8");

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            lsOutput.setByteStream(os);
            LSSerializer writer = impl.createLSSerializer();
            // result = writer.writeToString(xml);
            writer.write(xml, lsOutput);
            result = ((ByteArrayOutputStream) lsOutput.getByteStream()).toString();
            if ((omitXMLDeclaration) && (result.indexOf("?>") != -1))
            {
                result = result.substring(result.indexOf("?>") + 2);
            }
            // result = toString(getDocument(writer.writeToString(xml)),
            // true);
        }
        return result;
    }

    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param filename The file to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userHandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(String filename, String mimetype, final String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");

        File file = new File(filename);
        if (!file.exists())
        {
        	URL fileUrl = TestBase.class.getClassLoader().getResource(filename);
        	file = new File(fileUrl.getFile());
        }
        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(file)));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());

        return ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformUploadResponseToFileURL(response);
    }

    /**
     * Creates an item with a file in the framework.
     * 
     * @param userHandle The userHandle of a user with the appropriate grants.
     * @return The XML of the created item with a file, given back by the framework.
     * @throws Exception
     */
    protected String createItemWithFile(String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        PutMethod method = new PutMethod(ServiceLocator.getFrameworkUrl() + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(COMPONENT_FILE)));
        method.setRequestHeader("Content-Type", MIME_TYPE);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        logger.debug("Status=" + method.getStatusCode()); // >= HttpServletResponse.SC_MULTIPLE_CHOICE 300 ???
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());
        String response = method.getResponseBodyAsString();
        logger.debug("Response=" + response);

        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(method.getResponseBodyAsStream());
        document.getDocumentElement().normalize();

        // Extract the file information.
        String href = getValue(document, "/staging-file/@href");
        assertNotNull(href);

        // Create an item with the href in the component.
        String item = readFile(ITEM_FILE);
        item = item.replaceFirst("XXX_CONTENT_REF_XXX", ServiceLocator.getFrameworkUrl() + href);
        logger.debug("Item=" + item);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        assertNotNull(item);
        logger.debug("Item=" + item);

        return item;
    }
}
