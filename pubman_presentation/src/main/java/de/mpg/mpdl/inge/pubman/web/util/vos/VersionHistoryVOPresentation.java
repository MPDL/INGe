package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

@SuppressWarnings("serial")
public class VersionHistoryVOPresentation extends VersionHistoryEntryVO {
  private static final Logger logger = Logger.getLogger(VersionHistoryVOPresentation.class);

  private final List<EventLogEntryVOPresentation> eventLogEntries;



  public VersionHistoryVOPresentation(VersionHistoryEntryVO versionHistoryEntryVO) {
    this.eventLogEntries = new ArrayList<EventLogEntryVOPresentation>();
    this.setEvents(versionHistoryEntryVO.getEvents());
    this.setModificationDate(versionHistoryEntryVO.getModificationDate());
    this.setReference(versionHistoryEntryVO.getReference());
    this.setState(versionHistoryEntryVO.getState());

    for (final EventLogEntryVO event : this.getEvents()) {
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
    VersionHistoryVOPresentation.logger.info("Rollback to version "
        + this.getReference().getVersionNumber());

    final LoginHelper loginHelper = FacesTools.findBean("LoginHelper");

    final de.mpg.mpdl.inge.service.pubman.PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();
   

    // Get the two versions
    final PubItemVO pubItemVOLatestVersion = pubItemService.get(this.getReference().getObjectId(), loginHelper.getAuthenticationToken());
    final PubItemVO pubItemVOThisVersion =
        pubItemService.get(this.getReference().getObjectIdAndVersion(), loginHelper.getAuthenticationToken());


    // Now copy the old stuff into the current item
    pubItemVOLatestVersion.getMetadataSets().set(0, pubItemVOThisVersion.getMetadata());
    pubItemVOLatestVersion.getLocalTags().clear();
    pubItemVOLatestVersion.getLocalTags().addAll(pubItemVOThisVersion.getLocalTags());

    // Do not forget the files and locators
    pubItemVOLatestVersion.getFiles().clear();
    for (final FileVO fileVO : pubItemVOThisVersion.getFiles()) {
      final FileVO clonedFile = new FileVO(fileVO);
      clonedFile.setReference(fileVO.getReference());
      pubItemVOLatestVersion.getFiles().add(clonedFile);
    }

    // Then process it into the framework ...
    PubItemVO pubItemVONewVersion = pubItemService.update(pubItemVOLatestVersion, loginHelper.getAuthenticationToken());

    if (pubItemVOLatestVersion.getVersion().getState() == State.RELEASED
        && pubItemVONewVersion.getVersion().getState() == State.PENDING) {
      pubItemVONewVersion = ApplicationBean.INSTANCE.getPubItemService().submitPubItem(pubItemVONewVersion.getVersion().getObjectId(), "Submit and release after rollback to version "+ this.getReference().getVersionNumber(), loginHelper.getAuthenticationToken());
      pubItemVONewVersion = ApplicationBean.INSTANCE.getPubItemService().releasePubItem(pubItemVONewVersion.getVersion().getObjectId(), "Submit and release after rollback to version "+ this.getReference().getVersionNumber(), loginHelper.getAuthenticationToken());

    }

    // ... and set the new version as current item in PubMan
    ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean"))
        .setCurrentPubItem(new PubItemVOPresentation(pubItemVONewVersion));

    final ViewItemFull viewItemFull = (ViewItemFull) FacesTools.findBean("ViewItemFull");
    viewItemFull.setPubItem(new PubItemVOPresentation(pubItemVONewVersion));
    viewItemFull.init();

    return ViewItemFull.LOAD_VIEWITEM;
  }
}
