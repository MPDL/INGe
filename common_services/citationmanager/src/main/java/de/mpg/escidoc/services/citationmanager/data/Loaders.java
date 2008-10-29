/*
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

/**
*
* Citation Style Processing. 
* Loaders.   
*
* @author vmakarenko (initial creation)  
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

package de.mpg.escidoc.services.citationmanager.data;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;

public class Loaders {
	
    /**
     * Loads JasperDesign XML file
     * @param name is name of CitationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
	

    public static void loadCitationStyleJRXml(ProcessCitationStyles pcs, String name) throws JRException, CitationStyleManagerException, IOException{
    	
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);

        try {
            pcs.setJasperDesign(JRXmlLoader.load( 
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/CitationStyle.jrxml"
            ));
        }
        catch (JRException e) {
            throw new JRException("Error by loading of jasperDesign: " + e);
        }
    }
    
    /**
     * Loads test JasperDesign XML file
     * @param name is name of CitationStyle
     * @throws CitationStyleManagerException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public void loadCitationStyleTestJRXml(ProcessCitationStyles pcs) throws JRException, CitationStyleManagerException, FileNotFoundException, IOException
    {
    	Utils.checkPcs(pcs);
        try {
            pcs.setJasperDesign(JRXmlLoader.load(
            		ResourceUtil.CITATIONSTYLES_DIRECTORY 
            		+ "/" + pcs.DEFAULT_STYLENAME 
            		+ "/citation-style-test.jrxml"
            ));
        }
        catch (JRException e) {
            throw new JRException("Error by loading of test jasperDesign: " + e);
        }
    }

    /**
     * Loads Default JasperDesign XML file for CitationStyle definition
     * @param path is path to directory of CitationStyle
     * @throws JRException
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public static void loadDefaultCitationStyleJRXml(ProcessCitationStyles pcs) throws JRException, CitationStyleManagerException, IOException
    {
    	Utils.checkPcs(pcs);
        loadCitationStyleJRXml(pcs, pcs.DEFAULT_STYLENAME);
    }	
    
    /**
     * Loads <code>jasperDesign</code> from DEFAULT_STYLENAME
     * and sets <code>name</code> to the name of new CitationStyle
     * @param path is a path to Ciation Styles Directory
     * @param name is a neame of new Citation Style
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public static void loadDefaultCitationStyleJRXml(ProcessCitationStyles pcs, String name) throws JRException, CitationStyleManagerException, IOException 
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);

    	loadCitationStyleJRXml(pcs, pcs.DEFAULT_STYLENAME); 
    	pcs.setJasperDesignDefaultProperties(name);
    }    
    
    /**
     * Loads complete Citation Style XMLs bundle  + jrXML
     * @param path
     * @param name
     * @throws JRException
     * @throws IOException
     * @throws CitationStyleManagerException 
     */
    public static void loadCitationStyleBundle(ProcessCitationStyles pcs, String name) throws JRException, IOException, CitationStyleManagerException 
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);

    	loadCitationStyleDefinitionXmls(pcs, name); 
    	loadDefaultCitationStyleJRXml(pcs, name);
    }    

	/**
     * Loads FontStyleCollection from XML
     * @param path A path to the directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public static void loadFontStylesFromXml(ProcessCitationStyles pcs) throws CitationStyleManagerException
    {
    	Utils.checkPcs(pcs);
    	
        try {
            pcs.setFsc(FontStylesCollection.loadFromXml( 
            		ResourceUtil.getPathToCitationStyles()
            		+ pcs.DEFAULT_STYLENAME 
            		+ "/" + ResourceUtil.FONTSTYLES_FILENAME + ".xml"));
        } catch (Exception e) {
            throw new CitationStyleManagerException(
            		"Error by FontStylesCollection loading: ", e);
        }
    }
    /**
     * Loads LayoutElementsCollection from XML
     * @param name is name of CiatationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public static void loadLayoutElementsFromXml(ProcessCitationStyles pcs, String name) throws CitationStyleManagerException
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);
    	
        try {
            pcs.setLec(LayoutElementsCollection.loadFromXml(
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/LayoutElements.xml"
            ));
        } catch (Exception e) {
        	throw new CitationStyleManagerException("Error by LayoutElementsCollection loading: ", e);
        }
    }
    
    
    public static void loadLayoutElementsFromXml(ProcessCitationStyles pcs) throws CitationStyleManagerException
    {
    	Utils.checkPcs(pcs);
    	loadLayoutElementsFromXml(
    			pcs,
    			pcs.DEFAULT_STYLENAME
    	);
    }

    /**
     * Loads CitationStylesCollection from XML
     * @param name is name of CiatationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public static void loadCitationStyleFromXml(ProcessCitationStyles pcs, String name) throws CitationStyleManagerException
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);
    	
        try {
            pcs.setCsc(CitationStylesCollection.loadFromXml(
            		ResourceUtil.getPathToCitationStyles()
            		+ name 
            		+ "/"
            		+ pcs.CITATION_XML_FILENAME
            ));
            pcs.setLec(pcs.getCsc().getCitationStyleByName(name).getLayoutElements());
        } catch (Exception e) {
        	throw new CitationStyleManagerException("Error by loading CitationStylesCollection: ", e);
        }
    } 
    
    /**
     * Loads Citation Style Definition XMLs:
     * FontStyles.xml, LayoutElements.xml and CitationStyle.xml
     * @param path path to ReportingTool
     * @param name is a name of a Citation Style
     * @throws CitationStyleManagerException 
     */
    public static void loadCitationStyleDefinitionXmls(ProcessCitationStyles pcs, String name) throws CitationStyleManagerException
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(name);

        loadFontStylesFromXml(pcs);
        //loadLayoutElementsFromXml(path, name);
        loadCitationStyleFromXml(pcs, name);
    }    
}
