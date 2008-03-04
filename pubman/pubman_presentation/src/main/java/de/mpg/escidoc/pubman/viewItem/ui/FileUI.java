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

package de.mpg.escidoc.pubman.viewItem.ui;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;

/**
 * UI component for listing files in the view item page.
 * 
 * @author: Tobias Schraut, created 10.01.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 17.08.2007
 */

public class FileUI 
{
	private HtmlPanelGrid panGrid = new HtmlPanelGrid();
	private HtmlCommandLink lnkName = new HtmlCommandLink();
	private HtmlOutputText valFileSize = new HtmlOutputText();
	private UIParameter param = new UIParameter();
	private HtmlOutputText valFileContentType = new HtmlOutputText();
	private HtmlOutputText valFileVisibility = new HtmlOutputText();
	private HtmlOutputText lblFileDescription = new HtmlOutputText();
	private HtmlOutputText valFileDescription = new HtmlOutputText();
	private HtmlOutputText lblFileCitation = new HtmlOutputText();
	private HtmlOutputText valFileCitation = new HtmlOutputText();
	private HtmlOutputText emptySpace = new HtmlOutputText();
	
	private HtmlCommandButton btnDownload = new HtmlCommandButton();

	/**
	 * Public constructor. Initializes the UI.
	 * 
	 * @param file
	 *            to be added to the list
	 * @param position
	 *            index of the item in the corresponding file list
	 */
	public FileUI(PubFileVO file, int position) {
		initialize(file, position);
	}

	/**
	 * Initializes the UI and sets all attributes of the GUI components.
	 * 
	 * @param file
	 *            to be added to the list
	 * @param position
	 *            index of the item in the corresponding file list
	 */
	protected void initialize(PubFileVO file, int position) {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		if (viewRoot == null) {
			viewRoot = new UIViewRoot();
		}
		Application application = FacesContext.getCurrentInstance().getApplication();
		// get the selected language...
		InternationalizationHelper i18nHelper = (InternationalizationHelper) FacesContext
				.getCurrentInstance().getApplication().getVariableResolver()
				.resolveVariable(FacesContext.getCurrentInstance(),	InternationalizationHelper.BEAN_NAME);
		// ... and set the refering resource bundle
		ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
		this.panGrid = new HtmlPanelGrid();
		this.panGrid.setId(CommonUtils.createUniqueId(this.panGrid));
		this.panGrid.setBorder(0);
		this.panGrid.setColumns(2);
		this.panGrid.setWidth("100%");
		this.panGrid.setColumnClasses("viewItemColumnLeft,viewItemColumnRight");
		this.panGrid.setRowClasses("viewItemRow");
		if (file != null) 
		{
			this.param.setId(CommonUtils.createUniqueId(this.param));
			this.param.setName("fileID");
			this.param.setValue(file.getReference().getObjectId());
			this.btnDownload.getChildren().add(this.param);
			this.lnkName.setId(CommonUtils.createUniqueId(this.lnkName));
			this.lnkName.setValue(file.getName());
			this.lnkName.setStyle("height: 20px; width: 360px");
			this.lnkName.setImmediate(true);
			this.lnkName.setOnclick("downloadFile(" + position + "); return false");
			this.panGrid.getChildren().add(this.lnkName);
			this.valFileSize.setId(CommonUtils.createUniqueId(this.valFileSize));
			BigDecimal fileSize = new BigDecimal(file.getSize()).divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_UP);
			this.valFileSize.setValue(" (" + fileSize.toString() + "KB)");
			this.valFileSize.setStyle("height: 20px; width: 360px");
			this.valFileSize.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.valFileSize);
			this.valFileContentType.setId(CommonUtils.createUniqueId(this.valFileContentType));
			this.valFileContentType.setValue(file.getContentType().name());
			this.valFileContentType.setStyle("height: 20px; width: 360px");
			this.valFileContentType.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.valFileContentType);
			this.valFileVisibility.setId(CommonUtils.createUniqueId(this.valFileVisibility));
			this.valFileVisibility.setValue(bundle.getString("ViewItem_lblFileVisibility")
					+ file.getVisibility().name());
			this.valFileVisibility.setStyle("height: 20px; width: 360px");
			this.valFileVisibility.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.valFileVisibility);
			this.lblFileDescription.setId(CommonUtils.createUniqueId(this.lblFileDescription));
			this.lblFileDescription.setValue(bundle.getString("ViewItem_lblFileDescription"));
			this.lblFileDescription.setStyle("height: 20px; width: 360px");
			this.lblFileDescription.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.lblFileDescription);
			this.valFileDescription.setId(CommonUtils.createUniqueId(this.valFileDescription));
			if (file.getDescription() != null) {
				this.valFileDescription.setValue(file.getDescription());
			} else {
				this.valFileDescription.setValue("");
			}
			this.valFileDescription.setStyle("height: 20px; width: 360px");
			this.valFileDescription.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.valFileDescription);
			this.lblFileCitation.setId(CommonUtils.createUniqueId(this.lblFileCitation));
			this.lblFileCitation.setValue(bundle.getString("ViewItem_lblFileCitation"));
			this.lblFileCitation.setStyle("height: 20px; width: 360px");
			this.lblFileCitation.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.lblFileCitation);
			this.valFileCitation.setId(CommonUtils.createUniqueId(this.valFileCitation));
			this.valFileCitation.setValue(file.getPid());
			this.valFileCitation.setStyle("height: 20px; width: 360px");
			this.valFileCitation.setStyleClass("valueMetadata");
			this.panGrid.getChildren().add(this.valFileCitation);
			this.btnDownload.setId("btn_" + position);
			this.btnDownload.setValue("Download...");
			this.btnDownload.setActionListener(application.createMethodBinding(
					"#{ViewItem.generateDownloadLink}",
					new Class[] { ActionEvent.class }));
			this.btnDownload.setRendered(false);
			this.panGrid.getChildren().add(this.btnDownload);
		} 
		else 
		{
			this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
			this.emptySpace.setValue(" ");
			this.emptySpace.setStyle("height: 20px; width: 360px");
			this.panGrid.getChildren().add(this.emptySpace);
		}
	}

	public UIComponent getUIComponent() 
	{
		return this.panGrid;
	}
}
