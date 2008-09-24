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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map; 
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyle;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.CitationStylesCollection;
import de.mpg.escidoc.services.citationmanager.FontStyle;
import de.mpg.escidoc.services.citationmanager.FontStylesCollection;
import de.mpg.escidoc.services.citationmanager.LayoutElement;
import de.mpg.escidoc.services.citationmanager.LayoutElementsCollection;
import de.mpg.escidoc.services.citationmanager.Parameters;
import de.mpg.escidoc.services.citationmanager.ProcessScriptlet;
import de.mpg.escidoc.services.citationmanager.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.XmlHelper;

/**
*
* Citation Style Processing Engine   
*
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

/**
 * @author vlad
 *
 */
public class ProcessCitationStyles implements CitationStyleHandler{

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(ProcessCitationStyles.class);

    // Result of citation is used in jasper report 
    public String RESULT_CITATION_VARIABLE;
    
    // Default style name for, used as a starter for new style creation  
    public String DEFAULT_STYLENAME;
    
    // Default name for serialised output of the component: jrprint file
    public String DEFAULT_REPORTNAME;
    
    // Commonly used file name for citation style definition xml
    public String CITATION_XML_FILENAME;
    
    // if true the old scriptlets will not be deleted  
    // before the compilations of the new scriptlets, if false - otherwise 
    public boolean KEEP_OLD_SCRIPTLETS;
    
    // if true the jasper report compiler will not delete the 
    // old java execution plan file of the report, if false - otherwise  
    public boolean KEEP_COMPILER_KEEP_JAVA_FILE;
    
    // if true the jrxml file of the report will be created,
    // if false - otherwise
    public boolean CREATE_JRXML;
    
    
    // component properties
    public Properties props;

    
    // tasks for test
    private static final String TASK_FILL = "fill";
    private static final String TASK_FILL_SORT_TITLE = "sort-title";
    private static final String TASK_COMPILE = "compile";
    
    private static final String TASK_PDF = "pdf";
    private static final String TASK_RTF = "rtf";
    private static final String TASK_VIEW = "view";
    private static final String TASK_PRINT = "print";
    private static final String TASK_HTML = "html";
    private static final String TASK_XML = "xml";
    private static final String TASK_ODT = "odt";    
    private static final String TASK_VALIDATE_DS = "validate-ds";
    private static final String TASK_VALIDATE_CS = "validate-cs";
    
    // Output Formats enum
    public static enum OutFormats { rtf, pdf, html, odt, snippet }; 
    


    // FontStylesCollection
    private static FontStylesCollection fsc = null;

    // LayoutElementCollection
    private LayoutElementsCollection lec = null;

    // CitationStyleDefinition
    private CitationStylesCollection csc = null;


    // default JasperDesign which will be updated with new this procedure.
    private JasperDesign jasperDesign = null;

    // Dataset in JasperDesign which will be updated
    private JRDesignDataset dataSet = null;
    
    // ProcessScriptlet class instance
    private ProcessScriptlet ps = null;

    
	
	public ProcessCitationStyles() {

			// get main component properties
			try {
				props = ResourceUtil.getProperties("citman.properties");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	     	// populate constatns 
	        RESULT_CITATION_VARIABLE = props.getProperty("result.citation.variable");
	        DEFAULT_STYLENAME = props.getProperty("default.stylename");
	        DEFAULT_REPORTNAME = props.getProperty("default.reportname");
	        CITATION_XML_FILENAME = props.getProperty("citation.xml.filename");
	        KEEP_OLD_SCRIPTLETS = Boolean.parseBoolean(props.getProperty("keep.old.scriptlets").trim()); 
	        KEEP_COMPILER_KEEP_JAVA_FILE = Boolean.parseBoolean(props.getProperty("keep.compiler.keep.java.file").trim());
	        CREATE_JRXML = Boolean.parseBoolean(props.getProperty("create.jrxml").trim());

	}
	/**
     * Loads FontStyleCollection from XML
     * @param path A path to the directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public void loadFontStylesFromXml(File path) throws CitationStyleManagerException{
        try {
        	
            fsc = FontStylesCollection.loadFromXml( 
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ DEFAULT_STYLENAME 
            		+ "/" + ResourceUtil.FONTSTYLES_FILENAME + ".xml");
        } catch (Exception e) {
            throw new CitationStyleManagerException(
            		"Error by FontStylesCollection loading: " + e);
        }
    }
    /**
     * Loads LayoutElementsCollection from XML
     * @param name is name of CiatationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public void loadLayoutElementsFromXml(File path, String name) throws CitationStyleManagerException{
        try {
            lec = LayoutElementsCollection.loadFromXml(
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/LayoutElements.xml"
            );
        } catch (Exception e) {
        	throw new CitationStyleManagerException("Error by LayoutElementsCollection loading: " + e);
        }
    }
    
    public void loadLayoutElementsFromXml() throws CitationStyleManagerException{
    	
    	loadLayoutElementsFromXml(
    			new File(ResourceUtil.CITATIONSTYLES_DIRECTORY),
    			DEFAULT_STYLENAME
    	);

    }

    /**
     * Loads CitationStylesCollection from XML
     * @param name is name of CiatationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     */
    public void loadCitationStyleFromXml(String name) throws CitationStyleManagerException{
        try {
            csc = CitationStylesCollection.loadFromXml(
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ name 
            		+ "/"
            		+ CITATION_XML_FILENAME
            );
        } catch (Exception e) {
        	throw new CitationStyleManagerException("Error by loading CitationStylesCollection: " + e);
        }
    }


	/**
     * Writes LayoutElementsCollection to XML file
     * @param path A path to the CitationStyles directory
     * @param name A name of the CitationStyle
     * @throws IOException
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */
    private void writeLayoutElementsToXml(File path, String name) throws IOException, SAXException, CitationStyleManagerException {
        try {
            lec.writeToXml(path + "/" + name + "/LayoutElements.xml");
        } catch (IOException e) {
            throw new IOException("Cannot write to XML:" + e);
        } catch (SAXException e) {
            throw new SAXException("SAX error:" + e);
        }
    }

    /**
     * Writes LayoutElementsCollection to XML file
     * @param path A path to the CitationStyles directory
     * @param name A name of the CitationStyle
     * @throws IOException
     * @throws SAXException
     * @throws CitationStyleManagerException 
     */
    private void writeCitationStyleToXml(File path, String name) throws IOException, SAXException, CitationStyleManagerException {
        try {
            csc.writeToXml(path + "/" + name + "/" + CITATION_XML_FILENAME);
        } catch (IOException e) {
            throw new IOException("Cannot write to XML:" + e);
        } catch (SAXException e) {
            throw new SAXException("SAX error:" + e);
        }
    }

   /**
    * Find field by name in fieldsMap
    *
    * @param name is name of field
    * @return true if found otherwise
    */
    private boolean findInFieldsMap(String name) {
        Map fieldsMap = dataSet.getFieldsMap();
        return fieldsMap.containsKey(name);
    }


   /**
    * Find variable by name in variablesMap
    *
    * @param name is name of variable
    * @return true if found otherwise
    */
    private boolean findInVariablesMap(String name) {
        Map variablesMap = dataSet.getVariablesMap();
        return variablesMap.containsKey(name);
    }

    /**
     * Add JasperReport field
     * @param name is name of field
     * @param expr is XPath to the field in DataSource 
     * @throws JRException
     */
    private void addJRField(String name, String expr) throws JRException{
        if (findInFieldsMap(name))
        {
        	logger.info("Field " + name + " is already in the hash of fields");
            return;
        }
        //logger.info(name + ", " + expr);
        JRDesignField field = new JRDesignField();
        field.setName(name);
        String jre = translateToJRExpression(expr);
        field.setDescription(jre);
        //logger.info("field: " + name + ", expr: " + jre);
        field.setValueClass(String.class);
        dataSet.addField(field);
    }
    
    /**
     * Add JasperReport field where XPath equals the name
     * @param name is name of field
     * @throws JRException
     */
    private void addJRField(String name) throws JRException
    {
    	addJRField(name, name);
    }

    /**
     * Add JasperReport variable
     * @param name is name of field
     * @param description is XPath to the variable in DataSource 
     * @throws JRException
     */
    private void addJRVariable(String name, String expr) throws JRException {
        // add Variable to JasperDesign
        if (findInVariablesMap(name)) {
        	logger.info("Variable " + name + " is already in the hash of varaibles");
            return;
        }
        // walk around expression and add
        // ${...} as $F{...}
        // TODO: differentiate it to $F{...} and $V{...} later on!!!!! 
        //addJRFieldsFromJRExpression(expr);
        
        //logger.info(name + ", " + expr );
        JRDesignVariable variable = new JRDesignVariable();
        variable.setName(name);
        variable.setValueClass(String.class);
        variable.setSystemDefined(false);
        JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(String.class);
        String jre = translateToJRExpression(expr);
        //logger.info("variable: " + name + ", expr: " + jre);
        expression.setText(translateToJRExpression(jre));
        variable.setExpression(expression);

        dataSet.addVariable(variable);
    }
     /**
      * Fills JRFields[] of jasper report with
      * elements are taken according to CitationElements.xsd
     * @throws IOException 
     * @throws FileNotFoundException 
      */
     private void addDefaultJRFields() throws JRException, FileNotFoundException, IOException {
    	 
    	Properties props = ResourceUtil.getProperties(ResourceUtil.CITATIONSTYLES_DIRECTORY, "fields.properties");
    	Iterator iter = props.keySet().iterator(); 
    	while (iter.hasNext())
    	{
    		String str = (String)iter.next(); 
    		addJRField(str, props.getProperty(str));
    	}
     }

     
     /**
      * Add default variables of report  
     * @throws JRException
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private void addDefaultJRVariables() throws JRException, FileNotFoundException, IOException {
    	
    	Properties props = ResourceUtil.getProperties(ResourceUtil.CITATIONSTYLES_DIRECTORY, "variables.properties");
    	Iterator iter = props.keySet().iterator(); 
    	while (iter.hasNext())
    	{
    		String str = (String)iter.next(); 
    		addJRVariable(str, props.getProperty(str));
    	}
      }

    /**
     * Find ref location: whether in FieldsMap or in VariablesMap
     * @param ref
     * @return char 'F' - for FieldsMap and 'V' for VariablesMap
     * @throws Exception if there is no ref in the Maps
     */
    private char findRefLocation(String ref) throws CitationStyleManagerException {
    	char where = 
    		findInFieldsMap(ref) ? 'F' : 
    			findInVariablesMap(ref) ? 'V' : 'E' ;
    	if (where == 'E')
    		throw new CitationStyleManagerException("reference:<" + ref + "> is neither in Fields nor in Variables.");
    	return where;
    }

     

    /**
     * Creates expression string with all subelements of le.
     * Inserts delimiter between subelements
     *
     * @param le is input LayoutElement
     * @param delimiter is String to be inserted between elements
     *
     * @return extended expression String
     * @throws JRException 
     */
    public String applyElements(LayoutElement le, String delimiter) throws CitationStyleManagerException, JRException {
        String expr = "";
        String prevES = null;
        
        ArrayList<LayoutElement> elements = (ArrayList)le.getElementsAtDefault();
        Parameters parameters = (Parameters)le.getParametersAtDefault();
        
        int eSize = elements.size();
        if ( eSize > 0) {
        	int i = 1;
            for (LayoutElement e : elements) {
            	String eS = "$V{" + e.getId() + "}"; 
                addLayoutElementToVariablesMap(e);
                // insert delimiter between elements
            	if (i>1 && i<=eSize) { 
            		if (delimiter != null && delimiter.length()>0) {
                        expr += " + ("+ eS +".length()>0 && " + prevES + ".length()>0 ? \"" + delimiter + "\" : \"\") + ";
            		} else {
            			expr += " + ";
            		}
            	}    
        		expr += eS;
        		prevES = eS;
                i++;
            }
//            expr = expr.substring(0, expr.length() -
//                    (delimiter == null ? 3 : delimiter.length() + 8 ) );
        } else if (le.getRef().length() > 0) {
        	String ref = le.getRef();
//          In the case we can define complex MD handling, which is not supported with XPAth
            expr = "$" + findRefLocation(ref) +"{" + ref + "}";
        } else {
//          Static element, takes values from startsWith and endsWith,
//          will be applied further in applyParameters()
            String str = "";
            Parameters p = parameters;
            String sw = p.getStartsWith();
            String ew = p.getEndsWith();
            if ( sw!=null && sw.length()>0 )
                str = sw;
            if ( ew!=null && ew.length()>0 )
                str += ew;
            if ( str.length()==0 )
                throw new CitationStyleManagerException("Layout Element has no reference, no elements and it is not static element:<" + le.getName() + ">, id:<" + le.getId() + ">");
            else
                expr = "\"\"";
        }

        return expr;
    }

    /**
     * Paramaters' overwriting
     *
     * @param p1 is high parameter
     * @param p2 is low parameter
     * @return An overwritten parameter
     */
    public static String overParams(String p1, String p2){
        return p1 != null && p1.length() > 0 ? p1 : p2 != null && p2.length() > 0 ? p2 : null;
    }

    /**
     * Paramaters' overwriting
     *
     * @param p1 is high parameter
     * @param p2 is low parameter
     * @return An overwritten parameter
     */
    public static int overParams(int p1, int p2){
        return p1 != 0 ? p1 : p2 != 0 ? p2 : 0;
    }


    
     /**
      * Create expression string on hand of Parameters:
      * <li>delimiter</li> - only for repeatable elements
      * <li>startsWith</li>
      * <li>endsWith</li>
      * <li>maxLength</li>
      * <li>maxLengthEndsWith</li>
      * <li>validIf</li>
      * <li>fontStyle</li>
      *
      *
      * @param p is Parameters to be applied
      * @param expr is JRDesignExpression expression to be extended with p
      * @param isRepeatable is tag for repeatable elements processing
      *
      * @return extended expression String
     * @throws JRException 
      */
    public String applyParameters(LayoutElement le, String expr, boolean isRepeatable) throws CitationStyleManagerException, JRException {

        String ref = le.getRef();
        LayoutElement refLE = null;
        Parameters pLE = le.getParametersAtDefault();
        Parameters pREF = null;
        String p;
        String checkLen = "(!(%s).trim().equals(\"\") ? %s : \"\")";
        String oldExpr = expr;


        if (lec != null && ref != null && ref.length() > 0) {
            refLE = lec.getElementByName(ref);
            if (refLE != null) {
                pREF = refLE.getParametersAtDefault();
            }
        }


        
        String chunk = expr;


        // apply function  	
        String func = overParams(le.getFunc(), refLE != null ? refLE.getFunc() : null);
        if ( ProcessScriptlet.isInScriptletFunctionsTable( func ) ) 
        	chunk = "($P{REPORT_SCRIPTLET}." + func + "(" + chunk + "))";
        
        // startsWith
        p = overParams(pLE.getStartsWith(), pREF != null ? pREF.getStartsWith() : null);
        p = xmlEncode(p);
        if (p != null && p.length() > 0) {
        	chunk = "\"" + p + "\" + " + chunk;
        }

        // endsWith
        p = overParams(pLE.getEndsWith(), pREF != null ? pREF.getEndsWith() : null);
        p = xmlEncode(p);
        if (p != null && p.length() > 0) {
        	chunk = chunk + " + \"" + p + "\"";
        }
        
        // apply zero length checking
        expr = !isRepeatable ? String.format(checkLen, expr, chunk) : chunk;	
//        expr = chunk;	
        
        // maxLength
        int maxLength = overParams(pLE.getMaxLength(), pREF != null ? pREF.getMaxLength() : 0);
        if (maxLength > 0) {
            expr = "(" + expr + ")";
            chunk = "(" + expr + ".length() > " + maxLength + " ? " +
                expr + ".substring(0, " + (maxLength - 1) + ")";
            // maxLengthEndsWith
            p = overParams(pLE.getMaxLengthEndsWith(), pREF != null ? pREF.getMaxLengthEndsWith() : null);
            p = xmlEncode(p);
            if (p != null && p.length() > 0) {
                chunk += " + \"" + p + '"';
            }
            expr = chunk + ":" + expr + ")";
        }
        

        // validIf
        p = overParams(pLE.getValidIf(), pREF != null ? pREF.getValidIf() : null);
        if ( p != null && !p.trim().equals("") ) 
        {
        	//p = translateToJRExpression(p);
            expr = "((" + p + ") ? (" + expr + ") : \"\")";
        }

        // fontStyle
        p = overParams(pLE.getFontStyleRef(), pREF != null ? pREF.getFontStyleRef() : null);
        if ( p != null && !p.trim().equals("") ) 
        {
            FontStyle fs = fsc.getFontStyleByName(p);
            expr = String.format(fs.toStyle(), expr) ;
        }

//        logger.debug("le:" + le.getRepeatable()==null);
        if (ref != null && ref.length() > 0) 
        {
        	if ( isRepeatable )
        	{
                // avoid empty output for repeatable elements
        		// FIRST SCENARIO
        		// 1) create a special field to check 
        		// whether the repeatable element is not empty
        		String chk_field = "tmpField_" + le.getId();
//        		String XPath = "count(" + MD_XML_ROOT_XPATH + "/" + ref + ")>0";
        		String XPath = "count(" + ref + ")>0";
        		// 2) add field
        		try { 
					addJRField(chk_field, XPath);
				} catch (JRException e) {
					// TODO Auto-generated catch block
					throw new CitationStyleManagerException(e);
				}
				// 3) if the field is empty, do not output anything
				expr = "(($F{" + chk_field + "}).trim().equals(\"true\") ? " + expr + " : \"\")";  
//        		// SECOND SCENARIO
//        		// 1) create a temp variable to check 
//        		// whether the repeatable element is not empty
//        		String chk_variable = "tmpVar_" + le.getId();
//        		// 2) add variable
//        		try {
//					addJRVariable(chk_variable, oldExpr);
//				} catch (JRException e) {
//					// TODO Auto-generated catch block
//					throw new CitationStyleManagerException(e);
//				}
//				// 3) if the variable is empty, do not output anything 
//                expr = String.format(checkLen, "$V{" + chk_variable + "}", expr);	

        		
        	}
        	else
                // avoid "null" parameters for non-repeatable elements
        		expr = "$" + findRefLocation(ref) + "{" + ref + "} != null ? " + expr + " : \"\"";
        }
        
//        expr = String.format(checkLen, oldExpr, chunk);	
      
        return expr;
    }

//    /**
//     * Add new JRFields on hand of validIf expression
//     * 
//     * @param vi - valid-if expression
//     * @throws JRException
//     */
//	private void addJRFieldsFromJRExpression(String vi) throws JRException {
//		Pattern p = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE
//				| Pattern.DOTALL);
//		Matcher m = p.matcher(vi);
//		while (m.find())
//			addJRField(m.group(1));
//	}
//	
    /**
     * Substitute all ${...} of the valid-if elements 
     * with the correct JRExpression notation for JRField 
     * reference: $F{...} or JRVariable reference: $V{...} 
     * 
     * @param vi
     *            - validIf expression
     * @throws JRException 
     * @throws JRException
     */
	private String translateToJRExpression(String expr) throws JRException {
		Pattern p = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = p.matcher(expr);
		 StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			String name = m.group(1);
			if (findInVariablesMap(name))
			{
				m.appendReplacement(sb, "\\$V\\{"+ name +"\\}");
			}
			else 
			{
				m.appendReplacement(sb, "\\$F\\{"+ name +"\\}");
				addJRField(name);
			}
			logger.info("sb: " + sb.toString());
			
		}
		m.appendTail(sb);
		
		return sb.toString();
//		return 
//			Pattern.compile("\\$\\{", Pattern.DOTALL)
//			.matcher(vi)
//			.replaceAll("\\$F{");
	}	
    
	/**
      * Add le to variablesMap
      * is taken from LayoutElements.xml
      *
      * @param le LayoutElement to add to variablesMap
     * @throws JRException 
      *
      */
    public void addLayoutElementToVariablesMap(LayoutElement le) throws CitationStyleManagerException, JRException {

        String ref;
        String expr = "";
        Parameters p = le.getParametersAtDefault();
        String delimiter = p.getDelimiter();
        boolean isRepeatable = le.getRepeatable().equals("yes");

//      goaway if element was already part of position bundle and was already processed
//        if (isRepeatable && lesWithScriptletMethod.contains(le.getName()))
//            return;
//      clean up JasperDesign: wipe already defined variable
        if (findInVariablesMap(le.getId())) {
//          throw new Exception("Bad layout element name (" + le.getName() + "): there is already one with the same name");
            // remove variable, it will be redifined further
            dataSet.removeVariable(le.getId());

            // it has been already processed as position bundle! do nothing
        }
        

        ref = le.getRef();

        //  if le has reference to
        

          if (ref != null && !ref.trim().equals("")) {
            // repeatable!!! - produce Scriptlet
            if (isRepeatable) {
                  ps.createMethodForScriptlet(le, csc, fsc);
                expr = applyParameters(le, "(($P{REPORT_SCRIPTLET}).get" + le.getId() + "())", true);
            }
            // not repeatable: normal
            else {
                // field in fieldsMap
                if (findInFieldsMap(ref)) {
                    // we ignore elements if they are defined
                    expr = applyParameters(le, "($P{REPORT_SCRIPTLET}.xmlEncode($F{" + ref + "}))", false);
                }
                // variable in variablesMap
                else if (findInVariablesMap(ref)) {
                	// if there are elements
                	expr = applyElements(le, delimiter);
                	expr = applyParameters(le, expr, false);
                }
                else {
                	addJRField(ref);
                	expr = applyParameters(le, "($P{REPORT_SCRIPTLET}.xmlEncode($F{" + ref + "}))", false);
                    //throw new CitationStyleManagerException("Bad reference: " + ref);
                }
            }
        }
        // le has no ref
        else {
            expr = applyElements(le, delimiter);
            expr = applyParameters(le, expr, false);
        }
        
    
          
        addJRVariable(le.getId(), expr);
    }

    
    
    public void parseCitationStyle(CitationStyle cs) throws CitationStyleManagerException, JRException, FileNotFoundException, IOException {

        // walkaround through all csLayoutDefinitions
        String expr = "";

        ps = new ProcessScriptlet(cs);
        
        // add special variables defined in cs XML
        HashMap<String, String[]> vars = cs.getVariables();   
        Iterator<String> iter = vars.keySet().iterator();
        while ( iter.hasNext() )
        {
        	String name = iter.next();
        	//xpath is first  
        	String val = vars.get(name)[0]; 
        	if ( checkVal(val)  )
        	{
        		addJRField(name, val );        		
        	}
        	else
            	//expression is second
        	{
            	val = vars.get(name)[1];
            	if ( checkVal(val)  )
            	{
            		addJRVariable(name, val);        		
            	}
        	}
        	
        	
        }
        
        //generate result citation variable
        String V = null;
        for (LayoutElement csld : cs.getCsLayoutDefinitions()) {
            addLayoutElementToVariablesMap(csld);
            V =  "$V{" + csld.getId() + "}";
            expr += "!" + V + ".trim().equals(\"\") ? " + V + ":";
        }
        //expr = expr.substring(0, expr.length() - 1);
        expr += "\"\""; 
        expr = "($P{REPORT_SCRIPTLET}.cleanCit(" + expr + "))";


        // fixing of strange behavior of JR: 
        // result variables (e.g. names of CsLayoutDefinitions) should be 
        // as close as possible to the end of variables list.
        // otherwise some values of fields can be taken from the previous iteration 
        for (LayoutElement csld : cs.getCsLayoutDefinitions()) {
        	 JRVariable v = dataSet.removeVariable(csld.getId());
             addJRVariable(v.getName(), v.getExpression().getText());
        }

        if (findInVariablesMap(RESULT_CITATION_VARIABLE))
            dataSet.removeVariable(RESULT_CITATION_VARIABLE);

        addJRVariable(RESULT_CITATION_VARIABLE, expr);

    }

    /**
     * Returns first not null && not empty String from String[] 
     * @param values 
     * @return first not null && not empty String
     */
    private String coalesce(String[] values)
    {
    	for ( String val : values)
    		if ( checkVal(val) )
    			return val;
    	return null;
    }
    
    /**
     * Returns true if val is not null && not empty String 
     * @param val 
     * @return first not null && not empty String
     */
    private boolean checkVal(String val)
    {
    	return ( val != null && !val.trim().equals("") );
    }
  

     /**
      * Fills  according to LayoutElementsCollection
      * is taken from LayoutElements.xml
     * @throws IOException 
      *
      */
    public void parseAll(File path, String name) throws CitationStyleManagerException, IOException {

    	if (jasperDesign == null) 
    		throw new CitationStyleManagerException("Empty jasperDesign variable");
    	
    	// fill  CitationStyleCollection
        for (CitationStyle cs : csc.getCitationStyles()) {
            try {
                parseCitationStyle(cs);
            }
            catch (Exception e) {
                throw new CitationStyleManagerException("Cannot parse Citation Style(" + cs.getName() + "):" + e);
            }
        }
    	if (ps == null) 
    		throw new CitationStyleManagerException("Empty ProcessScriptlet instance variable");
        ps.writeToScriptlet(path, name);
        
//		set getScriprtletClassName with the package prefix
        String scn = ps.getScriptletClassName();
        jasperDesign.setScriptletClass(ProcessScriptlet.getPackageName() + "." + scn);
        
        String scriptlet_fn_java = 
        	ProcessScriptlet.getPathToScriptletJava() + scn + ".java";

        //compile scriptlet java and put it in  
        com.sun.tools.javac.Main.compile(new String[] {
                "-cp",
                JRProperties.getProperty(JRProperties.COMPILER_CLASSPATH),
                "-d", ResourceUtil.getPathToClasses(),
                scriptlet_fn_java
                
        });
        
        
      }



    /**
     * Loads JasperDesign XML file
     * @param name is name of CitationStyle
     * @param path is path to directory of CitationStyle
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public void loadCitationStyleJRXml(File path, String name) throws JRException, CitationStyleManagerException, IOException{
        try {
            jasperDesign = JRXmlLoader.load( 
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/CitationStyle.jrxml"
            );
            
//			init dataSet global variable              
            dataSet = jasperDesign.getMainDesignDataset();
            addDefaultJRFields();
            addDefaultJRVariables();

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
    public void loadCitationStyleTestJRXml() throws JRException, CitationStyleManagerException, FileNotFoundException, IOException{
        try {
            jasperDesign = JRXmlLoader.load(
            		ResourceUtil.CITATIONSTYLES_DIRECTORY 
            		+ "/" + DEFAULT_STYLENAME 
            		+ "/citation-style-test.jrxml"
            				
            );
            dataSet = jasperDesign.getMainDesignDataset();
            addDefaultJRFields();
            addDefaultJRVariables();

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
    public void loadCitationStyleJRXml(File path) throws JRException, CitationStyleManagerException, IOException{
        loadCitationStyleJRXml(path, DEFAULT_STYLENAME);
    }


    /**
     *  Validates
     * @throws JRException
     * @throws CitationStyleManagerException 
     */
    public void verifyCitationStyle() throws JRException, CitationStyleManagerException {
    	
    	if (jasperDesign == null) 
    		throw new CitationStyleManagerException("Empty jasperDesign variable");
        Collection brokenRules = JasperCompileManager.verifyDesign(jasperDesign);
        if (brokenRules != null && brokenRules.size() > 0) {
            StringBuffer sbuffer = new StringBuffer();
            sbuffer.append("Citation Style design is not valid :");
            int i = 1;
            for(Iterator it = brokenRules.iterator(); it.hasNext(); i++)
            {
                sbuffer.append("\n\t " + i + ". " + (String)it.next());
            }
            throw new JRException(sbuffer.toString());
//            logger.debug(sbuffer.toString());
        }

    }


    /**
     * Compiles JasperDesign file and saves it into .jasper file
     * @param path A path to the CitationStyles directory
     * @param name A name of CItation Style
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public void compileCitationStyleJRToFile(File path, String name) throws JRException, CitationStyleManagerException, IOException {
    	if (jasperDesign == null) 
    		throw new CitationStyleManagerException("Empty jasperDesign variable");
        try {
            JasperCompileManager.compileReportToFile(jasperDesign , 
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/CitationStyle.jasper");
        }
        catch (JRException e) {
            throw new JRException("FAILED .jasper creation: " + e);
        }
    }

    /**
     * Compiles JasperDesign file and saves it into .jrxml file
     * @param path A path to the CitationStyles directory
     * @param name A name of CItation Style
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public void compileCitationStyleJRToXml(File path, String name) throws JRException, CitationStyleManagerException, IOException 
    {
    	if (!CREATE_JRXML)
    		return;
    	
    	if (jasperDesign == null) 
    		throw new CitationStyleManagerException("Empty jasperDesign variable");
    	
        try {
            JasperCompileManager.writeReportToXmlFile(jasperDesign, 
            		// TODO: Ignore Path             		
//            		path +
            		ResourceUtil.getPathToCitationStyles()
            		+ name + "/CitationStyle.jrxml");
        } catch (JRException e) {
              throw new JRException("FAILED .jrxml creation: " + e);
        }
    }


    
    /**
     * Creates new CitationStyle directory on hand of existende CitationStyle
     * @param path A path to the CitationStyles directory
     * @param newName A name of CitationStyle to be created
     * @param templName A name of template CitationStyle
     * @throws Exception
     * @throws JRException
     * @throws IOException  
     * @throws CitationStyleManagerException 
     * @throws SAXException 
     * @throws CitationStyleManagerException 
     */
    public void createNewCitationStyle(File path, String templName, String newName) throws JRException, IOException, CitationStyleManagerException, SAXException, CitationStyleManagerException {
        // create new CitationStyle directory
        File newPath = new File(path, newName);
        File templPath = new File(path, templName);
        if (newPath.mkdir()) {
            // take only files!!!
            String[] files = templPath.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name){
                                return !(new File(dir, name)).isDirectory() && name.endsWith(".xml");
                            }
                        }
                    );
            for (String fn : files) 
                ResourceUtil.copyFileToFile(new File(templPath, fn), new File(newPath, fn));
            
        } else {
            throw new IOException("Cannot create new directory for CitationStyle:" + newPath );
        }
        //replace old name with new one
        loadCitationStyleFromXml(newName);
        csc.getCitationStyleByName(templName).setName(newName);
        writeCitationStyleToXml(path, newName);
    }

    /**
     * Creates new CitationStyle directory on hand of DefaultCitationStyle XMLs (located in path)
     * @param path A path to the CitationStyles directory
     * @param newName A name of CitationStyle to be created
     * @param templName A name of template CitationStyle
     * @throws Exception
     * @throws JRException
     * @throws SAXException 
     * @throws IOException 
     * @throws CitationStyleManagerException 
     */
    public void createNewCitationStyle(File path, String newName) throws JRException, CitationStyleManagerException, IOException, SAXException, CitationStyleManagerException {
        createNewCitationStyle(path, DEFAULT_STYLENAME, newName);
    }

    /**
     * Loads Citation Style Definition XMLs:
     * FontStyles.xml, LayoutElements.xml and CitationStyle.xml
     * @param path path to ReportingTool
     * @param name is a name of a Citation Style
     * @throws CitationStyleManagerException 
     */
    public void loadCitationStyleDefinitionXmls(File path, String name) throws CitationStyleManagerException{
        loadFontStylesFromXml(path);
        loadLayoutElementsFromXml(path, name);
        loadCitationStyleFromXml(name);
    }

    
    /**
     * Populates following default properties of jasperDesign: 
     * <code>name, scriptletClass, query root, report header</code>,    
     * @param name
     * @throws CitationStyleManagerException if jasperDesign is null
     */
    public void setJasperDesignDefaultProperties(String name) throws CitationStyleManagerException {
    	
    	if (jasperDesign == null) 
    	{
    		throw new CitationStyleManagerException("Empty jasperDesign variable");
    	}
    	if (csc == null) 
    	{
    		throw new CitationStyleManagerException("Ciataion Styles are not loaded");
    	}
    	
    	
//		set report name
        jasperDesign.setName(name);
        
//		assign correct scriptlet name               
        String sc = jasperDesign.getScriptletClass();
        String pn = ProcessScriptlet.getPackageName();
        if ( sc != null && !sc.startsWith(pn))
        	jasperDesign.setScriptletClass( pn + "." + sc );
        
//		set root path for XML DataSource  
        JRDesignQuery q = new JRDesignQuery();
        q.setText(csc.getCitationStyleByName(name).getMdXPath());
        jasperDesign.setQuery(q);

//		write the name of Citation Style in the header              
        JRDesignStaticText st = (JRDesignStaticText)jasperDesign.getTitle().getElementByKey("staticText");
        if ( st != null )
        	st.setText("Citation Style: " + name);
        
    }
    
    /**
     * Loads <code>jasperDesign</code> from DEFAULT_STYLENAME
     * and sets <code>name</code> to the name of new CitationStyle
     * @param path is a path to Ciation Styles Directory
     * @param name is a neame of new Citation Style
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */
    public void loadDefaultCitationStyleJRXml(File path, String name) throws JRException, CitationStyleManagerException, IOException {
        try {
            loadCitationStyleJRXml(path, DEFAULT_STYLENAME);
            setJasperDesignDefaultProperties(name);
        } catch (JRException e) {
            throw new JRException("FAILED loading from .jrxml: " + e);
        }
    }

    /**
     * @param path
     * @throws CitationStyleManagerException 
     * @throws JRException 
     * @throws IOException 
     */
    public void loadDefaultCitationStyleJRXml(File path) throws JRException, CitationStyleManagerException, IOException {
        loadDefaultCitationStyleJRXml(path, DEFAULT_STYLENAME);
    }

    /**
     * Loads complete Citation Style XMLs bundle  + jrXML
     * @param path
     * @param name
     * @throws JRException
     * @throws IOException
     * @throws CitationStyleManagerException 
     */
    public void loadCitationStyleBundle(File path, String name) throws JRException, IOException, CitationStyleManagerException {
    	
    	loadCitationStyleDefinitionXmls(path, name); 
    	loadCitationStyleJRXml(path);
    	setJasperDesignDefaultProperties(name);

    }

    public void loadCitationStyleBundle(File path) throws JRException, IOException, CitationStyleManagerException{
    	
        loadCitationStyleBundle(path, DEFAULT_STYLENAME);

    }


    public void saveCitationStyleBundle(File path, String name) throws JRException, IOException, CitationStyleManagerException{

        JRProperties.setProperty(JRProperties.COMPILER_KEEP_JAVA_FILE, String.valueOf(KEEP_COMPILER_KEEP_JAVA_FILE));
        compileCitationStyleJRToFile(path, name);
        compileCitationStyleJRToXml(path, name);

    }

    /**
     * Deletes CitationStyleBundle
     * @param path
     * @param name A name of CitationStyle
     * @throws IOException
     * @throws Exception
     */
    public static void deleteCitationStyleBundle(File path, String name) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("deleteCitationStyleBundle: there is no null directory");        }

        path = new File(path, name);
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("deleteCitationStyleBundle: there is no directory:" + path);
        }
        for (String f: path.list())
        	new File(path, f).delete();
        path.delete();
    }

   
    
    /**
     * Returns list of Citation Styles 
     * @param root
     * @return list of Citation Styles
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static String[] getCitationStylesList(File root) throws IllegalArgumentException, IOException {
    	File templPath = new File(getCsPath(root).toString());
    	return templPath.list(
    			new FilenameFilter() {
    				public boolean accept(File dir, String name){
    					return (new File(dir, name)).isDirectory() && !name.startsWith(".");
    				}
    			}
    	);
    }

    public static String[] getCitationStylesList() throws IllegalArgumentException, IOException {
    	return getCitationStylesList(new File("."));
    }
    
    public static boolean findInList(String[] a, String name){
    	for (String s: a) {
			if (s.equals(name)) 
				return true;
		}
    	return false;
    }    
    

    /**
     * @param str
     * @param count
     * @return
     */
    public static String xmlEncode(String str, int count) {
    	if (str!=null && str.length()>0) 
    		for (int i = 1; i <= count; i++) 
    			str = JRStringUtil.xmlEncode(str);
    	return str;
    }
    
    /**
     * @param str
     * @return
     */
    public static String xmlEncode(String str) {
    	return xmlEncode(str, 2);
    }

    

	
	/**
	 * @param root
	 * @return
	 */
	public static File getCsPath(File root) throws IOException{

        root = new File(root.getCanonicalPath());
        return new File(
        		root + "/" + ResourceUtil.CITATIONSTYLES_DIRECTORY
        );
		
	}
	
	
	/**
	 * @param root
	 * @param csName
	 * @throws Exception 
	 * @throws JRException 
	 * @throws IOException 
	 * @throws CitationStyleManagerException 
	 * @throws Exception
	 */
	public void compileReport(File root, String csName) throws JRException, IOException, CitationStyleManagerException{
	
		
		File csPath = getCsPath(root);
		
        ProcessScriptlet.cleanUpScriptlets(root, csName, KEEP_OLD_SCRIPTLETS);
        loadCitationStyleBundle(csPath, csName);
        parseAll(csPath, csName);
        saveCitationStyleBundle(csPath, csName);
	}
	
	public void compileReport(String csName) throws JRException, IOException, CitationStyleManagerException {
		compileReport(new File("."), csName);
	}
	
	/**
	 * Fill report on DataSet
	 * @param root is a root dir of application
	 * @param csName is a name of Citation Style to be shown 
	 * @param dsFile is a DataSource file
	 * @param jrpName is a name of generated .jrprint 
	 * @throws IOException 
	 * @throws JRException 
	 * @throws CitationStyleManagerException 
	 * @throws Exception
	 */
	public void fillReport(String csName, File dsFile, String jrpName) throws IOException, JRException, CitationStyleManagerException {


    	JRProperties.setProperty(JRProperties.COMPILER_KEEP_JAVA_FILE, String.valueOf(KEEP_COMPILER_KEEP_JAVA_FILE));
        
        long start = System.currentTimeMillis();
        
        Document document = JRXmlUtils.parse(dsFile);
        
        logger.info("JRXmlUtils.parse(dsFile) : " + (System.currentTimeMillis() - start));
                
        Map params = new HashMap();
        params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);

        //get REPORT_XML_ROOT_XPATH from loadCitationStyleFromXml !!!
        // не нужно!!! загружать прямо с /CitationStyle.jasper, <queryString>!!!!
        //loadCitationStyleFromXml(csName);
        
		String csj = ResourceUtil.getPathToCitationStyles() + csName + "/CitationStyle.jasper"; 
        JasperReport jasperReport = (JasperReport)JRLoader.loadObject(csj); 

        start = System.currentTimeMillis();
        
//        JasperFillManager.fillReportToFile(
//        		ResourceUtil.getPathToCitationStyles()
//        		+ csName 
//                + "/CitationStyle.jasper",
//                // root + 
//                jrpName + ".jrprint",
//                params,
//                new JRXmlDataSource(document, REPORT_XML_ROOT_XPATH)
//         );
        JasperFillManager.fillReportToFile(
        		jasperReport,
        		// root + 
        		jrpName + ".jrprint",
        		params,
        		new JRXmlDataSource(document, jasperReport.getQuery().getText())
        );
        
        logger.info("JasperFillManager.fillReportToFile : " + (System.currentTimeMillis() - start));        
	}

	
	/**
	 * @param csName
	 * @param dsFile
	 * @throws JRException 
	 * @throws IOException 
	 * @throws CitationStyleManagerException 
	 */
	public void fillReport(String csName, File dsFile) throws IOException, JRException, CitationStyleManagerException {
		
		fillReport(csName, dsFile, DEFAULT_REPORTNAME );
	};

	/**
     * Fills Citation Style to an OutputStream
	 * @param citationStyle
	 * @param os
	 * @param outFormat
	 * @param itemList
	 * @throws JRException
	 * @throws IOException
	 * @throws CitationStyleManagerException
	 */
	private void fillReportToOutputStream(String citationStyle, OutputStream os, String outFormat, String itemList) throws JRException, IOException, CitationStyleManagerException   {


//		long start;
//		start = System.currentTimeMillis();

		ByteArrayInputStream bais = new ByteArrayInputStream(itemList.getBytes("UTF-8"));
		BufferedInputStream bis = new BufferedInputStream(bais);

		Document document = JRXmlUtils.parse(bis);
//		logger.info("JRXmlUtils.parse(ByteArrayInputStream) : " + (System.currentTimeMillis() - start));        
		

		String csj = ResourceUtil.getPathToCitationStyles() + citationStyle + "/CitationStyle.jasper"; 
		JasperReport jr = (JasperReport)JRLoader.loadObject(csj); 
		if ( jr == null )
		{
			throw new CitationStyleManagerException("Cannot find: " + csj);
		}
		

		Map params = new HashMap();
		params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
		
		// generate snippet export 
		if (OutFormats.snippet == OutFormats.valueOf(outFormat))  
		{
			ProcessSnippet psn = new ProcessSnippet();
			psn.export(document, params, jr, os);
			return;
		}
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				jr,
				params,
				new JRXmlDataSource(document, jr.getQuery().getText())
		);

//		logger.info("JasperFillManager.fillReportToStream : " + (System.currentTimeMillis() - start));
		
//		start = System.currentTimeMillis();

		JRExporter exporter = null;    
		
		switch ( OutFormats.valueOf(outFormat) ) {
			case pdf:
				exporter = new JRPdfExporter();
				break;
			case html:
				exporter = new JRHtmlExporter();
				/* Switch off pagination and null pixel alignment for JRHtmlExporter */
		        exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
		        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
				break;
			case rtf:
				exporter = new JRRtfExporter();
				break;
			case odt:
				exporter = new JROdtExporter();
				break;
			default: 	
				throw new CitationStyleManagerException (
						"Output format " + outFormat + " is not supported");
		}
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
		

		
		exporter.exportReport();

		//		logger.info("export to " + outFormat + ": " + (System.currentTimeMillis() - start));


	}
	
	/**
	 * View report in different output formats
	 * @param root is a root dir of application
	 * @param jrpName is a name of .jrprint with filled report
	 * @param csName is a name of Citation Style to be shown 
	 * @param outFormat is an out put format
	 * @throws JRException 
	 * @throws Exception
	 */
	public void viewReport(String jrpName, String csName, String outFormat) throws JRException {

		outFormat = outFormat==null || outFormat.equals("") ? TASK_VIEW : outFormat;
		jrpName = jrpName==null || jrpName.equals("") ? DEFAULT_REPORTNAME : jrpName;
        
        String fileName =  jrpName + ".jrprint";

		if (outFormat.equalsIgnoreCase(TASK_VIEW)) {
			
			JasperViewer.viewReport(fileName, false, false);
			
        } else if (outFormat.equalsIgnoreCase(TASK_PRINT)) {
            
            JasperPrintManager.printReport(fileName, true);
			    
		}else if (outFormat.equalsIgnoreCase(TASK_PDF)) {

			JasperExportManager.exportReportToPdfFile(fileName);

		}else if (outFormat.equalsIgnoreCase(TASK_XML)) {
			
			JasperExportManager.exportReportToXmlFile(fileName, false);
			
		}else if (outFormat.equalsIgnoreCase(TASK_HTML)) {

			JRExporter exporter = new JRHtmlExporter();
			
            exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, fileName);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, csName + ".html");
            
			/* Switch off pagination and null pixel alignment for JRHtmlExporter */
			exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			
			exporter.exportReport();

//            JasperExportManager.exportReportToHtmlFile(fileName);

		}else if (outFormat.equalsIgnoreCase(TASK_RTF)) {

			File sourceFile = new File(fileName);

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");

			JRRtfExporter exporter = new JRRtfExporter();

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());

			exporter.exportReport();
            
		}else if (outFormat.equalsIgnoreCase(TASK_ODT)) {
		    
            File sourceFile = new File(fileName);
            
            JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
    
            File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".odt");
            
            JROdtExporter exporter = new JROdtExporter();
            
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
            
            exporter.exportReport();
        }   

	}
	
	/**
	 * @param csName
	 * @throws JRException 
	 * @throws Exception
	 */
	public void viewReport(String csName) throws JRException {
		
		viewReport(DEFAULT_REPORTNAME, csName, TASK_VIEW );
		
	}

		
	/**
	 * @param csName
	 * @param outFormat
	 * @throws JRException 
	 * @throws Exception
	 */
	public void viewReport(String csName, String outFormat) throws JRException {
		
		viewReport(DEFAULT_REPORTNAME, csName, outFormat);
	}
		

		
	/* 
	 * Explains citation styles and output types for them 
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#explainStyles()
	 */
	public String explainStyles() throws CitationStyleManagerException {
		
		String fileString = null;
		
		try {
			fileString = ResourceUtil.getResourceAsString(
					ResourceUtil.getPathToSchemas()
					+ ResourceUtil.EXPLAIN_FILE
			);
		} catch (IOException e) {
        	throw new CitationStyleManagerException(e);
		}
		return fileString;
	}
	
	
	public byte[] getOutput(String citationStyle, final String outFormat, String itemList) throws JRException, IOException, CitationStyleManagerException  {
		
		if (outFormat == null || outFormat.trim().equals(""))
			throw new CitationStyleManagerException("Output format is not defined");

		if (itemList == null || itemList.trim().equals("") )
			throw new CitationStyleManagerException( "Empty item-list" );
		
		int slashPos = outFormat.indexOf( "/" );
		String ouf = slashPos == -1 ? outFormat : outFormat.substring( slashPos + 1 );
		// TODO: mapping should be taken from explain-styles.xml 
		if (ouf.equals("vnd.oasis.opendocument.text")) 
			ouf = "odt";
		 
		try {
			OutFormats.valueOf(ouf);
		} catch (Exception e) {
			throw new CitationStyleManagerException( "Output format: " + outFormat + " is not supported" );
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
		fillReportToOutputStream(citationStyle, baos, ouf, itemList);
		
		return baos.toByteArray();
		
	}
	
	
	public byte[] getTextOutput(JasperReport jr, Document document, String xpath) throws JRException, CitationStyleManagerException  {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Map params = new HashMap();
		params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);

		
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				jr,
				params,
				new JRXmlDataSource(document, xpath)
		);

		JRExporter exporter = new JRTextExporter();    
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
        exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
		
		
		exporter.exportReport();
		
		return baos.toByteArray();
		
	}
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#isCitationStyle(java.lang.String)
	 */
	public boolean isCitationStyle(String citationStyle) throws CitationStyleManagerException 
	{
		if ( citationStyle == null || citationStyle.trim().equals("") )
		{
			throw new CitationStyleManagerException("Empty name of the citation style");
		}
		
		try {
			for ( String csn : XmlHelper.getListOfStyles() )
				if ( csn.equals(citationStyle) )
					return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new CitationStyleManagerException(e);
		}
		
		return false;
		
	}

	
	
	/*---------------*/
	public JasperDesign getJasperDesign() {
		return jasperDesign;
	}

	
	/**
     * Main class method. An application starts form here.
     *
     * @param args  - Application argument list
     * @return Nothing
	 * @throws CitationStyleManagerException 
	 * @throws IOException 
	 * @throws JRException 
	 * @throws TransformerException 
     */
    public static void main(String args[]) throws JRException, IOException, CitationStyleManagerException, TransformerException {



        String dsName = null;
        String csName = null;
        String taskName = null;

        if(args.length == 0)
        {
            usage();
            return;
        }

        int k = 0; 
        while ( args.length > k )
        {
            if ( args[k].startsWith("-T") )
                taskName = args[k].substring(2);
            if ( args[k].startsWith("-CS") )
                csName = args[k].substring(3);
            if ( args[k].startsWith("-DS") )
                dsName = args[k].substring(3);

            k++;
        }

        
        ProcessCitationStyles pcs = new ProcessCitationStyles(); 
        
        dsName = ResourceUtil.getPathToDataSources() + dsName;
        
            long start = System.currentTimeMillis();

            if (TASK_COMPILE.equalsIgnoreCase(taskName)) {
            	
            	pcs.compileReport(csName); 

                logger.info("Compiling time : " + (System.currentTimeMillis() - start));
                

            } else if (TASK_FILL.equalsIgnoreCase(taskName)) {
            	
            	pcs.fillReport(csName, new File(dsName));
            	
                logger.info("Filling time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_VIEW.equalsIgnoreCase(taskName))
            {
            	pcs.viewReport(csName);
            	
                logger.info("View creation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_PRINT.equalsIgnoreCase(taskName))
            {
                pcs.viewReport(csName, TASK_PRINT);
                
                logger.info("Print time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_PDF.equalsIgnoreCase(taskName))
            {
            	pcs.viewReport(csName, TASK_PDF);
            	
                logger.info("PDF creation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_XML.equalsIgnoreCase(taskName))
            {
            	pcs.viewReport(csName, TASK_XML);
            	
            	logger.info("XML creation time : " + (System.currentTimeMillis() - start));
            	
            }
            else if (TASK_RTF.equalsIgnoreCase(taskName))
            {
            	pcs.viewReport(csName, TASK_RTF);
            	
                logger.info("RTF creation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_HTML.equalsIgnoreCase(taskName))
            {
                pcs.viewReport(csName, TASK_HTML);
                
                logger.info("HTML creation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_ODT.equalsIgnoreCase(taskName))
            {
                pcs.viewReport(csName, TASK_ODT);

                logger.info("ODT creation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_FILL_SORT_TITLE.equalsIgnoreCase(taskName))
            {
                File xmlFile = new File(dsName);
                File xsltFile = new File(
                			ResourceUtil.getPathToTransformations()
                			+ "sort.xsl"
                );
                
                File xmlSortedFile = new File("sorted.xml");

                javax.xml.transform.Source xmlSource =
                        new javax.xml.transform.stream.StreamSource(xmlFile);
                javax.xml.transform.Source xsltSource =
                        new javax.xml.transform.stream.StreamSource(xsltFile);
              javax.xml.transform.Result result =
            	  new javax.xml.transform.stream.StreamResult(
            			  ResourceUtil.getPathToDataSources()
            			  + xmlSortedFile
            	  );
                
                

                // create an instance of TransformerFactory
                javax.xml.transform.TransformerFactory transFact =
                        javax.xml.transform.TransformerFactory.newInstance(  );

                javax.xml.transform.Transformer trans =
                        transFact.newTransformer(xsltSource);

                trans.transform(xmlSource, result);
                
                logger.info("Sorting of file: " + (System.currentTimeMillis() - start));
                
                start = System.currentTimeMillis();
                
                pcs.fillReport(
                		csName, 
                		new File(
                				ResourceUtil.getPathToDataSources() 
                				+ xmlSortedFile
                		)
                );

                logger.info("Filling of the sorted file: " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_VALIDATE_DS.equalsIgnoreCase(taskName))
            {

            	XmlHelper xh = new XmlHelper();
                try {
                	xh.validateDataSourceXML(dsName);
                    logger.info("DataSource file:" + dsName + " is valid.");
                }catch (CitationStyleManagerException e){
                    logger.info("DataSource file:" + dsName + " is not valid.\n" + e.toString());
                }
                logger.info("Data Source Validation time : " + (System.currentTimeMillis() - start));
                
            }
            else if (TASK_VALIDATE_CS.equalsIgnoreCase(taskName))
            {
            	
            	
            	XmlHelper xh = new XmlHelper();
                try {
                	xh.validateCitationStyleXML(
                			ResourceUtil.getPathToCitationStyles()
                			+ csName + "/" + pcs.CITATION_XML_FILENAME );
                	logger.info("Citation Style XML file for " + csName + " is valid.");
            	}catch (CitationStyleManagerException e){
            		logger.info("Citation Style definition file:" + csName + " is not valid.\n" + e.toString());
            	}
                logger.info("Citation Style validation time : " + (System.currentTimeMillis() - start));
                
            }
            else
            {
                usage();
                
            }



    }

    /**
     * @throws IOException 
     * @throws FileNotFoundException 
     *
     */
    private static void usage() throws FileNotFoundException, IOException
    {
        logger.debug( "ProcessCitationStyles usage:" );
        logger.debug( "\tjava ProcessCitationStyles -Ttask -CScitationstyle -DSdatasource" );
        logger.debug( "\tTasks : compile | fill | view | pdf | rtf" );

//      ------------------------------------------


        boolean err = false;

        String templStyle = "test";
        String newStyle = "APA";

        ProcessCitationStyles pcs = new ProcessCitationStyles(); 

        try {
			pcs.compileReport("APA");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			err = true;
		}

        if (!err) {
            logger.debug("Sucessfully processed!");
        }
    }
    
}



/*==========================================================================*/
