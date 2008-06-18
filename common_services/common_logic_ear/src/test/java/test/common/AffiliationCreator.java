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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
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

    /**
     * Gets an {@link  XmlTransforming} instance if necessary.
     * 
     * @throws NamingException
     */
    private static void initialize() throws NamingException
    {
        if (xmlTransforming == null)
        {
            xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
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
        String objectIdMPIFG = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdMPIFG", objectIdMPIFG);
        // create MPI-G as a sub-affiliation of MPG
        affiliationVO = getAffiliationMPI_G();
        String objectIdMPI_G = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdMPI_G", objectIdMPI_G);
        // create FML as a sub-affiliation of MPG
        affiliationVO = getAffiliationFML();
        String objectIdFML = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdFML", objectIdFML);
        // create MPIMF as a sub-affiliation of MPG
        affiliationVO = getAffiliationMPIMF();
        String objectIdMPIMF = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdMPIMF", objectIdMPIMF);
        // create ZEL as a sub-affiliation of MPIMF
        affiliationVO = getAffiliationZEL();
        parentObjIds.clear();
        parentObjIds.add(objectIdMPIMF);
        String objectIdZEL = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdZEL", objectIdZEL);
        // create MPH-HD as a sub-affiliation of MPIMF
        affiliationVO = getAffiliationMPH_HD();
        String objectIdMPH_HD = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdMPH_HD", objectIdMPH_HD);
        // create ZEL_2 as a sub-affiliation of MPH-HD
        affiliationVO = getAffiliationZEL();
        // The name of an affiliation mus be unique, therefore "_2" is appended.
        affiliationVO.setName(affiliationVO.getName() + "_2");
        parentObjIds.clear();
        parentObjIds.add(objectIdMPH_HD);
        String objectIdZEL2 = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdZEL2", objectIdZEL2);
        // create FU_BERLIN_2 as a sub-affiliation of ZEL_2
        affiliationVO = getAffiliationFU_BERLIN();
        // The name of an affiliation mus be unique, therefore "_2" is appended.
        affiliationVO.setName(affiliationVO.getName() + "_2");
        parentObjIds.clear();
        parentObjIds.add(objectIdZEL2);
        String objectIdFuBerlin_2 = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
        abbreviationToObjIdMapping.put("objectIdFuBerlin_2", objectIdFuBerlin_2);
        // create FU-BERLIN as a sub-affiliation of MPG, FG and HG
        affiliationVO = getAffiliationFU_BERLIN();
        parentObjIds.clear();
        parentObjIds.add(objectIdMPG);
        parentObjIds.add(objectIdFG);
        parentObjIds.add(objectIdHG);
        String objectIdFuBerlin = createSubAffiliation(affiliationVO, parentObjIds, systemAdministratorUserHandle, uniquer);
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
    public static String createTopLevelAffiliation(AffiliationVO affiliation, String userHandle, long uniquer) throws Exception
    {
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        if (affiliation == null)
        {
            throw new IllegalArgumentException(AffiliationCreator.class.getSimpleName() + ":createTopLevelAffiliation:affiliation is null");
        }
        String abbrev = affiliation.getAbbreviation();
        affiliation.setName(affiliation.getName() + " Nr." + uniquer + "***");
        // transform the AffiliationVO into an organizational unit (for create)
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliation);
        logger.debug("createTopLevelAffiliation() - PreCreate (without PID): This top level affiliation is sent to the framework:\n" + organizationalUnitPreCreate);
        // create the organizational unit in the framework
        String organizationalUnitPostCreate = ServiceLocator.getOrganizationalUnitHandler(userHandle).create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        // extract the objid from the returned OU
        String objectId = getObjid(organizationalUnitPostCreate);
        StringBuffer sb = new StringBuffer();
        sb.append("createTopLevelAffiliation() - " + abbrev + ": organizational unit created in the framework (objectId: " + objectId + ").");
        if (logger.isDebugEnabled())
        {
            sb.append("\n" + toString(getDocument(organizationalUnitPostCreate, false), false));
        }
        logger.info(sb.toString());
        return objectId;
    }

    /**
     * Creates a sub-affiliation of a list of other affiliations and checks the number of parent affiliations afterwards
     * 
     * @param affiliation The affiliation that shall be created.
     * @param parentObjectIds The list of object ids of the parent affiliations.
     * @param userHandle The handle of the logged in user (must have according rights).
     * @param uniquer As the names of affiliations mus be unique, this uniquer[long] is appended to the name of the
     *            affiliation for that the test can run as often as wanted
     * @return The object id of the created affiliation.
     * @throws Exception
     */
    public static String createSubAffiliation(final AffiliationVO affiliation, List<String> parentObjectIds, String userHandle, long uniquer) throws Exception
    {
        String abbrev = affiliation.getAbbreviation();
        affiliation.setName(affiliation.getName() + " Nr." + uniquer + "***");
        for (String objId : parentObjectIds)
        {
            AffiliationRO affRO = new AffiliationRO(objId);
            affiliation.getParentAffiliations().add(affRO);
        }
        // transform the AffiliationVO into an organizational unit (for create)
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliation);
        logger.debug("createSubAffiliation() - PreCreate - " + abbrev + ": AffiliationVO.externalID = " + affiliation.getExternalId());
        logger.debug("createSubAffiliation() - PreCreate - " + abbrev + ": organizational unit after transformation from AffiliationVO (unformatted) =\n" + organizationalUnitPreCreate);
        // create the organizational unit in the framework
        String organizationalUnitPostCreate = ServiceLocator.getOrganizationalUnitHandler(userHandle).create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        // extract the objid from the returned OU
        String objectId = getObjid(organizationalUnitPostCreate);
        StringBuffer sb = new StringBuffer();
        sb.append("createSubAffiliation() - " + abbrev + ": organizational unit created in the framework (objectId: " + objectId + ") as a sub_OU of ");
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
        logger.info("createSubAffiliation() - " + abbrev + ": parent-OUs retrieved.");
        logger.debug("createSubAffiliation() - " + abbrev + ": list of retrieved parent affiliations =\n" + toString(getDocument(parents, false), false));
        List<AffiliationVO> parentsVOList = xmlTransforming.transformToAffiliationList(parents);
        assertEquals(parentObjectIds.size(), parentsVOList.size());
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
        initialize();
        AffiliationVO affiliationMPG = new AffiliationVO();
        affiliationMPG.setName("Max-Planck-Gesellschaft");
        affiliationMPG.setAbbreviation("MPG");
        affiliationMPG.setAddress("Hofgartenstr. 8");
        affiliationMPG.setPostcode("80539");
        affiliationMPG.setCity("München");
        affiliationMPG.setTelephone("+49 (89) 2108 - 0");
        affiliationMPG.setFax("+49 (89) 2108 - 1111");
        affiliationMPG.setEmail("post@gv.mpg.de");
        try
        {
            affiliationMPG.setHomepageUrl(new URL("http://www.mpg.de"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationMPG.setCountryCode("NO");
        affiliationMPG
                .setDescription("In der Helmholtz-Gemeinschaft haben sich 15 naturwissenschaftlich-technische und medizinisch-biologische Forschungszentren zusammengeschlossen. Ihre Aufgabe ist es, langfristige Forschungsziele des Staates und der Gesellschaft zu verfolgen. Die Gemeinschaft strebt nach Erkenntnissen, die dazu beitragen, Lebensgrundlagen des Menschen zu erhalten und zu verbessern. Dazu identifiziert und bearbeitet sie große und drängende Fragen von Gesellschaft, Wissenschaft und Wirtschaft durch strategisch-programmatisch ausgerichtete Spitzenforschung in sechs Forschungsbereichen: Energie, Erde und Umwelt, Gesundheit, Schlüsseltechnologien, Struktur der Materie sowie Verkehr und Weltraum.");
        affiliationMPG.setExternalId("4711");
        affiliationMPG.setRegion("Worldwide");
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
        initialize();
        AffiliationVO affiliationHelmholtz = new AffiliationVO();
        affiliationHelmholtz.setName("Helmholtz-Gemeinschaft");
        affiliationHelmholtz.setAbbreviation("HG");
        affiliationHelmholtz.setAddress("Ahrstraße 45");
        affiliationHelmholtz.setPostcode("53175");
        affiliationHelmholtz.setCity("Bonn");
        affiliationHelmholtz.setTelephone("+49 228 30818-0");
        affiliationHelmholtz.setFax("+49 228 30818-30");
        affiliationHelmholtz.setEmail("org@helmholtz.de");
        try
        {
            affiliationHelmholtz.setHomepageUrl(new URL("http://www.helmholtz.de"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationHelmholtz.setCountryCode("DE");
        affiliationHelmholtz
                .setDescription("In der Helmholtz-Gemeinschaft haben sich 15 naturwissenschaftlich-technische und medizinisch-biologische Forschungszentren zusammengeschlossen. Ihre Aufgabe ist es, langfristige Forschungsziele des Staates und der Gesellschaft zu verfolgen. Die Gemeinschaft strebt nach Erkenntnissen, die dazu beitragen, Lebensgrundlagen des Menschen zu erhalten und zu verbessern. Dazu identifiziert und bearbeitet sie große und drängende Fragen von Gesellschaft, Wissenschaft und Wirtschaft durch strategisch-programmatisch ausgerichtete Spitzenforschung in sechs Forschungsbereichen: Energie, Erde und Umwelt, Gesundheit, Schlüsseltechnologien, Struktur der Materie sowie Verkehr und Weltraum.");
        affiliationHelmholtz.setExternalId("239832-3232");
        affiliationHelmholtz.setRegion("Worldwide");
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
        initialize();
        AffiliationVO affiliationFraunhofer = new AffiliationVO();
        affiliationFraunhofer.setName("Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.");
        affiliationFraunhofer.setAbbreviation("FG");
        affiliationFraunhofer.setAddress("Hansastraße 27c");
        affiliationFraunhofer.setPostcode("80686 ");
        affiliationFraunhofer.setCity("München");
        affiliationFraunhofer.setTelephone("+49 (0) 89 / 12 05- 0");
        affiliationFraunhofer.setFax("+49 (0) 89 / 12 05-75 31");
        affiliationFraunhofer.setEmail("info@fraunhofer.de");
        try
        {
            affiliationFraunhofer.setHomepageUrl(new URL("http://www.fraunhofer.de"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationFraunhofer.setCountryCode("NO");
        affiliationFraunhofer
                .setDescription("Die Fraunhofer-Gesellschaft ist die führende Organisation für angewandte Forschung in Europa. Sie betreibt anwendungsorientierte Forschung zum direkten Nutzen für Unternehmen und zum Vorteil der Gesellschaft.");
        affiliationFraunhofer.setExternalId("4711");
        affiliationFraunhofer.setRegion("Worldwide");
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
        initialize();
        AffiliationVO affiliationMPIFG = new AffiliationVO();
        affiliationMPIFG.setName("Max-Planck-Institut für Gesellschaftsforschung");
        affiliationMPIFG.setAbbreviation("MPIFG");
        affiliationMPIFG.setAddress("Paulstraße 3");
        affiliationMPIFG.setPostcode("50676");
        affiliationMPIFG.setCity("Köln");
        affiliationMPIFG.setTelephone("+49 221 27 67-0");
        affiliationMPIFG.setFax("+49 221 2767-555");
        affiliationMPIFG.setEmail("info@mpifg.de");
        try
        {
            affiliationMPIFG.setHomepageUrl(new URL("http://www.mpifg.de"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationMPIFG.setCountryCode("DE");
        affiliationMPIFG
                .setDescription("Das Max-Planck-Institut für Gesellschaftsforschung ist eine Einrichtung der Spitzenforschung in den Sozialwissenschaften. Es betreibt anwendungsoffene Grundlagenforschung mit dem Ziel einer empirisch fundierten Theorie der sozialen und politischen Grundlagen moderner Wirtschaftsordnungen. Im Mittelpunkt steht die Untersuchung der Zusammenhänge zwischen ökonomischem, sozialem und politischem Handeln. Mit einem vornehmlich institutionellen Ansatz wird erforscht, wie Märkte und Wirtschaftsorganisationen in historisch-institutionelle, politische und kulturelle Zusammenhänge eingebettet sind, wie sie entstehen und wie sich ihre gesellschaftlichen Kontexte verändern. Das Institut schlägt eine Brücke zwischen Theorie und Politik und leistet einen Beitrag zur politischen Diskussion über zentrale Fragen moderner Gesellschaften.");
        affiliationMPIFG.setExternalId("4711-4712");
        affiliationMPIFG.setRegion("Cologne and the rest of the World");
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
        initialize();
        AffiliationVO affiliationMPI_G = new AffiliationVO();
        affiliationMPI_G.setName("Max-Planck-Institut zur Erforschung multireligiöser und multiethnischer Gesellschaften");
        affiliationMPI_G.setAbbreviation("MPI-G");
        affiliationMPI_G.setAddress("Hermann-Föge-Weg 11");
        affiliationMPI_G.setPostcode("37073");
        affiliationMPI_G.setCity("Göttingen");
        affiliationMPI_G.setTelephone("(+49 551) 49 56 - 0");
        affiliationMPI_G.setFax("(+49 551) 49 56 - 170");
        affiliationMPI_G.setEmail("Geschichte@mpi-g.gwdg.de");
        try
        {
            affiliationMPI_G.setHomepageUrl(new URL("http://www.geschichte.mpg.de/"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationMPI_G.setCountryCode("DE");
        affiliationMPI_G.setDescription("Die Mission Historique Francaise en Allemagne untersteht der Zuständigkeit des französischen Außenministeriums, "
                + "das durch eine aus französischen und deutschen Persönlichkeiten zusammengesetzte wissenschaftliche Kommission beraten wird.\n"
                + "Die Mission Historique Francaise en Allemagne wurde 1977 durch Robert Mandrou gegründet, der auch ihr erster Direktor war. "
                + "Heute nimmt sie im wissenschaftlichen Austausch der Historiker in Frankreich und Deutschland einen allgemein anerkannten Platz ein.\n"
                + "Die MHFA mietet Räumlichkeiten in den Gebäuden des Max-Planck-Instituts für Geschichte. Zu diesem Institut pflegt sie enge "
                + "Beziehungen, so wie mit vielen weiteren Hochschulen und Forschungseinrichtungen in Frankreich, Deutschland und anderen europäischen "
                + "Ländern. Sie hat flexible und anpassungsfähige institutionelle Strukturen entwickelt.");
        affiliationMPI_G.setExternalId("r2d2-49");
        affiliationMPI_G.setRegion("Milky way");
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        AffiliationVO affiliationFML = new AffiliationVO();
        affiliationFML.setName("Friedrich-Miescher-Laboratorium für biologische Arbeitsgruppen in der Max-Planck-Gesellschaft");
        affiliationFML.setAbbreviation("FML");
        affiliationFML.setAddress("Spemannstr. 39");
        affiliationFML.setPostcode("72076");
        affiliationFML.setCity("Tübingen");
        affiliationFML.setTelephone("+49 (7071) 601 - 800");
        affiliationFML.setFax("+49 (7071) 601 - 801");
        affiliationFML.setEmail("info@fml.tuebingen.mpg.de");
        try
        {
            affiliationFML.setHomepageUrl(new URL("http://www.fml.tuebingen.mpg.de"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationFML.setCountryCode("DE");
        affiliationFML.setDescription("Das Friedrich-Miescher-Laboratorium wurde 1969 gegründet, um besonders qualifizierten jungen Wissenschaftlern "
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
        affiliationFML.setExternalId("MPI-27892-UBUNTU");
        affiliationFML.setRegion("Tout le monde");
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        AffiliationVO affiliationMPIMF = new AffiliationVO();
        affiliationMPIMF.setName("Max-Planck-Institut für medizinische Forschung");
        affiliationMPIMF.setAbbreviation("MPIMF");
        affiliationMPIMF.setAddress("Jahnstraße 29");
        affiliationMPIMF.setPostcode("69120");
        affiliationMPIMF.setCity("Heidelberg");
        affiliationMPIMF.setTelephone("+49 (6221) 486 - 0");
        affiliationMPIMF.setFax("+49 (6221) 486 - 585");
        affiliationMPIMF.setEmail("info@mpimf-heidelberg.mpg.de");
        try
        {
            affiliationMPIMF.setHomepageUrl(new URL("http://www.mpimf-heidelberg.mpg.de/"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationMPIMF.setCountryCode("DE");
        affiliationMPIMF.setDescription("Das Institut wurde 1930 als Kaiser-Wilhelm-Institut gegründet, um Methoden der Physik und Chemie in die "
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
        affiliationMPIMF.setExternalId("MPI-HD-49°25'N,8°43'O");
        affiliationMPIMF.setRegion("Heidelberg und Umgebung");
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        AffiliationVO affiliationZEL = new AffiliationVO();
        affiliationZEL.setName("Zentrale Einrichtung Lichtmikroskopie des MPI für Medizinische Forschung");
        affiliationZEL.setAbbreviation("ZEL");
        affiliationZEL.setAddress("Jahnstr. 29");
        affiliationZEL.setPostcode("69120");
        affiliationZEL.setCity("Heidelberg");
        affiliationZEL.setTelephone("06221-486-360");
        affiliationZEL.setFax("06221-486-325");
        affiliationZEL.setEmail("guenter.giese@mpimf-heidelberg.mpg.de");
        try
        {
            affiliationZEL.setHomepageUrl(new URL("http://lightmicro.mpimf-heidelberg.mpg.de/index.html"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationZEL.setCountryCode("DE");
        affiliationZEL.setDescription("Die Zentrale Einrichtung Lichtmikroskopie des MPI fuer Medizinische Forschung soll:\n"
                + "* Wissenschaflern des Institutes und von außerhalb die Nutzung von komplexen, aktuellen Methoden der Lichtmikroskopie " + "und der lichtmikroskopischen Datenanalyse bieten\n"
                + "* Unterstützung und Training für Probenvorbereitung, Datenaufnahme und Datenanalyse bieten\n" + "* die Kommunikation und den Austausch von experimentellen Erfahrungen fördern");
        affiliationZEL.setExternalId("LIGHT-123");
        affiliationZEL.setRegion("Germany");
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        AffiliationVO affiliationMPH_HD = new AffiliationVO();
        affiliationMPH_HD.setName("Max-Planck-Haus Heidelberg");
        affiliationMPH_HD.setAbbreviation("MPH-HD");
        affiliationMPH_HD.setAddress("Humboldtstr. 13");
        affiliationMPH_HD.setPostcode("69120");
        affiliationMPH_HD.setCity("Heidelberg");
        affiliationMPH_HD.setTelephone("+49 6221 486-428");
        affiliationMPH_HD.setFax("+49 6221 486-455");
        affiliationMPH_HD.setEmail("lang@vw.mpimf-heidelberg.mpg.de");
        try
        {
            affiliationMPH_HD.setHomepageUrl(new URL("http://www.mpimf-heidelberg.mpg.de/serviceEinrichtungen/maxPlanckHaus/index.html"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationMPH_HD.setCountryCode("DE");
        affiliationMPH_HD.setDescription("Im Hörsaal des Max-Planck-Hauses können Vortäge und Seminarveranstaltungen stattfinden; folgende technische " + "Ausstattung steht zur Verfügung:\n"
                + "* Beamer\n" + "* Tageslichtprojektor\n" + "* Diaprojektor\n" + "* Beamer\n" + "* Breitband-Internetanschluss\n" + "* Leinwand\n" + "* Wandtafel\n"
                + "* Mikrofonanlage mit drei Funkmikrofonen\n" + "* DVD\n");
        affiliationMPH_HD.setExternalId("MPH-HD-20982022");
        affiliationMPH_HD.setRegion("Heidelberg");
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
        // make sure the XmlTransforming instance is properly initialized
        initialize();
        AffiliationVO affiliationFU_BERLIN = new AffiliationVO();
        affiliationFU_BERLIN.setName("Freie Universität Berlin");
        affiliationFU_BERLIN.setAbbreviation("FU-BERLIN");
        affiliationFU_BERLIN.setAddress("Kaiserswerther Str. 16/18");
        affiliationFU_BERLIN.setPostcode("14195");
        affiliationFU_BERLIN.setCity("Berlin");
        affiliationFU_BERLIN.setTelephone("(030) 838-1");
        affiliationFU_BERLIN.setFax("(030) 838-2");
        affiliationFU_BERLIN.setEmail("info@fu-berlin.de");
        try
        {
            affiliationFU_BERLIN.setHomepageUrl(new URL("http://www.fu-berlin.de/"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        affiliationFU_BERLIN.setCountryCode("DE");
        affiliationFU_BERLIN.setDescription("Die Freie Universität Berlin gehört zu den führenden Universitäten der Welt und zeichnet sich durch ihren "
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
        affiliationFU_BERLIN.setExternalId("FU-B-2103984");
        affiliationFU_BERLIN.setRegion("Berlin");
        return affiliationFU_BERLIN;
    }

    /**
     * @throws Exception
     */
    public static void deleteAllAffiliationsContainingThreeAsteriskes() throws Exception
    {
        String systemAdministratorUserHandle = loginSystemAdministrator();

        int nextLoopAffiliationCount;
        do
        {
            // retrieve all affiliations in the framework (again)
            OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle);
            String affiliationsXML = ouh.retrieveOrganizationalUnits(FILTER_ALL);
            // logger.debug(toString(getDocument(affiliationsXML, false), false));
            List<AffiliationVO> affiliations = xmlTransforming.transformToAffiliationList(affiliationsXML);

            nextLoopAffiliationCount = 0;
            for (AffiliationVO affiliation : affiliations)
            {
                // As affiliations with child affiliations cannot be removed, skip them in this loop run
                String affiliationName = affiliation.getName();
                logger.debug("Examining '" + affiliationName + "'");
                if (affiliationName.contains("***"))
                {
                    if (affiliation.getHasChildren() == false)
                    {
                        ouh.delete(affiliation.getReference().getObjectId());
                        logger.debug("-> " + affiliationName + "...DELETED");
                    }
                    else
                    {
                        nextLoopAffiliationCount++;
                    }
                }
            }
            logger.info("Test affiliation removal loop ended. nextLoopAffiliationCount = " + nextLoopAffiliationCount);
        }
        while (nextLoopAffiliationCount > 0);
        logout(systemAdministratorUserHandle);
    }
}
