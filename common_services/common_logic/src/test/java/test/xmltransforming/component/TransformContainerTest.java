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

package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContainerRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO.State;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for transforming ContainerVOs to XML and back.
 * 
 * @author Wilhelm Frank (initial creation)
 */
public class TransformContainerTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static final String TEST_FILE_ROOT = "xmltransforming/component/transformContainerTest/";
    private static final String RELEASED_CONTAINER_FILE = TEST_FILE_ROOT + "released_container.xml";
    private static final String CONTAINER_LIST_FILE = TEST_FILE_ROOT + "container_list.xml";

    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToContainer(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToContainer() throws Exception
    {
        logger.info("### testTransformToContainer ###");
        //create new item to add to container
        PubItemVO item = getPubItemWithoutFiles();
        ItemRO member = new ItemRO(item.getVersion().getObjectId()); 

        // create a new container
        ContainerVO containerVO = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:ex1");
        containerVO.setContext(ctx);
        containerVO.setContentModel("escidoc:ex4");
        
        MdsPublicationVO mds = getMdsPublication1();
        containerVO.getMetadataSets().add(mds);
        containerVO.getMembers().add((ReferenceObject) member);

        // transform the ContainerVO
        long zeit = -System.currentTimeMillis();
        String containerXML = xmlTransforming.transformToContainer(containerVO);
        zeit += System.currentTimeMillis();
        logger.info("transformToContainer() " + zeit + "ms");
        logger.info("ContainerVO transformed to container(XML).");
        logger.debug("container(XML) =" + containerXML);
        // is container[XML] valid according to container.xsd?
        assertXMLValid(containerXML);

        // transform the container(XML) back to a ContainerVO
        ContainerVO newVO = xmlTransforming.transformToContainer(containerXML);
        // compare with original PubItemVO
        ObjectComparator oc = new ObjectComparator(containerVO, newVO);
        for (String diff : oc.getDiffs())
        {
            logger.info(diff);
        }

    }

    /**
     * Test of {@link XmlTransforming#transformToContainerList(List)}.
     * 
     * @throws Exception
     */

    @Test
    public void testTransformContainerVOListToContainerListXML() throws Exception
    {
        logger.info("### testTransformContainerVOListToContainerListXML ###");

        // create a List<ContainerVO> from scratch.
        List<ContainerVO> containerList = new ArrayList<ContainerVO>();
        ContainerVO container;
        for (int i = 0; i < 5; i++)
        {
            container = new ContainerVO();
            ContextRO ctx = new ContextRO();
            ctx.setObjectId("escidoc:ex1");
            container.setContext(ctx);
            container.setContentModel("escidoc:ex4");
            MdsPublicationVO mds = getMdsPublication1();
            container.getMetadataSets().add(mds);
            containerList.add(container);
        }

        // transform the ContainerVO
        long zeit = -System.currentTimeMillis();
        String containerListXML = xmlTransforming.transformToContainerList(containerList);
        zeit += System.currentTimeMillis();
        logger.info("transformToContainerList()-> " + zeit + "ms");
        logger.info("List<ContainerVO> transformed to container-list[XML].");
        logger.debug("container-list[XML] =\n" + containerListXML);

        // check the results
        assertNotNull(containerListXML);
        // is container-list[XML] valid according to container-list.xsd?
        assertXMLValid(containerListXML);
        // does container-list[XML] contain five nodes?
        final String xPath = "//container-list/container";
        Document doc = getDocument(containerListXML, false);
        NodeList list = selectNodeList(doc, xPath);
        assertEquals("container-list does not contain correct number of items", list.getLength(), 5);
    }
    
    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean#transformToContainer(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformReleasedContainerToContainerVOAndCheckPID() throws Exception
    {
        logger.info("### testTransformReleasedContainerToContainerVOAndCheckPID ###");

        // read container[XML] from file
        String releasedContainerXML = readFile(RELEASED_CONTAINER_FILE);
        logger.info("Container[XML] read from file.");
        logger.debug("Content: " + releasedContainerXML);
        // transform the container directly into a ContainerVO
        long zeit = -System.currentTimeMillis();
        ContainerVO containerVO = xmlTransforming.transformToContainer(releasedContainerXML);
        zeit += System.currentTimeMillis();
        logger.info("transformToContainer()-> " + zeit + "ms");
        logger.info("Transformed container to ContainerVO.");

        logger.debug("Last comment: " + containerVO.getVersion().getLastMessage());
        
        // check results
        assertNotNull(containerVO);
        assertNotNull("PID is null!", containerVO.getVersion().getPid());

        assertEquals("hdl:someHandle/test/escidoc:1787", containerVO.getVersion().getPid());
        
    }


    /**
     * Test method for checking the correct transformation of an container-list[XML] to a List<ContainerVO>.
     * 
     * @throws Exception
     */

    @Test
    public void testTransformContainerListXMLToContainerVOList() throws Exception
    {
        logger.info("### testTransformContainerListXMLToContainerVOList ###");

        // read container-list[XML] from file
        String containerListXML = readFile(CONTAINER_LIST_FILE);
        logger.info("container-list[XML] read from file.");
        logger.debug("container-list[XML]:\n" + containerListXML);

        // transform to a list of ContainerVOs
        List<? extends ContainerVO> containerList = xmlTransforming.transformToContainerList(containerListXML);
        assertNotNull(containerList);
    }
    
    @Test
    public void testTransformMemberListToMemberListXML() throws Exception
    {
        PubItemVO item1 = getPubItemWithoutFiles();
        PubItemVO item2 = getPubItemWithoutFiles();
        ItemRO member1 = new ItemRO(item1.getVersion().getObjectId());
        ItemRO member2 = new ItemRO(item2.getVersion().getObjectId());
        
        ContainerVO container = new ContainerVO();
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:ex1");
        container.setContext(ctx);
        container.setContentModel("escidoc:ex4");
        
        MdsPublicationVO mds = getMdsPublication1();
        container.getMetadataSets().add(mds);
        container.getMembers().add((ReferenceObject) member1);
        container.getMembers().add((ReferenceObject) member2);
        
        List<ValueObject> mlist = new ArrayList<ValueObject>();
        mlist.add(item1);
        mlist.add(item2);
        mlist.add(container);
        
        String mlistXML = xmlTransforming.transformToMemberList(mlist);
        logger.info("MemberList<VO> transformed to memberlist(XML).");
        logger.debug("memberlist(XML) =" + mlistXML);
        assertXMLValid(mlistXML);
        
    }

}
