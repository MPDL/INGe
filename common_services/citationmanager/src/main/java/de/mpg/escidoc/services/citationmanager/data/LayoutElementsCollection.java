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


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.digester.Digester;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/**
 * An instance of this class represents 
 * a Collection of {@link LayoutElement}s 
 * 
 * @author vmakarenko (initial creation)  
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class LayoutElementsCollection implements Cloneable {

    private String name;						// Name of LayoutElementsCollection 
    private List<LayoutElement> layoutElements;	// oredred list of LayoutElements

    public LayoutElementsCollection() {
    	setDefault();
	}
    
    public void setDefault() {
    	setName("LayoutElementCollectionName");
    	setLayoutElements(new ArrayList<LayoutElement>());
	}

	public List<LayoutElement> getLayoutElements() {
		return layoutElements;
	}

	public void setLayoutElements(List<LayoutElement> layoutElements) {
		this.layoutElements = layoutElements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds <code>le</code> to <code>layoutElements</code> ordered list  
	 * @param le
	 */
	public void addLayoutElement(LayoutElement le) {
        if (le==null)
            return;
        String name = le.getName();
        if (name!=null && name.length()==0)
            le.setName(null);
        layoutElements.add(le);
    }
    
    /**
     * Searches for {@link LayoutElement} by <code>name</code> in complete list of <code>layoutElements</code>  
     * @param name
     * @return LayoutElement or null if not found 
     */
    public LayoutElement getElementByName(String name) {
        if (name==null || name.length()==0)
            return null;
        for (LayoutElement le: layoutElements) {
            if (le.getElementByName(name)!=null)
                return le;
        }
        return null;
    }

    /**
     * Fills all empty names according to position in {@link LayoutElementsCollection} 
     */
    public void fillEmptyNames() {
        int count = 0;
        for ( LayoutElement le: layoutElements ) {
            le.fillEmptyNames( getName() + "_LE_" + count++ );
        }
    }

    /**
     * Generates uniq <code>Id</code>s for {@link LayoutElement}s according to position in tree
     */
    public void generateIDs () {
        int count = 0;
        for ( LayoutElement le: layoutElements ) {
            le.generateIDs("LE_" + count++ );
        }
    }
    

    /**
     * Searches for {@link LayoutElement} by <code>id</code> in complete list of <code>layoutElements</code>  
     * @param name
     * @return LayoutElement or null if not found 
     */
    public LayoutElement getElementById(String id) {
        if (id==null || id.length()==0)
            return null;
        for (LayoutElement le: layoutElements) {
        	LayoutElement e = le.getElementById(id); 
        	if (e!=null) 
        		return e; 
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

        ((LayoutElementsCollection)clone).setLayoutElements(new ArrayList<LayoutElement>());

        for ( LayoutElement le: layoutElements ) {
            ((LayoutElementsCollection)clone).addLayoutElement((LayoutElement)le.clone());
        }
        return clone;
    }


    public String toString() {
        String str = "";
        int i = 0;
        for ( LayoutElement le: layoutElements ) {
            i++;
            str += i+") name: " + name + "," + le + "\n";
        }
        return str;
    }


    /**
     * Loads {@link LayoutElementsCollection} from xmlfile
     * @param xmlFileName
     * @return LayoutElementsCollection
     * @throws IOException 
     * @throws SAXException
     */    
    
    public static LayoutElementsCollection loadFromXml( String xmlFileName )  throws IOException, SAXException {

        Digester digester = new Digester();
        digester.setValidating(false);


        // add root level
        digester.addObjectCreate("layoutElementsCollection", LayoutElementsCollection.class.getName());
        digester.addSetProperties("layoutElementsCollection");
        
        String path = "layoutElementsCollection/layout-element";
        
        digester.addObjectCreate(path, LayoutElement.class.getName());
        digester.addSetProperties(path);

	        digester = LayoutElement.getDigesterRules(digester, path);
	        path += "/elements/layout-element";
	
	        digester = LayoutElement.getDigesterRules(digester, path);
	        path += "/elements/layout-element";
	        
	        digester = LayoutElement.getDigesterRules(digester, path);
	        path += "/elements/layout-element";
	    
	     digester.addSetNext("layoutElementsCollection/layout-element", "addLayoutElement");

        //        // add layoutElement
//        digester.addObjectCreate("layoutElementsCollection/layoutElement", "de.mpg.escidoc.services.citationmanager.data.LayoutElement");
//        digester.addSetProperties("layoutElementsCollection/layoutElement");
//
//        // add layoutElement/parameters
//        String params = "layoutElementsCollection/layoutElement/parameters";
//        digester = Parameters.getDigesterRules(digester , params);
//
//
//        //add layoutElement/elements
//        String elems = "layoutElementsCollection/layoutElement/elements/layoutElement";
//        digester.addObjectCreate(elems, "de.mpg.escidoc.services.citationmanager.data.LayoutElement");
//        digester.addSetProperties(elems);
//        digester.addSetNext(elems, "addElement");
//
//
//        params = "layoutElementsCollection/layoutElement/elements/layoutElement/parameters";
//        digester = Parameters.getDigesterRules(digester, params);


//        digester.addSetNext("layoutElementsCollection/layoutElement", "addLayoutElement");


        FileInputStream input = new FileInputStream(xmlFileName);
        LayoutElementsCollection lec = (LayoutElementsCollection)digester.parse(input);

        lec.fillEmptyNames();
        lec.generateIDs();

        return lec;

    }

    /**
     * Writes {@link LayoutElementsCollection} to xmlfile
     * @param xmlFileName
     * @throws IOException 
     * @throws SAXException
     * @throws ParserConfigurationException 
     * @throws CitationStyleManagerException 
     */    
    
    public void writeToXml( String xmlFileName )  throws IOException, SAXException, CitationStyleManagerException {

    	Document d = XmlHelper.createDocument();
    	
    	Element root = d.createElement("layoutElementsCollection");
    	
        root.setAttribute ("name", getName());
        
        root = getDomElement(d, root);
        
        d.appendChild(root);

        XmlHelper.output(d, xmlFileName);

    }

    /**
     * Returns org.w3c.dom.Element for {@link LayoutElementsCollection} having <code>e</code> as root element 
     * @param e is a root element 
     * @return org.w3c.dom.Element for {@link LayoutElementsCollection}
     */
    
    public Element getDomElement(Document d, Element e) {
        for ( LayoutElement le: layoutElements ) {
            e.appendChild( le.getDomElement(d) );
        }
        return e;
    }
    

    public static void main(String[] args)  throws IOException, SAXException, CitationStyleManagerException{

//        LayoutElementsCollection lec = new LayoutElementsCollection();
//        LayoutElement le = new LayoutElement();
//        lec.addLayoutElement( le );

        LayoutElementsCollection lec = LayoutElementsCollection.loadFromXml("CitationStyles/Default/LayoutElements.xml");

        System.out.println(lec);
        
//        System.out.println("ja crutoj!!!:" + lec.getElementById("LE_1_P_default_E_3"));

//        lec.writeToXml("resource/CitationStyles/Default/LayoutElementsOutputTest.xml");

//        LayoutElementsCollection lecclone = (LayoutElementsCollection)lec.clone();

//        lecclone.writeToXml("LayoutElementsOutClone.xml");



    }
}