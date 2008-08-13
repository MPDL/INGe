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

package de.mpg.escidoc.services.pubman.searching.webservice;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.pubman.PubItemSearching;

/**
 * Servlet for the REST interface.
 *
 * @author franke (initial creation)
 * @author $Author: vmakarenko $ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class RestServlet extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(RestServlet.class);

    @EJB
    private PubItemSearching pubItemSearching;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
    {
        doPost(req, resp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
            {
    	String cqlQuery = null;
    	String language = null;
    	String exportFormat = null;
    	String outputFormat = null;
    	try
    	{
    		try
    		{
    			String qs = req.getQueryString();

    			LOGGER.debug("QueryString: " + qs);

    			// Init exporting service
    			InitialContext ctx = new InitialContext();
    			pubItemSearching = (PubItemSearching) ctx.lookup(PubItemSearching.SERVICE_NAME);

    			cqlQuery = req.getParameter("cqlQuery");

    			if ( cqlQuery == null || cqlQuery.trim().equals("") )
    			{
    				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "cqlQuery is not defined in the QueryString: " + qs);
    				return;
    			}


    			//TODO: default values for all parameters 
    			// should be defined later in VOs
    			language = req.getParameter("language");
    			language = language == null ? "" : language.trim().toLowerCase();
    			if ( language.equals("") )
    			{
    				language = "all";
    			}
    			else if ( "allende".indexOf(language) == -1 )
    			{
    				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong language: " + language);
    				return;
    			}

    			exportFormat = req.getParameter("exportFormat");
    			exportFormat = exportFormat == null ? "" : exportFormat.trim().toUpperCase();
    			if ( exportFormat.equals("") )
    			{
    				exportFormat = "ENDNOTE";
        			//if exportFormat is ENDNOTE set outputFormat forced to the txt  
    			}
    			// export format is not defined. TODO: move to VO 
    			else if ( "APAENDNOTE".indexOf(exportFormat) == -1 )
    			{
    				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong export format: " + exportFormat);
    				return;
    			}

    			if ( exportFormat.equals("ENDNOTE") )
    			{
    				outputFormat = FileFormatVO.TEXT_NAME;
    			}
    			else 
				{
					outputFormat = req.getParameter("outputFormat");
					outputFormat = outputFormat == null ? "" : outputFormat.trim().toLowerCase();
					//get default outputFormat if it is not defined
					if ( outputFormat.equals("") )
	    			{
						outputFormat = FileFormatVO.DEFAULT_NAME;
	    			}
					// check output format consistency
					else if ( !FileFormatVO.isOutputFormatSupported(outputFormat) )
					{
						resp.sendError(
								HttpServletResponse.SC_BAD_REQUEST,
								"File output format: " + outputFormat + 
								" is not supported for the export format: " + exportFormat 
						);
						return;
					}	
				}

    			byte[] result = pubItemSearching.searchAndOutput(cqlQuery, language, exportFormat, outputFormat);

    			String fileName = exportFormat + "_output" + getFileExtension(outputFormat);
    			LOGGER.debug("fileName: " + fileName);
    			String contentType = getContentType(outputFormat);
    			resp.setContentType(contentType);
    			LOGGER.debug("contentType: " + contentType);

    			ServletOutputStream os = resp.getOutputStream( );

    			resp.addHeader( "Content-Disposition","attachment; filename=" + fileName );

    			resp.setContentLength( result.length  );

    			for ( byte b : result )
    			{
    				os.write( b );
    			}
    			os.close();

    		}
    		catch (NamingException ne)
    		{
    			handleException(ne, resp);
    		}
    		catch (TechnicalException te)
    		{
    			handleException(te, resp);
    		}
    	}
    	catch (Exception e)
    	{
    		throw new ServletException(e);
    	}
   }

    
/**
 * Mapping of outputFormat to mime-type 
 * TODO: Get the mapping directly from ItemExportingBean  
 * @param outputFormat
 * @return mime-type according to the outputFormat 
 */
    private String getContentType(final String outputFormat) {
		return FileFormatVO.getMimeTypeByName(outputFormat);
	}

    /**
     * Mapping of the outputType file to the correct file extension  
     * TODO: Get the mapping directly from ItemExportingBean  
     * @param outputFormat
     * @return mime-type according to the outputFormat 
     */
        private String getFileExtension(final String outputFormat) {
    		return "." + ( 
    				outputFormat.trim().equals("snippet") ? 
    						"xml" : 
    						outputFormat 
    		);
    	}
    
    
//    private String  
    
    private void handleException(final Exception e, final HttpServletResponse resp) throws Exception
    {
        PrintWriter pw = resp.getWriter();
        pw.print("Error: ");
        e.printStackTrace(pw);
        pw.close();
    	
//    	String msg = "Error: " + e.getMessage() + "\n\n"; 
//        for ( StackTraceElement ste : e.getStackTrace() )
//        {
//        	msg +=  
//        			"at "
//        			+ ste.getClassName()
//        			+ "."
//        			+ ste.getMethodName()
//        			+ "("
//        			+ ste.getLineNumber()
//        			+ ")\n"
//        			;
//        }
//        resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
    }
}
