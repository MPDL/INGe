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

package de.mpg.escidoc.pubman.editItem.ui;

import java.util.Calendar;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Button;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.Label;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI component for editing content languages. 
 * 
 * @author: Thomas Diebäcker, created 05.06.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 07.08.2007
 */
public class ContentLanguageUI extends HtmlPanelGrid
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ContentLanguageUI.class);
    private Application application = FacesContext.getCurrentInstance().getApplication();
    protected PubItemVO pubItem = null;
    protected int indexContentLanguage = 0;
    
    //For handling the resource bundles (i18n)    
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication()
    .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    // GUI components
    protected Label lblContentLanguage = new Label();
    protected DropDown cboContentLanguage = new DropDown();    
    protected Button btAdd = new Button();
    protected Button btRemove = new Button();   
    
    /**
     * Public constructor.
     * Initializes the UI.
     * @param pubItem the pubItem that is being edited
     * @param indexContentLanguage index of the contentLanguage in PubItemVO
     */
    public ContentLanguageUI(PubItemVO pubItem, int indexContentLanguage)
    {
        this.pubItem = pubItem;
        this.indexContentLanguage = indexContentLanguage;
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();       
        
        i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication()
        .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        // set attributes for all GUI components
        this.setId(viewRoot.createUniqueId() + "_ContentLanguageUI" + Calendar.getInstance().getTimeInMillis());
        this.setColumns(4);
        this.setCellspacing("0");
        this.setCellpadding("0");
        this.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemButtonColumn, editItemButtonColumn");
        
        this.lblContentLanguage.setId(viewRoot.createUniqueId() + "_lblContentLanguage" + Calendar.getInstance().getTimeInMillis());
        this.lblContentLanguage.setValue(labelBundle.getString("EditItem_lblContentLanguage"));
        this.lblContentLanguage.setFor(this.cboContentLanguage.getId());
        this.lblContentLanguage.setLabelLevel(3);
        this.getChildren().add(this.lblContentLanguage);

        this.cboContentLanguage.setId(viewRoot.createUniqueId() + "_cboContentLanguage" + Calendar.getInstance().getTimeInMillis());
        this.cboContentLanguage.setStyleClass("editItemComboBoxLanguage");      
        this.cboContentLanguage.setItems(CommonUtils.getLanguageOptions());
        this.cboContentLanguage.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.languages[" + (this.indexContentLanguage) + "]}"));
        this.getChildren().add(this.cboContentLanguage);

        this.btAdd.setId(viewRoot.createUniqueId() + "_btAdd" + Calendar.getInstance().getTimeInMillis());
        this.btAdd.setValue(labelBundle.getString("EditItem_btAdd"));
        this.btAdd.setStyleClass("editDynamicButton");
        this.btAdd.setImmediate(true);
        this.btAdd.setActionListener(this.application.createMethodBinding("#{editItem$EditItem.addContentLanguage}", new Class[]{ActionEvent.class}));
        this.getChildren().add(this.btAdd);
        
        this.btRemove.setId(viewRoot.createUniqueId() + "_btRemove" + Calendar.getInstance().getTimeInMillis());
        this.btRemove.setValue(labelBundle.getString("EditItem_btRemove"));
        this.btRemove.setStyleClass("editDynamicButton");
        this.btRemove.setImmediate(true);
        this.btRemove.setActionListener(this.application.createMethodBinding("#{editItem$EditItem.removeContentLanguage}", new Class[]{ActionEvent.class}));
        this.btRemove.setVisible(this.isRemoveButtonVisible());
        this.getChildren().add(this.btRemove);        
    }
    
    /**
     * Determines if the remove button should be visible. 
     * The remove button should only be invisble if there is only one component (it should be possible to remove the 
     * first component, too if there are others following (see PUBMAN-110)).
     * As ContentLanguageUI does not inherit AbstractUI so far, this method has to implemented seperatly.
     * @return true if the remove button should be visible, otherwise false
     */
    public boolean isRemoveButtonVisible()
    {       
        return (this.indexContentLanguage != 0 || this.getPubItem().getMetadata().getLanguages().size() > 1);        
    }

    /**
     * Returns the language that is currently set in the UI.
     * @return the current language
     */
    public String getLanguage()
    {
        return CommonUtils.getUIValue(this.cboContentLanguage);
    }
    
    public PubItemVO getPubItem()
    {
        return pubItem;
    }

    public void setPubItem(PubItemVO pubItem)
    {
        this.pubItem = pubItem;
    }

    public int getIndexContentLanguage()
    {
        return indexContentLanguage;
    }

    public void setIndexContentLanguage(int indexContentLanguage)
    {
        this.indexContentLanguage = indexContentLanguage;

        // ValueBinding has to be set with the new index
        this.cboContentLanguage.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.metadata.language[" + (this.indexContentLanguage) + "]}"));
    }
    
    public Button getRemoveButton()
    {
        return this.btRemove;
    }
}
