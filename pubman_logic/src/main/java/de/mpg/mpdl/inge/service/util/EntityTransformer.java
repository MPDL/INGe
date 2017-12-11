package de.mpg.mpdl.inge.service.util;


import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.FileRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class EntityTransformer {

  public static ContextDbVO transformToNew(ContextVO contextVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", contextVo.getCreator().getObjectId()));
    owner.setName(contextVo.getCreator().getTitle());

    modifier.setObjectId(changeId("user", contextVo.getModifiedBy().getObjectId()));
    modifier.setName(contextVo.getModifiedBy().getTitle());

    ContextDbVO newContext = new ContextDbVO();
    newContext.setCreator(owner);
    newContext.setCreationDate(contextVo.getCreationDate());
    newContext.setLastModificationDate(contextVo.getLastModificationDate());
    newContext.setModifier(modifier);
    newContext.setDescription(contextVo.getDescription());
    newContext.setName(contextVo.getName());
    newContext.setObjectId(changeId("ctx", contextVo.getReference().getObjectId()));

    newContext.setState(ContextDbVO.State.valueOf(contextVo.getState().name()));
    newContext.setType(contextVo.getType());

    newContext.setAdminDescriptor(contextVo.getAdminDescriptor());


    for (de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRo : contextVo.getResponsibleAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", changeId("ou", oldAffRo.getObjectId())));
      newAffRo.setName(oldAffRo.getTitle());
      newContext.getResponsibleAffiliations().add(newAffRo);
    }



    return newContext;


  }


  public static PubItemDbRO transformToNew(PubItemVO itemVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", itemVo.getOwner().getObjectId()));
    owner.setName(itemVo.getOwner().getTitle());

    modifier.setObjectId(changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));
    modifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());


    PubItemVersionDbVO newPubItem = new PubItemVersionDbVO();
    for (de.mpg.mpdl.inge.model.valueobjects.FileVO oldFile : itemVo.getFiles()) {

      AccountUserDbRO fileOwner = new AccountUserDbRO();
      // AccountUserRO fileModifier = new AccountUserRO();

      fileOwner.setObjectId(changeId("user", oldFile.getCreatedByRO().getObjectId()));
      fileOwner.setName(oldFile.getCreatedByRO().getTitle());

      // fileModifier.setObjectId(changeId("user", oldFile.getM.getObjectId()));
      // fileModifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());

      FileDbVO file = new FileDbVO();
      file.setChecksum(oldFile.getChecksum());
      file.setChecksumAlgorithm(FileDbVO.ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
      file.setContent(oldFile.getContent());
      file.setContentCategory(oldFile.getContentCategory());
      file.setCreationDate(oldFile.getCreationDate());
      file.setCreator(fileOwner);
      file.setDescription(oldFile.getDescription());
      file.setLastModificationDate(oldFile.getLastModificationDate());
      file.setMetadata(oldFile.getDefaultMetadata());
      file.setMimeType(oldFile.getMimeType());
      // file.setModifier(oldFile.getM);
      file.setName(oldFile.getName());
      file.setObjectId(changeId("file", oldFile.getReference().getObjectId()));
      file.setPid(oldFile.getPid());
      file.setStorage(FileDbVO.Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(FileDbVO.Visibility.valueOf(oldFile.getVisibility().name()));

      newPubItem.getFiles().add(file);
    }



    newPubItem.setLastMessage(itemVo.getVersion().getLastMessage());
    newPubItem.setMetadata(itemVo.getMetadata());
    newPubItem.setModificationDate(itemVo.getVersion().getModificationDate());
    newPubItem.setModifiedBy(owner);
    newPubItem.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    newPubItem.setState(PubItemDbRO.State.valueOf(itemVo.getVersion().getState().name()));
    newPubItem.setVersionNumber(itemVo.getVersion().getVersionNumber());
    newPubItem.setVersionPid(itemVo.getVersion().getPid());


    PubItemObjectDbVO pubItemObject = new PubItemObjectDbVO();
    newPubItem.setObject(pubItemObject);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(changeId("ctx", itemVo.getContext().getObjectId()));
    pubItemObject.setContext(context);

    pubItemObject.setCreationDate(itemVo.getCreationDate());
    pubItemObject.setLastModificationDate(itemVo.getLatestVersion().getModificationDate());

    if (itemVo.getLatestRelease() != null) {
      PubItemDbRO pubItemRo = new PubItemDbRO();
      pubItemRo.setObjectId(itemVo.getLatestRelease().getObjectId());
      pubItemRo.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
      pubItemObject.setLatestRelease(pubItemRo);
    }

    PubItemDbRO pubItemRo = new PubItemDbRO();
    pubItemRo.setObjectId(itemVo.getLatestVersion().getObjectId());
    pubItemRo.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
    pubItemObject.setLatestRelease(pubItemRo);
    pubItemObject.setLatestVersion(newPubItem);



    pubItemObject.setLocalTags(itemVo.getLocalTags());
    pubItemObject.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    pubItemObject.setOwner(owner);
    pubItemObject.setPid(itemVo.getPid());
    pubItemObject.setPublicStatus(PubItemDbRO.State.valueOf(itemVo.getPublicStatus().name()));
    pubItemObject.setPublicStatusComment(itemVo.getPublicStatusComment());

    return newPubItem;


  }



  public static AffiliationDbVO transformToNew(AffiliationVO affVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", affVo.getCreator().getObjectId()));
    owner.setName(affVo.getCreator().getTitle());
    modifier.setObjectId(changeId("user", affVo.getModifiedBy().getObjectId()));
    modifier.setName(affVo.getModifiedBy().getTitle());

    AffiliationDbVO newAff = new AffiliationDbVO();
    newAff.setCreationDate(affVo.getCreationDate());

    newAff.setCreator(owner);
    newAff.setHasChildren(affVo.getHasChildren());
    newAff.setLastModificationDate(affVo.getLastModificationDate());
    newAff.setMetadata(affVo.getDefaultMetadata());
    newAff.setModifier(modifier);
    newAff.setName(affVo.getDefaultMetadata().getName());
    newAff.setObjectId(changeId("ou", affVo.getReference().getObjectId()));


    for (de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getPredecessorAffiliations().add(newAffRo);
    }

    if (affVo.getParentAffiliations().size() > 0) {
      AffiliationRO oldAffRo = affVo.getParentAffiliations().get(0);
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.setParentAffiliation(newAffRo);
    }


    newAff.setPublicStatus(AffiliationDbVO.State.valueOf(affVo.getPublicStatus().toUpperCase()));
    return newAff;


  }

  private static ItemVO.State transformToOld(PubItemDbRO.State state) {
    return ItemVO.State.valueOf(state.name());
  }

  private static AccountUserRO transformToOld(AccountUserDbRO newAccountUserRo) {
    if (newAccountUserRo == null) {
      return null;
    }

    AccountUserRO modifier = new AccountUserRO();
    modifier.setObjectId(newAccountUserRo.getObjectId());
    modifier.setTitle(newAccountUserRo.getName());

    return modifier;
  }

  private static ItemRO transformToOld(PubItemDbRO newItemRo) {
    ItemRO oldItemRo = new ItemRO();
    oldItemRo.setObjectId(newItemRo.getObjectId());
    oldItemRo.setLastMessage(newItemRo.getLastMessage());
    oldItemRo.setModificationDate(newItemRo.getModificationDate());
    oldItemRo.setModifiedByRO(transformToOld(newItemRo.getModifiedBy()));
    oldItemRo.setObjectId(newItemRo.getObjectId());
    oldItemRo.setPid(newItemRo.getVersionPid());

    if (newItemRo.getState() != null) {
      oldItemRo.setState(ItemVO.State.valueOf(newItemRo.getState().name()));
    }

    oldItemRo.setTitle(null);// TODO
    oldItemRo.setVersionNumber(newItemRo.getVersionNumber());

    return oldItemRo;
  }

  private static FileVO transformToOld(FileDbVO newFileVo) {
    FileVO oldFileVo = new FileVO();
    oldFileVo.setChecksum(newFileVo.getChecksum());
    // TODO
    // oldFileVo.setChecksumAlgorithm(FileVO.ChecksumAlgorithm.valueOf(newFileVo
    // .getChecksumAlgorithm().name()));
    oldFileVo.setContent(newFileVo.getContent());
    oldFileVo.setContentCategory(newFileVo.getContentCategory());
    oldFileVo.setCreatedByRO(transformToOld(newFileVo.getCreator()));
    oldFileVo.setCreationDate(newFileVo.getCreationDate());
    oldFileVo.setDefaultMetadata(newFileVo.getMetadata());
    oldFileVo.setDescription(newFileVo.getDescription());
    oldFileVo.setLastModificationDate(newFileVo.getLastModificationDate());
    oldFileVo.setMimeType(newFileVo.getMimeType());
    oldFileVo.setName(newFileVo.getName());
    oldFileVo.setPid(newFileVo.getPid());
    oldFileVo.setLocalFileIdentifier(newFileVo.getLocalFileIdentifier());

    FileRO oldFileRo = new FileRO();
    oldFileRo.setObjectId(newFileVo.getObjectId());
    oldFileRo.setTitle(newFileVo.getName());
    oldFileVo.setReference(oldFileRo);

    oldFileVo.setStorage(FileVO.Storage.valueOf(newFileVo.getStorage().name()));
    oldFileVo.setVisibility(FileVO.Visibility.valueOf(newFileVo.getVisibility().name()));

    return oldFileVo;
  }

  private static ContextRO transformToOld(ContextDbRO newContextRo) {
    ContextRO context = new ContextRO();
    context.setObjectId(newContextRo.getObjectId());
    context.setTitle(newContextRo.getName());

    return context;
  }

  public static PubItemVO transformToOld(PubItemVersionDbVO itemVo) {
    if (itemVo == null) {
      return null;
    }

    PubItemVO oldPubItem = new PubItemVO();
    oldPubItem.setContentModel("escidoc:persistent4");
    oldPubItem.setContext(transformToOld(itemVo.getObject().getContext()));
    oldPubItem.setCreationDate(itemVo.getObject().getCreationDate());

    if (itemVo.getObject().getLatestRelease() != null) {
      oldPubItem.setLatestRelease(transformToOld(itemVo.getObject().getLatestRelease()));
    }

    oldPubItem.setLatestVersion(transformToOld(itemVo.getObject().getLatestVersion()));
    oldPubItem.setMetadata(itemVo.getMetadata());
    oldPubItem.setOwner(transformToOld(itemVo.getObject().getOwner()));
    oldPubItem.setPid(itemVo.getObject().getPid());
    oldPubItem.setPublicStatus(transformToOld(itemVo.getObject().getPublicStatus()));
    oldPubItem.setPublicStatusComment(itemVo.getObject().getPublicStatusComment());
    oldPubItem.setVersion(transformToOld((PubItemDbRO) itemVo));

    for (String localTag : itemVo.getObject().getLocalTags()) {
      oldPubItem.getLocalTags().add(localTag);
    }

    for (FileDbVO newFile : itemVo.getFiles()) {
      oldPubItem.getFiles().add(transformToOld(newFile));
    }

    return oldPubItem;
  }

  public static ContextVO transformToOld(ContextDbVO newContextVo) {
    if (newContextVo == null) {
      return null;
    }

    ContextVO oldContextVo = new ContextVO();
    oldContextVo.setAdminDescriptor(newContextVo.getAdminDescriptor());
    oldContextVo.setCreationDate(newContextVo.getCreationDate());
    oldContextVo.setCreator(transformToOld(newContextVo.getCreator()));
    oldContextVo.setDescription(newContextVo.getDescription());
    oldContextVo.setLastModificationDate(newContextVo.getLastModificationDate());
    oldContextVo.setModifiedBy(transformToOld(newContextVo.getModifier()));
    oldContextVo.setName(newContextVo.getName());
    oldContextVo.setReference(transformToOld((ContextDbRO) newContextVo));
    oldContextVo.setState(ContextVO.State.valueOf(newContextVo.getState().name()));
    oldContextVo.setType(newContextVo.getType());

    for (AffiliationDbRO aff : newContextVo.getResponsibleAffiliations()) {
      oldContextVo.getResponsibleAffiliations().add(transformToOld(aff));
    }

    return oldContextVo;
  }

  private static AffiliationRO transformToOld(AffiliationDbRO newAffiliationRO) {
    if (newAffiliationRO == null) {
      return null;
    }

    AffiliationRO oldAffRO = new AffiliationRO();
    oldAffRO.setObjectId(newAffiliationRO.getObjectId());
    oldAffRO.setTitle(newAffiliationRO.getName());

    return oldAffRO;
  }

  public static AffiliationVO transformToOld(AffiliationDbVO newAffVo) {
    if (newAffVo == null) {
      return null;
    }

    AffiliationVO oldAffVo = new AffiliationVO();
    oldAffVo.setCreationDate(newAffVo.getCreationDate());
    oldAffVo.setLastModificationDate(newAffVo.getLastModificationDate());
    oldAffVo.setCreator(transformToOld(newAffVo.getCreator()));
    oldAffVo.setDefaultMetadata(newAffVo.getMetadata());
    oldAffVo.setHasChildren(newAffVo.getHasChildren());
    oldAffVo.setModifiedBy(transformToOld(newAffVo.getModifier()));

    for (AffiliationDbRO predecessor : newAffVo.getPredecessorAffiliations()) {
      oldAffVo.getPredecessorAffiliations().add(transformToOld((AffiliationDbRO) predecessor));
    }

    if (newAffVo.getParentAffiliation() != null) {
      oldAffVo.getParentAffiliations().add(transformToOld((AffiliationDbRO) newAffVo.getParentAffiliation()));
    }

    oldAffVo.setPublicStatus(newAffVo.getPublicStatus().name());
    oldAffVo.setReference(transformToOld((AffiliationDbRO) newAffVo));

    return oldAffVo;
  }

  public static List<VersionHistoryEntryVO> transformToVersionHistory(List<AuditDbVO> auditList) {
    if (auditList == null) {
      return null;
    }

    List<VersionHistoryEntryVO> vhList = new ArrayList<>();

    VersionHistoryEntryVO vhEntry = null;

    for (AuditDbVO audit : auditList) {

      if (vhEntry == null || audit.getPubItem().getVersionNumber() != vhEntry.getReference().getVersionNumber()) {

        vhEntry = new VersionHistoryEntryVO();
        vhEntry.setModificationDate(audit.getModificationDate());

        ItemRO ref = new ItemRO();
        ref.setObjectId(audit.getPubItem().getObjectId());
        ref.setVersionNumber(audit.getPubItem().getVersionNumber());
        ref.setLastMessage(audit.getComment());

        vhEntry.setReference(ref);
        vhEntry.setState(ItemVO.State.valueOf(audit.getPubItem().getState().name()));
        vhEntry.setEvents(new ArrayList<EventLogEntryVO>());

        vhList.add(vhEntry);
      }

      EventLogEntryVO event = new EventLogEntryVO();
      event.setComment(audit.getComment());
      event.setDate(audit.getModificationDate());

      switch (audit.getEvent()) {
        case CREATE: {
          event.setType(EventLogEntryVO.EventType.CREATE);
          break;
        }
        case RELEASE: {
          event.setType(EventLogEntryVO.EventType.RELEASE);
          break;
        }
        case SUBMIT: {
          event.setType(EventLogEntryVO.EventType.SUBMIT);
          break;
        }
        case REVISE: {
          event.setType(EventLogEntryVO.EventType.IN_REVISION);
          break;
        }
        case UPDATE: {
          event.setType(EventLogEntryVO.EventType.UPDATE);
          break;
        }
        case WITHDRAW: {
          event.setType(EventLogEntryVO.EventType.WITHDRAW);
          break;
        }
      }

      vhEntry.getEvents().add(event);
    }

    return vhList;
  }

  public static AccountUserVO transformToOld(AccountUserDbVO newAccountUser) {
    if (newAccountUser == null) {
      return null;
    }

    AccountUserVO oldAccountUser = new AccountUserVO();
    oldAccountUser.setCreationDate(newAccountUser.getCreationDate());
    oldAccountUser.setCreator(transformToOld(newAccountUser.getCreator()));
    oldAccountUser.setLastModificationDate(newAccountUser.getLastModificationDate());
    oldAccountUser.setModifiedBy(transformToOld(newAccountUser.getModifier()));
    oldAccountUser.setName(newAccountUser.getName());
    oldAccountUser.setActive(newAccountUser.isActive());
    oldAccountUser.setEmail(newAccountUser.getEmail());
    oldAccountUser.setUserid(newAccountUser.getLoginname());

    if (newAccountUser.getAffiliation() != null) {
      AffiliationRO affRo = new AffiliationRO();
      affRo.setObjectId(newAccountUser.getAffiliation().getObjectId());
      affRo.setTitle(newAccountUser.getAffiliation().getName());
      oldAccountUser.getAffiliations().add(affRo);
    }

    AccountUserRO userRo = new AccountUserRO();
    userRo.setObjectId(newAccountUser.getObjectId());
    userRo.setTitle(newAccountUser.getName());

    oldAccountUser.setReference(userRo);
    oldAccountUser.getGrants().clear();
    if (newAccountUser.getGrantList() != null) {
      oldAccountUser.getGrants().addAll(newAccountUser.getGrantList());
    }


    return oldAccountUser;
  }

  private static String changeId(String prefix, String href) {
    return href;
    // return href.substring(href.lastIndexOf("/")+1, href.length()).replaceAll("escidoc:", prefix +
    // "_").replaceAll(":", "_");
  }


}
