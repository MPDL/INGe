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

package de.mpg.escidoc.services.citationmanager.xslt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.saxon.event.SaxonOutputKeys;

import de.mpg.escidoc.services.citationmanager.CitationStyleManager;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/**
*
* Citation Style Manager    
*
* @author Initial creation: vmakarenko 
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

public class CitationStyleManagerImpl implements CitationStyleManager 
{

	public static enum TASKS { validate, compile, pdf, rtf, odt, html_plain, html_styled, txt, snippet, escidoc_snippet }; 
	
    private static XmlHelper xh = new XmlHelper();
    
    private static CitationStyleExecutor cse = new CitationStyleExecutor();
    

	public void compile(String cs) throws CitationStyleManagerException  
	{
		Utils.checkName(cs, "Citaion Style is not defined");
 
		try 
		{
			Transformer transformer = XmlHelper.tryTemplCache(
				ResourceUtil.getPathToClasses() 
				+ ResourceUtil.TRANSFORMATIONS_DIRECTORY  
				+ ResourceUtil.CITATION_STYLE_PROCESSING_XSL 
			).newTransformer();
			
			transformer.setURIResolver( new compilationURIResolver() );
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(SaxonOutputKeys.INDENT_SPACES, "4");
			
			transformer.transform(
					new StreamSource(
						ResourceUtil.getResourceAsStream(
							ResourceUtil.getPathToCitationStyleXML(cs)
						)
					), 
					new StreamResult(
						new FileOutputStream(
								ResourceUtil.getPathToCitationStyleXSL(cs)	
						)
					)
			);
			
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Cannot compile Citation Style " + cs, e);
		}
		

		
	}

	/**
	 * Class to resolve the XMLs location URIs 
	 * needed for compilation
	 * supported resources:   
	 *		variables.xml (global)
	 *		<CitationStyle>/variables.xml
	 *		font-styles.xml 
	 *		cs-processing-xslt-includes.xml 
	 *		functions.xml
	 */
	class compilationURIResolver implements URIResolver {

		public Source resolve(String href, String base) throws TransformerException 
		{
			InputStream is;
			try 
			{
				String 	path = (
 							"cs-processing-xslt-includes.xml".equals(href) ?
								ResourceUtil.getPathToClasses() +  ResourceUtil.TRANSFORMATIONS_DIRECTORY : 
								ResourceUtil.getPathToCitationStyles()
						) + href;
//				System.out.println(path);
				is = ResourceUtil.getResourceAsStream(path);
			} 
			catch (IOException e) 
			{
				throw new TransformerException(e);
			} 
			return new StreamSource(is);
		}
	}
	
	public void create(String cs) {
		// TODO Auto-generated method stub
		
	}

	public void delete(String cs) {
		// TODO Auto-generated method stub
		
	}


	public void update(String cs, String newCs) 
	{
		// TODO Auto-generated method stub
		
	}

	public String validate(String cs) throws IOException, CitationStyleManagerException  
	{
		Utils.checkName(cs, "Citaion Style is not defined");
		
		return xh.validateCitationStyleXML(cs);
	}

	public static void main(String args[]) throws IOException, CitationStyleManagerException, JRException 
	{
	
		CitationStyleManager csm = new CitationStyleManagerImpl();
		
		String il = null;
        String cs = null;
        String task = null;
        String of = null;

        if(args.length == 0)
        {
            usage();
            return;
        }

        int k = 0; 
        while ( args.length > k )
        {
            if ( args[k].startsWith("-T") )
                task = args[k].substring(2);
            if ( args[k].startsWith("-CS") )
                cs = args[k].substring(3);
            if ( args[k].startsWith("-OF") )
            	of = args[k].substring(3);
            if ( args[k].startsWith("-IL") )
                il = args[k].substring(3);

            k++;
        }
		
//        if ( task.equals(TASKS.validate.toString()))
        if ( TASKS.valueOf(task) == TASKS.validate )
        {
        	String report = csm.validate(cs);
        	if (report == null)
        		System.out.println(cs + " Citation Style XML for is valid.");
        	else
        		System.out.println(cs + " Citation Style XML for is not valid:\n" + report);
        	
        } 
        else if ( TASKS.valueOf(task) == TASKS.compile )
        {
        	System.out.println(cs + " Citation Style compilation.");
        	csm.compile(cs);
        	System.out.println("OK");
        }
        //all other tasks
        else if ( TASKS.valueOf(task) != null )
        {
        	String outFile = cs + "_output_" + task + "." + ResourceUtil.getExtensionByName(task);  
        	System.out.println(cs + " Citation Style output in " + task + " format. File: " + outFile);
        	byte[] result = cse.getOutput(cs, task, ResourceUtil.getResourceAsString(il));
        	FileOutputStream fos = new FileOutputStream(outFile);
        	fos.write(result);
        	fos.close();
        	System.out.println("OK");
        } 
         
		
	}
	
    private static void usage()
    {
        System.out.println( "CitationStyleManagerImpl usage:" );
        System.out.println( "\tjava CitationStyleManagerImpl -Ttask -CScitationstyle -ILitemlist" );
        System.out.println( "\tTasks : validate | compile | pdf | rtf | odt | txt | html_plain | html_styled | snippet | escidoc_snippet" );

    }	
	
}
