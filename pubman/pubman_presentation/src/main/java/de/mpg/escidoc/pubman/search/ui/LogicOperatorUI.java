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

package de.mpg.escidoc.pubman.search.ui;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;

import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.model.Option;

import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;

/**
 * @author Hugo Niedermaier
 * 
 */
public class LogicOperatorUI
{
    protected HtmlPanelGroup panel = new HtmlPanelGroup();
    protected DropDown cboLogicOperator = new DropDown();
      
    public final Option LOGIC_AND = new Option("And", "And");
    public final Option LOGIC_OR = new Option("Or", "Or");
    public final Option LOGIC_NOT = new Option("Not", "Not");
    public final Option[] LOGIC_OPTIONS = new Option[]{LOGIC_AND, LOGIC_OR, LOGIC_NOT};
    
    private HTMLElementUI htmlElement = new HTMLElementUI();

    public LogicOperatorUI( ResourceBundle bundle )
    {
        this.initialize();
        this.updateLanguage( bundle );
    }
    
    public void updateLanguage( ResourceBundle bundle ) 
    {
    	this.LOGIC_AND.setLabel( bundle.getString( "adv_search_logicop_and" ) );
    	this.LOGIC_OR.setLabel( bundle.getString( "adv_search_logicop_or" ) );
    	this.LOGIC_NOT.setLabel( bundle.getString( "adv_search_logicop_not" ) );
    }
    
    public static LogicOperator getLogicOperatorByString( String s ) 
    {
        if (s.equals("And"))
            return LogicOperator.AND;
        else if (s.equals("Or"))
            return LogicOperator.OR;
        else if (s.equals("Not"))
            return LogicOperator.NOT;
        else
            return LogicOperator.AND;
    }
    
    
    protected void initialize()
    {
        
        this.panel.setId(CommonUtils.createUniqueId(this.panel));
        
        this.panel.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "operatorCenter"));
        this.cboLogicOperator.setId(CommonUtils.createUniqueId(this.cboLogicOperator));
        this.cboLogicOperator.setItems(LOGIC_OPTIONS);
        this.panel.getChildren().add(this.cboLogicOperator);
        this.panel.getChildren().add(htmlElement.getEndTag("div"));
    }

    public UIComponent getUIComponent()
    {
        return this.panel;
    }

    public DropDown getCboLogicOperator()
    {
        return cboLogicOperator;
    }

    public void setCboLogicOperator(DropDown cboLogicOperator)
    {
        this.cboLogicOperator = cboLogicOperator;
    }
    
    public void setVisible(boolean b)
    {
        this.cboLogicOperator.setVisible(b);
    }
}
