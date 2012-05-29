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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.search.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * A date criteria is used to search for dates. A date criterion consists of a type of date indexes
 * and a from and to search query date.
 * @author endres
 * 
 */
public class MetadataDateSearchCriterion extends MetadataSearchCriterion
{    
    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    
    private String majorSearchTerm = null;

    /**
     * Constructor with date types and from and to search query.
     * @param types  date index types
     * @param from  date for beginning of date search
     * @param to  date for end of date search
     * @throws TechnicalException  if creation of object fails
     */
    public MetadataDateSearchCriterion(ArrayList<CriterionType> types, String from, String to)
        throws TechnicalException
    {
        super(types, from);
        if (from == null && to == null) 
        {
            throw new TechnicalException("Invalid query. Either 'from' or 'to' should have a value.");
        }
        majorSearchTerm = to;
    }
    
    /**
     * Constructor with date types,from and to search query and a logical operator.
     * @param types  date index types
     * @param from  date for beginning of date search
     * @param to  date for end of date search
     * @param operator  logical operator to connect this criteria to the previous one
     * @throws TechnicalException  if creation of object fails
     */
    public MetadataDateSearchCriterion(ArrayList<CriterionType> types, String from, String to, LogicalOperator operator)
        throws TechnicalException 
    {
        super(types, from, operator);
        majorSearchTerm = to;
    }

    /**
     * {@inheritDoc}
     */
    public String generateCqlQuery() throws ParseException, TechnicalException
    {
       return composeCqlFragments(getMinorSearchTerm(), getMajorSearchTerm());
    }
    
    private String composeCqlFragments(String minor, String major) throws ParseException, TechnicalException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ( ");
        for (int i = 0; i < getSearchIndexes().size(); i++)
        {
            if (i == (getSearchIndexes().size() - 1))
            {
               
                buffer.append(createCqlFragment(getSearchIndexes().get(i), minor, major));
            }
            else 
            {
                buffer.append(createCqlFragment(getSearchIndexes().get(i), minor, major));
                buffer.append(" " + CQL_OR + " ");
            }
        }
        buffer.append(" ) ");
        buffer.append(getCqlQueryFromSubCriteria());
        return buffer.toString();
    }
    
    private String createCqlFragment(String index, String minor, String major) 
        throws ParseException, TechnicalException
    {
        String fromQuery = null;
        String toQuery = null;
        if (minor != null)
        {
            minor = normalizeFromQuery(minor);
            QueryParser parserFrom = new QueryParser(minor,
                    booleanOperatorToString(BooleanOperator.GREATER_THAN_EQUALS));
            parserFrom.addCQLIndex(index);
            fromQuery = parserFrom.parse();
        }
        if (major != null)
        {
            String[] majorParts = normalizeToQuery(major);
            QueryParser parserTo = new QueryParser(majorParts[0], 
                    booleanOperatorToString(BooleanOperator.LESS_THAN_EQUALS));
            parserTo.addCQLIndex(index);
            toQuery = parserTo.parse();
            for (int i = 1; i < majorParts.length; i++)
            {
                QueryParser parserNotTo = new QueryParser(majorParts[i], 
                        booleanOperatorToString(BooleanOperator.EQUALS));
                parserNotTo.addCQLIndex(index);
                toQuery += " " + CQL_NOT + " ( " + parserNotTo.parse() + " ) ";
            }
        }
        
        StringBuffer buffer = new StringBuffer();
        
        if (fromQuery == null) 
        {
            buffer.append(" ( " + toQuery + " ) ");
        }
        else if (toQuery == null)
            
        {
            buffer.append(" ( " + fromQuery + " ) ");
        }
        else
        {
            buffer.append(" ( " + fromQuery + " " + CQL_AND + " ( " + toQuery + " ) ) ");
        }
        return buffer.toString();
    }
    
    public String normalizeFromQuery(String fromQuery)
    {
        if (fromQuery == null)
        {
            return null;
        }
        else if (fromQuery.matches("\\d\\d\\d\\d"))
        {
            return fromQuery;
        }
        else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d"))
        {
            String[] parts = fromQuery.split("-");
            if ("01".equals(parts[1]))
            {
                return parts[0];
            }
            else
            {
                return fromQuery;
            }
        }
        else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d"))
        {
            String[] parts = fromQuery.split("-");
            if ("01".equals(parts[2]))
            {
                if ("01".equals(parts[1]))
                {
                    return parts[0];
                }
                else
                {
                    return parts[0] + "-" + parts[1];
                }
            }
            else
            {
                return fromQuery;
            }
        }
        else
        {
            return fromQuery;
        }
    }
    
    public String[] normalizeToQuery(String toQuery)
    {
        if (toQuery == null)
        {
            return null;
        }
        else if (toQuery.matches("\\d\\d\\d\\d"))
        {
            return new String[]{toQuery + "-12-31"};
        }
        else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d"))
        {
            String[] parts = toQuery.split("-");
            if ("12".equals(parts[1]))
            {
                return new String[]{toQuery + "-31"};
            }
            else
            {
                return new String[]{toQuery + "-31", parts[0]};
            }
        }
        else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d"))
        {
            String[] parts = toQuery.split("-");
            // Get last day of month
            if ("31".equals(parts[2]) && "12".equals(parts[1]))
            {
                return new String[]{toQuery};
            }
            else
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                int maximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (Integer.parseInt(parts[2]) == maximumDay)
                {
                    return new String[]{toQuery, parts[0]};
                }
                else
                {
                    return new String[]{toQuery, parts[0], parts[0] + "-" + parts[1]};
                }
            }
        }
        else
        {
            return new String[]{toQuery};
        }
    }

    private String getMajorSearchTerm() {
        return this.majorSearchTerm;
    }
    
    private String getMinorSearchTerm() {
        return getSearchTerm();
    }
}
