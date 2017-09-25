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

package de.mpg.mpdl.inge.model.xmltransforming;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

/**
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public class DataGatheringService {
  private static final String PREDICATE_ISREVISIONOF =
      "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
  // private static final String PREDICATE_ISMEMBEROF =
  // "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasMember"; //
  // "http://escidoc.de/core/01/structural-relations/member";
  private static final String OUTPUT_FORMAT = "RDF/XML";

  private static final Logger logger = Logger.getLogger(DataGatheringService.class);

  // /**
  // * Separator used for concatenation of affiliation names to one organization name.
  // */
  // public static final String ORGANIZATION_NAME_SEPARATOR = ", ";

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.model.xmltransforming.DataGathering#findRevisionsOfItem(de.mpg.mpdl.inge.model
   * .xmltransforming .referenceobjects.ItemRO)
   */
  public static List<RelationVO> findRevisionsOfItem(String userHandle, ItemRO itemRef)
      throws TechnicalException {
    if (itemRef == null) {
      throw new IllegalArgumentException(DataGatheringService.class.getSimpleName()
          + ".findRevisionsOfItem:itemRef is null");
    }
    String param =
        "<param>" + "<query>* " + PREDICATE_ISREVISIONOF + " &lt;info:fedora/"
            + itemRef.getObjectId() + "&gt;</query>" + "<format>" + OUTPUT_FORMAT + "</format>"
            + "</param>";
    logger.debug("Param=" + param);
    try {

      String result;
      if (userHandle == null) {
        result = ServiceLocator.getSemanticScoreHandler().spo(param);
      } else {
        result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
      }
      List<RelationVO> relations = XmlTransformingService.transformToRelationVOList(result);
      return relations;
    } catch (Exception e) {
      logger.error("Error retrieving revisions.", e);
      throw new TechnicalException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public static List<RelationVO> findParentItemsOfRevision(String userHandle, ItemRO itemRef)
      throws TechnicalException {
    if (itemRef == null) {
      throw new IllegalArgumentException(DataGatheringService.class.getSimpleName()
          + ".findRevisionsOfItem:itemRef is null");
    }
    String param =
        "<param>" + "<query>&lt;info:fedora/" + itemRef.getObjectId() + "&gt; "
            + PREDICATE_ISREVISIONOF + " *</query>" + "<format>" + OUTPUT_FORMAT + "</format>"
            + "</param>";
    logger.debug("Param=" + param);
    try {
      String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
      List<RelationVO> relations = XmlTransformingService.transformToRelationVOList(result);
      return relations;
    } catch (Exception e) {
      logger.error("Error retrieving revisions.", e);
      throw new TechnicalException(e);
    }
  }


  // /**
  // * {@inheritDoc}
  // */
  // public static List<OrganizationVO> createOrganizationListFromAffiliation(final String
  // userHandle,
  // final AffiliationVO affiliation) throws TechnicalException, AffiliationNotFoundException,
  // URISyntaxException {
  // logger.debug("createOrganizationListFromAffiliation(AffiliationVO)");
  // if (affiliation == null) {
  // throw new IllegalArgumentException(DataGatheringService.class.getSimpleName()
  // + ":createOrganizationListFromAffiliation:affiliation is null");
  // }
  //
  // final AffiliationRO affiliationRef = affiliation.getReference();
  // // initialize the result organization list
  // List<OrganizationVO> organizationList = new ArrayList<OrganizationVO>();
  // try {
  // // Get the affiliation paths for the given affiliation from the framework
  // OrganizationalUnitHandler ouHandler =
  // de.mpg.mpdl.inge.framework.ServiceLocator.getOrganizationalUnitHandler(userHandle);
  // String affObjId = affiliationRef.getObjectId();
  // logger
  // .debug("createOrganizationListFromAffiliation(AffiliationVO) - trying to
  // ouHandler.retrievePathList(affObjId) with affObjId="
  // + affObjId);
  // String affiliationPathListXML = ouHandler.retrievePathList(affObjId);
  // logger.debug("createOrganizationListFromAffiliation() - retrieved path list XML=\n"
  // + affiliationPathListXML);
  // List<AffiliationPathVO> affiliationPathVOList =
  // XmlTransformingService.transformToAffiliationPathList(affiliationPathListXML);
  //
  // // cache already retrieved affiliations
  // // every cache entry consists of key:objectId, value: corresponding affiliation
  // // initialize cache with given affiliation
  // Map<String, AffiliationVO> affiliationCache = new HashMap<String, AffiliationVO>();
  // affiliationCache.put(affiliationRef.getObjectId(), affiliation);
  // // loop through the list of affiliation paths; every affiliation path yields one
  // // OrganizationVO
  // for (AffiliationPathVO affPathVO : affiliationPathVOList) {
  // // create a new OrganizationVO
  // OrganizationVO newOrg = new OrganizationVO();
  //
  // // create and set organization ADDRESS (to the address of the given affiliation)
  // StringBuffer address = new StringBuffer();
  //
  //
  // // TODO FrM: Adapt this to new AffiliationVO structure
  //
  // // appendAddressPart(affiliation.getAddress(), address);
  // // appendAddressPart(affiliation.getPostcode(), address);
  //
  // if (affiliation.getMetadataSets().size() > 0
  // && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
  // MdsOrganizationalUnitDetailsVO details =
  // (MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0);
  // appendAddressPart(details.getCity(), address);
  // appendAddressPart(details.getCountryCode(), address);
  // }
  //
  // if (address.length() > 0) {
  // newOrg.setAddress(address.toString());
  // }
  //
  // // create and set organization ID (to the pid of the given affiliation)
  // if (affiliation.getReference() != null) {
  // newOrg.setIdentifier(affiliation.getReference().getObjectId());
  // }
  //
  // // create and set organization NAME (to the concatenated list of organization names in the
  // // affiliation
  // // path)
  // // loop through the list of affiliationROs in the affiliation path
  // StringBuffer orgName = new StringBuffer();
  // for (AffiliationRO affRO : affPathVO.getAffiliationList()) {
  // String newAffObjId = affRO.getObjectId();
  // AffiliationVO newAff = affiliationCache.get(newAffObjId);
  // // check if affiliation(affRef) is already in the cache
  // if (newAff == null) {
  // // if not, retrieve the affiliation from the framework and put it in the cache
  // String newAffXML =
  // ServiceLocator.getOrganizationalUnitHandler(userHandle).retrieve(newAffObjId);
  // newAff = XmlTransformingService.transformToAffiliation(newAffXML);
  // affiliationCache.put(newAffObjId, newAff);
  // }
  // if (orgName.length() > 0) {
  // orgName.append(ORGANIZATION_NAME_SEPARATOR);
  // }
  // if (newAff.getMetadataSets().size() > 0
  // && newAff.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
  // MdsOrganizationalUnitDetailsVO details =
  // (MdsOrganizationalUnitDetailsVO) newAff.getMetadataSets().get(0);
  // orgName.append(details.getName());
  // }
  // }
  // newOrg.setName(orgName.toString());
  //
  // // add the new OrganizationVO to the result list
  // organizationList.add(newOrg);
  // }
  // } catch (OrganizationalUnitNotFoundException e) {
  // logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
  // throw new AffiliationNotFoundException(affiliationRef, e);
  // } catch (AuthenticationException e) {
  // logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
  // throw new TechnicalException(e);
  // } catch (AuthorizationException e) {
  // logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
  // throw new TechnicalException(e);
  // } catch (RemoteException e) {
  // logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
  // throw new TechnicalException(e);
  // } catch (ServiceException e) {
  // logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
  // throw new TechnicalException(e);
  // }
  //
  // return organizationList;
  // }

  /**
   * Appends a String to to a given StringBuffer. Add a comma followed by a blank as separator if
   * necessary.
   * 
   * @param addressPart
   * @param address
   */
  // private static void appendAddressPart(String addressPart, StringBuffer address) {
  // if (addressPart != null) {
  // if (address.length() > 0) {
  // address.append(", ");
  // }
  // address.append(addressPart);
  // }
  // }

  // public static List<RelationVO> findParentContainer(String userHandle, String id)
  // throws TechnicalException {
  // if (id == null) {
  // throw new IllegalArgumentException(DataGatheringService.class.getSimpleName()
  // + ".findParentContainer:itemId is null");
  // }
  // String param =
  // "<param>" + "<query>* " + PREDICATE_ISMEMBEROF + " &lt;info:fedora/" + id + "&gt;</query>"
  // + "<format>" + OUTPUT_FORMAT + "</format>" + "</param>";
  // logger.debug("Param=" + param);
  // try {
  // String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
  // List<RelationVO> relations = XmlTransformingService.transformToRelationVOList(result);
  // return relations;
  // } catch (Exception e) {
  // logger.error("Error retrieving revisions.", e);
  // throw new TechnicalException(e);
  // }
  // }
}
