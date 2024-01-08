/**
 * 
 */
package de.mpg.mpdl.inge.cslmanager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.WordUtils;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLNameBuilder;
import de.undercouch.citeproc.csl.CSLType;

/**
 * MetadataProvider provides csl item data for a given escidoc item-List
 * 
 * @author walter
 * 
 */
public class MetadataProvider implements ItemDataProvider {
  private static final Logger logger = Logger.getLogger(CitationStyleLanguageManagerService.class);

  private static final String[] dateFormats = {"yyyy-MM-dd", "yyyy-MM", "yyyy"};

  private List<PubItemVO> pubItemList;
  private List<String> ids = new ArrayList<String>();

  public MetadataProvider(String itemList) throws TechnicalException {
    try {

      pubItemList = XmlTransformingService.transformToPubItemList(itemList);
      for (PubItemVO pubItem : pubItemList) {
        ids.add(pubItem.getVersion().getObjectId());
      }
    } catch (TechnicalException e) {
      logger.error("Unable to transform itemList", e);
      throw new TechnicalException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.undercouch.citeproc.ItemDataProvider#getIds()
   */
  @Override
  public String[] getIds() {
    return ids.toArray(new String[ids.size()]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.undercouch.citeproc.ItemDataProvider#retrieveItem(java.lang.String)
   */
  @Override
  public CSLItemData retrieveItem(String id) {
    PubItemVO currentItem = null;
    MdsPublicationVO metadata = null;
    CSLItemDataBuilder cslItem = null;

    currentItem = pubItemList.get(ids.indexOf(id));
    metadata = currentItem.getMetadata();
    cslItem = new CSLItemDataBuilder().id(currentItem.getVersion().getObjectId());

    try {
      // helper variables;
      boolean publicationIsbnExists = false;
      boolean publicationIssnExists = false;

      // Genre
      cslItem.type(this.getCslGenre(metadata.getGenre()));

      // Title
      cslItem.title(metadata.getTitle());

      // Alternative title
      for (AlternativeTitleVO title : metadata.getAlternativeTitles()) {
        if (!SourceVO.AlternativeTitleType.HTML.toString().equals(title.getType())
            && !SourceVO.AlternativeTitleType.LATEX.toString().equals(title.getType())
            && !SourceVO.AlternativeTitleType.MATHML.toString().equals(title.getType())) {
          cslItem.titleShort(title.getValue());
          break;
        }
      }

      // Creators
      List<CSLName> authorList = new ArrayList<CSLName>();
      List<CSLName> editorList = new ArrayList<CSLName>();
      List<CSLName> directorList = new ArrayList<CSLName>();
      List<CSLName> illustratorList = new ArrayList<CSLName>();
      List<CSLName> translatorList = new ArrayList<CSLName>();
      List<CSLName> composerList = new ArrayList<CSLName>();
      List<CSLName> interviewerList = new ArrayList<CSLName>();
      for (CreatorVO creator : metadata.getCreators()) {
        if (CreatorVO.CreatorType.PERSON.equals(creator.getType())) {
          if (CreatorVO.CreatorRole.AUTHOR.equals(creator.getRole()) || CreatorVO.CreatorRole.COMMENTATOR.equals(creator.getRole())
              || CreatorVO.CreatorRole.ACTOR.equals(creator.getRole()) || CreatorVO.CreatorRole.INVENTOR.equals(creator.getRole())
              || CreatorVO.CreatorRole.DEVELOPER.equals(creator.getRole()) || CreatorVO.CreatorRole.INTERVIEWEE.equals(creator.getRole())) {
            authorList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.EDITOR.equals(creator.getRole()) || CreatorVO.CreatorRole.PRODUCER.equals(creator.getRole())
              || CreatorVO.CreatorRole.APPLICANT.equals(creator.getRole()) || CreatorVO.CreatorRole.CONTRIBUTOR.equals(creator.getRole())) {
            editorList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.DIRECTOR.equals(creator.getRole())) {
            directorList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.ILLUSTRATOR.equals(creator.getRole())
              || CreatorVO.CreatorRole.PHOTOGRAPHER.equals(creator.getRole()) || CreatorVO.CreatorRole.ARTIST.equals(creator.getRole())
              || CreatorVO.CreatorRole.PAINTER.equals(creator.getRole())
              || CreatorVO.CreatorRole.CINEMATOGRAPHER.equals(creator.getRole())) {
            illustratorList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.TRANSLATOR.equals(creator.getRole())
              || CreatorVO.CreatorRole.TRANSCRIBER.equals(creator.getRole())) {
            translatorList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.SOUND_DESIGNER.equals(creator.getRole())) {
            composerList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          } else if (CreatorVO.CreatorRole.INTERVIEWER.equals(creator.getRole())) {
            interviewerList
                .add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
          }
        } else if (CreatorVO.CreatorType.ORGANIZATION.equals(creator.getType())) {
          if (CreatorVO.CreatorRole.AUTHOR.equals(creator.getRole())) {
            authorList.add(new CSLNameBuilder().given("").family(creator.getOrganization().getName()).build());
          } else {
            // empty String for given needed
            editorList.add(new CSLNameBuilder().given("").family(creator.getOrganization().getName()).build());
          }
        }
      }
      if (authorList.size() > 0) {
        cslItem.author(getCSLNameArrayFromList(authorList));
      }
      if (editorList.size() > 0) {
        cslItem.editor(getCSLNameArrayFromList(editorList));
      }
      if (directorList.size() > 0) {
        cslItem.director(getCSLNameArrayFromList(directorList));
      }
      if (illustratorList.size() > 0) {
        cslItem.illustrator(getCSLNameArrayFromList(illustratorList));
      }
      if (translatorList.size() > 0) {
        cslItem.translator(getCSLNameArrayFromList(translatorList));
      }
      if (composerList.size() > 0) {
        cslItem.composer(getCSLNameArrayFromList(composerList));
      }
      if (interviewerList.size() > 0) {
        cslItem.interviewer(getCSLNameArrayFromList(interviewerList));
      }

      // Dates
      if (metadata.getDateSubmitted() != null) // Submitted
      {
        for (String formatString : dateFormats) {
          try {
            Date date = new SimpleDateFormat(formatString).parse(metadata.getDateSubmitted());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (dateFormats[0].equals(formatString)) {
              cslItem.submitted(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            } else if (dateFormats[1].equals(formatString)) {
              cslItem.submitted(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            } else if (dateFormats[2].equals(formatString)) {
              cslItem.submitted(calendar.get(Calendar.YEAR));
            }
            break;
          } catch (ParseException e) {
            // This ParseException is wanted if the formats are not equal --> not thrown
            if (logger.isDebugEnabled())
              logger.debug("Error parsing date submitted. Trying other dateformat");
          }
        }

      }
      if (metadata.getDatePublishedInPrint() != null) // Published in Print
      {
        for (String formatString : dateFormats) {
          try {
            Date date = new SimpleDateFormat(formatString).parse(metadata.getDatePublishedInPrint());
            setIssued(cslItem, formatString, date);
            break;
          } catch (ParseException e) {
            // This ParseException is wanted if the formats are not equal --> not thrown
            if (logger.isDebugEnabled())
              logger.debug("Error parsing date issued. Trying other dateformat");
          }
        }
      } else if (metadata.getDatePublishedOnline() != null) // Published online
      {
        for (String formatString : dateFormats) {
          try {
            Date date = new SimpleDateFormat(formatString).parse(metadata.getDatePublishedOnline());
            setIssued(cslItem, formatString, date);
            break;
          } catch (ParseException e) {
            // This ParseException is wanted if the formats are not equal --> not thrown
            if (logger.isDebugEnabled())
              logger.debug("Error parsing date issued (published in print). Trying other dateformat");
          }
        }
      } else if (metadata.getDateAccepted() != null && Genre.THESIS.equals(metadata.getGenre())) // Published
                                                                                                 // online
      {
        for (String formatString : dateFormats) {
          try {
            Date date = new SimpleDateFormat(formatString).parse(metadata.getDateAccepted());
            setIssued(cslItem, formatString, date);
            break;
          } catch (ParseException e) {
            // This ParseException is wanted if the formats are not equal --> not thrown
            if (logger.isDebugEnabled())
              logger.debug("Error parsing date issued (accepted). Trying other dateformat");
          }
        }
      }

      // Degree
      if (metadata.getDegree() != null) {
        switch (metadata.getDegree()) {
          case BACHELOR:
            cslItem.genre("Bachelor's Thesis");
            break;
          case DIPLOMA:
            cslItem.genre("Diploma Thesis");
            break;
          case HABILITATION:
            cslItem.genre("Habilitation Thesis");
            break;
          case MAGISTER:
            cslItem.genre("Magister Thesis");
            break;
          case MASTER:
            cslItem.genre("Master's Thesis");
            break;
          case PHD:
            cslItem.genre("PhD Thesis");
            break;
          case STAATSEXAMEN:
            cslItem.genre("Staatsexamen Thesis");
            break;
        }
      }

      // URL / Files
      if (currentItem.getFiles() != null && !currentItem.getFiles().isEmpty()) {
        List<FileVO> fileList = null;
        fileList = currentItem.getFiles();
        Collections.sort(fileList, new FileUrlPriorityComparator());
        if (fileList.get(0) != null) {
          if (FileVO.Visibility.PUBLIC.equals(fileList.get(0).getVisibility())
              && ("any-fulltext".equals(fileList.get(0).getContentCategory()) || "post-print".equals(fileList.get(0).getContentCategory())
                  || "pre-print".equals(fileList.get(0).getContentCategory())
                  || "publisher-version".equals(fileList.get(0).getContentCategory()) || "code".equals(fileList.get(0).getContentCategory())
                  || "multimedia".equals(fileList.get(0).getContentCategory()))) {
            if (FileVO.Storage.EXTERNAL_URL.equals(fileList.get(0).getStorage())) {
              cslItem.URL(fileList.get(0).getContent());
            } else if (FileVO.Storage.INTERNAL_MANAGED.equals(fileList.get(0).getStorage())) {
              cslItem.URL(fileList.get(0).getPid().replace(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT),
                  PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL)));
            }
          } else {
            for (IdentifierVO identifier : metadata.getIdentifiers()) {
              if (IdentifierVO.IdType.URI.equals(identifier.getType()) || IdentifierVO.IdType.URN.equals(identifier.getType())) {
                cslItem.URL(identifier.getId());
                break;
              }
            }
          }
        }
      }

      // Identifiers
      for (IdentifierVO identifier : metadata.getIdentifiers()) {
        if (IdentifierVO.IdType.DOI.equals(identifier.getType())) {
          cslItem.DOI(identifier.getId());
        } else if (IdentifierVO.IdType.ISBN.equals(identifier.getType())) {
          cslItem.ISBN(identifier.getId());
          publicationIsbnExists = true;
        } else if (IdentifierVO.IdType.ISSN.equals(identifier.getType())) {
          cslItem.ISSN(identifier.getId());
          publicationIssnExists = true;
        } else if (IdentifierVO.IdType.PMC.equals(identifier.getType())) {
          cslItem.PMCID(identifier.getId());
        } else if (IdentifierVO.IdType.PMID.equals(identifier.getType())) {
          cslItem.PMID(identifier.getId());
        }
      }
      if (metadata.getIdentifiers() != null && !metadata.getIdentifiers().isEmpty()) {
        List<IdentifierVO> identifierList = metadata.getIdentifiers();
        Collections.sort(identifierList, new IdentfierPriorityComparator());
        IdentifierVO identifier = identifierList.get(0);
        if (identifier != null && !IdentifierVO.IdType.DOI.equals(identifier.getType())
            && !IdentifierVO.IdType.ISBN.equals(identifier.getType()) && !IdentifierVO.IdType.ISSN.equals(identifier.getType())
            && !IdentifierVO.IdType.URI.equals(identifier.getType()) && !IdentifierVO.IdType.URN.equals(identifier.getType())
            && !IdentifierVO.IdType.ISSN.equals(identifier.getType()) && !IdentifierVO.IdType.CONE.equals(identifier.getType())
            && !IdentifierVO.IdType.EDOC.equals(identifier.getType()) && !IdentifierVO.IdType.PMC.equals(identifier.getType())
            && !IdentifierVO.IdType.PMID.equals(identifier.getType()) && !IdentifierVO.IdType.PND.equals(identifier.getType())
            && !IdentifierVO.IdType.ZDB.equals(identifier.getType())) {
          if (IdentifierVO.IdType.PATENT_NR.equals(identifier.getType())) {
            cslItem.number("Patent Nr.: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.PATENT_PUBLICATION_NR.equals(identifier.getType())) {
            cslItem.number("Patent Publication Nr.: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.PATENT_APPLICATION_NR.equals(identifier.getType())) {
            cslItem.number("Patent Application Nr.: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.REPORT_NR.equals(identifier.getType())) {
            cslItem.number("Report Nr.: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.ISI.equals(identifier.getType())) {
            cslItem.number("ISI: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.PII.equals(identifier.getType())) {
            cslItem.number("PII: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.SSRN.equals(identifier.getType())) {
            cslItem.number("SSRN: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.ARXIV.equals(identifier.getType())) {
            cslItem.number("arXiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.BIORXIV.equals(identifier.getType())) {
            cslItem.number("bioRxiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.CHEMRXIV.equals(identifier.getType())) {
            cslItem.number("ChemRxiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.EARTHARXIV.equals(identifier.getType())) {
            cslItem.number("EarthArXiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.PSYARXIV.equals(identifier.getType())) {
            cslItem.number("PsyArXiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.SOCARXIV.equals(identifier.getType())) {
            cslItem.number("SocArXiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.EDARXIV.equals(identifier.getType())) {
            cslItem.number("EdArXiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.MEDRXIV.equals(identifier.getType())) {
            cslItem.number("medRxiv: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.ADS.equals(identifier.getType())) {
            cslItem.number("ADS: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.ESS_OPEN_ARCHIVE.equals(identifier.getType())) {
            cslItem.number("ESSOAr: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.RESEARCH_SQUARE.equals(identifier.getType())) {
            cslItem.number("Research Square: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.BMC.equals(identifier.getType())) {
            cslItem.number("BMC: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.BIBTEX_CITEKEY.equals(identifier.getType())) {
            cslItem.number("BibTex Citekey: " + identifierList.get(0).getId());
          } else if (IdentifierVO.IdType.OTHER.equals(identifier.getType())) {
            cslItem.number("Other ID: " + identifierList.get(0).getId());
          } else {
            String type = identifierList.get(0).getTypeString();
            type = WordUtils.capitalizeFully(type);
            cslItem.number(type + ": " + identifierList.get(0).getId());
          }
        }
      }

      // Keywords
      if (metadata.getFreeKeywords() != null) {
        cslItem.keyword(metadata.getFreeKeywords());
      }

      // Abstract
      if (metadata.getAbstracts() != null && !metadata.getAbstracts().isEmpty()) {
        cslItem.abstrct(metadata.getAbstracts().get(0).getValue());
      }

      // Publisher / Publisher place / Edition
      if (metadata.getPublishingInfo() != null) {
        if (metadata.getPublishingInfo().getPublisher() != null) {
          cslItem.publisher(metadata.getPublishingInfo().getPublisher());
        }
        if (metadata.getPublishingInfo().getPlace() != null) {
          cslItem.publisherPlace(metadata.getPublishingInfo().getPlace());
        }
        if (metadata.getPublishingInfo().getEdition() != null) {
          cslItem.edition(metadata.getPublishingInfo().getEdition());
        }
      }

      // Number of pages
      if (metadata.getTotalNumberOfPages() != null) {
        cslItem.numberOfPages(metadata.getTotalNumberOfPages());
      }

      // Source
      if (metadata.getSources() != null && !metadata.getSources().isEmpty()) {
        SourceVO source = metadata.getSources().get(0);
        // Genre dependent choice
        if (SourceVO.Genre.SERIES.equals(source.getGenre())) {
          // Source title
          cslItem.collectionTitle(source.getTitle());

          // Source creators
          List<CSLName> collectionEditorList = new ArrayList<CSLName>();
          for (CreatorVO sourceCreator : source.getCreators()) {
            if (CreatorVO.CreatorRole.AUTHOR.equals(sourceCreator.getRole())
                || CreatorVO.CreatorRole.EDITOR.equals(sourceCreator.getRole())) {
              if (CreatorVO.CreatorType.PERSON.equals(sourceCreator.getType())) {
                collectionEditorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName())
                    .family(sourceCreator.getPerson().getFamilyName()).build());
              } else if (CreatorVO.CreatorType.ORGANIZATION.equals(sourceCreator.getType())) {
                collectionEditorList.add(new CSLNameBuilder().given("").family(sourceCreator.getOrganization().getName()).build());
              }
            }
          }
          if (collectionEditorList.size() > 0) {
            cslItem.collectionEditor(getCSLNameArrayFromList(collectionEditorList));
          }
        } else {
          // Source title
          cslItem.containerTitle(source.getTitle());

          // Source creators
          List<CSLName> containerAuthorList = new ArrayList<CSLName>();
          for (CreatorVO sourceCreator : source.getCreators()) {
            if (CreatorVO.CreatorRole.AUTHOR.equals(sourceCreator.getRole())) {
              if (CreatorVO.CreatorType.PERSON.equals(sourceCreator.getType())) {
                containerAuthorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName())
                    .family(sourceCreator.getPerson().getFamilyName()).build());
              } else if (CreatorVO.CreatorType.ORGANIZATION.equals(sourceCreator.getType())) {
                containerAuthorList.add(new CSLNameBuilder().given("").family(sourceCreator.getOrganization().getName()).build());
              }
            } else if (CreatorVO.CreatorRole.EDITOR.equals(sourceCreator.getRole())) {
              if (CreatorVO.CreatorType.PERSON.equals(sourceCreator.getType())) {
                editorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName())
                    .family(sourceCreator.getPerson().getFamilyName()).build());
              } else if (CreatorVO.CreatorType.ORGANIZATION.equals(sourceCreator.getType())) {
                editorList.add(new CSLNameBuilder().given("").family(sourceCreator.getOrganization().getName()).build());
              }
            }
          }
          if (containerAuthorList.size() > 0) {
            cslItem.containerAuthor(getCSLNameArrayFromList(containerAuthorList));
          }
          if (editorList.size() > 0) {
            cslItem.editor(getCSLNameArrayFromList(editorList));
          }
        }

        // Second Source
        if (metadata.getSources().size() > 1 && metadata.getSources().get(1) != null) {
          SourceVO secondSource = metadata.getSources().get(1);
          // Genre dependent choice
          if (SourceVO.Genre.SERIES.equals(secondSource.getGenre())) {
            // Source title
            cslItem.collectionTitle(secondSource.getTitle());
            // Source creators
            List<CSLName> collectionEditorList = new ArrayList<CSLName>();
            for (CreatorVO sourceCreator : secondSource.getCreators()) {
              if (CreatorVO.CreatorRole.AUTHOR.equals(sourceCreator.getRole())
                  || CreatorVO.CreatorRole.EDITOR.equals(sourceCreator.getRole())) {
                if (CreatorVO.CreatorType.PERSON.equals(sourceCreator.getType())) {
                  collectionEditorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName())
                      .family(sourceCreator.getPerson().getFamilyName()).build());
                } else if (CreatorVO.CreatorType.ORGANIZATION.equals(sourceCreator.getType())) {
                  collectionEditorList.add(new CSLNameBuilder().given("").family(sourceCreator.getOrganization().getName()).build());
                }
              }
            }
            if (collectionEditorList.size() > 0) {
              cslItem.collectionEditor(getCSLNameArrayFromList(collectionEditorList));
            }
            cslItem.collectionNumber(secondSource.getVolume());
          }
        }

        // Source short title
        for (AlternativeTitleVO sourceAlternativeTitle : source.getAlternativeTitles()) {
          if (!SourceVO.AlternativeTitleType.HTML.toString().equals(sourceAlternativeTitle.getType())
              && !SourceVO.AlternativeTitleType.LATEX.toString().equals(sourceAlternativeTitle.getType())
              && !SourceVO.AlternativeTitleType.MATHML.toString().equals(sourceAlternativeTitle.getType())) {
            // TODO mapping to journalAbbreviation is just a workaround for a little bug in
            // citeproc-js,
            // which is fixed in the next version
            cslItem.journalAbbreviation(sourceAlternativeTitle.getValue());
            cslItem.containerTitleShort(sourceAlternativeTitle.getValue());
            break;
          }
        }

        // Source publisher / Source publisher place / Source edition (all from source)
        if ((metadata.getPublishingInfo() == null || metadata.getPublishingInfo().getPublisher() == null)
            && (source.getPublishingInfo() != null && source.getPublishingInfo().getPublisher() != null)) {
          cslItem.publisher(source.getPublishingInfo().getPublisher());
        }
        if ((metadata.getPublishingInfo() == null || metadata.getPublishingInfo().getPlace() == null)
            && (source.getPublishingInfo() != null && source.getPublishingInfo().getPlace() != null)) {
          cslItem.publisherPlace(source.getPublishingInfo().getPlace());
        }
        if ((metadata.getPublishingInfo() == null || metadata.getPublishingInfo().getEdition() == null)
            && (source.getPublishingInfo() != null && source.getPublishingInfo().getEdition() != null)) {
          cslItem.edition(source.getPublishingInfo().getEdition());
        }

        // Source number of pages
        if (metadata.getTotalNumberOfPages() == null && source.getTotalNumberOfPages() != null) {
          cslItem.numberOfPages(source.getTotalNumberOfPages());
        }

        // Source volume
        if (source.getVolume() != null) {
          cslItem.volume(source.getVolume());
        }

        // Source issue
        if (source.getIssue() != null) {
          cslItem.issue(source.getIssue());
        }

        // Source start page
        if (source.getStartPage() != null) {
          cslItem.pageFirst(source.getStartPage());
          // Source combined "start page - end page"
          if (source.getEndPage() != null) {
            cslItem.page(source.getStartPage() + "-" + source.getEndPage());
          }
        }

        // Source sequence number --> Locator
        if (source.getSequenceNumber() != null) {
          cslItem.chapterNumber(source.getSequenceNumber());
        }

        // Source identifiers
        for (IdentifierVO identifier : source.getIdentifiers()) {
          if (IdentifierVO.IdType.ISBN.equals(identifier.getType()) && !publicationIsbnExists) {
            cslItem.ISBN(identifier.getId());
          } else if (IdentifierVO.IdType.ISSN.equals(identifier.getType()) && !publicationIssnExists) {
            cslItem.ISSN(identifier.getId());
          }
        }
      }

      // Event
      if (metadata.getEvent() != null) {
        EventVO event = metadata.getEvent();
        // Event title
        if (event.getTitle() != null) {
          cslItem.event(event.getTitle());
        }
        if (event.getPlace() != null) {
          cslItem.eventPlace(event.getPlace());
        }
        if (event.getStartDate() != null) {
          for (String formatString : dateFormats) {
            try {
              Date date = new SimpleDateFormat(formatString).parse(event.getStartDate());
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(date);
              if (dateFormats[0].equals(formatString)) {
                cslItem.eventDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
              } else if (dateFormats[1].equals(formatString)) {
                cslItem.eventDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
              } else if (dateFormats[2].equals(formatString)) {
                cslItem.eventDate(calendar.get(Calendar.YEAR));
              }
              break;
            } catch (ParseException e) {
              // This ParseException is wanted if the formats are not equal --> not thrown
              if (logger.isDebugEnabled())
                logger.debug("Error parsing date submitted");
            }
          }
        }
      }
    } catch (

    Exception e) {
      logger.error("Error creating cslItem metadata for id: " + id, e);
    }

    // build an return cslItem
    return cslItem.build();
  }

  private void setIssued(CSLItemDataBuilder cslItem, String formatString, Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    if (dateFormats[0].equals(formatString)) {
      cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    } else if (dateFormats[1].equals(formatString)) {
      cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    } else if (dateFormats[2].equals(formatString)) {
      cslItem.issued(calendar.get(Calendar.YEAR));
    }
  }

  private CSLType getCslGenre(Genre genre) {
    CSLType cslGenre = null;
    if (Genre.ARTICLE.equals(genre) || Genre.REVIEW_ARTICLE.equals(genre)) {
      cslGenre = CSLType.ARTICLE_JOURNAL;
    } else if (Genre.EDITORIAL.equals(genre) || Genre.PAPER.equals(genre) || Genre.OTHER.equals(genre)
        || Genre.PRE_REGISTRATION_PAPER.equals(genre) || Genre.REGISTERED_REPORT.equals(genre) || Genre.PREPRINT.equals(genre)
        || Genre.SOFTWARE.equals(genre)) {
      cslGenre = CSLType.ARTICLE;
    } else if (Genre.BLOG_POST.equals(genre)) {
      cslGenre = CSLType.POST_WEBLOG;
    } else if (Genre.BOOK.equals(genre) || Genre.COLLECTED_EDITION.equals(genre) || Genre.COMMENTARY.equals(genre)
        || Genre.ENCYCLOPEDIA.equals(genre) || Genre.FESTSCHRIFT.equals(genre) || Genre.HANDBOOK.equals(genre) || Genre.ISSUE.equals(genre)
        || Genre.JOURNAL.equals(genre) || Genre.MANUAL.equals(genre) || Genre.MONOGRAPH.equals(genre) || Genre.MULTI_VOLUME.equals(genre)
        || Genre.NEWSPAPER.equals(genre) || Genre.PROCEEDINGS.equals(genre) || Genre.SERIES.equals(genre)) {
      cslGenre = CSLType.BOOK;
    } else if (Genre.BOOK_ITEM.equals(genre) || Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre)
        || Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre) || Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre)
        || Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre)) {
      cslGenre = CSLType.CHAPTER;
    } else if (Genre.BOOK_REVIEW.equals(genre)) {
      cslGenre = CSLType.REVIEW_BOOK;
    } else if (Genre.CONFERENCE_PAPER.equals(genre) || Genre.CONFERENCE_REPORT.equals(genre) || Genre.MEETING_ABSTRACT.equals(genre)) {
      cslGenre = CSLType.PAPER_CONFERENCE;
    } else if (Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre)) {
      cslGenre = CSLType.ENTRY_ENCYCLOPEDIA;
    } else if (Genre.FILM.equals(genre)) {
      cslGenre = CSLType.MOTION_PICTURE;
    } else if (Genre.INTERVIEW.equals(genre)) {
      cslGenre = CSLType.INTERVIEW;
    } else if (Genre.MAGAZINE_ARTICLE.equals(genre)) {
      cslGenre = CSLType.ARTICLE_MAGAZINE;
    } else if (Genre.MANUSCRIPT.equals(genre)) {
      cslGenre = CSLType.MANUSCRIPT;
    } else if (Genre.NEWSPAPER_ARTICLE.equals(genre)) {
      cslGenre = CSLType.ARTICLE_NEWSPAPER;
    } else if (Genre.PATENT.equals(genre)) {
      cslGenre = CSLType.PATENT;
    } else if (Genre.REPORT.equals(genre)) {
      cslGenre = CSLType.REPORT;
    } else if (Genre.TALK_AT_EVENT.equals(genre) || Genre.POSTER.equals(genre) || Genre.COURSEWARE_LECTURE.equals(genre)) {
      cslGenre = CSLType.SPEECH;
    } else if (Genre.THESIS.equals(genre)) {
      cslGenre = CSLType.THESIS;
    } else if (Genre.CASE_NOTE.equals(genre) || Genre.CASE_STUDY.equals(genre) || Genre.OPINION.equals(genre)) {
      cslGenre = CSLType.LEGAL_CASE;
    } else if (Genre.DATA_PUBLICATION.equals(genre)) {
      cslGenre = CSLType.DATASET;
    }
    return cslGenre;
  }

  private static CSLName[] getCSLNameArrayFromList(List<CSLName> collectionEditorList) {
    CSLName[] creatorArray = new CSLName[collectionEditorList.size()];
    creatorArray = collectionEditorList.toArray(creatorArray);
    return creatorArray;
  }

  /**
   * Comparator Implementation to sort FileVOs for CSL-Output
   * 
   * @author walter
   * 
   */
  @SuppressWarnings("serial")
  private class FileUrlPriorityComparator implements Comparator<FileVO>, Serializable {
    @Override
    public int compare(FileVO file1, FileVO file2) {
      if (file1.equals(file2)) {
        return 0;
      }
      if (file1.getStorage().equals(file2.getStorage())) {
        if (FileVO.Storage.EXTERNAL_URL.equals(file1.getStorage())) {
          if (file1.getContentCategoryString().equals(file2.getContentCategoryString())) {
            return 0;
          } else if ("any-fulltext".equals(file1.getContentCategoryString()) || "post-print".equals(file1.getContentCategoryString())
              || "pre-print".equals(file1.getContentCategoryString()) || "publisher-version".equals(file1.getContentCategoryString())
              || "code".equals(file1.getContentCategoryString()) || "multimedia".equals(file1.getContentCategoryString())) {
            return -1;
          } else {
            return 1;
          }
        } else if (FileVO.Storage.INTERNAL_MANAGED.equals(file1.getStorage()) && FileVO.Visibility.PUBLIC.equals(file1.getVisibility())) {
          if ("any-fulltext".equals(file1.getContentCategoryString()) || "post-print".equals(file1.getContentCategoryString())
              || "pre-print".equals(file1.getContentCategoryString()) || "publisher-version".equals(file1.getContentCategoryString())
              || "code".equals(file1.getContentCategoryString()) || "multimedia".equals(file1.getContentCategoryString())) {
            return -1;
          } else {
            return 1;
          }
        } else {
          return 1;
        }
      } else if (FileVO.Storage.EXTERNAL_URL.equals(file1.getStorage())) {
        if ("any-fulltext".equals(file1.getContentCategoryString()) || "post-print".equals(file1.getContentCategoryString())
            || "pre-print".equals(file1.getContentCategoryString()) || "publisher-version".equals(file1.getContentCategoryString())
            || "code".equals(file1.getContentCategoryString()) || "multimedia".equals(file1.getContentCategoryString())) {
          return -1;
        } else {
          return 1;
        }
      } else if (FileVO.Storage.EXTERNAL_URL.equals(file2.getStorage())) {
        if ("any-fulltext".equals(file2.getContentCategoryString()) || "post-print".equals(file2.getContentCategoryString())
            || "pre-print".equals(file2.getContentCategoryString()) || "publisher-version".equals(file2.getContentCategoryString())
            || "code".equals(file1.getContentCategoryString()) || "multimedia".equals(file1.getContentCategoryString())) {
          return 1;
        } else {
          return -1;
        }
      } else if (FileVO.Storage.INTERNAL_MANAGED.equals(file1.getStorage()) && FileVO.Visibility.PUBLIC.equals(file1.getVisibility())) {
        if ("any-fulltext".equals(file1.getContentCategoryString()) || "post-print".equals(file1.getContentCategoryString())
            || "pre-print".equals(file1.getContentCategoryString()) || "publisher-version".equals(file1.getContentCategoryString())
            || "code".equals(file1.getContentCategoryString()) || "multimedia".equals(file1.getContentCategoryString())) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }
  }

  /**
   * Comparator Implementation to sort IdentifierVOs for CSL-Output
   * 
   * @author walter
   * 
   */
  @SuppressWarnings("serial")
  private class IdentfierPriorityComparator implements Comparator<IdentifierVO>, Serializable {
    @Override
    public int compare(IdentifierVO id1, IdentifierVO id2) {
      if (id1.getType().equals(id2.getType())) {
        return 0;
      } else {
        if (IdentifierVO.IdType.PATENT_NR.equals(id1.getType())) {
          return -1;
        } else if (IdentifierVO.IdType.PATENT_NR.equals(id2.getType())) {
          return 1;
        } else {
          if (IdentifierVO.IdType.PATENT_PUBLICATION_NR.equals(id1.getType())) {
            return -1;
          } else if (IdentifierVO.IdType.PATENT_PUBLICATION_NR.equals(id2.getType())) {
            return 1;
          } else {
            if (IdentifierVO.IdType.PATENT_APPLICATION_NR.equals(id1.getType())) {
              return -1;
            } else if (IdentifierVO.IdType.PATENT_APPLICATION_NR.equals(id2.getType())) {
              return 1;
            } else {
              if (IdentifierVO.IdType.REPORT_NR.equals(id1.getType())) {
                return -1;
              } else if (IdentifierVO.IdType.REPORT_NR.equals(id2.getType())) {
                return 1;
              } else {
                if (IdentifierVO.IdType.ISI.equals(id1.getType())) {
                  return -1;
                } else if (IdentifierVO.IdType.ISI.equals(id2.getType())) {
                  return 1;
                } else {
                  if (IdentifierVO.IdType.PII.equals(id1.getType())) {
                    return -1;
                  } else if (IdentifierVO.IdType.PII.equals(id2.getType())) {
                    return 1;
                  } else {
                    if (IdentifierVO.IdType.SSRN.equals(id1.getType())) {
                      return -1;
                    } else if (IdentifierVO.IdType.SSRN.equals(id2.getType())) {
                      return 1;
                    } else {
                      if (IdentifierVO.IdType.ARXIV.equals(id1.getType())) {
                        return -1;
                      } else if (IdentifierVO.IdType.ARXIV.equals(id2.getType())) {
                        return 1;
                      } else {
                        if (IdentifierVO.IdType.BIORXIV.equals(id1.getType())) {
                          return -1;
                        } else if (IdentifierVO.IdType.BIORXIV.equals(id2.getType())) {
                          return 1;
                        } else {
                          if (IdentifierVO.IdType.CHEMRXIV.equals(id1.getType())) {
                            return -1;
                          } else if (IdentifierVO.IdType.CHEMRXIV.equals(id2.getType())) {
                            return 1;
                          } else {
                            if (IdentifierVO.IdType.EARTHARXIV.equals(id1.getType())) {
                              return -1;
                            } else if (IdentifierVO.IdType.EARTHARXIV.equals(id2.getType())) {
                              return 1;
                            } else {
                              if (IdentifierVO.IdType.PSYARXIV.equals(id1.getType())) {
                                return -1;
                              } else if (IdentifierVO.IdType.PSYARXIV.equals(id2.getType())) {
                                return 1;
                              } else {
                                if (IdentifierVO.IdType.SOCARXIV.equals(id1.getType())) {
                                  return -1;
                                } else if (IdentifierVO.IdType.SOCARXIV.equals(id2.getType())) {
                                  return 1;
                                } else {
                                  if (IdentifierVO.IdType.EDARXIV.equals(id1.getType())) {
                                    return -1;
                                  } else if (IdentifierVO.IdType.EDARXIV.equals(id2.getType())) {
                                    return 1;
                                  } else {
                                    if (IdentifierVO.IdType.MEDRXIV.equals(id1.getType())) {
                                      return -1;
                                    } else if (IdentifierVO.IdType.MEDRXIV.equals(id2.getType())) {
                                      return 1;
                                    } else {
                                      if (IdentifierVO.IdType.ADS.equals(id1.getType())) {
                                        return -1;
                                      } else if (IdentifierVO.IdType.ADS.equals(id2.getType())) {
                                        return 1;
                                      } else {
                                        if (IdentifierVO.IdType.ESS_OPEN_ARCHIVE.equals(id1.getType())) {
                                          return -1;
                                        } else if (IdentifierVO.IdType.ESS_OPEN_ARCHIVE.equals(id2.getType())) {
                                          return 1;
                                        } else {
                                          if (IdentifierVO.IdType.RESEARCH_SQUARE.equals(id1.getType())) {
                                            return -1;
                                          } else if (IdentifierVO.IdType.RESEARCH_SQUARE.equals(id2.getType())) {
                                            return 1;
                                          } else {
                                            if (IdentifierVO.IdType.BMC.equals(id1.getType())) {
                                              return -1;
                                            } else if (IdentifierVO.IdType.BMC.equals(id2.getType())) {
                                              return 1;
                                            } else {
                                              if (IdentifierVO.IdType.BIBTEX_CITEKEY.equals(id1.getType())) {
                                                return -1;
                                              } else if (IdentifierVO.IdType.BIBTEX_CITEKEY.equals(id2.getType())) {
                                                return 1;
                                              } else {
                                                if (IdentifierVO.IdType.OTHER.equals(id1.getType())) {
                                                  return -1;
                                                } else if (IdentifierVO.IdType.OTHER.equals(id2.getType())) {
                                                  return 1;
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        return 1;
      }

    }
  }
}
