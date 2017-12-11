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

package de.mpg.mpdl.inge.model.valueobjects.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

/**
 * Base class for Comparator tests.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ Revised by BrP: 03.09.2007
 */
public class ComparatorTestBase {
  /**
   * Gets a list of 4 PubItemVOs.
   * 
   * @return List of PubItemVOs
   */
  protected ArrayList<PubItemVO> getPubItemList() {
    ArrayList<PubItemVO> list = new ArrayList<PubItemVO>();
    list.add(getPubItemVO1());
    list.add(getPubItemVO1());
    list.add(getPubItemVO2());
    list.add(getPubItemVO3());
    list.add(getPubItemVO4());
    return list;
  }

  /**
   * Gets a PubItemVO with id 1.
   * 
   * @return PubItemVO
   */
  protected PubItemVO getPubItemVO1() {
    PubItemVO item = new PubItemVO();
    // State
    item.getVersion().setState(ItemVO.State.PENDING);
    // RO
    ItemRO ref = new ItemRO();
    ref.setObjectId("1");
    item.setVersion(ref);
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setTitle("The first of all.");
    mds.setGenre(Genre.BOOK);
    mds.setDateCreated("2005-2-1");
    mds.setDatePublishedInPrint("2006-2-1");
    PublishingInfoVO pubInfo = new PublishingInfoVO();
    pubInfo.setPublisher("O'Reilly Media Inc., 1005 Gravenstein Highway North, Sebastopol");
    mds.setPublishingInfo(pubInfo);
    mds.setReviewMethod(ReviewMethod.INTERNAL);
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
    collectionRef.setObjectId("/ir/context/escidoc:persistent3");
    item.setContext(collectionRef);
    // Source
    SourceVO source = new SourceVO();
    source.getCreators().add(creator);
    source.setTitle("jaas verstehen.");
    mds.getSources().add(source);
    // Event
    EventVO event = new EventVO();
    event.setTitle("Another amazing conference");
    mds.setEvent(event);
    return item;
  }

  /**
   * Gets a PubItemVO with id 2.
   * 
   * @return PubItemVO
   */
  protected PubItemVO getPubItemVO2() {
    PubItemVO item = new PubItemVO();
    // State
    item.getVersion().setState(ItemVO.State.PENDING);
    // RO
    ItemRO ref = new ItemRO();
    ref.setObjectId("2");
    item.setVersion(ref);
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setTitle("Der Zweite.");
    mds.setGenre(Genre.ARTICLE);
    mds.setDateCreated("2006-5-19");
    mds.setDatePublishedInPrint("2007-1-14");
    PublishingInfoVO pubInfo = new PublishingInfoVO();
    pubInfo.setPublisher("Software & Support Verlag GmbH");
    mds.setPublishingInfo(pubInfo);
    mds.setReviewMethod(ReviewMethod.PEER);
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.EDITOR);
    OrganizationVO org = new OrganizationVO();
    org.setName("Max Planck Society");
    creator.setOrganization(org);
    mds.getCreators().add(creator);
    item.setMetadata(mds);
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId("/ir/context/escidoc:persistent3");
    item.setContext(collectionRef);
    // Source
    SourceVO source = new SourceVO();
    source.getCreators().add(creator);
    source.setTitle("Java und XML");
    mds.getSources().add(source);
    // Event
    EventVO event = new EventVO();
    event.setTitle("amazing conference");
    mds.setEvent(event);
    return item;
  }

  /**
   * Gets a PubItemVO with id 3.
   * 
   * @return PubItemVO
   */
  protected PubItemVO getPubItemVO3() {
    PubItemVO item = new PubItemVO();
    // State
    item.getVersion().setState(ItemVO.State.PENDING);
    // RO
    ItemRO ref = new ItemRO();
    ref.setObjectId("3");
    item.setVersion(ref);
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setTitle("der dritte");
    mds.setGenre(Genre.BOOK_ITEM);
    mds.setDateCreated("2001-3-11");
    mds.setDatePublishedInPrint("2007-1-13");
    PublishingInfoVO pubInfo = new PublishingInfoVO();
    pubInfo.setPublisher("Software & Support Verlag");
    mds.setPublishingInfo(pubInfo);
    mds.setReviewMethod(ReviewMethod.NO_REVIEW);
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.COMMENTATOR);
    PersonVO person = new PersonVO();
    person.setFamilyName("max planck");
    person.setCompleteName("Hans Max Planck");
    creator.setPerson(person);
    mds.getCreators().add(creator);
    CreatorVO creator2 = new CreatorVO();
    creator2.setRole(CreatorRole.EDITOR);
    OrganizationVO org = new OrganizationVO();
    org.setName("MPDL");
    creator2.setOrganization(org);
    mds.getCreators().add(creator2);
    item.setMetadata(mds);
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId("/ir/context/escidoc:persistent3");
    item.setContext(collectionRef);
    // Source
    SourceVO source = new SourceVO();
    source.getCreators().add(creator);
    source.getCreators().add(creator2);
    source.setTitle("Javamagazin xxx");
    mds.getSources().add(source);
    // Event
    EventVO event = new EventVO();
    event.setTitle("W-Jax München 2006");
    mds.setEvent(event);
    return item;
  }

  /**
   * Gets a PubItemVO with id 4.
   * <P>
   * This is a minimal PubItemVO.
   * 
   * @return PubItemVO
   */
  protected PubItemVO getPubItemVO4() {
    PubItemVO item = new PubItemVO();
    // State
    item.getVersion().setState(ItemVO.State.PENDING);
    // RO
    ItemRO ref = new ItemRO();
    ref.setObjectId("4");
    item.setVersion(ref);
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    mds.setTitle("Und noch ein armseliges Ding");
    mds.setGenre(Genre.OTHER);
    CreatorVO creator = new CreatorVO();
    creator.setRole(CreatorRole.EDITOR);
    OrganizationVO org = new OrganizationVO();
    org.setName("MPDL");
    creator.setOrganization(org);
    mds.getCreators().add(creator);
    item.setMetadata(mds);
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId("/ir/context/escidoc:persistent3");
    item.setContext(collectionRef);
    return item;
  }

  /**
   * Checks the order of elements from the list against the array of ids.
   * 
   * @param pubItemList
   * @param idList
   */
  protected void assertObjectIdOrder(List<PubItemVO> pubItemList, String[] idList) {
    assertEquals(pubItemList.size(), idList.length);
    for (int i = 0; i < pubItemList.size(); i++) {
      PubItemVO pubItem = pubItemList.get(i);
      String id = idList[i];
      assertEquals(id, pubItem.getVersion().getObjectId());
    }
  }
}
