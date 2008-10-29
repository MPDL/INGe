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

package de.mpg.escidoc.services.citationmanager.data;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/**
 * An instance of this class represents 
 * parameters which are defined for a {@link LayoutElement} 
 *
 * @author makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 146 $ $LastChangedDate: 2007-11-12 20:58:08 +0100 (Mon, 12 Nov 2007) $
 */


public class Parameters implements Cloneable {

    private static final Logger logger = Logger.getLogger(Parameters.class);
    
    private String validIf;				// validIf expression 			
    private String startsWith;			// startsWith
    private String endsWith;			// endsWith 
    private String delimiter;			// delimiter
    private String internalDelimiter;	// internalDelimiter is used in repeatable elements for subelements delimiting 
    private int maxCount;				// is used to limit the number of repeatable elements in output
    private String maxCountEndsWith;	// is used to inform that maxCount is exceeded
    private int maxLength;				// is used to limit the number characters in complete citation
    private String maxLengthEndsWith;	// is used to inform that maxLength is exceeded	
    private String position;			// name of position attribute of parameter element 

    private String fontStyleRef;		// reference to the used FontStyle
    
    // Positions in citation styles definition tree:
    // TODO: move the stuff to the Schematron   
    public final static String LE_P =       "0"; // layoutElement/parameters
    public final static String LE_Pr =      "1"; // layoutElement/parameters (repeatable)
    public final static String LE_E_LE_P =  "2"; // layoutElement/elements/layoutElement/parameters
    public final static String LE_E_LE_Pr = "3"; // layoutElement/elements/layoutElement/parameters (repeatable)
    public final static String CS_P =       "4"; // csLayoutDefinition/parameters
    public final static String CS_E_LE_P =  "5"; // csLayoutDefinition/elements/layoutElement/parameters
    

    // Table of applicability of parameters according to position in data tree
    // TODO: move the stuff to the Schematron   
    private static String[][] appTable = {
        { "validIf", LE_P, LE_Pr, LE_E_LE_P, CS_P, CS_E_LE_P },
        { "startsWith", LE_P, LE_Pr, LE_E_LE_P, LE_E_LE_Pr, CS_P, CS_E_LE_P },
        { "endsWith", LE_P, LE_Pr, LE_E_LE_P, LE_E_LE_Pr, CS_P, CS_E_LE_P },
        { "delimiter", LE_P, LE_Pr, LE_E_LE_P, CS_P, CS_E_LE_P },
        { "internalDelimiter", LE_Pr, CS_E_LE_P },
        { "fontStyle",  LE_P, LE_Pr, LE_E_LE_P, CS_E_LE_P },
        { "maxLength", LE_P, LE_Pr, LE_E_LE_P, LE_E_LE_Pr, CS_P, CS_E_LE_P },
        { "maxLengthEndsWith", LE_P, LE_Pr, LE_E_LE_P, LE_E_LE_Pr, CS_P, CS_E_LE_P },
        { "maxCount", LE_Pr, CS_E_LE_P },
        { "maxCountEndsWith", LE_Pr, CS_E_LE_P },
        { "position", LE_Pr, CS_E_LE_P }, //???
    };


    /**
     * Constructor
     */
    public Parameters(){
        setDefault();
    }

    /**
     * Sets default parameters.
     * TODO: move to the properties
     */
    public void setDefault(){
        setValidIf("");
        setStartsWith("");
        setEndsWith("");
        setDelimiter(" ");
        setInternalDelimiter(" ");
        setMaxCount(0);
        setMaxCountEndsWith("...");
        setMaxLength(0);
        setMaxLengthEndsWith("...");
        setFontStyleRef(FontStylesCollection.DEFAULT_FONTSTYLE_NAME);
        setPosition(LayoutElement.DEFAULT_POSITION_NAME);
    }

    /**
     * validIf setter-getter
     * @param newValidIf
     */
    public void setValidIf ( String newValidIf ) {
        validIf = newValidIf;
    }

    /**
     * validIf getter
     * @return validIf
     */
    public String getValidIf() {
        return validIf;
    }

    /**
     * startsWith setter
     * @param newStartsWith
     */
    public void setStartsWith ( String newStartsWith ) {
        startsWith = newStartsWith;
    }
    /**
     * startsWith getter
     * @return startsWith
     */
    public String getStartsWith() {
        return startsWith;
    }

    /**
     * endsWith setter
     * @param newStartsWith
     */
    public void setEndsWith ( String newEndsWith ) {
        endsWith = newEndsWith;
    }
    /**
     * endsWith getter
     * @return endsWith
     */
    public String getEndsWith() {
        return endsWith;
    }

    /**
     * delimiter setter
     * @param newDelimiter
     */
    public void setDelimiter ( String newDelimiter ) {
        delimiter = newDelimiter;
    }
    /**
     * delimiter getter
     * @return delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }


    /**
     * internalDelimiter setter
     * @param newInternalDelimiter
     */
    public void setInternalDelimiter ( String newInternalDelimiter ) {
        internalDelimiter = newInternalDelimiter;
    }
    /**
     * internalDelimiter getter
     * @return internalDelimiter
     */
    public String getInternalDelimiter() {
        return internalDelimiter;
    }


    /**
     * maxCount setter
     * @param newMaxCount
     */
    public void setMaxCount( int newMaxCount ) {
        maxCount = newMaxCount;
    }
    /**
     * maxCount getter
     * @return maxCount
     */
    public int getMaxCount() {
        return maxCount;
    }

    /**
     * maxCountEndsWith setter
     * @param newMaxCountEndsWith
     */
    public void setMaxCountEndsWith ( String newMaxCountEndsWith ) {
        maxCountEndsWith = newMaxCountEndsWith;
    }
    /**
     * maxCountEndsWith getter 
     * @return maxCountEndsWith
     */
    public String getMaxCountEndsWith() {
        return maxCountEndsWith;
    }

    /**
     * maxLength setter
     * @param newMaxLength
     */
    public void setMaxLength( int newMaxLength ) {
        maxLength = newMaxLength;
    }
    /**
     * maxLength getter
     * @return maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * maxLengthEndsWith setter
     * @param newMaxLengthEndsWith
     */
    public void setMaxLengthEndsWith ( String newMaxLengthEndsWith ) {
        maxLengthEndsWith = newMaxLengthEndsWith;
    }
    /**
     * maxLengthEndsWith getter
     * @return maxLengthEndsWith
     */
    public String getMaxLengthEndsWith() {
        return maxLengthEndsWith;
    }

    /**
     * fontStyleRef setter
     * @param newFontStyleRef
     */
    public void setFontStyleRef( String newFontStyleRef ) {
        fontStyleRef = newFontStyleRef;
    }
    /**
     * fontStyleRef getter
     * @return fontStyleRef
     */
    public String getFontStyleRef() {
        return fontStyleRef;
    }

    /**
     * position setter 
     * @param newPosition
     */
    public void setPosition ( String newPosition ) {
        position = newPosition;
    }
    /**
     * position getter 
     * @return position
     */
    public String getPosition() {
        return position;
    }


    /**
     * Creates org.w3c.dom.Element for the instance
     * @param d is org.w3c.dom.Document context of the Element 
     * @return org.w3c.dom.Element
     * @throws ParserConfigurationException 
     */
    public Element getDomElement(Document d) {

    	Element e;
    	
    	 Element root = d.createElement("parameters");
       
        if (position != null && position.length() > 0 && !position.equals("default")) {
        	root.setAttribute("position", position);
        }

        if (validIf != null && validIf.length() > 0) {
      		e = d.createElement("valid-if");
      		e.appendChild(d.createCDATASection(validIf));
            root.appendChild(e);
        }

      	if (startsWith!=null && startsWith.length()>0) {
      		e = d.createElement("starts-with");
      		e.setAttribute("value", startsWith);
      		root.appendChild(e);
         }
        
      	if (endsWith!=null && endsWith.length()>0) {
      		e = d.createElement("ends-with");
      		e.setAttribute("value", endsWith);
      		root.appendChild(e);
         }
        
       	if (delimiter!=null && !delimiter.equals(" ")) {
      		e = d.createElement("delimiter");
      		e.setAttribute("value", delimiter);
      		root.appendChild(e);
         }

       	if (internalDelimiter!=null && !internalDelimiter.equals(" ")) {
      		e = d.createElement("internal-delimiter");
      		e.setAttribute("value", internalDelimiter);
      		root.appendChild(e);
         }

        if (fontStyleRef!=null && fontStyleRef.length()>0 && !fontStyleRef.equals("NORMAL")) {
      		e = d.createElement("font-style");
      		e.setAttribute("ref", fontStyleRef);
      		root.appendChild(e);
        }

        if (maxCount>0) {
        	Element mc = d.createElement("max-count");
            mc.setAttribute("value", "" + maxCount);
            if (maxCountEndsWith != null && maxCountEndsWith.length() > 0) 
                mc.appendChild(d.createCDATASection(maxCountEndsWith));
            root.appendChild(mc);
        }

        if (maxLength>0) {
        	Element ml = d.createElement("max-length");
            ml.setAttribute("value", "" + maxLength);
            if (maxLengthEndsWith != null && maxLengthEndsWith.length() > 0)
            	ml.appendChild(d.createCDATASection(maxLengthEndsWith));
            root.appendChild(ml);
        }

        return root;
    }
    
    /**
     * Creates rules for org.apache.commons.digester.Digester for the instance 
     * @param dig is the <code>Digester</code> rules for anchestor element    
     * @param path is a position of {@link Parameters} element in {@link CitationStyle} or {@link LayoutElement} tree
     * @return org.apache.commons.digester.Digester
     */
    public static Digester getDigesterRules (Digester dig, String path) {
    	
    	path += "/parameters"; 

    	dig.addObjectCreate(path, Parameters.class.getName());

    	dig.addSetProperties(path);

    	dig.addBeanPropertySetter(path+"/valid-if", "validIf");

//  	dig.addBeanPropertySetter(path+"/startsWith");
    	dig.addSetProperties(path+"/starts-with", "value", "startsWith");

//  	dig.addBeanPropertySetter(path+"/endsWith");
    	dig.addSetProperties(path+"/ends-with", "value", "endsWith");

//  	dig.addBeanPropertySetter(path+"/delimiter", "delimiter");
    	dig.addSetProperties(path+"/delimiter", "value", "delimiter");

//  	dig.addBeanPropertySetter(path+"/internalDelimiter");
    	dig.addSetProperties(path+"/internal-delimiter", "value", "internalDelimiter");

//  	dig.addSetProperties(path+"/fontStyle");
    	dig.addSetProperties(path+"/font-style", "ref", "fontStyleRef");

    	dig.addBeanPropertySetter(path+"/max-count", "maxCountEndsWith");
    	dig.addSetProperties(path+"/max-count", "value", "maxCount");

    	dig.addBeanPropertySetter(path+"/max-length", "maxLengthEndsWith");
    	dig.addSetProperties(path+"/max-length", "value", "maxLength");

//  	dig.addSetProperties(path+"/position", "value", "position");
    	
    	dig.addSetNext(path, "setParameters" );

    	return dig;
    }

    /**
     * Gets the list of all available parameters
     * @return list of parameters' names are available
     */
    public static String[] paramsList () {
        String[] array = new String[appTable.length];
        for (int i = 0; i < appTable.length; i++) {
            array[i] = appTable[i][0];
        }
        return array;
    }

    /**
     * Returns true if parameter is applicable to location in {@link CitationStyle} definition tree
     * TODO: move to the Schematron
     * @param param is A parameters
     * @param loc A location in {@link CitationStyle} definition tree
     * @return true - if param is applicable to loc, false otherwise
     * @throws CitationStyleManagerException if param is null or wrong and if loc is null
     */
    public static boolean paramIsApplicableToLoc(String param, String loc) throws CitationStyleManagerException {
        boolean flag1 = false;
        if (param == null) {
            throw new CitationStyleManagerException("Null parameter");
        }
        if (loc == null) {
            throw new CitationStyleManagerException("Null location for parameter");
        }
        for (int i = 0; i < appTable.length; i++) {
            if (param.equals(appTable[i][0])) {
                flag1 = true;
                for (int j = 1; j < appTable[i].length; j++) {
                    if (loc.equals(appTable[i][j])) {
                        return true;
                    }
                }
            }
        }
        if (!flag1) {
            throw new CitationStyleManagerException("There is no parameter:" + param);
        }
        return false;
    }

    public Object clone() {
        Object clone = null;
        try {
          clone = super.clone();
        } catch(CloneNotSupportedException e) {
          // should never happen
        }
        return clone;
    }

    public String toString() {
        return "Parameters[" +
            "valid-if:"+ validIf +
            ",starts-with:" + startsWith +
            ",ends-with:" + endsWith +
            ",delimiter:" + delimiter +
            ",internal-delimiter:" + internalDelimiter +
//          ",fontStyle:" + fontStyle +
            ",font-style-ref:" + fontStyleRef +
            ",max-count:" + maxCount +
            ",max-count-ends-with:" + maxCountEndsWith +
            ",max-length:" + maxLength +
            ",max-length-ends-with:" +  maxLengthEndsWith +
            ",position:" + position +
        "]";
    }


    public static void main(String[] args) throws ParserConfigurationException, IOException, CitationStyleManagerException {

    	Parameters p = new Parameters();
        p.setStartsWith("start-with-test");
        p.setValidIf("Valid if Test");
    	
    	Document d = XmlHelper.createDocument();
        
        d.appendChild(p.getDomElement(d));        
        
        logger.info("Default Parameters:" + p);
        logger.info("XML:" + XmlHelper.outputString(d));
        logger.info("Clone Parameters:" + p.clone());

        try {
        	logger.info("has:" + paramIsApplicableToLoc("maxCount", LE_Pr) );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
