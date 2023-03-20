/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.dom.BibtexToplevelComment;
import bibtex.parser.BibtexParser;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.SubjectClassification;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.util.creators.Author;
import de.mpg.mpdl.inge.transformation.util.creators.AuthorDecoder;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Implementation of BibTex transformation.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author: MWalter $ (last modification)
 * @version $Revision: 5725 $ $LastChangedDate: 2015-10-07 14:43:23 +0200 (Wed, 07 Oct 2015) $
 */
public class Bibtex implements BibtexInterface {
  private static final Logger logger = Logger.getLogger(Bibtex.class);
  private Map<String, String> configuration = null;
  private Set<String> groupSet = null;
  private Set<String> projectSet = null;

  /**
   * sets the configuration-settings
   * 
   * @param configuration
   */
  public void setConfiguration(Map<String, String> configuration) {
    this.configuration = configuration;
  }

  /**
   * @param bibtex
   * @return eSciDoc-publication item XML representation of this BibTeX entry
   * @throws RuntimeException
   */
  public String getBibtex(String bibtex) throws RuntimeException {
    // Remove Math '$' from the whole BibTex-String
    Pattern mathPattern = Pattern.compile("(?sm)\\$(\\\\.*?)(?<!\\\\)\\$");
    Matcher mathMatcher = mathPattern.matcher(bibtex);
    StringBuffer sb = new StringBuffer();
    while (mathMatcher.find()) {
      mathMatcher.appendReplacement(sb, "$1");
    }
    mathMatcher.appendTail(sb);
    bibtex = sb.toString();
    BibtexParser parser = new BibtexParser(true);
    BibtexFile file = new BibtexFile();
    try {
      parser.parse(file, new StringReader(bibtex));
    } catch (Exception e) {
      logger.error("Error parsing BibTex record.");
      throw new RuntimeException(e);
    }
    PubItemVO itemVO = new PubItemVO();
    MdsPublicationVO mds = new MdsPublicationVO();
    itemVO.setMetadata(mds);
    List<?> entries = file.getEntries();
    boolean entryFound = false;
    if (entries == null || entries.size() == 0) {
      logger.warn("No entry found in BibTex record.");
      throw new RuntimeException();
    }
    for (Object object : entries) {
      if (object instanceof BibtexEntry) {
        if (entryFound) {
          logger.error("Multiple entries in BibTex record.");
          throw new RuntimeException();
        }
        entryFound = true;
        BibtexEntry entry = (BibtexEntry) object;
        // genre
        BibTexUtil.Genre bibGenre;
        try {
          bibGenre = BibTexUtil.Genre.valueOf(entry.getEntryType());
        } catch (IllegalArgumentException iae) {
          bibGenre = BibTexUtil.Genre.misc;
          logger.warn("Unrecognized genre: " + entry.getEntryType());
        }
        MdsPublicationVO.Genre itemGenre = BibTexUtil.getGenreMapping().get(bibGenre);
        mds.setGenre(itemGenre);
        SourceVO sourceVO = new SourceVO();
        SourceVO secondSourceVO = new SourceVO();
        Map<?, ?> fields = entry.getFields();
        // Mapping of BibTeX Standard Entries
        // title
        if (fields.get("title") != null) {
          if (fields.get("chapter") != null) {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("chapter").toString()), false) + " - "
                + BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("title").toString()), false));
          } else {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("title").toString()), false));
          }
        }
        // booktitle
        if (fields.get("booktitle") != null) {
          if (bibGenre == BibTexUtil.Genre.book) {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("booktitle").toString()), false));
          } else if (bibGenre == BibTexUtil.Genre.conference || bibGenre == BibTexUtil.Genre.inbook
              || bibGenre == BibTexUtil.Genre.incollection || bibGenre == BibTexUtil.Genre.inproceedings) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("booktitle").toString()), false));
            if (bibGenre == BibTexUtil.Genre.conference || bibGenre == BibTexUtil.Genre.inproceedings) {
              sourceVO.setGenre(Genre.PROCEEDINGS);
            } else if (bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.incollection) {
              sourceVO.setGenre(Genre.BOOK);
            }
          }
        }
        // fjournal, journal
        if (fields.get("fjournal") != null) {
          if (bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.misc || bibGenre == BibTexUtil.Genre.unpublished) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("fjournal").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.JOURNAL);
            if (fields.get("journal") != null) {
              sourceVO.getAlternativeTitles()
                  .add(new AlternativeTitleVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("journal").toString()), false)));
            }
          }
        } else if (fields.get("journal") != null) {
          if (bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.misc || bibGenre == BibTexUtil.Genre.unpublished
              || bibGenre == BibTexUtil.Genre.inproceedings) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("journal").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.JOURNAL);
          }
        }
        // number
        if (fields.get("number") != null && bibGenre != BibTexUtil.Genre.techreport) {
          sourceVO.setIssue(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("number").toString()), false));
        } else if (fields.get("number") != null && bibGenre == BibTexUtil.Genre.techreport) {
          {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.REPORT_NR,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("number").toString()), false)));
          }
        }
        // pages
        if (fields.get("pages") != null) {
          if (bibGenre == BibTexUtil.Genre.book || bibGenre == BibTexUtil.Genre.proceedings) {
            mds.setTotalNumberOfPages(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("pages").toString()), false));
          } else {
            BibTexUtil.fillSourcePages(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("pages").toString()), false), sourceVO);
            if (bibGenre == BibTexUtil.Genre.inproceedings && (fields.get("booktitle") == null || fields.get("booktitle").toString() == "")
                && (fields.get("event_name") != null && fields.get("event_name").toString() != "")) {
              sourceVO.setTitle(BibTexUtil.stripBraces(fields.get("event_name").toString(), false));
              sourceVO.setGenre(Genre.PROCEEDINGS);
            }
          }
        }
        // Publishing info
        PublishingInfoVO publishingInfoVO = new PublishingInfoVO();
        mds.setPublishingInfo(publishingInfoVO);
        // address
        if (fields.get("address") != null) {
          if (!(bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.inproceedings
              || bibGenre == BibTexUtil.Genre.conference || bibGenre == BibTexUtil.Genre.incollection)
              && (sourceVO.getTitle() == null || sourceVO.getTitle() == null)) {
            publishingInfoVO.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("address").toString()), false));
          } else {
            if (sourceVO.getPublishingInfo() == null) {
              PublishingInfoVO sourcePublishingInfoVO = new PublishingInfoVO();
              sourceVO.setPublishingInfo(sourcePublishingInfoVO);
            }
            sourceVO.getPublishingInfo().setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("address").toString()), false));
          }
        }
        // edition
        if (fields.get("edition") != null) {
          publishingInfoVO.setEdition(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("edition").toString()), false));
        }
        // publisher
        if (!(bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.inproceedings
            || bibGenre == BibTexUtil.Genre.conference || bibGenre == BibTexUtil.Genre.incollection)
            && (sourceVO.getTitle() == null || sourceVO.getTitle() == null)) {
          if (fields.get("publisher") != null) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("publisher").toString()), false));
          } else if (fields.get("school") != null && (bibGenre == BibTexUtil.Genre.mastersthesis || bibGenre == BibTexUtil.Genre.phdthesis
              || bibGenre == BibTexUtil.Genre.techreport)) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("school").toString()), false));
          } else if (fields.get("institution") != null) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("institution").toString()), false));
          } else if (fields.get("publisher") == null && fields.get("school") == null && fields.get("institution") == null
              && fields.get("address") != null) {
            publishingInfoVO.setPublisher("ANY PUBLISHER");
          }
        } else {
          if (sourceVO.getPublishingInfo() == null) {
            PublishingInfoVO sourcePublishingInfoVO = new PublishingInfoVO();
            sourceVO.setPublishingInfo(sourcePublishingInfoVO);
          }
          if (fields.get("publisher") != null) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("publisher").toString()), false));
          } else if (fields.get("school") != null && (bibGenre == BibTexUtil.Genre.mastersthesis || bibGenre == BibTexUtil.Genre.phdthesis
              || bibGenre == BibTexUtil.Genre.techreport)) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("school").toString()), false));
          } else if (fields.get("institution") != null) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("institution").toString()), false));
          } else if (fields.get("publisher") == null && fields.get("school") == null && fields.get("institution") == null
              && fields.get("address") != null) {
            sourceVO.getPublishingInfo().setPublisher("ANY PUBLISHER");
          }
        }
        // series
        if (fields.get("series") != null) {
          if (bibGenre == BibTexUtil.Genre.book || bibGenre == BibTexUtil.Genre.misc || bibGenre == BibTexUtil.Genre.techreport) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("series").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.SERIES);
          } else if (bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.incollection
              || bibGenre == BibTexUtil.Genre.inproceedings || bibGenre == BibTexUtil.Genre.conference) {
            secondSourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("series").toString()), false));
            secondSourceVO.setGenre(SourceVO.Genre.SERIES);
          }
        }
        // type --> degree
        if (fields.get("type") != null && bibGenre == BibTexUtil.Genre.mastersthesis) {
          if (fields.get("type").toString().toLowerCase().contains("master") || fields.get("type").toString().toLowerCase().contains("m.a.")
              || fields.get("type").toString().toLowerCase().contains("m.s.")
              || fields.get("type").toString().toLowerCase().contains("m.sc.")) {
            mds.setDegree(MdsPublicationVO.DegreeType.MASTER);
          } else if (fields.get("type").toString().toLowerCase().contains("bachelor")) {
            mds.setDegree(MdsPublicationVO.DegreeType.BACHELOR);
          } else if (fields.get("type").toString().toLowerCase().contains("magister")) {
            mds.setDegree(MdsPublicationVO.DegreeType.MAGISTER);
          } else if (fields.get("type").toString().toLowerCase().contains("diplom")) // covers also
                                                                                     // the english
                                                                                     // version
                                                                                     // (diploma)
          {
            mds.setDegree(MdsPublicationVO.DegreeType.DIPLOMA);
          } else if (fields.get("type").toString().toLowerCase().contains("statsexamen")
              || fields.get("type").toString().toLowerCase().contains("state examination")) {
            mds.setDegree(MdsPublicationVO.DegreeType.DIPLOMA);
          }
        } else if (fields.get("type") != null && bibGenre == BibTexUtil.Genre.phdthesis) {
          if (fields.get("type").toString().toLowerCase().contains("phd")
              || fields.get("type").toString().toLowerCase().contains("dissertation")
              || fields.get("type").toString().toLowerCase().contains("doktor")
              || fields.get("type").toString().toLowerCase().contains("doctor")) {
            mds.setDegree(MdsPublicationVO.DegreeType.PHD);
          } else if (fields.get("type").toString().toLowerCase().contains("habilitation")) {
            mds.setDegree(MdsPublicationVO.DegreeType.HABILITATION);
          }
        }
        // volume
        if (fields.get("volume") != null) {
          if (bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.book) {
            sourceVO.setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
          } else if (bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.inproceedings
              || bibGenre == BibTexUtil.Genre.incollection || bibGenre == BibTexUtil.Genre.conference) {
            if (sourceVO.getSources() != null && !sourceVO.getSources().isEmpty()) {
              sourceVO.getSources().get(0)
                  .setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
            } else {
              sourceVO.setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
            }
          }
        }
        // event infos
        if (bibGenre != null && (bibGenre.equals(BibTexUtil.Genre.inproceedings) || bibGenre.equals(BibTexUtil.Genre.proceedings)
            || bibGenre.equals(BibTexUtil.Genre.conference) || bibGenre.equals(BibTexUtil.Genre.poster)
            || bibGenre.equals(BibTexUtil.Genre.talk))) {
          EventVO event = new EventVO();
          boolean eventNotEmpty = false;
          // event location
          if (fields.get("location") != null) {
            event.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("location").toString()), false));
            eventNotEmpty = true;
          }
          // event place
          else if (fields.get("event_place") != null) {
            event.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("event_place").toString()), false));
            eventNotEmpty = true;
          }
          // event name/title
          if (fields.get("event_name") != null) {
            event.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("event_name").toString()), false));
            eventNotEmpty = true;
          }
          // event will be set only it's not empty
          if (eventNotEmpty == true) {
            if (event.getTitle() == null) {
              event.setTitle("");
            }
            mds.setEvent(event);
          }
        }
        // year, month
        String dateString = null;
        if (fields.get("year") != null) {
          dateString = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("year").toString()), false);
          if (fields.get("month") != null) {
            String month = BibTexUtil.parseMonth(fields.get("month").toString());
            dateString += "-" + month;
          }
          if (bibGenre == BibTexUtil.Genre.unpublished) {
            mds.setDateCreated(dateString);
          } else {
            mds.setDatePublishedInPrint(dateString);
          }
        }
        String affiliation = null;
        String affiliationAddress = null;
        // affiliation
        if (fields.get("affiliation") != null) {
          affiliation = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("affiliation").toString()), false);
        }
        // affiliationaddress
        if (fields.get("affiliationaddress") != null) {
          affiliationAddress = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("affiliationaddress").toString()), false);
        }
        // author
        boolean noConeAuthorFound = true;
        if (fields.get("author") != null) {
          if (fields.get("author") instanceof BibtexPersonList) {
            BibtexPersonList authors = (BibtexPersonList) fields.get("author");
            for (Object author : authors.getList()) {
              if (author instanceof BibtexPerson) {
                addCreator(mds, (BibtexPerson) author, CreatorVO.CreatorRole.AUTHOR, affiliation, affiliationAddress);
              } else {
                logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + author + "] in [" + author + "]");
              }
            }
          } else if (fields.get("author") instanceof BibtexPerson) {
            BibtexPerson author = (BibtexPerson) fields.get("author");
            addCreator(mds, (BibtexPerson) author, CreatorVO.CreatorRole.AUTHOR, affiliation, affiliationAddress);
          } else if (fields.get("author") instanceof BibtexString) {
            AuthorDecoder decoder;
            try {
              String authorString = BibTexUtil.bibtexDecode(fields.get("author").toString(), false);
              List<CreatorVO> teams = new ArrayList<CreatorVO>();
              if (authorString.contains("Team")) {
                // set pattern for finding Teams (leaded or followed by [and|,|;|{|}|^|$])
                Pattern pattern = Pattern.compile("(?<=(and|,|;|\\{|^))([\\w|\\s]*?Team[\\w|\\s]*?)(?=(and|,|;|\\}|$))", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(authorString);
                String matchedGroup;
                while (matcher.find()) {
                  matchedGroup = matcher.group();
                  // remove matchedGroup (and prefix/suffix) from authorString
                  if (authorString.startsWith(matchedGroup)) {
                    authorString = authorString.replaceAll(matchedGroup + "(and|,|;|\\})", "");
                  } else {
                    authorString = authorString.replaceAll("(and|,|;|\\{)" + matchedGroup, "");
                  }
                  // set matchedGroup as Organisation Author
                  OrganizationVO team = new OrganizationVO();
                  team.setName(matchedGroup.trim());
                  CreatorVO creatorVO = new CreatorVO(team, CreatorVO.CreatorRole.AUTHOR);
                  teams.add(creatorVO);
                }
              }
              decoder = new AuthorDecoder(authorString, false);
              if (decoder.getBestFormat() != null) {
                List<Author> authors = decoder.getAuthorListList().get(0);
                for (Author author : authors) {
                  PersonVO personVO = new PersonVO();
                  personVO.setFamilyName(author.getSurname());
                  if (author.getGivenName() != null) {
                    personVO.setGivenName(author.getGivenName());
                  } else {
                    personVO.setGivenName(author.getInitial());
                  }
                  /*
                   * Case for MPI-KYB (Biological Cybernetics) with CoNE identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (author.getTags().get("identifier") != null)) {
                    String query = author.getTags().get("identifier");
                    int affiliationsCount = Integer.parseInt(author.getTags().get("affiliationsCount"));
                    if (affiliationsCount > 0 || configuration.get("OrganizationalUnit") != null) {
                      for (int ouCount = 0; ouCount < (affiliationsCount > 0 ? affiliationsCount : 1); ouCount++) // 1
                                                                                                                  // is
                                                                                                                  // for
                                                                                                                  // the
                                                                                                                  // case
                                                                                                                  // configuration.get("OrganizationalUnit")
                                                                                                                  // !=
                                                                                                                  // null
                      {
                        String organizationalUnit = (author.getTags().get("affiliation" + String.valueOf(ouCount)) != null
                            ? author.getTags().get("affiliation" + String.valueOf(ouCount))
                            : (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                        Node coneEntries = null;
                        if (query.equals(author.getTags().get("identifier"))) {
                          coneEntries = Util.queryConeExactWithIdentifier("persons", query, organizationalUnit);
                          // for MPIKYB due to OUs which do not occur in CoNE
                          if (coneEntries.getFirstChild().getFirstChild() == null) {
                            logger.error("No Person with Identifier (" + author.getTags().get("identifier") + ") and OU ("
                                + organizationalUnit + ") found in CoNE for Publication \"" + fields.get("title") + "\"");
                          }
                        } else {
                          coneEntries = Util.queryConeExact("persons", query, organizationalUnit);
                        }
                        Node coneNode = coneEntries.getFirstChild().getFirstChild();
                        if (coneNode != null) {
                          Node currentNode = coneNode.getFirstChild();
                          boolean first = true;
                          while (currentNode != null) {
                            if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                              first = false;
                              noConeAuthorFound = false;
                              Node coneEntry = currentNode;
                              String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                              personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                              for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                                Node posNode = coneEntry.getChildNodes().item(i);
                                if ("escidoc:position".equals(posNode.getNodeName())) {
                                  String from = null;
                                  String until = null;
                                  String name = null;
                                  String id = null;
                                  Node node = posNode.getFirstChild().getFirstChild();
                                  while (node != null) {
                                    if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                      name = node.getFirstChild().getNodeValue();
                                    } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                      from = node.getFirstChild().getNodeValue();
                                    } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                      until = node.getFirstChild().getNodeValue();
                                    } else if ("dc:identifier".equals(node.getNodeName())) {
                                      id = node.getFirstChild().getNodeValue();
                                    }
                                    node = node.getNextSibling();
                                  }
                                  if (smaller(from, dateString) && smaller(dateString, until)) {
                                    OrganizationVO org = new OrganizationVO();
                                    org.setName(name);
                                    org.setIdentifier(id);
                                    personVO.getOrganizations().add(org);
                                  }
                                }
                              }
                            } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                              throw new RuntimeException("Ambigous CoNE entries for " + query);
                            }
                            currentNode = currentNode.getNextSibling();
                          }
                        } else {
                          throw new RuntimeException("Missing CoNE entry for " + query);
                        }
                      }
                    }
                  }
                  /*
                   * Case for MPI-Microstructure Physics with affiliation identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("affiliation id in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (author.getTags().get("identifier") != null)) {
                    String identifier = author.getTags().get("identifier");
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    if (!("extern".equals(identifier))) {
                      Node coneEntries = null;
                      coneEntries = Util.queryConeExact("persons", query,
                          (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                      Node coneNode = coneEntries.getFirstChild().getFirstChild();
                      if (coneNode != null) {
                        Node currentNode = coneNode.getFirstChild();
                        boolean first = true;
                        while (currentNode != null) {
                          if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                            first = false;
                            noConeAuthorFound = false;
                            Node coneEntry = currentNode;
                            String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                            personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                            if (identifier != null && !("".equals(identifier))) {
                              try {
                                String ouSubTitle = identifier.substring(0, identifier.indexOf(","));
                                Document document = Util.queryFramework(
                                    "/oum/organizational-units?query=" + URLEncoder.encode("\"/title\"=\"" + ouSubTitle + "\"", "UTF-8"));
                                NodeList ouList = document.getElementsByTagNameNS("http://www.escidoc.de/schemas/organizationalunit/0.8",
                                    "organizational-unit");
                                Element ou = (Element) ouList.item(0);
                                String href = ou.getAttribute("xlink:href");
                                String ouId = href.substring(href.lastIndexOf("/") + 1);
                                OrganizationVO org = new OrganizationVO();
                                org.setName(identifier);
                                org.setIdentifier(ouId);
                                personVO.getOrganizations().add(org);
                              } catch (Exception e) {
                                logger.error("Error getting OUs", e);
                                throw new RuntimeException("Error getting Organizational Unit for " + identifier);
                              }
                            }
                          } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            throw new RuntimeException("Ambigous CoNE entries for " + query);
                          }
                          currentNode = currentNode.getNextSibling();
                        }
                      } else {
                        throw new RuntimeException("Missing CoNE entry for " + query);
                      }
                    }
                  } else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("empty brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors"))
                          && (author.getTags().get("brackets") != null))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (coneNode != null) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (currentNode != null) {
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                          first = false;
                          noConeAuthorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (node != null) {
                                if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                  name = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                  from = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                  until = node.getFirstChild().getNodeValue();
                                } else if ("dc:identifier".equals(node.getNodeName())) {
                                  id = node.getFirstChild().getNodeValue();
                                }
                                node = node.getNextSibling();
                              }
                              if (smaller(from, dateString) && smaller(dateString, until)) {
                                OrganizationVO org = new OrganizationVO();
                                org.setName(name);
                                org.setIdentifier(id);
                                personVO.getOrganizations().add(org);
                              }
                            }
                          }
                        } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    } else {
                      throw new RuntimeException("Missing CoNE entry for " + query);
                    }
                  } else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("no".equals(configuration.get("CurlyBracketsForCoNEAuthors")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (coneNode != null) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (currentNode != null) {
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                          first = false;
                          noConeAuthorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (node != null) {
                                if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                  name = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                  from = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                  until = node.getFirstChild().getNodeValue();
                                } else if ("dc:identifier".equals(node.getNodeName())) {
                                  id = node.getFirstChild().getNodeValue();
                                }
                                node = node.getNextSibling();
                              }
                              if (smaller(from, dateString) && smaller(dateString, until)) {
                                OrganizationVO org = new OrganizationVO();
                                org.setName(name);
                                org.setIdentifier(id);
                                personVO.getOrganizations().add(org);
                              }
                            }
                          }
                        } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    }
                  }
                  /*
                   * Case for MPI-RA (Radio Astronomy) with identifier and affiliation in brackets
                   * This Case is using NO CoNE!
                   */
                  if (configuration != null && "false".equals(configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (author.getTags().get("identifier") != null)) {
                    String identifier = author.getTags().get("identifier");
                    String authoAffiliation = author.getTags().get("affiliation0");
                    OrganizationVO org = new OrganizationVO();
                    org.setName(authoAffiliation);
                    org.setIdentifier(identifier);
                    personVO.getOrganizations().add(org);
                  }
                  if (affiliation != null) {
                    OrganizationVO organization = new OrganizationVO();
                    organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
                    organization.setName(affiliation);
                    organization.setAddress(affiliationAddress);
                    personVO.getOrganizations().add(organization);
                  }
                  CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.AUTHOR);
                  mds.getCreators().add(creatorVO);
                }
              }
              if (!teams.isEmpty()) {
                mds.getCreators().addAll(teams);
              }
            } catch (Exception e) {
              logger.error("An error occured while getting field 'author'.", e);
              throw new RuntimeException(e);
            }
          }
        }
        // editor
        boolean noConeEditorFound = false;
        if (fields.get("editor") != null) {
          logger.debug("fields.get(\"editor\"): " + fields.get("editor").getClass());
          if (fields.get("editor") instanceof BibtexPersonList) {
            BibtexPersonList editors = (BibtexPersonList) fields.get("editor");
            for (Object editor : editors.getList()) {
              if (editor instanceof BibtexPerson) {
                addCreator(mds, (BibtexPerson) editor, CreatorVO.CreatorRole.EDITOR, affiliation, affiliationAddress);
              } else {
                logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + editor + "] in [" + editors + "]");
              }
            }
          } else if (fields.get("editor") instanceof BibtexPerson) {
            BibtexPerson editor = (BibtexPerson) fields.get("editor");
            addCreator(mds, (BibtexPerson) editor, CreatorVO.CreatorRole.EDITOR, affiliation, affiliationAddress);
          } else if (fields.get("editor") instanceof BibtexString) {
            AuthorDecoder decoder;
            try {
              String editorString = BibTexUtil.bibtexDecode(fields.get("editor").toString(), false);
              List<CreatorVO> teams = new ArrayList<CreatorVO>();
              if (editorString.contains("Team")) {
                // set pattern for finding Teams (leaded or followed by [and|,|;|{|}|^|$])
                Pattern pattern = Pattern.compile("(?<=(and|,|;|\\{|^))([\\w|\\s]*?Team[\\w|\\s]*?)(?=(and|,|;|\\}|$))", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(editorString);
                String matchedGroup;
                while (matcher.find()) {
                  matchedGroup = matcher.group();
                  // remove matchedGroup (and prefix/suffix) from authorString
                  if (editorString.startsWith(matchedGroup)) {
                    editorString = editorString.replaceAll(matchedGroup + "(and|,|;|\\})", "");
                  } else {
                    editorString = editorString.replaceAll("(and|,|;|\\{)" + matchedGroup, "");
                  }
                  // set matchedGroup as Organisation Author
                  OrganizationVO team = new OrganizationVO();
                  team.setName(matchedGroup.trim());
                  CreatorVO creatorVO = new CreatorVO(team, CreatorVO.CreatorRole.EDITOR);
                  teams.add(creatorVO);
                }
              }
              decoder = new AuthorDecoder(editorString, false);
              if (decoder.getBestFormat() != null) {
                List<Author> editors = decoder.getAuthorListList().get(0);
                for (Author editor : editors) {
                  PersonVO personVO = new PersonVO();
                  personVO.setFamilyName(editor.getSurname());
                  if (editor.getGivenName() != null) {
                    personVO.setGivenName(editor.getGivenName());
                  } else {
                    personVO.setGivenName(editor.getInitial());
                  }
                  /*
                   * Case for MPI-KYB (Biological Cybernetics) with CoNE identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (editor.getTags().get("identifier") != null)) {
                    String query = editor.getTags().get("identifier");
                    int affiliationsCount = Integer.parseInt(editor.getTags().get("affiliationsCount"));
                    if (affiliationsCount > 0 || configuration.get("OrganizationalUnit") != null) {
                      for (int ouCount = 0; ouCount < (affiliationsCount > 0 ? affiliationsCount : 1); ouCount++) // 1
                                                                                                                  // is
                                                                                                                  // for
                                                                                                                  // the
                                                                                                                  // case
                                                                                                                  // configuration.get("OrganizationalUnit")
                                                                                                                  // !=
                                                                                                                  // null
                      {
                        String organizationalUnit = (editor.getTags().get("affiliation" + String.valueOf(ouCount)) != null
                            ? editor.getTags().get("affiliation" + String.valueOf(ouCount))
                            : (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                        Node coneEntries = null;
                        if (query.equals(editor.getTags().get("identifier"))) {
                          coneEntries = Util.queryConeExactWithIdentifier("persons", query, organizationalUnit);
                          // for MPIKYB due to OUs which do not occur in CoNE
                          if (coneEntries.getFirstChild().getFirstChild() == null) {
                            logger.error("No Person with Identifier (" + editor.getTags().get("identifier") + ") and OU ("
                                + organizationalUnit + ") found in CoNE for Publication \"" + fields.get("title") + "\"");
                          }
                        } else {
                          coneEntries = Util.queryConeExact("persons", query, organizationalUnit);
                        }
                        Node coneNode = coneEntries.getFirstChild().getFirstChild();
                        if (coneNode != null) {
                          Node currentNode = coneNode.getFirstChild();
                          boolean first = true;
                          while (currentNode != null) {
                            if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                              first = false;
                              noConeEditorFound = false;
                              Node coneEntry = currentNode;
                              String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                              personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                              for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                                Node posNode = coneEntry.getChildNodes().item(i);
                                if ("escidoc:position".equals(posNode.getNodeName())) {
                                  String from = null;
                                  String until = null;
                                  String name = null;
                                  String id = null;
                                  Node node = posNode.getFirstChild().getFirstChild();
                                  while (node != null) {
                                    if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                      name = node.getFirstChild().getNodeValue();
                                    } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                      from = node.getFirstChild().getNodeValue();
                                    } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                      until = node.getFirstChild().getNodeValue();
                                    } else if ("dc:identifier".equals(node.getNodeName())) {
                                      id = node.getFirstChild().getNodeValue();
                                    }
                                    node = node.getNextSibling();
                                  }
                                  if (smaller(from, dateString) && smaller(dateString, until)) {
                                    OrganizationVO org = new OrganizationVO();
                                    org.setName(name);
                                    org.setIdentifier(id);
                                    personVO.getOrganizations().add(org);
                                  }
                                }
                              }
                            } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                              throw new RuntimeException("Ambigous CoNE entries for " + query);
                            }
                            currentNode = currentNode.getNextSibling();
                          }
                        } else {
                          throw new RuntimeException("Missing CoNE entry for " + query);
                        }
                      }
                    }
                  }
                  /*
                   * Case for MPI-Microstructure Physics with affiliation identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("affiliation id in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (editor.getTags().get("identifier") != null)) {
                    String identifier = editor.getTags().get("identifier");
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    if (!("extern".equals(identifier))) {
                      Node coneEntries = null;
                      coneEntries = Util.queryConeExact("persons", query,
                          (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                      Node coneNode = coneEntries.getFirstChild().getFirstChild();
                      if (coneNode != null) {
                        Node currentNode = coneNode.getFirstChild();
                        boolean first = true;
                        while (currentNode != null) {
                          if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                            first = false;
                            noConeAuthorFound = false;
                            Node coneEntry = currentNode;
                            String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                            personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                            if (identifier != null && !("".equals(identifier))) {
                              try {
                                String ouSubTitle = identifier.substring(0, identifier.indexOf(","));
                                Document document = Util.queryFramework(
                                    "/oum/organizational-units?query=" + URLEncoder.encode("\"/title\"=\"" + ouSubTitle + "\"", "UTF-8"));
                                NodeList ouList = document.getElementsByTagNameNS("http://www.escidoc.de/schemas/organizationalunit/0.8",
                                    "organizational-unit");
                                Element ou = (Element) ouList.item(0);
                                String href = ou.getAttribute("xlink:href");
                                String ouId = href.substring(href.lastIndexOf("/") + 1);
                                OrganizationVO org = new OrganizationVO();
                                org.setName(identifier);
                                org.setIdentifier(ouId);
                                personVO.getOrganizations().add(org);
                              } catch (Exception e) {
                                logger.error("Error getting OUs", e);
                                throw new RuntimeException("Error getting Organizational Unit for " + identifier);
                              }
                            }
                          } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            throw new RuntimeException("Ambigous CoNE entries for " + query);
                          }
                          currentNode = currentNode.getNextSibling();
                        }
                      } else {
                        throw new RuntimeException("Missing CoNE entry for " + query);
                      }
                    }
                  } else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("empty brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors"))
                          && (editor.getTags().get("brackets") != null))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (coneNode != null) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (currentNode != null) {
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                          first = false;
                          noConeEditorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (node != null) {
                                if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                  name = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                  from = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                  until = node.getFirstChild().getNodeValue();
                                } else if ("dc:identifier".equals(node.getNodeName())) {
                                  id = node.getFirstChild().getNodeValue();
                                }
                                node = node.getNextSibling();
                              }
                              if (smaller(from, dateString) && smaller(dateString, until)) {
                                OrganizationVO org = new OrganizationVO();
                                org.setName(name);
                                org.setIdentifier(id);
                                personVO.getOrganizations().add(org);
                              }
                            }
                          }
                        } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    } else {
                      throw new RuntimeException("Missing CoNE entry for " + query);
                    }
                  } else if (configuration != null && "true".equals(configuration.get("CoNE"))
                      && ("no".equals(configuration.get("CurlyBracketsForCoNEAuthors")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (coneNode != null) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (currentNode != null) {
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE && first) {
                          first = false;
                          noConeEditorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (node != null) {
                                if ("eprints:affiliatedInstitution".equals(node.getNodeName())) {
                                  name = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:start-date".equals(node.getNodeName())) {
                                  from = node.getFirstChild().getNodeValue();
                                } else if ("escidoc:end-date".equals(node.getNodeName())) {
                                  until = node.getFirstChild().getNodeValue();
                                } else if ("dc:identifier".equals(node.getNodeName())) {
                                  id = node.getFirstChild().getNodeValue();
                                }
                                node = node.getNextSibling();
                              }
                              if (smaller(from, dateString) && smaller(dateString, until)) {
                                OrganizationVO org = new OrganizationVO();
                                org.setName(name);
                                org.setIdentifier(id);
                                personVO.getOrganizations().add(org);
                              }
                            }
                          }
                        } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    }
                  }
                  /*
                   * Case for MPI-RA (Radio Astronomy) with identifier and affiliation in brackets
                   * This Case is using NO CoNE!
                   */
                  if (configuration != null && "false".equals(configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (editor.getTags().get("identifier") != null)) {
                    String identifier = editor.getTags().get("identifier");
                    String authoAffiliation = editor.getTags().get("affiliation0");
                    OrganizationVO org = new OrganizationVO();
                    org.setName(authoAffiliation);
                    org.setIdentifier(identifier);
                    personVO.getOrganizations().add(org);
                  }
                  if (affiliation != null) {
                    OrganizationVO organization = new OrganizationVO();
                    organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
                    organization.setName(affiliation);
                    organization.setAddress(affiliationAddress);
                    personVO.getOrganizations().add(organization);
                  }
                  CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.EDITOR);
                  if ((bibGenre == BibTexUtil.Genre.article || bibGenre == BibTexUtil.Genre.inbook
                      || bibGenre == BibTexUtil.Genre.inproceedings || bibGenre == BibTexUtil.Genre.conference
                      || bibGenre == BibTexUtil.Genre.incollection) && (sourceVO.getTitle() != null || sourceVO.getTitle() == null)) {
                    sourceVO.getCreators().add(creatorVO);
                  } else {
                    mds.getCreators().add(creatorVO);
                  }
                }
              }
              if (!teams.isEmpty()) {
                mds.getCreators().addAll(teams);
              }
            } catch (Exception e) {
              logger.error("An error occured while getting field 'editor'.", e);
              throw new RuntimeException(e);
            }
          }
        }
        // No CoNE Author or Editor Found
        if (noConeAuthorFound == true && noConeEditorFound == true && configuration != null && "true".equals(configuration.get("CoNE"))) {
          throw new RuntimeException("No CoNE-Author and no CoNE-Editor was found");
        }
        // If no affiliation is given, set the first author to "external"
        boolean affiliationFound = false;
        for (CreatorVO creator : mds.getCreators()) {
          if (creator.getPerson() != null && creator.getPerson().getOrganizations() != null) {
            for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
              if (organization.getIdentifier() != null) {
                affiliationFound = true;
                break;
              }
            }
          }
        }
        if (!affiliationFound && mds.getCreators().size() > 0) {
          OrganizationVO externalOrganization = new OrganizationVO();
          externalOrganization.setName("External Organizations");
          try {
            externalOrganization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
          } catch (Exception e) {
            throw new RuntimeException("Property inge.pubman.external.organisation.id not found", e);
          }
          if (mds.getCreators().get(0).getPerson() != null) {
            mds.getCreators().get(0).getPerson().getOrganizations().add(externalOrganization);
          }
        }
        // Mapping of "common" (maybe relevant), non standard BibTeX Entries
        // abstract
        if (fields.get("abstract") != null) {
          mds.getAbstracts().add(new AbstractVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("abstract").toString()), false)));
        }
        // contents
        if (fields.get("contents") != null) {
          mds.setTableOfContents(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("contents").toString()), false));
        }
        // isbn
        if (fields.get("isbn") != null) {
          if (bibGenre == BibTexUtil.Genre.inproceedings || bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.incollection
              || bibGenre == BibTexUtil.Genre.conference) {
            if (sourceVO != null) {
              sourceVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISBN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("isbn").toString()), false)));
            }
          } else {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISBN,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("isbn").toString()), false)));
          }
        }
        // issn
        if (fields.get("issn") != null) {
          if (bibGenre == BibTexUtil.Genre.inproceedings || bibGenre == BibTexUtil.Genre.inbook || bibGenre == BibTexUtil.Genre.incollection
              || bibGenre == BibTexUtil.Genre.conference) {
            if (sourceVO.getSources() != null && !sourceVO.getSources().isEmpty()) {
              sourceVO.getSources().get(0).getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
            }
          } else if (bibGenre == BibTexUtil.Genre.article) {
            if (sourceVO != null) {
              sourceVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
            }
          } else {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
          }
        }
        // keywords
        if (fields.get("keywords") != null) {
          mds.setFreeKeywords(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("keywords").toString()), false));
        }
        // language
        /*
         * if (fields.get("language") != null) {
         * mds.getLanguages().add(BibTexUtil.stripBraces(BibTexUtil
         * .bibtexDecode(fields.get("language").toString ()), false)); }
         */
        // subtitle
        if (fields.get("subtitle") != null) {
          mds.getAlternativeTitles()
              .add(new AlternativeTitleVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("subtitle").toString()), false)));
        }
        // url is now mapped to locator
        if (fields.get("url") != null) {
          // mds.getIdentifiers().add(
          // new IdentifierVO(
          // IdentifierVO.IdType.URI,
          // BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("url").toString()), false)));
          FileVO locator = new FileVO();
          locator.setContent(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("url").toString()), false));
          locator.setName("Link");
          locator.setStorage(FileVO.Storage.EXTERNAL_URL);
          locator.setVisibility(FileVO.Visibility.PUBLIC);
          locator.setContentCategory("any-fulltext");
          MdsFileVO metadata = new MdsFileVO();
          metadata.setContentCategory("any-fulltext");
          metadata.setTitle("Link");
          locator.getMetadataSets().add(metadata);
          itemVO.getFiles().add(locator);
        }
        // web_url as URI-Identifier
        else if (fields.get("web_url") != null) {
          mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI,
              BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("web_url").toString()), false)));
        }
        // Prevent the creation of an empty source
        if (sourceVO.getTitle() != null && sourceVO.getTitle() != null && sourceVO.getTitle() != "" && sourceVO.getGenre() != null) {
          mds.getSources().add(sourceVO);
          // Prevent the creation of an empty second
          if (sourceVO.getSources() != null && !sourceVO.getSources().isEmpty() && sourceVO.getSources().get(0) != null
              && sourceVO.getSources().get(0).getTitle() != null && sourceVO.getSources().get(0).getTitle() != null
              && sourceVO.getSources().get(0).getTitle() != "") {
            mds.getSources().add(sourceVO.getSources().get(0));
          }
        }
        // Prevent the creation of an empty second source
        if (secondSourceVO.getTitle() != null && secondSourceVO.getTitle() != null && secondSourceVO.getTitle() != ""
            && secondSourceVO.getGenre() != null) {
          mds.getSources().add(secondSourceVO);
          // Prevent the creation of an empty second
          if (secondSourceVO.getSources() != null && !secondSourceVO.getSources().isEmpty() && secondSourceVO.getSources().get(0) != null
              && secondSourceVO.getSources().get(0).getTitle() != null && secondSourceVO.getSources().get(0).getTitle() != null
              && secondSourceVO.getSources().get(0).getTitle() != "") {
            mds.getSources().add(secondSourceVO.getSources().get(0));
          }
        }
        // New mapping for MPIS
        // DOI
        if (fields.get("doi") != null) {
          mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.DOI,
              BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("doi").toString()), false)));
        }
        // eid
        if (fields.get("eid") != null) {
          if (mds.getSources().size() == 1) {
            mds.getSources().get(0).setSequenceNumber(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("eid").toString()), false));
          }
        }
        // rev
        if (fields.get("rev") != null) {
          if ("Peer".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("rev").toString()), false))) {
            mds.setReviewMethod(ReviewMethod.PEER);
          } else if ("No review".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("rev").toString()), false))) {
            mds.setReviewMethod(ReviewMethod.NO_REVIEW);
          }
        }
        // MPG-Affil
        if (fields.get("MPG-Affil") != null) {
          if ("Peer".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("MPG-Affil").toString()), false))) {
            // TODO
          }
        }
        // MPIS Groups
        if (fields.get("group") != null) {
          String[] groups = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("group").toString()), false).split(",");
          for (String group : groups) {
            group = group.trim();
            if (!"".equals(group)) {
              if (groupSet == null) {
                try {
                  groupSet = loadGroupSet();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }
              if (!groupSet.contains(group)) {
                throw new RuntimeException("Group '" + group + "' not found.");
              }
              mds.getSubjects().add(new SubjectVO(group, null, SubjectClassification.MPIS_GROUPS.toString()));
            }
          }
        }
        // MPIS Projects
        if (fields.get("project") != null) {
          String[] projects = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("project").toString()), false).split(",");
          for (String project : projects) {
            project = project.trim();
            if (!"".equals(project)) {
              if (projectSet == null) {
                try {
                  projectSet = loadProjectSet();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }
              if (!projectSet.contains(project)) {
                throw new RuntimeException("Project '" + project + "' not found.");
              }
              mds.getSubjects().add(new SubjectVO(project, null, SubjectClassification.MPIS_PROJECTS.toString()));
            }
          }
        }
        // Cite Key
        mds.getIdentifiers().add(new IdentifierVO(IdType.BIBTEX_CITEKEY, entry.getEntryKey()));
      } else if (object instanceof BibtexToplevelComment) {
        logger.debug("Comment found: " + ((BibtexToplevelComment) object).getContent());
      }
    }
    try {
      if (entryFound) {
        return XmlTransformingService.transformToItem(itemVO);
      } else {
        logger.warn("No entry found in BibTex record.");
        throw new RuntimeException();
      }
    } catch (TechnicalException e) {
      logger.error("An error ocurred while transforming the item.");
      throw new RuntimeException(e);
    }
  }

  private void addCreator(MdsPublicationVO publicationVO, BibtexPerson person, CreatorVO.CreatorRole role, String affiliation,
      String affiliationAddress) throws RuntimeException {
    PersonVO personVO = new PersonVO();
    personVO.setFamilyName(BibTexUtil.bibtexDecode(person.getLast() + (person.getLineage() != null ? " " + person.getLineage() : "")
        + (person.getPreLast() != null ? ", " + person.getPreLast() : "")));
    personVO.setGivenName(BibTexUtil.bibtexDecode(person.getFirst()));
    if (configuration != null && "true".equals(configuration.get("CoNE"))) {
      String query = personVO.getFamilyName() + " " + personVO.getGivenName() + " "
          + (configuration.get("OrganizationalUnit") != null ? configuration.get("OrganizationalUnit") : "");
      List<String> coneEntries = Util.queryConeForJava("persons", query);
      if (coneEntries.size() == 1) {
        personVO.setIdentifier(new IdentifierVO(IdType.CONE, coneEntries.get(0)));
      } else {
        throw new RuntimeException("Ambigous CoNE entry for " + query + ": " + coneEntries);
      }
    }
    if (affiliation != null || affiliationAddress != null) {
      OrganizationVO organization = new OrganizationVO();
      organization.setName(affiliation);
      organization.setAddress(affiliationAddress);
      try {
        organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANISATION_ID));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      personVO.getOrganizations().add(organization);
    }
    CreatorVO creatorVO = new CreatorVO(personVO, role);
    publicationVO.getCreators().add(creatorVO);
  }

  /**
   * Checks if date1 is before date2.
   * 
   * @param date1 A date in one of the formats "YYYY", "YYYY-MM" or "YYYY-MM-DD"
   * @param date2 A date in one of the formats "YYYY", "YYYY-MM" or "YYYY-MM-DD"
   * @return Returns true if date1 is before date2
   */
  private boolean smaller(String date1, String date2) {
    if (date1 == null || "".equals(date1) || date2 == null || "".equals(date2)) {
      return true;
    }
    date1 = (date1 + "-01-01").substring(0, 10);
    date2 = (date2 + "-ZZ-ZZ").substring(0, 10);
    return date1.compareTo(date2) <= 0;
  }

  /**
   * Get group classification from CoNE.
   * 
   * @return A set containing MPIS groups.
   * @throws Exception
   */
  public static Set<String> loadGroupSet() throws Exception {
    HttpClient httpClient = new HttpClient();
    GetMethod getMethod = new GetMethod(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + "mpis-groups/all?f=options");
    httpClient.executeMethod(getMethod);
    InputStream inputStream = getMethod.getResponseBodyAsStream();
    String line;
    Set<String> result = new HashSet<String>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    while ((line = bufferedReader.readLine()) != null) {
      // result.add(line.replaceAll("(\\d|_)+\\|", ""));
      try {
        result.add(line.substring(line.indexOf("|") + 1, line.length()));
      } catch (IndexOutOfBoundsException e) {
      }
    }
    inputStream.close();
    return result;
  }

  /**
   * Get project classification from CoNE.
   * 
   * @return A set containing MPIS projects.
   * @throws Exception
   */
  public static Set<String> loadProjectSet() throws Exception {
    HttpClient httpClient = new HttpClient();
    GetMethod getMethod = new GetMethod(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + "mpis-projects/all?f=options");
    httpClient.executeMethod(getMethod);
    InputStream inputStream = getMethod.getResponseBodyAsStream();
    String line;
    Set<String> result = new HashSet<String>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    while ((line = bufferedReader.readLine()) != null) {
      try {
        result.add(line.substring(line.indexOf("|") + 1, line.length()));
      } catch (IndexOutOfBoundsException e) {
      }
      // result.add(line.replaceAll("(\\d|_)+\\|", ""));
    }
    inputStream.close();
    return result;
  }
}
