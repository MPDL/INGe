/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;

import java.util.List;
import java.util.Map;

import de.mpg.escidoc.services.cone.util.Pair;

/**
 * Interface between the CoNE data storage and the presentation.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public interface Querier
{
    /**
     * Retrieve a list of entities matching the given search query.
     * 
     * @param model The entity type, e.g. "journals", "languages".
     * @param query The search query, one or more words.
     * @return A {@link List} of key-value pairs containing the matching results.
     * @throws Exception Any exception
     */
    public List<Pair> query(String model, String query) throws Exception;

    /**
     * Retrieve a list of objects matching the given search query and the given language.
     * 
     * @param model The object type, e.g. "journals", "languages".
     * @param query The search query, one or more words.
     * @param lang The given language in ISO-639-1 format (2 letters).
     * @return A {@link List} of key-value pairs containing the matching results.
     * @throws Exception Any exception
     */
    public List<Pair> query(String model, String query, String lang) throws Exception;

    /**
     * Retrieve a list of objects matching the given search query and the given language.
     * 
     * @param model The object type, e.g. "journals", "languages".
     * @param query The search query, one or more words.
     * @param lang The given language in ISO-639-1 format (2 letters).
     * @param limit The maximum number of results returned.
     * 
     * @return A {@link List} of key-value pairs containing the matching results.
     * @throws Exception Any exception
     */
    public List<Pair> query(String model, String query, String lang, int limit) throws Exception;

    /**
     * Retrieves details about an entity identified by the given id.
     * 
     * @param model The entity type, e.g. "journals", "languages".
     * @param id The identifier.
     * @return A {@link Map} of {@link List}s of {@link String}s containing the information about the entity. It is a
     *         list because it is possible that there are more than one objects to a given subject/predicate
     *         combination. E.g. there may be multiple alternative titles to a journal.
     * @throws Exception Any exception.
     */
    public Map<String, List<String>> details(String model, String id) throws Exception;

    /**
     * Retrieves details about an entity identified by the given id and returns the results in the given language.
     * 
     * @param model The entity type, e.g. "journals", "languages".
     * @param id The identifier.
     * @param lang The given language in ISO-639-1 format (2 letters).
     * @return A {@link Map} of {@link List}s of {@link String}s containing the information about the entity. It is a
     *         list because it is possible that there are more than one objects to a given subject/predicate
     *         combination. E.g. there may be multiple alternative titles to a journal.
     * @throws Exception Any exception.
     */
    public Map<String, List<String>> details(String model, String id, String lang) throws Exception;
}
