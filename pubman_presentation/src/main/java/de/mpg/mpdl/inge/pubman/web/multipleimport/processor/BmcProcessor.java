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

package de.mpg.mpdl.inge.pubman.web.multipleimport.processor;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * takes a BMC article and returns him (no known collections of articles)
 *
 * @author Stefan Krause, Editura GmbH & Co. KG (initial creation)
 *
 * @author $Author: skrause $ (last modification)
 *
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class BmcProcessor extends GenericXmlProcessor {
  private static final String BMC_NS = "http://www.biomedcentral.com/xml/schemas/";

  @Override
  protected void addItems(Node root) {
    if (this.isBmcArticle(root)) {
      this.addItem(root);
    } else
    // I've never seen a collection of BMC records, but a generic outermost element will do the job,
    // Stf, 2013-03-22
    {
      Boolean foundArticle = false;
      NodeList nodes = root.getChildNodes();

      for (int i = 0; i < nodes.getLength(); i++) {
        Node currentNode = nodes.item(i);
        if (this.isBmcArticle(currentNode)) {
          this.addItem(currentNode);
          foundArticle = true;
        }
      }

      if (!foundArticle) {
        throw new RuntimeException("document format not supported: root = {" + root.getNamespaceURI() + "}" + root.getLocalName());
      }

      foundArticle = null;
      nodes = null;
    }
  }

  private Boolean isBmcArticle(Node node) {
    if (null == node || null == node.getLocalName()) // to prevent a NPE
    {
      return false;
    } else if ("art".equals(node.getLocalName())
        && (null == node.getNamespaceURI() || node.getNamespaceURI().equals(BmcProcessor.BMC_NS))) {
      return true;
    } else {
      return false;
    }
  }
}
