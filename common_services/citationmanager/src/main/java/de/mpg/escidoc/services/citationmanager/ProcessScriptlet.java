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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.data.CitationStyle;
import de.mpg.escidoc.services.citationmanager.data.CitationStylesCollection;
import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.data.FontStylesCollection;
import de.mpg.escidoc.services.citationmanager.data.LayoutElement;
import de.mpg.escidoc.services.citationmanager.data.Parameters;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;


/**
*
* Generate Scriptlet class for repeatable elements processing
*
* @author makarenko (initial creation)
* @author $Author: vdm $ (last modification)
* @version $Revision: 146 $ $LastChangedDate: 2007-11-12 20:58:08 +0100 (Mon, 12 Nov 2007) $
*
*/

public class ProcessScriptlet {

    private static final Logger logger = Logger.getLogger(ProcessScriptlet.class);
//    private static final Logger logger = Logger.getLogger(ProcessScriptlet.class);
	
    public final static String SCRIPTLET_CLASSNAME_PREFIX = "ScriptletForRepeatableElements";
    // TODO: should be moved directly to the CS definition
    public static String SCRIPTLET_XPATH_ROOT = null; 
    public final static String SCRIPTLETS_JAVA_DIRECTORY = "src/main/java/";
    private String scriptletClassName = null;
    
    

//  table of special functions which can be applied in layout-elements (@func="funcname")
    public static Properties scriptletFunctions = null;

    public final static String scriptletBodyHeader =
        "package de.mpg.escidoc.services.citationmanager.scriptlets;\n" +
        "import net.sf.jasperreports.engine.JRDefaultScriptlet;\n" +
//        "import net.sf.jasperreports.engine.JRScriptletException;\n" +
//        "import net.sf.jasperreports.engine.JRAbstractScriptlet;\n" +
        "import net.sf.jasperreports.engine.data.JRXmlDataSource;\n" +
        "import net.sf.jasperreports.engine.design.JRDesignField;\n" +
        "import net.sf.jasperreports.engine.util.JRStringUtil;\n" +
        "import de.mpg.escidoc.services.citationmanager.utils.Utils;\n" +
//        "import org.w3c.dom.Document;\n" +
//        "import org.w3c.dom.Node;\n" +
//        "import java.util.StringTokenizer;\n" + // for func get_initials
//        "import java.util.ArrayList;\n\n" +
        "import java.util.*;\n" +
        "import java.util.regex.*;\n\n" +

        "public class %s extends JRDefaultScriptlet {\n" +
        "private ArrayList<String[]> elems = new ArrayList<String[]>();\n" +
        "private long cTime = 0;\n" +

        "private String insertDelimiter(String left, String delim, String right) {\n" +
        "    String result;\n" +
        "    return (delim!=null && delim.length()>0 &&\n" +
        "       left!=null && left.length()>0 &&\n" +
        "        right!=null && right.length()>0 ) ? delim : \"\";\n" +
        "}\n" +

        "public String xmlEncode(String str) {\n" +
        "   if (str!=null && str.length()>0) {\n" +
//      "       str = JRStringUtil.xmlEncode(str);\n" +
        "       str = JRStringUtil.xmlEncode(str);\n" +
        "   }\n" +
        "   return str;\n" +
        "}\n" +
        
//        "public void testString(String str) {\n" +
//        "   for(char ch : str.toCharArray()) {\n" +
//        "		System.out.print(\"ch:\"+ch+\",int:\"+((int)ch)+\";\");\n" +
//        "   }\n" +
//        "}\n" +
        
        
//  Remove dublications of blanks, spaces and punctuation
//	TODO: check it!        
        "public String cleanCit(String str) {\n" +
        "   if (Utils.checkLen(str)) {\n" +
//        "System.out.println(\"before:\" + str);\n" +
//        "if ( str.indexOf(\"Working paper on research progress\")!=-1 ) {System.out.println(\"--1:\" + str);\n}" +
        // remove nulls
        "       str = str.replace(\"null\", \"\");\n" +
        // replace all \s with blank, new line as well everywhere in citation
		"		str = Pattern.compile(\"[\\n\\r\\t]+\", Pattern.DOTALL).matcher(str).replaceAll(\" \");\n" +
        // remove empty styled text:
        "       str = str.replaceAll(\"<style[^>]*?>\\\\s*<[/]style[^>]*?>\",\"\");\n" +
        // remove empty snippet tags:
//        "System.out.println(\"--1:\" + str);\n" +
		"       str = str.replaceAll(\"\\\\[span.*?\\\\]\\\\s*\\\\[/span\\\\]\",\"\");\n" +
		// replace all duplicated punctuations with the only one
//        "System.out.println(\"--2:\" + str);\n" +
//		"       str = str.replaceAll(\"(([.,:;?!])+\\\\s*\\\\2)+\",\"$2\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[.]+)+\",\".\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[,]+)+\",\",\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[:]+)+\",\":\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[?]+)+\",\"?\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[!]+)+\",\"!\");\n" +
		"       str = str.replaceAll(\"(\\\\s*[;]+)+\",\";\");\n" +
		// punctuation combinations: 
		// remove dot after punctuations ([?!]). => $1
		"       str = str.replaceAll(\"([?!])\\\\s*[.]\",\"$1\");\n" +
		
		
		// remove all \s before punctuations (";" are excluded due to xml entities)
//        "System.out.println(\"--3:\" + str);\n" +
//		"       str = str.replaceAll(\"\\\\s+([.,:;?!])\",\"$1\");\n" +
//        "if ( str.indexOf(\"Working paper on research progress\")!=-1 ) {testString(str);\n}" +
//        "if ( str.indexOf(\"Working paper on research progress\")!=-1 ) {System.out.println(\"--2:\" + str);\n}" +
//				next 2: ( 2007 ) to (2007) 		
//        "System.out.println(\"--4:\" + str);\n" +
		"       str = str.replaceAll(\"([({<\\\\[])\\\\s+(.*)\",\"$1$2\");\n" + 
//        "System.out.println(\"--5:\" + str);\n" +
		"       str = str.replaceAll(\"(.*)\\\\s+([\\\\]>})])\",\"$1$2\");\n" + 
//        "if ( str.indexOf(\"( 2008\")!=-1 ) {testString(str);\n}" +
//        "System.out.println(\"uge kaputt:\" + str);\n" +
//		"System.out.println(\"before2:\" + str);\n" +
//		"       str = str.replaceAll(\"([,.;:?!])+(\\\\s)+(\\\\<[/]?style.*?\\\\>)(\\\\s)+([,.;:?!])+\",\"$1$2$3\");\n" +
//		"System.out.println(\"before3:\" + str);\n" +
//        "System.out.println(\"after:\" + str);\n" +
        // remove hanging punctuations round the style 
		"       str = str.replaceAll(\"(([.,;?!])+\\\\s*<[/]?style[^>]*?>)\\\\s*\\\\2+\",\"$1\");\n" +
		"       str = str.replaceAll(\"\\\\s+(<[/]?style[^>]*?>)?\\\\s+\",\"$1 \");\n" +
		"       str = str.replaceAll(\"\\\\s*(<[/]?style[^>]*?>)\\\\s*([.,;?!])\",\"$1$2\");\n" +
		// snippet clean up
		"       str = str.replaceAll(\"(([.,;?!])+\\\\s*\\\\[[/]?span\\\\s*\\\\])\\\\s*\\\\2+\",\"$1\");\n" +
		"       str = str.replaceAll(\"\\\\s+(\\\\[[/]?span\\\\s*\\\\])\\\\s+\",\"$1 \");\n" +
		"       str = str.replaceAll(\"\\\\s*(\\\\[[/]?span\\\\s*\\\\])\\\\s*([.,;?!])\",\"$1$2\");\n" +
//        "if ( str.indexOf(\"Working paper on research progress\")!=-1 ) {System.out.println(\"--3:\" + str);\n}" +
//        "System.out.println(\"after2:\" + str);\n" +
        "   }\n" +
        "   return Utils.checkVal(str) ? str: null;\n" +
        "}\n";


//  scriptletBody for repeatable elements
    private String scriptletBody;
    

    public ProcessScriptlet(CitationStyle cs) throws FileNotFoundException, IOException 
    {
    	this(cs, "");
    }

    public ProcessScriptlet(CitationStyle cs, String postfix) throws FileNotFoundException, IOException 
    {
    	
        scriptletBody = String.format(scriptletBodyHeader, generateScriptletClassName(cs.getName() + postfix));
        
        String[] tokens = cs.getMdXPath().split("/");
        SCRIPTLET_XPATH_ROOT = tokens[ tokens.length - 1 ];    
        
    	scriptletFunctions = ResourceUtil.getProperties(ResourceUtil.CITATIONSTYLES_DIRECTORY, "functions.properties");
    	Iterator iter = scriptletFunctions.keySet().iterator(); 
    	while (iter.hasNext()) 
    		scriptletBody += scriptletFunctions.getProperty((String)iter.next());

    }

    
    /**
     * checks whether func in scriptletFunctionsTable
     * @param func - name of function to be applied
     * @return true if func in scriptletFunctionsTable, false - otherwise
     * @throws CitationStyleManagerException
     */
    public static boolean isInScriptletFunctionsTable(String func) throws CitationStyleManagerException {
        if ( func==null || func.trim().equals("") || scriptletFunctions==null )
            return false;
        if (scriptletFunctions.containsKey(func)) 
        	return true;
        throw new CitationStyleManagerException("Unknown function:<" + func +">");
    }

    private static String getWhileFooterChunk(String pos, int maxCount) {
        String wf = "}\n\n}\n" +
        "int es = elems.size();\n";
        if (pos.equals("last")) {
            wf += 
// debug -->
//                  "System.out.println(\"hasLast && count>2 && !subDs.next(), last, str, elems.size():\" + " +
//                  "hasLast + \",\" + count + \",\" + n + \",\" + last + \",\" + str + \",\" + elems.size());\n" +
// debug <--
            "if ( hasLast && count>2 && ( " +
            ( maxCount>0 ? "maxCount || " : "") +
            "!subDs.next( ) ) ) {\n" +
            "int idx = es - ( " +
            ( maxCount>0 ? "maxCount ? 3 : " : "") +
            "2 ) ;\n" +
            "String[] elem = (String[])elems.get( idx );\n" +
            "elems.set(idx + 1, new String[]{ last, " +
            ( maxCount>0 ? "maxCount ? elem[1] : " : "")+
            "\"\" });\n" +
            "elem[1] = delim;\n" +
            "elems.set(idx, elem);\n" +
            "\n}\n";

        }
        wf +=
        "for(int i=0; i<es; i++) {\n" +
            "String[] elem = (String[])elems.get(i);\n" +
            "result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:\"\") : \"\";\n" +
         "}\n" +
         "elems.clear();\n" +

//      debug <---
//         "System.err.println(\"Scriptlet time for \" + XPath + \": \" + (System.currentTimeMillis() - start));\n" +
//         "cTime+=System.currentTimeMillis() - start;" +
//         "System.err.println(\"Complete time: \" + cTime);\n" +
//       debug --->

         "return result;\n}\n";
        return wf;
    }


    /**
     * Returns internal part of while loop according to position
     * @param elements
     * @param position
     * @param flag is switcher for first "if" cause.
     * @return String
     */
    private static String getWhileInternalChunk(
            List<LayoutElement> elements,
            Parameters parameters,
            FontStylesCollection fsc,
            boolean flag)
        throws CitationStyleManagerException {

        String startsWith;
        String endsWith;
        Parameters ep;
        int pos = 0;
        int i = 0;
        boolean err = false;
        String chunkDef = "";
        String chunkRes = "str = ";
//      String de = null;

        String position = parameters.getPosition();
        String internalDelimiter = parameters.getInternalDelimiter();
        String delimiter = parameters.getDelimiter();
        String fontStyleRef = parameters.getFontStyleRef();

        // position checking:
        if (position.equals("default") || position.equals("")) {
            position = "default";
        } else if (position.equals("last")){
            pos = -1;
            chunkRes = "last = ";
        } else {
            try {
                pos = Integer.parseInt(position);
            } catch(NumberFormatException exc) {
                err = true;
            }
        }
        if (err || pos < -1) {
            throw new CitationStyleManagerException("Invalid position:" + position);
        }

//      Position handling
//      last handling (not in if!)
        if (pos == -1) {
            chunkDef = "}\nif (!hasLast) hasLast = true;\n" +
//            		"subDs.next();" +
//            		"subDs.moveFirst();" +
            		"if (hasLast) {\n";
        } else if (pos == 0) {
            chunkDef = "default:\n";
//      position is number
        } else {
            chunkDef = "case " + pos + ":\n";
        }

//      Internal part
        for (LayoutElement e : elements) {
            // only startsWith and endsWith are applicable for the moment for
            // elements/layoutElement/parameters in repetable le !!!
//            ep = e.getParameters();
            ep = e.getParametersAtDefault();


//            de = getDOMExpression(e.getRef());
//            if (de==null || de.length()==0 )
//                throw new Exception("Invalid reference:" + e.getRef());


            String chunkN = "chunk_" + position + "_" + i;
            String fieldN = "field_" + position + "_" + i;

//            chunkDef += chunkN + " = " + de + ";";
            chunkDef += chunkN + " = (String)subDs.getFieldValue(" + fieldN + ");";

//          xmlEncode for value!
            chunkDef += chunkN + " = xmlEncode(" + chunkN + ");";

//          debug <---
//          chunkDef += "System.out.println(\"Debug (field,value)=(\" + " + fieldN + ".getDescription() + \",\" + " + chunkN +" + \")\");";
//          debug --->


            chunkDef += chunkN + " = " + chunkN + "!=null && " + chunkN + ".length()>0 ? " + chunkN + " : \"\";";

            // Function handling
            String func = e.getFunc();
            if ( isInScriptletFunctionsTable( func ) )
                chunkDef += chunkN + " = " + func + "(" + chunkN + ");";

            // starts&endsWith (xmlEncoded!)
            startsWith = Utils.xmlEncode(ep.getStartsWith(), 1);
            endsWith = Utils.xmlEncode(ep.getEndsWith(), 1);
            chunkDef += chunkN + " = " +
                chunkN + ".length()>0 ? (" +
                (Utils.checkLen(startsWith) ? "\"" + startsWith + "\" + " : "") +
                chunkN +
                (Utils.checkLen(endsWith) ? " + \"" + endsWith + "\"" : "") +
                ") : \"\"; ";


            // maxLength handling
            int maxLength = ep.getMaxLength();
            if (maxLength > 0) {

                // TODO: Not really the length of string due to XML encoding
                chunkDef += chunkN + " = " + chunkN + ".length() > " + maxLength + " ? " + chunkN + ".substring(0, " + (maxLength - 1) + ")";
                //maxLengthEndsWith
                String maxLengthEndsWith = Utils.xmlEncode(ep.getMaxLengthEndsWith(), 1);
                if (Utils.checkLen(maxLengthEndsWith)) {
                    chunkDef += " + \"" + maxLengthEndsWith + '"';
                }
                chunkDef += " : " + chunkN +";";
            }

            chunkRes += chunkN;

            // internalDelimiter handling
            internalDelimiter = Utils.xmlEncode(internalDelimiter, 1);
            chunkRes += (internalDelimiter != null && internalDelimiter.length() > 0 && i<elements.size()-1?
                    " + insertDelimiter(" +
                            chunkN + ", \"" +
                            internalDelimiter + "\", " +
                            "chunk_" + position + "_" + (i + 1) +
                            ") + " : "");



//          FontsStyles Handling  
            chunkDef += chunkN + " = " + func + "(" + chunkN + ");";
            FontStyle fs = fsc.getFontStyleByName(fontStyleRef);
            chunkDef += chunkN + " = " +
//              String.format(fsc.getFontStyleByName(ep.getFontStyleRef()).toStyle(), chunkN) + ";\n";
               fs.applyStyle( fs.applyCssClass( chunkN ) ) + ";\n";
            

//          debug <---
//          System.out.println(ep.getFontStyleRef());
//          System.out.println(fontStyleRef);
//          chunkDef += "System.out.println(\"Debug expr=\" + " + expr + " + \")\");\n";
//          debug --->

            i++;


        }
        // trim " + " and last internalDelimiter at the end of chunk4
//        chunkRes = chunkRes.substring(0, chunkRes.length() - " + ".length());
//         if (internalDelimiter != null && internalDelimiter.length() > 0) {
//             chunkRes = chunkRes.substring(0, chunkRes.length() - (internalDelimiter + " + \"\"").length());
//         }

        // delimiter handling
        chunkRes += ";";

        if (delimiter != null && delimiter.length() > 0) {
            delimiter = Utils.xmlEncode(delimiter, 1);
            if (pos==-1) {
                chunkRes +=
                    "delim = \"" + delimiter +"\";";
            } else {
                chunkRes +=
                    "elems.add( new String[]{ str, \"" + delimiter + "\" } );";
            }
        };


        if (pos!=-1) {
            chunkRes += "\nbreak;\n";
        }

        return chunkDef + chunkRes;
    }

    /**
     * Creates content for method which will be process repeatable LayoutElement le
     *
     * @param le is repeatable LayoutElement
     * @return HashMap with all fields to be inserted into Scriptlet method
     * @throws Exception
     */
    public void createMethodForScriptlet(LayoutElement le, CitationStylesCollection csc, FontStylesCollection fsc) throws CitationStyleManagerException {

        String headerChunk = "";
        String whileChunk = "";
        ArrayList<LayoutElement> elements;
        Parameters parameters;
        Parameters pLE;
        Parameters pREF;
        String xPath;
        String ref = le.getRef();
        LayoutElement refLE; 
        String pos;
        int i = 0;
        String name = le.getName();
        String id = le.getId();


//        List<LayoutElement> posLEs = null;
        HashMap positionBundle = null;


//      Check whether element in csc
        if (csc.getCitationStyleByLayoutElementId(id)!=null) {
//            CitationStyle cs = csc.getCitationStyleByLayoutElementName(name);
//            cs.resetPositionBundle();
//            posLEs = cs.getPositionBundle(cs.getElementByName(name), name);
            positionBundle = le.getPositionBundle();
        } else {
            throw new CitationStyleManagerException("Unknown identifier:<" + name + ">; id:<" + id + ">");
        }
//  Sort posLEs always following way:
//  numbers-default-last
//      Collections.sort( posLEs, new MyComparator());



//      Collections.sort(it, new Comparator () {
//      public int compare(Object o1, Object o2) {
//          String s1 = ((LayoutElement)o1).getPosition().toString();
//          String s2 = ((LayoutElement)o2).getPosition().toString();
//          return s1.compareTo(s2);
//      }
//  }
//  );
        Object[] keys = positionBundle.keySet().toArray();
        Arrays.sort(keys);
        for (int j = 0; j < keys.length; j++) {
            String key = (String)keys[j];
            elements = (ArrayList<LayoutElement>)le.getElementsAt(key);
            parameters = (Parameters)le.getParametersAt(key);

            pLE = parameters;
            pREF = null;
            refLE = null;
//
//
            // if there are elements get them
            if (elements.size() > 0) {
                xPath = getXPath(le);
                if ( xPath == null)
                    throw new CitationStyleManagerException("Empty reference for repeatable layout element:<" + name + ">; id:<" + id + ">");
            // if there is no elements => take elements and le.getRef() from ancestor

//               no referencing anymore!
//          } else if (ref != null && ref.length() > 0) {
//              refLE = lec.getElementByName(ref);
//              if (refLE!=null) {
//                  elements = (ArrayList<LayoutElement>)(refLE.getElements());
//                  xPath = getXPath(refLE);
//              } else {
//                  throw new Exception("Error by referencing: There is no layout element:" + ref);
//              }
            } else {
                throw new CitationStyleManagerException
                ("Repeatable layout element:<" + name + "> has no elements; ref=<" + ref + ">");
            }
            // if there is ref, take parameters of it
//          if (ref!=null && ref.length()>0) {
//              refLE = lec.getElementByName(ref);
//              if (refLE != null) {
//                  pREF = refLE.getParameters();
//              }
//          }

            String delimiter = Utils.overParams(pLE.getDelimiter(), pREF != null ? pREF.getDelimiter() : null);
            // only once actions
            int maxCount = 0;
            if (i==0) {
//                headerChunk = getHeaderChunk(name, xPath);
                headerChunk = getHeaderChunk(id, xPath);
                // maxCount handling
                maxCount = Utils.overParams(pLE.getMaxCount(), pREF != null ? pREF.getMaxCount() : 0);
                String maxCountEndsWith = null;
                if (maxCount > 0) {
                	
                	String lastMaxCountEndsWith = le.getParametersAt("last-max-count").getEndsWith();
                    maxCountEndsWith = Utils.overParams(lastMaxCountEndsWith, pLE.getMaxCountEndsWith());
//                    maxCountEndsWith = Utils.overParams(pLE.getMaxCountEndsWith(), pREF != null ? pREF.getMaxCountEndsWith() : null);
                	
                }
                whileChunk = getWhileHeaderChunk(maxCount, maxCountEndsWith, delimiter);
            }

            String internalDelimiter  = Utils.overParams(pLE.getInternalDelimiter(), pREF != null ? pREF.getInternalDelimiter() : null);
            pos = key;
            headerChunk += getJRDesignFieldsChunk(elements, pos);


            whileChunk += getWhileInternalChunk(elements, parameters, fsc, i==0);

            if (i==positionBundle.size()-1) {
                whileChunk += getWhileFooterChunk(pos, maxCount);
            }

//            if (i==0) {
//                lesWithScriptletMethod.add(name);
//            }
            i++;
        }

        scriptletBody += headerChunk + whileChunk;

    }


    /**
     * Writes scriptletBody to file
     * @param path
     * @param name
     * @throws IOException
     */
    public File writeToScriptlet() throws IOException{
        scriptletBody += "}";
        
        // TODO: Now we are ignoring path to save scriptlet, 
        String root = ResourceUtil.getPathToClasses();
        
       
        File f = new File(
        		getPathToScriptletJava()
        		+ getScriptletClassName() 
        		+ ".java"
        );
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            out.print(scriptletBody);
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new IOException("Cannot write to the " + f + ": " + e);
        }
        return f;
    }

    private String getHeaderChunk(String name, String xPath) {
        String h = "public String get" + name + "() throws Exception {\n" +
        "String result = \"\";\n" +
        "String str = \"\";\n" +
        "String last = \"\";\n" +
        "String delim = \"\";\n" +

//      debug <---
//        "long start = System.currentTimeMillis();\n" +
//        "String XPath = \""+ xPath + "\";\n" +
//      debug --->

        "JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue(\"REPORT_DATA_SOURCE\"));\n" +
//  debug <---
//        "Document sds = ds.subDocument();\n" +
//        "JRDesignField field = new JRDesignField();\n" +
//        "field.setDescription(\"family-name\");\n" +
//        "field.setValueClass(String.class);\n" +
//        "JRXmlDataSource sds = ds.subDataSource(\"md-record/publication/creator/person\");\n" +
//        "sds.next();\n" +
//        "System.out.println(\"root = \" + (String) sds.getFieldValue(field));\n" +
//  debug --->
        "JRXmlDataSource subDs = ds.subDataSource(\"" + SCRIPTLET_XPATH_ROOT + "/" + xPath + "\");\n";
        return h;
    }



    private static String getJRDesignFieldsChunk(List<LayoutElement> elements, String pos) throws CitationStyleManagerException {
        String fields = "";
        String tmp;
        boolean err = false;
        int i = 0;
        int num = 0;
        // position checking:
        if (pos.equals("default") || pos.equals("")) {
            pos = "default";
        } else if (pos.equals("last")){
            num = -1;
        } else {
            try {
                num = Integer.parseInt(pos);
            } catch(NumberFormatException exc) {
                err = true;
            }
        }
        if (err || num < -1) {
            throw new CitationStyleManagerException("Invalid position:" + pos);
        }

        for (LayoutElement e: elements) {
            tmp = "_" + pos + "_" + i;
            fields +=
                "JRDesignField field" + tmp + " = new JRDesignField();\n" +
                "field" + tmp + ".setDescription(\"" + e.getRef() + "\");\n" +
                "field" + tmp + ".setValueClass(String.class);\n" +
                "String chunk" + tmp + " = \"\";\n\n" ;
            i++;
        }
//      System.out.println("fieldsChunk = " + fieldsChunk);
        return fields;
    }

    private static String getWhileHeaderChunk(int maxCount, String maxCountEndsWith, String delimiter) {
        String wh;
        String d = Utils.checkVal(delimiter) ? "\"" + delimiter + "\"": "";

        wh = 
        	"int count = 1;\n" +	
            "boolean hasLast = false;\n" +
            (
            		maxCount > 0 ?
            		(
            	      "boolean maxCount = false;\n" +
                      "away: while ( subDs.next() ) {\n" +
                      "if ( count >" +  maxCount + ") {\n" +
                      // maxCountEndsWith handling
                      ( Utils.checkVal(maxCountEndsWith) ?
                              " elems.add(new String[]{ \"" + maxCountEndsWith + "\", \"\" } );\n " : "") +
                          " maxCount = true;\n" +
                          " break away;\n}"
            		) :
            		(
            	      "while ( subDs.next() ) {\n"
            		)	
            ) +	
            "\n count++;\n" +
            "switch (count) {\n";            

        
//        	if (maxCount > 0) {
//            wh =
//            "int count = 1;\n" +
//            "boolean hasLast = false;\n" +
//            "boolean maxCount = false;\n" +
//            "away: while ( subDs.next() ) {\n" +
//            "if ( count >" +  maxCount + ") {\n" +
//            // maxCountEndsWith handling
//            (maxCountEndsWith != null && maxCountEndsWith.length() > 0 ?
//                    " elems.add(new String[]{ \"" + maxCountEndsWith + "\", \"\" } );\n " : "") +
//                " maxCount = true;\n" +
//                " break away;\n}" +
//                "\n count++;\n";
//        } else {
//            wh = "int count = 1;\n" +
//            "while ( subDs.next() ) {\n";
//        }

//        wh += "Document d = subDs.subDocument();\n";

//  debug   <---
//        wh +=  "Node node;\n";
//        wh +=  "System.out.println(\"I am here!!!:\" + count);\n";
//  debug   --->

//        wh += "switch (count) {\n";
        return wh;
    }

//    /**
//     * Generates DOM expression to get element value in scriptlet
//     * @param ref is a name of element in DataSource XML
//     * @return chunk string for DOM expression
//     */
//    private static String getDOMExpression(String ref) {
//        String expr = null;
//        if (ref!=null && ref.length()>0) {
//            if (ref.startsWith("@")) {
//                expr = "d.getDocumentElement().getAttribute(\"" + ref.substring(1) + "\")";
//            } else if (ref.startsWith(".")){
//                expr = "subDs.getText(d.getDocumentElement())";
//            } else {
//                expr = "subDs.getText(d.getElementsByTagName(\"" + ref + "\").item(0))";
//            }
//        }
//        return expr;
//    }


    /**
     * Get XPath for scriptlet's method.
     * Criteria: 1) for repeatable elements takes xPath directly from @ref
     * 2) for non-repeatable checks whether ref is in fieldsMap
     * Otherwise returns 0
     * @param le LayoutElement
     * @return XPath
     */
    private static String getXPath(LayoutElement le) {
        String ref = le.getRef();
//      String rep = le.getRepeatable();
        if (ref!=null && ref.length()>0) {
//          if (rep!=null && rep.length()>0 && rep.equals("yes")) {
//              return ref;
//          } else if (findInFieldsMap(ref)) {
                return ref;
//          }
        }
        return null;
    }


    /**
     * Deletes generated Scriptlet .java and .class for style <code>csName</code> in path <code>csPath</code>
     * @param csPath
     * @param csName
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JRException 
     * @throws JRException 
     */
    public static void cleanUpScriptlets(String csName, String cssClassPostfix, boolean keepOldScriptlets) throws IllegalArgumentException, IOException, JRException {

    	// get name of the last created scriptlet to be cleaned up 
    	
    	String[] pfa =  new String[]{"", cssClassPostfix};
    	File file;
    	String fn;
    	
    	for (String pf : pfa)
    	{
        	String csj = ResourceUtil.getPathToCitationStyles() + csName + "/CitationStyle"+ pf + ".jasper"; 
        	InputStream is = ResourceUtil.getResourceAsStream( csj );
        	if ( is == null )
        	{
        		logger.warn( csj + " has not been found, cannot clean up old scriptlets" );
        		return;
        	}
        	JasperReport jr = (JasperReport)JRLoader.loadObject(is);
      	
        	// remove old sriptlet .class file
        	fn = ResourceUtil.getPathToClasses() + convertQNameToPath(jr.getScriptletClass()) + ".class";
        	
            file = new File(fn);
    		// delete .class
            if ( file.delete() )
            {
            	logger.info("action: cleaned up old Scriptlet Class: " + file + " for Citation Style: " + csName);
            }
            else
            {
            	logger.info("action: cannot clean up old Scriptlet Class: " + file + " for Citation Style: " + csName);
            }
            
            //go away if we want to keep OLD SCRIPTLET for debugging
            if (keepOldScriptlets)  
                continue;

        	fn = getPathToScriptletJava() + file.getName().replace(".class", "") + ".java";
            file = new File(fn);
//          remove old sriptlet .java file
            if ( file.delete() )
            {
            	logger.info("action: cleaned up old java Scriptlet Class: " + file + " for Citation Style: " + csName);
            }
            else
            {
            	logger.info("action: cannot clean up old java Scriptlet Class: " + file + " for Citation Style: " + csName);
            }
    		
    	}
    	
    }

    
    /**
     * Returns uniq class name of scriptlet
     * @return scriptlet class name
     */
	public String getScriptletClassName() {
		return scriptletClassName;
	}

    /**
     * Generate and set uniq class name for scriptlet
     * @return generated scriptlet class name
     */ 
	public String generateScriptletClassName(String cs) 
	{
        this.scriptletClassName =  SCRIPTLET_CLASSNAME_PREFIX + "_" + cs ;
        return this.scriptletClassName;
	}
    
    /**
     * Converts QName to path: "/" instead of "."
     * @param qname 
     * @return path
     */
    public static String convertQNameToPath(String qname)
    {
    	return qname == null ? null : qname.replace(".", "/"); 
    }

    /**
     * Returns package name of the class
     * @return package name
     */
    public static String getPackageName()
    {
    	return ProcessScriptlet.class.getPackage().getName() + ".scriptlets";
    }
    
    /**
     * Returns path to the scriptlet sources  
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToScriptletJava() throws IOException 
    {
        return 
        		ResourceUtil.getPathToClasses().replace(ResourceUtil.CLASS_DIRECTORY, SCRIPTLETS_JAVA_DIRECTORY)
        		+ convertQNameToPath(getPackageName() + "/") 
        ;
    }
   
    

}
