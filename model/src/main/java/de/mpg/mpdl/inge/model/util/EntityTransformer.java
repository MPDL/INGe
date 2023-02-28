package de.mpg.mpdl.inge.model.util;


import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.Mapper;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

public class EntityTransformer {

  private static Mapper dozerMapper = MapperFactory.getDozerMapper();

  /*
  public static ContextDbVO transformToNew(ContextVO contextVo) {
    return dozerMapper.map(contextVo, ContextDbVO.class);
  }
  */

  public static ItemVersionVO transformToNew(PubItemVO itemVo) {
    return dozerMapper.map(itemVo, ItemVersionVO.class);
  }

  public static FileDbVO transformToNew(FileVO fileVo) {
    return dozerMapper.map(fileVo, FileDbVO.class);
  }

  /*
  public static AffiliationDbVO transformToNew(AffiliationVO affVo) {
    return dozerMapper.map(affVo, AffiliationDbVO.class);
  }
  */

  /*
  private static AccountUserRO transformToOld(AccountUserDbRO newAccountUserRo) {
    if (newAccountUserRo == null) {
      return null;
    }
  
    AccountUserRO modifier = new AccountUserRO();
    modifier.setObjectId(newAccountUserRo.getObjectId());
    modifier.setTitle(newAccountUserRo.getName());
  
    return modifier;
  }
  */

  public static PubItemVO transformToOld(ItemVersionVO itemVo) {
    if (itemVo == null) {
      return null;
    }

    return dozerMapper.map(itemVo, PubItemVO.class);
  }

  public static List<ItemVersionVO> transformToNew(List<PubItemVO> oldItemList) {
    List<ItemVersionVO> newItemList = new ArrayList<>();
    if (oldItemList != null) {
      for (PubItemVO itemVO : oldItemList) {
        newItemList.add(transformToNew(itemVO));
      }
    }
    return newItemList;
  }

  public static List<PubItemVO> transformToOld(List<ItemVersionVO> newItemList) {
    List<PubItemVO> oldList = new ArrayList<>();
    if (newItemList != null) {
      for (ItemVersionVO itemVO : newItemList) {
        oldList.add(transformToOld(itemVO));
      }
    }

    return oldList;
  }

  public static ContextVO transformToOld(ContextDbVO newContextVo) {
    if (newContextVo == null) {
      return null;
    }

    return dozerMapper.map(newContextVo, ContextVO.class);
  }

  public static AffiliationVO transformToOld(AffiliationDbVO newAffVo) {
    if (newAffVo == null) {
      return null;
    }

    return dozerMapper.map(newAffVo, AffiliationVO.class);
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
        vhEntry.setState(ItemVO.State.valueOf(audit.getPubItem().getVersionState().name()));
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

  /*
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
  */
}
