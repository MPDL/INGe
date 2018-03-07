package de.mpg.mpdl.inge.migration.beans;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO.Workflow;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

@Component
public class ContextImportBean {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;
  @Value("${contexts.path}")
  private String contextsPath;
  @Autowired
  private ContextRepository contextRepository;
  @Autowired
  private MigrationUtilBean utils;

  public void importContexts() throws Exception {
    URI uri = new URIBuilder(escidocUrl + contextsPath).addParameter("maximumRecords", "5000").build();
    log.info(uri.toString());
    String contextXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

    try {
      SearchRetrieveResponseVO<de.mpg.mpdl.inge.model.valueobjects.ContextVO> contextList =
          XmlTransformingService.transformToSearchRetrieveResponse(contextXml);

      for (SearchRetrieveRecordVO<de.mpg.mpdl.inge.model.valueobjects.ContextVO> rec : contextList.getRecords()) {
        saveContext(rec.getData());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveContext(de.mpg.mpdl.inge.model.valueobjects.ContextVO context) throws Exception {
    try {
      ContextDbVO newVo = transformToNew(context);
      log.info("Saving " + newVo.getObjectId());
      contextRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ContextDbVO transformToNew(ContextVO contextVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(utils.changeId("user", contextVo.getCreator().getObjectId()));
    owner.setName(contextVo.getCreator().getTitle());

    modifier.setObjectId(utils.changeId("user", contextVo.getModifiedBy().getObjectId()));
    modifier.setName(contextVo.getModifiedBy().getTitle());

    ContextDbVO newContext = new ContextDbVO();
    newContext.setCreator(owner);
    newContext.setCreationDate(contextVo.getCreationDate());
    newContext.setLastModificationDate(contextVo.getLastModificationDate());
    newContext.setModifier(modifier);
    newContext.setDescription(contextVo.getDescription());
    newContext.setName(contextVo.getName());
    newContext.setObjectId(utils.changeId("ctx", contextVo.getReference().getObjectId()));

    newContext.setState(ContextDbVO.State.valueOf(contextVo.getState().name()));

    newContext.setAllowedGenres(contextVo.getAdminDescriptor().getAllowedGenres());
    newContext.setAllowedSubjectClassifications(contextVo.getAdminDescriptor().getAllowedSubjectClassifications());
    newContext.setContactEmail(contextVo.getAdminDescriptor().getContactEmail());
    newContext.setWorkflow(Workflow.valueOf(contextVo.getAdminDescriptor().getWorkflow().name()));

    for (AffiliationRO oldAffRo : contextVo.getResponsibleAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(utils.changeId("ou", utils.changeId("ou", oldAffRo.getObjectId())));
      newAffRo.setName(oldAffRo.getTitle());
      newContext.getResponsibleAffiliations().add(newAffRo);
    }

    return newContext;
  }

}
