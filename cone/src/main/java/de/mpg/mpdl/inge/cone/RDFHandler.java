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

package de.mpg.mpdl.inge.cone;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.ModelList.Predicate;
import de.mpg.mpdl.inge.util.PropertyReader;


/**
 * SAX handler to read out RDF/XML data.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RDFHandler extends DefaultHandler {

  private final List<LocalizedTripleObject> result = new ArrayList<>();
  private final Stack<LocalizedTripleObject> stack = new Stack<>();
  private final Stack<QName> tagStack = new Stack<>();

  private final Querier querier;
  private final Model model;
  private StringBuilder currentContent;

  private static final Logger logger = Logger.getLogger(RDFHandler.class);

  private static final QName rdfRootTag = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf");

  public RDFHandler(boolean loggedIn, Model model) throws ConeException {
    this.model = model;
    querier = QuerierFactory.newQuerier(loggedIn);
    try {
      PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
    } catch (Exception e) {
      throw new ConeException(e);
    }
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

    QName currentTag = new QName(uri, localName);

    if (tagStack.size() == 1 && tagStack.peek().equals(rdfRootTag) && currentTag.equals(model.getRdfAboutTag())) {
      // New element
      String subject = attributes.getValue("rdf:about");
      this.stack.push(new TreeFragment(subject));
    }

    else if (tagStack.size() > 1 && tagStack.get(0).equals(rdfRootTag)) {
      String predicate;

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
        for (String key : container.keySet()) {
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

        // For predicates that link to other resources, use rdf:resource
        // attribute as content
        Predicate p = null;
        try {
          p = model.getPredicate(predicate);
        } catch (ConeException e) {
          logger.warn("getPredica<te failed", e);
        }
        if (p != null && p.getResourceModel() != null && !p.isIncludeResource()) {
          if (attributes.getValue("rdf:resource") != null) {
            firstValue.setValue(attributes.getValue("rdf:resource"));
          } else
            throw new SAXException("Excpected attribute rdf:resource for element" + name + " (namespace " + uri
                + "), because it has attribute resourceModel and is set as includeResource=false");

        }

        if (((TreeFragment) this.stack.peek()).get(predicate) != null) {
          ((TreeFragment) this.stack.peek()).get(predicate).add(firstValue);
        } else {
          List<LocalizedTripleObject> newList = new ArrayList<>();
          newList.add(firstValue);
          ((TreeFragment) this.stack.peek()).put(predicate, newList);
        }
        this.stack.push(firstValue);
      } else {
        throw new SAXException("Wrong RDF structure at " + name + " (namespace " + uri + ")");
      }
    }
    tagStack.push(currentTag);
    currentContent = new StringBuilder();
  }

  @Override
  public void endElement(String uri, String localName, String name) {
    // super.endElement(uri, localName, name);

    QName currentTag = new QName(uri, localName);

    if (currentContent != null && !currentContent.isEmpty()) {
      if (this.stack.peek() instanceof LocalizedString) {
        ((LocalizedString) this.stack.peek()).setValue(currentContent.toString());
      } else {
        logger.warn("Wrong RDF structure at " + name + " (namespace " + uri + ")");
      }
    }


    tagStack.pop();
    currentContent = null;

    if (tagStack.size() == 1 && tagStack.peek().equals(rdfRootTag)) {
      result.add(this.stack.pop());
      return;
    }

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
  public final void characters(char[] ch, int start, int length) {
    if (currentContent != null) {
      currentContent.append(ch, start, length);
    }
  }


}
