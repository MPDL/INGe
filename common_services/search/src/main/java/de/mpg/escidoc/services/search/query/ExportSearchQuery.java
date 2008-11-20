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

package de.mpg.escidoc.services.search.query;


/**
 * Search query used by the export interface.
 * 
 * @author endres
 * 
 */
public class ExportSearchQuery extends SearchQuery
{

    /** Serializable identifier. */
    private static final long serialVersionUID = 1L;

    /** Cql query. */
    private String cqlQuery = null;
    /** Export format to use. */
    private String exportFormat = null;
    /** Output format to use. */
    private String outputFormat = null;
    /** Index database selector. */
    private String indexSelector = null;
    /** Sort keys. */
    private String sortKeys;

    /**
     * Create an export search query.
     * 
     * @param cqlQuery
     *            the cql query to use
     * @param exportFormat
     *            the export format to use
     * @param outputFormat
     *            the output format to use
     */
    public ExportSearchQuery(String cqlQuery, String exportFormat, String outputFormat, String sortKeys)
    {
        this.outputFormat = outputFormat;
        this.exportFormat = exportFormat;
        this.cqlQuery = cqlQuery;
    }

    /**
     * Create an export search query.
     * 
     * @param cqlQuery
     *            the cql query to use
     * @param exportFormat
     *            the export format to use
     * @param outputFormat
     *            the output format to use
     */
    public ExportSearchQuery(String cqlQuery, String exportFormat, String outputFormat)
    {
        this.outputFormat = outputFormat;
        this.exportFormat = exportFormat;
        this.sortKeys = sortKeys;
        this.cqlQuery = cqlQuery;
    }

    /**
     * Create an export search query with a index database selector.
     * 
     * @param cqlQuery
     *            the cql query to use
     * @param exportFormat
     *            the export format to use
     * @param indexSelector
     *            the selected index selector
     * @param outputFormat
     *            the output format to use
     */
    public ExportSearchQuery(String cqlQuery, String indexSelector, String exportFormat, String outputFormat,
            String sortKeys)
    {
        this.outputFormat = outputFormat;
        this.exportFormat = exportFormat;
        this.sortKeys = sortKeys;
        this.cqlQuery = cqlQuery;
        this.indexSelector = indexSelector;
    }

    /**
     * Getter for the cql query.
     * 
     * @return cql query
     */
    public String getCqlQuery()
    {
        return cqlQuery;
    }

    /**
     * Getter for the export format.
     * 
     * @return export format
     */
    public String getExportFormat()
    {
        return exportFormat;
    }

    /**
     * Getter for the output format.
     * 
     * @return output format
     */
    public String getOutputFormat()
    {
        return outputFormat;
    }

    /**
     * Getter for the output format.
     * 
     * @return output format
     */
    public String getIndexSelector()
    {
        return indexSelector;
    }

    /**
     * Getter for the sortKeys.
     * 
     * @return output format
     */
    public String getSortKeys()
    {
        return sortKeys;
    }

    /**
     * True if an index selector is available.
     * 
     * @return true or false
     */
    public boolean hasIndexSelector()
    {
        return (this.indexSelector != null);
    }
}
