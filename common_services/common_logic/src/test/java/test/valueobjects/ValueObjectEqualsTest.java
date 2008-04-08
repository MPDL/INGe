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

package test.valueobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import test.TestBase;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * Test cases for the equals methods of value objects.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * Revised by BrP: 03.09.2007
 */
public class ValueObjectEqualsTest extends TestBase
{
    private MdsPublicationVO mds1;
    private MdsPublicationVO mds2;

    /**
     * Creates the test data and is called before each test method.
     */
    @Before
    public void setUp()
    {
        mds1 = getMdsPublication1();
        mds2 = getMdsPublication1();
    }

    /**
     * Tests the two MdsPublicationVOs for equality.
     */
    @Test
    public void equalsMdsPublicationVO()
    {
        assertTrue(mds1.equals(mds2));
        assertTrue(mds2.equals(mds1));
    }

    /**
     * Tests two empty MdsPublicationVOs for equality. 
     */
    @Test
    public void equalsMdsPublicationVOEmpty()
    {
        mds1 = new MdsPublicationVO();
        mds2 = new MdsPublicationVO();
        assertTrue(mds1.equals(mds2));
        assertTrue(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Titles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentTitles()
    {
        mds1.getTitle().setValue(mds1.getTitle().getValue() + "X");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different AlternativeTitles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentAlternativeTitles()
    {
        TextVO altTitle = mds1.getAlternativeTitles().get(mds1.getAlternativeTitles().size() - 1);
        altTitle.setValue(altTitle.getValue() + "X");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of AlternativeTitles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentAlternativeTitleCount()
    {
        TextVO altTitle = mds1.getAlternativeTitles().get(mds1.getAlternativeTitles().size() - 1);
        mds1.getAlternativeTitles().add(altTitle);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different order of AlternativeTitles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentAlternativeTitleOrder()
    {
        Collections.reverse(mds1.getAlternativeTitles());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of Creators for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorsCount()
    {
        CreatorVO creator = mds1.getCreators().get(mds1.getCreators().size() - 1);
        mds1.getCreators().add(creator);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }
    
    /**
     * Tests the two MdsPublicationVOs with different order of Creators for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorsOrder()
    {
        Collections.reverse(mds1.getCreators());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Creator Roles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorRole()
    {
        mds1.getCreators().get(mds1.getCreators().size() - 1).setRole(CreatorRole.PHOTOGRAPHER);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Creator Person Family Names for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorPersonFamilyName()
    {
        mds1.getCreators().get(0).getPerson().setFamilyName("Other Meier");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Creator Person Identifiers for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorPersonIdentifierId()
    {
        mds1.getCreators().get(0).getPerson().getIdentifier().setId("Another Id");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of Creator Person Organizations for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorPersonOrganizations()
    {
        mds1.getCreators().get(0).getPerson().getOrganizations().add(new OrganizationVO());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Creator Person Organization Names for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorPersonOrganizationName()
    {
        TextVO name = new TextVO();
        name.setLanguage("en");
        name.setValue("Another org.");
        mds1.getCreators().get(0).getPerson().getOrganizations().get(0).setName(name);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Creator Person Organization Addresses for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorPersonOrganizationAddress()
    {
        mds1.getCreators().get(0).getPerson().getOrganizations().get(0).setAddress("Another org address.");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));

        mds1.getCreators().get(0).getPerson().getOrganizations().get(0).setAddress(null);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }
    
    /**
     * Tests the two MdsPublicationVOs with different Creator Organization Names for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorOrganizationName()
    {
        TextVO name = new TextVO();
        name.setLanguage("en");
        name.setValue("Another org name.");
        mds1.getCreators().get(1).getOrganization().setName(name);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));

        mds1.getCreators().get(1).getOrganization().setName(null);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }
    
    /**
     * Tests the two MdsPublicationVOs with different Creator Organization Addresses for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorOrganizationAddress()
    {
        mds1.getCreators().get(1).getOrganization().setAddress("Another org address.");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));

        mds1.getCreators().get(1).getOrganization().setAddress(null);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }
    
    /**
     * Tests the two MdsPublicationVOs with different Creator Organization Identifiers for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentCreatorOrganizationIdentifier()
    {
        mds1.getCreators().get(1).getOrganization().setIdentifier("ordIDNew");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));

        mds1.getCreators().get(1).getOrganization().setIdentifier(null);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }


    /**
     * Tests the two MdsPublicationVOs with different Summaries for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSummaries()
    {
        mds1.getAbstracts().add(mds1.getAbstracts().get(0));
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Summary Contents for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSummaryContents()
    {
        mds1.getAbstracts().get(0).setValue("This is a different summary.");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Summary Languages for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSummaryLanguages()
    {
        mds1.getAbstracts().get(0).setLanguage("ch");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Identifiers for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentIdentifiers()
    {
        mds1.getIdentifiers().add(mds1.getIdentifiers().get(0));
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Identifier Types for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentIdentifierType()
    {
        mds1.getIdentifiers().get(0).setType(IdentifierVO.IdType.ESCIDOC);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Identifier orders for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentIdentifierOrder()
    {
        Collections.reverse(mds1.getIdentifiers());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of Identifier for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentNullIdentifier()
    {
        mds1.getIdentifiers().clear();
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Dates for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentDates()
    {
        mds1.setDateAccepted(new Date().toString());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));

        mds1.setDateCreated(new Date().toString());
        mds1.setDateSubmitted(new Date().toString());
        mds1.setDatePublishedInPrint(new Date().toString());
        mds1.setDateModified(new Date().toString());

        assertFalse(mds1.getDateCreated().equals(mds2.getDateCreated()));
        assertFalse(mds2.getDateCreated().equals(mds1.getDateCreated()));

        assertFalse(mds1.getDateSubmitted().equals(mds2.getDateSubmitted()));
        assertFalse(mds2.getDateSubmitted().equals(mds1.getDateSubmitted()));

        assertFalse(mds1.getDatePublishedInPrint().equals(mds2.getDatePublishedInPrint()));
        assertFalse(mds2.getDatePublishedInPrint().equals(mds1.getDatePublishedInPrint()));

        assertFalse(mds1.getDateModified().equals(mds2.getDateModified()));
        assertFalse(mds2.getDateModified().equals(mds1.getDateModified()));
    }

    /**
     * Tests the two MdsPublicationVOs with different Dates one set to null Types for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithNullDates()
    {
        mds1.setDateCreated(null);
        mds1.setDateSubmitted(null);
        mds1.setDatePublishedInPrint(null);
        mds1.setDateModified(null);
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of Sources for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSourceCount()
    {
        mds1.getSources().add(new SourceVO());
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different number of sub Sources for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSourceSource()
    {
        mds1.getSources().get(0).getSources().clear();
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Source Titles for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSourceSourceTitle()
    {
        mds1.getSources().get(0).getSources().get(0).getTitle().setValue("Another source title now.");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two MdsPublicationVOs with different Source Title Languages for equality.
     */
    @Test
    public void equalsMdsPublicationVOWithDifferentSourceSourceTitleLanguage()
    {
        mds1.getSources().get(0).getSources().get(0).getTitle().setLanguage("ch");
        assertFalse(mds1.equals(mds2));
        assertFalse(mds2.equals(mds1));
    }

    /**
     * Tests the two SourceVOs for equality.
     */
    @Test
    public void equalsSourceVO()
    {
        SourceVO source1 = mds1.getSources().get(0);
        SourceVO source2 = mds2.getSources().get(0);

        assertTrue(source1.equals(source2));
        assertTrue(source2.equals(source1));
    }

    /**
     * Tests the two SourceVOs with different EndPages for equality.
     */
    @Test
    public void equalsSourceVOWithDifferentEndPage()
    {
        SourceVO source1 = mds1.getSources().get(0);
        SourceVO source2 = mds2.getSources().get(0);
        source1.setEndPage("XXXl.987");
        assertFalse(source1.equals(source2));
        assertFalse(source2.equals(source1));

    }

    /**
     * Tests the two SourceVOs with different Issues for equality.
     */
    @Test
    public void equalsSourceVOWithDifferentIssue()
    {
        SourceVO source1 = mds1.getSources().get(0);
        SourceVO source2 = mds2.getSources().get(0);
        source1.setIssue("Another issue now.");
        assertFalse(source1.equals(source2));
        assertFalse(source2.equals(source1));
    }

    /**
     * Tests the two SourceVOs with different Places for equality.
     */
    @Test
    public void equalsSourceVOWithDifferentPlace()
    {
        SourceVO source1 = mds1.getSources().get(0);
        SourceVO source2 = mds2.getSources().get(0);

        source1.getPublishingInfo().setPlace("Another place now.");
        assertFalse(source1.equals(source2));
        assertFalse(source2.equals(source1));

        source1.getPublishingInfo().setPlace(null);
        assertFalse(source1.equals(source2));
        assertFalse(source2.equals(source1));
    }

    /**
     * Tests the two EventVOs for equality.
     */
    @Test
    public void equalsEventVO()
    {
        EventVO event1 = mds1.getEvent();
        EventVO event2 = mds2.getEvent();
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));
    }

    /**
     * Tests the two EventVOs with different Titles for equality.
     */
    @Test
    public void equalsEventVOWithDifferentTitle()
    {
        EventVO event1 = mds1.getEvent();
        EventVO event2 = mds2.getEvent();
        event1.getTitle().setValue("Another event title now.");
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
    }

    /**
     * Tests the two EventVOs with different Title Languages for equality.
     */
    @Test
    public void equalsEventVOWithDifferentTitleLanguage()
    {
        EventVO event1 = mds1.getEvent();
        EventVO event2 = mds2.getEvent();
        event1.getTitle().setValue("ch");
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
    }

    /**
     * Tests the two EventVOs with different StartDates for equality.
     */
    @Test
    public void equalsEventVOWithDifferentStartDate()
    {
        EventVO event1 = mds1.getEvent();
        EventVO event2 = mds2.getEvent();
        event1 = getMdsPublication1().getEvent();
        event1.setStartDate(new Date().toString());
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
    }
}
