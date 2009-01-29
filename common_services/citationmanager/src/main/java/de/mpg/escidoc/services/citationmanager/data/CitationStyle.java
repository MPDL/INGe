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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mpg.escidoc.services.citationmanager.utils.Utils;


/**
 * An instance of this class represents 
 * a CitationStyle 
 * 
 * @author vmakarenko (initial creation)  
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CitationStyle implements Cloneable {
	
    private static final Logger logger = Logger.getLogger(CitationStyle.class);	

    private String name;					// Name of CS
    private String mdXPath;					// Medatata root xpath
	private boolean elementSpecific; 		// String value of @ref
    private boolean readOnly; 				// CS can be overridden
    private boolean hasName; 				// triger is set to true if document has defined name 
    
    // Ordered list of LayoutElements which definred Citation Styles Layout Defifintions  
    private List<LayoutElement> csLayoutDefinitions;
    
    // special variables to be used in CitationStyle
    private HashMap<String, String[]> variables;
    
    // globally defined layoutElements on the citation style level 
    private LayoutElementsCollection layoutElements;



	/**
     * Constructor 
     */
    public CitationStyle() {
        setDefault();
    }

    /**
     * Default values definition 
     */
    public void setDefault(){
    	setName(null);
    	setMdXPath(null);
        setElementSpecific(false); 	
        setReadOnly(false);
        hasName = true;
        setCsLayoutDefinitions(new ArrayList<LayoutElement>());
        setVariables(new HashMap<String, String[]>());
        setLayoutElements(new LayoutElementsCollection());
        
    }
    
   
    public void setName ( String newName ) {
        name = newName != null ? newName : name;
    }
    public String getName() {
        return name;
    }
    
	public void setMdXPath(String mdXPath) {
		this.mdXPath = mdXPath !=null ? mdXPath : this.mdXPath;
	}
	
	public String getMdXPath() {
		return mdXPath;
	}

    public void setElementSpecific ( boolean newElementSpecific ) {
        elementSpecific = newElementSpecific;
    }
    public boolean getElementSpecific() {
        return elementSpecific;
    }

    public void setReadOnly ( boolean newReadOnly ) {
        readOnly = newReadOnly;
    }
    public boolean getReadOnly() {
        return readOnly;
    }

    public void setCsLayoutDefinitions ( List<LayoutElement> newCsLayoutDefinitions ) {
        csLayoutDefinitions = newCsLayoutDefinitions;
    }
    
    public  List<LayoutElement> getCsLayoutDefinitions() {
        return csLayoutDefinitions;
    }

    public HashMap<String, String[]> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, String[]> variables) {
		this.variables = variables;
	}

    public void addVariable( String name, String type, String expression ) {
        if ( name !=null && !name.trim().equals("") )
            variables.put(name, new String[] { 
            		type.equals("xpath") ? expression : null,
            		type.equals("expression") ? expression : null 
            });
    }
	
	public LayoutElementsCollection getLayoutElements() {
		return layoutElements;
	}

	public void setLayoutElements(LayoutElementsCollection layoutElements) {
		this.layoutElements = layoutElements;    
	}
	
	// add layout element to the special collection 
	// of predefined layout elements  
    public void addLayoutElement( LayoutElement le ) {
        if ( le !=null && le.getName() != null && !le.getName().trim().equals("") )
        {
        	layoutElements.addLayoutElement(le);
        }
    }
	
	
	
    /**
     * Adds Citation Style Layout Definition (csld) to the list <code>csLayoutDefinitions</code>
     * in case if there is no csld with the same name 
     * @param csld is a {@link LayoutElement}
     */
    public void addCsLayoutDefinition( LayoutElement csld ) {
        if (csld!=null && getElementByName(csld.getName())==null)
            csLayoutDefinitions.add(csld);
    }


    /**
     * Searches {@link LayoutElement} by name in complete <code>csLayoutDefinitions</code> list.
     * @param name is a LayoutElement name
     * @return {@link LayoutElement}
     */
    public LayoutElement getElementByName( String name ) {
        if ( name==null || name.trim().equals("") )
            return null;
        
        //check first predefined layout elements
        LayoutElement pes = layoutElements.getElementByName(name);
        if ( pes != null )
        	return pes;
        
        //check csLayoutDefinitions
        for ( LayoutElement csld: csLayoutDefinitions ) {
            if ( csld.getElementByName(name)!=null)
                return csld;
        }
        return null;
    }

    /**
     * Searches {@link LayoutElement} by <code>id</code> in complete <code>csLayoutDefinitions</code> list.
     * @param id is an id of LayoutElement which should be found   
     * @return {@link LayoutElement}
     */
    public LayoutElement getElementById( String id ) {
        if (id==null || id.trim().equals(""))
            return null;
        
        //check first predefined layout elements
        LayoutElement pes = layoutElements.getElementById(id);
        if ( pes != null )
        	return pes;
        
        for ( LayoutElement csld: csLayoutDefinitions ) {
            if ( csld.getElementById(id) != null )
                return csld;
        }
        return null;
    }

    /**
     * Replaces the element in <code>csLayoutDefinitions</code> list with the specified element <code>le</code>.
     * @param name is a name of <code>csLayoutDefinitions</code> to be replaced
     * @param le is replacement element
     * @return the replaced element or <code>null</code> if no element has been found 
     */
    public LayoutElement replaceCsldByNameWith(String name, LayoutElement le){
        int index = -1;
        LayoutElement tmp = null;
        if (name==null || (name!=null && name.length()==0) || le==null) {
            return null;
        }
        tmp = getElementByName(name);
        if (tmp==null)
            return null;
        else {
            index = csLayoutDefinitions.indexOf(tmp);
            if (index==-1)
                return null;
            tmp = csLayoutDefinitions.set(index, le);
        }
        return tmp;
    }

    
    /**
     * Creates Element for CitationStyle class
     * @return Element
     */
    public Element getDomElement(Document d) {
    	
    	Element cs = d.createElement("citation-style");
    	
    	if (hasName && !(name==null || "".equals(name.trim())))
    		cs.setAttribute("name", name);
    	
    	cs.setAttribute("element-specific", getElementSpecific()? "yes" : "no");
    	cs.setAttribute("read-only", getReadOnly() ? "yes" : "no");
    	cs.setAttribute("md-xpath", mdXPath);

    	// variables
    	Iterator<String> iter = variables.keySet().iterator();
    	if ( iter.hasNext() )
    	{
        	Element vs = d.createElement("variables");
        	while(iter.hasNext()) 
        	{	
        		String name = iter.next();
        		Element variable = d.createElement("variable");
        		variable.setAttribute("name", name);
        		String exp1 = variables.get(name)[0];
        		String exp2 = variables.get(name)[1];
        		variable.setAttribute("type",
        				Utils.checkVal(exp1) ? "xpath" :
        				Utils.checkVal(exp2) ? "expression" :
        				null
        		);
        		variable.appendChild(d.createCDATASection(Utils.coalesce(new String[]{ exp1, exp2 })));
        		vs.appendChild(variable);
        	}
        	cs.appendChild(vs);
    	}
    	
    	// predefined layout-elements
    	if ( layoutElements.getLayoutElements().size()>0 )
    	{
    		Element ls = d.createElement("layout-elements");
    		cs.appendChild(layoutElements.getDomElement(d, ls));
    	}
    	
    	
    	//layout-definitions
    	for ( LayoutElement csld: csLayoutDefinitions ) {
    		cs.appendChild(csld.getDomElement(d, "cs-layout-definition") );
    	}
    	return cs;
    }


    /**
     * Fills empty names of <code>csLayoutDefinitions</code> with uniq name
     * @param prefix a prefix for csld with empty name
     */
    public void fillEmptyNames( String prefix ) {
        if (name==null || (name!=null && name.equals(""))) {
        	hasName = false; 
        	setName( prefix );
        } else {
           prefix += "_" + getName();
        }
        int count = 0;
        for ( LayoutElement csld: csLayoutDefinitions ) {
            csld.fillEmptyNames( prefix + "_CSLD_" + count++);
        }
    }

    /**
     * Generates internal Ids for all <code>cs-layout-definition</code>s in the Citation Style
     * @param prefix is prefix for csld
     */
    public void generateIDs (String prefix) {
    	int count = 1;
    	for ( LayoutElement ple: layoutElements.getLayoutElements() ) {
    		ple.generateIDs(prefix + "_PLE_" + count++ );
    	}
        count = 1;
        for ( LayoutElement csld: getCsLayoutDefinitions() ) {
            csld.generateIDs(prefix + "_CSLD_" + count++ );
        }
        
    }
    
    public Object clone() {
        Object clone = null;
        try {
          clone = super.clone();
        } catch(CloneNotSupportedException e) {
          // should never happen
        }

        
        //variables
        ((CitationStyle)clone).setVariables(new HashMap<String, String[]>());
        Iterator<String> iter = variables.keySet().iterator();
    	while(iter.hasNext()) 
    	{	
    		String name = iter.next();
    		((CitationStyle)clone).addVariable(name, variables.get(name)[0], variables.get(name)[1]);
    	}
    	
        //layout elements
        ((CitationStyle)clone).setLayoutElements(new LayoutElementsCollection());    	
    	for ( LayoutElement le: layoutElements.getLayoutElements() ) {
        	((CitationStyle)clone).addLayoutElement((LayoutElement)le.clone());
        }

    	// layout definitions
        ((CitationStyle)clone).setCsLayoutDefinitions(new ArrayList<LayoutElement>());
        for ( LayoutElement csld: csLayoutDefinitions ) {
            ((CitationStyle)clone).addCsLayoutDefinition((LayoutElement)csld.clone());
        }
        
        return clone;
    }

    public String toString() {
        return "CitationStyle" +
        	"[" +
        		"name:" + name + "," + 
        		"md-xpath:" + mdXPath + "," + 
        		"element-specific:" + elementSpecific + "," + 
        		"readOnly:" + readOnly + "," +
        		variables + ", " + 
        		layoutElements + ", " + 
        		csLayoutDefinitions + 
        	"]";
    }


    public static void main(String[] args) {

        CitationStyle cs = new CitationStyle();
        cs.setName("APA");
        cs.setMdXPath("//items/item/md-records/md-record/publication");
        cs.addVariable("hasPublication", "not(publication)=false()", null);
        cs.addVariable("hasPublication2", null, "${hasPublication}");
        
        LayoutElement le = new LayoutElement();
        le.setName("test_layout_element");
        cs.addLayoutElement(le);
        

        LayoutElement csld1 = new LayoutElement();

        csld1.setName("Kukushka");

        cs.addCsLayoutDefinition ( csld1 );

        LayoutElement csld2 = new LayoutElement();

        csld2.setName("Mamushka");
        csld2.setRepeatable("yes");

        cs.replaceCsldByNameWith("Kukushka", csld2);

        cs.fillEmptyNames ("Prefix");

        logger.info("Default CitationStyle:" + cs);

        logger.info("Clone" + (CitationStyle)cs.clone() );


    }

}
