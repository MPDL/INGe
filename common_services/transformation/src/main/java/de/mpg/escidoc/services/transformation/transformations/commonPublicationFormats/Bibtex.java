/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.dom.BibtexToplevelComment;
import bibtex.parser.BibtexParser;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.BibTexUtil;
import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Implementation of BibTex transformation.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Bibtex
{
    private final Logger logger = Logger.getLogger(Bibtex.class);

    /**
     * Public constructor.
     */
    public Bibtex()
    {       
    }
    
    /**
     * 
     * @param bibtex
     * @return eSciDoc-publication item XML representation of this BibTeX entry
     * @throws RuntimeException
     */
    public String getBibtex(String bibtex) throws RuntimeException
    {
        BibtexParser parser = new BibtexParser(true);
        BibtexFile file = new BibtexFile();
        try
        {
            parser.parse(file, new StringReader(bibtex));
        }
        catch (Exception e)
        {
            this.logger.error("Error parsing BibTex record.");
            throw new RuntimeException();
        }

        PubItemVO itemVO = new PubItemVO();
        MdsPublicationVO mds = new MdsPublicationVO();
        itemVO.setMetadata(mds);

        List entries = file.getEntries();

        boolean entryFound = false;

        if (entries == null || entries.size() == 0)
        {
            this.logger.warn("No entry found in BibTex record.");
            throw new RuntimeException();
        }

        for (Object object : entries)
        {
            if (object instanceof BibtexEntry)
            {
                if (entryFound)
                {
                    this.logger.error("Multiple entries in BibTex record.");
                    throw new RuntimeException();
                }
                entryFound = true;
                BibtexEntry entry = (BibtexEntry) object;

                // genre
                BibTexUtil.Genre bibGenre;
                try
                {
                    bibGenre = BibTexUtil.Genre.valueOf(entry.getEntryType());
                }
                catch (IllegalArgumentException iae)
                {
                    bibGenre = BibTexUtil.Genre.misc;
                    this.logger.warn("Unrecognized genre: " + entry.getEntryType());
                }
                MdsPublicationVO.Genre itemGenre = BibTexUtil.getGenreMapping().get(bibGenre);
                mds.setGenre(itemGenre);
                SourceVO sourceVO = new SourceVO(new TextVO());
                mds.getSources().add(sourceVO);

                Map fields = entry.getFields();

                // Mapping of BibTeX Standard Entries

                // Publishing info
                PublishingInfoVO publishingInfoVO = new PublishingInfoVO();
                mds.setPublishingInfo(publishingInfoVO);
                // address
                if (fields.get("address") != null)
                {
                    publishingInfoVO.setPlace(BibTexUtil.bibtexDecode(fields.get("address").toString()));
                }

                // title
                if (fields.get("title") != null)
                {
                    mds.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("title").toString())));
                }

                // booktitle
                if (fields.get("booktitle") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.book)
                    {
                        mds.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("booktitle").toString())));
                    }
                    else if (bibGenre == BibTexUtil.Genre.conference
                            || bibGenre == BibTexUtil.Genre.inbook
                            || bibGenre == BibTexUtil.Genre.incollection
                            || bibGenre == BibTexUtil.Genre.inproceedings)
                    {
                        sourceVO.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("booktitle").toString())));
                        if (bibGenre == BibTexUtil.Genre.conference
                            || bibGenre == BibTexUtil.Genre.inproceedings)
                        {
                            sourceVO.setGenre(Genre.PROCEEDINGS);
                        }
                        else if (bibGenre == BibTexUtil.Genre.inbook
                            || bibGenre == BibTexUtil.Genre.incollection)
                        {
                            sourceVO.setGenre(Genre.BOOK);
                        }
                    }
                }

                // edition
                if (fields.get("edition") != null)
                {
                    publishingInfoVO.setEdition(BibTexUtil.bibtexDecode(fields.get("edition").toString()));
                }

                // fjournal, journal
                if (fields.get("fjournal") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.article
                            || bibGenre == BibTexUtil.Genre.misc
                            || bibGenre == BibTexUtil.Genre.unpublished)
                    {
                        sourceVO.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("fjournal").toString())));
                        sourceVO.setGenre(SourceVO.Genre.JOURNAL);
                        
                        if (fields.get("journal") != null)
                        {
                            sourceVO.getAlternativeTitles().add(
                                    new TextVO(
                                            BibTexUtil.bibtexDecode(fields.get("journal").toString())));
                        }

                    }
                }
                else if (fields.get("journal") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.article
                            || bibGenre == BibTexUtil.Genre.misc
                            || bibGenre == BibTexUtil.Genre.unpublished)
                    {
                        sourceVO.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("journal").toString())));
                        sourceVO.setGenre(SourceVO.Genre.JOURNAL);
                    }
                }

                // number
                if (fields.get("number") != null)
                {
                    sourceVO.setIssue(BibTexUtil.bibtexDecode(fields.get("number").toString()));
                }

                // pages
                if (fields.get("pages") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.book || bibGenre == BibTexUtil.Genre.proceedings)
                    {
                        mds.setTotalNumberOfPages(BibTexUtil.bibtexDecode(fields.get("pages").toString()));
                    }
                    else
                    {
                        BibTexUtil.fillSourcePages(BibTexUtil.bibtexDecode(fields.get("pages").toString()), sourceVO);
                    }
                }

                // publisher
                if (fields.get("publisher") != null)
                {
                    publishingInfoVO.setPublisher(BibTexUtil.bibtexDecode(fields.get("publisher").toString()));
                }

                // series
                if (fields.get("series") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.book
                            || bibGenre == BibTexUtil.Genre.misc
                            || bibGenre == BibTexUtil.Genre.techreport)
                    {
                        sourceVO.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("series").toString())));
                    }
                    else if (bibGenre == BibTexUtil.Genre.inbook
                            || bibGenre == BibTexUtil.Genre.incollection)
                    {
                        SourceVO sourceOfSource = new SourceVO(
                                new TextVO(BibTexUtil.bibtexDecode(fields.get("series").toString())));
                        sourceVO.getSources().add(sourceOfSource);
                    }
                }

                // volume
                if (fields.get("volume") != null)
                {
                    if (bibGenre == BibTexUtil.Genre.article
                            || bibGenre == BibTexUtil.Genre.book)
                    {
                        sourceVO.setVolume(BibTexUtil.bibtexDecode(fields.get("volume").toString()));
                    }
                    else if (bibGenre == BibTexUtil.Genre.inbook)
                    {
                        sourceVO.getSources().get(0).setVolume(
                                BibTexUtil.bibtexDecode(fields.get("volume").toString()));
                    }
                }

                // year, month
                if (fields.get("year") != null)
                {
                    String dateString = BibTexUtil.bibtexDecode(fields.get("year").toString());
                    if (fields.get("month") != null)
                    {
                        String month = BibTexUtil.parseMonth(fields.get("month").toString());
                        dateString += "-" + month;
                    }

                    if (bibGenre == BibTexUtil.Genre.unpublished)
                    {
                        mds.setDateCreated(dateString);
                    }
                    else
                    {
                        mds.setDatePublishedInPrint(dateString);
                    }
                }

                String affiliation = null;
                String affiliationAddress = null;

                // affiliation
                if (fields.get("affiliation") != null)
                {
                    affiliation = BibTexUtil.bibtexDecode(fields.get("affiliation").toString());
                }

                // affiliationaddress
                if (fields.get("affiliationaddress") != null)
                {
                    affiliationAddress = BibTexUtil.bibtexDecode(fields.get("affiliationaddress").toString());
                }

                // author
                if (fields.get("author") != null)
                {
                    if (fields.get("author") instanceof BibtexPersonList)
                    {
                        BibtexPersonList authors = (BibtexPersonList) fields.get("author");
                        for (Object author : authors.getList())
                        {
                            if (author instanceof BibtexPerson)
                            {
                                addCreator(
                                            mds,
                                            (BibtexPerson) author,
                                            CreatorVO.CreatorRole.AUTHOR,
                                            affiliation,
                                            affiliationAddress);
                            }
                            else
                            {
                                this.logger.warn(
                                        "Entry in BibtexPersonList not a BibtexPerson: ["
                                        + author + "] in [" + author + "]");
                            }
                        }
                    }
                    if (fields.get("author") instanceof BibtexPerson)
                    {
                        BibtexPerson author = (BibtexPerson) fields.get("author");
                        addCreator(
                                    mds,
                                    (BibtexPerson) author,
                                    CreatorVO.CreatorRole.AUTHOR,
                                    affiliation,
                                    affiliationAddress);
                    }
                    else if (fields.get("author") instanceof BibtexString)
                    {
                        AuthorDecoder decoder;
                        try
                        {
                            decoder = new AuthorDecoder(
                                    BibTexUtil.bibtexDecode(fields.get("author").toString()));

                            if (decoder.getBestFormat() != null)
                            {
                                List<Author> authors = decoder.getAuthorListList().get(0);
                                for (Author author : authors)
                                {
                                    PersonVO personVO = new PersonVO();
                                    personVO.setFamilyName(author.getSurname());
                                    
                                    
                                    if (author.getGivenName() != null)
                                    {
                                        personVO.setGivenName(author.getGivenName());
                                    }
                                    else
                                    {
                                        personVO.setGivenName(author.getInitial());
                                    }
                                    if (affiliation != null)
                                    {
                                        OrganizationVO organization = new OrganizationVO();
                                        organization.setIdentifier(
                                                PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
                                        organization.setName(new TextVO(affiliation));
                                        organization.setAddress(affiliationAddress);
                                        personVO.getOrganizations().add(organization);
                                    }
                                    CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.AUTHOR);
                                    mds.getCreators().add(creatorVO);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            this.logger.error("An error ocuured while getting field 'author'.");
                            throw new RuntimeException(e);
                        }
                    }
                }

                // editor
                if (fields.get("editor") != null)
                {

                    this.logger.debug("fields.get(\"editor\"): " + fields.get("editor").getClass());

                    if (fields.get("editor") instanceof BibtexPersonList)
                    {
                        BibtexPersonList editors = (BibtexPersonList) fields.get("editor");
                        for (Object editor : editors.getList())
                        {
                            if (editor instanceof BibtexPerson)
                            {
                                addCreator(
                                        mds,
                                        (BibtexPerson) editor,
                                        CreatorVO.CreatorRole.EDITOR,
                                        affiliation,
                                        affiliationAddress);
                            }
                            else
                            {
                                this.logger.warn(
                                        "Entry in BibtexPersonList not a BibtexPerson: ["
                                        + editor + "] in [" + editors + "]");
                            }
                        }
                    }
                    if (fields.get("editor") instanceof BibtexPerson)
                    {
                        BibtexPerson editor = (BibtexPerson) fields.get("editor");
                        addCreator(
                                mds,
                                (BibtexPerson) editor,
                                CreatorVO.CreatorRole.EDITOR,
                                affiliation,
                                affiliationAddress);
                    }
                    else if (fields.get("editor") instanceof BibtexString)
                    {

                        String editor = BibTexUtil.bibtexDecode(fields.get("editor").toString());
                        
                        try
                        {
                            AuthorDecoder decoder = new AuthorDecoder(editor);
                            if (decoder.getBestFormat() != null)
                            {
                                List<Author> authors = decoder.getAuthorListList().get(0);
                                for (Author author : authors)
                                {
                                    PersonVO personVO = new PersonVO();
                                    personVO.setFamilyName(author.getSurname());
                                    OrganizationVO organization = new OrganizationVO();
                                    personVO.getOrganizations().add(organization);
                                    if (author.getGivenName() != null)
                                    {
                                        personVO.setGivenName(author.getGivenName());
                                    }
                                    else
                                    {
                                        personVO.setGivenName(author.getInitial());
                                    }
                                    if (affiliation != null)
                                    {
                                        organization.setName(new TextVO(affiliation));
                                        organization.setAddress(affiliationAddress);
                                        organization.setIdentifier(
                                                PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
                                    }
                                    CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.EDITOR);
                                    mds.getCreators().add(creatorVO);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            this.logger.error("An error ocuured while getting field 'editor'.");
                            throw new RuntimeException(e);
                        }
                    }
                }

                // If no affiliation is given, set the first author to "external"
                boolean affiliationFound = false;
                for (CreatorVO creator : mds.getCreators())
                {
                    if (creator.getPerson() != null && creator.getPerson().getOrganizations() != null)
                    {
                        for (OrganizationVO organization : creator.getPerson().getOrganizations())
                        {
                            if (organization.getIdentifier() != null)
                            {
                                affiliationFound = true;
                                break;
                            }
                        }
                    }
                }
                
                if (!affiliationFound && mds.getCreators().size() > 0)
                {
                    OrganizationVO externalOrganization = new OrganizationVO();
                    externalOrganization.setName(new TextVO("External Organizations"));
                    try
                    {
                        externalOrganization
                            .setIdentifier(
                                    PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Property escidoc.pubman.external.organisation.id not found", e);
                    }
                    if (mds.getCreators().get(0).getPerson() != null)
                    {
                        mds.getCreators().get(0).getPerson().getOrganizations().add(externalOrganization);
                    }
                }
                
                // Mapping of "common" (maybe relevant), non standard BibTeX Entries

                // abstract
                if (fields.get("abstract") != null)
                {
                    mds.getAbstracts().add(new TextVO(BibTexUtil.bibtexDecode(fields.get("abstract").toString())));
                }

                // contents
                if (fields.get("contents") != null)
                {
                    mds.setTableOfContents(new TextVO(BibTexUtil.bibtexDecode(fields.get("contents").toString())));
                }

                // isbn
                if (fields.get("isbn") != null)
                {
                    mds.getIdentifiers().add(
                            new IdentifierVO(
                                    IdentifierVO.IdType.ISBN,
                                    BibTexUtil.bibtexDecode(fields.get("isbn").toString())));
                }

                // issn
                if (fields.get("issn") != null)
                {
                    mds.getIdentifiers().add(
                            new IdentifierVO(
                                    IdentifierVO.IdType.ISSN,
                                    BibTexUtil.bibtexDecode(fields.get("issn").toString())));
                }

                // keywords
                if (fields.get("keywords") != null)
                {
                    mds.setFreeKeywords(new TextVO(BibTexUtil.bibtexDecode(fields.get("keywords").toString())));
                }

                // language
                if (fields.get("language") != null)
                {
                    mds.getLanguages().add(BibTexUtil.bibtexDecode(fields.get("language").toString()));
                }

                // subtitle
                if (fields.get("subtitle") != null)
                {
                    mds.getAlternativeTitles().add(
                            new TextVO(BibTexUtil.bibtexDecode(fields.get("language").toString())));
                }

                // url is now mapped to locator
                if (fields.get("url") != null)
                {
//                    mds.getIdentifiers().add(
//                            new IdentifierVO(
//                                    IdentifierVO.IdType.URI,
//                                    BibTexUtil.bibtexDecode(fields.get("url").toString())));
                    
                    FileVO locator = new FileVO();
                    locator.setContent(BibTexUtil.bibtexDecode(fields.get("url").toString()));
                    locator.setName("Link");
                    locator.setStorage(FileVO.Storage.EXTERNAL_URL);
                    locator.setVisibility(FileVO.Visibility.PUBLIC);
                    locator.setContentCategory("any-fulltext");
                    
                    MdsFileVO metadata = new MdsFileVO();
                    metadata.setContentCategory("any-fulltext");
                    metadata.setTitle(new TextVO("Link"));
                    locator.getMetadataSets().add(metadata);

                    itemVO.getFiles().add(locator);
                }

            }
            else if (object instanceof BibtexToplevelComment)
            {
                this.logger.debug("Comment found: " + ((BibtexToplevelComment) object).getContent());
            }
        }

        XmlTransforming xmlTransforming = new XmlTransformingBean();
        try
        {
            if (entryFound )
            {
                return xmlTransforming.transformToItem(itemVO);
            }
            else
            {
                this.logger.warn("No entry found in BibTex record.");
                throw new RuntimeException();
            }
        }
        catch (TechnicalException e)
        {
            this.logger.error("An error ocurred while transforming the item.");
            throw new RuntimeException(e);
        }
    }
    
    private void addCreator(
            MdsPublicationVO publicationVO,
            BibtexPerson person,
            CreatorVO.CreatorRole role,
            String affiliation,
            String affiliationAddress) throws RuntimeException
    {
        PersonVO personVO = new PersonVO();
        personVO.setFamilyName(BibTexUtil.bibtexDecode(person.getLast()
                + (person.getLineage() != null ? " " + person.getLineage() : "")
                + (person.getPreLast() != null ? ", " + person.getPreLast() : "")));
        personVO.setGivenName(BibTexUtil.bibtexDecode(person.getFirst()));
        if (affiliation != null || affiliationAddress != null)
        {
            OrganizationVO organization = new OrganizationVO();
            organization.setName(new TextVO(affiliation));
            organization.setAddress(affiliationAddress);
            try
            {
                organization.setIdentifier(PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
            }
            catch (Exception e)
            {
                throw new RuntimeException();
            }

            personVO.getOrganizations().add(organization);
        }
        CreatorVO creatorVO = new CreatorVO(personVO, role);
        publicationVO.getCreators().add(creatorVO);
    }
}
