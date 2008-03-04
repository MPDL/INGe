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

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;

import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Represents a Checkbox and a output text ui element.
 * @author endres
 *
 */
public class CheckBoxLabelUI extends InternationalizedImpl {
	
	/** language bundle identifier */
	private String langBundleIdent = null;
	/** checkbox */
	private HtmlSelectBooleanCheckbox checkbox = null;
	/** checkbox label */
	private HtmlOutputText outputText = null;
	
	/**
	 * Created a new instance.
	 * @param checkboxIdent  identifier for the checkbox
	 * @param langBundleIdent  key for the language bundle
	 * @param bundle  bundle which holds the current language information
	 */
	public CheckBoxLabelUI( String checkboxIdent, String langBundleIdent )
	{
		this.langBundleIdent = new String( langBundleIdent );
		
		this.checkbox = new HtmlSelectBooleanCheckbox();
		this.checkbox.setId( CommonUtils.createUniqueId( this.checkbox ) );
		this.checkbox.setValue( checkboxIdent );
		this.checkbox.setImmediate(true);
		this.outputText = new HtmlOutputText();
		this.outputText.setId( CommonUtils.createUniqueId( this.outputText ) );
		this.outputText.setValue( getLabel( langBundleIdent ) );
	}

	/**
	 * Add the UI elements to a panel.
	 * @param panel panel to add the UI elements
	 */
	public void addToPanel( HtmlPanelGroup panel ) 
	{
		panel.getChildren().add( this.checkbox );
		panel.getChildren().add( this.outputText );
	}
	
	/**
	 * Updates the language of the outputtext
	 * @param bundle  bundle which holds the current language information
	 */
	public void updateLanguage( ResourceBundle bundle )
	{
		this.outputText.setValue(getLabel( this.langBundleIdent ) );
	}
	
	/**
	 * Returns whether a checkbox is selected or not
	 * @return true is selected, false is unselected
	 */
	public boolean isSelected()
	{
		return this.checkbox.isSelected();
	}
	
	/**
	 * Set the checkbox selected or unselected
	 * @param selected  true selected, false unselected
	 */
	public void setSelected( boolean selected )
	{
		this.checkbox.setSelected( selected );
	}
}
