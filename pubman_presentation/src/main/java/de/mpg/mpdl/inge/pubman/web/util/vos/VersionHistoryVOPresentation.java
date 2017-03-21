package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

@SuppressWarnings("serial")
public class VersionHistoryVOPresentation extends VersionHistoryEntryVO {
  private static final Logger logger = Logger.getLogger(VersionHistoryVOPresentation.class);

  private List<EventLogEntryVOPresentation> eventLogEntries;

  public VersionHistoryVOPresentation(VersionHistoryEntryVO versionHistoryEntryVO) {
    this.eventLogEntries = new ArrayList<EventLogEntryVOPresentation>();
    this.setEvents(versionHistoryEntryVO.getEvents());
    this.setModificationDate(versionHistoryEntryVO.getModificationDate());
    this.setReference(versionHistoryEntryVO.getReference());
    this.setState(versionHistoryEntryVO.getState());

    for (EventLogEntryVO event : getEvents()) {
      this.eventLogEntries.add(new EventLogEntryVOPresentation(event, this));
    }
  }

  public List<EventLogEntryVOPresentation> getEventLogEntries() {
    return this.eventLogEntries;
  }

  /**
   * JSF action to rollback the item to this version.
   * 
   * @return Nothing
   */
  public String rollback() throws Exception {
    logger.info("Rollback to version " + this.getReference().getVersionNumber());

    LoginHelper loginHelper = FacesTools.findBean("LoginHelper");

    ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());

    // Get the two versions
    String xmlItemLatestVersion = itemHandler.retrieve(this.getReference().getObjectId());
    String xmlItemThisVersion = itemHandler.retrieve(this.getReference().getObjectIdAndVersion());
    PubItemVO pubItemVOLatestVersion =
        XmlTransformingService.transformToPubItem(xmlItemLatestVersion);
    PubItemVO pubItemVOThisVersion = XmlTransformingService.transformToPubItem(xmlItemThisVersion);

    // Now copy the old stuff into the current item
    pubItemVOLatestVersion.getMetadataSets().set(0, pubItemVOThisVersion.getMetadata());
    pubItemVOLatestVersion.getLocalTags().clear();
    pubItemVOLatestVersion.getLocalTags().addAll(pubItemVOThisVersion.getLocalTags());

    // Do not forget the files and locators
    pubItemVOLatestVersion.getFiles().clear();
    for (FileVO fileVO : pubItemVOThisVersion.getFiles()) {
      FileVO clonedFile = new FileVO(fileVO);
      clonedFile.setReference(fileVO.getReference());
      pubItemVOLatestVersion.getFiles().add(clonedFile);
    }

    // Then process it into the framework ...
    String xmlItemNewVersion = XmlTransformingService.transformToItem(pubItemVOLatestVersion);
    xmlItemNewVersion = itemHandler.update(this.getReference().getObjectId(), xmlItemNewVersion);
    PubItemVO pubItemVONewVersion = XmlTransformingService.transformToPubItem(xmlItemNewVersion);

    if (pubItemVOLatestVersion.getVersion().getState() == State.RELEASED
        && pubItemVONewVersion.getVersion().getState() == State.PENDING) {
      pubItemVONewVersion =
          PubItemService.submitPubItem(pubItemVONewVersion,
              "Submit and release after rollback to version "
                  + this.getReference().getVersionNumber(), loginHelper.getAccountUser());
      PubItemService.releasePubItem(pubItemVONewVersion.getVersion(),
          pubItemVONewVersion.getModificationDate(),
          "Submit and release after rollback to version " + this.getReference().getVersionNumber(),
          loginHelper.getAccountUser());

      xmlItemNewVersion = itemHandler.retrieve(this.getReference().getObjectId());
      pubItemVONewVersion = XmlTransformingService.transformToPubItem(xmlItemNewVersion);
    }

    // ... and set the new version as current item in PubMan
    ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean"))
        .setCurrentPubItem(new PubItemVOPresentation(pubItemVONewVersion));

    ViewItemFull viewItemFull = (ViewItemFull) FacesTools.findBean("ViewItemFull");
    viewItemFull.setPubItem(new PubItemVOPresentation(pubItemVONewVersion));
    viewItemFull.init();

    return ViewItemFull.LOAD_VIEWITEM;
  }
}
