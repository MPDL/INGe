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

package test.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.types.Coordinates;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * This class containts methods that create affiliations and affiliation structures in the framework. It is used by
 * tests in xmltransforming and datagathering.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class AffiliationCreator extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(AffiliationCreator.class);
    private static XmlTransforming xmlTransforming = null;
    private static final String FILTER_ALL = "<param></param>";

    static
    {
        try
        {
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        }
        catch (Exception e)
        {
            fail("XmlTransforming not found.");
        }
    }

    /**
     * This method creates an affiliation structure in the framework and stores a mapping between affiliation
     * abbreviations and the affiliations' objectIds in a Key-Value-Mapping. The HashMap is given back. The created
     * structure is as follows: * MPG->(MPIFG,MPI-G,FML,MPIMF->(ZEL,MPH-HD->(ZEL2->(FU-BERLIN2))),FU-BERLIN //
     * HG->FU-BERLIN // FG->FU-BERLIN The used abbreviations are as follows: "objectIdMPG" "objectIdFG" "objectIdHG"
     * "objectIdMPIFG" "objectIdMPI_G" "objectIdFML" "objectIdMPIMF" "objectIdZEL" "objectIdMPH_HD" "objectIdZEL2"
     * "objectIdFuBerlin_2" "objectIdFuBerlin"
     * 
     * @param systemAdministratorUserHandle
     * @return A Key-Value-Mapping between the affiliation abbreviations and the affiliations' objectIds of the created
     *         affiliations.
     * @throws Exception
     */
    public static Map<String, String> createAffiliationStructure(String systemAdministratorUserHandle) throws Exception
    {

        Map<String, String> abbreviationToObjIdMapping = new HashMap<String, String>();
        logger.info("### " + AffiliationCreator.class.getSimpleName() + ":createAffiliationStructure ###");
        // create MPG (top level) affiliation
        //
        AffiliationVO affiliationVO = getTopLevelAffiliationMPG();
        long uniquer = System.currentTimeMillis();
        String objectIdMPG = createTopLevelAffiliation(affiliationVO, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdMPG", objectIdMPG);
        // create Fraunhofer (top level) affiliation
        //
        affiliationVO = getTopLevelAffiliationFraunhofer();
        String objectIdFG = createTopLevelAffiliation(affiliationVO, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdFG", objectIdFG);
        // create Helmholtz (top level) affiliation
        //
        affiliationVO = getTopLevelAffiliationHelmholtz();
        String objectIdHG = createTopLevelAffiliation(affiliationVO, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdHG", objectIdHG);
        // create MPIFG as a sub-affiliation of MPG
        affiliationVO = getAffiliationMPIFG();
        List<String> parentObjIds = new ArrayList<String>();
        parentObjIds.add(objectIdMPG);
        String objectIdMPIFG =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdMPIFG", objectIdMPIFG);
        // create MPI-G as a sub-affiliation of MPG
        affiliationVO = getAffiliationMPI_G();
        String objectIdMPI_G =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdMPI_G", objectIdMPI_G);
        // create FML as a sub-affiliation of MPG
        affiliationVO = getAffiliationFML();
        String objectIdFML = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdFML", objectIdFML);
        // create MPIMF as a sub-affiliation of MPG
        affiliationVO = getAffiliationMPIMF();
        String objectIdMPIMF =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdMPIMF", objectIdMPIMF);
        // create ZEL as a sub-affiliation of MPIMF
        affiliationVO = getAffiliationZEL();
        parentObjIds.clear();
        parentObjIds.add(objectIdMPIMF);
        String objectIdZEL = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdZEL", objectIdZEL);
        // create MPH-HD as a sub-affiliation of MPIMF
        affiliationVO = getAffiliationMPH_HD();
        String objectIdMPH_HD =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdMPH_HD", objectIdMPH_HD);
        // create ZEL_2 as a sub-affiliation of MPH-HD
        affiliationVO = getAffiliationZEL();
        // The name of an affiliation mus be unique, therefore "_2" is appended.
        if (affiliationVO.getMetadataSets().size() > 0
                && affiliationVO.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            ((MdsOrganizationalUnitDetailsVO) affiliationVO.getMetadataSets().get(0))
                    .setName(((MdsOrganizationalUnitDetailsVO) affiliationVO.getMetadataSets().get(0))
                            .getName() + "_2");
        } 
        parentObjIds.clear();
        parentObjIds.add(objectIdMPH_HD);
        String objectIdZEL2 = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdZEL2", objectIdZEL2);
        // create FU_BERLIN_2 as a sub-affiliation of ZEL_2
        affiliationVO = getAffiliationFU_BERLIN();
        // The name of an affiliation mus be unique, therefore "_2" is appended.
        if (affiliationVO.getMetadataSets().size() > 0
                && affiliationVO.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            ((MdsOrganizationalUnitDetailsVO) affiliationVO.getMetadataSets().get(0))
                    .setName(((MdsOrganizationalUnitDetailsVO) affiliationVO.getMetadataSets().get(0))
                                .getName() + "_2");
        }
        parentObjIds.clear();
        parentObjIds.add(objectIdZEL2);
        String objectIdFuBerlin_2 =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdFuBerlin_2", objectIdFuBerlin_2);
        // create FU-BERLIN as a sub-affiliation of MPG, FG and HG
        affiliationVO = getAffiliationFU_BERLIN();
        parentObjIds.clear();
        parentObjIds.add(objectIdMPG);
        parentObjIds.add(objectIdFG);
        parentObjIds.add(objectIdHG);
        String objectIdFuBerlin =
            createSubAffiliation(
                    affiliationVO,
                    parentObjIds,
                    systemAdministratorUserHandle,
                    uniquer);
        abbreviationToObjIdMapping.put("objectIdFuBerlin", objectIdFuBerlin);
        logger.debug("Affiliation structure created!");
        return abbreviationToObjIdMapping;
    }

    /**
     * Creates a top level affiliation (i.e. an affiliation w/o a parent affiliation)
     * 
     * @param affiliation The affiliation that shall be created.
     * @param userHandle The handle of the logged in user (must have according rights).
     * @param uniquer As the names of affiliations must be unique, this uniquer[long] is appended to the name of the
     *            affiliation for that the test can run as often as wanted (even of the removal of the affiliations at
     *            the end of the test is commented out)
     * @return The object id of the created affiliation.
     * @throws Exception
     */
    public static String createTopLevelAffiliation(
            AffiliationVO affiliation,
            String userHandle, long uniquer) throws Exception
    {
        // make sure the XmlTransforming instance is properly initialized
        if (affiliation == null)
        {
            throw new IllegalArgumentException(
                    AffiliationCreator.class.getSimpleName()
                    + ":createTopLevelAffiliation:affiliation is null");
        }
        String name = null;
        if (affiliation.getMetadataSets().size() > 0
                && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            name = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0))
                    .getName() + " Nr." + uniquer + "***";
            ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0))
                    .setName(name);
        }

        // transform the AffiliationVO into an organizational unit (for create)
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliation);
        logger.debug(
                "createTopLevelAffiliation() - PreCreate (without PID): This top level "
                + "affiliation is sent to the framework:\n" + organizationalUnitPreCreate);
        // create the organizational unit in the framework
        String organizationalUnitPostCreate =
            ServiceLocator
                    .getOrganizationalUnitHandler(userHandle)
                    .create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        // extract the objid from the returned OU
        String objectId = getObjid(organizationalUnitPostCreate);
        StringBuffer sb = new StringBuffer();
        sb.append(
                "createTopLevelAffiliation() - "
                + name
                + ": organizational unit created in the framework (objectId: "
                + objectId
                + ").");
        if (logger.isDebugEnabled())
        {
            sb.append("\n" + toString(getDocument(organizationalUnitPostCreate, false), false));
        }
        logger.info(sb.toString());
        return objectId;
    }

    /**
     * Creates a sub-affiliation of a list of other affiliations and checks the number
     * of parent affiliations afterwards.
     * 
     * @param affiliation The affiliation that shall be created.
     * @param parentObjectIds The list of object ids of the parent affiliations.
     * @param userHandle The handle of the logged in user (must have according rights).
     * @param uniquer As the names of affiliations mus be unique, this uniquer[long] is appended to the name of the
     *            affiliation for that the test can run as often as wanted
     * @return The object id of the created affiliation.
     * @throws Exception
     */
    public static String createSubAffiliation(
            final AffiliationVO affiliation,
            List<String> parentObjectIds,
            String userHandle,
            long uniquer) throws Exception
    {
        String name = null;
        if (affiliation.getMetadataSets().size() > 0
                && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            name = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0))
                    .getName() + " Nr." + uniquer + "***";
            ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0))
                    .setName(name);
        }
        for (String objId : parentObjectIds)
        {
            AffiliationRO affRO = new AffiliationRO(objId);
            affiliation.getParentAffiliations().add(affRO);
        }
        // transform the AffiliationVO into an organizational unit (for create)
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliation);
        if (affiliation.getMetadataSets().size() > 0
                && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            MdsOrganizationalUnitDetailsVO details =
                ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0));
            logger.debug(
                    "createSubAffiliation() - PreCreate - "
                    + name + ": AffiliationVO.externalID = "
                    + details.getIdentifiers());
        }
        logger.debug(
                "createSubAffiliation() - PreCreate - "
                + name
                + ": organizational unit after transformation from AffiliationVO (unformatted) =\n"
                + organizationalUnitPreCreate);
        // create the organizational unit in the framework
        String organizationalUnitPostCreate =
            ServiceLocator.getOrganizationalUnitHandler(userHandle)
                .create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        // extract the objid from the returned OU
        String objectId = getObjid(organizationalUnitPostCreate);
        StringBuffer sb = new StringBuffer();
        sb.append(
                "createSubAffiliation() - "
                + name +
                ": organizational unit created in the framework (objectId: "
                + objectId +
                ") as a sub_OU of ");
        for (String objId : parentObjectIds)
        {
            sb.append(objId + ", ");
        }
        // remove last ", "
        int l = sb.length();
        sb.delete(l - 2, l);
        sb.append(" (objectId: " + objectId + ").");
        if (logger.isDebugEnabled())
        {
            sb.append("\n" + toString(getDocument(organizationalUnitPostCreate, false), false));
        }
        logger.info(sb.toString());
        // check relationship to parent affiliation(s)
        String parents = ServiceLocator.getOrganizationalUnitHandler(userHandle).retrieveParents(objectId);
        logger.info("createSubAffiliation() - " + name + ": parent-OUs retrieved.");
        logger.debug(
                "createSubAffiliation() - "
                + name
                + ": list of retrieved parent affiliations =\n"
                + toString(getDocument(parents, false), false));
        List<AffiliationRO> parentsROList = xmlTransforming.transformToParentAffiliationList(parents);
        assertEquals(parentObjectIds.size(), parentsROList.size());
        return objectId;
    }

    /**
     * Delivers the (top-level) affiliation 'Max-Planck-Gesellschaft'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getTopLevelAffiliationMPG() throws NamingException
    {
        // make sure the XmlTransforming instance is properly initialized
        AffiliationVO affiliationMPG = new AffiliationVO();
        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        details.setName("Max-Planck-Gesellschaft");
        details.getAlternativeNames().add("MPG");
        details.setCity("München");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpg.de"));

        details.setCountryCode("NO");
        details.getDescriptions()
                .add("In der Helmholtz-Gemeinschaft haben sich 15 "
                        + "naturwissenschaftlich-technische und medizinisch-biologische "
                        + "Forschungszentren zusammengeschlossen. Ihre Aufgabe ist es, "
                        + "langfristige Forschungsziele des Staates und der Gesellschaft "
                        + "zu verfolgen. Die Gemeinschaft strebt nach Erkenntnissen, die "
                        + "dazu beitragen, Lebensgrundlagen des Menschen zu erhalten und "
                        + "zu verbessern. Dazu identifiziert und bearbeitet sie große und "
                        + "drängende Fragen von Gesellschaft, Wissenschaft und Wirtschaft "
                        + "durch strategisch-programmatisch ausgerichtete Spitzenforschung "
                        + "in sechs Forschungsbereichen: Energie, Erde und Umwelt, "
                        + "Gesundheit, Schlüsseltechnologien, Struktur der Materie sowie "
                        + "Verkehr und Weltraum.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "4711"));

        affiliationMPG.getMetadataSets().add(details);
        
        return affiliationMPG;
    }

    /**
     * Delivers the (top-level) affiliation 'Helmholtz-Gemeinschaft'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getTopLevelAffiliationHelmholtz() throws NamingException
    {
        // make sure the XmlTransforming instance is properly initialized
        AffiliationVO affiliationHelmholtz = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();

        details.setName("Helmholtz-Gemeinschaft");
        details.getAlternativeNames().add("HG");

        details.setCity("Bonn");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI,"http://www.helmholtz.de"));
        details.setCountryCode("DE");
        details.getDescriptions()
                .add("In der Helmholtz-Gemeinschaft haben sich 15 naturwissenschaftlich-technische und medizinisch-biologische Forschungszentren zusammengeschlossen. Ihre Aufgabe ist es, langfristige Forschungsziele des Staates und der Gesellschaft zu verfolgen. Die Gemeinschaft strebt nach Erkenntnissen, die dazu beitragen, Lebensgrundlagen des Menschen zu erhalten und zu verbessern. Dazu identifiziert und bearbeitet sie große und drängende Fragen von Gesellschaft, Wissenschaft und Wirtschaft durch strategisch-programmatisch ausgerichtete Spitzenforschung in sechs Forschungsbereichen: Energie, Erde und Umwelt, Gesundheit, Schlüsseltechnologien, Struktur der Materie sowie Verkehr und Weltraum.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.EDOC,"239832-3232"));
        affiliationHelmholtz.getMetadataSets().add(details);
    
    
        return affiliationHelmholtz;
    }

    /**
     * Delivers the (top-level) affiliation 'Fraunhofer-Gesellschaft'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getTopLevelAffiliationFraunhofer() throws NamingException
    {
        // make sure the XmlTransforming instance is properly initialized
        AffiliationVO affiliationFraunhofer = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.");

        details.getAlternativeNames().add("FG");

        details.setCity("München");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.fraunhofer.de"));

        details.setCountryCode("NO");
        details.getDescriptions()
                .add("Die Fraunhofer-Gesellschaft ist die führende Organisation für angewandte Forschung in Europa. Sie betreibt anwendungsorientierte Forschung zum direkten Nutzen für Unternehmen und zum Vorteil der Gesellschaft.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "4711"));
        
        affiliationFraunhofer.getMetadataSets().add(details);
        
        return affiliationFraunhofer;
    }

    /**
     * Delivers the affiliation 'Max-Planck-Institut für Gesellschaftsforschung'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationMPIFG() throws NamingException
    {
        // make sure the XmlTransforming instance is properly initialized
        AffiliationVO affiliationMPIFG = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Max-Planck-Institut für Gesellschaftsforschung");
        details.getAlternativeNames().add("MPIFG");
        details.setCity("Köln");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpifg.de"));

        details.setCountryCode("DE");
        details.getDescriptions()
                .add("Das Max-Planck-Institut für Gesellschaftsforschung ist eine Einrichtung der Spitzenforschung in den Sozialwissenschaften. Es betreibt anwendungsoffene Grundlagenforschung mit dem Ziel einer empirisch fundierten Theorie der sozialen und politischen Grundlagen moderner Wirtschaftsordnungen. Im Mittelpunkt steht die Untersuchung der Zusammenhänge zwischen ökonomischem, sozialem und politischem Handeln. Mit einem vornehmlich institutionellen Ansatz wird erforscht, wie Märkte und Wirtschaftsorganisationen in historisch-institutionelle, politische und kulturelle Zusammenhänge eingebettet sind, wie sie entstehen und wie sich ihre gesellschaftlichen Kontexte verändern. Das Institut schlägt eine Brücke zwischen Theorie und Politik und leistet einen Beitrag zur politischen Diskussion über zentrale Fragen moderner Gesellschaften.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "4711-4712"));
        
        affiliationMPIFG.getMetadataSets().add(details);
        

        return affiliationMPIFG;
    }

    /**
     * Delivers the affiliation 'Max-Planck-Institut zur Erforschung multireligiöser und multiethnischer
     * Gesellschaften'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationMPI_G() throws NamingException
    {
        // make sure the XmlTransforming instance is properly initialized
        AffiliationVO affiliationMPI_G = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Max-Planck-Institut zur Erforschung multireligiöser und multiethnischer Gesellschaften");
        details.getAlternativeNames().add("MPI-G");
        details.setCity("Göttingen");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.geschichte.mpg.de/"));

        details.setCountryCode("DE");
        details.getDescriptions().add("Die Mission Historique Francaise en Allemagne untersteht der Zuständigkeit des französischen Außenministeriums, "
                + "das durch eine aus französischen und deutschen Persönlichkeiten zusammengesetzte wissenschaftliche Kommission beraten wird.\n"
                + "Die Mission Historique Francaise en Allemagne wurde 1977 durch Robert Mandrou gegründet, der auch ihr erster Direktor war. "
                + "Heute nimmt sie im wissenschaftlichen Austausch der Historiker in Frankreich und Deutschland einen allgemein anerkannten Platz ein.\n"
                + "Die MHFA mietet Räumlichkeiten in den Gebäuden des Max-Planck-Instituts für Geschichte. Zu diesem Institut pflegt sie enge "
                + "Beziehungen, so wie mit vielen weiteren Hochschulen und Forschungseinrichtungen in Frankreich, Deutschland und anderen europäischen "
                + "Ländern. Sie hat flexible und anpassungsfähige institutionelle Strukturen entwickelt.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "r2d2-49"));
        affiliationMPI_G.getMetadataSets().add(details);

        return affiliationMPI_G;
    }

    /**
     * Delivers the affiliation 'Friedrich-Miescher-Laboratorium für biologische Arbeitsgruppen in der
     * Max-Planck-Gesellschaft'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationFML() throws NamingException
    {
        AffiliationVO affiliationFML = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Friedrich-Miescher-Laboratorium für biologische Arbeitsgruppen in der Max-Planck-Gesellschaft");
        details.getAlternativeNames().add("FML");

        details.setCity("Tübingen");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.fml.tuebingen.mpg.de"));
        
        details.setCountryCode("DE");
        details.getDescriptions().add("Das Friedrich-Miescher-Laboratorium wurde 1969 gegründet, um besonders qualifizierten jungen Wissenschaftlern "
                + "die Möglichkeit zu bieten, mit unabhängigen Arbeitsgruppen für einen befristeten Zeitraum (fünf Jahre) eigene "
                + "Forschungsprojekte zu bearbeiten. Die Forschungsschwerpunkte wechseln mit der Berufung neuer Gruppenleiter.\n"
                + "Administrative und organisatorische Interessen der Arbeitsgruppen des Friedrich-Miescher-Laboratoriums werden "
                + "alternierend durch einen der Arbeitsgruppenleiter vertreten. Die räumliche und thematische Einbettung des "
                + "Laboratoriums auf dem Campus der Max-Planck-Institute in Tübingen ermöglicht intensive wissenschaftliche und "
                + "methodische Kontakte zu den Abteilungen der benachbarten Institute.\n"
                + "Die seit Januar 2005 bestehende und von Gunnar Rätsch geleitete Gruppe ist an der Beantwortung biologischer "
                + "Fragestellungen mithilfe moderner Methoden des maschinellen Lernens interessiert. Maschinelles Lernen beschäftigt "
                + "sich mit der computergestützen Analyse komplexer Phänomene und hat sich als sehr nützlich bei der Untersuchung "
                + "biologischer Systeme erwiesen. Die Gruppe hat sich zum Ziel gesetzt, effiziente und präzise Lernmethoden zu entwickeln, "
                + "die in der Lage sind, mit großen genomischen Datenmengen umzugehen. Es wird angestrebt, durch für den Menschen "
                + "nachvollziehbare Vorhersagen ein besseres Verständnis der betrachteten biologischen Zusammenhänge zu erlangen. "
                + "Die Arbeitsgruppe ist besonders an der Anwendung und Entwicklung neuer Sequenzanalysealgorithmen zur Vorhersage "
                + "neuer Gene auf der genomischen DNA interessiert. Sie strebt außerdem ein besseres Verständnis der zellulären "
                + "Spleißmechanismen mithilfe dieser Methoden an. Die genaue Vorhersage von alternativen Spleißprodukten und die "
                + "Untersuchung von Regulationsmechanismen stehen im Moment im Mittelpunkt ihrer Forschung.\n"
                + "Die Gruppe von Silke Hauf beschäftigt sich mit der Regulation der Chromosomensegregation während der Zellteilung "
                + "von Eukaryonten. Insbesondere wird untersucht, wie Kinetochore die Chromosomensegregation beeinflussen und für "
                + "einen fehlerfreien Ablauf sorgen. Korrekte Chromosomensegregation ist essentiell für die Weitergabe der "
                + "Erbinformation an die Tochterzellen, und damit essentiell für den Bestand von Organismen. Die grundlegenden "
                + "Komponenten und Prinzipien haben sich daher über viele Jahrmillionen nur wenig verändert, und ähneln sich in "
                + "verschiedensten Organismen. Spalthefe, ein einzelliger Pilz mit Kinetochoren, die den menschlichen in ihrer "
                + "Struktur ähneln, wird als Modellorganismus verwendet werden. Bislang unbekannte Regulatoren der Chromosomensegregation "
                + "sollen durch genetische Screens in Spalthefe identifiziert, und ihre Funktion sowohl in der Hefe als auch in " + "menschlichen Zellen untersucht werden.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "MPI-27892-UBUNTU"));
        affiliationFML.getMetadataSets().add(details);

        return affiliationFML;
    }

    /**
     * Delivers the affiliation 'Max-Planck-Institut für medizinische Forschung'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationMPIMF() throws NamingException
    {

        AffiliationVO affiliationMPIMF = new AffiliationVO();

        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Max-Planck-Institut für medizinische Forschung");
        details.getAlternativeNames().add("MPIMF");
        details.setCity("Heidelberg");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpimf-heidelberg.mpg.de/"));

        details.setCountryCode("DE");
        details.getDescriptions().add("Das Institut wurde 1930 als Kaiser-Wilhelm-Institut gegründet, um Methoden der Physik und Chemie in die "
                + "medizinische Grundlagenforschung einzuführen. Die Abteilungen für Chemie, Physiologie und Biophysik konzentrierten "
                + "sich auf biophysikalische und chemische Fragestellungen, in der Tradition der Naturstoffchemie des Instituts. Mit "
                + "einer Abteilung für Molekularbiologie wurde in den 60er Jahren neuen Entwicklungen in der Biologie Rechnung getragen. "
                + "Ende der 80er Jahre und während der 90er Jahre kamen Untersuchungen zu spezifischen Funktionen von Muskel- und "
                + "Nervenzellen hinzu. Neue Abteilungen für Zellphysiologie (1989), Molekulare Zellforschung (1992-1999), Molekulare "
                + "Neurobiologie (1995), Biomedizinische Optik (1999) und Biomolekulare Mechanismen (2002) wurden ebenso wie die "
                + "Nachwuchsgruppen Ionenkanalstruktur (1997-2003) und für Entwicklungsgenetik (1999-2005) gegründet. Am Institut "
                + "arbeiteten seit seiner Gründung fünf Nobelpreisträger: Meyerhof (Physiologie), Kuhn (Chemie), Bothe (Physik), " + "Mößbauer (Physik) und Sakmann (Physiologie).\n"
                + "Gegenwärtig hat das Institut vier Abteilungen. Die Abteilung Zellphysiologie bearbeitet die Entstehung von "
                + "elektrischen Signalen und der Weiterleitung zwischen und innerhalb von Nervenzellen sowie deren Veränderbarkeit durch "
                + "Übung und Gebrauch. Die Abteilung Molekulare Neurobiologie hat als Schwerpunkt die Analyse und Veränderung in der Maus "
                + "von Genen, deren Produkte für die schnelle Signalübermittlung im Gehirn verantwortlich sind und geht der Frage nach, "
                + "welche Hirnleistungen vererbt oder welche erworben werden. Die Abteilung Biomedizinische Optik bestimmt unter Anwendung "
                + "und Weiterentwicklung der Multiquantenmikroskopie die Aktivität von Gruppen von Nervenzellen, in Gewebepräparaten und "
                + "in intakten Tieren. Ziel der Arbeiten der Abteilung Biomolekulare Mechanismen ist es, die molekularen Grundlagen von "
                + "Modellreaktionen anhand biophysikalischer und strukturbiologischer Untersuchungen aufzuklären.\n"
                + "Schwerpunkt der Emeritusgruppe Biophysik ist die Struktur des Myosin-Aktin Komplexes mit atomarer Auflösung.\n"
                + "Künftig sollen unter anderem am Institut Nervenzellen und ihre vielfältigen Verschaltungen in der Großhirnrinde, die "
                + "für Empfang und Verarbeitung von Meldungen der Sinnesorgane, wie dem Geruchs-, Seh- und Tastsinn, verantwortlich sind, "
                + "mit Hilfe von molekulargenetischen, physiologischen und bildgebenden Verfahren untersucht werden. Insbesondere "
                + "interessiert uns, wie Information in den Kontaktpunkten (Synapsen) der Verdrahtungen zwischen Nervenzellen gespeichert "
                + "und abgerufen wird und wie neue Kontaktpunkte gebildet sowie nicht mehr benötigte entfernt werden. Für diese künftigen "
                + "Arbeiten sollen neu zu entwickelnde Genschalter zum Einsatz kommen, mit denen die Aktivität von Schlüsselmolekülen "
                + "für die schnelle Signalübertragung an Kontaktpunkten zwischen Nervenzellen gesteuert werden kann. Die bildgebende "
                + "Multiquantenmikroskopie soll miniaturisiert und in ihrer Eindringtiefe verbessert werden, so dass Aktivitätsmessungen "
                + "in der Großhirnrinde von sich frei bewegenden Mäusen durchgeführt werden können.");
        details.setCoordinates(new Coordinates(1.45246462436, 2.34673657346));
        affiliationMPIMF.getMetadataSets().add(details);
        
        return affiliationMPIMF;
    }

    /**
     * Delivers the affiliation 'Zentrale Einrichtung Lichtmikroskopie des MPI für Medizinische Forschung'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationZEL() throws NamingException
    {

        AffiliationVO affiliationZEL = new AffiliationVO();
        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        
        details.setName("Zentrale Einrichtung Lichtmikroskopie des MPI für Medizinische Forschung");
        details.getAlternativeNames().add("ZEL");
        details.setCity("Heidelberg");

        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://lightmicro.mpimf-heidelberg.mpg.de/index.html"));

        details.setCountryCode("DE");
        details.getDescriptions().add("Die Zentrale Einrichtung Lichtmikroskopie des MPI fuer Medizinische Forschung soll:\n"
                + "* Wissenschaflern des Institutes und von außerhalb die Nutzung von komplexen, aktuellen Methoden der Lichtmikroskopie " + "und der lichtmikroskopischen Datenanalyse bieten\n"
                + "* Unterstützung und Training für Probenvorbereitung, Datenaufnahme und Datenanalyse bieten\n" + "* die Kommunikation und den Austausch von experimentellen Erfahrungen fördern");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "LIGHT-123"));
        affiliationZEL.getMetadataSets().add(details);

        return affiliationZEL;
    }

    /**
     * Delivers the affiliation 'Max-Planck-Haus Heidelberg'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationMPH_HD() throws NamingException
    {

        AffiliationVO affiliationMPH_HD = new AffiliationVO();
        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        details.setName("Max-Planck-Haus Heidelberg");
        details.getAlternativeNames().add("MPH-HD");
        details.setCity("Heidelberg");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpimf-heidelberg.mpg.de/serviceEinrichtungen/maxPlanckHaus/index.html"));

        details.setCountryCode("DE");
        details.getDescriptions().add("Im Hörsaal des Max-Planck-Hauses können Vortäge und Seminarveranstaltungen stattfinden; folgende technische " + "Ausstattung steht zur Verfügung:\n"
                + "* Beamer\n" + "* Tageslichtprojektor\n" + "* Diaprojektor\n" + "* Beamer\n" + "* Breitband-Internetanschluss\n" + "* Leinwand\n" + "* Wandtafel\n"
                + "* Mikrofonanlage mit drei Funkmikrofonen\n" + "* DVD\n");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "MPH-HD-20982022"));
        affiliationMPH_HD.getMetadataSets().add(details);
        return affiliationMPH_HD;
    }

    /**
     * Delivers the affiliation 'Freie Universität Berlin'.
     * 
     * @return The affiliation.
     * @throws NamingException
     */
    public static AffiliationVO getAffiliationFU_BERLIN() throws NamingException
    {

        AffiliationVO affiliationFU_BERLIN = new AffiliationVO();
        MdsOrganizationalUnitDetailsVO details = new MdsOrganizationalUnitDetailsVO();
        details.setName("Freie Universität Berlin");
        details.getAlternativeNames().add("FU-BERLIN");
        details.setCity("Berlin");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.fu-berlin.de/"));

        details.setCountryCode("DE");
        details.getDescriptions().add("Die Freie Universität Berlin gehört zu den führenden Universitäten der Welt und zeichnet sich durch ihren "
                + "modernen und internationalen Charakter aus. Deutschlandweit zählt die Freie Universität mit über hundert Studienfächern "
                + "und 35.500 Studierenden - davon 15 Prozent aus aller Welt - zu den größten und leistungsstärksten Universitäten. Auch "
                + "ausländische Gastwissenschaftler, wie die Alexander von Humboldt-Stipendiaten, wählen deutschlandweit bevorzugt die "
                + "Freie Universität als Ort für ihren Forschungsaufenthalt.\n"
                + "Die Medizin nicht eingerechnet, ist die Freie Universität derzeit Sprecheruniversität von acht Sonderforschungsbereichen "
                + "und vier Forschergruppen der Deutschen Forschungsgemeinschaft (DFG). Auf Grund ihres besonderen Fächerprofils sind an "
                + "der Freien Universität zwei Sonderforschungsbereiche im geisteswissenschaftlichen Bereich angesiedelt - berlinweit ein "
                + "Novum. Dreizehn Wissenschaftler der Freien Universität erhielten bislang den Leibniz-Preis der DFG, die ranghöchste "
                + "Auszeichnung für Forschungsleistungen in Deutschland. Mit der Summe von 55 Millionen Euro wirbt die Freie Universität "
                + "einen beträchtlichen Teil ihrer Einnahmen aus Drittmitteln ein.\n"
                + "Eine strategische Allianz hat die Freie Universität mit der Ludwig-Maximilians-Universität in München geschlossen. "
                + "Außerdem arbeitet die Freie Universität mit weltweit aktiven Firmen wie der BMW-Group, Schering, Siemens, Deutsche " + "Telekom oder Pfizer eng zusammen.");
        details.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "FU-B-2103984"));

        affiliationFU_BERLIN.getMetadataSets().add(details);
        
        return affiliationFU_BERLIN;
    }

    /**
     * @throws Exception Any exception
     */
    public static void deleteAllAffiliationsContainingThreeAsteriskes() throws Exception
    {
        String systemAdministratorUserHandle = loginSystemAdministrator();

        
        //logout(systemAdministratorUserHandle);
       // retrieve all affiliations in the framework (again)
      OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle);
      String affiliationsXML = ouh.retrieveOrganizationalUnits(FILTER_ALL);
      System.out.println(affiliationsXML);
      List<AffiliationVO> affiliations = xmlTransforming.transformToAffiliationList(affiliationsXML);
       
      for (AffiliationVO affiliation : affiliations)
      {
          String affiliationName = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0)).getName();
          if (affiliationName.contains("***") )
          {
              deleteAffiliationNode( ouh, affiliation );
          }
      }
//        do
//        {            
//            // retrieve all affiliations in the framework (again)
//            OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle);
//            String affiliationsXML = ouh.retrieveOrganizationalUnits(FILTER_ALL);
//            System.out.println(affiliationsXML);
//            List<AffiliationVO> affiliations = xmlTransforming.transformToAffiliationList(affiliationsXML);
//
//            nextLoopAffiliationCount = 0;
//            for (AffiliationVO affiliation : affiliations)
//            {
//                // As affiliations with child affiliations cannot be removed, skip them in this loop run
//                if (affiliation.getMetadataSets().size() > 0 && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
//                {
//                    String affiliationName = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0)).getName();
//                    logger.debug("Examining '" + affiliationName + "'");
//                    if (affiliationName.contains("***"))
//                    {
//                        logger.info(affiliation.getReference().getObjectId() + " has " + (affiliation.getHasChildren() ? "" : "no ") + "children");
//                        if (!affiliation.getHasChildren())
//                        {
//                            try
//                            {
//                                ouh.delete(affiliation.getReference().getObjectId());
//                                logger.debug("-> " + affiliationName + "...DELETED");
//                            }
//                            catch (Exception e) {
//                                logger.error("Error deleting " + affiliation.getReference().getObjectId());
//                            }
//                        }
//                        else
//                        {
//                            nextLoopAffiliationCount++;
//                        }
//                    }
//                }
//            }
//            logger.info("Test affiliation removal loop ended. nextLoopAffiliationCount = " + nextLoopAffiliationCount);
//        }
//        while (nextLoopAffiliationCount > 0);
//        logout(systemAdministratorUserHandle);
    }
    
    private static void deleteAffiliationNode(OrganizationalUnitHandler ouh, AffiliationVO affiliation ) {
        if( affiliation.getHasChildren() == true )
        {
            List<AffiliationRO> childList = affiliation.getChildAffiliations();
            for( int i = 0; i < childList.size(); i++ )
            {
                String xmlAffiliation = null;
                AffiliationVO childAffiliation = null;
                try
                {
                    xmlAffiliation = ouh.retrieve( childList.get( i ).getObjectId() );
                    childAffiliation = xmlTransforming.transformToAffiliation( xmlAffiliation );
                } 
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                deleteAffiliationNode( ouh, childAffiliation );
                try
                {
                    ouh.delete(affiliation.getReference().getObjectId());
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                ouh.delete(affiliation.getReference().getObjectId());
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
