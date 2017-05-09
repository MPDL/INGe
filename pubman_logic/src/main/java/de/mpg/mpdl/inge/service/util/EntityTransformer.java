package de.mpg.mpdl.inge.service.util;


import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.db.model.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State;
import de.mpg.mpdl.inge.model.referenceobjects.FileRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class EntityTransformer {

  public static ContextDbVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.ContextVO contextVo) {
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

    newContext.setState(de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO.State.valueOf(contextVo
        .getState().name()));
    newContext.setType(contextVo.getType());

    newContext.setAdminDescriptor(contextVo.getAdminDescriptor());


    for (de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRo : contextVo
        .getResponsibleAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", changeId("ou", oldAffRo.getObjectId())));
      newAffRo.setName(oldAffRo.getTitle());
      newContext.getResponsibleAffiliations().add(newAffRo);
    }



    return newContext;


  }


  public static PubItemDbRO transformToNew(
      de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO itemVo) {
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
      file.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
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
      file.setStorage(Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(Visibility.valueOf(oldFile.getVisibility().name()));

      newPubItem.getFiles().add(file);
    }



    newPubItem.setLastMessage(itemVo.getVersion().getLastMessage());
    newPubItem.setMetadata(itemVo.getMetadata());
    newPubItem.setModificationDate(itemVo.getVersion().getModificationDate());
    newPubItem.setModifiedBy(owner);
    newPubItem.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    newPubItem.setState(State.valueOf(itemVo.getVersion().getState().name()));
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
    pubItemObject.setPublicStatus(State.valueOf(itemVo.getPublicStatus().name()));
    pubItemObject.setPublicStatusComment(itemVo.getPublicStatusComment());

    return newPubItem;


  }



  public static AffiliationDbVO transformToNew(
      de.mpg.mpdl.inge.model.valueobjects.AffiliationVO affVo) {
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


    for (de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRo : affVo
        .getPredecessorAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getPredecessorAffiliations().add(newAffRo);
    }
    for (de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRo : affVo
        .getParentAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getParentAffiliations().add(newAffRo);
    }

    newAff.setPublicStatus(de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO.State
        .valueOf(affVo.getPublicStatus().toUpperCase()));
    return newAff;


  }

  private static de.mpg.mpdl.inge.model.valueobjects.ItemVO.State transformToOld(State state) {
    return de.mpg.mpdl.inge.model.valueobjects.ItemVO.State.valueOf(state.name());
  }

  private static de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO transformToOld(
      AccountUserDbRO newAccountUserRo) {
    de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO modifier =
        new de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO();
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
    oldItemRo.setState(de.mpg.mpdl.inge.model.valueobjects.ItemVO.State.valueOf(newItemRo
        .getState().name()));
    oldItemRo.setTitle(null);// TODO
    oldItemRo.setVersionNumber(newItemRo.getVersionNumber());
    return oldItemRo;

  }

  private static de.mpg.mpdl.inge.model.valueobjects.FileVO transformToOld(FileDbVO newFileVo) {
    de.mpg.mpdl.inge.model.valueobjects.FileVO oldFileVo =
        new de.mpg.mpdl.inge.model.valueobjects.FileVO();
    oldFileVo.setChecksum(newFileVo.getChecksum());
    oldFileVo.setChecksumAlgorithm(de.mpg.mpdl.inge.model.valueobjects.FileVO.ChecksumAlgorithm
        .valueOf(newFileVo.getChecksumAlgorithm().name()));
    oldFileVo.setContent(newFileVo.getContent());
    oldFileVo.setContentCategory(newFileVo.getContentCategory());
    oldFileVo.setCreatedByRO(transformToOld(newFileVo.getCreator()));
    oldFileVo.setCreationDate(newFileVo.getCreationDate());
    oldFileVo.setDefaultMetadata(newFileVo.getMetadata());
    oldFileVo.setDescription(newFileVo.getDescription());
    oldFileVo.setLastModificationDate(newFileVo.getLastModificationDate());
    oldFileVo.setMimeType(new FileDbVO().getMimeType());
    oldFileVo.setName(newFileVo.getName());
    oldFileVo.setPid(new FileDbVO().getPid());

    FileRO oldFileRo = new FileRO();
    oldFileRo.setObjectId(newFileVo.getObjectId());
    oldFileRo.setTitle(newFileVo.getName());
    oldFileVo.setReference(oldFileRo);

    oldFileVo.setStorage(de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage.valueOf(newFileVo
        .getStorage().name()));
    oldFileVo.setVisibility(de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility.valueOf(newFileVo
        .getVisibility().name()));

    return oldFileVo;
  }

  private static de.mpg.mpdl.inge.model.referenceobjects.ContextRO transformToOld(
      ContextDbRO newContextRo) {
    de.mpg.mpdl.inge.model.referenceobjects.ContextRO context =
        new de.mpg.mpdl.inge.model.referenceobjects.ContextRO();
    context.setObjectId(newContextRo.getObjectId());
    context.setTitle(newContextRo.getName());
    return context;
  }

  public static PubItemVO transformToOld(PubItemVersionDbVO itemVo) {

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


    for (FileDbVO newFile : itemVo.getFiles()) {
      oldPubItem.getFiles().add(transformToOld(newFile));
    }

    return oldPubItem;


  }

  public static de.mpg.mpdl.inge.model.valueobjects.ContextVO transformToOld(
      ContextDbVO newContextVo) {

    de.mpg.mpdl.inge.model.valueobjects.ContextVO oldContextVo =
        new de.mpg.mpdl.inge.model.valueobjects.ContextVO();
    oldContextVo.setAdminDescriptor(newContextVo.getAdminDescriptor());
    oldContextVo.setCreationDate(newContextVo.getCreationDate());
    oldContextVo.setCreator(transformToOld(newContextVo.getCreator()));
    oldContextVo.setDescription(newContextVo.getDescription());
    oldContextVo.setLastModificationDate(newContextVo.getLastModificationDate());
    oldContextVo.setModifiedBy(transformToOld(newContextVo.getModifier()));
    oldContextVo.setName(newContextVo.getName());


    oldContextVo.setReference(transformToOld((ContextDbRO) newContextVo));
    oldContextVo.setState(de.mpg.mpdl.inge.model.valueobjects.ContextVO.State.valueOf(newContextVo
        .getState().name()));
    oldContextVo.setType(newContextVo.getType());


    for (AffiliationDbRO aff : newContextVo.getResponsibleAffiliations()) {
      oldContextVo.getResponsibleAffiliations().add(transformToOld(aff));
    }


    return oldContextVo;


  }

  private static de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO transformToOld(
      AffiliationDbRO newAffiliationRO) {
    de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO oldAffRO =
        new de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO();
    oldAffRO.setObjectId(newAffiliationRO.getObjectId());
    oldAffRO.setTitle(newAffiliationRO.getName());
    return oldAffRO;
  }

  public static de.mpg.mpdl.inge.model.valueobjects.AffiliationVO transformToOld(
      AffiliationDbVO newAffVo) {
    de.mpg.mpdl.inge.model.valueobjects.AffiliationVO oldAffVo =
        new de.mpg.mpdl.inge.model.valueobjects.AffiliationVO();
    oldAffVo.setCreationDate(newAffVo.getCreationDate());
    oldAffVo.setLastModificationDate(newAffVo.getLastModificationDate());
    oldAffVo.setCreator(transformToOld(newAffVo.getCreator()));
    oldAffVo.setDefaultMetadata(newAffVo.getMetadata());
    oldAffVo.setHasChildren(newAffVo.getHasChildren());
    oldAffVo.setModifiedBy(transformToOld(newAffVo.getModifier()));

    for (AffiliationDbRO predecessor : newAffVo.getPredecessorAffiliations()) {
      oldAffVo.getPredecessorAffiliations().add(transformToOld((AffiliationDbRO) predecessor));

    }

    for (AffiliationDbRO parent : newAffVo.getParentAffiliations()) {
      oldAffVo.getParentAffiliations().add(transformToOld((AffiliationDbRO) parent));

    }


    oldAffVo.setPublicStatus(newAffVo.getPublicStatus().name());
    oldAffVo.setReference(transformToOld((AffiliationDbRO) newAffVo));
    return oldAffVo;



  }

  private static String changeId(String prefix, String href) {
    return href;
    // return href.substring(href.lastIndexOf("/")+1, href.length()).replaceAll("escidoc:", prefix +
    // "_").replaceAll(":", "_");
  }


}
