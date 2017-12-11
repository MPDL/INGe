package de.mpg.mpdl.inge.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

public class PubItemUtil {

  private final static Logger logger = Logger.getLogger(PubItemUtil.class);

  private static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";

  /**
   * Cleans up the ValueObject for saving/submitting from unused sub-VOs.
   * 
   * @param pubItem the PubItem to clean up
   */
  public static void cleanUpItem(final PubItemVO pubItem) {
    try {
      pubItem.getMetadata().cleanup();

      // delete unfilled file
      if (pubItem.getFiles() != null) {
        for (int i = (pubItem.getFiles().size() - 1); i >= 0; i--) {
          // Cleanup MD
          pubItem.getFiles().get(i).getDefaultMetadata().cleanup();
          if ((pubItem.getFiles().get(i).getName() == null || pubItem.getFiles().get(i).getName().length() == 0)
              && (pubItem.getFiles().get(i).getContent() == null || pubItem.getFiles().get(i).getContent().length() == 0)) {
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
        if (creator.getPerson() != null) {
          for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
            if (organization.getIdentifier() == null || organization.getIdentifier().equals("")) {
              organization.setIdentifier(PropertyReader.getProperty("inge.pubman.external.organisation.id"));
            }
          }
        } else {
          if (creator.getOrganization() != null
              && (creator.getOrganization().getIdentifier() == null || creator.getOrganization().getIdentifier().equals(""))) {
            creator.getOrganization().setIdentifier(PropertyReader.getProperty("inge.pubman.external.organisation.id"));
          }
        }
      }

      if (pubItem.getMetadata().getSources() != null) {
        for (final SourceVO source : pubItem.getMetadata().getSources()) {
          for (final CreatorVO creator : source.getCreators()) {
            if (creator.getPerson() != null) {
              for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
                if (organization.getIdentifier() == null || organization.getIdentifier().equals("")) {
                  organization.setIdentifier(PropertyReader.getProperty("inge.pubman.external.organisation.id"));
                }
              }
            } else {
              if (creator.getOrganization() != null
                  && (creator.getOrganization().getIdentifier() == null || creator.getOrganization().getIdentifier().equals(""))) {
                creator.getOrganization().setIdentifier(PropertyReader.getProperty("inge.pubman.external.organisation.id"));
              }
            }
          }
        }
      }

      // remove empty tags
      if (pubItem.getLocalTags() != null) {
        final List<String> emptyTags = new ArrayList<String>();
        for (final String tag : pubItem.getLocalTags()) {
          if (tag == null || "".equals(tag)) {
            emptyTags.add(tag);
          }
        }
        for (final String tag : emptyTags) {
          pubItem.getLocalTags().remove(tag);
        }
      }

    } catch (final Exception e) {
      logger.error("Error getting external org id", e);
    }
  }

  public static PubItemVO createRevisionOfPubItem(final PubItemVO originalPubItem, String relationComment, final ContextRO pubCollection,
      final AccountUserVO owner) {
    PubItemVO copiedPubItem = new PubItemVO();
    copiedPubItem.setOwner(owner.getReference());
    copiedPubItem.setContext(pubCollection);
    copiedPubItem.setMetadata(new MdsPublicationVO());
    copiedPubItem.getMetadata().setGenre(originalPubItem.getMetadata().getGenre());

    for (CreatorVO creator : originalPubItem.getMetadata().getCreators()) {
      copiedPubItem.getMetadata().getCreators().add((CreatorVO) creator.clone());
    }

    if (originalPubItem.getMetadata().getTitle() != null) {
      copiedPubItem.getMetadata().setTitle(originalPubItem.getMetadata().getTitle());
    }

    for (String language : originalPubItem.getMetadata().getLanguages()) {
      copiedPubItem.getMetadata().getLanguages().add(language);
    }

    for (AlternativeTitleVO title : originalPubItem.getMetadata().getAlternativeTitles()) {
      copiedPubItem.getMetadata().getAlternativeTitles().add((AlternativeTitleVO) title.clone());
    }

    if (originalPubItem.getMetadata().getFreeKeywords() != null) {
      copiedPubItem.getMetadata().setFreeKeywords(originalPubItem.getMetadata().getFreeKeywords());
    }

    if (originalPubItem.getMetadata().getSubjects() != null) {
      for (SubjectVO subject : originalPubItem.getMetadata().getSubjects()) {
        copiedPubItem.getMetadata().getSubjects().add(subject);
      }
    }

    ItemRelationVO relation = new ItemRelationVO();
    relation.setType(PREDICATE_ISREVISIONOF);
    relation.setTargetItemRef(originalPubItem.getVersion());
    relation.setDescription(relationComment);
    copiedPubItem.getRelations().add(relation);

    return copiedPubItem;
  }



}
