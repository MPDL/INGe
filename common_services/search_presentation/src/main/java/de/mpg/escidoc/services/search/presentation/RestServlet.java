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

package de.mpg.escidoc.services.search.presentation;

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

import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;

/**
 * This servlet takes an cql query, calls the search service and returns the
 * result.
 * 
 * @author vmakarenko
 * 
 */
public class RestServlet extends HttpServlet
{

    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Logging instance. */
    private static final Logger LOGGER = Logger.getLogger(RestServlet.class);

    /** EJB instance of search service. */
    @EJB
    private Search itemContainerSearch;

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
                itemContainerSearch = (Search) ctx.lookup(Search.SERVICE_NAME);
                cqlQuery = req.getParameter("cqlQuery");
                if ( !checkVal(cqlQuery) )
                {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "cqlQuery is not defined in the QueryString: "
                            + qs);
                    return;
                }

                language = req.getParameter("language");
                language = language == null ? "" : language.trim().toLowerCase();
                if (language.equals(""))
                {
                    language = "all";
                } 
                else if ( !("all".equals(language) || "en".equals(language) || "de".equals(language)) )
                {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong language: " + language);
                    return;
                }

                exportFormat = req.getParameter("exportFormat");
                exportFormat = !checkVal(exportFormat) ? "" : exportFormat.trim().toUpperCase();
                
                
                
                ProcessCitationStyles pcs = new ProcessCitationStyles();
                StructuredExport se = new StructuredExport();
                if (exportFormat.equals(""))
                {
                 // TODO: move default values to services
                	exportFormat = "ENDNOTE";
                    // if exportFormat is ENDNOTE set outputFormat forced to the
                    // txt
                } 
                else if ( !(pcs.isCitationStyle(exportFormat) || se.isStructuredFormat(exportFormat)) )
                {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong export format: " + exportFormat);
                    return;
                }

                if ( se.isStructuredFormat(exportFormat) )
                {
                    outputFormat = FileFormatVO.TEXT_NAME;
                } 
                else
                	// citation style
                {
                    outputFormat = req.getParameter("outputFormat");
                    outputFormat = !checkVal(outputFormat) ? "" : outputFormat.trim().toLowerCase();
                    // get default outputFormat if it is not defined
                    if (outputFormat.equals(""))
                    {
                        outputFormat = FileFormatVO.DEFAULT_NAME;
                    }
                    // check output format consistency
                    else if ( pcs.getMimeType(exportFormat, outputFormat)==null )
                    {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File output format: " + outputFormat
                                + " is not supported for the export format: " + exportFormat);
                        return;
                    }
                }

                String index = null;

                // transform language selector to enum
                if (language.contains("all"))
                {
                    index = "escidoc_all";
                } 
                else if (language.contains("en"))
                {
                    index = "escidoc_en";
                } 
                else if (language.contains("de"))
                {
                    index = "escidoc_de";
                } 
                else
                {
                    throw new TechnicalException("Cannot map language string to database selector.");
                }
                
                // create the query
                ExportSearchQuery query = new ExportSearchQuery(cqlQuery, index, exportFormat,
                        outputFormat );
                
                // check if sortKeys is set
                if( checkVal( req.getParameter("sortKeys") ) )
                {
                    query.setSortKeys( req.getParameter("sortKeys") );
                }
                
                // check if startRecord is set
                if( checkVal( req.getParameter("startRecord") ) )
                {
                    query.setStartRecord( req.getParameter("startRecord") );
                }
                
                // check if maximum records are set
                if( checkVal( req.getParameter("maximumRecords") ) )
                {
                    query.setMaximumRecords( req.getParameter("maximumRecords") );
                }
                
                // check if sortOrder is set
                if( checkVal( req.getParameter("sortOrder") ) )
                {
                    String sortOrder = req.getParameter("sortOrder");
                    if( sortOrder.contains("descending") )
                    {
                        query.setSortOrder(SortingOrder.DESCENDING);
                    }
                    else
                    {
                        query.setSortOrder(SortingOrder.ASCENDING);
                    }
                    
                }
                
                // query the search service
                ExportSearchResult queryResult = itemContainerSearch.searchAndExportItems(query);

                byte[] result = queryResult.getResult();

                String fileName = exportFormat + "_output" + getFileExtension(outputFormat);
                LOGGER.debug("fileName: " + fileName);
                String contentType = getContentType(outputFormat);
                resp.setContentType(contentType);
                LOGGER.debug("contentType: " + contentType);

                ServletOutputStream os = resp.getOutputStream();

                resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);

                resp.setContentLength(result.length);

                for (byte b : result)
                {
                    os.write(b);
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
     * Mapping of outputFormat to mime-type. 
     * TODO: Get the mapping directly from ItemExportingBean
     * 
     * @param outputFormat
     * @return mime-type according to the outputFormat
     */
    private String getContentType(final String outputFormat)
    {
        return FileFormatVO.getMimeTypeByName(outputFormat);
    }

    /**
     * Mapping of the outputType file to the correct file extension. 
     * TODO: Get the mapping directly from ItemExportingBean
     * 
     * @param outputFormat
     * @return mime-type according to the outputFormat
     */
    private String getFileExtension(final String outputFormat)
    {
        return "." + (outputFormat.trim().equals("snippet") ? "xml" : outputFormat);
    }

    /**
     * Take care on an incoming exception.
     * 
     * @param e
     *            exception
     * @param resp
     *            response stream
     * @throws Exception
     *             if the handling went wrong on any reason
     */
    private void handleException(final Exception e, final HttpServletResponse resp) throws Exception
    {
        PrintWriter pw = resp.getWriter();
        pw.print("Error: ");
        e.printStackTrace(pw);
        pw.close();
    }
    
    private boolean checkVal(String str) {
    	return !(str == null || str.trim().equals(""));
    }	
	    
}
