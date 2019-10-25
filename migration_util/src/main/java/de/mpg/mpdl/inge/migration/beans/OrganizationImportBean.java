package de.mpg.mpdl.inge.migration.beans;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

@Component
public class OrganizationImportBean {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;
  @Value("${ous.path}")
  private String ousPath;
  @Autowired
  private OrganizationRepository orgRepository;
  @Autowired
  private MigrationUtilBean utils;
  @Autowired
  private Reindexing reindexing;

  private Queue<AffiliationVO> updateLaterAffs = new LinkedList<AffiliationVO>();

  public void importAffs() throws Exception {
    URI uri = new URIBuilder(escidocUrl + ousPath).addParameter("query", "\"/id\"=\"e*\" not \"/parents/parent/id\">\"''\"").build();
    log.info(uri.toString());
    String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

    try {
      SearchRetrieveResponseVO<AffiliationVO> ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);

      log.info(ouList.getNumberOfRecords() + " ous were found");
      saveOuList(ouList);

      uri = new URIBuilder(escidocUrl + ousPath).build();
      log.info(uri.toString());
      ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);
      ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);

      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : ouList.getRecords()) {
        updateOUWithPredecessors(affRecord.getData());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void saveChildren4GivenParent(String motherId) {
    URI uri;
    AffiliationDbVO newVo = null;
    try {
      uri = new URIBuilder(escidocUrl + ousPath).addParameter("query", "\"/parents/parent/id\"=\"" + motherId + "\"").build();
      String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);
      SearchRetrieveResponseVO<AffiliationVO> ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);
      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : ouList.getRecords()) {
        String href = affRecord.getData().getReference().getObjectId();
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());
        // saveOuWithoutPredecessor(affRecord.getData());
        newVo = transformToNew(affRecord.getData());
        newVo.getPredecessorAffiliations().clear();
        log.info("Saving " + newVo.getObjectId());
        orgRepository.save(newVo);
        reindexing.reindexOU(newVo.getObjectId());
      }
    } catch (URISyntaxException | IOException | TechnicalException e) {
      log.error("error getting children 4" + motherId, e);
    } catch (Exception e) {
      log.error("error reindexing " + newVo.getObjectId(), e);
    }
    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      log.error("how can you sleep whilst the world is burnin'", e);
    }
  }

  private void saveOuList(SearchRetrieveResponseVO<AffiliationVO> srr) throws Exception {
    if (srr.getNumberOfRecords() > 0) {
      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : srr.getRecords()) {

        String href = affRecord.getData().getReference().getObjectId();
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());

        saveOuWithoutPredecessor(affRecord.getData());

        URI uri = new URIBuilder(escidocUrl + ousPath).addParameter("query", "\"/parents/parent/id\"=\"" + objectId + "\"").build();
        String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

        SearchRetrieveResponseVO<AffiliationVO> ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);
        saveOuList(ouList);

      }
    }
  }

  private void saveOuWithoutPredecessor(AffiliationVO affVo) throws Exception {

    AffiliationDbVO newVo = transformToNew(affVo);
    newVo.getPredecessorAffiliations().clear();
    log.info("Saving " + newVo.getObjectId());
    orgRepository.save(newVo);
    updateLaterAffs.add(affVo);
  }

  private void updateOUWithPredecessors(AffiliationVO affVo) {
    String id = utils.changeId("ou", affVo.getReference().getObjectId());
    if (!affVo.getPredecessorAffiliations().isEmpty()) {
      AffiliationDbVO newVo = orgRepository.findOne(id);
      for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(utils.changeId("ou", oldAffRo.getObjectId()));
        newAffRo.setName(oldAffRo.getTitle());

        newVo.getPredecessorAffiliations().add(newAffRo);
      }
      log.info("Updating " + newVo.getObjectId());
      orgRepository.save(newVo);
    }
  }

  private AffiliationDbVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.AffiliationVO affVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(utils.changeId("user", affVo.getCreator().getObjectId()));
    // owner.setName(affVo.getCreator().getTitle());
    modifier.setObjectId(utils.changeId("user", affVo.getModifiedBy().getObjectId()));
    // modifier.setName(affVo.getModifiedBy().getTitle());

    AffiliationDbVO newAff = new AffiliationDbVO();
    newAff.setCreationDate(affVo.getCreationDate());

    newAff.setCreator(owner);
    // newAff.setHasChildren(affVo.getHasChildren());
    newAff.setLastModificationDate(affVo.getLastModificationDate());
    newAff.setMetadata(affVo.getDefaultMetadata());
    newAff.setModifier(modifier);
    newAff.setName(affVo.getDefaultMetadata().getName());
    newAff.setObjectId(utils.changeId("ou", affVo.getReference().getObjectId()));

    for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(utils.changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getPredecessorAffiliations().add(newAffRo);
    }
    for (AffiliationRO oldAffRo : affVo.getParentAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(utils.changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.setParentAffiliation(newAffRo);
    }

    newAff.setPublicStatus(AffiliationDbVO.State.valueOf(affVo.getPublicStatus().toUpperCase()));
    return newAff;

  }

}
