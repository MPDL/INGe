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

import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;

import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.EventCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.IdentifierCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.OrganizationCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.SourceCriterionVO;

/**
 * This mask collects search data for organization or event or identifier or source.
 * @author endres
 * @version $Revision: 1639 $ $LastChangedDate: 2007-12-04 15:06:47 +0100 (Di, 04 Dez 2007) $
 *
 */
public class GenericUIMask extends UIMask
{
    /** Type of search */
    public static enum Type 
    {
        /** Organization search mask */
        ORGANIZATION, 
        /** Event search mask */
        EVENT, 
        /** Identifier search mask */
        IDENTIFIER, 
        /** Source search mask */
        SOURCE};
    
    private Type type;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlPanelGroup panel1 = new HtmlPanelGroup();
    private HtmlPanelGroup panel2 = new HtmlPanelGroup();
    private HtmlOutputLabel lblSearchString = new HtmlOutputLabel();
    private HtmlInputText txtSearchString = new HtmlInputText();
    private HtmlSelectOneMenu cboLanguage = new HtmlSelectOneMenu();        
    private HtmlOutputLabel lblCboLanguage = new HtmlOutputLabel();
    
    /**
     * Implements one text input field for given type of search.
     * @param t type of search
     * @param st search type reference
     */
    public GenericUIMask( Type t, SearchTypeUI st )
    {
        super( st );
        
        this.type = t;
        
        this.panel1.setId(CommonUtils.createUniqueId(this.panel1));
        this.panel1.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "searchTerm"));
        this.lblSearchString.setId(CommonUtils.createUniqueId(this.lblSearchString));
        this.lblSearchString.setValue(getLabel("adv_search_lblSearchTerm"));
        this.txtSearchString.setId(CommonUtils.createUniqueId(this.txtSearchString));
        this.txtSearchString.setImmediate(true);
        this.panel1.getChildren().add(lblSearchString);
        this.panel1.getChildren().add(txtSearchString);
        this.panel1.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel(this.panel1);
        
        //the drop down language only for the organization, event and source criterion
        if (type == Type.ORGANIZATION || type == Type.EVENT || type == Type.SOURCE)
        {
            this.panel2.setId(CommonUtils.createUniqueId(this.panel2));
            this.panel2.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formGroupTitle"));
            this.lblCboLanguage.setId(CommonUtils.createUniqueId(this.lblCboLanguage));
            this.lblCboLanguage.setValue(getLabel("adv_search_lblLanguage"));
            // disable language for now
            this.lblCboLanguage.setRendered( false );
            
            this.cboLanguage.getChildren().clear();
            this.cboLanguage.setId(CommonUtils.createUniqueId(this.cboLanguage));
            this.cboLanguage.getChildren().addAll(CommonUtils.convertToSelectItemsUI(CommonUtils.getLanguageOptions()));
            this.cboLanguage.setImmediate(true);
            // disable language for now
            this.cboLanguage.setRendered(false);
            this.panel2.getChildren().add(this.lblCboLanguage);
            this.panel2.getChildren().add(this.cboLanguage);
            this.panel2.getChildren().add(htmlElement.getEndTag("div"));
            // disable language for now
            // this.addPanelToCommonPanel(this.panel2);
        }
        
        // finally add the buttons
        super.addButtonsAndLogicOperatorToPanel();      
    }
    
    @Override
    void clearForm()
    {
        this.getTxtSearchString().setValue("");
        this.getCboLanguage().setValue("-");
    }

    @Override
    CriterionVO getCriterionFromArrays()
    {
        if (type == Type.ORGANIZATION)
          {
              OrganizationCriterionVO organizationCriterionVO = new OrganizationCriterionVO();
              organizationCriterionVO.setSearchString((String)this.getTxtSearchString().getValue());
//              organizationCriterionVO.setLanguage(mask.getCboLanguage().getSelected().toString());             
              return organizationCriterionVO;
          }
          else if (type == Type.SOURCE)
          {
              SourceCriterionVO sourceCriterionVO = new SourceCriterionVO();
              sourceCriterionVO.setSearchString((String)this.getTxtSearchString().getValue());
//              sourceCriterionVO.setLanguage(mask.getCboLanguage().getSelected().toString());
              return sourceCriterionVO;
          }
          else if (type == Type.EVENT)
          {
              EventCriterionVO eventCriterionVO = new EventCriterionVO();
              eventCriterionVO.setSearchString((String)this.getTxtSearchString().getValue());
//              eventCriterionVO.setLanguage(mask.getCboLanguage().getSelected().toString());
              return eventCriterionVO;
          }
          else if (type == Type.IDENTIFIER)
          {
              IdentifierCriterionVO identifierCriterionVO = new IdentifierCriterionVO();
              identifierCriterionVO.setSearchString((String)this.getTxtSearchString().getValue());
              return identifierCriterionVO;
          }
        // TODO endres: add an exception
          else 
          {
              return new EventCriterionVO();
          }
    }

    @Override
    boolean hasData()
    {
        String searchString = (String)this.getTxtSearchString().getValue();
        if (searchString != null && searchString.length() > 0)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    @Override
    public void refreshAppearance() 
    {
    	// refresh buttons and operator
        super.refreshAppearanceButtonsAndOp();
        
        this.lblSearchString.setValue(getLabel("adv_search_lblSearchTerm"));
    }

    public HtmlSelectOneMenu getCboLanguage()
    {
        return cboLanguage;
    }

    public HtmlInputText getTxtSearchString()
    {
        return txtSearchString;
    }

    
}
