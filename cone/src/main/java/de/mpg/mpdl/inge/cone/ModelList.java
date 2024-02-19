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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * A SAX parser that reads in the servieces.xml configuration file.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ModelList {

  public enum Event
  {
    ONLOAD,
    ONSAVE
  }

  public enum Type
  {
    STRING,
    XML
  }

  private static ModelList instance = null;
  private static final Logger logger = LogManager.getLogger(ModelList.class);
  private Set<Model> list = new HashSet<>();
  private Map<String, String> defaultNamepaces = new HashMap<>();
  private Map<String, Set<String>> formatMimetypes = new HashMap<>();

  private ModelList() throws ConeException {
    try {
      InputStream in = ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH),
          ModelList.class.getClassLoader());
      ServiceListHandler listHandler = new ServiceListHandler();
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(in, listHandler);
      list = listHandler.getList();
      logger.debug("Length: " + list.size());
      for (Model model : list) {
        if (logger.isDebugEnabled()) {
          logger.debug("Model:" + model.getName());
        }
      }
    } catch (IOException | SAXException | ParserConfigurationException ex) {
      throw new ConeException(ex);
    }
  }

  /**
   * Returns the singleton.
   *
   * @throws Exception Any exception.
   * @return The singleton
   * @throws ConeException
   */
  public static synchronized ModelList getInstance() throws ConeException {
    if (instance == null) {
      instance = new ModelList();
    }
    return instance;
  }

  /**
   * Returns the singleton.
   *
   * @throws Exception Any exception.
   * @return The singleton
   */
  public static void reload() throws Exception {
    instance = new ModelList();
  }

  public Set<Model> getList() {
    return list;
  }

  /**
   * Find a model by its alias.
   *
   * @param alias The String to look for.
   * @return The first {@link Model} in the list using the given alias.
   * @throws ConeException
   */
  public Model getModelByAlias(String alias) throws ConeException {
    for (Model model : getList()) {
      if (model.getName().equals(alias) || model.getAliases().contains(alias)) {
        return model;
      }
    }

    throw new ConeException("Model " + alias + " not found");
  }

  public Map<String, String> getDefaultNamepaces() {
    return defaultNamepaces;
  }

  public void setDefaultNamepaces(Map<String, String> defaultNamepaces) {
    this.defaultNamepaces = defaultNamepaces;
  }

  public Map<String, Set<String>> getFormatMimetypes() {
    return formatMimetypes;
  }

  public void setFormatMimetypes(Map<String, Set<String>> formatMimetypes) {
    this.formatMimetypes = formatMimetypes;
  }

  /**
   * SAX handler.
   *
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  private class ServiceListHandler extends de.mpg.mpdl.inge.util.ShortContentHandler {
    private final Set<Model> list = new LinkedHashSet<>();
    private Model currentService = null;
    private final Stack<List<Predicate>> predicateStack = new Stack<>();
    private Set<String> currentFormat = null;

    @Override
    public void content(String uri, String localName, String name, String content) {
      if ("models/model/name".equals(localStack.toString())) {
        currentService.setName(content.trim());
      } else if ("models/model/description".equals(localStack.toString())) {
        currentService.setDescription(content.trim());
      } else if ("models/model/aliases/alias".equals(localStack.toString())) {
        currentService.getAliases().add(content.trim());
      } else if ("models/model/open".equals(localStack.toString())) {
        currentService.setOpen(Boolean.parseBoolean(content.trim()));
      } else if ("models/model/rdf-about-tag".equals(localStack.toString())) {
        String[] parts = content.split(":");
        if (parts.length == 2) {
          String ns = getNamespaces().get(parts[0]);
          currentService.setRdfAboutTag(new QName(ns, parts[1], parts[0]));
        } else if (parts.length == 1) {
          currentService.setRdfAboutTag(new QName(parts[0]));
        } else {

        }

      } else if ("models/model/primary-identifier".equals(localStack.toString())) {
        currentService.setIdentifier(content.trim().isEmpty() ? null : content.trim());
      } else if ("models/model/results/result/result-pattern".equals(localStack.toString())) {
        int resultSize = currentService.getResults().size();
        currentService.getResults().get(resultSize - 1).setResultPattern(content.trim());
      } else if ("models/model/results/result/sort-pattern".equals(localStack.toString())) {
        int resultSize = currentService.getResults().size();
        currentService.getResults().get(resultSize - 1).setSortPattern(content.trim());
      } else if ("models/model/results/result/type".equals(localStack.toString())) {
        int resultSize = currentService.getResults().size();
        currentService.getResults().get(resultSize - 1).setType(content.trim());
      }

    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      super.startElement(uri, localName, name, attributes);
      if ("models/model".equals(localStack.toString())) {
        currentService = new Model();
      } else if ("models/model/predicates".equals(localStack.toString())) {
        this.predicateStack.push(currentService.getPredicates());
      } else if ("models/model/results/result".equals(localStack.toString())) {
        currentService.getResults().add(new ModelResult());
      } else if ("predicate".equals(name)) {
        if (attributes.getValue("value") == null) {
          throw new SAXException(
              "Predicate value for " + attributes.getValue("name") + " in model " + currentService.getName() + " must not be null");
        }

        // if parent is "predicates" (and therefore not another
        // sub-predicate) and value is same as
        // primary identifier
        else if (localStack.size() > 1 && "predicates".equals(localStack.get(localStack.size() - 2))
            && attributes.getValue("value").equals(currentService.getIdentifier())) {
          if (!Boolean.parseBoolean(attributes.getValue("mandatory"))) {
            throw new SAXException(
                "Identifier predicate " + attributes.getValue("value") + " in model " + currentService.getName() + " must be mandatory");
          } else if (Boolean.parseBoolean(attributes.getValue("multiple"))) {
            throw new SAXException(
                "Identifier predicate " + attributes.getValue("value") + " in model " + currentService.getName() + " must not be multiple");
          } else if (Boolean.parseBoolean(attributes.getValue("localized"))) {
            throw new SAXException("Identifier predicate " + attributes.getValue("value") + " in model " + currentService.getName()
                + " must not be localized");
          }
        }

        Predicate predicate = new Predicate(attributes.getValue("value"), attributes.getValue("name"),
            Boolean.parseBoolean(attributes.getValue("multiple")), Boolean.parseBoolean(attributes.getValue("mandatory")),
            Boolean.parseBoolean(attributes.getValue("localized")), Boolean.parseBoolean(attributes.getValue("generateObject")),
            (attributes.getValue("includeResource") == null ? true : Boolean.parseBoolean(attributes.getValue("includeResource"))),
            Boolean.parseBoolean(attributes.getValue("searchable")), Boolean.parseBoolean(attributes.getValue("restricted")),
            Boolean.parseBoolean(attributes.getValue("overwrite")), Boolean.parseBoolean(attributes.getValue("shouldBeUnique")),
            (attributes.getValue("modify") == null ? true : Boolean.parseBoolean(attributes.getValue("modify"))),
            attributes.getValue("event"), attributes.getValue("resourceModel"), attributes.getValue("default"),
            attributes.getValue("suggest-url"), attributes.getValue("type"));
        this.predicateStack.peek().add(predicate);
        this.predicateStack.push(predicate.getPredicates());
      } else if ("models/model/primary-identifier".equals(localStack.toString())) {
        currentService.setGenerateIdentifier(Boolean.parseBoolean(attributes.getValue("generate-cone-id")));
        currentService
            .setIdentifierPrefix((attributes.getValue("identifier-prefix") == null ? "" : attributes.getValue("identifier-prefix")));
        currentService.setSubjectPrefix((attributes.getValue("subject-prefix") == null ? "" : attributes.getValue("subject-prefix")));
        currentService.setControlled(Boolean.parseBoolean(attributes.getValue("control")));
      } else if ("models/config/default-namespace".equals(localStack.toString())) {
        defaultNamepaces.put(attributes.getValue("uri"), attributes.getValue("prefix"));
      } else if ("models/formats/format".equals(localStack.toString())) {
        currentFormat = new HashSet<>();
        formatMimetypes.put(attributes.getValue("id"), currentFormat);
      } else if ("models/formats/format/mime-type".equals(localStack.toString())) {
        currentFormat.add(attributes.getValue("id"));
      }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      if ("models/model".equals(localStack.toString())) {
        list.add(currentService);
      } else if ("predicate".equals(name)) {
        this.predicateStack.pop();
      }
      super.endElement(uri, localName, name);
    }

    @Override
    public void endDocument() throws SAXException {
      super.endDocument();

      Stack<String> modelStack = new Stack<>();

      for (Model model : list) {
        modelStack.push(model.getName());
        try {
          setI18nFlags(model, model.getPredicates(), modelStack);
        } catch (ConeException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        modelStack.pop();
      }
    }

    /**
     * @param model
     * @throws ConeException
     */
    private void setI18nFlags(Model model, List<Predicate> predicates, Stack<String> modelStack) throws SAXException, ConeException {
      for (Predicate predicate : predicates) {
        for (ModelResult result : model.getResults()) {
          if (predicate.isLocalized() && result.getResultPattern().contains("<" + predicate.getId() + ">")) {
            model.setLocalizedResultPattern(true);
          } else if (predicate.isResource()
              && isSubResourceLocalized(predicate.getId() + "|", predicate.getResourceModel(), result.getResultPattern(), new Stack<>())) {
            model.setLocalizedResultPattern(true);
          } else if (!predicate.isLocalized() && result.getResultPattern().contains("<" + predicate.getId() + ">")) {
            model.setGlobalResultPattern(true);
          }
        }

        if (predicate.isSearchable()) {
          model.setLocalizedMatches(true);
          model.setGlobalMatches(true);
          /*
           * if (predicate.isLocalized()) { model.setLocalizedMatches(true); } else {
           * model.setGlobalMatches(true); }
           */
        }

        if (predicate.getPredicates() != null && !predicate.getPredicates().isEmpty()) {
          setI18nFlags(model, predicate.getPredicates(), modelStack);
        } else if (predicate.isResource()) {
          try {
            for (Model nextModel : list) {
              if (nextModel.getName().equals(predicate.getResourceModel())) {
                if (!(modelStack.contains(nextModel.getName()))) {
                  modelStack.push(nextModel.getName());
                  setI18nFlags(model, nextModel.getPredicates(), modelStack);
                  modelStack.pop();
                }
                break;
              }
            }
          } catch (Exception e) {
            throw new ConeException(e);
          }
        }
      }
    }

    private boolean isSubResourceLocalized(String prefix, String modelName, String pattern, Stack<String> modelStack) throws SAXException {
      Model model = null;
      try {
        for (Model existingModel : list) {
          if (modelName.equals(existingModel.getName())) {
            model = existingModel;
            break;
          }
        }
      } catch (Exception e) {
        throw new SAXException("Error getting sub model '" + modelName + "'", e);
      }

      for (Predicate predicate : model.getPredicates()) {
        if (predicate.isLocalized() && pattern.contains("<" + prefix + predicate.getId() + ">")) {
          return true;
        } else if (predicate.isResource() && !modelStack.contains(modelName)) {
          modelStack.push(modelName);
          return isSubResourceLocalized(prefix + predicate.getId() + "|", predicate.getResourceModel(), pattern, modelStack);
        }
      }
      return false;
    }

    public Set<Model> getList() {
      return list;
    }

  }

  /**
   * A bean holding data of a CoNE service.
   *
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  public class Model {
    final QName rdfDescriptionTag = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf");

    private String name;
    private String description;
    private List<String> aliases = new ArrayList<>();
    private List<Predicate> predicates = new ArrayList<>();
    private String identifier;
    private String identifierPrefix;
    private String subjectPrefix;
    private boolean generateIdentifier;
    private boolean controlled;
    private List<ModelResult> results = new ArrayList<>();
    private boolean localizedResultPattern;
    private boolean globalResultPattern;
    private boolean localizedMatches;
    private boolean globalMatches;
    private boolean open;
    private QName rdfAboutTag = rdfDescriptionTag;

    /**
     * Default constructor.
     */
    public Model() {}

    /**
     * Constructor by name.
     *
     * @param name The service name
     */
    public Model(String name) {
      this.name = name;
      this.open = false;
    }

    /**
     * Constructor by name and description.
     *
     * @param name The service name
     * @param description The description
     */
    public Model(String name, String description) {
      this.name = name;
      this.description = description;
      this.open = false;
    }

    /**
     * Constructor by name, description and aliases.
     *
     * @param name The service name
     * @param description The description
     * @param aliases The {@link List} of aliases.
     */
    public Model(String name, String description, List<String> aliases) {
      this.name = name;
      this.description = description;
      this.aliases = aliases;
      this.open = false;
    }

    /**
     * Constructor by name, description and aliases.
     *
     * @param name The service name
     * @param description The description
     * @param aliases The {@link List} of aliases
     * @param predicates The {@link List} of allowed predicates
     */
    public Model(String name, String description, List<String> aliases, List<Predicate> predicates) {
      this.name = name;
      this.description = description;
      this.aliases = aliases;
      this.predicates = predicates;
      this.open = false;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public List<String> getAliases() {
      return aliases;
    }

    public void setAliases(List<String> aliases) {
      this.aliases = aliases;
    }

    public List<Predicate> getPredicates() {
      return predicates;
    }

    public void setPredicates(List<Predicate> predicates) {
      this.predicates = predicates;
    }

    public String getIdentifier() {
      return identifier;
    }

    public void setIdentifier(String identifier) {
      this.identifier = identifier;
    }

    public boolean isGenerateIdentifier() {
      return generateIdentifier;
    }

    public void setGenerateIdentifier(boolean generateIdentifier) {
      this.generateIdentifier = generateIdentifier;
    }

    public String getIdentifierPrefix() {
      return identifierPrefix;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
      this.identifierPrefix = identifierPrefix;
    }

    public boolean isLocalizedResultPattern() {
      return localizedResultPattern;
    }

    public void setLocalizedResultPattern(boolean localizedResultPattern) {
      this.localizedResultPattern = localizedResultPattern;
    }

    public boolean isGlobalResultPattern() {
      return globalResultPattern;
    }

    public void setGlobalResultPattern(boolean globalResultPattern) {
      this.globalResultPattern = globalResultPattern;
    }

    public boolean isLocalizedMatches() {
      return localizedMatches;
    }

    public void setLocalizedMatches(boolean localizedMatches) {
      this.localizedMatches = localizedMatches;
    }

    public boolean isGlobalMatches() {
      return globalMatches;
    }

    public void setGlobalMatches(boolean globalMatches) {
      this.globalMatches = globalMatches;
    }

    public String getSubjectPrefix() {
      return subjectPrefix;
    }

    public void setSubjectPrefix(String subjectPrefix) {
      this.subjectPrefix = subjectPrefix;
    }

    public boolean isControlled() {
      return controlled;
    }

    public void setControlled(boolean controlled) {
      this.controlled = controlled;
    }

    public void setOpen(boolean open) {
      this.open = open;
    }

    public boolean isOpen() {
      return this.open;
    }

    /**
     * Find a predicate by id.
     *
     * @param predicateId the id of the predicate. If the id is null, a {@link NullPointerException}
     *        is thrown.
     *
     * @return null if there is no predicate with the given id, the according predicate otherwise.
     * @throws ConeException
     */
    public Predicate getPredicate(String predicateId) throws ConeException {
      if (predicateId == null) {
        throw new ConeException("Empty predicate name");
      }
      for (Predicate predicate : getPredicates()) {
        if (predicateId.equals(predicate.getId())) {
          return predicate;
        }
      }
      return null;
    }

    /**
     * Compares to other objects.
     *
     * @param object The object this object is compared to
     * @return true, if the other object is a with the same name.
     */
    @Override
    public boolean equals(Object object) {
      if (object == null) {
        return false;
      }
      if (object instanceof Model) {
        if (((Model) object).name == null) {
          return (this.name == null);
        } else {
          return (((Model) object).name.equals(this.name));
        }
      }
      return false;

    }

    /**
     * Returns the hashCode of the service name. This is needed for using {@link HashSet}s
     * correctly.
     *
     * @return The hashCode
     */
    @Override
    public int hashCode() {
      return (this.name == null ? 0 : this.name.hashCode());
    }

    public List<ModelResult> getResults() {
      return results;
    }

    public void setResults(List<ModelResult> results) {
      this.results = results;
    }

    public QName getRdfAboutTag() {
      return rdfAboutTag;
    }

    public void setRdfAboutTag(QName rdfImportTag) {
      this.rdfAboutTag = rdfImportTag;
    }
  }

  /**
   * Inner VO class to define the data structure inside a model.
   *
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   *
   */
  public class Predicate {

    private String name;
    private String id;
    private boolean multiple;
    private boolean mandatory;
    private boolean localized;
    private final List<Predicate> predicates = new ArrayList<>();
    private boolean generateObject = false;
    private boolean includeResource = true;
    private String resourceModel;
    private boolean searchable;
    private boolean restricted;
    private boolean overwrite;
    private boolean shouldBeUnique;
    private boolean modify;
    private Event event;
    private String defaultValue;
    private String suggestUrl;
    private Type type;

    /**
     * Constructor using all fields.
     *
     * @param id The value of the predicate.
     * @param multiple Flag that indicates that this predicate might occur more than once.
     * @param mandatory Flag that indicates the this predicate must occur at least once.
     * @param localized Flag that indicates that this predicate might occur in different languages.
     * @param name The label of this predicate.
     * @param generateObject Flag indicating that this predicate has sub-predicates that are not
     *        defined by a certain identifier.
     * @param includeResource If this predicate is a resource (link to another model), this flag
     *        indicates if the linked item should be included in the details view. Default: true
     * @param searchable Flag that indicates that this predicate shall be found when querying this
     *        model.
     * @param restricted Flag that indicates whether the value of this predicate should be displayed
     *        only for logged-in users
     * @param overwrite Flag indicating if the value of this predicate should be overwritten when
     *        modifying. Makes only sense for event-driven predicates, e.g. modification date
     * @param shouldBeUnique Flag indicating whether the value of this predicate must be unique
     *        within this model
     * @param modify Flag indicating whether the value of this predicate can be modified in the edit
     *        mask. Default: true
     * @param eventString Indicates the type of event on which the value of this predicate must be
     *        changed. See {@link Event}
     * @param resourceModel Flag indicating if the object is an identifier to a stand-alone
     *        resourceModel. If so, this resourceModel won't be editable, but linked. Furthermore,
     *        it will not be deleted in case the current subject is deleted.
     * @param defaultValue The default value of this predicate when nothing is set in the edit mask
     * @param suggestUrl A link to an url that provides autosuggest values for the field in the edit
     *        mask
     * @param typeString A type indictator for the value of this predicate. See {@link Type}
     */
    public Predicate(String id, String name, boolean multiple, boolean mandatory, boolean localized, boolean generateObject,
        boolean includeResource, boolean searchable, boolean restricted, boolean overwrite, boolean shouldBeUnique, boolean modify,
        String eventString, String resourceModel, String defaultValue, String suggestUrl, String typeString) {
      this.id = id;

      this.multiple = multiple;
      this.mandatory = mandatory;
      this.localized = localized;
      this.name = name;
      this.generateObject = generateObject;
      this.includeResource = includeResource;
      this.searchable = searchable;
      this.restricted = restricted;
      this.overwrite = overwrite;
      this.shouldBeUnique = shouldBeUnique;
      this.modify = modify;
      if (eventString != null && !eventString.isEmpty()) {
        this.event = Event.valueOf(eventString.toUpperCase());
      }
      this.resourceModel = resourceModel;
      this.defaultValue = defaultValue;
      this.suggestUrl = suggestUrl;

      if (typeString != null && !typeString.isEmpty()) {
        this.setType(Type.valueOf(typeString.toUpperCase()));
      }
    }

    public boolean isResource() {
      return (resourceModel != null);
    }

    /**
     * Find a sub predicate by id.
     *
     * @param predicateId the id of the sub predicate. If the id is null, a
     *        {@link NullPointerException} is thrown.
     *
     * @return null if there is no sub predicate with the given id, the according sub predicate
     *         otherwise.
     * @throws ConeException
     */
    public Predicate getPredicate(String predicateId) throws ConeException {
      if (predicateId == null) {
        throw new ConeException("Empty predicate name");
      }
      for (Predicate predicate : getPredicates()) {
        if (predicateId.equals(predicate.getId())) {
          return predicate;
        }
      }
      return null;
    }

    public String getDefault(HttpServletRequest request) throws ConeException {
      if (this.defaultValue == null) {
        return null;
      } else if (this.defaultValue.startsWith("'") && this.defaultValue.endsWith("'")) {
        return this.defaultValue.substring(1, this.defaultValue.length() - 1);
      } else {
        try {
          int index = this.defaultValue.lastIndexOf(".");
          Class cls = Class.forName(this.defaultValue.substring(0, index));
          Method method = cls.getMethod(this.defaultValue.substring(index + 1), HttpServletRequest.class);
          String result = (String) method.invoke(null, new Object[] {request});
          return result;
        } catch (Exception e) {
          throw new ConeException(e);
        }
      }
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public boolean isMultiple() {
      return multiple;
    }

    public void setMultiple(boolean multiple) {
      this.multiple = multiple;
    }

    public boolean isMandatory() {
      return mandatory;
    }

    public void setMandatory(boolean mandatory) {
      this.mandatory = mandatory;
    }

    public boolean isLocalized() {
      return localized;
    }

    public void setLocalized(boolean localized) {
      this.localized = localized;
    }

    public List<Predicate> getPredicates() {
      return predicates;
    }

    public boolean isIncludeResource() {
      return includeResource;
    }

    public void setIncludeResource(boolean includeResource) {
      this.includeResource = includeResource;
    }

    public boolean isGenerateObject() {
      return generateObject;
    }

    public void setGenerateObject(boolean generateObject) {
      this.generateObject = generateObject;
    }

    public String getResourceModel() {
      return resourceModel;
    }

    public void setResourceModel(String resourceModel) {
      this.resourceModel = resourceModel;
    }

    public void setSearchable(boolean searchable) {
      this.searchable = searchable;
    }

    public boolean isSearchable() {
      return searchable;
    }

    public void setRestricted(boolean restricted) {
      this.restricted = restricted;
    }

    public boolean isRestricted() {
      return restricted;
    }

    public boolean isOverwrite() {
      return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
    }

    public boolean isShouldBeUnique() {
      return shouldBeUnique;
    }

    public void setShouldBeUnique(boolean shouldBeUnique) {
      this.shouldBeUnique = shouldBeUnique;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
    }

    public boolean isModify() {
      return modify;
    }

    public void setModify(boolean modify) {
      this.modify = modify;
    }

    public Event getEvent() {
      return event;
    }

    public void setEvent(Event event) {
      this.event = event;
    }

    public String toString() {
      return id;
    }

    public String getSuggestUrl() {
      return suggestUrl;
    }

    public void setSuggestUrl(String suggestUrl) {
      this.suggestUrl = suggestUrl;
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }

  }

  public class ModelResult {

    private String resultPattern;

    private String sortPattern;

    private String type;

    public String getResultPattern() {
      return resultPattern;
    }

    public void setResultPattern(String resultPattern) {
      this.resultPattern = resultPattern;
    }

    public String getSortPattern() {
      return sortPattern;
    }

    public void setSortPattern(String sortPattern) {
      this.sortPattern = sortPattern;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

  }
}
