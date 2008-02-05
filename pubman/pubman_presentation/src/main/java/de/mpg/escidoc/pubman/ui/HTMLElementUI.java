/*
*
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

package de.mpg.escidoc.pubman.ui;

import javax.faces.component.html.HtmlOutputText;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * UI class for generating unescaped common HTML elements.
 *
 * @author: Tobias Schraut, created 06.09.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class HTMLElementUI
{
    private HtmlOutputText text;

    /**
     * Public constructor.
     */
    public HTMLElementUI()
    {
    }

    /**
     * Shortcut for the method getStartTag(String elementName, String styleClass, String id) without having to provide
     * a styleclass or an id.
     *
     * @author Thomas Diebaecker
     * @param elementName
     * @return a HtmlOutputText component which contains the unescaped tag
     */
    public HtmlOutputText getStartTag(String elementName)
    {
        return this.getStartTag(elementName, null, null);
    }

    /**
     * Shortcut for the method getStartTag(String elementName, String styleClass, String id) without having to provide
     * an id.
     *
     * @author Thomas Diebaecker
     * @param elementName name of the HTML Element (e.g. div)
     * @param styleClass the style class in the CSS Stylesheet which formats the element
     * @return a HtmlOutputText component which contains the unescaped tag
     */
    public HtmlOutputText getStartTagWithStyleClass(String elementName, String styleClass)
    {
        return this.getStartTag(elementName, styleClass, null);
    }

    /**
     * Shortcut for the method getStartTag(String elementName, String styleClass, String id) without having to provide
     * a style class.
     *
     * @author Thomas Diebaecker
     * @param elementName name of the HTML Element (e.g. div)
     * @param id ID in the CSS Stylesheet to be referenced
     * @return a HtmlOutputText component which contains the unescaped tag
     */
    public HtmlOutputText getStartTagWithID(String elementName, String id)
    {
        return this.getStartTag(elementName, null, id);
    }

    /**
     * Generates the desired unescaped HTML start Tag (e.g. <div>).
     *
     * @param elementName name of the HTML Element(e.g. div)
     * @param styleClass the style class in the CSS Stylesheet which formats the element
     * @param id ID in the CSS Stylesheet to be referenced
     * @return HtmlOutputText a HtmlOutputText component which contains the unescaped tag
     */
    private HtmlOutputText getStartTag(String elementName, String styleClass, String id)
    {
        if (elementName != null || !elementName.equals(""))
        {
            String cssStyleClass = "";
            String elementID = "";
            
            if (styleClass != null)
            {
                cssStyleClass = "class='" + styleClass + "'";
            }
            else if (id != null)
            {
                elementID = "id='" + id + "'";
            }
            
            this.text = new HtmlOutputText();
            this.text.setId(CommonUtils.createUniqueId(this.text));
            this.text.setEscape(false);
            this.text.setValue("<" + elementName + " " + cssStyleClass + elementID + ">");
        }
        
        return this.text;
    }

    /**
     * Generates the desired unescaped HTML end Tag (e.g. </div>).
     * 
     * @param elementName elementName name of the HTML Element(e.g. div)
     * @return HtmlOutputText a HtmlOutputText component which contains the unescaped tag
     */
    public HtmlOutputText getEndTag(String elementName)
    {
        if (elementName != null || !elementName.equals(""))
        {
            this.text = new HtmlOutputText();
            this.text.setId(CommonUtils.createUniqueId(this.text));
            this.text.setEscape(false);
            this.text.setValue("</" + elementName + ">");
        }
        
        return this.text;
    }
}
