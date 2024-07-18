package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PubItemUtil {

  private static final Logger logger = LogManager.getLogger(PubItemUtil.class);

  private PubItemUtil() {}


  /**
   * Cleans up the ValueObject for saving/submitting from unused sub-VOs.
   *
   * @param pubItem the PubItem to clean up
   */
  public static void cleanUpItem(ItemVersionVO pubItem) {
    // cleanup item according to genre specific MD specification -> sets non visible fields to null
    cleanUpGenre(pubItem);

    try {
      // Cleanup MD
      pubItem.getMetadata().cleanup();

      // delete unfilled file
      if (null != pubItem.getFiles()) {
        for (int i = (pubItem.getFiles().size() - 1); 0 <= i; i--) {
          // Cleanup MD
          pubItem.getFiles().get(i).getMetadata().cleanup();
          if ((null == pubItem.getFiles().get(i).getName() || pubItem.getFiles().get(i).getName().isEmpty())
              && (null == pubItem.getFiles().get(i).getContent() || pubItem.getFiles().get(i).getContent().isEmpty())) {
            pubItem.getFiles().remove(i);
          }
        }
      }
    } catch (Exception e1) {
      throw new RuntimeException("Error while cleaning up  item", e1);
    }

    // TODO MF: Check specification for this behaviour: Always when an organization does not have an
    // identifier, make it "external".
    // assign the external org id to default organisation
    try {
      for (CreatorVO creator : pubItem.getMetadata().getCreators()) {
        adaptConeLinks(creator);
      }

      if (null != pubItem.getMetadata().getSources()) {
        for (SourceVO source : pubItem.getMetadata().getSources()) {
          for (CreatorVO creator : source.getCreators()) {
            adaptConeLinks(creator);
          }
        }
      }

      // remove empty tags
      if (null != pubItem.getObject().getLocalTags()) {
        List<String> emptyTags = new ArrayList<>();
        for (String tag : pubItem.getObject().getLocalTags()) {
          if (null == tag || tag.isEmpty()) {
            emptyTags.add(tag);
          }
        }
        for (String tag : emptyTags) {
          pubItem.getObject().getLocalTags().remove(tag);
        }
      }

    } catch (Exception e) {
      logger.error("Error getting external org id", e);
    }
  }

  private static void cleanUpGenre(ItemVersionVO pubItem) {
    // cleanup item according to genre specific MD specification
    GenreSpecificItemManager itemManager = new GenreSpecificItemManager(pubItem);
    try {
      pubItem = itemManager.cleanupItem();
    } catch (Exception e) {
      throw new RuntimeException("Error while cleaning up item genre specificly", e);
    }
  }

  private static void adaptConeLinks(CreatorVO creator) {
    if (null != creator.getPerson()) {
      //Make CoNE link relative
      if (null != creator.getPerson().getIdentifier() && null != creator.getPerson().getIdentifier().getId()
          && IdentifierVO.IdType.CONE == creator.getPerson().getIdentifier().getType()) {
        String personsIdentifier = creator.getPerson().getIdentifier().getId();
        if (personsIdentifier.startsWith("http")) {
          creator.getPerson().getIdentifier().setId(ConeUtils.makeConePersonsLinkRelative(personsIdentifier));
        }
      }

      for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
        if (null == organization.getIdentifier() || organization.getIdentifier().isEmpty()) {
          organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
        }
      }
    } else {
      if (null != creator.getOrganization()
          && (null == creator.getOrganization().getIdentifier() || creator.getOrganization().getIdentifier().isEmpty())) {
        creator.getOrganization().setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
      }
    }
  }

//  public static ItemVersionVO createRevisionOfPubItem(ItemVersionVO originalPubItem, String relationComment, ContextDbRO pubCollection,
//      AccountUserDbVO owner) {
//    ItemVersionVO copiedPubItem = new ItemVersionVO();
//    AccountUserDbRO itemCreator = new AccountUserDbRO();
//    itemCreator.setObjectId(owner.getObjectId());
//    itemCreator.setName(owner.getName());
//    copiedPubItem.getObject().setCreator(itemCreator);
//    copiedPubItem.getObject().setContext(pubCollection);
//    copiedPubItem.setMetadata(new MdsPublicationVO());
//    copiedPubItem.getMetadata().setGenre(originalPubItem.getMetadata().getGenre());
//
//    for (CreatorVO creator : originalPubItem.getMetadata().getCreators()) {
//      copiedPubItem.getMetadata().getCreators().add(creator.clone());
//    }
//
//    if (null != originalPubItem.getMetadata().getTitle()) {
//      copiedPubItem.getMetadata().setTitle(originalPubItem.getMetadata().getTitle());
//    }
//
//    for (String language : originalPubItem.getMetadata().getLanguages()) {
//      copiedPubItem.getMetadata().getLanguages().add(language);
//    }
//
//    for (AlternativeTitleVO title : originalPubItem.getMetadata().getAlternativeTitles()) {
//      copiedPubItem.getMetadata().getAlternativeTitles().add(title.clone());
//    }
//
//    if (null != originalPubItem.getMetadata().getFreeKeywords()) {
//      copiedPubItem.getMetadata().setFreeKeywords(originalPubItem.getMetadata().getFreeKeywords());
//    }
//
//    if (null != originalPubItem.getMetadata().getSubjects()) {
//      for (SubjectVO subject : originalPubItem.getMetadata().getSubjects()) {
//        copiedPubItem.getMetadata().getSubjects().add(subject);
//      }
//    }
//
//    /*
//    ItemRelationVO relation = new ItemRelationVO();
//    relation.setType(PREDICATE_ISREVISIONOF);
//    relation.setTargetItemRef(originalPubItem.getVersion());
//    relation.setDescription(relationComment);
//    copiedPubItem.getRelations().add(relation);
//     */
//    return copiedPubItem;
//  }



  public static void setOrganizationIdPathInItem(ItemVersionVO pubItem, OrganizationService ouService) throws IngeApplicationException {
    if (null != pubItem.getMetadata()) {
      if (null != pubItem.getMetadata().getCreators()) {
        setOrganizationIdPathsInCreators(pubItem.getMetadata().getCreators(), ouService);
      }

      if (null != pubItem.getMetadata().getSources()) {
        for (SourceVO source : pubItem.getMetadata().getSources()) {
          setOrganizationIdPathsInCreators(source.getCreators(), ouService);
        }
      }
    }
  }

  private static void setOrganizationIdPathsInCreators(List<CreatorVO> creatorList, OrganizationService ouService)
      throws IngeApplicationException {

    for (CreatorVO creator : creatorList) {
      if (null != creator.getPerson()) {
        for (OrganizationVO ou : creator.getPerson().getOrganizations()) {
          setOrganizationIdPathInOrganization(ou, ouService);
        }

      } else if (null != creator.getOrganization()) {
        setOrganizationIdPathInOrganization(creator.getOrganization(), ouService);
      }
    }

  }


  private static void setOrganizationIdPathInOrganization(OrganizationVO ou, OrganizationService ouService)
      throws IngeApplicationException {

    if (null != ou.getIdentifier() && !ou.getIdentifier().trim().isEmpty()) {
      List<String> ouPath = ouService.getIdPath(ou.getIdentifier().trim());
      ou.setIdentifierPath(ouPath.toArray(new String[] {}));
    } else {
      ou.setIdentifierPath(null);
    }


  }



}
