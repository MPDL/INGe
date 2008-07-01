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
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContainerRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test of {@link ContainerTransforming} methods for transforming and integration with common_logic and the framework.
 * 
 * @author Wilhelm Frank (initial creation)
 * @author $Author: wfrank $ (last change)
 */
public class TransformContainerIntegrationTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming;
    private AccountUserVO user;
    private String userHandle;
    private static String TEST_FILE_ROOT = "xmltransforming/integration/transformContainerIntegrationTest/";
    private static String CONTAINER_FILE = TEST_FILE_ROOT + "container_with_members.xml";
    private static final String CONTAINER_SCHEMA_FILE = "xsd/soap/container/0.7/container.xsd";
    private static final String CONTAINER_LIST_SCHEMA_FILE = "xsd/soap/container/0.7/container-list.xsd";
    private static final String JPG_FARBTEST_FILE = TEST_FILE_ROOT + "farbtest_wasserfarben.jpg";
    
    private static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
    private static final String PREDICATE_FEDORARELATIONSHIP = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#fedoraRelationship";
    private static final String PREDICATE_ISMEMBEROF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isMemberOf";

    private static final String WITHDRAWAL_COMMENT = "Withdrawal comment";

    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // TODO FrM: Wech
        // xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
        xmlTransforming = new XmlTransformingBean();
    }

    /**
     * Logs in as depositor and retrieves his grants (before every single test method).
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // get user handle for user "test_dep_scientist"
        userHandle = loginSystemAdministrator();
        // use this handle to retrieve user "escidoc:user1"
        String userXML = ServiceLocator.getUserAccountHandler(userHandle).retrieve("escidoc:user1");
        // transform userXML to AccountUserVO
        user = xmlTransforming.transformToAccountUser(userXML);
        String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(user.getReference().getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = user.getGrants();
        for (GrantVO grant : grants)
        {
            userGrants.add(grant);
        }
        user.setHandle(userHandle);
    }

    /**
     * Logs out (after every single test method).
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
    }

    /**
     * Tests the transformation of container[XML] to ContainerVO with a self-created container retrieved from
     * the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainerWithMembers() throws Exception
    {
        logger.debug("### testTransformToContainerWithMembers1 ###");
        // read container[XML] from file
        String containerPreCreate = readFile(CONTAINER_FILE);
        logger.debug("containerPreCreate(XML) =" + containerPreCreate);
        // create the container in the framework, framework gives back the created container
        String containerPostCreate = ServiceLocator.getContainerHandler(userHandle).create(containerPreCreate);
        logger.info("containerPostCreate: " + toString(getDocument(containerPostCreate, false), false));
        // transform the container given back by the framework to a ContainerVO
        ContainerVO container = xmlTransforming.transformToContainer(containerPostCreate);
        // check results
        String expectedObjid = getObjid(containerPostCreate);
        assertEquals(expectedObjid, container.getVersion().getObjectId());
        assertEquals(1, container.getVersion().getVersionNumber());
        assertEquals(ContainerVO.State.PENDING, container.getVersion().getState());
        assertEquals(null, container.getPid());
        assertNotNull(container.getVersion().getModificationDate());
        assertEquals("escidoc:42108", container.getContext().getObjectId());
        assertEquals("escidoc:user42", container.getOwner().getObjectId());
        assertTrue(2 == container.getMembers().size());
        MdsPublicationVO md = container.getMetadata();
    }

    /**
     * Tests the transformation of container[XML] (with members) to containerVO with a self-created container retrieved from
     * the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainerWithMembers2() throws Exception
    {
        logger.debug("### testTransformToContainerWithMembers2 ###");
        // read container[XML] from file
        String containerXMLPreCreate = readFile(CONTAINER_FILE);
        logger.info("container[XML] read from file.");
        // create the container in the framework, framework gives back the created container
        String containerXMLPostCreate = ServiceLocator.getContainerHandler(userHandle).create(containerXMLPreCreate);
        logger.info("container[XML] created in the framework.");
        assertNotNull(containerXMLPostCreate);
        logger.debug("container(XML) (after creation) =" + containerXMLPostCreate);
        // use the container given back by the framework to test the transforming
        long zeit = -System.currentTimeMillis();
        ContainerVO containerVO = xmlTransforming.transformToContainer(containerXMLPostCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToContainer()->" + zeit + "ms");
        logger.info("Transformed returned container to ContainerVO.");
        // check results
        assertNotNull(containerVO);
        if (containerVO.getContext() != null)
        {
            logger.debug("containerVO.getContext().getObjectId(): " + containerVO.getContext().getObjectId());
        }
        if (containerVO.getVersion() != null)
        {
            logger.debug("containerVO.getVersion().getObjectId(): " + containerVO.getVersion().getObjectId());
        }
    }

    /**
     * Tests the transformation of container[XML] (containing two members) to containerVO with a self-created container retrieved
     * from the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainerWithOneMembersCreate() throws Exception
    {
        logger.debug("### testTransformToContainerWithOneMembersCreate ###");
        // create a new container with one member using framework_access directly
        // create new itemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)

        logger.info("Content: " + fileVO.getContent());

        fileVO.setContentCategory("post-print");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        //fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem() (with component)->" + zeit + "ms");
        logger.info("PubItemVO with file transformed to item(XML) for create.");
        logger.info("item(XML) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("Response from framework =" + pubItemXMLPostCreate);
        //create new ItemRO
        ItemRO member = new ItemRO(getObjid(pubItemXMLPostCreate));
        
        //create new container
        ContainerVO containerVOPreCreate = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:42108");
        containerVOPreCreate.setContext(ctx);
        containerVOPreCreate.setContentModel("escidoc:ex4");
        
        MdsPublicationVO mds = getMdsPublication1();
        containerVOPreCreate.setMetadata(mds);
        containerVOPreCreate.getMembers().add((ReferenceObject)member);
        
        // transform the ContainerVO into an container (for create)
        zeit = -System.currentTimeMillis();
        String containerXMLPreCreate = xmlTransforming.transformToContainer(containerVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToContainer() (with member)->" + zeit + "ms");
        logger.info("ContainerVO with member transformed to container(XML) for create.");
        logger.info("container(XML) =" + containerXMLPreCreate);
        // create the container in the framework
        String containerXMLPostCreate = ServiceLocator.getContainerHandler(userHandle).create(containerXMLPreCreate);
        assertNotNull(containerXMLPostCreate);
        logger.info("container(XML) created in the framework.");
        logger.debug("Container objid: " + getObjid(containerXMLPostCreate));
        logger.debug("Response from framework =" + containerXMLPostCreate);
    }

    /**
     * Creates a container with a member in the framework and updates the container.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainerWithOneMemberUpdate() throws Exception
    {
        logger.debug("### testTransformToContainerWithOneMemberUpdate ###");
        // create a new container with one member using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("supplementary-material");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        //fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        logger.info("PubItemVO with file transformed to item(XML) for create." + "\nContentItem() (item after transformation from PubItemVO) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostCreate) + "\nResponse from framework =" + pubItemXMLPostCreate);
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Create: Returned item transformed back to PubItemVO.");
        if (pubItemVOPostCreate.getVersion() != null)
        {
            logger.debug("pubItemVOPostCreate.getVersion().getObjectId() (objid): " + pubItemVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            fail("pubItemVOPostCreate.getVersion() is null!");
        }
        
        logger.debug("pubItemVOPostCreate.getModificationDate(): " + pubItemVOPostCreate.getVersion().getModificationDate());
        
      //create new ItemRO
        ItemRO member = new ItemRO(getObjid(pubItemXMLPostCreate));
        
        //create new container
        ContainerVO containerVOPreCreate = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:42108");
        containerVOPreCreate.setContext(ctx);
        containerVOPreCreate.setContentModel("escidoc:ex4");
        
        MdsPublicationVO mds = getMdsPublication1();
        containerVOPreCreate.setMetadata(mds);
        containerVOPreCreate.getMembers().add((ReferenceObject)member);
        
        // transform the ContainerVO into an container (for create)
        long zeit = -System.currentTimeMillis();
        String containerXMLPreCreate = xmlTransforming.transformToContainer(containerVOPreCreate);
        zeit += System.currentTimeMillis();
        // create the container in the framework
        String containerXMLPostCreate = ServiceLocator.getContainerHandler(userHandle).create(containerXMLPreCreate);
        assertNotNull(containerXMLPostCreate);
        // transform the returned container to a ContainerVO
        ContainerVO containerVOPostCreate = xmlTransforming.transformToContainer(containerXMLPostCreate);
        logger.debug("Create: Returned container transformed back to ContainerVO.");
        if (containerVOPostCreate.getVersion() != null)
        {
            logger.debug("containerVOPostCreate.getVersion().getObjectId() (objid): " + containerVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            fail("containerVOPostCreate.getVersion() is null!");
        }
        
        logger.debug("containerVOPostCreate.getModificationDate(): " + containerVOPostCreate.getVersion().getModificationDate());
        // transform the ContainerVO into a container again
        String containerXMLPreUpdate = xmlTransforming.transformToContainer(containerVOPostCreate);
        String id = getObjid(containerXMLPreUpdate);
        logger.info("ContainerVO transfored back to container(XML) for update." + "\nContainer() =\n######\n" + containerXMLPreUpdate + "\n######\nContainer id: " + id);
        // update the container in the framework
        logger.info("Trying to update the container in the framework...");

        logger.debug("containerXMLPreUpdate: " + containerXMLPreUpdate);

        String containerXMLPostUpdate = ServiceLocator.getContainerHandler(userHandle).update(id, containerXMLPreUpdate);
        assertNotNull(containerXMLPostUpdate);
        logger.info("container(XML) updated in the framework." + "\nContainer objid: " + getObjid(containerXMLPostUpdate) + "\nResponse from framework =\n######\n" + containerXMLPostUpdate + "\n######\n");
        // transform the returned container to a ContainerVO
        ContainerVO containerVOPostUpdate = xmlTransforming.transformToContainer(containerXMLPostUpdate);
        logger.debug("Update: Returned container transformed back to ContainerVO.");
        // check results
        assertTrue(containerVOPostUpdate.getLatestVersion().getVersionNumber() >= 1);        
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication1 = containerVOPreCreate.getMetadata();
        MdsPublicationVO mdsPublication2 = containerVOPostUpdate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        for (String diff : oc.getDiffs())
        {
            logger.error(diff);
        }
        assertEquals("This problem should disappear with the resolution of FIZ bug #288", 0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }


    /**
     * Checks whether the transforming of an item with relations works properly.
     * 
     * @throws Exception
     */
    @Test
    public void testRoundtripWithRelations() throws Exception
    {
        // create a "target" item
        PubItemVO targetItemPreCreate = getPubItemWithoutFiles();
        String targetItemPreCreateXml = xmlTransforming.transformToItem(targetItemPreCreate);
        String targetItemPostCreateXml = ServiceLocator.getItemHandler(userHandle).create(targetItemPreCreateXml);
        PubItemVO targetItemPostCreate = xmlTransforming.transformToPubItem(targetItemPostCreateXml);
        ItemRO targetItemRef = targetItemPostCreate.getVersion();

        // create a "source" container and add some fancy relations to the "target" item
        ContainerVO sourceContainerPreCreate = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:42108");
        sourceContainerPreCreate.setContext(ctx);
        sourceContainerPreCreate.setContentModel("escidoc:ex4");
        MdsPublicationVO mds = getMdsPublication1();
        sourceContainerPreCreate.setMetadata(mds);
        
        List<ItemRelationVO> sourceItemRelations = sourceContainerPreCreate.getRelations();
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_ISREVISIONOF, targetItemRef));
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_FEDORARELATIONSHIP, targetItemRef));
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_ISMEMBEROF, targetItemRef));

        // validate and create the "source" container
        String sourceContainerPreCreateXml = xmlTransforming.transformToContainer(sourceContainerPreCreate);
        logger.debug("The source container (with relations):" + sourceContainerPreCreateXml);
        assertXMLValid(sourceContainerPreCreateXml);
        String sourceContainerPostCreateXml = ServiceLocator.getContainerHandler(userHandle).create(sourceContainerPreCreateXml);

        // transform back to ContainerVO
        ContainerVO sourceContainerPostCreate = xmlTransforming.transformToContainer(sourceContainerPostCreateXml);
        assertNotNull(sourceContainerPostCreate);

        // check relations
        List<ItemRelationVO> relations = sourceContainerPostCreate.getRelations();
        assertEquals(3, relations.size());
        int containsExpectedRelations = 0;
        for (ItemRelationVO relation : relations)
        {
            if (relation.getType().equals(PREDICATE_ISREVISIONOF))
            {
                containsExpectedRelations |= 1;
            }
            if (relation.getType().equals(PREDICATE_FEDORARELATIONSHIP))
            {
                containsExpectedRelations |= 2;
            }
            if (relation.getType().equals(PREDICATE_ISMEMBEROF))
            {
                containsExpectedRelations |= 4;
            }
        }
        assertEquals(7, containsExpectedRelations);

    }


    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToContainerList(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainerList() throws Exception
    {
        logger.debug("### testTransformToContainerList ###");
        // first container: create a minimal ContainerVO from scratch and transform it to an container(XML)
        ContainerVO containerVO = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:42108");
        containerVO.setContext(ctx);
        containerVO.setContentModel("escidoc:ex4");
        MdsPublicationVO mds = getMdsPublication1();
        containerVO.setMetadata(mds);
        String container1 = xmlTransforming.transformToContainer(containerVO);
        logger.debug("container1 created from scratch and transformed to XML.");
        assertNotNull(container1);
        // second container: read container[XML] from file
        String container2 = readFile(CONTAINER_FILE);
        logger.debug("container2(XML) read from file.");
        assertNotNull(container2);
        // create the two containers in the framework, remember object ids
        String container1Response = ServiceLocator.getContainerHandler(userHandle).create(container1);
        String objid1 = getObjid(container1Response);
        logger.debug("container1 created. objid: " + objid1);
        String container2Response = ServiceLocator.getContainerHandler(userHandle).create(container2);
        String objid2 = getObjid(container2Response);
        logger.debug("container2 created. objid: " + objid2);
        // retrieve the two containers from the framework using a FilterTaskParamVO
        FilterTaskParamVO filter = new FilterTaskParamVO();
        ItemRefFilter f1 = filter.new ItemRefFilter();
        f1.getIdList().add(new ItemRO(objid1));
        f1.getIdList().add(new ItemRO(objid2));
        filter.getFilterList().add(f1);
        String filterXML = xmlTransforming.transformToFilterTaskParam(filter);
        //filterXML = filterXML.replace("\n", "");
        // temporarelly using filter string, because FIZ very special parsing does not allow white spaces at certain places.
        String filterTMP = "<param><filter name=\"http://purl.org/dc/elements/1.1/identifier\"><id>"+objid1+"</id><id>"+objid2+"</id></filter></param>";
        logger.debug("Used filter to retrieve the containers: \n" + filterXML);
        String containerListXML = ServiceLocator.getContainerHandler(userHandle).retrieveContainers(filterTMP);
        logger.debug(containerListXML);
        assertXMLValid(containerListXML);
        List<? extends ContainerVO> containerList = xmlTransforming.transformToContainerList(containerListXML);
        assertNotNull(containerList);
    }

}
