/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingTestBase;

/**
 * Test cases for the method transformToPubItem of the interface XmlTransforming. The main purpose
 * of the test methods in this class is to check the handling of incomplete or invalid PubItemVO by
 * the {@link XmlTransforming} component.
 * 
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TransformInvalidPubItemTest extends XmlTransformingTestBase {
  private static final Logger logger = LogManager.getLogger(TransformInvalidPubItemTest.class);

  /**
   * @throws Exception
   */
  @Test
  public void transformInvalidPubItemToXML() throws Exception {
    PubItemVO pubItemVO;
    String pubItemXML;

    pubItemVO = getEmptyPubItemVO();
    pubItemXML = XmlTransformingService.transformToItem(pubItemVO);
    assertNotNull(pubItemXML);
    logger.debug(pubItemXML);

    pubItemVO = getSmallPubItemVOWithStateAndEmptyMdsAndMinimalCollection();
    pubItemXML = XmlTransformingService.transformToItem(pubItemVO);
    assertNotNull(pubItemXML);
    logger.debug(pubItemXML);
  }

  /**
   * Creates an empty PubItemVO.
   * 
   * @return pubItem
   */
  private PubItemVO getEmptyPubItemVO() {
    PubItemVO item = new PubItemVO();
    return item;
  }

  /**
   * Creates a small PubItemVO with a state, an empty MdsPublicationVO and a minimal Collection.
   * 
   * @return pubItem
   */
  private PubItemVO getSmallPubItemVOWithStateAndEmptyMdsAndMinimalCollection() {
    PubItemVO item = new PubItemVO();
    // State
    item.getVersion().setState(ItemVO.State.PENDING);

    // (1) metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    item.setMetadata(mds);

    // (2) pubCollection
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId("/ir/context/escidoc:persistent3");
    item.setContext(collectionRef);

    return item;
  }

  private PubItemVO getMinimalPubItemVO() {
    PubItemVO pubItemVO = new PubItemVO();
    // set collection
    ContextRO pubCollectionRO = new ContextRO();
    pubCollectionRO.setObjectId("escidoc:persistent3");
    pubItemVO.setContext(pubCollectionRO);
    // set a (incomplete) metadata set
    MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
    pubItemVO.setMetadata(mdsPublicationVO);
    return pubItemVO;
  }

  private PubItemVO getPubItemVOWithIncompleteMD() throws Exception {
    ContextRO pubCollection = new ContextRO();
    pubCollection.setObjectId("escidoc:persistent3");

    // AccountUserVO user = getUserTestDepLibWithHandle();

    // PubItemVO pubItem = pubItemDepositing.createPubItem(pubCollection, user); // Variant 1: This
    // is what the
    // presentation does. Throws an authentication exception
    // PubItemVO pubItem = createPubItemLikeDepositing(pubCollection, user); // Variant 2: The same
    // code, copied
    // locally. Works.
    PubItemVO pubItem = getMinimalPubItemVO(); // Variant 3: Use the existing method. Works the
                                               // same.

    initializeItemLikePresentation(pubItem);

    return pubItem;
  }

  private void initializeItemLikePresentation(PubItemVO pubItem) {
    if (pubItem != null) {
      // add PublishingInfoVO if needed to be able to bind uiComponents to it
      if (pubItem.getMetadata().getPublishingInfo() == null) {
        PublishingInfoVO newPublishingInfo = new PublishingInfoVO();
        pubItem.getMetadata().setPublishingInfo(newPublishingInfo);
      }

      // add PersonOrganization if needed to be able to bind uiComponents to it
      for (int i = 0; i < pubItem.getMetadata().getCreators().size(); i++) {
        CreatorVO creatorVO = pubItem.getMetadata().getCreators().get(i);

        if (creatorVO.getPerson() != null && creatorVO.getPerson().getOrganizations().size() == 0) {
          // create a new Organization for this person
          OrganizationVO newPersonOrganization = new OrganizationVO();
          creatorVO.getPerson().getOrganizations().add(newPersonOrganization);
        }
      }

      // add ContentLanguage if needed to be able to bind uiComponents to it
      if (pubItem.getMetadata().getLanguages().size() == 0) {
        pubItem.getMetadata().getLanguages().add(new String());
      }

      if (pubItem.getMetadata().getEvent() == null) {
        EventVO eventVO = new EventVO();
        pubItem.getMetadata().setEvent(eventVO);
      }

      // add Identifier if needed to be able to bind uiComponents to it
      if (pubItem.getMetadata().getIdentifiers().size() == 0) {
        pubItem.getMetadata().getIdentifiers().add(new IdentifierVO());
      }

      // add Abstract if needed to be able to bind uiComponents to it
      if (pubItem.getMetadata().getAbstracts().size() == 0) {
        pubItem.getMetadata().getAbstracts().add(new AbstractVO());
      }
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void transformInvalidPubItemToXML2() throws Exception {
    PubItemVO pubItemVO;
    String pubItemXML;

    pubItemVO = getPubItemVOWithIncompleteMD();
    pubItemXML = XmlTransformingService.transformToItem(pubItemVO);
    assertNotNull(pubItemXML);
    logger.debug(pubItemXML);

    pubItemVO = getSmallPubItemVOWithStateAndEmptyMdsAndMinimalCollection();
    pubItemXML = XmlTransformingService.transformToItem(pubItemVO);
    assertNotNull(pubItemXML);
    logger.debug(pubItemXML);
  }
}
