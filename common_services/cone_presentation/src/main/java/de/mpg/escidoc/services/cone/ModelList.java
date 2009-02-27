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

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * A SAX parser that reads in the servieces.xml configuration file.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ModelList
{
    private static ModelList instance = null;
    private static final Logger logger = Logger.getLogger(ModelList.class);
    private Set<Model> list = new HashSet<Model>();

    private ModelList() throws Exception
    {
        InputStream in = ResourceUtil.getResourceAsStream("explain/models.xml");
        ServiceListHandler listHandler = new ServiceListHandler();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(in, listHandler);
        list = listHandler.getList();
        logger.debug("Length: " + list.size());
        for (Model model : list)
        {
            logger.debug("Model:" + model.getName());
        }
    }

    /**
     * Returns the singleton.
     * 
     * @throws Exception Any exception.
     * @return The singleton
     */
    public static ModelList getInstance() throws Exception
    {
        if (instance == null)
        {
            instance = new ModelList();
        }
        return instance;
    }

    public Set<Model> getList()
    {
        return list;
    }

    /**
     * Find a model by its alias.
     * 
     * @param alias The String to look for.
     * @return The first {@link Model} in the list using the given alias.
     */
    public Model getModelByAlias(String alias)
    {
        for (Model model : getList())
        {
            if (model.getAliases().contains(alias))
            {
                return model;
            }
        }
        return null;
    }
    
    /**
     * SAX handler.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    private class ServiceListHandler extends ShortContentHandler
    {
        private Set<Model> list = new HashSet<Model>();
        private Model currentService = null;
        private Stack<List<Predicate>> predicateStack = new Stack<List<Predicate>>();

        @Override
        public void content(String uri, String localName, String name, String content)
        {
            if ("models/model/name".equals(stack.toString()))
            {
                currentService.setName(content.trim());
            }
            else if ("models/model/description".equals(stack.toString()))
            {
                currentService.setDescription(content.trim());
            }
            else if ("models/model/aliases/alias".equals(stack.toString()))
            {
                currentService.getAliases().add(content.trim());
            }
            else if ("models/model/primary-identifier".equals(stack.toString()))
            {
                currentService.setIdentifier(content.trim());
            }
            else if ("models/model/result/pattern".equals(stack.toString()))
            {
                currentService.getResultPattern().add(content.trim());
            }
            
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, name, attributes);
            if ("models/model".equals(stack.toString()))
            {
                currentService = new Model();
            }
            else if ("models/model/predicates".equals(stack.toString()))
            {
                this.predicateStack.push(currentService.getPredicates());
            }
            else if ("predicate".equals(name))
            {
                Predicate predicate = new Predicate(
                        attributes.getValue("value"),
                        attributes.getValue("name"),
                        Boolean.parseBoolean(attributes.getValue("multiple")),
                        Boolean.parseBoolean(attributes.getValue("mandatory")),
                        Boolean.parseBoolean(attributes.getValue("localized")),
                        Boolean.parseBoolean(attributes.getValue("generateObject")),
                        Boolean.parseBoolean(attributes.getValue("searchable")),
                        attributes.getValue("resourceModel"));
                this.predicateStack.peek().add(predicate);
                this.predicateStack.push(predicate.getPredicates());
            }
            else if ("models/model/primary-identifier".equals(stack.toString()))
            {
                currentService.setGenerateIdentifier(Boolean.parseBoolean(attributes.getValue("generate")));
                currentService.setIdentifierPrefix(attributes.getValue("prefix"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            if ("models/model".equals(stack.toString()))
            {
                list.add(currentService);
            }
            else if ("predicate".equals(name))
            {
                this.predicateStack.pop();
            }
            super.endElement(uri, localName, name);
        }

        @Override
        public void endDocument() throws SAXException
        {
            super.endDocument();
            
            Stack<String> stack = new Stack<String>();
            
            for (Model model : list)
            {
                stack.push(model.getName());
                setI18nFlags(model, model.getPredicates(), stack);
                stack.pop();
            }
        }

        /**
         * @param model
         */
        private void setI18nFlags(Model model, List<Predicate> predicates, Stack<String> stack)
        {
            for (Predicate predicate : predicates)
            {
                for (String pattern : model.getResultPattern())
                {
                    if (predicate.isLocalized() && pattern.contains("<" + predicate.getId() + ">"))
                    {
                        model.setLocalizedResultPattern(true);
                    }
                    else if (!predicate.isLocalized() && pattern.contains("<" + predicate.getId() + ">"))
                    {
                        model.setGlobalResultPattern(true);
                    }
                }
                
                if (predicate.isSearchable())
                {
                    if (predicate.isLocalized())
                    {
                        model.setLocalizedMatches(true);
                    }
                    else
                    {
                        model.setGlobalMatches(true);
                    }
                }
                
                if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
                {
                    setI18nFlags(model, predicate.getPredicates(), stack);
                }
                else if (predicate.isResource())
                {
                    try
                    {
                        for (Model nextModel : list)
                        {
                            if (nextModel.getName().equals(predicate.getResourceModel()))
                            {
                                if (!(stack.contains(nextModel.getName())))
                                {
                                    stack.push(nextModel.getName());
                                    setI18nFlags(model, nextModel.getPredicates(), stack);
                                    stack.pop();
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public Set<Model> getList()
        {
            return list;
        }
   
    }

    /**
     * Generic SAX handler with convenience methods. Useful for XML with only short string content. Classes that extend
     * this class should always call super() at the beginning of an overridden method.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    private class ShortContentHandler extends DefaultHandler
    {
        private StringBuffer currentContent;
        protected XMLStack stack = new XMLStack();

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            stack.push(name);
            currentContent = new StringBuffer();
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            content(uri, localName, name, currentContent.toString());
            stack.pop();
        }

        @Override
        public final void characters(char[] ch, int start, int length) throws SAXException
        {
            currentContent.append(ch, start, length);
        }

        /**
         * Called when string content was found.
         * 
         * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
         *            processing is not being performed.
         * @param localName The local name (without prefix), or the empty string if Namespace processing is not being
         *            performed.
         * @param name The qualified name (with prefix), or the empty string if qualified names are not available.
         * @param content The string content of the current tag.
         */
        public void content(String uri, String localName, String name, String content)
        {
            // Do nothing by default
        }

        /**
         * A {@link Stack} extension to facilitate XML navigation.
         * 
         * @author franke (initial creation)
         * @author $Author$ (last modification)
         * @version $Revision$ $LastChangedDate$
         */
        private class XMLStack extends Stack<String>
        {
            /**
             * Returns a String representation of the Stack in an XPath like way (e.g. "root/subtag/subsub"):
             */
            @Override
            public synchronized String toString()
            {
                StringWriter writer = new StringWriter();
                for (Iterator<String> iterator = this.iterator(); iterator.hasNext();)
                {
                    String element = (String) iterator.next();
                    writer.append(element);
                    if (iterator.hasNext())
                    {
                        writer.append("/");
                    }
                }
                return writer.toString();
            }
        }
    }

    /**
     * A bean holding data of a CoNE service.
     * 
     * @author franke (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public class Model
    {
        private String name;
        private String description;
        private List<String> aliases = new ArrayList<String>();
        private List<Predicate> predicates = new ArrayList<Predicate>();
        private String identifier;
        private String identifierPrefix;
        private boolean generateIdentifier;
        private List<String> resultPattern = new ArrayList<String>();
        private boolean localizedResultPattern;
        private boolean globalResultPattern;
        private boolean localizedMatches;
        private boolean globalMatches;
        /**
         * Default constructor.
         */
        public Model()
        {
        }

        /**
         * Constructor by name.
         * 
         * @param name The service name
         */
        public Model(String name)
        {
            this.name = name;
        }

        /**
         * Constructor by name and description.
         * 
         * @param name The service name
         * @param description The description
         */
        public Model(String name, String description)
        {
            this.name = name;
            this.description = description;
        }

        /**
         * Constructor by name, description and aliases.
         * 
         * @param name The service name
         * @param description The description
         * @param aliases The {@link List} of aliases.
         */
        public Model(String name, String description, List<String> aliases)
        {
            this.name = name;
            this.description = description;
            this.aliases = aliases;
        }

        /**
         * Constructor by name, description and aliases.
         * 
         * @param name The service name
         * @param description The description
         * @param aliases The {@link List} of aliases
         * @param predicates The {@link List} of allowed predicates
         */
        public Model(String name, String description, List<String> aliases, List<Predicate> predicates)
        {
            this.name = name;
            this.description = description;
            this.aliases = aliases;
            this.predicates = predicates;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public List<String> getAliases()
        {
            return aliases;
        }

        public void setAliases(List<String> aliases)
        {
            this.aliases = aliases;
        }

        public List<Predicate> getPredicates()
        {
            return predicates;
        }

        public void setPredicates(List<Predicate> predicates)
        {
            this.predicates = predicates;
        }

        public String getIdentifier()
        {
            return identifier;
        }

        public void setIdentifier(String identifier)
        {
            this.identifier = identifier;
        }

        public boolean isGenerateIdentifier()
        {
            return generateIdentifier;
        }

        public void setGenerateIdentifier(boolean generateIdentifier)
        {
            this.generateIdentifier = generateIdentifier;
        }

        public String getIdentifierPrefix()
        {
            return identifierPrefix;
        }

        public void setIdentifierPrefix(String identifierPrefix)
        {
            this.identifierPrefix = identifierPrefix;
        }

        public List<String> getResultPattern()
        {
            return resultPattern;
        }

        public void setResultPattern(List<String> resultPattern)
        {
            this.resultPattern = resultPattern;
        }

        public boolean isLocalizedResultPattern()
        {
            return localizedResultPattern;
        }

        public void setLocalizedResultPattern(boolean localizedResultPattern)
        {
            this.localizedResultPattern = localizedResultPattern;
        }

        public boolean isGlobalResultPattern()
        {
            return globalResultPattern;
        }

        public void setGlobalResultPattern(boolean globalResultPattern)
        {
            this.globalResultPattern = globalResultPattern;
        }
        
        public boolean isLocalizedMatches()
        {
            return localizedMatches;
        }

        public void setLocalizedMatches(boolean localizedMatches)
        {
            this.localizedMatches = localizedMatches;
        }

        public boolean isGlobalMatches()
        {
            return globalMatches;
        }

        public void setGlobalMatches(boolean globalMatches)
        {
            this.globalMatches = globalMatches;
        }

        /**
         * Find a predicate by id.
         * 
         * @param predicateId the id of the predicate. If the id is null, a {@link NullPointerException} is thrown.
         * 
         * @return null if there is no predicate with the given id, the according predicate otherwise.
         */
        public Predicate getPredicate(String predicateId)
        {
            if (predicateId == null)
            {
                throw new NullPointerException("Empty predicate name");
            }
            for (Predicate predicate : getPredicates())
            {
                if (predicateId.equals(predicate.getId()))
                {
                    return predicate;
                }
            }
            return null;
        }

        /**
         * Compares to other objects.
         * 
         * @param object The object this object is compared to
         * @return true, if the other object is a {@link Model} with the same name.
         */
        @Override
        public boolean equals(Object object)
        {
            if (object instanceof Model)
            {
                if (object == null)
                {
                    return false;
                }
                else if (((Model) object).name == null)
                {
                    return (this.name == null);
                }
                else
                {
                    return (((Model) object).name.equals(this.name));
                }
            }
            else
            {
                return false;
            }
        }

        /**
         * Returns the hashCode of the service name. This is needed for using {@link HashSet}s correctly.
         * 
         * @return The hashCode
         */
        @Override
        public int hashCode()
        {
            return (this.name == null ? 0 : this.name.hashCode());
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
    public class Predicate
    {
        private String name;
        private String id;
        private boolean multiple;
        private boolean mandatory;
        private boolean localized;
        private List<Predicate> predicates = new ArrayList<Predicate>();
        private boolean generateObject = false;
        private String resourceModel;
        private boolean searchable;

        /**
         * Constructor using all fields.
         * 
         * @param id The value of the predicate.
         * @param multiple Flag that indicates that this predicate might occur more than once.
         * @param mandatory Flag that indicates the this predicate must occur at least once.
         * @param localized Flag that indicates that this predicate might occur in different languages.
         * @param name The label of this predicate.
         * @param generateObject Flag indicating that this predicate has sub-predicates that are not defined
         * by a certain identifier.
         * @param searchable Flag that indicates that this predicate shall be found when querying this model.
         * @param resourceModel Flag indicating if the object is an identifier to a stand-alone resourceModel. If so,
         * this resourceModel won't be editable, but linked. Furthermore, it will not be deleted in case the
         * current subject is deleted.
         */
        public Predicate(
                String id,
                String name,
                boolean multiple,
                boolean mandatory,
                boolean localized,
                boolean generateObject,
                boolean searchable,
                String resourceModel)
        {
            this.id = id;
            
            this.multiple = multiple;
            this.mandatory = mandatory;
            this.localized = localized;
            this.name = name;
            this.generateObject = generateObject;
            this.searchable = searchable;
            this.resourceModel = resourceModel;
        }
        
        public boolean isResource()
        {
            return (resourceModel != null);
        }
        
        /**
         * Find a sub predicate by id.
         * 
         * @param predicateId the id of the sub predicate. If the id is null, a {@link NullPointerException} is thrown.
         * 
         * @return null if there is no sub predicate with the given id, the according sub predicate otherwise.
         */
        public Predicate getPredicate(String predicateId)
        {
            if (predicateId == null)
            {
                throw new NullPointerException("Empty predicate name");
            }
            for (Predicate predicate : getPredicates())
            {
                if (predicateId.equals(predicate.getId()))
                {
                    return predicate;
                }
            }
            return null;
        }
        
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public String getId()
        {
            return id;
        }
        public void setId(String id)
        {
            this.id = id;
        }
        public boolean isMultiple()
        {
            return multiple;
        }
        public void setMultiple(boolean multiple)
        {
            this.multiple = multiple;
        }

        public boolean isMandatory()
        {
            return mandatory;
        }

        public void setMandatory(boolean mandatory)
        {
            this.mandatory = mandatory;
        }

        public boolean isLocalized()
        {
            return localized;
        }

        public void setLocalized(boolean localized)
        {
            this.localized = localized;
        }

        public List<Predicate> getPredicates()
        {
            return predicates;
        }

        public boolean isGenerateObject()
        {
            return generateObject;
        }

        public void setGenerateObject(boolean generateObject)
        {
            this.generateObject = generateObject;
        }

        public String getResourceModel()
        {
            return resourceModel;
        }

        public void setResourceModel(String resourceModel)
        {
            this.resourceModel = resourceModel;
        }

        public void setSearchable(boolean searchable)
        {
            this.searchable = searchable;
        }
        
        public boolean isSearchable()
        {
            return searchable;
        }


    }
}
