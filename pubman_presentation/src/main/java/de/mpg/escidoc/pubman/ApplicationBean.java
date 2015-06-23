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
* or http://www.escidoc.org/license.
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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.exceptions.PubManStylesheetNotAvailableException;
import de.mpg.escidoc.pubman.exceptions.PubManVersionNotAvailableException;
import de.mpg.escidoc.pubman.util.CreatorVOPresentation;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.util.SourceVOPresentation;
import de.mpg.escidoc.services.citationmanager.utils.XsltHelper;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;

/**
 * ApplicationBean which stores all application wide values.
 *
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 09.08.2007
 */
public class ApplicationBean extends FacesBean
{
    /** system type enum */
    public enum SystemType {
        /** profile for developer machines */
        Workstation,
        /** profile for the development test server */
        Dev_Server,
        /** profile for the demo server */
        Test_Server,
        /** profile for the qa server */
        QA_Server,
        /** profile for the production server */
        Production_Server
    }
    public static final String BEAN_NAME = "ApplicationBean";
    public static final String DEFAULT_STYLESHEET_URL = "";
    private static Logger logger = Logger.getLogger(ApplicationBean.class);

    private final String APP_TITLE = "Publication Manager";
    /** system type of this application instance */
    private SystemType systemType;
    private String appTitle = null;
    private String version = null;
    private String appContext = "";
    
    /** filename of the ear-internal property file */ 
    private static final String PROPERTY_FILENAME = "solution.properties";
    
    /** Stylesheet distinguation */
    private static final String STANDARD_STYLESHEET = "stylesheet";
    private static final String ALTERNATE_STYLESHEET = "alternate stylesheet";
    
    /** Initialization of the Transformation Service*/
    private Transformation transformationService;
    
    private Map<String, SelectItem[]> languageSelectItems;
    private Map<String, String> contentCategoryMap;
    private Map<String, String> excludedSourceGenreMap;
    private Map<String, String> creatorRoleMap;

    private Set<AffiliationVO> ouList = new HashSet<AffiliationVO>();
    private String instanceContextPath;
	private String footerSnippet;
	private String pubmanInstanceUrl;
	private String commonPresentationUrl;
	private String pubmanBlogFeedUrl;
	private String pubmanStyleTags;
	private String cookieVersion;
	private String logoUrl;
	private String additionalLogoCss;
	private boolean handlesActivated;
	private String shortVersion;
    

    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        // set the system type of the application
        try
        {
            this.systemType = fetchSystemTypeFromProperty();
        } catch (PubManVersionNotAvailableException e)
        {
           logger.warn("System type is not retrievable! Setting now to PRODUCTION");
           this.systemType=SystemType.Production_Server;
        }
        this.transformationService = new TransformationBean();
        this.languageSelectItems = new HashMap<String, SelectItem[]>();
        this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
        this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
        this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();
       
        loadProperties();
        
        this.init();
    }

    /**
     * This method is called when this bean is initially added to application scope.
     * Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into application scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        // 
    }
    
    public void loadProperties()
    {
    	 try {
    		 
    		 
    		Properties solProperties = CommonUtils.getProperties( PROPERTY_FILENAME );
            this.version = solProperties.getProperty("escidoc.pubman.version");
            
            this.shortVersion = "";
            int whereToCut;
            try
            {
                shortVersion = solProperties.getProperty("escidoc.pubman.version");
                // get the position of the first blank before the word 'build'
                whereToCut = shortVersion.indexOf(" ");
                shortVersion = shortVersion.substring(0, whereToCut + 1);
            }
            catch (Exception e)
            {
                logger.warn("The version of the application cannot be retrieved.");
            }

            
            this.appTitle = getLabel("Pubman_browserTitle");
            // hide the version information if system type is production
            if(!this.fetchSystemTypeFromProperty().equals(SystemType.Production_Server) && this.version!=null)
            {
            	this.appTitle += " " + this.getVersion();
            }
            
            this.pubmanInstanceUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");

            
            
            
            this.commonPresentationUrl = PropertyReader.getProperty("escidoc.pubman.common.presentation.url");
            if (this.commonPresentationUrl == null)
            {
            	this.commonPresentationUrl = "";
            }
          
            
            this.pubmanBlogFeedUrl = PropertyReader.getProperty("escidoc.pubman.blog.news");
            if (this.pubmanBlogFeedUrl == null)
            {
            	this.pubmanBlogFeedUrl = "";
            }
            
            try {
				this.pubmanStyleTags = buildPubmanStyleTags();
			} catch (Exception e) {
				logger.error("Error while building style tags", e);
			}

           
            this.cookieVersion = PropertyReader.getProperty("escidoc.pubman.cookie.version");
            if (this.cookieVersion == null)
            {
            	this.cookieVersion = "";
            }
            
            this.instanceContextPath = PropertyReader.getProperty("escidoc.pubman.instance.context.path");
            
            this.appContext = PropertyReader.getProperty("escidoc.pubman.instance.context.path") + "/faces/";
            
    		this.logoUrl = PropertyReader.getProperty("escidoc.pubman.logo.url");
    		
    		this.additionalLogoCss = PropertyReader.getProperty("escidoc.pubman.logo.css");
			if (this.additionalLogoCss == null)
	        {
	        	this.additionalLogoCss = "";
	        }
            
			try 
			{
				this.handlesActivated = Boolean.parseBoolean(PropertyReader.getProperty("escidoc.handles.activated"));
			} catch (Exception e) {
				logger.error("Error reading property 'escidoc.handles.activated'", e);
				this.handlesActivated = false;
			} 
			
			
			String footerFileName = PropertyReader.getProperty("escidoc.pubman.footer.fileName"); 
			try {
				if(footerFileName!= null && !footerFileName.isEmpty())
				{
					this.footerSnippet = ResourceUtil.getResourceAsString(footerFileName, this.getClass().getClassLoader()); 
				}
			} catch (Exception e) {
				logger.error("Error while reading footer file: " + footerFileName);
			}
			
	
			
			
			
		} catch (Exception e) {
			logger.error("Error while reading properties", e);
		}
    }

    /**
     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer
     * Faces view. If no more suitable encoding can be found, return "UTF-8" as a general purpose default.
     * The default implementation uses the implementation from our superclass, FacesBean.
     *
     * @return the local character encoding
     */
    public String getLocaleCharacterEncoding()
    {
        return System.getProperty("file.encoding"); // super.getLocaleCharacterEncoding();
    }

    /**
     * Returns the title and version of the application, shown in the header.
     *
     * @return applicationtitle, including version
     */
    public String getAppTitle()
    {
        return appTitle;
    }

    /**
     * Provides the escidoc version string.
     *
     * @return the escidoc version
     * @throws PubManVersionNotAvailableException if escidoc version can not be retrieved.
     */
    private String getVersion()
    {
    	return version;
        
    }
    
    /**
     * Provides the escidoc version string without build date.
     *
     * @return the escidoc version without build date
     * @throws PubManVersionNotAvailableException if escidoc version can not be retrieved.
     */
    public String getShortVersion()
    {

        return shortVersion;
    }
    
    /**
     * Provides the escidoc instance string.
     *
     * @return the escidoc instance
     * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
     */
    public String getPubmanInstanceUrl() throws PubManVersionNotAvailableException 
    {
       return this.pubmanInstanceUrl;
        
    }
    
    /**
     * Provides the url for common-presentation.
    *
    */
   public String getCommonPresentationUrl()
   {
       return this.commonPresentationUrl;
   }
    
    /**
     * Provides the url for the pubman blog feed.
    *
    * @return the escidoc instance
    * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
    */
   public String getPubmanBlogFeedUrl()
   {
       return this.pubmanBlogFeedUrl;
   }
    
    /**
     * Provides the URLs of the pubman stylsheets.
     *
     * @return the escidoc instance
     */
    public String buildPubmanStyleTags() throws PubManStylesheetNotAvailableException
    {
        StringBuffer styleTags = new StringBuffer();
        String StylesheetStandard = "";
        String StylesheetContrast = "";
        String StylesheetClassic = "";
        String StylesheetSpecial = "";
        
        // First append the standard PubMan Stylesheet
        try {
            if("true".equals(PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.apply")))
            {
                if(PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.type").equals(ALTERNATE_STYLESHEET))
                {
                    styleTags.append("<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.url") +"\" id=\"Standard\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.type") +"\"/>");
                }
                else
                {
                    StylesheetStandard = "<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.url") +"\" id=\"Standard\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.type") +"\"/>";
                }
            }
        
        
            // Then append the high contrast Stylesheet

            if("true".equals(PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.apply")))
            {
                if(PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.type").equals(ALTERNATE_STYLESHEET))
                {
                    styleTags.append("<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.url") +"\" id=\"HighContrast\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.type") +"\"/>");
                }
                else
                {
                    StylesheetContrast = "<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.url") +"\" id=\"HighContrast\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.type") +"\"/>";
                }
            }
        
        
            // Then append the classic Stylesheet
        
            if("true".equals(PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.apply")))
            {
                if(PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.type").equals(ALTERNATE_STYLESHEET))
                {
                    styleTags.append("<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.url") +"\" id=\"Classic\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.type") +"\"/>");
                }
                else
                {
                    StylesheetClassic = "<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.url") +"\" id=\"Classic\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.type") +"\"/>";
                }
            }
        
        
            // Then append the special Stylesheet
       
            if("true".equals(PropertyReader.getProperty("escidoc.pubman.stylesheet.special.apply")))
            {
                if(PropertyReader.getProperty("escidoc.pubman.stylesheet.special.type").equals(ALTERNATE_STYLESHEET))
                {
                    styleTags.append("<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.special.url") +"\" id=\"Special\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.special.type") +"\"/>");
                }
                else
                {
                    StylesheetSpecial = "<link href=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.special.url") +"\" id=\"Special\" type=\"text/css\" title=\""+ ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") +"\" rel=\""+ PropertyReader.getProperty("escidoc.pubman.stylesheet.special.type") +"\"/>";
                }
            }
        } catch (Exception e)
        {
            throw new PubManStylesheetNotAvailableException(e);
        }
        
        // then append the stylesheet String variables (no matter if empty) to ensure that the stylesheet with the standard rel tag is the last entry in the list.
        
        styleTags.append(StylesheetStandard);
        styleTags.append(StylesheetContrast);
        styleTags.append(StylesheetClassic);
        styleTags.append(StylesheetSpecial);
        
        // Last Step: add Favicon information if it should be applied
        try {
            if("true".equals(PropertyReader.getProperty("escidoc.pubman.favicon.apply")))
            {
            	styleTags.append("<link rel=\"shortcut icon\" type=\"image/png\" href=\"" + PropertyReader.getProperty("escidoc.pubman.favicon.url") + "\"/>");
            }
        } catch (IOException e)
        {
            throw new PubManStylesheetNotAvailableException(e);
        } catch (URISyntaxException e)
        {
            throw new PubManStylesheetNotAvailableException(e);
        }
        return styleTags.toString();
        
    }
    
    public String getPubmanStyleTags() 
    {
    	return this.pubmanStyleTags;
    }
    
    
    /**
     * This method returns the cookie version for PubMan hold in the  pubman.properties
     * @return String cookie version for PubMan
     * @throws PubManVersionNotAvailableException
     */
    public String getCookieVersion() throws PubManVersionNotAvailableException 
    {
        return this.cookieVersion;
        

        
    }
    

    /**
     * Returns the current application context.
     *
     * @return the application context
     */
    public String getAppContext()
    {
        
        return appContext;
    }
    
    /**
     * Sets the application context.
     *
     * @param appContext the new application context
     */
    public void setAppContext(String appContext)
    {
        this.appContext = appContext;
    }

    /**
     * Returns the current instance context path.
     *
     * @return the instance context path
     */
    public String getInstanceContextPath()
    {
        return instanceContextPath;
    }

    /**
     * Sets the instance context path.
     *
     * @param newInstanceContextPath the new instance context path
     */
    public void setInstanceContextPath(String newInstanceContextPath)
    {
        this.instanceContextPath = newInstanceContextPath;
    }
    
    /**
     * Provides the escidoc instance string.
     *
     * @return the escidoc instance
     * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
     */
    public SystemType fetchSystemTypeFromProperty() throws PubManVersionNotAvailableException 
    {
        String sysType;
        try
        {
            sysType = PropertyReader.getProperty("escidoc.systemtype");
        } catch (IOException e)
        {
            throw new PubManVersionNotAvailableException(e);
        } catch (URISyntaxException e)
        {
            throw new PubManVersionNotAvailableException(e);
        }
        
        if( sysType.equals("workstation") )
        {
            return SystemType.Workstation;
        }
        else if( sysType.equals("dev") )
        {
            return SystemType.Dev_Server;
        }
        else if( sysType.equals("qa") )
        {
            return SystemType.QA_Server;
        }
        else if( sysType.equals("test") )
        {
            return SystemType.Test_Server;
        }
        else if( sysType.equals("production") )
        {
            return SystemType.Production_Server;
        }
        else throw new PubManVersionNotAvailableException("SystemType Property unsupported!");
    }
    
    public boolean getCheckSystemTypeProduction()
    {
        if(systemType == SystemType.Production_Server) 
            return true;
        else 
            return false;
    }
    
    public String getReloadResourceBundlesAndProperties() throws Exception
    {
        String returnVal = "";
        ResourceBundle.clearCache();
        PropertyReader.loadProperties();
        loadProperties();
        languageSelectItems.clear();
        ouList.clear();
        
        //Renew Journal CitationStyles for JUS Exports
        XsltHelper.getJournalsXML();
        
        returnVal = "... Resource bundles and properties reloaded, language selection menu reset, Journal citation styles from CoNE reloaded.";
        return returnVal;
        
        
    }
    

    public Transformation getTransformationService()
    {
        return this.transformationService;
    }

    public void setTransformationService(Transformation transformationService)
    {
        this.transformationService = transformationService;
    }

    public void setLanguageSelectItems(Map<String, SelectItem[]> languageSelectItems) {
        this.languageSelectItems = languageSelectItems;
    }

    public Map<String, SelectItem[]> getLanguageSelectItems() {
        return languageSelectItems;
    }

    public Set<AffiliationVO> getOuList() {
        return ouList;
    }

    public void setOuList(Set<AffiliationVO> ouList) {
        this.ouList = ouList;
    }
    
    public String getLogoUrl()
    {
    	return this.logoUrl;
    	
    }
    
    public String getAdditionalLogoCss()
    {
    	
    	return this.additionalLogoCss;
    }
    
    /**
     * @return the application wide contentCategoryMap
     */
    public synchronized Map<String, String> getContentCategoryMap()
    {
        if (this.contentCategoryMap == null)
        {
            this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
        }
        return this.contentCategoryMap;
    }
    
    /**
     * @return the application wide excludedSourceGenreMap
     */
    public synchronized Map<String, String> getExcludedSourceGenreMap()
    {
        if (this.excludedSourceGenreMap == null)
        {
            this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
        }
        return this.excludedSourceGenreMap;
    }

    /**
     * @return the creatorRoleMap
     */
    public synchronized Map<String, String> getCreatorRoleMap()
    {
        if (this.creatorRoleMap == null)
        {
            this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();
        }
        return this.creatorRoleMap;
    }

	public boolean isHandlesActivated() {
		return this.handlesActivated;
	}

	public String getFooterSnippet() {
		return footerSnippet;
	}

	public void setFooterSnippet(String footerSnippet) {
		this.footerSnippet = footerSnippet;
	}

	
}
