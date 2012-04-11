package de.mpg.escidoc.pubman.util;

import java.util.Comparator;

import javax.faces.model.SelectItem;

/**
 * Can be used to order a list of SelectItems by their names
 * @author haarlae1
 *
 */
public class SelectItemComparator implements Comparator<SelectItem> {

	public int compare(SelectItem si1, SelectItem si2) {
		
		if(si1.getLabel()!=null && si2.getLabel()!=null)
		{
			return si1.getLabel().toLowerCase().compareTo(si2.getLabel().toLowerCase());
		}
		return 0;
		
	}

}
