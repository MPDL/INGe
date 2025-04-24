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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
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
  private static final Logger logger = LogManager.getLogger(Bibtex.class);
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
    StringBuilder sb = new StringBuilder();
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
    if (null == entries || entries.isEmpty()) {
      logger.warn("No entry found in BibTex record.");
      throw new RuntimeException();
    }
    for (Object object : entries) {
      if (object instanceof BibtexEntry entry) {
        if (entryFound) {
          logger.error("Multiple entries in BibTex record.");
          throw new RuntimeException();
        }
        entryFound = true;
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
        if (null != fields.get("title")) {
          if (null != fields.get("chapter")) {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("chapter").toString()), false) + " - "
                + BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("title").toString()), false));
          } else {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("title").toString()), false));
          }
        }
        // booktitle
        if (null != fields.get("booktitle")) {
          if (BibTexUtil.Genre.book == bibGenre) {
            mds.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("booktitle").toString()), false));
          } else if (BibTexUtil.Genre.conference == bibGenre || BibTexUtil.Genre.inbook == bibGenre
              || BibTexUtil.Genre.incollection == bibGenre || BibTexUtil.Genre.inproceedings == bibGenre) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("booktitle").toString()), false));
            if (BibTexUtil.Genre.conference == bibGenre || BibTexUtil.Genre.inproceedings == bibGenre) {
              sourceVO.setGenre(SourceVO.Genre.PROCEEDINGS);
            } else if (BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.incollection == bibGenre) {
              sourceVO.setGenre(SourceVO.Genre.BOOK);
            }
          }
        }
        // fjournal, journal
        if (null != fields.get("fjournal")) {
          if (BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.misc == bibGenre || BibTexUtil.Genre.unpublished == bibGenre) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("fjournal").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.JOURNAL);
            if (null != fields.get("journal")) {
              sourceVO.getAlternativeTitles()
                  .add(new AlternativeTitleVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("journal").toString()), false)));
            }
          }
        } else if (null != fields.get("journal")) {
          if (BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.misc == bibGenre || BibTexUtil.Genre.unpublished == bibGenre
              || BibTexUtil.Genre.inproceedings == bibGenre) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("journal").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.JOURNAL);
          }
        }
        // number
        if (null != fields.get("number") && BibTexUtil.Genre.techreport != bibGenre) {
          sourceVO.setIssue(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("number").toString()), false));
        } else if (null != fields.get("number") && BibTexUtil.Genre.techreport == bibGenre) {
          {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.REPORT_NR,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("number").toString()), false)));
          }
        }
        // pages
        if (null != fields.get("pages")) {
          if (BibTexUtil.Genre.book == bibGenre || BibTexUtil.Genre.proceedings == bibGenre) {
            mds.setTotalNumberOfPages(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("pages").toString()), false));
          } else {
            BibTexUtil.fillSourcePages(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("pages").toString()), false), sourceVO);
            if (BibTexUtil.Genre.inproceedings == bibGenre && (null == fields.get("booktitle") || "".equals(
                fields.get("booktitle").toString()))
                && (null != fields.get("event_name") && !"".equals(fields.get("event_name").toString()))) {
              sourceVO.setTitle(BibTexUtil.stripBraces(fields.get("event_name").toString(), false));
              sourceVO.setGenre(SourceVO.Genre.PROCEEDINGS);
            }
          }
        }
        // Publishing info
        PublishingInfoVO publishingInfoVO = new PublishingInfoVO();
        mds.setPublishingInfo(publishingInfoVO);
        // address
        if (null != fields.get("address")) {
          if (!(BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.inproceedings == bibGenre
              || BibTexUtil.Genre.conference == bibGenre || BibTexUtil.Genre.incollection == bibGenre)
              && null == sourceVO.getTitle()) {
            publishingInfoVO.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("address").toString()), false));
          } else {
            if (null == sourceVO.getPublishingInfo()) {
              PublishingInfoVO sourcePublishingInfoVO = new PublishingInfoVO();
              sourceVO.setPublishingInfo(sourcePublishingInfoVO);
            }
            sourceVO.getPublishingInfo().setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("address").toString()), false));
          }
        }
        // edition
        if (null != fields.get("edition")) {
          publishingInfoVO.setEdition(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("edition").toString()), false));
        }
        // publisher
        if (!(BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.inproceedings == bibGenre
            || BibTexUtil.Genre.conference == bibGenre || BibTexUtil.Genre.incollection == bibGenre)
            && null == sourceVO.getTitle()) {
          if (null != fields.get("publisher")) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("publisher").toString()), false));
          } else if (null != fields.get("school") && (BibTexUtil.Genre.mastersthesis == bibGenre || BibTexUtil.Genre.phdthesis == bibGenre
              || BibTexUtil.Genre.techreport == bibGenre)) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("school").toString()), false));
          } else if (null != fields.get("institution")) {
            publishingInfoVO.setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("institution").toString()), false));
          } else if (null == fields.get("publisher") && null == fields.get("school") && null == fields.get("institution")
              && null != fields.get("address")) {
            publishingInfoVO.setPublisher("ANY PUBLISHER");
          }
        } else {
          if (null == sourceVO.getPublishingInfo()) {
            PublishingInfoVO sourcePublishingInfoVO = new PublishingInfoVO();
            sourceVO.setPublishingInfo(sourcePublishingInfoVO);
          }
          if (null != fields.get("publisher")) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("publisher").toString()), false));
          } else if (null != fields.get("school") && (BibTexUtil.Genre.mastersthesis == bibGenre || BibTexUtil.Genre.phdthesis == bibGenre
              || BibTexUtil.Genre.techreport == bibGenre)) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("school").toString()), false));
          } else if (null != fields.get("institution")) {
            sourceVO.getPublishingInfo()
                .setPublisher(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("institution").toString()), false));
          } else if (null == fields.get("publisher") && null == fields.get("school") && null == fields.get("institution")
              && null != fields.get("address")) {
            sourceVO.getPublishingInfo().setPublisher("ANY PUBLISHER");
          }
        }
        // series
        if (null != fields.get("series")) {
          if (BibTexUtil.Genre.book == bibGenre || BibTexUtil.Genre.misc == bibGenre || BibTexUtil.Genre.techreport == bibGenre) {
            sourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("series").toString()), false));
            sourceVO.setGenre(SourceVO.Genre.SERIES);
          } else if (BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.incollection == bibGenre
              || BibTexUtil.Genre.inproceedings == bibGenre || BibTexUtil.Genre.conference == bibGenre) {
            secondSourceVO.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("series").toString()), false));
            secondSourceVO.setGenre(SourceVO.Genre.SERIES);
          }
        }
        // type --> degree
        if (null != fields.get("type") && BibTexUtil.Genre.mastersthesis == bibGenre) {
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
        } else if (null != fields.get("type") && BibTexUtil.Genre.phdthesis == bibGenre) {
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
        if (null != fields.get("volume")) {
          if (BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.book == bibGenre) {
            sourceVO.setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
          } else if (BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.inproceedings == bibGenre
              || BibTexUtil.Genre.incollection == bibGenre || BibTexUtil.Genre.conference == bibGenre) {
            if (null != sourceVO.getSources() && !sourceVO.getSources().isEmpty()) {
              sourceVO.getSources().get(0)
                  .setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
            } else {
              sourceVO.setVolume(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("volume").toString()), false));
            }
          }
        }
        // event infos
        if (null != bibGenre && (bibGenre.equals(BibTexUtil.Genre.inproceedings) || bibGenre.equals(BibTexUtil.Genre.proceedings)
            || bibGenre.equals(BibTexUtil.Genre.conference) || bibGenre.equals(BibTexUtil.Genre.poster)
            || bibGenre.equals(BibTexUtil.Genre.talk))) {
          EventVO event = new EventVO();
          boolean eventNotEmpty = false;
          // event location
          if (null != fields.get("location")) {
            event.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("location").toString()), false));
            eventNotEmpty = true;
          }
          // event place
          else if (null != fields.get("event_place")) {
            event.setPlace(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("event_place").toString()), false));
            eventNotEmpty = true;
          }
          // event name/title
          if (null != fields.get("event_name")) {
            event.setTitle(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("event_name").toString()), false));
            eventNotEmpty = true;
          }
          // event will be set only it's not empty
          if (eventNotEmpty) {
            if (null == event.getTitle()) {
              event.setTitle("");
            }
            mds.setEvent(event);
          }
        }
        // year, month
        String dateString = null;
        if (null != fields.get("year")) {
          dateString = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("year").toString()), false);
          if (null != fields.get("month")) {
            String month = BibTexUtil.parseMonth(fields.get("month").toString());
            dateString += "-" + month;
          }
          if (BibTexUtil.Genre.unpublished == bibGenre) {
            mds.setDateCreated(dateString);
          } else {
            mds.setDatePublishedInPrint(dateString);
          }
        }
        String affiliation = null;
        String affiliationAddress = null;
        // affiliation
        if (null != fields.get("affiliation")) {
          affiliation = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("affiliation").toString()), false);
        }
        // affiliationaddress
        if (null != fields.get("affiliationaddress")) {
          affiliationAddress = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("affiliationaddress").toString()), false);
        }
        // author
        boolean noConeAuthorFound = true;
        if (null != fields.get("author")) {
          if (fields.get("author") instanceof BibtexPersonList authors) {
            for (Object author : authors.getList()) {
              if (author instanceof BibtexPerson) {
                addCreator(mds, (BibtexPerson) author, CreatorVO.CreatorRole.AUTHOR, affiliation, affiliationAddress);
              } else {
                logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + author + "] in [" + author + "]");
              }
            }
          } else if (fields.get("author") instanceof BibtexPerson author) {
            addCreator(mds, author, CreatorVO.CreatorRole.AUTHOR, affiliation, affiliationAddress);
          } else if (fields.get("author") instanceof BibtexString) {
            AuthorDecoder decoder;
            try {
              String authorString = BibTexUtil.bibtexDecode(fields.get("author").toString(), false);
              List<CreatorVO> teams = new ArrayList<>();
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
              if (null != decoder.getBestFormat()) {
                List<Author> authors = decoder.getAuthorListList().get(0);
                for (Author author : authors) {
                  PersonVO personVO = new PersonVO();
                  personVO.setFamilyName(author.getSurname());
                  if (null != author.getGivenName()) {
                    personVO.setGivenName(author.getGivenName());
                  } else {
                    personVO.setGivenName(author.getInitial());
                  }
                  /*
                   * Case for MPI-KYB (Biological Cybernetics) with CoNE identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != author.getTags().get("identifier"))) {
                    String query = author.getTags().get("identifier");
                    int affiliationsCount = Integer.parseInt(author.getTags().get("affiliationsCount"));
                    if (0 < affiliationsCount || null != this.configuration.get("OrganizationalUnit")) {
                      for (int ouCount = 0; ouCount < (0 < affiliationsCount ? affiliationsCount : 1); ouCount++) // 1
                                                                                                                  // is
                                                                                                                  // for
                                                                                                                  // the
                                                                                                                  // case
                                                                                                                  // configuration.get("OrganizationalUnit")
                                                                                                                  // !=
                                                                                                                  // null
                      {
                        String organizationalUnit =
                            (null != author.getTags().get("affiliation" + ouCount) ? author.getTags().get("affiliation" + ouCount)
                                : (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                        Node coneEntries = null;
                        if (query.equals(author.getTags().get("identifier"))) {
                          coneEntries = Util.queryConeExactWithIdentifier("persons", query, organizationalUnit);
                          // for MPIKYB due to OUs which do not occur in CoNE
                          if (null == coneEntries.getFirstChild().getFirstChild()) {
                            logger.error("No Person with Identifier (" + author.getTags().get("identifier") + ") and OU ("
                                + organizationalUnit + ") found in CoNE for Publication \"" + fields.get("title") + "\"");
                          }
                        } else {
                          coneEntries = Util.queryConeExact("persons", query, organizationalUnit);
                        }
                        Node coneNode = coneEntries.getFirstChild().getFirstChild();
                        if (null != coneNode) {
                          Node currentNode = coneNode.getFirstChild();
                          boolean first = true;
                          while (null != currentNode) {
                            if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                              first = false;
                              noConeAuthorFound = false;
                              Node coneEntry = currentNode;
                              String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                              personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                              for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                                Node posNode = coneEntry.getChildNodes().item(i);
                                if ("escidoc:position".equals(posNode.getNodeName())) {
                                  String from = null;
                                  String until = null;
                                  String name = null;
                                  String id = null;
                                  Node node = posNode.getFirstChild().getFirstChild();
                                  while (null != node) {
                                    switch (node.getNodeName()) {
                                      case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                      case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                      case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                      case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                            } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
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
                  else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("affiliation id in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != author.getTags().get("identifier"))) {
                    String identifier = author.getTags().get("identifier");
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    if (!("extern".equals(identifier))) {
                      Node coneEntries = null;
                      coneEntries = Util.queryConeExact("persons", query,
                          (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                      Node coneNode = coneEntries.getFirstChild().getFirstChild();
                      if (null != coneNode) {
                        Node currentNode = coneNode.getFirstChild();
                        boolean first = true;
                        while (null != currentNode) {
                          if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                            first = false;
                            noConeAuthorFound = false;
                            Node coneEntry = currentNode;
                            String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                            personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                            if (null != identifier && !("".equals(identifier))) {
                              try {
                                String ouSubTitle = identifier.substring(0, identifier.indexOf(","));
                                Document document = Util.queryFramework("/oum/organizational-units?query="
                                    + URLEncoder.encode("\"/title\"=\"" + ouSubTitle + "\"", StandardCharsets.UTF_8));
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
                          } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                            throw new RuntimeException("Ambigous CoNE entries for " + query);
                          }
                          currentNode = currentNode.getNextSibling();
                        }
                      } else {
                        throw new RuntimeException("Missing CoNE entry for " + query);
                      }
                    }
                  } else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("empty brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors"))
                          && (null != author.getTags().get("brackets")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (null != coneNode) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (null != currentNode) {
                        if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                          first = false;
                          noConeAuthorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (null != node) {
                                switch (node.getNodeName()) {
                                  case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                  case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                  case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                  case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                        } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    } else {
                      throw new RuntimeException("Missing CoNE entry for " + query);
                    }
                  } else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("no".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (null != coneNode) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (null != currentNode) {
                        if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                          first = false;
                          noConeAuthorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (null != node) {
                                switch (node.getNodeName()) {
                                  case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                  case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                  case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                  case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                        } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
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
                  if (null != this.configuration && "false".equals(this.configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != author.getTags().get("identifier"))) {
                    String identifier = author.getTags().get("identifier");
                    String authoAffiliation = author.getTags().get("affiliation0");
                    OrganizationVO org = new OrganizationVO();
                    org.setName(authoAffiliation);
                    org.setIdentifier(identifier);
                    personVO.getOrganizations().add(org);
                  }
                  if (null != affiliation) {
                    OrganizationVO organization = new OrganizationVO();
                    organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANIZATION_ID));
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
        if (null != fields.get("editor")) {
          logger.debug("fields.get(\"editor\"): " + fields.get("editor").getClass());
          if (fields.get("editor") instanceof BibtexPersonList editors) {
            for (Object editor : editors.getList()) {
              if (editor instanceof BibtexPerson) {
                addCreator(mds, (BibtexPerson) editor, CreatorVO.CreatorRole.EDITOR, affiliation, affiliationAddress);
              } else {
                logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + editor + "] in [" + editors + "]");
              }
            }
          } else if (fields.get("editor") instanceof BibtexPerson editor) {
            addCreator(mds, editor, CreatorVO.CreatorRole.EDITOR, affiliation, affiliationAddress);
          } else if (fields.get("editor") instanceof BibtexString) {
            AuthorDecoder decoder;
            try {
              String editorString = BibTexUtil.bibtexDecode(fields.get("editor").toString(), false);
              List<CreatorVO> teams = new ArrayList<>();
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
              if (null != decoder.getBestFormat()) {
                List<Author> editors = decoder.getAuthorListList().get(0);
                for (Author editor : editors) {
                  PersonVO personVO = new PersonVO();
                  personVO.setFamilyName(editor.getSurname());
                  if (null != editor.getGivenName()) {
                    personVO.setGivenName(editor.getGivenName());
                  } else {
                    personVO.setGivenName(editor.getInitial());
                  }
                  /*
                   * Case for MPI-KYB (Biological Cybernetics) with CoNE identifier in brackets and
                   * affiliations to adopt from CoNE for each author (also in brackets)
                   */
                  if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != editor.getTags().get("identifier"))) {
                    String query = editor.getTags().get("identifier");
                    int affiliationsCount = Integer.parseInt(editor.getTags().get("affiliationsCount"));
                    if (0 < affiliationsCount || null != this.configuration.get("OrganizationalUnit")) {
                      for (int ouCount = 0; ouCount < (0 < affiliationsCount ? affiliationsCount : 1); ouCount++) // 1
                                                                                                                  // is
                                                                                                                  // for
                                                                                                                  // the
                                                                                                                  // case
                                                                                                                  // configuration.get("OrganizationalUnit")
                                                                                                                  // !=
                                                                                                                  // null
                      {
                        String organizationalUnit =
                            (null != editor.getTags().get("affiliation" + ouCount) ? editor.getTags().get("affiliation" + ouCount)
                                : (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                        Node coneEntries = null;
                        if (query.equals(editor.getTags().get("identifier"))) {
                          coneEntries = Util.queryConeExactWithIdentifier("persons", query, organizationalUnit);
                          // for MPIKYB due to OUs which do not occur in CoNE
                          if (null == coneEntries.getFirstChild().getFirstChild()) {
                            logger.error("No Person with Identifier (" + editor.getTags().get("identifier") + ") and OU ("
                                + organizationalUnit + ") found in CoNE for Publication \"" + fields.get("title") + "\"");
                          }
                        } else {
                          coneEntries = Util.queryConeExact("persons", query, organizationalUnit);
                        }
                        Node coneNode = coneEntries.getFirstChild().getFirstChild();
                        if (null != coneNode) {
                          Node currentNode = coneNode.getFirstChild();
                          boolean first = true;
                          while (null != currentNode) {
                            if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                              first = false;
                              noConeEditorFound = false;
                              Node coneEntry = currentNode;
                              String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                              personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                              for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                                Node posNode = coneEntry.getChildNodes().item(i);
                                if ("escidoc:position".equals(posNode.getNodeName())) {
                                  String from = null;
                                  String until = null;
                                  String name = null;
                                  String id = null;
                                  Node node = posNode.getFirstChild().getFirstChild();
                                  while (null != node) {
                                    switch (node.getNodeName()) {
                                      case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                      case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                      case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                      case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                            } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
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
                  else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("affiliation id in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != editor.getTags().get("identifier"))) {
                    String identifier = editor.getTags().get("identifier");
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    if (!("extern".equals(identifier))) {
                      Node coneEntries = null;
                      coneEntries = Util.queryConeExact("persons", query,
                          (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                      Node coneNode = coneEntries.getFirstChild().getFirstChild();
                      if (null != coneNode) {
                        Node currentNode = coneNode.getFirstChild();
                        boolean first = true;
                        while (null != currentNode) {
                          if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                            first = false;
                            noConeAuthorFound = false;
                            Node coneEntry = currentNode;
                            String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                            personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                            if (null != identifier && !("".equals(identifier))) {
                              try {
                                String ouSubTitle = identifier.substring(0, identifier.indexOf(","));
                                Document document = Util.queryFramework("/oum/organizational-units?query="
                                    + URLEncoder.encode("\"/title\"=\"" + ouSubTitle + "\"", StandardCharsets.UTF_8));
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
                          } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                            throw new RuntimeException("Ambigous CoNE entries for " + query);
                          }
                          currentNode = currentNode.getNextSibling();
                        }
                      } else {
                        throw new RuntimeException("Missing CoNE entry for " + query);
                      }
                    }
                  } else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("empty brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors"))
                          && (null != editor.getTags().get("brackets")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (null != coneNode) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (null != currentNode) {
                        if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                          first = false;
                          noConeEditorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (null != node) {
                                switch (node.getNodeName()) {
                                  case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                  case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                  case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                  case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                        } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
                          throw new RuntimeException("Ambigous CoNE entries for " + query);
                        }
                        currentNode = currentNode.getNextSibling();
                      }
                    } else {
                      throw new RuntimeException("Missing CoNE entry for " + query);
                    }
                  } else if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))
                      && ("no".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))) {
                    String query = personVO.getFamilyName() + ", " + personVO.getGivenName();
                    Node coneEntries = Util.queryConeExact("persons", query,
                        (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : ""));
                    Node coneNode = coneEntries.getFirstChild().getFirstChild();
                    if (null != coneNode) {
                      Node currentNode = coneNode.getFirstChild();
                      boolean first = true;
                      while (null != currentNode) {
                        if (Node.ELEMENT_NODE == currentNode.getNodeType() && first) {
                          first = false;
                          noConeEditorFound = false;
                          Node coneEntry = currentNode;
                          String coneId = coneEntry.getAttributes().getNamedItem("rdf:about").getNodeValue();
                          personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneId));
                          for (int i = 0; i < coneEntry.getChildNodes().getLength(); i++) {
                            Node posNode = coneEntry.getChildNodes().item(i);
                            if ("escidoc:position".equals(posNode.getNodeName())) {
                              String from = null;
                              String until = null;
                              String name = null;
                              String id = null;
                              Node node = posNode.getFirstChild().getFirstChild();
                              while (null != node) {
                                switch (node.getNodeName()) {
                                  case "eprints:affiliatedInstitution" -> name = node.getFirstChild().getNodeValue();
                                  case "escidoc:start-date" -> from = node.getFirstChild().getNodeValue();
                                  case "escidoc:end-date" -> until = node.getFirstChild().getNodeValue();
                                  case "dc:identifier" -> id = node.getFirstChild().getNodeValue();
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
                        } else if (Node.ELEMENT_NODE == currentNode.getNodeType()) {
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
                  if (null != this.configuration && "false".equals(this.configuration.get("CoNE"))
                      && ("identifier and affiliation in brackets".equals(this.configuration.get("CurlyBracketsForCoNEAuthors")))
                      && (null != editor.getTags().get("identifier"))) {
                    String identifier = editor.getTags().get("identifier");
                    String authoAffiliation = editor.getTags().get("affiliation0");
                    OrganizationVO org = new OrganizationVO();
                    org.setName(authoAffiliation);
                    org.setIdentifier(identifier);
                    personVO.getOrganizations().add(org);
                  }
                  if (null != affiliation) {
                    OrganizationVO organization = new OrganizationVO();
                    organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANIZATION_ID));
                    organization.setName(affiliation);
                    organization.setAddress(affiliationAddress);
                    personVO.getOrganizations().add(organization);
                  }
                  CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.EDITOR);
                  if ((BibTexUtil.Genre.article == bibGenre || BibTexUtil.Genre.inbook == bibGenre
                      || BibTexUtil.Genre.inproceedings == bibGenre || BibTexUtil.Genre.conference == bibGenre
                      || BibTexUtil.Genre.incollection == bibGenre) && (null != sourceVO.getTitle() || null == sourceVO.getTitle())) {
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
        if (noConeAuthorFound && noConeEditorFound && null != this.configuration && "true".equals(this.configuration.get("CoNE"))) {
          throw new RuntimeException("No CoNE-Author and no CoNE-Editor was found");
        }
        // If no affiliation is given, set the first author to "external"
        boolean affiliationFound = false;
        for (CreatorVO creator : mds.getCreators()) {
          if (null != creator.getPerson() && null != creator.getPerson().getOrganizations()) {
            for (OrganizationVO organization : creator.getPerson().getOrganizations()) {
              if (null != organization.getIdentifier()) {
                affiliationFound = true;
                break;
              }
            }
          }
        }
        if (!affiliationFound && !mds.getCreators().isEmpty()) {
          OrganizationVO externalOrganization = new OrganizationVO();
          externalOrganization.setName("External Organizations");
          try {
            externalOrganization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANIZATION_ID));
          } catch (Exception e) {
            throw new RuntimeException("Property inge.pubman.external.organization.id not found", e);
          }
          if (null != mds.getCreators().get(0).getPerson()) {
            mds.getCreators().get(0).getPerson().getOrganizations().add(externalOrganization);
          }
        }
        // Mapping of "common" (maybe relevant), non standard BibTeX Entries
        // abstract
        if (null != fields.get("abstract")) {
          mds.getAbstracts().add(new AbstractVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("abstract").toString()), false)));
        }
        // contents
        if (null != fields.get("contents")) {
          mds.setTableOfContents(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("contents").toString()), false));
        }
        // isbn
        if (null != fields.get("isbn")) {
          if (BibTexUtil.Genre.inproceedings == bibGenre || BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.incollection == bibGenre
              || BibTexUtil.Genre.conference == bibGenre) {
            if (null != sourceVO) {
              sourceVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISBN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("isbn").toString()), false)));
            }
          } else {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISBN,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("isbn").toString()), false)));
          }
        }
        // issn
        if (null != fields.get("issn")) {
          if (BibTexUtil.Genre.inproceedings == bibGenre || BibTexUtil.Genre.inbook == bibGenre || BibTexUtil.Genre.incollection == bibGenre
              || BibTexUtil.Genre.conference == bibGenre) {
            if (null != sourceVO.getSources() && !sourceVO.getSources().isEmpty()) {
              sourceVO.getSources().get(0).getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
            }
          } else if (BibTexUtil.Genre.article == bibGenre) {
            if (null != sourceVO) {
              sourceVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                  BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
            }
          } else {
            mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN,
                BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("issn").toString()), false)));
          }
        }
        // keywords
        if (null != fields.get("keywords")) {
          mds.setFreeKeywords(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("keywords").toString()), false));
        }
        // language
        /*
         * if (fields.get("language") != null) {
         * mds.getLanguages().add(BibTexUtil.stripBraces(BibTexUtil
         * .bibtexDecode(fields.get("language").toString ()), false)); }
         */
        // subtitle
        if (null != fields.get("subtitle")) {
          mds.getAlternativeTitles()
              .add(new AlternativeTitleVO(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("subtitle").toString()), false)));
        }
        // url is now mapped to locator
        if (null != fields.get("url")) {
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
        else if (null != fields.get("web_url")) {
          mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI,
              BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("web_url").toString()), false)));
        }
        // Prevent the creation of an empty source
        if (null != sourceVO.getTitle() && null != sourceVO.getTitle() && !"".equals(sourceVO.getTitle()) && null != sourceVO.getGenre()) {
          mds.getSources().add(sourceVO);
          // Prevent the creation of an empty second
          if (null != sourceVO.getSources() && !sourceVO.getSources().isEmpty() && null != sourceVO.getSources().get(0)
              && null != sourceVO.getSources().get(0).getTitle() && null != sourceVO.getSources().get(0).getTitle()
              && !"".equals(sourceVO.getSources().get(0).getTitle())) {
            mds.getSources().add(sourceVO.getSources().get(0));
          }
        }
        // Prevent the creation of an empty second source
        if (null != secondSourceVO.getTitle() && null != secondSourceVO.getTitle() && !"".equals(secondSourceVO.getTitle())
            && null != secondSourceVO.getGenre()) {
          mds.getSources().add(secondSourceVO);
          // Prevent the creation of an empty second
          if (null != secondSourceVO.getSources() && !secondSourceVO.getSources().isEmpty() && null != secondSourceVO.getSources().get(0)
              && null != secondSourceVO.getSources().get(0).getTitle() && null != secondSourceVO.getSources().get(0).getTitle()
              && !"".equals(secondSourceVO.getSources().get(0).getTitle())) {
            mds.getSources().add(secondSourceVO.getSources().get(0));
          }
        }
        // New mapping for MPIS
        // DOI
        if (null != fields.get("doi")) {
          mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.DOI,
              BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("doi").toString()), false)));
        }
        // eid
        if (null != fields.get("eid")) {
          if (1 == mds.getSources().size()) {
            mds.getSources().get(0).setSequenceNumber(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("eid").toString()), false));
          }
        }
        // rev
        if (null != fields.get("rev")) {
          if ("Peer".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("rev").toString()), false))) {
            mds.setReviewMethod(MdsPublicationVO.ReviewMethod.PEER);
          } else if ("No review".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("rev").toString()), false))) {
            mds.setReviewMethod(MdsPublicationVO.ReviewMethod.NO_REVIEW);
          }
        }
        // MPG-Affil
        if (null != fields.get("MPG-Affil")) {
          if ("Peer".equals(BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("MPG-Affil").toString()), false))) {
            // TODO
          }
        }
        // MPIS Groups
        if (null != fields.get("group")) {
          String[] groups = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("group").toString()), false).split(",");
          for (String group : groups) {
            group = group.trim();
            if (!"".equals(group)) {
              if (null == this.groupSet) {
                try {
                  this.groupSet = loadGroupSet();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }
              if (!this.groupSet.contains(group)) {
                throw new RuntimeException("Group '" + group + "' not found.");
              }
              mds.getSubjects().add(new SubjectVO(group, null, MdsPublicationVO.SubjectClassification.MPIS_GROUPS.toString()));
            }
          }
        }
        // MPIS Projects
        if (null != fields.get("project")) {
          String[] projects = BibTexUtil.stripBraces(BibTexUtil.bibtexDecode(fields.get("project").toString()), false).split(",");
          for (String project : projects) {
            project = project.trim();
            if (!"".equals(project)) {
              if (null == this.projectSet) {
                try {
                  this.projectSet = loadProjectSet();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              }
              if (!this.projectSet.contains(project)) {
                throw new RuntimeException("Project '" + project + "' not found.");
              }
              mds.getSubjects().add(new SubjectVO(project, null, MdsPublicationVO.SubjectClassification.MPIS_PROJECTS.toString()));
            }
          }
        }
        // Cite Key
        mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.BIBTEX_CITEKEY, entry.getEntryKey()));
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
    personVO.setFamilyName(BibTexUtil.bibtexDecode(person.getLast() + (null != person.getLineage() ? " " + person.getLineage() : "")
        + (null != person.getPreLast() ? ", " + person.getPreLast() : "")));
    personVO.setGivenName(BibTexUtil.bibtexDecode(person.getFirst()));
    if (null != this.configuration && "true".equals(this.configuration.get("CoNE"))) {
      String query = personVO.getFamilyName() + " " + personVO.getGivenName() + " "
          + (null != this.configuration.get("OrganizationalUnit") ? this.configuration.get("OrganizationalUnit") : "");
      List<String> coneEntries = Util.queryConeForJava("persons", query);
      if (1 == coneEntries.size()) {
        personVO.setIdentifier(new IdentifierVO(IdentifierVO.IdType.CONE, coneEntries.get(0)));
      } else {
        throw new RuntimeException("Ambigous CoNE entry for " + query + ": " + coneEntries);
      }
    }
    if (null != affiliation || null != affiliationAddress) {
      OrganizationVO organization = new OrganizationVO();
      organization.setName(affiliation);
      organization.setAddress(affiliationAddress);
      try {
        organization.setIdentifier(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_EXTERNAL_ORGANIZATION_ID));
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
    if (null == date1 || date1.isEmpty() || null == date2 || date2.isEmpty()) {
      return true;
    }
    date1 = (date1 + "-01-01").substring(0, 10);
    date2 = (date2 + "-ZZ-ZZ").substring(0, 10);
    return 0 >= date1.compareTo(date2);
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
    Set<String> result = new HashSet<>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    while (null != (line = bufferedReader.readLine())) {
      // result.add(line.replaceAll("(\\d|_)+\\|", ""));
      try {
        result.add(line.substring(line.indexOf("|") + 1));
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
    Set<String> result = new HashSet<>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    while (null != (line = bufferedReader.readLine())) {
      try {
        result.add(line.substring(line.indexOf("|") + 1));
      } catch (IndexOutOfBoundsException e) {
      }
      // result.add(line.replaceAll("(\\d|_)+\\|", ""));
    }
    inputStream.close();
    return result;
  }
}
