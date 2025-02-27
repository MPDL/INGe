/*
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
package de.mpg.mpdl.inge.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for reading properties from the global escidoc property file.
 *
 * This class tries to locate the properties in various ways. Once the properties file has been read
 * it is cached. The following steps are executed to find a properties file:
 * <ul>
 * <li>First the location of the properties file is determined by looking for the system property
 * <code>pubman.properties.file</code>. This property can be used to set the path to the properties
 * file that should be used. If this property is not set the default file path
 * <code>pubman.properties</code> is used.
 * <li>Second step is to read the properties file: First we try to read the properties file from the
 * local file system. If it cannot be found, it is searched in the classpath.
 * </ul>
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2011-09-30 11:15:01 +0200 (Fri, 30 Sep 2011) $
 * @revised by BrP: 03.09.2007
 */
public class PropertyReader {
  private static final Logger logger = LogManager.getLogger(PropertyReader.class);

  public static final String INGE_AA_CLIENT_CLASS = "inge.aa.client.class";
  public static final String INGE_AA_CLIENT_FINISH_CLASS = "inge.aa.client.finish.class";
  public static final String INGE_AA_CLIENT_LOGOUT_CLASS = "inge.aa.client.logout.class";
  public static final String INGE_AA_CLIENT_START_CLASS = "inge.aa.client.start.class";
  public static final String INGE_AA_DEFAULT_TARGET = "inge.aa.default.target";
  public static final String INGE_AA_INSTANCE_URL = "inge.aa.instance.url";
  public static final String INGE_AA_PRIVATE_KEY_FILE = "inge.aa.private.key.file";
  public static final String INGE_AA_PUBLIC_KEY_FILE = "inge.aa.public.key.file";
  public static final String INGE_AUTH_MPG_IP_LIST_USE = "inge.auth.mpg.ip.use";
  public static final String INGE_AUTH_MPG_JSON_IP_LIST_URL = "inge.auth.mpg.ip.json.url";
  public static final String INGE_CLUSTER_NODE_NAME = "inge.cluster.node.name";
  public static final String INGE_CONE_CACHE_USE = "inge.cone.cache.use";
  public static final String INGE_CONE_DATABASE_DRIVER_CLASS = "inge.cone.database.driver.class";
  public static final String INGE_CONE_DATABASE_NAME = "inge.cone.database.name";
  public static final String INGE_CONE_DATABASE_SERVER_NAME = "inge.cone.database.server.name";
  public static final String INGE_CONE_DATABASE_SERVER_PORT = "inge.cone.database.server.port";
  public static final String INGE_CONE_DATABASE_USER_NAME = "inge.cone.database.user.name";
  public static final String INGE_CONE_DATABASE_USER_PASSWORD = "inge.cone.database.user.password";
  public static final String INGE_CONE_EXTENDED_ATTRIBUTES_USE = "inge.cone.extended.attributes.use";
  public static final String INGE_CONE_LANGUAGE_DEFAULT = "inge.cone.language.default";
  public static final String INGE_CONE_MODELSXML_PATH = "inge.cone.modelsxml.path";
  public static final String INGE_CONE_PERSON_ID_IDENTIFIER = "inge.cone.person.id.identifier";
  public static final String INGE_CONE_QUERIER_CLASS = "inge.cone.querier.class";
  public static final String INGE_CONE_RDFS_TEMPLATE = "inge.cone.rdfs.template";
  public static final String INGE_CONE_RESULTS_DEFAULT = "inge.cone.results.default";
  public static final String INGE_CONE_RESULTS_MAXIMUM = "inge.cone.results.maximum";
  public static final String INGE_CONE_SERVICE_URL = "inge.cone.service.url";
  public static final String INGE_CONE_SUBJECTVOCAB = "inge.cone.subjectVocab";
  public static final String INGE_CRON_CLEANUP_STAGING_FILES = "inge.cron.cleanup_staging_files";
  public static final String INGE_CRON_CLEANUP_STAGING_FILES_HOURS = "inge.cron.cleanup_staging_files.hours";
  public static final String INGE_CRON_FIX_BROKEN_IMPORTS = "inge.cron.fix_broken_imports";
  public static final String INGE_CRON_FIX_BROKEN_IMPORTS_HOURS = "inge.cron.fix_broken_imports.hours";
  public static final String INGE_CRON_INIT_IP_LIST = "inge.cron.init_ip_list";
  public static final String INGE_CRON_INIT_SEARCH_INDEX_FIELDS = "inge.cron.init_search_index_fields";
  public static final String INGE_CRON_PUBMAN_SITEMAP = "inge.cron.pubman.sitemap";
  public static final String INGE_CRON_REFRESH_CONE_CACHE = "inge.cron.refresh_cone_cache";
  public static final String INGE_CROSSREF_PID = "inge.crossref.pid";
  public static final String INGE_CSL_JAVASCRIPT_ENGINE = "inge.csl.javascript.engine";
  public static final String INGE_DATABASE_DRIVER_CLASS = "inge.database.driver.class";
  public static final String INGE_DATABASE_JDBC_URL = "inge.database.jdbc.url";
  public static final String INGE_DATABASE_USER_NAME = "inge.database.user.name";
  public static final String INGE_DATABASE_USER_PASSWORD = "inge.database.user.password";
  public static final String INGE_DOI_SERVICE_CREATE_URL = "inge.doi.service.create.url";
  public static final String INGE_DOI_SERVICE_PASSWORD = "inge.doi.service.password";
  public static final String INGE_DOI_SERVICE_URL = "inge.doi.service.url";
  public static final String INGE_DOI_SERVICE_USE = "inge.doi.service.use";
  public static final String INGE_DOI_SERVICE_USER = "inge.doi.service.user";
  public static final String INGE_EMAIL_ALTERNATIVE_MODERATOR = "inge.email.alternative.moderator";
  public static final String INGE_EMAIL_AUTHENTICATIONPWD = "inge.email.authenticationpwd";
  public static final String INGE_EMAIL_AUTHENTICATIONUSER = "inge.email.authenticationuser";
  public static final String INGE_EMAIL_MAILSERVERNAME = "inge.email.mailservername";
  public static final String INGE_EMAIL_SENDER = "inge.email.sender";
  public static final String INGE_EMAIL_WITHAUTHENTICATION = "inge.email.withauthentication";
  public static final String INGE_ES_PASSWORD = "inge.es.password";
  public static final String INGE_ES_REST_HOST_PORT = "inge.es.rest.host-port";
  public static final String INGE_ES_REST_PATH_PREFIX = "inge.es.rest.path-prefix";
  public static final String INGE_ES_USER = "inge.es.user";
  public static final String INGE_FILESTORAGE_FILESYSTEM_PATH = "inge.filestorage.filesystem_path";
  public static final String INGE_FILESTORAGE_OAI_FILESYSTEM_PATH = "inge.filestorage.oai.filesystem_path";
  public static final String INGE_FILESTORAGE_SEAWEED_DIRECT_SUBMIT_PATH = "inge.filestorage.seaweed_direct_submit_path";
  public static final String INGE_FILESTORAGE_SEAWEED_MASTER_SERVER_IP = "inge.filestorage.seaweed_master_server_ip";
  public static final String INGE_ID_TYPES_TO_DISPLAY = "inge.id.types.to.display";
  public static final String INGE_IMPORT_SOURCES_XML = "inge.import.sources.xml";
  public static final String INGE_INDEX_CONTEXT_NAME = "inge.index.context.name";
  public static final String INGE_INDEX_CONTEXT_SORT = "inge.index.context.sort";
  public static final String INGE_INDEX_CONTEXT_TYPE = "inge.index.context.type";
  public static final String INGE_INDEX_ITEM_NAME = "inge.index.item.name";
  public static final String INGE_INDEX_ITEM_SORT = "inge.index.item.sort";
  public static final String INGE_INDEX_ITEM_TYPE = "inge.index.item.type";
  public static final String INGE_INDEX_ORGANIZATION_NAME = "inge.index.organization.name";
  public static final String INGE_INDEX_ORGANIZATION_SORT = "inge.index.organization.sort";
  public static final String INGE_INDEX_ORGANIZATION_TYPE = "inge.index.organization.type";
  public static final String INGE_INDEX_USER_NAME = "inge.index.user.name";
  public static final String INGE_INDEX_USER_SORT = "inge.index.user.sort";
  public static final String INGE_INDEX_USER_TYPE = "inge.index.user.type";
  public static final String INGE_JWT_SHARED_SECRET = "inge.jwt.shared-secret";
  public static final String INGE_LOGIC_TEMPORARY_FILESYSTEM_ROOT_PATH = "inge.logic.temporary_filesystem_root_path";
  public static final String INGE_PID_HANDLE_ACTIVATED = "inge.pid.handle.activated";
  public static final String INGE_PID_HANDLE_SHORT = "inge.pid.handle.short";
  public static final String INGE_PID_HANDLE_URL = "inge.pid.handle.url";
  public static final String INGE_PID_SERVICE_CREATE_PATH = "inge.pid.service.create.path";
  public static final String INGE_PID_SERVICE_PASSWORD = "inge.pid.service.password";
  public static final String INGE_PID_SERVICE_TIMEOUT = "inge.pid.service.timeout";
  public static final String INGE_PID_SERVICE_URL = "inge.pid.service.url";
  public static final String INGE_PID_SERVICE_USE = "inge.pid.service.use";
  public static final String INGE_PID_SERVICE_USER = "inge.pid.service.user";
  public static final String INGE_PUBMAN_BLOG_NEWS = "inge.pubman.blog.news";
  public static final String INGE_PUBMAN_COMPONENT_PATTERN = "inge.pubman.component.pattern";
  public static final String INGE_PUBMAN_COOKIE_VERSION = "inge.pubman.cookie.version";
  public static final String INGE_PUBMAN_CSL_EDITOR_INSTANCE = "inge.pubman.csl_editor.instance";
  public static final String INGE_PUBMAN_EXTERNAL_ORGANISATION_ID = "inge.pubman.external.organisation.id";
  public static final String INGE_PUBMAN_FAVICON_APPLY = "inge.pubman.favicon.apply";
  public static final String INGE_PUBMAN_FAVICON_URL = "inge.pubman.favicon.url";
  public static final String INGE_PUBMAN_FOOTER_FILENAME = "inge.pubman.footer.fileName";
  public static final String INGE_PUBMAN_GENRES_CONFIGURATION = "inge.pubman.genres.configuration";
  public static final String INGE_PUBMAN_HELP_URL = "inge.pubman.help.url";
  public static final String INGE_PUBMAN_HOME_CONTENT_URL = "inge.pubman.home.content.url";
  public static final String INGE_PUBMAN_INSTANCE_CONTEXT_PATH = "inge.pubman.instance.context.path";
  public static final String INGE_PUBMAN_INSTANCE_SSRN_CONTEXTS = "inge.pubman.instance.ssrn_contexts";
  public static final String INGE_PUBMAN_INSTANCE_URL = "inge.pubman.instance.url";
  public static final String INGE_PUBMAN_ITEM_PATTERN = "inge.pubman.item.pattern";
  public static final String INGE_PUBMAN_LOGO_CSS = "inge.pubman.logo.css";
  public static final String INGE_PUBMAN_LOGO_URL = "inge.pubman.logo.url";
  public static final String INGE_PUBMAN_POLICY_URL = "inge.pubman.policy.url";
  public static final String INGE_PUBMAN_PRESENTATION_URL = "inge.pubman.presentation.url";
  public static final String INGE_PUBMAN_PRESENTATION_VIEWFULLITEM_DEFAULTSIZE = "inge.pubman_presentation.viewFullItem.defaultSize";
  public static final String INGE_PUBMAN_PRIVACY_URL = "inge.pubman.privacy.url";
  public static final String INGE_PUBMAN_ROOT_AUTHORS_ICON = "inge.pubman.root.authors.icon";
  public static final String INGE_PUBMAN_ROOT_ORGANISATION_ID = "inge.pubman.root.organisation.id";
  public static final String INGE_PUBMAN_ROOT_ORGANIZATION_NAME = "inge.pubman.root.organization.name";
  public static final String INGE_PUBMAN_SITEMAP_MAX_ITEMS = "inge.pubman.sitemap.max.items";
  public static final String INGE_PUBMAN_SITEMAP_RETRIEVE_ITEMS = "inge.pubman.sitemap.retrieve.items";
  public static final String INGE_PUBMAN_STYLESHEET_URL = "inge.pubman.stylesheet.url";
  public static final String INGE_REST_API_DESCRIPTION = "inge.rest.api.description";
  public static final String INGE_REST_DEVELOPMENT_ADMIN_PASSWORD = "inge.rest.development.admin.password";
  public static final String INGE_REST_DEVELOPMENT_ADMIN_USERNAME = "inge.rest.development.admin.username";
  public static final String INGE_REST_DEVELOPMENT_ENABLED = "inge.rest.development.enabled";
  public static final String INGE_REST_DEVELOPMENT_FILE_URL = "inge.rest.development.file_url";
  public static final String INGE_REST_SERVICE_URL = "inge.rest.service.url";
  public static final String INGE_SEARCH_AND_EXPORT_DEFAULT_QUERY = "inge.search.and.export.default.query";
  public static final String INGE_SEARCH_AND_EXPORT_DEFAULT_SORT_KEY = "inge.search.and.export.default.sort.key";
  public static final String INGE_SEARCH_AND_EXPORT_DEFAULT_SORT_ORDER = "inge.search.and.export.default.sort.order";
  public static final String INGE_SEARCH_AND_EXPORT_MAXIMUM_RECORDS = "inge.search.and.export.maximum.records";
  public static final String INGE_SEARCH_AND_EXPORT_MAX_LIMIT = "inge.search.and.export.max.limit";
  public static final String INGE_SEARCH_AND_EXPORT_START_RECORD = "inge.search.and.export.start.record";
  public static final String INGE_SYSTEMTYPE = "inge.systemtype";
  public static final String INGE_TRANSFORMATION_ARXIV2ESCIDOC_PUBLICATION_COMPONENT_STYLESHEET_FILENAME =
      "inge.transformation.arxiv2escidoc_publication_component.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ARXIV2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME =
      "inge.transformation.arxiv2escidoc_publication_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_BIBTEX_CONFIGURATION_FILENAME = "inge.transformation.bibtex.configuration.filename";
  public static final String INGE_TRANSFORMATION_BMC2ESCIDOC_CONFIGURATION_FILENAME =
      "inge.transformation.bmc2escidoc.configuration.filename";
  public static final String INGE_TRANSFORMATION_BMC2ESCIDOC_PUBLICATION_COMPONENT_STYLESHEET_FILENAME =
      "inge.transformation.bmc2escidoc_publication_component.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_BMC2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME =
      "inge.transformation.bmc2escidoc_publication_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_BMC2ESCIDOC_STYLESHEET_FILENAME = "inge.transformation.bmc2escidoc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_BMC_FULLTEXT_XML2BMC_FULLTEXT_HTML_STYLESHEET_FILENAME =
      "inge.transformation.bmc_fulltext_xml2bmc_fulltext_html.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_CROSSREF2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME =
      "inge.transformation.crossref2escidoc_publication_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_DOI_STYLESHEET_FILENAME = "inge.transformation.doi.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_EDOC_CONFIGURATION_FILENAME = "inge.transformation.edoc.configuration.filename";
  public static final String INGE_TRANSFORMATION_EDOC_STYLESHEET_FILENAME = "inge.transformation.edoc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ENDNOTE_CONFIGURATION_FILENAME = "inge.transformation.endnote.configuration.filename";
  public static final String INGE_TRANSFORMATION_ENDNOTE_ICE_STYLESHEET_FILENAME = "inge.transformation.endnote.ice.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ENDNOTE_STYLESHEET_FILENAME = "inge.transformation.endnote.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC2BIBTEX_STYLESHEET_FILENAME =
      "inge.transformation.escidoc2bibtex.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC2EDOC_EXPORT_STYLESHEET_FILENAME =
      "inge.transformation.escidoc2edoc_export.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC2EDOC_IMPORT_STYLESHEET_FILENAME =
      "inge.transformation.escidoc2edoc_import.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC2ENDNOTE_STYLESHEET_FILENAME =
      "inge.transformation.escidoc2endnote.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC2MARCXML_STYLESHEET_FILENAME =
      "inge.transformation.escidoc2marcxml.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC_ITEMLIST_TO_SNIPPET_STYLESHEET_FILENAME =
      "inge.transformation.escidoc.itemlist.to.snippet.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC_SNIPPET_TO_HTML_STYLESHEET_FILENAME =
      "inge.transformation.escidoc.snippet.to.html.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC_V1_TO_ESCIDOC_V2_STYLESHEET_FILENAME =
      "inge.transformation.escidoc_v1_to_escidoc_v2.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC_V2_TO_ESCIDOC_V1_STYLESHEET_FILENAME =
      "inge.transformation.escidoc_v2_to_escidoc_v1.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ESCIDOC_V3_TO_ESCIDOC_V2_STYLESHEET_FILENAME =
      "inge.transformation.escidoc_v3_to_escidoc_v2.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_HTML_METATAGS_DC_STYLESHEET_FILENAME =
      "inge.transformation.html_metatags_dc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_HTML_METATAGS_HIGHWIRE_STYLESHEET_FILENAME =
      "inge.transformation.html_metatags_highwire.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_JUS_INDESIGN_STYLESHEET_FILENAME = "inge.transformation.jus_indesign.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_JUS_SNIPPET2JUS_STYLESHEET_FILENAME =
      "inge.transformation.jus_snippet2jus.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_MAB_CONTENT_URL_PREFIX = "inge.transformation.mab.content.url.prefix";
  public static final String INGE_TRANSFORMATION_MAB_STYLESHEET_FILENAME = "inge.transformation.mab.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_MARCXML2ESCIDOC_CONFIGURATION_FILENAME =
      "inge.transformation.marcxml2escidoc.configuration.filename";
  public static final String INGE_TRANSFORMATION_MARCXML2ESCIDOC_STYLESHEET_FILENAME =
      "inge.transformation.marcxml2escidoc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_MODS2MARC_STYLESHEET_FILENAME = "inge.transformation.mods2marc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_MODS2OAIDC_STYLESHEET_FILENAME = "inge.transformation.mods2oaidc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_MODS_ITEM_STYLESHEET_FILENAME = "inge.transformation.mods_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_OAI_DC_STYLESHEET_FILENAME = "inge.transformation.oai_dc.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_PEER_STYLESHEET_FILENAME = "inge.transformation.peer.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_PMC2ESCIDOC_PUBLICATION_COMPONENT_STYLESHEET_FILENAME =
      "inge.transformation.pmc2escidoc_publication_component.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_PMC2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME =
      "inge.transformation.pmc2escidoc_publication_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_RIS_CONFIGURATION_FILENAME = "inge.transformation.ris.configuration.filename";
  public static final String INGE_TRANSFORMATION_RIS_STYLESHEET_FILENAME = "inge.transformation.ris.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_SPIRES2ESCIDOC_PUBLICATION_ITEM_STYLESHEET_FILENAME =
      "inge.transformation.spires2escidoc_publication_item.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_WOS_CONFIGURATION_FILENAME = "inge.transformation.wos.configuration.filename";
  public static final String INGE_TRANSFORMATION_WOS_STYLESHEET_FILENAME = "inge.transformation.wos.stylesheet.filename";
  public static final String INGE_TRANSFORMATION_ZFN_CONFIGURATION_FILENAME = "inge.transformation.zfn.configuration.filename";
  public static final String INGE_TRANSFORMATION_ZFN_STYLESHEET_FILENAME = "inge.transformation.zfn.stylesheet.filename";
  public static final String INGE_UNAPI_SERVICE_URL = "inge.unapi.service.url";

  // system properties
  public static final String FILE_ENCODING = "file.encoding";
  public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
  public static final String JBOSS_HOME_DIR = "jboss.home.dir";
  public static final String LINE_SEPARATOR = "line.separator";
  public static final String POM = "pom";

  private static final String DEFAULT_PROPERTY_FILE = "pubman.properties";

  private static Properties properties;
  private static String fileLocation = "";
  private static int counterForLoadingProperties = 0;

  private PropertyReader() {
    loadProperties();
  }

  private static PropertyReader getInstance() {
    return PropertyReaderHolder.instance;
  }

  public static String getProperty(String key) {
    return PropertyReader.getInstance().doGetProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    return null != PropertyReader.getInstance().doGetProperty(key) ? PropertyReader.getInstance().doGetProperty(key) : defaultValue;
  }

  public static Properties getProperties() {
    PropertyReader.getInstance();

    return PropertyReader.properties;
  }

  /**
   * Force the property file to be reloaded into the Properties object
   */
  public static void forceReloadProperties() {
    new PropertyReader();
  }

  /**
   * Gets the value of a property for the given key from the system properties or the PubMan
   * property file. It is always tried to get the requested property value from the system
   * properties. This option gives the opportunity to set a specific property temporary using the
   * system properties. If the requested property could not be obtained from the system properties
   * the PubMan property file is accessed. (For details on access to the properties file see class
   * description.)
   *
   * @param key The key of the property.
   * @param callingClass Class of the calling class
   * @return The value of the property.
   */
  private String doGetProperty(String key) {
    // First check system properties
    String value = System.getProperty(key);
    if (null != value) {
      return value;
    }

    // Get the property
    value = properties.getProperty(key);

    return value;
  }

  /**
   * Load the properties from the location defined by the system property
   * <code>pubman.properties.file</code>. If this property is not set the default file path
   * <code>pubman.properties</code> is used. If no properties can be loaded, the jvm is terminated.
   */
  private static void loadProperties() {
    counterForLoadingProperties++;

    String propertiesFile = "";
    Properties solProperties = new Properties();

    URL solution = PropertyReader.class.getClassLoader().getResource("solution.properties");

    if (null != solution) {
      logger.info("Solution URI is <" + solution + ">");

      try {
        InputStream in = getInputStream("solution.properties");
        solProperties.load(in);
        in.close();

        String appname = solProperties.getProperty("appname");
        propertiesFile = appname + ".properties";
      } catch (IOException e) {
        logger.warn("Could not read properties from solution.properties file.");
      }

    } else {
      // Use Default location of properties file
      propertiesFile = DEFAULT_PROPERTY_FILE;
      logger.info("Trying default property file: <" + DEFAULT_PROPERTY_FILE + ">");
    }

    properties = new Properties();
    try {
      InputStream instream = getInputStream(propertiesFile);
      properties.load(instream);
      properties.putAll(solProperties);
      instream.close();
    } catch (IOException e) {
      logger.fatal("Got no properties to load...<" + propertiesFile + ">", e);
      throw new ExceptionInInitializerError(e);
    }

    logger.info("Properties loaded successfully from " + fileLocation);
  }

  /**
   * Retrieves the Inputstream of the given file path. First the resource is searched in the file
   * system, if this fails it is searched using the classpath.
   *
   * @param filepath The path of the file to open.
   * @return The inputstream of the given file path.
   * @throws IOException If the file could not be found neither in the file system nor in the
   *         classpath.
   */
  private static InputStream getInputStream(String filepath) throws IOException {
    return getInputStream(filepath, PropertyReader.class);
  }

  /**
   * Retrieves the Inputstream of the given file path. First the resource is searched in the file
   * system, if this fails it is searched using the classpath.
   *
   * @param filepath The path of the file to open.
   * @param callingClass Class of the calling class
   * @return The inputstream of the given file path.
   * @throws IOException If the file could not be found neither in the file system nor in the
   *         classpath.
   */
  private static InputStream getInputStream(String filepath, Class<PropertyReader> callingClass) throws IOException {
    InputStream instream = null;
    // First try to search in file system
    try {
      instream = new FileInputStream(filepath);
      fileLocation = (new File(filepath)).getAbsolutePath();
    } catch (Exception e) {
      // try to get resource from classpath
      URL url = callingClass.getClassLoader().getResource(filepath);
      if (null != url) {
        instream = url.openStream();
        fileLocation = url.getFile();
      }
    }
    if (null == instream) {
      throw new FileNotFoundException(filepath);
    }
    return instream;
  }

  // only for test purpose
  static int getCounter() {
    return counterForLoadingProperties;
  }

  private static class PropertyReaderHolder {
    private static final PropertyReader instance = new PropertyReader();
  }
}
