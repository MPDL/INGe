/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.services.cone.rdfimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ShortContentHandler;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.ModelList.Predicate;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.LocalizedTripleObject;
import de.mpg.escidoc.services.cone.util.TreeFragment;
import de.mpg.escidoc.services.util.PropertyReader;


/**
 * SAX handler to read out RDF/XML data.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class RDFHandler extends DefaultHandler {

  private List<LocalizedTripleObject> result = new ArrayList<LocalizedTripleObject>();
  private Stack<LocalizedTripleObject> stack = new Stack<LocalizedTripleObject>();
  private String instanceUrl;

  private Stack<QName> tagStack = new Stack<QName>();

  private Querier querier;

  private Model model;

  private StringBuffer currentContent;

  private String currentRdfAbout;

  private final static QName rdfRootTag = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#",
      "RDF", "rdf");
  public final static QName rdfDescriptionTag = new QName(
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf");


  public RDFHandler(boolean loggedIn, Model model) {
    this.model = model;
    querier = QuerierFactory.newQuerier(loggedIn);
    try {
      this.instanceUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException {
    // super.startElement(uri, localName, name, attributes);


    QName currentTag = new QName(uri, localName);


    if (tagStack.size() == 1 && tagStack.peek().equals(rdfRootTag)
        && currentTag.equals(model.getRdfAboutTag())) {
      // New element
      String subject = attributes.getValue("rdf:about");
      this.stack.push(new TreeFragment(subject));
    }


    else if (tagStack.size() > 1 && tagStack.get(0).equals(rdfRootTag)) {
      String predicate;
      // String namespace;
      // String tagName;
      if (uri != null) {

        if (!uri.endsWith("/") && !uri.endsWith("#")) {
          predicate = uri + " " + localName;
        } else {
          predicate = uri + localName;
        }
      }

      else {
        predicate = name;
      }

      if (currentTag.equals(model.getRdfAboutTag()))
      // if(tag.equals(model.getRdfImportTag()))
      {
        LocalizedString wrongData = (LocalizedString) this.stack.pop();
        TreeFragment container = (TreeFragment) this.stack.peek();
        String newSubject = attributes.getValue("rdf:about");
        if (newSubject == null) {
          try {
            newSubject = querier.createUniqueIdentifier(null);
          } catch (Exception e) {
            throw new SAXException(e);
          }
        }

        String pred = null;
        for (Iterator<String> iterator = container.keySet().iterator(); iterator.hasNext();) {
          String key = (String) iterator.next();
          if (container.get(key).contains(wrongData)) {
            pred = key;
            break;
          }
        }

        TreeFragment betterData = new TreeFragment(newSubject, attributes.getValue("xml:lang"));
        container.get(pred).remove(wrongData);
        container.get(pred).add(betterData);
        this.stack.push(betterData);
      } else if (this.stack.peek() instanceof TreeFragment) {
        LocalizedString firstValue = new LocalizedString();
        firstValue.setLanguage(attributes.getValue("xml:lang"));

        // For predicates that link to other resources, use rdf:resource attribute as content
        Predicate p = model.getPredicate(predicate);
        if (p != null && p.getResourceModel() != null && !p.isIncludeResource()) {
          if (attributes.getValue("rdf:resource") != null) {
            firstValue.setValue(attributes.getValue("rdf:resource"));
          } else
            throw new SAXException("Excpected attribute rdf:resource for element" + name
                + " (namespace " + uri
                + "), because it has attribute resourceModel and is set as includeResource=false");

        }

        if (((TreeFragment) this.stack.peek()).get(predicate) != null) {
          ((TreeFragment) this.stack.peek()).get(predicate).add(firstValue);
        } else {
          List<LocalizedTripleObject> newList = new ArrayList<LocalizedTripleObject>();
          newList.add(firstValue);
          ((TreeFragment) this.stack.peek()).put(predicate, newList);
        }
        this.stack.push(firstValue);
      } else {
        throw new SAXException("Wrong RDF structure at " + name + " (namespace " + uri + ")");
      }
    }
    tagStack.push(currentTag);
    currentContent = new StringBuffer();
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    // super.endElement(uri, localName, name);

    QName currentTag = new QName(uri, localName);

    if (currentContent != null && currentContent.length() > 0) {
      if (this.stack.peek() instanceof LocalizedString) {
        ((LocalizedString) this.stack.peek()).setValue(currentContent.toString());
      } else {
        throw new RuntimeException("Wrong RDF structure at " + name + " (namespace " + uri + ")");
      }
    }


    tagStack.pop();
    currentContent = null;

    if (tagStack.size() == 1 && tagStack.peek().equals(rdfRootTag)) {
      result.add(this.stack.pop());
      return;
    }

    /*
     * String namespace; String tagName; if (name.contains(":")) { String nsPrefix =
     * name.split(":")[0]; namespace = namespaces.get(nsPrefix); tagName = name.split(":")[1]; }
     * else if (namespaces.get("") != null) { namespace = namespaces.get(""); tagName = name; } else
     * { namespace = null; tagName = name; }
     */


    if (!this.stack.isEmpty() && !currentTag.equals(model.getRdfAboutTag())) {
      this.stack.pop();
    }

  }



  public List<LocalizedTripleObject> getResult() {
    return this.result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    try {
      querier.release();
    } catch (Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * Append characters to current content.
   */
  @Override
  public final void characters(char[] ch, int start, int length) throws SAXException {
    if (currentContent != null) {
      currentContent.append(ch, start, length);
    }
  }


}
