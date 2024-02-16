package de.mpg.mpdl.inge.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;

public class PubItemUtil {

  private static final Logger logger = Logger.getLogger(PubItemUtil.class);

  /**
   * Cleans up the ValueObject for saving/submitting from unused sub-VOs.
   *
   * @param pubItem the PubItem to clean up
   */
  public static void cleanUpItem(final ItemVersionVO pubItem) {
    try {
      pubItem.getMetadata().cleanup();

      // delete unfilled file
      if (pubItem.getFiles() != null) {
        for (int i = (pubItem.getFiles().size() - 1); i >= 0; i--) {
          // Cleanup MD
          pubItem.getFiles().get(i).getMetadata().cleanup();
          if ((pubItem.getFiles().get(i).getName() == null || pubItem.getFiles().get(i).getName().isEmpty())
              && (pubItem.getFiles().get(i).getContent() == null || pubItem.getFiles().get(i).getContent().isEmpty())) {
            pubItem.getFiles().remove(i);
          }
        }
      }
    } catch (final Exception e1) {
      throw new RuntimeException("Error while cleaning up  item", e1);
    }

    // TODO MF: Check specification for this behaviour: Always when an organization does not have an
    // identifier, make it "external".
    // assign the external org id to default organisation
    try {
      for (final CreatorVO creator : pubItem.getMetadata().getCreators()) {
        adaptConeLinks(creator);
      }

      if (pubItem.getMetadata().getSources() != null) {
        for (final SourceVO source : pubItem.getMetadata().getSources()) {
          for (final CreatorVO creator : source.getCreators()) {
            adaptConeLinks(creator);
          }
        }
      }

      // remove empty tags
      if (pubItem.getObject().getLocalTags() != null) {
        final List<String> emptyTags = new ArrayList<>();
        for (final String tag : pubItem.getObject().getLocalTags()) {
          if (tag == null || tag.isEmpty()) {
            emptyTags.add(tag);
          }
        }
        for (final String tag : emptyTags) {
          pubItem.getObject().getLocalTags().remove(tag);
        }
      }

    } catch (final Exception e) {
      logger.error("Error getting external org id", e);
    }
  }

  private static void adaptConeLinks(final CreatorVO creator) {
    if (creator.getPerson() != null) {
      //Make CoNE link relative
      if (creator.getPerson().getIdentifier() != null && creator.getPerson().getIdentifier().getId() != null
          && creator.getPerson().getIdentifier().getType() == IdType.CONE) {
        String personsIdentifier = creator.getPerson().getIdentifier().getId();
        if (personsIdentifier.startsWith("http")) {
          creator.getPerson().getIdentifier().setId(ConeUtils.makeConePersonsLinkRelative(personsIdentifier));
        }
      }

      for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
        if (organization.getIdentifier() == null || organization.getIdentifier().isEmpty()) {
          organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
        }
      }
    } else {
      if (creator.getOrganization() != null
          && (creator.getOrganization().getIdentifier() == null || creator.getOrganization().getIdentifier().isEmpty())) {
        creator.getOrganization().setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
      }
    }
  }

  public static ItemVersionVO createRevisionOfPubItem(final ItemVersionVO originalPubItem, String relationComment,
      final ContextDbRO pubCollection, final AccountUserDbVO owner) {
    ItemVersionVO copiedPubItem = new ItemVersionVO();
    AccountUserDbRO itemCreator = new AccountUserDbRO();
    itemCreator.setObjectId(owner.getObjectId());
    itemCreator.setName(owner.getName());
    copiedPubItem.getObject().setCreator(itemCreator);
    copiedPubItem.getObject().setContext(pubCollection);
    copiedPubItem.setMetadata(new MdsPublicationVO());
    copiedPubItem.getMetadata().setGenre(originalPubItem.getMetadata().getGenre());

    for (CreatorVO creator : originalPubItem.getMetadata().getCreators()) {
      copiedPubItem.getMetadata().getCreators().add(creator.clone());
    }

    if (originalPubItem.getMetadata().getTitle() != null) {
      copiedPubItem.getMetadata().setTitle(originalPubItem.getMetadata().getTitle());
    }

    for (String language : originalPubItem.getMetadata().getLanguages()) {
      copiedPubItem.getMetadata().getLanguages().add(language);
    }

    for (AlternativeTitleVO title : originalPubItem.getMetadata().getAlternativeTitles()) {
      copiedPubItem.getMetadata().getAlternativeTitles().add(title.clone());
    }

    if (originalPubItem.getMetadata().getFreeKeywords() != null) {
      copiedPubItem.getMetadata().setFreeKeywords(originalPubItem.getMetadata().getFreeKeywords());
    }

    if (originalPubItem.getMetadata().getSubjects() != null) {
      for (SubjectVO subject : originalPubItem.getMetadata().getSubjects()) {
        copiedPubItem.getMetadata().getSubjects().add(subject);
      }
    }

    /*
    ItemRelationVO relation = new ItemRelationVO();
    relation.setType(PREDICATE_ISREVISIONOF);
    relation.setTargetItemRef(originalPubItem.getVersion());
    relation.setDescription(relationComment);
    copiedPubItem.getRelations().add(relation);
     */
    return copiedPubItem;
  }



  public static void setOrganizationIdPathInItem(ItemVersionVO pubItem, OrganizationService ouService)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {
    if (pubItem.getMetadata() != null) {
      if (pubItem.getMetadata().getCreators() != null) {
        setOrganizationIdPathsInCreators(pubItem.getMetadata().getCreators(), ouService);
      }

      if (pubItem.getMetadata().getSources() != null) {
        for (SourceVO source : pubItem.getMetadata().getSources()) {
          setOrganizationIdPathsInCreators(source.getCreators(), ouService);
        }
      }
    }
  }

  private static void setOrganizationIdPathsInCreators(List<CreatorVO> creatorList, OrganizationService ouService)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {

    for (CreatorVO creator : creatorList) {
      if (creator.getPerson() != null) {
        for (OrganizationVO ou : creator.getPerson().getOrganizations()) {
          setOrganizationIdPathInOrganization(ou, ouService);
        }

      } else if (creator.getOrganization() != null) {
        setOrganizationIdPathInOrganization(creator.getOrganization(), ouService);
      }
    }

  }


  private static void setOrganizationIdPathInOrganization(OrganizationVO ou, OrganizationService ouService)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {

    if (ou.getIdentifier() != null && !ou.getIdentifier().trim().isEmpty()) {
      List<String> ouPath = ouService.getIdPath(ou.getIdentifier().trim());
      ou.setIdentifierPath(ouPath.toArray(new String[] {}));
    } else {
      ou.setIdentifierPath(null);
    }


  }



}
