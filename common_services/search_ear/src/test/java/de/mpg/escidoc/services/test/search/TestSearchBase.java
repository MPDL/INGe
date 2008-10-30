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


import static org.junit.Assert.assertNotNull;


import java.util.ArrayList;


import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;


import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.ItemContainerSearch;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;

/**
 * 
 * TODO Base class for TestMetadataSearch and TestSearchAndExport.
 * Creates an test item on the coreservice infrastructure.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestSearchBase extends TestBase
{
    private static Logger logger = Logger.getLogger(TestSearchBase.class);
    @EJB
    protected ItemContainerSearch itemContainerSearch;
    protected static String itemTitle;
    protected static String creatorsGivenName;
    protected static String creatorsFamilyName;
    protected static CreatorRole creatorsRole;
    protected static String sourceTitle;
    protected static String eventTitle;
    protected static String dateAccepted;
    protected static String datePublishedOnline;
    protected static ItemVO testItem;
    protected static String publisher;
    protected static String edition;
    protected static String organizationName;
    protected static String genreName;
    protected static String abstractText;
    protected static String itemPid;
    protected static String dateCreated;

    /**
     * Sets up the search service before each test.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.itemContainerSearch = (ItemContainerSearch) getService(ItemContainerSearch.SERVICE_NAME);
    }

    /**
     * Creates the test item.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void createTestItem() throws Exception
    {
        AccountUserVO user = getUserTestDepLibWithHandle();
        // create test item 1
        PubItemVO myItem = getNewPubItemWithoutFiles();
        itemTitle = "Der kleine Prinz Test for Metadata Search" + System.nanoTime();
        myItem.getMetadata().getTitle().setValue(itemTitle);
        // change creator's organization
        OrganizationVO orga = new OrganizationVO();
        organizationName = "TestOrganization" + System.nanoTime();
        orga.setName(new TextVO(organizationName));
        myItem.getMetadata().getCreators().get(0).getPerson().getOrganizations().add(orga);
        // change the creator's name
        creatorsGivenName = "Hans" + System.nanoTime();
        creatorsFamilyName = "Meier" + System.nanoTime();
        myItem.getMetadata().getCreators().get(0).getPerson().setGivenName(creatorsGivenName);
        myItem.getMetadata().getCreators().get(0).getPerson().setFamilyName(creatorsFamilyName);
        myItem.getMetadata().getCreators().get(0).getPerson().setCompleteName(
                creatorsGivenName + " " + creatorsFamilyName);
        // change the creator's role
        creatorsRole = CreatorRole.AUTHOR;
        myItem.getMetadata().getCreators().get(0).setRole(creatorsRole);
        // add a genre
        genreName = Genre.ARTICLE.toString();
        myItem.getMetadata().setGenre(Genre.ARTICLE);
        // add a source
        SourceVO source = new SourceVO();
        myItem.getMetadata().getSources().add(source);
        sourceTitle = "SourceTestTitle" + System.nanoTime();
        source.setTitle(new TextVO(sourceTitle));
        // add publishing info
        PublishingInfoVO pubInfo = new PublishingInfoVO();
        source.setPublishingInfo(pubInfo);
        edition = "edition" + System.nanoTime();
        pubInfo.setEdition(edition);
        publisher = "Test Publisher" + System.nanoTime();
        pubInfo.setPublisher(publisher);
        // add an event
        EventVO event = new EventVO();
        eventTitle = "Event" + System.nanoTime();
        event.setTitle(new TextVO(eventTitle));
        myItem.getMetadata().setEvent(event);
        // add abstract
        abstractText = "Test Abstract No. " + System.nanoTime();
        TextVO abs = new TextVO(abstractText);
        myItem.getMetadata().getAbstracts().add(abs);
        // add dates
        dateAccepted = "2008-05-05";
        myItem.getMetadata().setDateAccepted(dateAccepted);
        datePublishedOnline = "2008-01-01";
        myItem.getMetadata().setDatePublishedOnline(datePublishedOnline);
        dateCreated = "2005-04-04";
        myItem.getMetadata().setDateCreated(dateCreated);
        // add test file
        FileVO file = new FileVO();
        file.setContent(uploadFile(TESTFILE_PATH, "application/pdf", user.getHandle()).toString());
        file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        file.setVisibility(Visibility.PUBLIC);
        file.setDefaultMetadata(new MdsFileVO());
        file.getDefaultMetadata().setTitle(new TextVO("Der_kleine_Prinz_Auszug.pdf"));
        file.setMimeType("application/pdf");
        file.setDescription("Auszug aus \"Der kleine Prinz\" von Antoine de Saint-Exupery.");
        file.setContentCategory("ANY_FULLTEXT");
        myItem.getFiles().add(file);
        ItemVO createdItem = createItem(myItem, user);
        assertNotNull(createdItem);
        testItem = submitAndReleaseItem(createdItem, user);
        assertNotNull(testItem);
        itemPid = testItem.getPid();
        logger.info("Test Item created: object id=" + testItem.getVersion().getObjectId());
        // create test item 2
        // wait a little bit for indexing...
        // if test fails, the time given for indexing might be too short
        // (with Thread.sleep(2000) the test sometimes failed.
        Thread.sleep(5000);
    }

    
    /**
     * Returns a standard metadata query object using publication content type
     * @return
     * @throws Exception
     */
    protected MetadataSearchQuery getStandardQuery() throws Exception
    {
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add((PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication")));
        MetadataSearchQuery query = new MetadataSearchQuery(contentTypes);
        return query;
    }

    
}
