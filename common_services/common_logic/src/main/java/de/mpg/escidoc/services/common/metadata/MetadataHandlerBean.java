/*
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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.metadata;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.parser.BibtexParser;
import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.BibTexUtil;
import de.mpg.escidoc.services.common.util.LocalURIResolver;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 *
 * Validate item data against a given validation schema.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 146 $ $LastChangedDate: 2007-12-18 14:42:42 +0100 (Tue, 18 Dec 2007) $
 *
 */
@Stateless
@Remote
@RemoteBinding(jndiBinding = de.mpg.escidoc.services.common.MetadataHandler.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MetadataHandlerBean implements MetadataHandler
{

    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(MetadataHandlerBean.class);

    /**
     * {@inheritDoc}
     */
	public String fetchOAIRecord(String identifier, String source, String format) throws Exception
	{
		
		URL sourceUrl = new URL(source + identifier + "&metadataPrefix=" + format);
		
		logger.debug("arXiv URL: " + sourceUrl);
		
		URLConnection conn = sourceUrl.openConnection();
		String contentTypeHeader = conn.getHeaderField("Content-Type");
		String contentType = contentTypeHeader;
		String charset = "UTF-8";
		if (contentType.contains(";"))
		{
			contentType = contentType.substring(0, contentType.indexOf(";"));
			if (contentTypeHeader.contains("encoding="))
			{
				charset = contentTypeHeader.substring(contentTypeHeader.indexOf("encoding=") + 9);
				logger.debug("Charset found: " + charset);
			}
		}
		InputStreamReader reader = new InputStreamReader(sourceUrl.openStream(), charset);

		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setURIResolver(new LocalURIResolver("metadata/xslt"));
		Transformer transformer = factory.newTransformer(new StreamSource(ResourceUtil.getResourceAsStream("metadata/xslt/arXiv2pubItem.xsl")));
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(reader), new StreamResult(writer));
		
		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String bibtex2item(String bibtex) throws Exception {
		
		BibtexParser parser = new BibtexParser(true);
		BibtexFile file = new BibtexFile();
		parser.parse(file, new StringReader(bibtex));
		
		PubItemVO itemVO = new PubItemVO();
		MdsPublicationVO mds = new MdsPublicationVO();
		itemVO.setMetadata(mds);

		List entries = file.getEntries();
		
		if (entries == null || entries.size() == 0)
		{
			throw new NoEntryInBibtexException();
		}
		else if (entries.size() > 1)
		{
			throw new MultipleEntriesInBibtexException();
		}
		
		for (Object object : entries) {
			logger.debug("Entry: " + object);
			if (object instanceof BibtexEntry)
			{
								
				BibtexEntry entry = (BibtexEntry) object;
				
				// genre
				BibTexUtil.Genre bibGenre = BibTexUtil.Genre.valueOf(entry.getEntryType());
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
					}
				}
				
				// edition
				if (fields.get("edition") != null)
				{
					publishingInfoVO.setEdition(BibTexUtil.bibtexDecode(fields.get("edition").toString()));
				}
				
				// journal
				if (fields.get("journal") != null)
				{
					if (bibGenre == BibTexUtil.Genre.article
							|| bibGenre == BibTexUtil.Genre.misc
							|| bibGenre == BibTexUtil.Genre.unpublished)
					{
						sourceVO.setTitle(new TextVO(BibTexUtil.bibtexDecode(fields.get("journal").toString())));
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
						sourceVO.setStartPage(BibTexUtil.bibtexDecode(fields.get("pages").toString()));
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
						SourceVO sourceOfSource = new SourceVO(new TextVO(BibTexUtil.bibtexDecode(fields.get("series").toString())));
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
						sourceVO.getSources().get(0).setVolume(BibTexUtil.bibtexDecode(fields.get("volume").toString()));
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
				
				// affiliation
				if (fields.get("affiliation") != null)
				{
					
				}
				
				// author
				if (fields.get("author") != null)
				{
					if (fields.get("author") instanceof BibtexPersonList)
					{
						BibtexPersonList authors = (BibtexPersonList) fields.get("author");
						for (Object author : authors.getList()) {
							if (author instanceof BibtexPerson)
							{
								addCreator(mds, (BibtexPerson)author, CreatorVO.CreatorRole.AUTHOR);
							}
							else
							{
								logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + author + "] in [" + author + "]");
							}
						}
					}
					if (fields.get("author") instanceof BibtexPerson)
					{
						BibtexPerson author = (BibtexPerson) fields.get("author");
						addCreator(mds, (BibtexPerson)author, CreatorVO.CreatorRole.AUTHOR);
					}
					else if (fields.get("author") instanceof BibtexString)
					{
						AuthorDecoder decoder = new AuthorDecoder(BibTexUtil.bibtexDecode(fields.get("author").toString()));
						if (decoder.getBestFormat() != null)
						{
							List<Author> authors = decoder.getAuthorListList().get(0);
							for (Author author : authors) {
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
								CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.AUTHOR);
								mds.getCreators().add(creatorVO);
							}
						}
					}
				}
				
				// editor
				if (fields.get("editor") != null)
				{
					
					logger.debug("fields.get(\"editor\"): " + fields.get("editor").getClass());
					
					if (fields.get("editor") instanceof BibtexPersonList)
					{
						BibtexPersonList editors = (BibtexPersonList) fields.get("editor");
						for (Object editor : editors.getList()) {
							if (editor instanceof BibtexPerson)
							{
								addCreator(mds, (BibtexPerson)editor, CreatorVO.CreatorRole.EDITOR);
							}
							else
							{
								logger.warn("Entry in BibtexPersonList not a BibtexPerson: [" + editor + "] in [" + editors + "]");
							}
						}
					}
					if (fields.get("editor") instanceof BibtexPerson)
					{
						BibtexPerson editor = (BibtexPerson) fields.get("editor");
						addCreator(mds, (BibtexPerson)editor, CreatorVO.CreatorRole.EDITOR);
					}
					else if (fields.get("editor") instanceof BibtexString)
					{
						
						String editor = BibTexUtil.bibtexDecode(fields.get("editor").toString());
						
						AuthorDecoder decoder = new AuthorDecoder(editor);
						if (decoder.getBestFormat() != null)
						{
							List<Author> authors = decoder.getAuthorListList().get(0);
							for (Author author : authors) {
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
								CreatorVO creatorVO = new CreatorVO(personVO, CreatorVO.CreatorRole.EDITOR);
								mds.getCreators().add(creatorVO);
							}
						}
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
					mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISBN, BibTexUtil.bibtexDecode(fields.get("isbn").toString())));
				}
				
				// issn
				if (fields.get("issn") != null)
				{
					mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ISSN, BibTexUtil.bibtexDecode(fields.get("issn").toString())));
				}
				
				// keywords
				if (fields.get("keywords") != null)
				{
					mds.setSubject(new TextVO(BibTexUtil.bibtexDecode(fields.get("keywords").toString())));
				}

				// language
				if (fields.get("language") != null)
				{
					mds.getLanguages().add(BibTexUtil.bibtexDecode(fields.get("language").toString()));
				}
				
				// subtitle
				if (fields.get("subtitle") != null)
				{
					mds.getAlternativeTitles().add(new TextVO(BibTexUtil.bibtexDecode(fields.get("language").toString())));
				}
				
				// url
				if (fields.get("url") != null)
				{
					mds.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, BibTexUtil.bibtexDecode(fields.get("url").toString())));
				}
				
			}
		}
		
		XmlTransforming xmlTransforming = new XmlTransformingBean();
		
		logger.debug("Source:" + itemVO.getMetadata().getSources().get(0).getTitle());
		
		return xmlTransforming.transformToItem(itemVO);
	}

	private void addCreator(MdsPublicationVO publicationVO, BibtexPerson person, CreatorVO.CreatorRole role)
	{
		PersonVO personVO = new PersonVO();
		personVO.setFamilyName(BibTexUtil.bibtexDecode(person.getLast() + (person.getLineage() != null ? " " + person.getLineage() : "") + (person.getPreLast() != null ? ", " + person.getPreLast() : "")));
		personVO.setGivenName(BibTexUtil.bibtexDecode(person.getFirst()));
		CreatorVO creatorVO = new CreatorVO(personVO, role);
		publicationVO.getCreators().add(creatorVO);
	}

}
