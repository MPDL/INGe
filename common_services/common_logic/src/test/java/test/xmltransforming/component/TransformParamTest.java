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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test class for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TransformParamTest extends TestBase
{
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * Test for {@link XmlTransforming#transformToTaskParam(TaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToTaskParam() throws Exception
    {
        logger.info("### testTransformToTaskParam ###");
        String expectedXML = "last-modification-date=\"1967-08-06T12:34:56.000Z\"/";

        GregorianCalendar cal = new GregorianCalendar(1967, Calendar.AUGUST, 06, 12, 34, 56);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        Date date = cal.getTime();
        TaskParamVO taskVO = new TaskParamVO(date);

        String xmlparam = xmlTransforming.transformToTaskParam(taskVO);
        assertNotNull( "XML Tranforming returns a null object!", xmlparam ); 
        logger.debug("TaskParam: " + xmlparam);
        logger.debug("Expected: " + expectedXML);
        
        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamCreatorAndState() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamCreatorAndState ###");
        String expectedXML = "<filter name=\"http://escidoc.de/core/01/structural-relations/created-by\">escidoc:user1</filter>";
        String expectedXML2 = "<filter name=\"http://escidoc.de/core/01/properties/version/status\">pending</filter>";    
            
        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter f1 = filter.new OwnerFilter(new AccountUserRO("escidoc:user1"));
        Filter f2 = filter.new ItemStatusFilter(State.PENDING);
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        assertNotNull( "XML Tranforming returns a null object!", xmlparam );
        logger.debug("OwnerFilter + PubItemStatusFilter: " + xmlparam + "\n" + "Expected: " + expectedXML);
        
        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        
        if( xmlparam.indexOf( expectedXML2 ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithState() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithState ###");
        String expectedXML = "<filter name=\"http://escidoc.de/core/01/properties/version/status\">pending</filter>";
        String expectedXML2 = "<filter name=\"http://escidoc.de/core/01/properties/version/status\">submitted</filter>";

        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter f1 = filter.new ItemStatusFilter(State.PENDING);
        Filter f2 = filter.new ItemStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("PubItemStatusFilter + PubItemStatusFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);
        
        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        if( xmlparam.indexOf( expectedXML2 ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithCreator() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithCreator ###");
        String expectedXML = "<filter name=\"http://escidoc.de/core/01/structural-relations/created-by\">escidoc:user1</filter>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();

        Filter filter = filterParam.new OwnerFilter(new AccountUserRO("escidoc:user1"));
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("OwnerFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithIdList() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithIdList ###");
        String expectedXML = "<id>escidoc:1</id>";
        String expectedXML2 = "<id>escidoc:2</id>";
        String expectedXML3 = "<id>escidoc:3</id>";
                
            
        FilterTaskParamVO filter = new FilterTaskParamVO();
        ItemRefFilter f1 = filter.new ItemRefFilter();
        f1.getIdList().add(new ItemRO("escidoc:1"));
        f1.getIdList().add(new ItemRO("escidoc:2"));
        f1.getIdList().add(new ItemRO("escidoc:3"));
        filter.getFilterList().add(f1);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("ItemRefFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        if( xmlparam.indexOf( expectedXML2 ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        if( xmlparam.indexOf( expectedXML3 ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithRole() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithRole ###");
        String expectedXML = "<filter name=\"role\">Depositor</filter>";
        String expectedXML2 = "<filter name=\"user\">objectId4711</filter>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        Filter filter = filterParam.new RoleFilter("Depositor", new AccountUserRO("objectId4711"));
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("RoleFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
        if( xmlparam.indexOf( expectedXML2 ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithType() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithType ###");
        String expectedXML = "<filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">escidoc:persistent4</filter>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        Filter filter = filterParam.new FrameworkItemTypeFilter("escidoc:persistent4");
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("FrameworkItemTypeFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);
        
        if( xmlparam.indexOf( expectedXML ) > 0 ) {
            // data is okay
        }
        else {
            // data is not okay, fail test
            fail( "Received data is wrong!");
        }
    }

}
