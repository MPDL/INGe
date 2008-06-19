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

package de.mpg.escidoc.services.common.datagathering;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.AffiliationNotFoundException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationPathVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This class provides the ejb implementation of the {@link DataGathering} interface.
 *
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 03.09.2007
 */
@Remote
@RemoteBinding(jndiBinding = DataGathering.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DataGatheringBean implements DataGathering
{
    private static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
    private static final String OUTPUT_FORMAT = "RDF/XML";

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(DataGatheringBean.class);

    /**
     * Separator used for concatenation of affiliation names to one organization name.
     */
    public static final String ORGANIZATION_NAME_SEPARATOR = ", ";

    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;


    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.common.DataGathering#findRevisionsOfItem(de.mpg.escidoc.services.common.referenceobjects.ItemRO)
     */
    public List<RelationVO> findRevisionsOfItem(String userHandle, ItemRO itemRef) throws TechnicalException
    {
        if (itemRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ".findRevisionsOfItem:itemRef is null");
        }
        String param = "<param>"
            + "<query>* " + PREDICATE_ISREVISIONOF + " &lt;info:fedora/" + itemRef.getObjectId() + "&gt;</query>"
            + "<format>" + OUTPUT_FORMAT + "</format>"
            + "</param>";
        logger.debug("Param=" + param);
        try
        {
            String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
            List<RelationVO> relations = xmlTransforming.transformToRelationVOList(result);
            return relations;
        }
        catch (Exception e)
        {
            logger.error("Error retrieving revisions.", e);
            throw new TechnicalException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public List<RelationVO> findParentItemsOfRevision(String userHandle, ItemRO itemRef) throws TechnicalException
    {
        if (itemRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ".findRevisionsOfItem:itemRef is null");
        }
        String param = "<param>"
            + "<query>&lt;info:fedora/" + itemRef.getObjectId() + "&gt; " + PREDICATE_ISREVISIONOF + " *</query>"
            + "<format>" + OUTPUT_FORMAT + "</format>"
            + "</param>";
        logger.debug("Param=" + param);
        try
        {
            String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
            List<RelationVO> relations = xmlTransforming.transformToRelationVOList(result);
            return relations;
        }
        catch (Exception e)
        {
            logger.error("Error retrieving revisions.", e);
            throw new TechnicalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<OrganizationVO> createOrganizationListFromAffiliation(final String userHandle,
            final AffiliationVO affiliation) throws TechnicalException, AffiliationNotFoundException, URISyntaxException
    {
        logger.debug("createOrganizationListFromAffiliation(AffiliationVO)");
        if (affiliation == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":createOrganizationListFromAffiliation:affiliation is null");
        }

        final AffiliationRO affiliationRef = affiliation.getReference();
        // initialize the result organization list
        List<OrganizationVO> organizationList = new ArrayList<OrganizationVO>();
        try
        {
            // Get the affiliation paths for the given affiliation from the framework
            OrganizationalUnitHandler ouHandler = de.mpg.escidoc.services.framework.ServiceLocator
                    .getOrganizationalUnitHandler(userHandle);
            String affObjId = affiliationRef.getObjectId();
            logger
                    .debug("createOrganizationListFromAffiliation(AffiliationVO) - trying to ouHandler.retrievePathList(affObjId) with affObjId="
                            + affObjId);
            String affiliationPathListXML = ouHandler.retrievePathList(affObjId);
            logger.debug("createOrganizationListFromAffiliation() - retrieved path list XML=\n" + affiliationPathListXML);
            List<AffiliationPathVO> affiliationPathVOList = xmlTransforming
                    .transformToAffiliationPathList(affiliationPathListXML);

            // cache already retrieved affiliations
            // every cache entry consists of key:objectId, value: corresponding affiliation 
            // initialize cache with given affiliation
            Map<String, AffiliationVO> affiliationCache = new HashMap<String, AffiliationVO>();
            affiliationCache.put(affiliationRef.getObjectId(), affiliation);
            // loop through the list of affiliation paths; every affiliation path yields one OrganizationVO
            for (AffiliationPathVO affPathVO : affiliationPathVOList)
            {
                // create a new OrganizationVO
                OrganizationVO newOrg = new OrganizationVO();

                // create and set organization ADDRESS (to the address of the given affiliation)
                StringBuffer address = new StringBuffer();
                
                
                // TODO FrM: Adapt this to new AffiliationVO structure
                
//                appendAddressPart(affiliation.getAddress(), address);
//                appendAddressPart(affiliation.getPostcode(), address);
                
                if (affiliation.getMetadataSets().size() > 0 && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
                {
                    MdsOrganizationalUnitDetailsVO details = (MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0);
                    appendAddressPart(details.getCity(), address);
                    appendAddressPart(details.getCountryCode(), address);
                }
                
                if (address.length() > 0)
                {
                    newOrg.setAddress(address.toString());
                }

                // create and set organization ID (to the pid of the given affiliation)
                if (affiliation.getReference() != null)
                {
                    newOrg.setIdentifier(affiliation.getReference().getObjectId());
                }

                // create and set organization NAME (to the concatenated list of organization names in the affiliation
                // path)
                // loop through the list of affiliationROs in the affiliation path
                StringBuffer orgName = new StringBuffer();
                for (AffiliationRO affRO : affPathVO.getAffiliationList())
                {
                    String newAffObjId = affRO.getObjectId();
                    AffiliationVO newAff = affiliationCache.get(newAffObjId);
                    // check if affiliation(affRef) is already in the cache
                    if (newAff == null)
                    {
                        // if not, retrieve the affiliation from the framework and put it in the cache
                        String newAffXML = ServiceLocator.getOrganizationalUnitHandler(userHandle).retrieve(newAffObjId);
                        newAff = xmlTransforming.transformToAffiliation(newAffXML);
                        affiliationCache.put(newAffObjId, newAff);
                    }
                    if (orgName.length() > 0)
                    {
                        orgName.append(ORGANIZATION_NAME_SEPARATOR);
                    }
                    if (newAff.getMetadataSets().size() > 0 && newAff.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
                    {
                        MdsOrganizationalUnitDetailsVO details = (MdsOrganizationalUnitDetailsVO) newAff.getMetadataSets().get(0);
                        orgName.append(details.getName());
                    }
                }
                TextVO name = new TextVO();
                name.setValue(orgName.toString());
                newOrg.setName(name);

                // add the new OrganizationVO to the result list
                organizationList.add(newOrg);
            }
        }
        catch (OrganizationalUnitNotFoundException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new AffiliationNotFoundException(affiliationRef, e);
        }
        catch (AuthenticationException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (AuthorizationException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (RemoteException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (ServiceException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }

        return organizationList;
    }

    /**
     * Appends a String to to a given StringBuffer. Add a comma followed by a blank as separator if necessary.
     * 
     * @param addressPart
     * @param address
     */
    private void appendAddressPart(String addressPart, StringBuffer address)
    {
        if (addressPart != null)
        {
            if (address.length() > 0)
            {
                address.append(", ");
            }
            address.append(addressPart);
        }
    }
}
