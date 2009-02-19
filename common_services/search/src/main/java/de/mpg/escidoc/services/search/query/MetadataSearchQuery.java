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

import java.io.IOException;
import java.util.ArrayList;

import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.ModifierSet;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * A search query with a set of metadata criteria. The criteria can be combined
 * with logical operators to each other. The first criteria should have no
 * logical operator. The next criteria should define a logical operator to
 * connect to the criteria before.<br/> Per default a search query consists of
 * two types of criteria. A list of contentTypes and a list of other search
 * criteria.
 * 
 * @author endres
 * 
 */
public class MetadataSearchQuery extends SearchQuery
{
    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** List of search criteria. */
    private ArrayList<MetadataSearchCriterion> searchCriteria = null;
    /** List of content types to look for. */
    private ArrayList<MetadataSearchCriterion> contentTypes = null;

    /**
     * Creates a query with a list of content types.
     * 
     * @param contentTypes
     *            list of content types
     * @throws TechnicalException
     *             if the object cannot be instantiated 
     */
    public MetadataSearchQuery(ArrayList<String> contentTypes) throws TechnicalException
    {
        this.contentTypes = new ArrayList<MetadataSearchCriterion>();
        addContentTypeCriterions(contentTypes);
        this.searchCriteria = new ArrayList<MetadataSearchCriterion>();
    }
    
    /**
     * Creates a query with a list of content types and a list of search
     * criteria.
     * 
     * @param contentTypes
     *            list of content types
     * @param criteria
     *            list of criteria
     * @throws TechnicalException
     *             if the object cannot be instantiated
     */
    public MetadataSearchQuery(ArrayList<String> contentTypes, ArrayList<MetadataSearchCriterion> criteria)
        throws TechnicalException
    {
        this.contentTypes = new ArrayList<MetadataSearchCriterion>();
        addContentTypeCriterions(contentTypes);
        this.searchCriteria = criteria;
    }

    /**
     * Adds a criterion.
     * 
     * @param criterion
     *            criterion to add
     */
    public void addCriterion(MetadataSearchCriterion criterion)
    {
        this.searchCriteria.add(criterion);
    }

    /**
     * Returns a cql root node with the whole cql query in the tree.
     * 
     * @return root node of cql query tree
     * @throws CQLParseException
     *             if building up of the tree fails
     * @throws IOException
     *             if an io error occurs
     * @throws ParseException
     *             if the parsing from the search terms fails
     * @throws TechnicalException
     *             if an internal error occurs
     */
    public CQLNode getCqlNode() throws CQLParseException, IOException, ParseException, TechnicalException
    {

        CQLNode node = null;
        if (searchCriteria.size() != 0)
        {
            node = searchCriteria.get(0).generateCqlTree();
        }
        // first add the nodes from the list
        for (int i = 1; i < searchCriteria.size(); i++)
        {
            node = generateNodeWithCriterion(node, searchCriteria.get(i));
        }
        // then add the content type nodes
        for (int i = 0; i < contentTypes.size(); i++)
        {
            node = generateNodeWithCriterion(node, contentTypes.get(i));
        }
        return node;
    }
    
    /**
     * Checks if a set of criteria can be transformed into a cql query. 
     * If a query has more than one criteria, the following criteria must have a 
     * logical operator, otherwise no valid cql query can be created.
     * @return true if query is valid, false if not
     */
    private boolean isQueryValid() 
    {
        for (int i = 1; i < searchCriteria.size(); i++)
        {
            if ((searchCriteria.get(i).getLogicalOperator() == null)
                    || searchCriteria.get(i).getLogicalOperator() == LogicalOperator.UNSET)
            {
                // query is invalid
                return false;
            }
        }
        // query is valid
        return true;
    }

    /**
     * Get the cql query as string.
     * 
     * @return cql query string
     * @throws ParseException
     *             if parsing of the searchterms fails
     * @throws TechnicalException
     *             if an internal error occurs
     */
    public String getCqlString() throws ParseException, TechnicalException
    {
        if (!isQueryValid()) 
        {
            throw new TechnicalException("The search query is invalid, missing logical operator at criteria");
        }
        
        StringBuffer buffer = new StringBuffer();
        
        // parenthesis to separate the criteria from the content type
        buffer.append("(");
        
        if (searchCriteria.size() != 0)
        {
            buffer.append(searchCriteria.get(0).generateCqlQuery());
        }
        // first add the nodes from the list
        for (int i = 1; i < searchCriteria.size(); i++)
        {

            buffer.append(" " + searchCriteria.get(i).getLogicalOperatorAsString() + " ");
            buffer.append(searchCriteria.get(i).generateCqlQuery());
        }
        buffer.append(")");
        
        // then add the content type nodes
        for (int i = 0; i < contentTypes.size(); i++)
        {
            buffer.append(" " + contentTypes.get(i).getLogicalOperatorAsString() + " ");
            buffer.append(contentTypes.get(i).generateCqlQuery());
        }
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getCqlQuery() throws CQLParseException, IOException, ParseException, TechnicalException
    {
        // build the query with a string buffer
        return getCqlString();
        // build the query traversing a cql node tree
        // return getCqlNode().toCQL();
    }

    /**
     * Generate a three node tree with a criterion and a existing node. The
     * existing node will be attached to a newly created node and a new root
     * node above the two will be returned.
     * 
     * @param node
     *            node to be attached to
     * @param criterion
     *            criterion for the newly created node
     * @return root node of the two node tree
     * @throws CQLParseException
     *             if the tree building fails
     * @throws IOException
     *             if an io error occurs
     * @throws ParseException
     *             if the search terms cannot be parsed
     * @throws TechnicalException
     *             if an internal error occurs
     */
    private CQLNode generateNodeWithCriterion(CQLNode node, MetadataSearchCriterion criterion)
        throws CQLParseException, IOException, ParseException, TechnicalException
    {
        CQLNode newRoot = null;
        switch (criterion.getLogicalOperator())
        {
            case AND:
                newRoot = new CQLAndNode(node, criterion.generateCqlTree(), new ModifierSet(criterion
                        .getLogicalOperatorAsString()));
                break;
            case OR:
                newRoot = new CQLOrNode(node, criterion.generateCqlTree(), new ModifierSet(criterion
                        .getLogicalOperatorAsString()));
                break;
            case NOT:
                newRoot = new CQLNotNode(node, criterion.generateCqlTree(), new ModifierSet(criterion
                        .getLogicalOperatorAsString()));
                break;
            default:
                throw new TechnicalException("No logical operator specified. Cannot bind to node.");
        }
        return newRoot;
    }

    /**
     * Add content type criteria to the query.
     * 
     * @param contentT
     *            content type criteria
     * @throws TechnicalException
     *             if criteria type is not supported
     */
    private void addContentTypeCriterions(ArrayList<String> contentT) throws TechnicalException
    {
        for (int i = 0; i < contentT.size(); i++)
        {
            this.contentTypes.add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.CONTENT_TYPE,
                    contentT.get(i), MetadataSearchCriterion.LogicalOperator.AND));
        }
    }

}
