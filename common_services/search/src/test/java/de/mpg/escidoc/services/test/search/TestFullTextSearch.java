package de.mpg.escidoc.services.test.search;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.apache.axis.message.MessageElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.escidoc.www.services.adm.AdminHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ResultVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.util.AdminHelper;
import de.mpg.escidoc.services.util.PropertyReader;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.service.SRWPort;


public class TestFullTextSearch {
  private static String userHandle;
  private static ItemHandler itemHandler;
  private static AdminHandler adminHandler;
  private static SRWPort searchHandler_item_container_admin;
  private static SRWPort searchHandler_escidoc_all;
  private static XmlTransforming xmlTransforming;

  private static HashSet<String> itemIdsForPurging;

  private static Logger logger = Logger.getLogger(TestFullTextSearch.class);

  private static String query_item_container_admin =
      "\"/properties/content-model/id\"=\"YYY\" AND \"/fulltext\"=XXX";

  private static String query_escidoc_all =
      "escidoc.objecttype=\"item\" AND escidoc.content-model.objid=\"YYY\" AND \"escidoc.fulltext\"=XXX";

  private static long sleepingTime = 5000;

  private static final String BAYESIAN = "\"Bayesian parameter estimation\"";
  private static final String BODY_MASS = "\"log-transformed body-mass\"";

  @BeforeClass
  public static void init() throws Exception {
    userHandle =
        AdminHelper.loginUser(PropertyReader.getProperty("framework.admin.username"),
            PropertyReader.getProperty("framework.admin.password"));

    itemHandler = ServiceLocator.getItemHandler(userHandle);
    adminHandler = ServiceLocator.getAdminHandler(userHandle);
    searchHandler_item_container_admin =
        ServiceLocator.getSearchHandler("item_container_admin",
            new URL(PropertyReader.getFrameworkUrl()), userHandle);
    searchHandler_escidoc_all =
        ServiceLocator.getSearchHandler("escidoc_all", new URL(PropertyReader.getFrameworkUrl()),
            userHandle);

    xmlTransforming = new XmlTransformingBean();

    itemIdsForPurging = new HashSet<String>();
  }

  @AfterClass
  public static void cleanUp() throws Exception {
    if (itemIdsForPurging.size() == 0)
      return;

    StringBuffer b = new StringBuffer();
    b.append("<param>");

    for (String objId : itemIdsForPurging) {
      b.append("<id>");
      b.append(objId);
      b.append("</id>");
    }

    b.append("</param>");

    logger.info("starting to delete objects: " + b.toString());

    String frameworkReturnXml = adminHandler.deleteObjects(b.toString());
    logger.info("Adminhandler.deleteObjects returned: " + frameworkReturnXml);
  }

  @Test
  public void testFullTextSearch1() throws Exception {
    FulltextSearchResult result_item_container_1 =
        this.doFulltextSearch(searchHandler_item_container_admin, query_item_container_admin,
            BAYESIAN, "");
    PubItemVO pubItemVO = this.createAndSubmitItem();

    // fulltext search in item_container_admin
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_item_container_2 =
        this.doFulltextSearch(searchHandler_item_container_admin, query_item_container_admin,
            BAYESIAN, pubItemVO.getLatestVersion().getObjectId());

    assertTrue("Expected positive number of hits ", result_item_container_2.getNumberOfHits() > 0);
    assertTrue(
        "Found result_item_container_1 " + result_item_container_1.getNumberOfHits()
            + " result_item_container_2 " + result_item_container_2.getNumberOfHits(),
        result_item_container_2.getNumberOfHits() == (result_item_container_1.getNumberOfHits() + 1));
    assertTrue(result_item_container_2.isFound());
    assertTrue(result_item_container_2.getVersionNumber() == 1);


    // release item
    FulltextSearchResult result_escidoc_all_1 =
        this.doFulltextSearch(searchHandler_escidoc_all, query_escidoc_all, BAYESIAN, pubItemVO
            .getLatestVersion().getObjectId());
    pubItemVO = this.releaseItem(pubItemVO);

    // fulltext search in escidoc_all
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_escidoc_all_2 =
        this.doFulltextSearch(searchHandler_escidoc_all, query_escidoc_all, BAYESIAN, pubItemVO
            .getLatestRelease().getObjectId());

    assertTrue(result_escidoc_all_2.getNumberOfHits() > 0);
    assertTrue(result_escidoc_all_2.isFound());
    assertTrue(result_escidoc_all_2.getVersionNumber() == 1);
    assertTrue("Found result_escidoc_all_1 <" + result_escidoc_all_1.getNumberOfHits()
        + "> result_escidoc_all_2 <" + result_escidoc_all_2.getNumberOfHits() + ">",
        result_escidoc_all_2.getNumberOfHits() == (result_escidoc_all_1.getNumberOfHits() + 1));

    // remove components and submit
    pubItemVO = this.removeComponentsAndSubmit(pubItemVO);

    // fulltext search in item_container_admin
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_item_container_3 =
        this.doFulltextSearch(searchHandler_item_container_admin, query_item_container_admin,
            BAYESIAN, pubItemVO.getLatestVersion().getObjectId());
    // found the latest released version
    assertTrue(result_item_container_3.isFound());
    assertTrue(result_item_container_3.getVersionNumber() == 1);
    assertTrue("Found result_item_container_2 <" + result_item_container_2.getNumberOfHits()
        + "> result_item_container_3 <" + result_item_container_3.getNumberOfHits() + ">",
        result_item_container_3.getNumberOfHits() == (result_item_container_2.getNumberOfHits()));
    assertTrue(
        "Got result_item_container_2.getVersion <" + result_item_container_2.getVersionNumber()
            + "> result_item_container_3 <" + result_item_container_3.getVersionNumber() + ">",
        result_item_container_3.getVersionNumber() == result_item_container_2.getVersionNumber());


    // release item without component
    FulltextSearchResult result_escidoc_all_3 =
        this.doFulltextSearch(searchHandler_escidoc_all, query_escidoc_all, BAYESIAN, pubItemVO
            .getLatestVersion().getObjectId());
    pubItemVO = this.releaseItem(pubItemVO);

    // fulltext search in escidoc_all
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_escidoc_all_4 =
        this.doFulltextSearch(searchHandler_escidoc_all, query_escidoc_all, BAYESIAN, pubItemVO
            .getLatestRelease().getObjectId());
    // find one less
    assertTrue(!result_escidoc_all_4.isFound());
    assertTrue("Found result_escidoc_all_3 <" + result_escidoc_all_3.getNumberOfHits()
        + "> result_escidoc_all_4 <" + result_escidoc_all_4.getNumberOfHits() + ">",
        result_escidoc_all_3.getNumberOfHits() == (result_escidoc_all_4.getNumberOfHits() + 1));

    // add another component
    pubItemVO = this.addComponentAndSubmit(pubItemVO, "BGC1891.pdf");

    // fulltext search in item_container_admin with search string in new component
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_item_container_4 =
        this.doFulltextSearch(searchHandler_item_container_admin, query_item_container_admin,
            BODY_MASS, pubItemVO.getLatestVersion().getObjectId());

    assertTrue("Expected positive number of hits ", result_item_container_4.getNumberOfHits() > 0);
    assertTrue(result_item_container_4.isFound());
    assertTrue(result_item_container_4.getVersionNumber() == 3);

    // search for search string of old component should be negative
    FulltextSearchResult result_item_container_5 =
        this.doFulltextSearch(searchHandler_item_container_admin, query_item_container_admin,
            BAYESIAN, pubItemVO.getLatestVersion().getObjectId());

    assertTrue(!result_item_container_5.isFound());

    // release item with new component
    pubItemVO = this.releaseItem(pubItemVO);

    // fulltext search in escidoc_all for search string of new component
    Thread.sleep(sleepingTime);
    FulltextSearchResult result_escidoc_all_5 =
        this.doFulltextSearch(searchHandler_escidoc_all, query_escidoc_all, BODY_MASS, pubItemVO
            .getLatestRelease().getObjectId());
    assertTrue("Expected positive number of hits ", result_escidoc_all_5.getNumberOfHits() > 0);
    assertTrue(result_escidoc_all_5.isFound());
    assertTrue(result_escidoc_all_5.getVersionNumber() == 3);
  }

  private String getQuery(String querySnippet, String s) throws IOException, URISyntaxException {
    return querySnippet.replaceAll("XXX", s).replaceAll("YYY",
        PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
  }

  private PubItemVO createAndSubmitItem() throws Exception {
    // new item
    PubItemVO actualItemVO = getComplexPubItemWithoutFiles();

    actualItemVO = addFileToItem(actualItemVO, "BGC1879.pdf");
    String actualItem = xmlTransforming.transformToItem(actualItemVO);
    long start = System.currentTimeMillis();
    actualItem = itemHandler.create(actualItem);
    long end = System.currentTimeMillis();
    actualItemVO = xmlTransforming.transformToPubItem(actualItem);
    logger.info("Create item finished: <" + actualItemVO.getVersion().getObjectId() + "> needed |"
        + (end - start) + "| msec");


    itemIdsForPurging.add(actualItemVO.getVersion().getObjectId());

    // Submit the item
    return submitItem(actualItemVO);
  }

  private PubItemVO addFileToItem(PubItemVO pubItemVO, String fileName) throws Exception {
    // Add file to item
    FileVO initPubFile = new FileVO();
    String testfile = "src/test/resources/components/" + fileName;
    initPubFile.setDescription("Sehen Sie B6?");
    initPubFile.setVisibility(Visibility.PUBLIC);
    initPubFile
        .setContentCategoryString("http://purl.org/escidoc/metadata/ves/content-categories/abstract");
    initPubFile.setContent(uploadFile(testfile, "application/pdf", userHandle).toString());
    initPubFile.setName(fileName);
    initPubFile.setMimeType("application/pdf");
    // initPubFile.setSize((int)new
    // File("src/test/resources/depositing/pubItemDepositingTest/farbtest_B6.gif").length());
    initPubFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);

    MdsFileVO mdsFile = new MdsFileVO();
    mdsFile.setContentCategory("http://purl.org/escidoc/metadata/ves/content-categories/abstract");
    mdsFile.setTitle(new TextVO(fileName));

    initPubFile.getMetadataSets().add(mdsFile);

    pubItemVO.getFiles().add(initPubFile);

    return pubItemVO;
  }

  private PubItemVO submitItem(PubItemVO actualItemVO) throws Exception {
    String paramXml;
    TaskParamVO param;
    String actualItem;
    long start;
    long end;

    param = new TaskParamVO(actualItemVO.getModificationDate(), "Submit Item");
    paramXml = xmlTransforming.transformToTaskParam(param);
    start = System.currentTimeMillis();
    itemHandler.submit(actualItemVO.getVersion().getObjectId(), paramXml);
    end = System.currentTimeMillis();

    actualItem = itemHandler.retrieve(actualItemVO.getVersion().getObjectId());
    actualItemVO = xmlTransforming.transformToPubItem(actualItem);
    logger.info("Submit item finished: <" + actualItemVO.getVersion().getObjectId() + "> needed |"
        + (end - start) + "| msec");

    return actualItemVO;
  }

  public PubItemVO releaseItem(PubItemVO actualItemVO) throws Exception {
    long start, end;
    String url;
    PidTaskParamVO pidParam;
    String result = null;
    String paramXml;
    TaskParamVO param;

    Date lastModificationDate = actualItemVO.getModificationDate();
    String objectId = actualItemVO.getVersion().getObjectId();

    // Floating PID assignment.

    if (actualItemVO.getPid() == null || actualItemVO.getPid().equals("")) {
      start = System.currentTimeMillis();
      // Build PidParam
      url =
          PropertyReader.getProperty("escidoc.pubman.instance.url")
              + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
              + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                  objectId);

      logger.debug("URL given to PID resolver: " + url);


      pidParam = new PidTaskParamVO(lastModificationDate, url);
      paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

      try {
        // Assign floating PID
        result = itemHandler.assignObjectPid(objectId, paramXml);

        logger.debug("Floating PID assigned: " + result);
      } catch (Exception e) {
        System.out.println(e.getClass());
        logger.warn("Object PID assignment for " + objectId
            + " failed. It probably already has one.");
        logger.debug("Stacktrace:", e);
      }
      end = System.currentTimeMillis();
      logger.info("assign object PID for <" + objectId + "> needed |" + (end - start) + "| msec");
      // Retrieve the item to get last modification date
      String actualItem = itemHandler.retrieve(objectId);

      actualItemVO = xmlTransforming.transformToPubItem(actualItem);
    }

    if (actualItemVO.getVersion().getPid() == null || actualItemVO.getVersion().getPid().equals("")) {
      start = System.currentTimeMillis();
      // Build PidParam
      url =
          PropertyReader.getProperty("escidoc.pubman.instance.url")
              + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
              + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                  objectId + ":" + actualItemVO.getVersion().getVersionNumber());

      logger.debug("URL given to PID resolver: " + url);

      pidParam = new PidTaskParamVO(actualItemVO.getModificationDate(), url);
      paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

      try {
        // Assign version PID
        result =
            itemHandler.assignVersionPid(actualItemVO.getVersion().getObjectId() + ":"
                + actualItemVO.getVersion().getVersionNumber(), paramXml);

        logger.debug("Version PID assigned: " + result);
      } catch (Exception e) {
        logger.warn("Version PID assignment for " + objectId
            + " failed. It probably already has one.", e);
      }
      end = System.currentTimeMillis();
      logger.info("assign version PID for <" + objectId + "> needed |" + (end - start) + "| msec");
    }

    // Loop over files
    for (FileVO file : actualItemVO.getFiles()) {
      start = System.currentTimeMillis();
      if (file.getPid() == null || file.getPid().equals("")) {
        // Build PidParam
        url =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + PropertyReader.getProperty("escidoc.pubman.component.pattern")
                    .replaceAll("\\$1", objectId)
                    .replaceAll("\\$2", file.getReference().getObjectId())
                    .replaceAll("\\$3", CommonUtils.urlEncode(file.getName()));

        logger.debug("URL given to PID resolver: " + url);
        // LOGGER.debug("file.getLastModificationDate(): " +
        // file.getLastModificationDate());

        try {

          ResultVO resultVO = xmlTransforming.transformToResult(result);
          pidParam = new PidTaskParamVO(resultVO.getLastModificationDate(), url);
          paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

          // Assign component PID
          result =
              itemHandler.assignContentPid(actualItemVO.getVersion().getObjectId(), file
                  .getReference().getObjectId(), paramXml);

          logger.debug("PID assigned: " + result);
        } catch (Exception e) {

          logger.warn("Component PID assignment for " + objectId
              + " failed. It probably already has one.", e);
        }
      }
      end = System.currentTimeMillis();
      logger.info("assign content PID for " + objectId + "> needed |" + (end - start) + "| msec");

    }

    // Retrieve the item to get last modification date
    String actualItem = itemHandler.retrieve(objectId);
    actualItemVO = xmlTransforming.transformToPubItem(actualItem);

    // Release the item
    param = new TaskParamVO(actualItemVO.getModificationDate(), "this is the release comment");
    paramXml = xmlTransforming.transformToTaskParam(param);

    start = System.currentTimeMillis();
    itemHandler.release(objectId, paramXml);
    end = System.currentTimeMillis();
    logger.info("pure itemHandler.release item " + objectId + "> needed |" + (end - start)
        + "| msec");

    actualItem = itemHandler.retrieve(objectId);
    actualItemVO = xmlTransforming.transformToPubItem(actualItem);

    return actualItemVO;
  }

  private PubItemVO addComponentAndSubmit(PubItemVO pubItemVO, String fileName) throws Exception {
    pubItemVO = this.addFileToItem(pubItemVO, fileName);

    String actualItem = xmlTransforming.transformToItem(pubItemVO);

    itemHandler.update(pubItemVO.getVersion().getObjectId(), actualItem);

    String item = itemHandler.retrieve(pubItemVO.getVersion().getObjectId());
    pubItemVO = xmlTransforming.transformToPubItem(item);

    return this.submitItem(pubItemVO);
  }

  private PubItemVO removeComponentsAndSubmit(PubItemVO pubItemVO) throws Exception {
    if (pubItemVO.getFiles() == null)
      return pubItemVO;

    for (int i = 0; i < pubItemVO.getFiles().size(); i++) {
      pubItemVO.getFiles().remove(i);
    }

    String actualItem = xmlTransforming.transformToItem(pubItemVO);

    itemHandler.update(pubItemVO.getVersion().getObjectId(), actualItem);

    String item = itemHandler.retrieve(pubItemVO.getVersion().getObjectId());
    pubItemVO = xmlTransforming.transformToPubItem(item);

    return this.submitItem(pubItemVO);
  }


  private PubItemVO getComplexPubItemWithoutFiles() throws Exception {
    PubItemVO item = new PubItemVO();

    // Metadata
    MdsPublicationVO mds = getMdsPublication();
    item.setMetadata(mds);
    // PubCollectionRef
    ContextRO collectionRef = new ContextRO();
    collectionRef.setObjectId(PropertyReader
        .getProperty("escidoc.framework_access.context.id.test"));
    item.setContext(collectionRef);
    item.setContentModel(PropertyReader
        .getProperty("escidoc.framework_access.content-model.id.publication"));
    return item;
  }

  private MdsPublicationVO getMdsPublication() {
    // Metadata
    MdsPublicationVO mds = new MdsPublicationVO();
    CreatorVO creator;
    OrganizationVO organization;
    PublishingInfoVO pubInfo;

    // Genre
    mds.setGenre(Genre.BOOK);

    // Creator
    creator = new CreatorVO();
    // Creator.Role
    creator.setRole(CreatorRole.AUTHOR);
    // Creator.Person
    PersonVO person = new PersonVO();
    // Creator.Person.CompleteName
    person.setCompleteName("Hans Meier");
    // Creator.Person.GivenName
    person.setGivenName("Hans");
    // Creator.Person.FamilyName
    person.setFamilyName("Meier");
    // Creator.Person.AlternativeName
    person.getAlternativeNames().add("Werner");
    person
        .getAlternativeNames()
        .add(
            "These tokens are escaped and must stay escaped: \"&amp;\", \"&gt;\", \"&lt;\", \"&quot;\", \"&apos;\"");
    person.getAlternativeNames().add(
        "These tokens are escaped and must stay escaped, too: &auml; &Auml; &szlig;");
    // Creator.Person.Title
    person.getTitles().add("Dr. (?)");
    // Creator.Person.Pseudonym
    person.getPseudonyms().add("<b>Shorty</b>");
    person.getPseudonyms().add("<'Dr. Short'>");
    // Creator.Person.Organization
    organization = new OrganizationVO();
    // Creator.Person.Organization.Name
    TextVO name = new TextVO();
    name.setValue("Vinzenzmurr");
    name.setLanguage("de");
    organization.setName(name);
    // Creator.Person.Organization.Address
    organization.setAddress("<a href=\"www.buxtehude.de\">Irgendwo in Deutschland</a>");
    // Creator.Person.Organization.Identifier
    try {
      organization.setIdentifier(PropertyReader.getProperty("framework.organizational_unit.id"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    person.getOrganizations().add(organization);
    // Creator.Person.Identifier
    person.setIdentifier(new IdentifierVO(IdType.PND, "HH-XY-2222"));
    creator.setPerson(person);
    mds.getCreators().add(creator);
    creator = new CreatorVO();
    // Creator.Role
    creator.setRole(CreatorRole.CONTRIBUTOR);
    // Source.Creator.Organization
    organization = new OrganizationVO();
    // Creator.Organization.Name
    name.setValue("MPDL");
    name.setLanguage("en");
    organization.setName(name);
    // Creator.Organization.Address
    organization.setAddress("Amalienstraße");
    // Creator.Organization.Identifier
    try {
      organization.setIdentifier(PropertyReader.getProperty("framework.organizational_unit.id"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    creator.setOrganization(organization);
    mds.getCreators().add(creator);

    // Title
    mds.setTitle(new TextVO("Über den Wölken. The first of all. Das Maß aller Dinge.", "en"));

    // Language
    mds.getLanguages().add("deu");
    mds.getLanguages().add("eng");
    mds.getLanguages().add("fra");

    // Alternative Title
    mds.getAlternativeTitles().add(new TextVO("Die Erste von allen.", "de"));
    mds.getAlternativeTitles().add(new TextVO("Wulewu", "fr"));

    // Identifier
    mds.getIdentifiers().add(new IdentifierVO(IdType.ISI, "0815"));
    mds.getIdentifiers().add(new IdentifierVO(IdType.ISSN, "issn"));

    // Publishing Info
    pubInfo = new PublishingInfoVO();
    pubInfo.setPublisher("O'Reilly Media Inc., 1005 Gravenstein Highway North, Sebastopol");
    pubInfo.setEdition("One and a half");
    pubInfo.setPlace("Garching-Itzehoe-Capreton");
    mds.setPublishingInfo(pubInfo);

    // Date
    mds.setDateCreated("2005-02");
    mds.setDateSubmitted("2005-08-31");
    mds.setDateAccepted("2005");
    mds.setDatePublishedInPrint("2006-02-01");
    mds.setDateModified("2007-02-28");

    // Review method
    mds.setReviewMethod(ReviewMethod.INTERNAL);

    // Source
    SourceVO source = new SourceVO();
    // Source.Title
    source.setGenre(SourceVO.Genre.BOOK);
    source.setTitle(new TextVO("Dies ist die Wurzel allen Übels.", "jp"));
    // Source.AlternativeTitle
    source.getAlternativeTitles().add(new TextVO("This is the root of all ???.", "en"));
    source.getAlternativeTitles()
        .add(
            new TextVO("< and & are illegal characters in XML and therefore have to be escaped.",
                "en"));
    source.getAlternativeTitles().add(
        new TextVO(
            "> and ' and ? are problematic characters in XML and therefore should be escaped.",
            "en"));
    source
        .getAlternativeTitles()
        .add(
            new TextVO(
                "What about `, $, §, \", @ and the good old % (not to forget the /, the !, -, the _, the ~, the @ and the #)?",
                "en"));
    source.getAlternativeTitles().add(
        new TextVO("By the way, the Euro sign looks like this: €", "en"));
    // Source.Creator
    creator = new CreatorVO();
    // Source.Creator.Role
    creator.setRole(CreatorRole.AUTHOR);
    // Source.Creator.Organization
    organization = new OrganizationVO();
    // Source.Creator.Organization.Name
    name.setValue("murrrmurr");
    name.setLanguage("de");
    organization.setName(name);
    // Source.Creator.Organization.Address
    organization.setAddress("Ümläüte ßind schön. à bientôt!");
    // Source.Creator.Organization.Identifier
    try {
      organization.setIdentifier(PropertyReader.getProperty("framework.organizational_unit.id"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    creator.setOrganization(organization);
    source.getCreators().add(creator);
    // Source.Volume
    source.setVolume("8a");
    // Source.Issue
    source.setIssue("13b");
    // Source.StartPage
    source.setStartPage("-12");
    // Source.EndPage
    source.setEndPage("131313");
    // Source.SequenceNumber
    source.setSequenceNumber("1-3-6");
    // Source.PublishingInfo
    pubInfo = new PublishingInfoVO();
    // Source.PublishingInfo.Publisher
    pubInfo.setPublisher("Martas Druckerei");
    // Source.PublishingInfo.Edition
    pubInfo.setEdition("III");
    // Source.PublishingInfo.Place
    pubInfo.setPlace("Hamburg-München");
    source.setPublishingInfo(pubInfo);
    // Source.Identifier
    source.getIdentifiers().add(new IdentifierVO(IdType.ISBN, "XY-347H-112"));
    // Source.Source
    source.getSources().add(new SourceVO(new TextVO("The source of the source.", "en")));
    CreatorVO sourceSourceCreator = new CreatorVO(new OrganizationVO(), CreatorRole.ARTIST);
    name.setValue("Creator of the Source of the source");
    name.setLanguage("en");
    sourceSourceCreator.getOrganization().setName(name);
    try {
      sourceSourceCreator.getOrganization().setIdentifier(
          PropertyReader.getProperty("framework.organizational_unit.id"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    source.getSources().get(0).getCreators().add(sourceSourceCreator);
    mds.getSources().add(source);

    // Event
    EventVO event = new EventVO();
    // Event.Title
    event.setTitle(new TextVO("Weekly progress meeting", "en"));
    // Event.AlternativeTitle
    event.getAlternativeTitles().add(new TextVO("Wöchentliches Fortschrittsmeeting", "de"));
    // Event.StartDate
    event.setStartDate("2004-11-11");
    // Event.EndDate
    event.setEndDate("2005-02-19");
    // Event.Place
    name.setValue("Köln");
    name.setLanguage("de");
    event.setPlace(name);
    // Event.InvitationStatus
    event.setInvitationStatus(InvitationStatus.INVITED);
    mds.setEvent(event);

    // Total Numeber of Pages
    mds.setTotalNumberOfPages("999");

    // Degree
    mds.setDegree(DegreeType.MASTER);

    // Abstract
    mds.getAbstracts().add(new TextVO("Dies ist die Zusammenfassung der Veröffentlichung.", "de"));
    mds.getAbstracts().add(new TextVO("This is the summary of the publication.", "en"));

    // Subject
    name.setValue("wichtig,wissenschaftlich,spannend");
    name.setLanguage("de");
    mds.setFreeKeywords(name);

    // Table of Contents
    name.setValue("1.Einleitung 2.Inhalt");
    name.setLanguage("de");
    mds.setTableOfContents(name);

    // Location
    mds.setLocation("IPP, Garching");

    return mds;
  }

  protected URL uploadFile(String filename, String mimetype, String userHandle) throws Exception {
    // Prepare the HttpMethod.
    String fwUrl = PropertyReader.getFrameworkUrl();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");

    method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(filename)));
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

    // Execute the method with HttpClient.
    HttpClient client = new HttpClient();
    client.executeMethod(method);
    String response = method.getResponseBodyAsString();

    return xmlTransforming.transformUploadResponseToFileURL(response);

  }

  private FulltextSearchResult doFulltextSearch(SRWPort searchHandler, String snippet,
      String searchString, String objectId) throws Exception {
    SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
    FulltextSearchResult result = new FulltextSearchResult();

    searchRetrieveRequest.setVersion("1.1");
    searchRetrieveRequest.setQuery(getQuery(snippet, searchString));
    searchRetrieveRequest.setRecordPacking("xml");

    logger.info("searchRetrieveRequest query <" + searchRetrieveRequest.getQuery() + ">");

    SearchRetrieveResponseType searchResult =
        searchHandler.searchRetrieveOperation(searchRetrieveRequest);
    if (searchResult.getDiagnostics() != null) {
      // something went wrong
      for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic()) {
        logger.info("diagnostic <" + diagnostic.getDetails() + ">");
      }
    }

    if (searchResult.getNumberOfRecords().intValue() > 0) {
      logger.info("Found <" + searchResult.getNumberOfRecords().intValue() + "> hits for <"
          + searchRetrieveRequest.getQuery() + ">");
      result.setNumberOfHits(searchResult.getNumberOfRecords().intValue());

      if ("".equals(objectId)) {
        result.setFound(true);
        return result;
      }

      try {
        for (int i = 0; i < searchResult.getRecords().getRecord().length; i++) {
          MessageElement[] elements =
              searchResult.getRecords().getRecord()[i].getRecordData().get_any(); // recordData

          for (MessageElement e : elements) {
            String msg = e.getAsString();
            SearchResultElement sr = xmlTransforming.transformToSearchResult(msg); // search-result-record

            float score = sr.getScore();

            String currentObjectId = ((ItemResultVO) sr).getLatestVersion().getObjectId();
            int currentVersion = ((ItemResultVO) sr).getVersion().getVersionNumber();
            logger.info("objId found <" + currentObjectId + "> searching <" + objectId + ">");
            if (((ItemResultVO) sr).getLatestVersion().getObjectId().equals(objectId)) {
              result.setFound(true);
              result.setVersionNumber(currentVersion);
              break;
            }
          }
        }
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return result;
  }

  private class FulltextSearchResult {
    private boolean found = false;
    private int numberOfHits = 0;
    private int versionNumber = 0;

    public boolean isFound() {
      return found;
    }

    public void setFound(boolean found) {
      this.found = found;
    }

    public int getNumberOfHits() {
      return numberOfHits;
    }

    public void setNumberOfHits(int numberOfHits) {
      this.numberOfHits = numberOfHits;
    }

    public int getVersionNumber() {
      return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
      this.versionNumber = versionNumber;
    }
  }

}
