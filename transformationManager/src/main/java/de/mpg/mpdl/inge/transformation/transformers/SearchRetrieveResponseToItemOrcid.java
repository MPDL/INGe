package de.mpg.mpdl.inge.transformation.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@TransformerModule(sourceFormat = de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT.SEARCH_RESULT_VO,
    targetFormat = TransformerFactory.FORMAT.ORCID)
public class SearchRetrieveResponseToItemOrcid extends SingleTransformer implements ChainableTransformer {

  private static final Logger logger = LogManager.getLogger(SearchRetrieveResponseToItemOrcid.class);

  private static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})(?:[/-](\\d{1,2}))?(?:[/-](\\d{1,2}))?.*");

  private static final Map<String, String> GENRE_MAPPING = new HashMap<>();
  static {
    GENRE_MAPPING.put("ARTICLE", "journal-article");
    GENRE_MAPPING.put("BLOG_POST", "blog-post");
    GENRE_MAPPING.put("BOOK", "book");
    GENRE_MAPPING.put("BOOK_ITEM", "book-chapter");
    GENRE_MAPPING.put("BOOK_REVIEW", "book-review");
    GENRE_MAPPING.put("CASE_NOTE", "annotation");
    GENRE_MAPPING.put("CASE_STUDY", "other");
    GENRE_MAPPING.put("COLLECTED_EDITION", "book");
    GENRE_MAPPING.put("COMMENTARY", "other");
    GENRE_MAPPING.put("CONFERENCE_PAPER", "conference-paper");
    GENRE_MAPPING.put("CONFERENCE_REPORT", "conference-output");
    GENRE_MAPPING.put("CONTRIBUTION_TO_COLLECTED_EDITION", "book-chapter");
    GENRE_MAPPING.put("CONTRIBUTION_TO_COMMENTARY", "other");
    GENRE_MAPPING.put("CONTRIBUTION_TO_ENCYCLOPEDIA", "encyclopedia-entry");
    GENRE_MAPPING.put("CONTRIBUTION_TO_FESTSCHRIFT", "book-chapter");
    GENRE_MAPPING.put("CONTRIBUTION_TO_HANDBOOK", "book-chapter");
    GENRE_MAPPING.put("COURSEWARE_LECTURE", "lecture-speech");
    GENRE_MAPPING.put("DATA_PUBLICATION", "data-set");
    GENRE_MAPPING.put("EDITORIAL", "other");
    GENRE_MAPPING.put("ENCYCLOPEDIA", "book");
    GENRE_MAPPING.put("FESTSCHRIFT", "book");
    GENRE_MAPPING.put("FILM", "moving-image");
    GENRE_MAPPING.put("HANDBOOK", "book");
    GENRE_MAPPING.put("INTERVIEW", "public-speech");
    GENRE_MAPPING.put("ISSUE", "journal-issue");
    GENRE_MAPPING.put("JOURNAL", "journal-issue");
    GENRE_MAPPING.put("MAGAZINE_ARTICLE", "magazine-article");
    GENRE_MAPPING.put("MANUAL", "learning-object");
    GENRE_MAPPING.put("MANUSCRIPT", "other");
    GENRE_MAPPING.put("MEETING_ABSTRACT", "conference-output");
    GENRE_MAPPING.put("MONOGRAPH", "book");
    GENRE_MAPPING.put("MULTI_VOLUME", "book");
    GENRE_MAPPING.put("NEWSPAPER", "other");
    GENRE_MAPPING.put("NEWSPAPER_ARTICLE", "newspaper-article");
    GENRE_MAPPING.put("OPINION", "other");
    GENRE_MAPPING.put("OTHER", "other");
    GENRE_MAPPING.put("PAPER", "working-paper");
    GENRE_MAPPING.put("PATENT", "patent");
    GENRE_MAPPING.put("POSTER", "conference-poster");
    GENRE_MAPPING.put("PREPRINT", "preprint");
    GENRE_MAPPING.put("PRE_REGISTRATION_PAPER", "other");
    GENRE_MAPPING.put("PROCEEDINGS", "conference-proceedings");
    GENRE_MAPPING.put("REGISTERED_REPORT", "other");
    GENRE_MAPPING.put("REPORT", "report");
    GENRE_MAPPING.put("REVIEW_ARTICLE", "other");
    GENRE_MAPPING.put("SERIES", "other");
    GENRE_MAPPING.put("SOFTWARE", "software");
    GENRE_MAPPING.put("TALK_AT_EVENT", "conference-presentation");
    GENRE_MAPPING.put("THESIS", "dissertation-thesis");
  }

  private static final Map<String, String> EXTERNAL_ID_MAPPING = new HashMap<>();
  static {
    EXTERNAL_ID_MAPPING.put("ADS", "bibcode");
    EXTERNAL_ID_MAPPING.put("ARXIV", "arxiv");
    EXTERNAL_ID_MAPPING.put("BIBTEX_CITEKEY", "other-id");
    EXTERNAL_ID_MAPPING.put("BIORXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("BMC", "other-id");
    EXTERNAL_ID_MAPPING.put("CHEMRXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("DOI", "doi");
    EXTERNAL_ID_MAPPING.put("EARTHARXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("EDARXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("ESS_OPEN_ARCHIVE", "other-id");
    EXTERNAL_ID_MAPPING.put("ISBN", "isbn");
    EXTERNAL_ID_MAPPING.put("ISI", "wosuid");
    EXTERNAL_ID_MAPPING.put("ISSN", "issn");
    EXTERNAL_ID_MAPPING.put("MEDRXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("OTHER", "other-id");
    EXTERNAL_ID_MAPPING.put("REPORT_NR", "other-id");
    EXTERNAL_ID_MAPPING.put("PATENT_APPLICATION_NR", "other-id");
    EXTERNAL_ID_MAPPING.put("PATENT_NR", "pat");
    EXTERNAL_ID_MAPPING.put("PATENT_PUBLICATION_NR", "other-id");
    EXTERNAL_ID_MAPPING.put("PMC", "pmc");
    EXTERNAL_ID_MAPPING.put("PMID", "pmid");
    EXTERNAL_ID_MAPPING.put("PSYARXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("RESEARCH_SQUARE", "other-id");
    EXTERNAL_ID_MAPPING.put("SOCARXIV", "other-id");
    EXTERNAL_ID_MAPPING.put("SSRN", "ssrn");
    EXTERNAL_ID_MAPPING.put("URI", "uri");
    EXTERNAL_ID_MAPPING.put("URN", "urn");
    EXTERNAL_ID_MAPPING.put("ZDB", "other-id");
  }

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      SearchRetrieveResponseVO<ItemVersionVO> searchResult =
          (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();

      ObjectMapper mapper = MapperFactory.getObjectMapper();
      ObjectNode root = mapper.createObjectNode();
      ArrayNode bulkArray = mapper.createArrayNode();
      root.set("bulk", bulkArray);

      if (searchResult != null && searchResult.getRecords() != null) {
        List<ItemVersionVO> itemList =
            searchResult.getRecords().stream().map(SearchRetrieveRecordVO::getData).filter(Objects::nonNull).collect(Collectors.toList());

        List<String> citationList = null;
        String citStyle = getConfiguration().get("citation");
        if (citStyle == null || citStyle.trim().isEmpty()) {
          citStyle = "APA";
        }
        try {
          ExportFormatVO exportFormat = new ExportFormatVO(getTargetFormat().getName(), citStyle, getConfiguration().get("csl_id"));
          citationList = CitationStyleExecuterService.getOutput(itemList, exportFormat);
        } catch (Exception e) {
          logger.warn("Could not generate citations: " + e.getMessage());
        }

        int itemIdx = 0;
        for (SearchRetrieveRecordVO<ItemVersionVO> record : searchResult.getRecords()) {
          ItemVersionVO item = record.getData();
          if (item == null) {
            continue;
          }

          String citation = (citationList != null && itemIdx < citationList.size()) ? citationList.get(itemIdx) : null;
          ObjectNode workNode = buildWorkNode(item, citation, citStyle, mapper);
          ObjectNode itemWrapper = mapper.createObjectNode();
          itemWrapper.set("work", workNode);
          bulkArray.add(itemWrapper);
          itemIdx++;
        }
      }

      TransformerStreamResult res = (TransformerStreamResult) result;
      if (res.getOutputStream() != null) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(res.getOutputStream(), root);
      } else if (res.getWriter() != null) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(res.getWriter(), root);
      }
    } catch (Exception e) {
      throw new TransformationException("Error while orcid transformation", e);
    }
  }

  private ObjectNode buildWorkNode(ItemVersionVO item, String citation, String citStyle, ObjectMapper mapper) {
    ObjectNode work = mapper.createObjectNode();
    MdsPublicationVO metadata = item.getMetadata();
    if (metadata == null) {
      return work;
    }

    // 1. title
    if (metadata.getTitle() != null && !metadata.getTitle().trim().isEmpty()) {
      ObjectNode titleNode = mapper.createObjectNode();
      ObjectNode mainTitle = mapper.createObjectNode();
      mainTitle.put("value", metadata.getTitle());
      titleNode.set("title", mainTitle);

      if (metadata.getAlternativeTitles() != null) {
        for (AlternativeTitleVO alt : metadata.getAlternativeTitles()) {
          if (alt.getValue() != null && !alt.getValue().trim().isEmpty()) {
            if (alt.getLanguage() != null && !alt.getLanguage().trim().isEmpty()) {
              if (!titleNode.has("translated-title")) {
                ObjectNode trans = mapper.createObjectNode();
                trans.put("value", alt.getValue());
                trans.put("language-code", toLanguageCode(alt.getLanguage()));
                titleNode.set("translated-title", trans);
              }
            } else {
              if (!titleNode.has("subtitle")) {
                ObjectNode sub = mapper.createObjectNode();
                sub.put("value", alt.getValue());
                titleNode.set("subtitle", sub);
              }
            }
          }
        }
      }
      work.set("title", titleNode);
    }

    // 2. journal-title
    if (metadata.getSources() != null) {
      for (SourceVO src : metadata.getSources()) {
        if (src.getTitle() != null && !src.getTitle().trim().isEmpty()) {
          ObjectNode jTitle = mapper.createObjectNode();
          jTitle.put("value", src.getTitle());
          work.set("journal-title", jTitle);
          break;
        }
      }
    }

    // 3. short-description
    if (metadata.getAbstracts() != null) {
      for (AbstractVO abs : metadata.getAbstracts()) {
        if (abs.getValue() != null && !abs.getValue().trim().isEmpty()) {
          work.put("short-description", abs.getValue());
          break;
        }
      }
    }

    // 4. citation
    if (citation != null && !citation.trim().isEmpty()) {
      ObjectNode citNode = mapper.createObjectNode();
      String citType = "APA".equalsIgnoreCase(citStyle) ? "formatted-apa" : citStyle.toLowerCase();
      citNode.put("citation-type", citType);
      citNode.put("citation-value", citation);
      work.set("citation", citNode);
    }

    // 5. type
    String genre = metadata.getGenre() != null ? metadata.getGenre().name() : null;
    String workType = genre != null ? GENRE_MAPPING.getOrDefault(genre, "other") : "other";
    work.put("type", workType);

    // 6. publication-date
    String dateStr = getPublicationDateString(metadata);
    if (dateStr != null) {
      ObjectNode pubDate = parsePublicationDate(dateStr, mapper);
      if (pubDate != null) {
        work.set("publication-date", pubDate);
      }
    }

    // 7. external-ids
    ArrayNode extIdArray = mapper.createArrayNode();

    // metadata identifiers (relationship = self)
    if (metadata.getIdentifiers() != null) {
      for (IdentifierVO id : metadata.getIdentifiers()) {
        if (id.getId() != null && !id.getId().trim().isEmpty() && id.getType() != null) {
          String orcidType = EXTERNAL_ID_MAPPING.get(id.getType().name());
          if (orcidType != null) {
            ObjectNode extId = mapper.createObjectNode();
            extId.put("external-id-type", orcidType);
            extId.put("external-id-value", id.getId());
            if ("doi".equalsIgnoreCase(orcidType)) {
              ObjectNode urlObj = mapper.createObjectNode();
              urlObj.put("value", id.getId().startsWith("http") ? id.getId() : "https://doi.org/" + id.getId());
              extId.set("external-id-url", urlObj);
            } else if ("uri".equalsIgnoreCase(orcidType) && id.getId().startsWith("http")) {
              ObjectNode urlObj = mapper.createObjectNode();
              urlObj.put("value", id.getId());
              extId.set("external-id-url", urlObj);
            }
            extId.put("external-id-relationship", "self");
            extIdArray.add(extId);
          }
        }
      }
    }

    // projectInfo id -> grant_number (relationship = self)
    if (metadata.getProjectInfo() != null) {
      for (ProjectInfoVO pi : metadata.getProjectInfo()) {
        if (pi.getGrantIdentifier() != null && pi.getGrantIdentifier().getId() != null
            && !pi.getGrantIdentifier().getId().trim().isEmpty()) {
          ObjectNode extId = mapper.createObjectNode();
          extId.put("external-id-type", "grant_number");
          extId.put("external-id-value", pi.getGrantIdentifier().getId());
          extId.put("external-id-relationship", "self");
          extIdArray.add(extId);
        }
      }
    }

    // Item-ID (ohne Version!) -> source-work-id (relationship = self)
    if (item.getObjectId() != null && !item.getObjectId().trim().isEmpty()) {
      ObjectNode extId = mapper.createObjectNode();
      extId.put("external-id-type", "source-work-id");
      extId.put("external-id-value", item.getObjectId());
      extId.put("external-id-relationship", "self");
      extIdArray.add(extId);
    }

    // Object handle -> handle (relationship = self)
    String objectPid = null;
    if (item.getObject() != null && item.getObject().getObjectPid() != null) {
      objectPid = item.getObject().getObjectPid();
    } else if (item.getVersionPid() != null) {
      objectPid = item.getVersionPid();
    }
    if (objectPid != null && !objectPid.trim().isEmpty()) {
      ObjectNode extId = mapper.createObjectNode();
      extId.put("external-id-type", "handle");
      extId.put("external-id-value", objectPid);
      extId.put("external-id-relationship", "self");
      extIdArray.add(extId);
    }

    // Source identifiers (relationship = part-of)
    if (metadata.getSources() != null) {
      for (SourceVO src : metadata.getSources()) {
        if (src.getIdentifiers() != null) {
          for (IdentifierVO id : src.getIdentifiers()) {
            if (id.getId() != null && !id.getId().trim().isEmpty() && id.getType() != null) {
              String orcidType = EXTERNAL_ID_MAPPING.get(id.getType().name());
              if (orcidType != null) {
                ObjectNode extId = mapper.createObjectNode();
                extId.put("external-id-type", orcidType);
                extId.put("external-id-value", id.getId());
                if ("doi".equalsIgnoreCase(orcidType)) {
                  ObjectNode urlObj = mapper.createObjectNode();
                  urlObj.put("value", id.getId().startsWith("http") ? id.getId() : "https://doi.org/" + id.getId());
                  extId.set("external-id-url", urlObj);
                }
                extId.put("external-id-relationship", "part-of");
                extIdArray.add(extId);
              }
            }
          }
        }
      }
    }

    if (!extIdArray.isEmpty()) {
      ObjectNode extIds = mapper.createObjectNode();
      extIds.set("external-id", extIdArray);
      work.set("external-ids", extIds);
    }

    // 8. url
    if (item.getFiles() != null) {
      for (FileDbVO file : item.getFiles()) {
        if (file != null && file.getContent() != null && !file.getContent().trim().isEmpty()) {
          if (FileDbVO.Storage.EXTERNAL_URL.equals(file.getStorage()) || file.getContent().startsWith("http://")
              || file.getContent().startsWith("https://")) {
            ObjectNode urlObj = mapper.createObjectNode();
            urlObj.put("value", file.getContent().trim());
            work.set("url", urlObj);
            break;
          }
        }
      }
    }

    // 9. contributors
    if (metadata.getCreators() != null && !metadata.getCreators().isEmpty()) {
      ArrayNode contribArray = mapper.createArrayNode();
      int idx = 0;
      for (CreatorVO creator : metadata.getCreators()) {
        ObjectNode contrib = mapper.createObjectNode();

        if (creator.getPerson() != null && creator.getPerson().getOrcid() != null && !creator.getPerson().getOrcid().trim().isEmpty()) {
          String orcid = creator.getPerson().getOrcid().trim();
          String path = orcid.replaceFirst("^https?://orcid\\.org/", "").replaceFirst("^orcid\\.org/", "");
          ObjectNode orcidNode = mapper.createObjectNode();
          orcidNode.put("uri", "https://orcid.org/" + path);
          orcidNode.put("path", path);
          orcidNode.put("host", "orcid.org");
          contrib.set("contributor-orcid", orcidNode);
        }

        String name = null;
        if (creator.getPerson() != null) {
          PersonVO p = creator.getPerson();
          if (p.getCompleteName() != null && !p.getCompleteName().trim().isEmpty()) {
            name = p.getCompleteName();
          } else {
            StringBuilder sb = new StringBuilder();
            if (p.getGivenName() != null && !p.getGivenName().trim().isEmpty()) {
              sb.append(p.getGivenName().trim());
            }
            if (p.getFamilyName() != null && !p.getFamilyName().trim().isEmpty()) {
              if (sb.length() > 0)
                sb.append(" ");
              sb.append(p.getFamilyName().trim());
            }
            name = sb.length() > 0 ? sb.toString() : null;
          }
        } else if (creator.getOrganization() != null && creator.getOrganization().getName() != null) {
          name = creator.getOrganization().getName();
        }

        if (name != null) {
          ObjectNode creditName = mapper.createObjectNode();
          creditName.put("value", name);
          contrib.set("credit-name", creditName);
        }

        ObjectNode attrs = mapper.createObjectNode();
        attrs.put("contributor-sequence", idx == 0 ? "first" : "additional");
        String role = "author";
        if (creator.getRole() != null) {
          if (CreatorVO.CreatorRole.EDITOR.equals(creator.getRole())) {
            role = "editor";
          } else if (CreatorVO.CreatorRole.AUTHOR.equals(creator.getRole())) {
            role = "author";
          }
        }
        attrs.put("contributor-role", role);
        contrib.set("contributor-attributes", attrs);

        contribArray.add(contrib);
        idx++;
      }

      if (!contribArray.isEmpty()) {
        ObjectNode contributorsNode = mapper.createObjectNode();
        contributorsNode.set("contributor", contribArray);
        work.set("contributors", contributorsNode);
      }
    }

    // 10. language-code
    if (metadata.getLanguages() != null && !metadata.getLanguages().isEmpty()) {
      String lang = metadata.getLanguages().get(0);
      if (lang != null && !lang.trim().isEmpty()) {
        work.put("language-code", toLanguageCode(lang.trim()));
      }
    }

    return work;
  }

  private String getPublicationDateString(MdsPublicationVO metadata) {
    if (metadata.getDatePublishedInPrint() != null && !metadata.getDatePublishedInPrint().trim().isEmpty()) {
      return metadata.getDatePublishedInPrint().trim();
    }
    if (metadata.getDatePublishedOnline() != null && !metadata.getDatePublishedOnline().trim().isEmpty()) {
      return metadata.getDatePublishedOnline().trim();
    }
    if (metadata.getDateAccepted() != null && !metadata.getDateAccepted().trim().isEmpty()) {
      return metadata.getDateAccepted().trim();
    }
    if (metadata.getDateSubmitted() != null && !metadata.getDateSubmitted().trim().isEmpty()) {
      return metadata.getDateSubmitted().trim();
    }
    if (metadata.getDateModified() != null && !metadata.getDateModified().trim().isEmpty()) {
      return metadata.getDateModified().trim();
    }
    if (metadata.getDateCreated() != null && !metadata.getDateCreated().trim().isEmpty()) {
      return metadata.getDateCreated().trim();
    }
    if (metadata.getEvent() != null && metadata.getEvent().getStartDate() != null && !metadata.getEvent().getStartDate().trim().isEmpty()) {
      return metadata.getEvent().getStartDate().trim();
    }
    return null;
  }

  private ObjectNode parsePublicationDate(String dateStr, ObjectMapper mapper) {
    Matcher matcher = DATE_PATTERN.matcher(dateStr);
    if (matcher.find()) {
      String year = matcher.group(1);
      String month = matcher.group(2);
      String day = matcher.group(3);

      ObjectNode pubDate = mapper.createObjectNode();
      if (year != null && !year.isEmpty()) {
        ObjectNode yNode = mapper.createObjectNode();
        yNode.put("value", year);
        pubDate.set("year", yNode);
      }
      if (month != null && !month.isEmpty()) {
        ObjectNode mNode = mapper.createObjectNode();
        mNode.put("value", String.format("%02d", Integer.parseInt(month)));
        pubDate.set("month", mNode);
      }
      if (day != null && !day.isEmpty()) {
        ObjectNode dNode = mapper.createObjectNode();
        dNode.put("value", String.format("%02d", Integer.parseInt(day)));
        pubDate.set("day", dNode);
      }
      return pubDate;
    }
    return null;
  }

  private static String toLanguageCode(String lang) {
    if (lang == null || lang.trim().isEmpty()) {
      return null;
    }
    lang = lang.trim();
    if (lang.length() == 2) {
      return lang.toLowerCase();
    }
    for (Locale locale : Locale.getAvailableLocales()) {
      try {
        if (locale.getISO3Language().equalsIgnoreCase(lang)) {
          return locale.getLanguage();
        }
      } catch (Exception ignored) {
      }
    }
    return lang.toLowerCase();
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    TransformerStreamResult tr = new TransformerStreamResult(new ByteArrayOutputStream());
    return tr;
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {}

}
