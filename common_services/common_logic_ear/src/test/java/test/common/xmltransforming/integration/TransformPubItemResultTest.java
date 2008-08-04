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

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.FileRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.HitwordVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.TextFragmentVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.LockStatus;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;

/**
 * Test class for {@link XmlTransforming#transformToPubItemResultVO(String)}
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: tendres $ (last modification)
 * @version $Revision: 663 $ $LastChangedDate: 2007-12-12 14:18:51 +0100 (Wed, 12 Dec 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemResultTest extends XmlTransformingTestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TransformPubItemResultTest.class);

    private static String TEST_FILE_ROOT = "xmltransforming/component/transformPubItemResultTest/";
    private static String SEARCH_RESULT_SAMPLE_FILE = TEST_FILE_ROOT + "search-result_sample.xml";
    private static String SEARCH_RESULT_SAMPLE2_FILE = TEST_FILE_ROOT + "search-result_sample2.xml";
    private static String COMPLEX_SEARCH_RESULT_SAMPLE1_FILE = TEST_FILE_ROOT + "complex_search-result_sample1.xml";
    private static final String SEARCH_RESULT_SCHEMA_FILE = "xsd/soap/search-result/0.6/search-result.xsd";

    /**
     * An instance of XmlTransforming.
     */
    private static XmlTransforming xmlTransforming;

    /**
     * Setup before.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Test of {@link XmlTransforming#transformToPubItemResultVO(String)}. Reads search result[XML] from a file,
     * transforms the XML to a PubItemResultVO and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToSimplePubItemResultVO() throws Exception
    {
        // read search result[XML] from a file
        String searchResultXml = readFile(SEARCH_RESULT_SAMPLE_FILE);
        logger.debug("read search result[XML] from file('" + SEARCH_RESULT_SAMPLE_FILE + "')=\n" + searchResultXml);
        assertNotNull(searchResultXml);

        // assert that the read XML is valid
        assertXMLValid(searchResultXml);

        // transform the XML to a PubItemResultVO
        PubItemResultVO pubItemResult = xmlTransforming.transformToPubItemResultVO(searchResultXml);
        assertNotNull(pubItemResult);
        assertEquals(getExpectedPubItem().getMetadata(), pubItemResult.getMetadata());
        assertEquals(State.RELEASED, pubItemResult.getVersion().getState());

        // check the results
        ObjectComparator oc = new ObjectComparator(getExpectedPubItem(), pubItemResult);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Test of {@link XmlTransforming#transformToPubItemResultVO(String)}. Reads search result[XML] from a file,
     * transforms the XML to a PubItemResultVO and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToComplexPubItemResultVO_1() throws Exception
    {
        // read search result[XML] from a file
        String searchResultXml = readFile(COMPLEX_SEARCH_RESULT_SAMPLE1_FILE);
        logger.debug("read search result[XML] from file('" + COMPLEX_SEARCH_RESULT_SAMPLE1_FILE + "')=\n" + searchResultXml);
        assertNotNull(searchResultXml);

        // assert that the read XML is valid
        // assertXMLValid(searchResultXml, SEARCH_RESULT_SCHEMA_FILE);

        // transform the XML to a PubItemResultVO
        PubItemResultVO pubItemResult = xmlTransforming.transformToPubItemResultVO(searchResultXml);
        assertNotNull(pubItemResult);

        // check the results

        // pubItemResult.getCreationDate()
        String dateString = "2007-10-05T11:24:45.000Z";
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
        Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();
        assertEquals(date, pubItemResult.getCreationDate());

        // pubItemResult.getCurrentVersion()
        // Is no longer transformed
//        assertEquals("submit test", pubItemResult.getCurrentVersion().getComment());
//        dateString = "2007-10-05T12:02:01.546Z";
//        xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
//        date = xmlGregorianCalendar.toGregorianCalendar().getTime();
//        assertEquals(date, pubItemResult.getCurrentVersion().getDate());
//        assertEquals("escidoc:user2", pubItemResult.getCurrentVersion().getModifiedBy());
//        assertEquals(5, pubItemResult.getCurrentVersion().getNumber());
//        assertEquals(PubItemVO.State.RELEASED, pubItemResult.getCurrentVersion().getVersionStatus());

        // pubItemResult.getFiles()
        assertEquals(3, pubItemResult.getFiles().size());
        FileVO file1 = pubItemResult.getFiles().get(0);
        assertEquals("/ir/item/escidoc:22/components/component/escidoc:23/content", file1.getContent());
        assertEquals("abstract", file1.getContentCategory());
        dateString = "2007-10-05T11:24:42.546Z";
        xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
        date = xmlGregorianCalendar.toGregorianCalendar().getTime();
        assertEquals(date, file1.getCreationDate());
        assertEquals("top01", file1.getDescription());
        dateString = "2007-10-05T12:02:01.546Z";
        xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
        date = xmlGregorianCalendar.toGregorianCalendar().getTime();
        assertEquals(date, file1.getLastModificationDate());
        // assertEquals(???, file1.getLocator());
        assertEquals("application/pdf", file1.getMimeType());
        assertEquals("top01.pdf", file1.getName());
        assertEquals("PID0815", file1.getPid());
        assertNotNull(file1.getReference());
        assertEquals("escidoc:23", file1.getReference().getObjectId());
        //file-size property of components doesn't exist anymore
        //assertEquals(234840l, file1.getSize());
        assertEquals(Visibility.PUBLIC, file1.getVisibility());

        // pubItemResult.getLatestRevision();
        // pubItemResult.getLatestVersion();
        // pubItemResult.getLockStatus();
        // pubItemResult.getMetadata();
        // pubItemResult.getModificationDate();
        // pubItemResult.getOwner();
        // pubItemResult.getPid();
        // pubItemResult.getPubCollection();
        // pubItemResult.getReference();

        // pubItemResult.getSearchHitList()
        assertEquals(1, pubItemResult.getSearchHitList().size());
        SearchHitVO searchHit = pubItemResult.getSearchHitList().get(0);
        // assertEquals(???, searchHit.getHitReference());
        assertEquals(SearchHitType.METADATA, searchHit.getType());
        assertEquals(4, searchHit.getTextFragmentList().size());
        TextFragmentVO textFragment1 = searchHit.getTextFragmentList().get(0);
        assertNotNull(textFragment1);
        assertEquals("> <srel:context xlink:type=\"simple\" xlink:title=\"PubMan Test Collection &amp;apos; &amp;gt;", textFragment1.getData());
        assertEquals(1, textFragment1.getHitwordList().size());
        HitwordVO hitWord = textFragment1.getHitwordList().get(0);
        assertEquals(56, hitWord.getStartIndex());
        assertEquals(59, hitWord.getEndIndex());
        assertEquals("Test", textFragment1.getData().substring(hitWord.getStartIndex(), hitWord.getEndIndex() + 1));

        // pubItemResult.getState()
        assertEquals(State.RELEASED, pubItemResult.getVersion().getState());

        // pubItemResult.getWithdrawalComment();

    }

    /**
     * Test of {@link XmlTransforming#transformToPubItemResultVO(String)}. Reads search result[XML] from a file,
     * transforms the XML to a PubItemResultVO and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubItemResultVOWithHighlightInfo() throws Exception
    {
        // read search result[XML] from a file
        String searchResultXml = readFile(SEARCH_RESULT_SAMPLE2_FILE);
        logger.debug("searchResultXml: " + searchResultXml);
        assertNotNull(searchResultXml);

        // transform the XML to a PubItemResultVO
        PubItemResultVO pubItemResult = xmlTransforming.transformToPubItemResultVO(searchResultXml);
        assertNotNull(pubItemResult);

        // check the results
        ObjectComparator oc = new ObjectComparator(getExpectedPubItem(), pubItemResult);
        assertTrue(oc.toString(), oc.isEqual());
        List<SearchHitVO> searchHitlist = pubItemResult.getSearchHitList();
        assertNotNull("searchHitList is null", searchHitlist);
        oc = new ObjectComparator(getExpectedSearchHitList(), searchHitlist);
        assertTrue(oc.toString(), oc.isEqual());
    }
    
    /**
     * Test of {@link XmlTransforming#transformToItem}. Creates a PubItemSearchResultVO (which is a subclass of
     * PubItemVO) and tries to transform it to pubItem XML.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformPubItemResultVOToItemXml() throws Exception
    {
      PubItemResultVO pubItemResult = getPubItemResultNamedTheFirstOfAll();
      String pubItemXML = xmlTransforming.transformToItem(pubItemResult);
      logger.info(pubItemXML);
      Document doc = getDocument(pubItemXML, false);
      
      // TODO FrM: Clarify test
      assertXMLExist("This is not an item XML.", doc, "/item");
    }

    /**
     * Delivers a well-defined PubItemVO.
     * 
     * @return The well-defined PubItemVO.
     */
    protected PubItemVO getExpectedPubItem() throws Exception
    {
        PubItemVO item = new PubItemVO();
        item.setVersion(new ItemRO("escidoc:441"));
        item.getVersion().setState(State.RELEASED);
        item.setPublicStatus(State.RELEASED);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        
        Date modDate = sdf.parse("2007-03-21T00:01:00.0+0000");
        Date creationDate = sdf.parse("2007-03-21T00:00:00.0+0000");
        
        item.getVersion().setModificationDate(modDate);
        item.getVersion().setLastMessage("submit test");
        item.getVersion().setVersionNumber(1);
        item.getVersion().setPid("hdl:someHandle/test/escidoc:21280:1");
        item.setLatestVersion(new ItemRO("escidoc:441"));
        item.getLatestVersion().setModificationDate(modDate);
        item.getLatestVersion().setVersionNumber(1);
        item.setLatestRelease(new ItemRO("escidoc:441"));
        item.getLatestRelease().setModificationDate(modDate);
        item.getLatestRelease().setVersionNumber(1);
        item.getLatestRelease().setPid("hdl:someHandle/test/escidoc:21280:1");

        item.setCreationDate(creationDate);
        item.setLockStatus(LockStatus.UNLOCKED);
        item.setOwner(new AccountUserRO("escidoc:user1"));

        // Metadata
        MdsPublicationVO mds = new MdsPublicationVO();
        TextVO title = new TextVO();
        title.setLanguage("en");
        title.setValue("PubMan: The first of all.");
        mds.setTitle(title);
        mds.setGenre(Genre.BOOK);
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.AUTHOR);
        PersonVO person = new PersonVO();
        person.setGivenName("Hans");
        person.setFamilyName("Meier");
        person.setCompleteName("Hans Meier");
        creator.setPerson(person);
        mds.getCreators().add(creator);
        item.setMetadata(mds);
        // PubCollectionRef
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId("escidoc:persistent3");
        item.setContext(collectionRef);
        // PubFile
        FileVO file = new FileVO();
        file.setReference(new FileRO("escidoc:442"));
        file.setContent("/ir/item/escidoc:441/components/component/escidoc:442/content");
        file.setContentCategory("abstract");
        file.setVisibility(Visibility.PUBLIC);
        file.setName("farbtest.gif");
        file.setDescription("Ein Farbtest.");
        file.setPid("PIDBLA");
        file.setStorage(Storage.INTERNAL_MANAGED);
        
        logger.debug("modDate: " + modDate);
        
        file.setLastModificationDate(modDate);
        file.setCreationDate(creationDate);
        file.setStorage(Storage.INTERNAL_MANAGED);
        item.getFiles().add(file);
        return item;
    }

    /**
     * Delivers a well-defined List<SearchHitVO>.
     * 
     * @return The well-defined List<SearchHitVO>
     * @throws UnsupportedEncodingException
     */
    private List<SearchHitVO> getExpectedSearchHitList() throws UnsupportedEncodingException
    {
        List<SearchHitVO> result = new ArrayList<SearchHitVO>();

        SearchHitVO hitVO1 = new SearchHitVO();

        hitVO1.setType(SearchHitType.FULLTEXT);
        hitVO1.setHitReference(new FileRO("escidoc:442"));

        TextFragmentVO tf = new TextFragmentVO();
        String textFragmentData = "„Schönen guten Tag“, antwortete der kleine Prinz, der sich umdrehte, aber nichts sah.";
        tf.setData(textFragmentData);
        // convert the string to UTF-8
        // FrM: Nie, nie, nie, nie, nie, nie machen!
        // byte[] utf8textFragmentData = textFragmentData.getBytes("UTF8");
        // tf.setData(new String(utf8textFragmentData));

        HitwordVO hw = new HitwordVO();
        hw.setStartIndex(1);
        hw.setEndIndex(5);
        tf.getHitwordList().add(hw);
        hw = new HitwordVO();
        hw.setStartIndex(6);
        hw.setEndIndex(9);
        tf.getHitwordList().add(hw);
        hitVO1.getTextFragmentList().add(tf);

        tf = new TextFragmentVO();
        // convert the string to UTF-8
        tf.setData("Der Fuchs schien aufgeregt: „Auf einem\n„Ja“\n„Gibt es Jäger auf deinem Planeten?“\n" + "„Nein“\n„Das ist interessant. Und Hühner?“");
        hw = new HitwordVO();
        hw.setStartIndex(8);
        hw.setEndIndex(14);
        tf.getHitwordList().add(hw);
        hw = new HitwordVO();
        hw.setStartIndex(19);
        hw.setEndIndex(26);
        tf.getHitwordList().add(hw);
        hitVO1.getTextFragmentList().add(tf);

        result.add(hitVO1);

        SearchHitVO hitVO2 = new SearchHitVO();

        hitVO2.setType(SearchHitType.METADATA);

        tf = new TextFragmentVO();
        tf.setData("<escidoc:pseudonym xml:lang=\"it\">Humphrey C.</escidoc:pseudonym>\n" + "<escidoc:pseudonym xml:lang=\"it\">Iklos Makaba</escidoc:pseudonym>\n" + "<escidoc:organization>\n"
                + "<escidoc:organization-name xml:lang=\"fr\">\n" + "Facultad de Ciencias");
        hw = new HitwordVO();
        hw.setStartIndex(1);
        hw.setEndIndex(5);
        tf.getHitwordList().add(hw);
        hw = new HitwordVO();
        hw.setStartIndex(6);
        hw.setEndIndex(9);
        tf.getHitwordList().add(hw);
        hitVO2.getTextFragmentList().add(tf);

        tf = new TextFragmentVO();
        tf.setData("<escidoc:pseudonym xml:lang=\"it\">Humphrey C.</escidoc:pseudonym>\n" + "<escidoc:pseudonym xml:lang=\"it\">Iklos Makaba</escidoc:pseudonym>\n" + "<escidoc:organization>\n"
                + "<escidoc:organization-name xml:lang=\"fr\">\n" + "Facultad de Ciencias");
        hw = new HitwordVO();
        hw.setStartIndex(8);
        hw.setEndIndex(14);
        tf.getHitwordList().add(hw);
        hw = new HitwordVO();
        hw.setStartIndex(19);
        hw.setEndIndex(26);
        tf.getHitwordList().add(hw);
        hitVO2.getTextFragmentList().add(tf);

        result.add(hitVO2);

        return result;
    }
}
