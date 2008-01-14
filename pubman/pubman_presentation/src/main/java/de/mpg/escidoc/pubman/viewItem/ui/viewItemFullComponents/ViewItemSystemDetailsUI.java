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

package de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.releases.ReleasesSessionBean;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * UI for creating the details section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 11.09.2007
 * @version: $Revision: 1609 $ $LastChangedDate: 2007-11-26 18:21:32 +0100 (Mon, 26 Nov 2007) $
 */
public class ViewItemSystemDetailsUI extends HtmlPanelGroup
{
    
    
    
    private static Logger logger = Logger.getLogger(ViewItemSystemDetailsUI.class);
    private PubItemVO pubItem;
    private PubCollectionVO pubCollection = null;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlCommandButton btnViewRevisions = new HtmlCommandButton();
    private HtmlCommandButton btnViewReleaseHistory = new HtmlCommandButton();
    private HtmlCommandButton btnViewItemStatistics = new HtmlCommandButton();

    private final String PROPERTY_PREFIX_FOR_VIEWSTATISTICS_DISABLED = "escidoc.pubman_presentation.disable.view_statistics";
    
    
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    private static final String FUNCTION_VIEW_REVISIONS = "view_revisions";
    
    /**
     * Public constructor.
     */
    public ViewItemSystemDetailsUI(PubItemVO pubItemVO)
    {
        initialize(pubItemVO);
    }
    
    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * 
     * @param pubItemVO a pubitem
     */
    protected void initialize(PubItemVO pubItemVO)
    {
        this.pubItem = pubItemVO;
        
        Application application = FacesContext.getCurrentInstance().getApplication();
        
        // get the selected language...
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        ApplicationBean applicationBean = (ApplicationBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ApplicationBean.BEAN_NAME);
        
        boolean isViewRevisionsDisabled = this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + ViewItemSystemDetailsUI.FUNCTION_VIEW_REVISIONS);
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        this.btnViewRevisions = new HtmlCommandButton();
        this.btnViewRevisions.setId(CommonUtils.createUniqueId(this.btnViewRevisions));
        
        this.btnViewRevisions.setValue(bundleLabel.getString("ViewItemFull_btnItemRevisions"));
        //this.btnViewRevisions.addActionListener(this);
        this.btnViewRevisions.setImmediate(true);
        this.btnViewRevisions.setType("submit");
        this.btnViewRevisions.setAction(application.createMethodBinding("#{viewItem$viewItemFull.showRevisions}", null));
        this.btnViewRevisions.setDisabled(false);
        this.btnViewRevisions.setRendered(!isViewRevisionsDisabled);
        
        this.btnViewReleaseHistory = new HtmlCommandButton();
        this.btnViewReleaseHistory.setId(CommonUtils.createUniqueId(this.btnViewRevisions));
        
        this.btnViewReleaseHistory.setValue(bundleLabel.getString("ViewItemFull_btnItemVersions"));
        //this.btnViewRevisions.addActionListener(this);
        this.btnViewReleaseHistory.setImmediate(true);
        this.btnViewReleaseHistory.setType("submit");
        
        // TODO ScT: remove when the procedure of handling release history button is fully clarified
        // examine if the button should be disbaled if no releases are available
        /*if(this.getReleasesSessionBean().getReleaseList() != null)
        {
            if(this.getReleasesSessionBean().getReleaseList().size() > 0)
            {
                this.btnViewReleaseHistory.setDisabled(false);
            }
            else
            {
                this.btnViewReleaseHistory.setDisabled(true);
                this.btnViewReleaseHistory.setStyleClass("inlineButtonDisabled");
            }
        }
        else
        {
            this.btnViewReleaseHistory.setDisabled(true);
        }*/
        
        this.btnViewReleaseHistory.setAction(application.createMethodBinding("#{viewItem$viewItemFull.showReleaseHistory}", null));
 
        this.btnViewItemStatistics = new HtmlCommandButton();
        this.btnViewItemStatistics.setId(CommonUtils.createUniqueId(this.btnViewItemStatistics));
        this.btnViewItemStatistics.setValue(bundleLabel.getString("ViewItemFull_btnItemStatistics"));
        this.btnViewItemStatistics.setOnclick("openCenteredWindow('ViewItemStatisticsPage.jsp', 650, 350, 'Item statistics')");
        this.btnViewItemStatistics.setImmediate(true);
        this.btnViewItemStatistics.setType("submit");
        try
        {
            if ("false".equals(PropertyReader.getProperty(PROPERTY_PREFIX_FOR_VIEWSTATISTICS_DISABLED)))
                    this.btnViewItemStatistics.setRendered(true);
            else 
                    this.btnViewItemStatistics.setRendered(false);

        } catch (Exception e)
        {
            logger.warn("Propertyfile not readable for view item statistics property'");
        }
               
        
        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/bt_nb1_22xy22_cont.gif");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        // add the subheader
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblSubHeaderSystemDetails")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getMetadata() != null)
        {
            // *** ITEM VERSION ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblVersion")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblVersion") + " " + this.pubItem.getReference().getVersionNumber() + bundleLabel.getString("ViewItemFull_lblReleased") + " " + CommonUtils.format(this.pubItem.getModificationDate())));
            
            this.getChildren().add(this.btnViewReleaseHistory);
            this.getChildren().add(this.btnViewItemStatistics);

            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** NAME OF THE COLLECTION ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblnameOfCollection")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getCollectionName()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** AFFILIATIONS ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblIsAffiliatedTo")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getAffiliations()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** DATE OF LAST MODIFICATION ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblLastModified")));
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(CommonUtils.format(this.pubItem.getModificationDate())));
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** INTELLECTUAL REVISION ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblItemRevisions")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            if(isViewRevisionsDisabled == true)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            }
            else
            {
                this.getChildren().add(this.btnViewRevisions);
            }
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** ITEM STATE ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblItemState")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            // Check if item is withdrawn
            if(this.pubItem.getState() != null && this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
            {
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd withdrawn"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblWithdrawn")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
            else
            {
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getState() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(applicationBean.convertEnumToString(this.pubItem.getState())));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
            
            // *** CITE ITEM AS ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblCiteItemAs")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getPid()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** IDENTIFIERS ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblSystemIdentifiers")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getReference().getObjectId()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
        }
    }
    
    
    /**
     * Gets the name of the Collection the item belongs to.
     * 
     * @return String formatted Collection name
     */
    private String getCollectionName()
    {
        String collectionName="";
        if(this.pubCollection == null)
        {
            ItemControllerSessionBean itemControllerSessionBean = getItemControllerSessionBean();
            try
            {
                this.pubCollection = itemControllerSessionBean.retrieveCollection(this.pubItem.getPubCollection().getObjectId());
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
        
        if(this.pubCollection != null)
        {
            collectionName = this.pubCollection.getName();
        }
        return collectionName;
    }
    
    /**
     * Gets the affiliation of the Collection the item belongs to.
     * 
     * @return String formatted Collection name
     */
    private String getAffiliations()
    {
        StringBuffer affiliations = new StringBuffer();
        List<AffiliationRO> affiliationRefList = new ArrayList<AffiliationRO>();
        List<AffiliationVO> affiliationList = new ArrayList<AffiliationVO>();
        ItemControllerSessionBean itemControllerSessionBean = getItemControllerSessionBean();
        
        if(this.pubCollection == null)
        {
            try
            {
                this.pubCollection = itemControllerSessionBean.retrieveCollection(this.pubItem.getPubCollection().getObjectId());
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
        
        if(this.pubCollection != null)
        {
            affiliationRefList = this.pubCollection.getResponsibleAffiliations();
        }
        // first get all affiliations
        if(affiliationRefList != null)
        {
            for(int i = 0; i < affiliationRefList.size(); i++)
            {
                try
                {
                    affiliationList.add(itemControllerSessionBean.retrieveAffiliation(affiliationRefList.get(i).getObjectId()));
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
            }
        }
        
        // then extract the names and add to StringBuffer
        for(int i = 0; i < affiliationList.size(); i++)
        {
            affiliations.append(affiliationList.get(i).getName());
            if(i < affiliationList.size() - 1)
            {
                affiliations.append(", ");
            }
        }
        return affiliations.toString();
    }

    
    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        ItemControllerSessionBean itemControllerSessionBean = (ItemControllerSessionBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ItemControllerSessionBean.BEAN_NAME);
        return itemControllerSessionBean;
    }
    
    /**
     * Returns the RightsManagementSessionBean.
     * @author DiT
     * @return a reference to the scoped data bean (RightsManagementSessionBean)
     */
    protected RightsManagementSessionBean getRightsManagementSessionBean()
    {
        return(RightsManagementSessionBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                RightsManagementSessionBean.BEAN_NAME);
    }
    
    /**
     * Returns the ReleasesSessionBean.
     * 
     * @return a reference to the scoped data bean (ReleasesSessionBean)
     */
    protected ReleasesSessionBean getReleasesSessionBean()
    {
        return(ReleasesSessionBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ReleasesSessionBean.BEAN_NAME);
    }
}