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

import java.util.ArrayList;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * @author endres
 * 
 */
public class MetadataDateSearchCriterion extends MetadataSearchCriterion
{

    /** Serial identifier. */
    private static final long serialVersionUID = 1L;

    private String toField = null;

    /**
     * @param type
     * @param searchTerm
     * @param operator
     * @throws TechnicalException
     */
    public MetadataDateSearchCriterion(ArrayList<CriterionType> types, String from, String to)
            throws TechnicalException
    {
        super(types, from);
        toField = to;
    }

    public String generateCqlQuery() throws ParseException, TechnicalException
    {

        QueryParser parserFrom = new QueryParser(getSearchTerm(),
                booleanOperatorToString(BooleanOperator.GREATER_THAN_EQUALS));
        QueryParser parserTo = new QueryParser(this.toField, booleanOperatorToString(BooleanOperator.LESS_THAN_EQUALS));
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ( ");
        for (int i = 0; i < getSearchIndexes().size(); i++)
        {
            parserFrom.addCQLIndex(getSearchIndexes().get(i));
            parserTo.addCQLIndex(getSearchIndexes().get(i));
        }
        buffer.append(" ( " + parserFrom.parse() + " " + CQL_AND + " " + parserTo.parse() + " ) ");
        buffer.append(" ) ");
        return buffer.toString();
    }
}
