/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development
 * and Distribution License, Version 1.0 only (the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License. When distributing Covered Code, include this CDDL HEADER in
 * each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.cone;

import java.util.List;
import java.util.Map;

/**
 * Interface between the CoNE data storage and the presentation.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public interface Querier {

  enum ModeType
  {
    FAST, FULL
  }


  /**
   * Retrieve a list of entities matching the given search query.
   *
   * @param modelName The entity type, e.g. "journals", "languages".
   * @param searchString The search query, one or more words.
   * @return A {@link List} of key-value pairs containing the matching results.
   * @throws Exception Any exception
   */
  List<? extends Describable> query(String modelName, String searchString, ModeType modeType)
      throws ConeException;

  /**
   * Retrieve a list of objects matching the given search query and the given language.
   *
   * @param modelName The object type, e.g. "journals", "languages".
   * @param searchString The search query, one or more words.
   * @param language The given language in ISO-639-1 format (2 letters).
   * @return A {@link List} of key-value pairs containing the matching results.
   * @throws Exception Any exception
   */
  List<? extends Describable> query(String modelName, String searchString, String language, ModeType modeType) throws ConeException;

  /**
   * Retrieve a list of objects matching the given search query and the given language.
   *
   * @param modelName The object type, e.g. "journals", "languages".
   * @param searchString The search query, one or more words.
   * @param language The given language in ISO-639-1 format (2 letters).
   * @param limit The maximum number of results returned.
   *
   * @return A {@link List} of key-value pairs containing the matching results.
   * @throws Exception Any exception
   */
  List<? extends Describable> query(String modelName, String searchString, String language, ModeType modeType, int limit)
      throws ConeException;

  /**
   * Retrieve a list of objects matching the given search fields and the given language.
   *
   * @param modelName The object type, e.g. "journals", "languages".
   * @param searchFields The search fields, key is the predicate and value is the search term.
   * @param language The given language in ISO-639-1 format (2 letters).
   * @param limit The maximum number of results returned.
   *
   * @return A {@link List} of key-value pairs containing the matching results.
   * @throws Exception Any exception
   */
  List<? extends Describable> query(String modelName, Pair<String>[] searchFields, String language, ModeType modeType, int limit)
      throws ConeException;

  /**
   * Retrieves details about an entity identified by the given id.
   *
   * @param modelName The entity type, e.g. "journals", "languages".
   * @param id The identifier.
   * @return A {@link Map} of {@link List}s of {@link LocalizedString}s containing the information
   *         about the entity. It is a list because it is possible that there are more than one
   *         objects to a given subject/predicate combination. E.g. there may be multiple
   *         alternative titles to a journal.
   * @throws Exception Any exception.
   */
  TreeFragment details(String modelName, String id) throws ConeException;

  /**
   * Retrieves details about an entity identified by the given id and returns the results in the
   * given language.
   *
   * @param modelName The entity type, e.g. "journals", "languages".
   * @param id The identifier.
   * @param language The given language in ISO-639-1 format (2 letters).
   * @return A {@link Map} of {@link List}s of {@link LocalizedString}s containing the information
   *         about the entity. It is a list because it is possible that there are more than one
   *         objects to a given subject/predicate combination. E.g. there may be multiple
   *         alternative titles to a journal.
   * @throws Exception Any exception.
   */
  TreeFragment details(String modelName, String id, String language) throws ConeException;

  /**
   * Inserts a map of values into the database.
   *
   * @param modelName The entity type, e.g. "journals", "languages".
   * @param id The identifier.
   * @param values A {@link Map} of {@link List}s of {@link LocalizedString}s containing the
   *        information about the entity. It is a list because it is possible that there are more
   *        than one objects to a given subject/predicate combination. E.g. there may be multiple
   *        alternative titles to a journal.
   * @throws Exception Any exception.
   */
  void create(String modelName, String id, TreeFragment values) throws ConeException;

  /**
   * Deletes all entries from the database that fit the given model and id.
   *
   * @param modelName The entity type, e.g. "journals", "languages".
   * @param id The identifier.
   * @throws Exception Any exception.
   */
  void delete(String modelName, String id) throws ConeException;

  String createUniqueIdentifier(String model) throws ConeException;

  List<String> getAllIds(String modelName) throws ConeException;

  List<String> getAllIds(String modelName, int hits) throws ConeException;

  void release() throws ConeException;

  void setLoggedIn(boolean loggedIn);

  boolean getLoggedIn();

  void cleanup() throws ConeException;
}
