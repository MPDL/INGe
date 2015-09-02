/**
 * 
 */
package de.mpg.escidoc.services.citation_style_language_manager;

import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLType;

/**
 * @author walter
 * 
 */
public class MetadataProvider implements ItemDataProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.undercouch.citeproc.ItemDataProvider#getIds()
	 */
	@Override
	public String[] getIds() {
		String ids[] = { "ID-0", "ID-1", "ID-2" };
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.undercouch.citeproc.ItemDataProvider#retrieveItem(java.lang.String)
	 */
	@Override
	public CSLItemData retrieveItem(String id) {
		CSLItemData item = new CSLItemDataBuilder().id(id)
				.type(CSLType.ARTICLE_JOURNAL).title("A Test Title")
				.author("Matthias", "Walter").issued(2013, 9, 6)
				.containerTitle("Dummy journal").build();
		return item;
	}

}
