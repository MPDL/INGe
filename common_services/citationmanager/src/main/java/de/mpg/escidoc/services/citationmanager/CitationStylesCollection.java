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

package de.mpg.escidoc.services.citationmanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * An instance of this class represents 
 * a Collection of {@link CitationStyle}s 
 * 
 * $Rev$
 * $Author$  
 * $Date$ 
 */
public class CitationStylesCollection implements Cloneable  {

    private String name;						// name of CitationStyle
    private List<CitationStyle> citationStyles;	// list of CitationStyles


    public CitationStylesCollection() {
    	setDefault();
	}

    
    private void setDefault() {
    	setName("");
    	setCitationStyles(new ArrayList<CitationStyle>());
	}

    public List<CitationStyle> getCitationStyles() {
		return citationStyles;
	}

	public void setCitationStyles(List<CitationStyle> citationStyles) {
		this.citationStyles = citationStyles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Add {@link CitationStyle} <code>cs</code> to <code>citationStyles</code> list  
	 * @param cs
	 */
	public void addCitationStyle( CitationStyle cs ) {
        if ( cs != null && getCitationStyleByName( cs.getName() ) == null )
            citationStyles.add( cs );
    }

    /**
     * Returns {@link CitationStyle} according to <code>name</code>
     * @param name is a name of {@link CitationStyle} to be found
     * @return {@link CitationStyle} or null if no <code>CitationStyle</code> has been found in <code>citationStyles</code> 
     */
    public CitationStyle getCitationStyleByName(String name) {
        for ( CitationStyle cs: citationStyles ) {
            if (cs!=null && cs.getName()!= null && cs.getName().equals(name))
                return cs;
        }
        return null;
    }

    /**
     * Returns {@link CitationStyle} if it contains {@link LayoutElement} according to <code>name</code>
     * @param name is a name of {@link LayoutElement} to be found
     * @return {@link CitationStyle} or null if no <code>CitationStyle</code> has been found in <code>citationStyles</code>
     */
    public CitationStyle getCitationStyleByLayoutElementName(String name) {
        for ( CitationStyle cs: citationStyles ) {
            if (cs!=null && cs.getElementByName(name)!=null)
                return cs;
        }
        return null;
    }
    
    /**
     * Returns CitationStyle if it contains LayoutElement according to id
     * @param name is a name of LayoutElement to find
     * @return CitationStyle or null otherwise
     */    public CitationStyle getCitationStyleByLayoutElementId(String id) {
        for ( CitationStyle cs: citationStyles ) {
            if (cs!=null && cs.getElementById(id)!=null)
                return cs;
        }
        return null;
    }
    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch(CloneNotSupportedException e) {
        // should never happen
        }

        ((CitationStylesCollection)clone).setName(getName());

        ((CitationStylesCollection)clone).setCitationStyles(new ArrayList<CitationStyle>());

        for (  CitationStyle cs: citationStyles ) {
            ((CitationStylesCollection)clone).addCitationStyle((CitationStyle)cs.clone());
        }


        return clone;
    }

    /**
     * Fills empty names for all <code>citationStyles</code> with empty names
     */    
    public void fillEmptyNames() {
        int count = 0;
        for ( CitationStyle cs: getCitationStyles() ) {
            cs.fillEmptyNames( getName() + "_CSC_" + count++ );
        }
    }

    /**
     * Generates uniq Ids for all <code>citationStyles</code> which are presented in the {@link CitationStylesCollection}
     */
    public void generateIDs () {
        int count = 1;
        for ( CitationStyle cs: getCitationStyles() ) {
            cs.generateIDs("CS_" + count++ );
        }
    }
    
    public String toString() {

        String str = "";
        int i = 0;
        for ( CitationStyle cs: citationStyles ) {
            i++;
            str += i+") name: " + name + "," + cs + "\n";
        }
        return str;
    }


    /**
     * Loads {@link CitationStylesCollection} from xmlfile
     * @param xmlFileName
     * @return {@link CitationStylesCollection}
     * @throws IOException 
     * @throws SAXException
     */      
    public static CitationStylesCollection loadFromXml( String xmlFileName )  throws IOException, SAXException {


        Digester digester = new Digester();
        digester.setValidating(false);

        // add root level
        String csc = "citation-styles-collection";
        digester.addObjectCreate(csc, "de.mpg.escidoc.services.citationmanager.CitationStylesCollection");
        digester.addSetProperties(csc);

            // add citation-style
            String cs = csc + "/citation-style";
            digester.addObjectCreate(cs, "de.mpg.escidoc.services.citationmanager.CitationStyle");
            digester.addSetProperties(cs);
            digester.addSetProperties(csc, "element-specific", "elementSpecific");
            digester.addSetProperties(csc, "read-only", "readOnly");

                // add cs-layout-definition
                String csld = cs + "/cs-layout-definition";
                digester.addObjectCreate(csld, "de.mpg.escidoc.services.citationmanager.LayoutElement");
                digester.addSetProperties(csld);

 
                    // add cs-layout-definition
                    digester = LayoutElement.getDigesterRules(digester, csld);
                    String elems = csld + "/elements/layout-element";


                        // add cs-layout-definition/elements/layout-element
                        digester = LayoutElement.getDigesterRules(digester, elems);
                        elems += "/elements/layout-element";

                            
                            // add cs-layout-definition/elements/layout-element/elements/layout-element
                            digester = LayoutElement.getDigesterRules(digester, elems);
                            elems += "/elements/layout-element";
                            
                            	// add cs-layout-definition/elements/layout-element/elements/LayoutElement/elements/LayoutElement/parameters
                                digester = Parameters.getDigesterRules(digester, elems);
                            

                digester.addSetNext(csld, "addCsLayoutDefinition");

            digester.addSetNext(cs, "addCitationStyle");


        FileInputStream input = new FileInputStream( xmlFileName );
        CitationStylesCollection cscl = (CitationStylesCollection)digester.parse( input );

        cscl.fillEmptyNames();
        cscl.generateIDs();

        return cscl;

    }

    /**
     * Writes {@link CitationStylesCollection} to xmlfile
     * @param xmlFileName is name of output xmlfile
     * @throws IOException 
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */       
    public void writeToXml( String xmlFileName )  throws IOException, SAXException, CitationStyleManagerException {

    	Document d = XmlHelper.createDocument();
    	
    	Element root = d.createElement("citation-styles-collection");
        root.setAttribute ("name", getName());
        root = getDomElement(d, root);
        d.appendChild(root);
        
        XmlHelper.output(d, xmlFileName);
        
    }

    /**
     * Creates Element for complete {@link CitationStylesCollection} 
     * @param e is a root element for created xml subtree
     * @return
     */
    public Element getDomElement(Document d, Element e) {
        for ( CitationStyle cs: citationStyles ) {
            e.appendChild( cs.getDomElement(d) );
        }
        return e;
    }



    public static void main(String[] args)  throws IOException, SAXException, CitationStyleManagerException{


        CitationStylesCollection csc = CitationStylesCollection.loadFromXml("CitationStyles/APA/CitationStyle.xml");
//        csc.writeToXml("resource/CitationStyles/APA/CitationStyleTestOutput.xml");
//        CitationStylesCollection csc = CitationStylesCollection.loadFromXml("CitationStyles\\CitationStyle.xml");


        System.out.println(csc);

//        CitationStyle cs = csc.getCitationStyleByName("CitationStyle_eDoc");
//
//        System.out.println("<<<<Citation Style>>>>:" + cs);


//        csc.writeToXml("CitationStyles\\CitationStylesOut.xml");

//        CitationStylesCollection cscclone = (CitationStylesCollection)csc.clone();

//        cscclone.writeToXml("CitationStyles\\CitationStylesOutClone.xml");

    }
}