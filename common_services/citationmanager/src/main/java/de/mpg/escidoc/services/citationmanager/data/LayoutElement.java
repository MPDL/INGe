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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.digester.Digester;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/* 
 * An instance of this class represents a layout element
 * 
 * @author $Author:$ (last modification)
 * @version $Revision:$ $LastChangedDate:$
 *
 * */
public class LayoutElement implements Cloneable {

    private String name;			// Name of LE;
    private String ref;				// String value of @ref
    private String func;			// Name of applicable function
    private String repeatable;		// indicator of repeatable data
    private String position;		// indicator of position for repeatable le
    private String id;				// internal uniq id
    public	boolean hasName;		// shows whether LE has user defined name
    
    public static final String DEFAULT_POSITION_NAME = "default";

    private Parameters parameters;	// parameters of LE at current position	
    private List<LayoutElement> elements; // elements of LE at current position (ordered list of LE)
    
    // parameters and elements will be added to position bundle according to position 
    private HashMap<String, HashMap> positionBundle; 

    
	public LayoutElement() {
        setDefault();
    }
	
    public void setDefault(){
        setName(""); 
        setRef("");
        setFunc("");
        setRepeatable("");
        setPosition(DEFAULT_POSITION_NAME);
        setId(null);
        hasName = true;
        
    	setParameters(new Parameters());
    	setElements(new ArrayList<LayoutElement>());
    	setPositionBundle(new HashMap<String, HashMap>());
    }

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
    
    public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(String repeatable) {
		this.repeatable = repeatable;
	}

	public boolean _isRepeatable() {
		return repeatable!=null && repeatable.equalsIgnoreCase("yes"); 
	}
    	
    public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
    public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public List<LayoutElement> getElements() {
		return elements;
	}

	public void setElements(List<LayoutElement> elements) {
		this.elements = elements;
	}

	public boolean hasElements() {
        return elements.size() > 0;
    }
    
    public boolean hasElementsAtDefault() {
        return getElementsAtDefault().size() > 0;
    }    

    public void addElement ( LayoutElement le ) {
        if ( le != null && getElementByName(le.getName()) == null ) {
            elements.add( le );
        }
    }

        
    /**
     *	Takes <code>elements</code> and <code>parameters</code> and adds 
     *  them to PositionBundle according to <code>position</code>.
     *	Resets <code>elements</code> and <code>parameters</code>. 
     * @throws CitationStyleManagerException 
     */
    public void addPositionBundle() throws CitationStyleManagerException 
    	//throws CitationStyleManagerException 
    {
    	String err = "Cannot add to position bundle: ";
    	if (!position.equals(parameters.getPosition())) {
    		throw new CitationStyleManagerException(err + "different positions for parameters(" + 
    				parameters.getPosition() + 
    				") and elements(" + 
    				position + ")");
    	} else if 
    		(
//    			elements!=null && elements.size()>0 &&
    			parameters!=null && 
    			position!=null && position.length()>0
    		) {
    		HashMap<String, Object> hm = new HashMap<String, Object>();
    		hm.put("parameters", parameters);
    		hm.put("elements", elements);
    		positionBundle.put(position, hm);

    		//reset parameters & elements
    		setParameters(new Parameters());
    		setElements(new ArrayList<LayoutElement>());
    		setPosition(DEFAULT_POSITION_NAME);
    	}
    }
    
    /**
     * Returns a <code>positionBundle</code> according to <code>position</code> 
     * @param position - a position key
     * @return HashMap
     */
    public HashMap getPositionBundleAt(String position){
		return (HashMap)positionBundle.get(position);
    }

    /**
     * Returns a <code>positionBundle</code> at <code>DEFAULT_POSITION_NAME</code> 
     * @param position - a position key
     * @return HashMap
     */
    public HashMap getPositionBundleAtDefault(){
		return (HashMap)positionBundle.get(DEFAULT_POSITION_NAME);
    }

    public void setPositionBundle(HashMap<String, HashMap> newPositionBundle){
		positionBundle = newPositionBundle;
    }
    
    public ArrayList<LayoutElement> getElementsAt(String position){
    	HashMap hm = getPositionBundleAt(position);
		return hm!=null ? (ArrayList)hm.get("elements") : new ArrayList<LayoutElement>();
    }
    
    public ArrayList getElementsAtDefault(){
		return getElementsAt(DEFAULT_POSITION_NAME);
    }
    
    public Parameters getParametersAt(String position){
    	HashMap hm = getPositionBundleAt(position);
		return hm!=null ? (Parameters)hm.get("parameters") : new Parameters();
    }

    public Parameters getParametersAtDefault(){
		return getParametersAt(DEFAULT_POSITION_NAME);
    }

    public HashMap getPositionBundle(){
		return positionBundle;
    }
    
    public boolean hasPositionBundle(){
		return positionBundle.keySet().size()>0;
    }


    /**
     * Get element by <code>name</code> 
     * @param name
     * @return LayoutElement or null
     */
    public LayoutElement getElementByName (String name) {
    	if (name==null || name.length()==0) {
    		return null;
    	} else if (getName().equals(name)) {
    		return this;
    	} else {
    		Iterator it = positionBundle.keySet().iterator();
    		while (it.hasNext()) {
    			String key = (String)it.next();
    			ArrayList<LayoutElement> elems = getElementsAt(key);
    			if (elems.size()>0) {
    				for (LayoutElement e: elems) {
    					LayoutElement le = e.getElementByName(name);
    					if (le != null)
    						return le;
    				}
    			}
    		}
    	}	    
    	return null;
    }

    /**
     * Walks around layoutElement and sets names according to position in tree for empty names  
     * @param prefix is given from parent element
     */
    public void fillEmptyNames(String prefix) {
    	String name = getName();
    	if (name==null || (name!=null && name.equals(""))) {
    		hasName = false; 
    		setName( prefix );
    	} else {
    		prefix += "_" + getName();
    		int count = 0;
    		Iterator it = positionBundle.keySet().iterator();
    		while (it.hasNext()) {
    			String key = (String)it.next();
    			ArrayList<LayoutElement> elems = getElementsAt(key);
    			for ( LayoutElement e: elems ) {
    				e.fillEmptyNames( prefix + "_P_" + key + "_E_" + count++ );
    			}
    		}
    	}
    }
    
   

    /**
     * Walk around completet collection and generate uniq ids for layout elements
     * @param prefix has uniq path to the element in xml tree
     */
    public void generateIDs ( String prefix ) {
    	setId( prefix );
    	if (getElementsAtDefault().size() > 0) {
    		int count = 1;
    		Iterator<String> it = positionBundle.keySet().iterator();
    		while (it.hasNext()) {
    			String key = (String)it.next();
    			ArrayList<LayoutElement> elems = getElementsAt(key);
    			for ( LayoutElement e: elems ) {
    				e.generateIDs( prefix + "_P_" + key + "_E_" + count++ );
    			}
    		}
    	}
    }

    /**
     * Get element by uniqal <code>id</code> 
     * @param id
     * @return LayoutElement or null
     */
    public LayoutElement getElementById(String id) {
    	if (id==null || id.length()==0) {
    		return null;
    	}  else if (id.equals(getId())) {
    		return this;
    	} else {
    		Iterator it = positionBundle.keySet().iterator();
    		while (it.hasNext()) {
    			String key = (String)it.next();
    			ArrayList<LayoutElement> elems = getElementsAt(key);
    			for ( LayoutElement e: elems ) {
    				LayoutElement ee = e.getElementById(id);
    				if (ee!=null)
    					return ee;
    			}
    		}
    	}
    	return null;
    }
  
    /**
     * Digester rules for LayoutElement
     * @param dig
     * @param root
     * @return
     */
    public static Digester getDigesterRules(Digester dig, String root) {
    	
    	String path = root;
    	
    	dig = Parameters.getDigesterRules(dig, root);
    	
//    	elements@position
    	dig.addSetProperties(root + "/elements");
    	
        	path += "/elements/layout-element";
        	dig.addObjectCreate(path, LayoutElement.class.getName());
        	dig.addSetProperties(path);
        	dig.addSetNext(path, "addElement" );
        	
        	dig.addCallMethod(root + "/elements", "addPositionBundle");	
        
        return dig;
    }
    
    
    
    public Element getDomElement(Document d, String root) {
    	
    	org.w3c.dom.Element element = d.createElement(root);
    	
    	if (hasName && !(name==null || name.length()==0)) {
    		element.setAttribute("name", name);
    	}
    	
    	if ( ref.length() > 0 ) {
    		element.setAttribute("ref", ref);
    	}
    	
    	if ( func.length() > 0 ) {
    		element.setAttribute("func", func);
    	}
    	
    	if ( repeatable.length() > 0 ) {
    		element.setAttribute("repeatable", repeatable);
    	}
    	
    	Iterator it = positionBundle.keySet().iterator();
    	while (it.hasNext()) {
    		String key = (String)it.next();
    		
    		Parameters params = getParametersAt(key);
    		
    		Element pe = params.getDomElement(d);
    		
    		if (pe.getChildNodes().getLength()>0) {
    			
    			element.appendChild(pe);
    			
    			ArrayList<LayoutElement> elems = getElementsAt(key);
    			Element eelems = d.createElement("elements");
    			if (elems!=null && elems.size()>0) {
    				if (key.length()>0 && !key.equals(DEFAULT_POSITION_NAME)) {
    					eelems.setAttribute("position", key);
    				}
    				for (LayoutElement le: elems) {
    					eelems.appendChild(le.getDomElement(d));
    				}
    			}
    			element.appendChild(eelems);
    		}
    		
    		
    	}		
    	
    	return element;
    }
    
    public Element getDomElement(Document d) {
    	return getDomElement(d, "layout-element");
    }

    
    
    public Object clone() {
        Object clone = null;
        try {
          clone = super.clone();
        } catch(CloneNotSupportedException e) {
          // should never happen
        }
        
        ((LayoutElement)clone).setPositionBundle(new HashMap<String, HashMap>());
		Iterator it = positionBundle.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			
			((LayoutElement)clone).setPosition(key);

			Parameters params = getParametersAt(key);
	        ((LayoutElement)clone).setParameters((Parameters)params.clone());
	        
	        ArrayList<LayoutElement> elems = getElementsAt(key);
			
	        for ( LayoutElement e: elems ) {
	            ((LayoutElement)clone).addElement((LayoutElement)e.clone());
	        }
	        try {
				((LayoutElement)clone).addPositionBundle();
			} catch (CitationStyleManagerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

        return clone;
    }


    public String toString() {
		Iterator it = positionBundle.keySet().iterator();
		String s = "";
		while (it.hasNext()) {
			String key = (String)it.next();
			Parameters params = getParametersAt(key);
			ArrayList<LayoutElement> elems = getElementsAt(key);
			s +="\n[position:" + key + 
				",\n parameters:" + params +  
				",\n elements:" + elems + "]";
		}
		
        return "LayoutElement[name:"+ name +
            ", id:" + id +
            ", ref:" + ref +
            ", func:" + func +
            ", repeatable:" + repeatable +
//            ", id:" + id + 
            ", positionBundles:" + s +
            "]\n";
    }


    public static void main(String[] args) throws IOException, CitationStyleManagerException {

//        Parameters p = new Parameters();
//        System.out.println("Default LayoutElements:" + p);

        LayoutElement le = new LayoutElement();
        le.setName("MAIN_LE");
        
      
        LayoutElement le2 = new LayoutElement();
        le2.setName("LE_2");
        le2.setFunc("get_initials");
//
        LayoutElement le3 = new LayoutElement();
        le3.setName("LE_3");
//
        LayoutElement le4 = new LayoutElement();
        le4.setName("LE_4");
    
        
        le.addElement(le2);
        le.addElement(le3);
        le.addElement(le4);
        
        le.setPosition("1");
        le.parameters.setStartsWith("11111");
        le.parameters.setPosition("1");
        try {
			le.addPositionBundle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
        
        LayoutElement le5 = new LayoutElement();
        le5.setName("LE_5");
//
        LayoutElement le6 = new LayoutElement();
        le6.setName("LE_6");
//
        LayoutElement le7 = new LayoutElement();
        le7.setName("LE_7");
        System.out.println("size = " + le.elements.size() + "elems" + (Object)le.getElements().hashCode() );
        
        le.addElement(le5);
        le.addElement(le6);
        le.addElement(le7);
        
        System.out.println("size = " + le.elements.size());
        
        le.setPosition("last");      
        le.parameters.setStartsWith("22222");
        le.parameters.setPosition("last");
        try {
			le.addPositionBundle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
    	Document d = XmlHelper.createDocument();
        
        d.appendChild(le.getDomElement(d));        
        
        System.out.println("XML:" + XmlHelper.outputString(d));
		

//        String n = "TESTNAME";
//        System.out.println ( "" +le2);
//        System.out.println ( " name:" + n + ": " +le.getElementByName(n));

//        le.fillEmptyNames("Prefix");
        System.out.println ( "Instant:" +le);

        System.out.println ( "Clone:" + (LayoutElement)le.clone() );

    }

}
