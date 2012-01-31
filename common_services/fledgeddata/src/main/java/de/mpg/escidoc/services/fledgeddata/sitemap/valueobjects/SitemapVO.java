/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata.sitemap.valueobjects;

import java.util.ArrayList;

/**
 * 
 * @author kleinfe1
 *
 */
public class SitemapVO
{
	private ArrayList<SitemapEntryVO> entries;
	
	public SitemapVO()
	{
		entries = new ArrayList<SitemapEntryVO>();
	}
	
	public void addEntry(SitemapEntryVO entry)
	{
		this.entries.add(entry);
	}
	
	public void emptySitemap()
	{
		this.entries.clear();
	}

	public ArrayList<SitemapEntryVO> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<SitemapEntryVO> entries) {
		this.entries = entries;
	}
}