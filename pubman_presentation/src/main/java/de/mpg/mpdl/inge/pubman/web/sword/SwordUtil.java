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

package de.mpg.mpdl.inge.pubman.web.sword;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.Source;
import org.w3.atom.Summary;
import org.w3.atom.Title;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

/**
 * This class provides helper method for the SWORD Server implementation.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class SwordUtil extends FacesBean {
  private static final Logger logger = Logger.getLogger(SwordUtil.class);

  // private static final String LOGIN_URL = "/aa/login";
  // private static final String LOGOUT_URL = "/aa/logout/clear.jsp";
  // private static final int NUMBER_OF_URL_TOKENS = 2;

  // private static final String acceptedFormat = "application/zip";
  private static final String itemPath = "/pubman/item/";
  private static final String mdFormatBibTex = TransformerFactory.BIBTEX;
  private static final String mdFormatEndnote = TransformerFactory.ENDNOTE;
  private static final String mdFormatEscidoc = "http://purl.org/escidoc/metadata/schemas/0.1/publication";
  private static final String mdFormatPeerTEI = "http://purl.org/net/sword-types/tei/peer";
  private static final String treatmentText =
      "Zip archives recognised as content packages are opened and the individual files contained in them are stored.";
  private static final String[] fileEndings = {".xml", ".bib", ".tei", ".enl"};

  private Deposit currentDeposit;
  private String depositXml = "";
  private final List<String> filenames = new ArrayList<String>();

  /**
   * Accepted packagings.
   */
  public String[] packaging = {SwordUtil.mdFormatEscidoc, SwordUtil.mdFormatBibTex, SwordUtil.mdFormatEndnote, SwordUtil.mdFormatPeerTEI};

  public SwordUtil() {
    this.init();
  }

  public void init() {
    this.filenames.clear();
  }

  // /**
  // * Logs in a user.
  // *
  // * @return AccountUserVO
  // */
  // public AccountUserVO checkUser(ServiceDocumentRequest sdr) {
  // AccountUserVO userVO = null;
  //
  // // Forward http authentification
  // if (sdr.getUsername() != null && sdr.getPassword() != null) {
  // final String username = sdr.getUsername();
  // final String pwd = sdr.getPassword();
  // try {
  // final String handle = AdminHelper.loginUser(username, pwd);
  // this.getLoginHelper().setESciDocUserHandle(handle);
  // userVO = this.getLoginHelper().getAccountUser();
  // } catch (final Exception e) {
  // SwordUtil.logger.error(e);
  // return null;
  // }
  // }
  //
  // return userVO;
  // }

  /**
   * Checks if a user has depositing rights for a collection.
   * 
   * @param collection
   * @param user
   * @return true if the user has depositing rights, else false
   */
  public boolean checkCollection(String collection, Principal user) {
    List<PubContextVOPresentation> contextList = null;
    final ContextListSessionBean contextListBean = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    contextListBean.init();
    contextList = contextListBean.getDepositorContextList();
    for (int i = 0; i < contextList.size(); i++) {
      final String context = contextList.get(i).getObjectId();
      if (context.toLowerCase().equals(collection.toLowerCase().trim())) {
        return true;
      }
    }

    return false;
  }

  // /**
  // * Logs in the given user with the given password.
  // *
  // * @param userid The id of the user to log in.
  // * @param password The password of the user to log in.
  // * @return The handle for the logged in user.
  // * @throws HttpException
  // * @throws IOException
  // * @throws ServiceException
  // * @throws URISyntaxException
  // */
  // @Deprecated
  // public String loginUser(String userid, String password) throws HttpException, IOException,
  // ServiceException, URISyntaxException {
  // final String frameworkUrl = PropertyReader.getFrameworkUrl();
  // final StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
  // if (tokens.countTokens() != SwordUtil.NUMBER_OF_URL_TOKENS) {
  // throw new IOException(
  // "Url in the config file is in the wrong format, needs to be http://<host>:<port>");
  // }
  // tokens.nextToken();
  // final StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
  //
  // if (hostPort.countTokens() != SwordUtil.NUMBER_OF_URL_TOKENS) {
  // throw new IOException(
  // "Url in the config file is in the wrong format, needs to be http://<host>:<port>");
  // }
  // final String host = hostPort.nextToken();
  // final int port = Integer.parseInt(hostPort.nextToken());
  //
  // final HttpClient client = new HttpClient();
  //
  // client.getHostConfiguration().setHost(host, port, "http");
  // client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
  //
  // final PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
  // login.addParameter("j_username", userid);
  // login.addParameter("j_password", password);
  //
  // client.executeMethod(login);
  //
  // login.releaseConnection();
  // final CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
  // final Cookie[] logoncookies =
  // cookiespec.match(host, port, "/", false, client.getState().getCookies());
  //
  // final Cookie sessionCookie = logoncookies[0];
  //
  // final PostMethod postMethod = new PostMethod(SwordUtil.LOGIN_URL);
  // postMethod.addParameter("target", frameworkUrl);
  // client.getState().addCookie(sessionCookie);
  // client.executeMethod(postMethod);
  //
  // if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode()) {
  // throw new HttpException("Wrong status code: " + login.getStatusCode());
  // }
  //
  // String userHandle = null;
  // final Header[] headers = postMethod.getResponseHeaders();
  // for (int i = 0; i < headers.length; ++i) {
  // if ("Location".equals(headers[i].getName())) {
  // final String location = headers[i].getValue();
  // final int index = location.indexOf('=');
  // userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
  // }
  // }
  // if (userHandle == null) {
  // throw new ServiceException("User not logged in.");
  // }
  // return userHandle;
  // }

  // /**
  // * @param fc
  // * @throws IOException
  // * @throws ServiceException
  // * @throws URISyntaxException
  // */
  // public void logoutUser() throws IOException, ServiceException, URISyntaxException {
  // FacesTools.getExternalContext().redirect(
  // PropertyReader.getFrameworkUrl()
  // + SwordUtil.LOGOUT_URL
  // + "?target="
  // + URLEncoder.encode(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
  // + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
  // + "?logout=true", "UTF-8"));
  // }

  /**
   * Creates a Account User.
   * 
   * @param user
   * @param pwd
   * @return AccountUserVO
   */
  public Principal getAccountUser(String user, String pwd) {
    try {
      final Principal p = ApplicationBean.INSTANCE.getUserAccountService().login(user, pwd);
      return p;
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This method takes a zip file and reads out the entries.
   * 
   * @param in
   * @throws TechnicalException
   * @throws NamingException
   * @throws SWORDContentTypeException
   */
  public ItemVersionVO readZipFile(InputStream in, Principal user) throws SWORDContentTypeException {
    String item = null;
    final List<FileDbVO> attachements = new ArrayList<FileDbVO>();
    ItemVersionVO pubItem = null;
    final int bufLength = 1024;
    final char[] buffer = new char[bufLength];
    int readReturn;
    int count = 0;

    try {
      ZipEntry zipentry;
      final ZipInputStream zipinputstream = new ZipInputStream(in);

      while ((zipentry = zipinputstream.getNextEntry()) != null) {
        count++;
        SwordUtil.logger.debug("Processing zip entry file: " + zipentry.getName());

        String name = URLDecoder.decode(zipentry.getName(), "UTF-8");
        name = name.replaceAll("/", "_slsh_");
        this.filenames.add(name);
        boolean metadata = false;

        // check if the file is a metadata file
        for (int i = 0; i < SwordUtil.fileEndings.length; i++) {

          final String ending = SwordUtil.fileEndings[i];
          if (name.endsWith(ending)) {
            metadata = true;
            // Retrieve the metadata

            final StringWriter sw = new StringWriter();
            final Reader reader = new BufferedReader(new InputStreamReader(zipinputstream, "UTF-8"));

            while ((readReturn = reader.read(buffer)) != -1) {
              sw.write(buffer, 0, readReturn);
            }

            item = new String(sw.toString());
            this.depositXml = item;
            pubItem = this.createItem(item, user);

            // if not escidoc format, add as component
            if (!this.currentDeposit.getFormatNamespace().equals(SwordUtil.mdFormatEscidoc)) {
              attachements.add(this.convertToFileAndAdd(new ByteArrayInputStream(item.getBytes("UTF-8")), name, user, zipentry));
            }
          }
        }

        if (!metadata) {
          attachements.add(this.convertToFileAndAdd(zipinputstream, name, user, zipentry));
        }

        zipinputstream.closeEntry();
      }
      zipinputstream.close();


      // Now add the components to the Pub Item (if they do not exist. If they exist, use the
      // existing component metadata and just change the content)
      for (final FileDbVO newFile : attachements) {
        boolean existing = false;
        for (final FileDbVO existingFile : pubItem.getFiles()) {
          if (existingFile.getName().replaceAll("/", "_slsh_").equals(newFile.getName())) {
            // file already exists, replace content
            existingFile.setContent(newFile.getContent());
            existingFile.getMetadata().setSize(newFile.getMetadata().getSize());
            existing = true;
          }
        }

        if (!existing) {
          pubItem.getFiles().add(newFile);
        }
      }

      // If peer format, add additional copyright information to component. They are read from the
      // TEI metadata.
      if (this.currentDeposit.getFormatNamespace().equals(SwordUtil.mdFormatPeerTEI)) {

        final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

        final String fileXml = itemTransformingService.transformFromTo(TransformerFactory.FORMAT.PEER_TEI_XML,
            TransformerFactory.FORMAT.ESCIDOC_COMPONENT_XML, this.depositXml, null);


        try {


          final FileDbVO transformdedFileVO = EntityTransformer.transformToNew(XmlTransformingService.transformToFileVO(fileXml));
          for (final FileDbVO pubItemFile : pubItem.getFiles()) {
            pubItemFile.getMetadata().setRights(transformdedFileVO.getMetadata().getRights());
            pubItemFile.getMetadata().setCopyrightDate(transformdedFileVO.getMetadata().getCopyrightDate());
          }

        } catch (final Exception e) {
          SwordUtil.logger.error("File Xml could not be transformed into FileDbVO. ", e);
        }
      }

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    if (count == 0) {
      SwordUtil.logger.info("No zip file was provided.");
      throw new SWORDContentTypeException();
    }

    return pubItem;
  }

  /**
   * @param item
   * @param files
   * @return
   * @throws NamingException
   * @throws TechnicalException
   * @throws SWORDContentTypeException
   */
  private ItemVersionVO createItem(String item, Principal user) throws ValidationException, Exception {
    ItemVersionVO itemVO = null;

    if (item == null) {
      throw new IngeTechnicalException();
    }

    String transformedItem = null;

    try {
      // Format escidocFormat = new Format("escidoc-publication-item", "application/xml", "UTF-8");
      TransformerFactory.FORMAT trgFormat = null;
      Boolean transform = false;

      // Transform from tei to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(SwordUtil.mdFormatPeerTEI)) {
        // trgFormat = new Format("peer_tei", "application/xml", "UTF-8");
        trgFormat = TransformerFactory.FORMAT.PEER_TEI_XML;
        transform = true;
      }

      // Transform from bibtex to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(SwordUtil.mdFormatBibTex)) {
        trgFormat = TransformerFactory.FORMAT.BIBTEX_STRING;
        transform = true;
      }

      // Transform from endnote to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(SwordUtil.mdFormatEndnote)) {
        trgFormat = TransformerFactory.FORMAT.ENDNOTE_STRING;
        transform = true;
      }

      if (transform) {
        final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();
        transformedItem = itemTransformingService.transformFromTo(TransformerFactory.FORMAT.PEER_TEI_XML, trgFormat, item, null);
      }

      // Create item
      itemVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(transformedItem));

      // Set Version to null in order to force PubItemPubItemService to create a new item.
      itemVO.setObjectId(null);

      SwordUtil.logger.debug("Item successfully created.");
    } catch (final Exception e) {
      SwordUtil.logger.error("Transformation to PubItem failed.", e);
      final ValidationReportItemVO itemReport =
          new ValidationReportItemVO("Error transforming item into eSciDoc Publication Item.", ValidationReportItemVO.Severity.ERROR);
      final ValidationReportVO report = new ValidationReportVO();
      report.getItems().add(itemReport);
      throw new ValidationException(report);
    }

    return itemVO;
  }

  /**
   * @param user
   * @param item
   * @return Saved pubItem
   * @throws NamingException
   * @throws AuthorizationException
   * @throws SecurityException
   * @throws TechnicalException
   * @throws URISyntaxException
   * @throws NamingException
   * @throws PubItemStatusInvalidException
   * @throws IngeEsServiceException
   * @throws AuthenticationException
   * @throws ValidationException
   * @throws PubItemAlreadyReleasedException
   * @throws PubItemNotFoundException
   * @throws PubCollectionNotFoundException
   * @throws PubItemLockedException
   * @throws PubItemMandatoryAttributesMissingException
   * @throws ValidationServiceException
   * @throws PubManException
   * @throws DepositingException
   */
  public ItemVersionVO doDeposit(ItemVersionVO item) throws AuthenticationException, IngeTechnicalException,
      de.mpg.mpdl.inge.service.exceptions.AuthorizationException, IngeApplicationException {

    ItemVersionVO depositedItem = null;
    final String method = this.getMethod(item);

    if (method == null) {
      throw new IngeTechnicalException(null, null);
    }

    final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();
    final String authenticationToken = this.getItemControllerSessionBean().getLoginHelper().getAuthenticationToken();

    if (method.equals("SAVE_SUBMIT") || method.equals("SUBMIT")) {
      depositedItem = pubItemService.create(item, authenticationToken);
      depositedItem =
          pubItemService.submitPubItem(depositedItem.getObjectId(), depositedItem.getModificationDate(), "", authenticationToken);
    }

    if (method.equals("RELEASE")) {
      depositedItem = pubItemService.create(item, authenticationToken);
      depositedItem =
          pubItemService.releasePubItem(depositedItem.getObjectId(), depositedItem.getModificationDate(), "", authenticationToken);
    }

    return depositedItem;
  }

  // /**
  // * Returns the Workflow of the current context.
  // */
  // public PublicationAdminDescriptorVO.Workflow getWorkflow() {
  // if ((this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow()
  // == PublicationAdminDescriptorVO.Workflow.STANDARD)) {
  // return Workflow.STANDARD;
  // }
  //
  // if ((this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow()
  // == PublicationAdminDescriptorVO.Workflow.SIMPLE)) {
  // return Workflow.SIMPLE;
  // }
  //
  // return null;
  // }

  private String getMethod(ItemVersionVO item) {
    boolean isWorkflowStandard = false;
    boolean isWorkflowSimple = true;

    boolean isStatePending = true;
    boolean isStateSubmitted = false;
    boolean isStateInRevision = false;

    if (item != null && item.getObjectId() != null && item.getVersionState() != null) {
      isStatePending = ItemVersionRO.State.PENDING.equals(item.getVersionState());
      isStateSubmitted = ItemVersionRO.State.SUBMITTED.equals(item.getVersionState());
      isStateInRevision = ItemVersionRO.State.IN_REVISION.equals(item.getVersionState());
    }

    isWorkflowStandard = ContextDbVO.Workflow.STANDARD == this.getItemControllerSessionBean().getCurrentContext().getWorkflow();
    isWorkflowSimple = ContextDbVO.Workflow.SIMPLE == this.getItemControllerSessionBean().getCurrentContext().getWorkflow();

    final boolean isModerator =
        GrantUtil.hasRole(this.getLoginHelper().getAccountUser(), PredefinedRoles.MODERATOR, item.getObject().getContext().getObjectId());
    boolean isOwner = true;
    if (item.getObject().getCreator() != null) {
      isOwner = (this.getLoginHelper().getAccountUser() != null
          ? this.getLoginHelper().getAccountUser().getObjectId().equals(item.getObject().getCreator().getObjectId())
          : false);
    }

    if ((isStatePending || isStateSubmitted) && isWorkflowSimple && isOwner) {
      return "RELEASE";
    }

    if ((isStatePending || isStateInRevision) && isWorkflowStandard && isOwner) {
      return "SAVE_SUBMIT";
    }

    if (((isStatePending || isStateInRevision) && isOwner) || (isStateSubmitted && isModerator)) {
      return "SUBMIT";
    }

    return null;
  }

  /**
   * Returns the Workflow for a given context.
   * 
   * @param pubContext
   * @return workflow type as string
   */
  public String getWorkflowAsString(PubContextVOPresentation pubContext) {
    final boolean isWorkflowStandard = pubContext.getWorkflow() == ContextDbVO.Workflow.STANDARD;
    final boolean isWorkflowSimple = pubContext.getWorkflow() == ContextDbVO.Workflow.SIMPLE;

    if (isWorkflowStandard) {
      return "Standard Workflow";
    }

    if (isWorkflowSimple) {
      return "Simple Workflow";
    }

    return "";
  }

  /**
   * Converts a byte[] into a FileDbVO.
   * 
   * @param file
   * @param name
   * @param user
   * @return FileDbVO
   * @throws Exception
   */
  private FileDbVO convertToFileAndAdd(InputStream zipinputstream, String name, Principal user, ZipEntry zipEntry) throws Exception {
    final MdsFileVO mdSet = new MdsFileVO();
    final FileDbVO fileVO = new FileDbVO();
    final FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String mimeType = fileNameMap.getContentTypeFor(name);

    // Hack: FileNameMap class does not know tei, bibtex and endnote
    if (name.endsWith(".tei")) {
      mimeType = "application/xml";
    }
    if (name.endsWith(".bib") || name.endsWith(".enl")) {
      mimeType = "text/plain";
    }

    final String fileURL = this.uploadFile(zipinputstream, mimeType, user, zipEntry, name);

    if (fileURL != null && !fileURL.toString().trim().equals("")) {
      if (this.currentDeposit.getContentDisposition() != null && !this.currentDeposit.getContentDisposition().equals("")) {
        name = this.currentDeposit.getContentDisposition();
      }

      fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
      fileVO.setVisibility(FileDbVO.Visibility.PUBLIC);
      fileVO.setMetadata(mdSet);
      fileVO.getMetadata().setTitle(name);
      fileVO.setMimeType(mimeType);
      fileVO.setName(name);

      final FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(mimeType);
      fileVO.getMetadata().getFormats().add(formatVO);
      fileVO.setContent(fileURL.toString());
      fileVO.getMetadata().setSize((int) zipEntry.getSize());
      // This is the provided metadata file which we store as a component
      if (!name.endsWith(".pdf")) {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          final Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            this.error(this.getMessage("NoContentCategory"));
            Logger.getLogger(PubFileVOPresentation.class).warn("WARNING: no content-category has been defined in Genres.xml");
          }
        }
        fileVO.getMetadata().setContentCategory(contentCategory);
      } else {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("PUBLISHER_VERSION") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("PUBLISHER_VERSION");
        } else {
          final Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            this.error(this.getMessage("NoContentCategory"));
            Logger.getLogger(PubFileVOPresentation.class).warn("WARNING: no content-category has been defined in Genres.xml");
          }
        }
        fileVO.getMetadata().setContentCategory(contentCategory);
      }
    }

    return fileVO;
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param InputStream to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected String uploadFile(InputStream in, String mimetype, Principal principal, ZipEntry zipEntry, String name) throws Exception {

    StagedFileDbVO stagedFile = ApplicationBean.INSTANCE.getFileService().createStageFile(in, name, principal.getJwToken());
    return String.valueOf(stagedFile.getId());

  }

  public SWORDEntry createResponseAtom(ItemVersionVO item, Deposit deposit) {
    final SWORDEntry se = new SWORDEntry();
    final PubManSwordServer server = new PubManSwordServer();

    // This info can only be filled if item was successfully created
    if (item != null) {
      final Title title = new Title();
      title.setContent(item.getMetadata().getTitle());
      se.setTitle(title);

      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      final TimeZone utc = TimeZone.getTimeZone("UTC");
      sdf.setTimeZone(utc);
      final String milliFormat = sdf.format(new Date());
      se.setUpdated(milliFormat);
    }

    final Summary s = new Summary();
    String filename = "";
    for (int i = 0; i < this.filenames.size(); i++) {
      if (filename.equals("")) {
        filename = this.filenames.get(i);
      } else {
        filename = filename + " ," + this.filenames.get(i);
      }
    }
    s.setContent(filename);
    se.setSummary(s);

    final Content content = new Content();
    content.setSource("");

    // // Only set content if item was deposited
    if (!deposit.isNoOp() && item != null) {
      content.setSource(server.getBaseURL() + "/rest/items/" + item.getObjectId());
      se.setId(server.getBaseURL() + SwordUtil.itemPath + item.getObjectId());
    }
    se.setContent(content);

    final Source source = new Source();
    final Generator generator = new Generator();
    generator.setContent(server.getBaseURL());
    source.setGenerator(generator);
    se.setSource(source);
    se.setTreatment(SwordUtil.treatmentText);
    se.setNoOp(deposit.isNoOp());

    final Author author = new Author();
    author.setName(deposit.getUsername());
    se.addAuthors(author);

    return se;
  }

  public boolean checkMetadatFormat(String format) {
    for (int i = 0; i < this.packaging.length; i++) {
      final String pack = this.packaging[i];
      if (format.equalsIgnoreCase(pack)) {
        return true;
      }
    }

    return false;
  }

  public String getTreatmentText() {
    return SwordUtil.treatmentText;
  }

  public void setCurrentDeposit(Deposit currentDeposit) {
    this.currentDeposit = currentDeposit;
  }

  public ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }
}
