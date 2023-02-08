/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import jakarta.faces.bean.ApplicationScoped;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.ManagedProperty;
import jakarta.faces.model.SelectItem;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.utils.XsltHelper;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManStylesheetNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.SourceVOPresentation;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * ApplicationBean which stores all application wide values.
 * 
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 09.08.2007
 */
@ManagedBean(name = "ApplicationBean")
@ApplicationScoped
public class ApplicationBean extends FacesBean {

  /**
   * 
   */
  private static final long serialVersionUID = -3659975702163802434L;

  /** system type enum */
  public enum SystemType
  {
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

  private static final Logger logger = Logger.getLogger(ApplicationBean.class);

  //  private static final String ALTERNATE_STYLESHEET = "alternate stylesheet";

  public static ApplicationBean INSTANCE;

  private Map<String, SelectItem[]> languageSelectItems;
  private Map<String, String> contentCategoryMap;
  private Map<String, String> creatorRoleMap;
  private Map<String, String> excludedSourceGenreMap;

  private Set<AffiliationDbVO> ouList = new HashSet<AffiliationDbVO>();

  private String additionalLogoCss;
  private String appContext = "";
  private String appTitle = null;
  private String cookieVersion;
  private String cslEditorInstanceUrl;
  private String footerSnippet;
  private String instanceContextPath;
  private String logoUrl;
  private String pubmanBlogFeedUrl;
  private String pubmanInstanceUrl;
  private String pubmanRootAuthorsIcon;
  private String pubmanRootOrganizationName;
  private String pubmanStyleTags;
  private String version = null;
  private String buildDate = null;

  private boolean pidHandleActivated;

  @ManagedProperty("#{contextServiceDbImpl}")
  private ContextService contextService;

  @ManagedProperty("#{restDataSource}")
  private DataSource dataSource;

  @ManagedProperty("#{fileServiceFSImpl}")
  private FileService fileService;

  @ManagedProperty("#{itemValidatingService}")
  private ItemValidatingService itemValidatingService;

  @ManagedProperty("#{organizationServiceDbImpl}")
  private OrganizationService organizationService;

  @ManagedProperty("#{pubItemServiceDbImpl}")
  private PubItemService pubItemService;

  @ManagedProperty("#{searchAndExportServiceImpl}")
  private SearchAndExportService searchAndExportService;

  @ManagedProperty("#{userAccountServiceImpl}")
  private UserAccountService userAccountService;

  @ManagedProperty("#{mpgJsonIpListProvider}")
  private IpListProvider ipListProvider;

  @ManagedProperty("#{itemTransformingServiceImpl}")
  private ItemTransformingService itemTransformingService;

  public ApplicationBean() {
    this.languageSelectItems = new HashMap<String, SelectItem[]>();
    this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
    this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
    this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();

    ApplicationBean.INSTANCE = this;

    this.loadProperties();
  }

  /**
   * Provides the URL of the pubman stylesheet.
   * 
   * @return the URL
   */
  private String buildPubmanStyleTags() throws PubManStylesheetNotAvailableException {
    final StringBuffer styleTags = new StringBuffer();
    //    String StylesheetStandard = "";
    //    String StylesheetContrast = "";
    //    String StylesheetClassic = "";
    //    String StylesheetSpecial = "";
    String stylesheet = "";

    // First append the standard PubMan Stylesheet
    try {
      //      if ("true".equals(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_APPLY))) {
      //        if (PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_TYPE).equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
      //          styleTags.append("<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_URL)
      //              + "\" id=\"Standard\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_TYPE) + "\"/>");
      //        } else {
      //          StylesheetStandard = "<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_URL)
      //              + "\" id=\"Standard\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_STANDARD_TYPE) + "\"/>";
      //        }
      //      }

      // Then append the high contrast Stylesheet

      //      if ("true".equals(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_APPLY))) {
      //        if (PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_TYPE).equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
      //          styleTags.append("<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_URL)
      //              + "\" id=\"HighContrast\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast")
      //              + "\" rel=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_TYPE) + "\"/>");
      //        } else {
      //          StylesheetContrast = "<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_URL)
      //              + "\" id=\"HighContrast\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast")
      //              + "\" rel=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CONTRAST_TYPE) + "\"/>";
      //        }
      //      }

      // Then append the classic Stylesheet

      //      if ("true".equals(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_APPLY))) {
      //        if (PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_TYPE).equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
      //          styleTags.append("<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_URL)
      //              + "\" id=\"Classic\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_TYPE) + "\"/>");
      //        } else {
      //          StylesheetClassic = "<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_URL)
      //              + "\" id=\"Classic\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_CLASSIC_TYPE) + "\"/>";
      //        }
      //      }

      // Then append the special Stylesheet

      //      if ("true".equals(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_SPECIAL_APPLY))) {
      //        if (PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_SPECIAL_TYPE).equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
      //          styleTags.append("<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_SPECIAL_URL)
      //              + "\" id=\"Special\" type=\"text/css\" title=\""
      //              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_SPECIAL_TYPE) + "\"/>");
      //        } else {
      //      StylesheetSpecial = "<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_URL)
      //          + "\" id=\"Special\" type=\"text/css\" title=\""
      //          + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") + "\" rel=\""
      //              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_SPECIAL_TYPE) + "\"/>";
      //        }
      //      }
      stylesheet = "<link href=\"" + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_URL)
          + "\" type=\"text/css\" rel=\"stylesheet\"/>";
    } catch (final Exception e) {
      throw new PubManStylesheetNotAvailableException(e);
    }

    // then append the stylesheet String variables (no matter if empty) to ensure that the
    // stylesheet with the standard rel tag is the last entry in the list.

    //    styleTags.append(StylesheetStandard);
    //    styleTags.append(StylesheetContrast);
    //    styleTags.append(StylesheetClassic);
    //    styleTags.append(StylesheetSpecial);
    styleTags.append(stylesheet);

    // Last Step: add Favicon information if it should be applied

    if ("true".equals(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_FAVICON_APPLY))) {
      styleTags.append("<link rel=\"shortcut icon\" type=\"image/png\" href=\""
          + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_FAVICON_URL) + "\"/>");
    }

    return styleTags.toString();
  }

  /**
   * Provides the escidoc instance string.
   * 
   * @return the escidoc instance
   * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
   */
  public SystemType getSystemTypeFromProperty() throws PubManVersionNotAvailableException {
    final String sysType = PropertyReader.getProperty(PropertyReader.INGE_SYSTEMTYPE);

    if (sysType.equals("workstation")) {
      return SystemType.Workstation;
    } else if (sysType.equals("dev")) {
      return SystemType.Dev_Server;
    } else if (sysType.equals("qa")) {
      return SystemType.QA_Server;
    } else if (sysType.equals("test")) {
      return SystemType.Test_Server;
    } else if (sysType.equals("production")) {
      return SystemType.Production_Server;
    } else {
      throw new PubManVersionNotAvailableException("SystemType Property unsupported!");
    }
  }

  public String getAdditionalLogoCss() {
    return this.additionalLogoCss;
  }

  public String getAppContext() {
    return this.appContext;
  }

  /**
   * Returns the title and version of the application, shown in the header.
   * 
   * @return applicationtitle, including version
   */
  public String getAppTitle() {
    return this.appTitle;
  }

  public synchronized Map<String, String> getContentCategoryMap() {
    if (this.contentCategoryMap == null) {
      this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
    }

    return this.contentCategoryMap;
  }

  public ContextService getContextService() {
    return this.contextService;
  }

  public String getCookieVersion() {
    return this.cookieVersion;
  }

  public synchronized Map<String, String> getCreatorRoleMap() {
    if (this.creatorRoleMap == null) {
      this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();
    }

    return this.creatorRoleMap;
  }

  public String getCslEditorInstanceUrl() {
    return this.cslEditorInstanceUrl;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public synchronized Map<String, String> getExcludedSourceGenreMap() {
    if (this.excludedSourceGenreMap == null) {
      this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
    }

    return this.excludedSourceGenreMap;
  }

  public FileService getFileService() {
    return this.fileService;
  }

  public String getFooterSnippet() {
    return this.footerSnippet;
  }

  public String getInstanceContextPath() {
    return this.instanceContextPath;
  }

  public ItemValidatingService getItemValidatingService() {
    return this.itemValidatingService;
  }

  public Map<String, SelectItem[]> getLanguageSelectItems() {
    return this.languageSelectItems;
  }

  public String getLogoUrl() {
    return this.logoUrl;
  }

  public OrganizationService getOrganizationService() {
    return this.organizationService;
  }

  public Set<AffiliationDbVO> getOuList() {
    return this.ouList;
  }

  public PubItemService getPubItemService() {
    return this.pubItemService;
  }

  public SearchAndExportService getSearchAndExportService() {
    return this.searchAndExportService;
  }

  public String getPubmanBlogFeedUrl() {
    return this.pubmanBlogFeedUrl;
  }

  public String getPubmanInstanceUrl() {
    return this.pubmanInstanceUrl;
  }

  public String getPubmanRootAuthorsIcon() {
    return this.pubmanRootAuthorsIcon;
  }

  public String getPubmanRootOrganizationName() {
    return this.pubmanRootOrganizationName;
  }

  public String getPubmanStyleTags() {
    return this.pubmanStyleTags;
  }

  public String getReloadResourceBundlesAndProperties() throws Exception {
    ResourceBundle.clearCache();
    PropertyReader.forceReloadProperties();
    this.loadProperties();
    this.languageSelectItems.clear();
    this.ouList.clear();

    // Renew Journal CitationStyles for JUS Exports
    XsltHelper.getJournalsXML();

    return "... Resource bundles and properties reloaded, language selection menu reset, Journal citation styles from CoNE reloaded.";
  }


  public UserAccountService getUserAccountService() {
    return this.userAccountService;
  }

  public boolean isPidHandleActivated() {
    return this.pidHandleActivated;
  }

  private void loadProperties() {
    try {
      Properties versionProperties = new Properties();
      versionProperties.load(ResourceUtil.getResourceAsStream("/pubman-version.txt", ApplicationBean.class.getClassLoader()));
      this.version = versionProperties.getProperty("version");
      this.buildDate = versionProperties.getProperty("build.date");

      this.appTitle = this.getLabel("Pubman_browserTitle");
      // hide the version information if system type is production
      if (!this.getSystemTypeFromProperty().equals(SystemType.Production_Server) && this.getVersion() != null) {
        this.appTitle += " " + this.getVersion() + " " + buildDate;
      }

      this.pubmanInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);

      this.pubmanBlogFeedUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_BLOG_NEWS);
      if (this.pubmanBlogFeedUrl == null) {
        this.pubmanBlogFeedUrl = "";
      }

      try {
        this.pubmanStyleTags = this.buildPubmanStyleTags();
      } catch (final Exception e) {
        ApplicationBean.logger.error("Error while building style tags", e);
      }

      this.cookieVersion = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COOKIE_VERSION);
      if (this.cookieVersion == null) {
        this.cookieVersion = "";
      }

      this.instanceContextPath = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);

      this.appContext = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH) + "/faces/";

      this.logoUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_LOGO_URL);

      this.additionalLogoCss = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_LOGO_CSS);
      if (this.additionalLogoCss == null) {
        this.additionalLogoCss = "";
      }

      this.pidHandleActivated = Boolean.parseBoolean(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_ACTIVATED));

      final String footerFileName = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_FOOTER_FILENAME);
      try {
        if (footerFileName != null && !footerFileName.isEmpty()) {
          this.footerSnippet = ResourceUtil.getResourceAsString(footerFileName, this.getClass().getClassLoader());
        }
      } catch (final Exception e) {
        ApplicationBean.logger.error("Error while reading footer file: " + footerFileName);
      }

      this.cslEditorInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_CSL_EDITOR_INSTANCE);

      this.pubmanRootAuthorsIcon = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_AUTHORS_ICON);

      this.pubmanRootOrganizationName = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_NAME);
    } catch (final Exception e) {
      ApplicationBean.logger.error("Error while reading properties", e);
    }
  }

  public void setAppContext(String appContext) {
    this.appContext = appContext;
  }

  public void setContextService(ContextService contextService) {
    this.contextService = contextService;
  }

  public void setCslEditorInstanceUrl(String cslEditorInstanceUrl) {
    this.cslEditorInstanceUrl = cslEditorInstanceUrl;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  public void setFooterSnippet(String footerSnippet) {
    this.footerSnippet = footerSnippet;
  }

  public void setInstanceContextPath(String newInstanceContextPath) {
    this.instanceContextPath = newInstanceContextPath;
  }

  public void setItemValidatingService(ItemValidatingService itemValidatingService) {
    this.itemValidatingService = itemValidatingService;
  }

  public void setLanguageSelectItems(Map<String, SelectItem[]> languageSelectItems) {
    this.languageSelectItems = languageSelectItems;
  }

  public void setOrganizationService(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  public void setOuList(Set<AffiliationDbVO> ouList) {
    this.ouList = ouList;
  }

  public void setPubItemService(PubItemService pubItemService) {
    this.pubItemService = pubItemService;
  }

  public void setSearchAndExportService(SearchAndExportService searchAndExportService) {
    this.searchAndExportService = searchAndExportService;
  }

  public void setUserAccountService(UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  public IpListProvider getIpListProvider() {
    return ipListProvider;
  }

  public void setIpListProvider(IpListProvider ipListProvider) {
    this.ipListProvider = ipListProvider;
  }

  public ItemTransformingService getItemTransformingService() {
    return itemTransformingService;
  }

  public void setItemTransformingService(ItemTransformingService itemTransformingService) {
    this.itemTransformingService = itemTransformingService;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
