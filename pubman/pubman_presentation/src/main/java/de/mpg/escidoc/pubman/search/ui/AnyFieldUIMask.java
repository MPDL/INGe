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

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.RadioButtonGroup;
import com.sun.rave.web.ui.component.TextField;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.pubman.valueobjects.AnyFieldCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TitleCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TopicCriterionVO;

/**
 * This mask collects search data for a anyfield, title or subject query.
 * @author endres
 * @version $Revision: 1639 $ $LastChangedDate: 2007-12-04 15:06:47 +0100 (Tue, 04 Dec 2007) $
 *
 */
public class AnyFieldUIMask extends UIMask
{
    private HtmlPanelGroup panel1 = new HtmlPanelGroup();
    private HtmlPanelGroup panel2 = new HtmlPanelGroup();
   
    private Label lblSearchStringAnyField = new Label();
    private TextField txtSearchStringAnyField = new TextField();
    private HtmlSelectBooleanCheckbox chkIncludeFiles = new HtmlSelectBooleanCheckbox();
    private HtmlOutputText txtIncludeFiles = new HtmlOutputText();
//    private DropDown cboLanguage = new DropDown();        
//    private Label lblCboLanguage = new Label();
    private RadioButtonGroup rbgType = new RadioButtonGroup();
    public static final String TITLE = "TITLE";
    public static final String TOPIC = "TOPIC";
    public static final String ANY = "ANY";
    private Option TYPE_TITLE = new Option(TITLE, bundle.getString("adv_search_lblRgbTitle"));
    private Option TYPE_TOPIC = new Option(TOPIC, bundle.getString("adv_search_lblRgbTopic"));
    private Option TYPE_ANY = new Option(ANY, bundle.getString("adv_search_lblRgbAny"));
    private Option[] TYPE_OPTIONS = new Option[]{TYPE_TITLE, TYPE_TOPIC, TYPE_ANY};
    
    /** 
     * Creates a panel with a text input and a combo box with 3 items. 
     * @param st reference of search type
     */
    public AnyFieldUIMask( SearchTypeUI st )
    {
        super( st );
        
        this.panel1.setId(CommonUtils.createUniqueId(this.panel1));
        this.panel1.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "searchTerm"));
        this.lblSearchStringAnyField.setId(CommonUtils.createUniqueId(this.lblSearchStringAnyField));
        this.lblSearchStringAnyField.setValue(bundle.getString("adv_search_lblSearchTerm"));
        this.txtSearchStringAnyField.setId(CommonUtils.createUniqueId(this.txtSearchStringAnyField));
        this.txtSearchStringAnyField.setImmediate(true);
        this.panel1.getChildren().add(lblSearchStringAnyField);
        this.panel1.getChildren().add(txtSearchStringAnyField);
        this.panel1.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel(this.panel1);
        
        this.panel2.setId(CommonUtils.createUniqueId(this.panel2));
        this.panel2.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "formGroupTitle"));
//        this.lblCboLanguage.setId(CommonUtils.createUniqueId(this.lblCboLanguage));
//        this.lblCboLanguage.setValue(bundle.getString("adv_search_lblLanguage"));
//        // disable the language text for now
//        this.lblCboLanguage.setRendered(false);
//        this.panel2.getChildren().add(this.lblCboLanguage);
//        this.cboLanguage.setId(CommonUtils.createUniqueId(this.cboLanguage));
//        this.cboLanguage.setItems(CommonUtils.getLanguageOptions());
//        this.cboLanguage.setImmediate(true); 
//        // disable the language box for now
//        this.cboLanguage.setRendered(false);
//        this.panel2.getChildren().add(this.cboLanguage);
        this.rbgType.setId(CommonUtils.createUniqueId(this.rbgType));
        this.rbgType.setItems(TYPE_OPTIONS);
        this.rbgType.setSelected("TITLE");
        this.rbgType.setImmediate(true);
        //language option only for title, otherwise disable drop down box
        //include file checkbox only for any field radiobutton, otherwise disable checkbox
        this.rbgType.setOnChange("updateAnyFieldMask(); return false");
        this.panel2.getChildren().add(this.rbgType);
        
        this.chkIncludeFiles.setId(CommonUtils.createUniqueId(this.chkIncludeFiles));
        this.chkIncludeFiles.setImmediate(true);
        this.chkIncludeFiles.setValue(bundle.getString("adv_search_lblChkInclude"));
        this.chkIncludeFiles.setDisabled(true);
        this.panel2.getChildren().add(this.chkIncludeFiles);
        this.txtIncludeFiles.setId(CommonUtils.createUniqueId(this.txtIncludeFiles));
        this.txtIncludeFiles.setValue(bundle.getString("adv_search_lblChkInclude"));
        this.panel2.getChildren().add(this.txtIncludeFiles);

        this.panel2.getChildren().add(htmlElement.getEndTag("div"));
        this.addPanelToCommonPanel(this.panel2);
        
        // finally add the buttons 
        this.addButtonsAndLogicOperatorToPanel();
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.search.ui.UIMask#clearForm()
     */
    @Override
    void clearForm()
    {
        this.txtSearchStringAnyField.setValue("");
//        this.cboLanguage.setSelected("0");
        this.rbgType.setSelected("TITLE");
        this.chkIncludeFiles.setSelected(false);
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.search.ui.UIMask#getCriterion()
     */
    @Override
    CriterionVO getCriterionFromArrays()
    {
        if (this.getRbgType().getSelected().toString().equals(TITLE))
        {
            TitleCriterionVO titleCriterionVO = new TitleCriterionVO();
            titleCriterionVO.setSearchString((String)this.getTxtSearchStringAnyField().getText());
//            titleCriterionVO.setLanguage(this.getCboLanguage().getSelected().toString());
            
            return titleCriterionVO;
        }
        else if (this.getRbgType().getSelected().toString().equals(TOPIC))
        {
            TopicCriterionVO topicCriterionVO = new TopicCriterionVO();
            topicCriterionVO.setSearchString((String)this.getTxtSearchStringAnyField().getText());
//            topicCriterionVO.setLanguage(this.getCboLanguage().getSelected().toString());
           
            return topicCriterionVO;
        }
        else
        {
            AnyFieldCriterionVO anyFieldCriterionVO = new AnyFieldCriterionVO();
            anyFieldCriterionVO.setSearchString((String)this.getTxtSearchStringAnyField().getText());
            if (this.getChkIncludeFiles().isSelected())
            {
                anyFieldCriterionVO.setIncludeFiles(true);                        
            }
            else
            {
                anyFieldCriterionVO.setIncludeFiles(false);                        
            }
           
            return anyFieldCriterionVO;
        }
    }
    
    @Override
    public boolean hasData()
    {
        String searchString = (String)getTxtSearchStringAnyField().getText();
        if( searchString != null && searchString.length() > 0 ) 
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public RadioButtonGroup getRbgType()
    {
        return rbgType;
    }

    public TextField getTxtSearchStringAnyField()
    {
        return txtSearchStringAnyField;
    }

    public HtmlSelectBooleanCheckbox getChkIncludeFiles()
    {
        return chkIncludeFiles;
    }
    
    @Override
    public void refreshAppearance()
    {
    	// refresh the buttons
    	super.refreshAppearanceButtonsAndOp();
    	
    	// language specific stuff
    	this.lblSearchStringAnyField.setValue(bundle.getString("adv_search_lblSearchTerm"));
    	this.txtIncludeFiles.setValue(bundle.getString("adv_search_lblChkInclude"));
    	
    	this.TYPE_TITLE.setLabel( bundle.getString("adv_search_lblRgbTitle") );
    	this.TYPE_TOPIC.setLabel( bundle.getString("adv_search_lblRgbTopic") );
    	this.TYPE_ANY.setLabel( bundle.getString("adv_search_lblRgbAny") );
    	
    	
    	// refresh if the 'include files' checkbox is visible
        if (this.getRbgType().getSelected().toString().equals(ANY))
          {
        	// combobox for query language 
//              this.getCboLanguage().setDisabled(true);
              this.getChkIncludeFiles().setDisabled(false);
          }
          else
          {
        	  // combobox for query language
//              this.getCboLanguage().setDisabled(false);
              this.getChkIncludeFiles().setDisabled(true);
          }
    }

}
