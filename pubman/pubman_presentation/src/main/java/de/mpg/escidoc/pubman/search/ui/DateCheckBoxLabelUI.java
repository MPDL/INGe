/**
 * 
 */
package de.mpg.escidoc.pubman.search.ui;

import java.util.ResourceBundle;

import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO.DateType;

/**
 * @author endres
 *
 */
public class DateCheckBoxLabelUI extends CheckBoxLabelUI {
	
	/** genre */
	private DateType myDateType;
	
	/**
	 * Cretes a new instance.
	 * @param dt date type
	 * @param langBundleIdent language bundle identifier 
	 * @param bundle language bundle
	 */
	public DateCheckBoxLabelUI( DateType dt, String langBundleIdent)
	{
		super( dt.toString(), langBundleIdent );
		this.myDateType = dt;
	}
	
	/**
	 * Get genre
	 * @return genre
	 */
	public DateType getDateType() 
	{
		return this.myDateType;
	}

}
