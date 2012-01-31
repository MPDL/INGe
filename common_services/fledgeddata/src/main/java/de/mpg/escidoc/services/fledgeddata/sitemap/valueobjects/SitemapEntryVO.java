/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata.sitemap.valueobjects;

/**
 * 
 * @author kleinfe1
 *
 */
public class SitemapEntryVO
{
	private String url_loc;  			//Provides the full URL of the page, including the protocol
	private String url_lastmod;			//The date that the file was last modified, in ISO 8601 format.
	private String url_changefreq;		//How frequently the page may change
	private String url_prio;			//The priority of that URL relative to other URLs on the site. The valid range is from 0.0 to 1.0, with 1.0 being the most important

	public SitemapEntryVO (String url_loc, String url_lastmod, String url_changefreq, String url_prio)
	{
		this.url_loc = url_loc;
		this.url_lastmod = url_lastmod;
		this.url_changefreq = url_changefreq;
		this.url_prio = url_prio;
	}

	
	public String getUrl_loc() {
		return url_loc;
	}

	public void setUrl_loc(String url_loc) {
		this.url_loc = url_loc;
	}

	public String getUrl_lastmod() {
		return url_lastmod;
	}

	public void setUrl_lastmod(String url_lastmod) {
		this.url_lastmod = url_lastmod;
	}

	public String getUrl_changefreq() {
		return url_changefreq;
	}

	public void setUrl_changefreq(String url_changefreq) {
		this.url_changefreq = url_changefreq;
	}

	public String getUrl_prio() {
		return url_prio;
	}

	public void setUrl_prio(String url_prio) {
		this.url_prio = url_prio;
	}
	
}