/**
 * 
 */
package de.mpg.escidoc.services.citation_style_language_manager;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLNameBuilder;
import de.undercouch.citeproc.csl.CSLType;

/**
 * @author walter
 * 
 */
//@Stateless
public class MetadataProvider implements ItemDataProvider {
	
	private final static Logger logger = Logger
			.getLogger(CitationStyleLanguageManagerDefaultImpl.class);
	
//	@EJB
	private XmlTransforming xmlTransformer;
	private String itemList;
	private List<PubItemVO> pubItemList;
	private List<String> ids = new ArrayList<String>();
	
	MetadataProvider (String itemList) {
		try {
			Context context = new InitialContext();
			xmlTransformer= (XmlTransforming) context.lookup("java:global/pubman_ear/common_logic/XmlTransformingBean");
			pubItemList = xmlTransformer.transformToPubItemList(itemList);
			for (PubItemVO pubItem : pubItemList) 
			{
				ids.add(pubItem.getVersion().getObjectId());
			}
		} catch (TechnicalException e) {
			logger.error("Unable to transform itemList", e);
		} catch (NamingException e) {
			logger.error("Unable to find XmlTransforming service",e);
		}
	}
	
	@PostConstruct
	private void init() 
	{
		
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
	 * @see
	 * de.undercouch.citeproc.ItemDataProvider#retrieveItem(java.lang.String)
	 */
	@Override
	public CSLItemData retrieveItem(String id) {
		PubItemVO currentItem = pubItemList.get(ids.indexOf(id));
		MdsPublicationVO metadata = currentItem.getMetadata();
		CSLName personName = new CSLName();
		CSLItemDataBuilder cslItem = new CSLItemDataBuilder().id(currentItem.getVersion().getObjectId());
		
		String[] dateFormats = { "YYYY-MM-DD" , "YYYY-MM", "YYYY" };
		
		// Genre
		cslItem.type(this.getCslGenre(metadata.getGenre()));
		
		// Title
		cslItem.title(metadata.getTitle().getValue());
		
		// Alternative title
		for (TextVO title : metadata.getAlternativeTitles())
		{
			if (!SourceVO.AlternativeTitleType.HTML.equals(title.getType())
					&& !SourceVO.AlternativeTitleType.LATEX.equals(title.getType())
					&& !SourceVO.AlternativeTitleType.MATHML.equals(title.getType()))
			{
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
		for (CreatorVO creator : metadata.getCreators())
		{
			if (CreatorVO.CreatorType.PERSON.equals(creator.getType()))
			{
				if (CreatorVO.CreatorRole.AUTHOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.COMMENTATOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.ACTOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.INVENTOR.equals(creator.getRole())) 
				{
					authorList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
				else if (CreatorVO.CreatorRole.EDITOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.PRODUCER.equals(creator.getRole())
						|| CreatorVO.CreatorRole.APPLICANT.equals(creator.getRole())
						|| CreatorVO.CreatorRole.CONTRIBUTOR.equals(creator.getRole())) 
				{
					editorList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
				else if (CreatorVO.CreatorRole.DIRECTOR.equals(creator.getRole()))
				{
					directorList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
				else if (CreatorVO.CreatorRole.ILLUSTRATOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.PHOTOGRAPHER.equals(creator.getRole())
						|| CreatorVO.CreatorRole.ARTIST.equals(creator.getRole())
						|| CreatorVO.CreatorRole.PAINTER.equals(creator.getRole())
						|| CreatorVO.CreatorRole.CINEMATOGRAPHER.equals(creator.getRole()))
				{
					illustratorList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
				else if (CreatorVO.CreatorRole.TRANSLATOR.equals(creator.getRole())
						|| CreatorVO.CreatorRole.TRANSCRIBER.equals(creator.getRole()))
				{
					translatorList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
				else if (CreatorVO.CreatorRole.SOUND_DESIGNER.equals(creator.getRole()))
				{
					composerList.add(new CSLNameBuilder().given(creator.getPerson().getGivenName()).family(creator.getPerson().getFamilyName()).build());
				}
			}
			else if (CreatorVO.CreatorType.ORGANIZATION.equals(creator.getType()))
			{
				editorList.add(new CSLNameBuilder().given("").family(creator.getOrganization().getName().getValue()).build()); // empty String needed
			}
		}
		if (authorList.size() > 0)
		{
			cslItem.author(getCSLNameArrayFromList(authorList));
		}
		if (editorList.size() > 0)
		{
			cslItem.editor(getCSLNameArrayFromList(editorList));
		}
		if (directorList.size() > 0)
		{
			cslItem.director(getCSLNameArrayFromList(directorList));
		}
		if (illustratorList.size() > 0)
		{
			cslItem.illustrator(getCSLNameArrayFromList(illustratorList));
		}
		if (translatorList.size() > 0)
		{
			cslItem.translator(getCSLNameArrayFromList(translatorList));
		}
		if (composerList.size() > 0)
		{
			cslItem.composer(getCSLNameArrayFromList(composerList));
		}
		
		// Date
		if (metadata.getDateSubmitted() != null) // Submitted
		{
			for (String formatString : dateFormats)
			{
				try
				{
					Date date = new SimpleDateFormat(formatString).parse(metadata.getDateSubmitted());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					cslItem.submitted(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException e)
				{
					logger.error("Error parsing date submitted. Trying other dateformat");
				}
			}
			
		}
		if (metadata.getDatePublishedInPrint() != null) // Published in Print
		{
			for (String formatString : dateFormats)
			{
				try
				{
					Date date = new SimpleDateFormat(formatString).parse(metadata.getDatePublishedInPrint());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException e)
				{
					logger.error("Error parsing date issued. Trying other dateformat");
				}
			}
		}
		else if (metadata.getDatePublishedOnline() != null) // Published online
		{
			for (String formatString : dateFormats)
			{
				try
				{
					Date date = new SimpleDateFormat(formatString).parse(metadata.getDatePublishedOnline());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException e)
				{
					logger.error("Error parsing date issued (published in print). Trying other dateformat");
				}
			}
		}
		else if (metadata.getDateAccepted() != null
				&& Genre.THESIS.equals(metadata.getGenre())) // Published online
		{
			for (String formatString : dateFormats)
			{
				try
				{
					Date date = new SimpleDateFormat(formatString).parse(metadata.getDateAccepted());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException e)
				{
					logger.error("Error parsing date issued (accepted). Trying other dateformat");
				}
			}
		}
		
		// URL / Files
		if (currentItem.getFiles() != null 
				&& !currentItem.getFiles().isEmpty())
		{
			List<FileVO> fileList = currentItem.getFiles();
			Collections.sort(fileList, new FileUrlPriorityComparator());
			if (fileList.get(0) != null)
			{
				if (FileVO.Visibility.PUBLIC.equals(fileList.get(0).getVisibility()))
				{
					if (("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext".
									equals(fileList.get(0).getContentCategory())
							|| "http://purl.org/escidoc/metadata/ves/content-categories/post-print".
									equals(fileList.get(0).getContentCategory())
							|| "http://purl.org/escidoc/metadata/ves/content-categories/pre-print".
									equals(fileList.get(0).getContentCategory())
							|| "http://purl.org/escidoc/metadata/ves/content-categories/publisher-version".
									equals(fileList.get(0).getContentCategory())))
					{
						if (FileVO.Storage.EXTERNAL_URL.equals(fileList.get(0).getStorage()))
						{
							cslItem.URL(fileList.get(0).getContent());
						}
						else if (FileVO.Storage.INTERNAL_MANAGED.equals(fileList.get(0).getStorage()))
						{
							cslItem.URL(fileList.get(0).getPid());
						}
					}
					
				}
			}
		}
		else
		{
			for (IdentifierVO identifier : metadata.getIdentifiers())
			{
				if (IdentifierVO.IdType.URI.equals(identifier.getType())
						|| IdentifierVO.IdType.URN.equals(identifier.getType()))
				{
					cslItem.URL(identifier.getId());
					break;
				}
			}
		}
		
		// Identifier
		for (IdentifierVO identifier : metadata.getIdentifiers())
		{
			if (IdentifierVO.IdType.DOI.equals(identifier.getType()))
			{
				cslItem.DOI(identifier.getId());
			}
		}
		
		// Keywords
		if (metadata.getFreeKeywords() != null 
				&& metadata.getFreeKeywords().getValue() != null)
		{
			cslItem.keyword(metadata.getFreeKeywords().getValue());
		}
		
		// Abstract
		if (metadata.getAbstracts() != null 
				&& !metadata.getAbstracts().isEmpty()) 
		{
			cslItem.abstrct(metadata.getAbstracts().get(0).getValue());
		}
		
		// Publisher / Publisher place / Edition
		if (metadata.getPublishingInfo() != null)
		{
			if (metadata.getPublishingInfo().getPublisher() != null)
			{
				cslItem.publisher(metadata.getPublishingInfo().getPublisher());
			}
			if (metadata.getPublishingInfo().getPlace() != null)
			{
				cslItem.publisher(metadata.getPublishingInfo().getPlace());
			}
			if (metadata.getPublishingInfo().getEdition() != null)
			{
				cslItem.publisher(metadata.getPublishingInfo().getEdition());
			}
		}
		
		// Number of pages
		if (metadata.getTotalNumberOfPages() != null)
		{
			cslItem.publisher(metadata.getTotalNumberOfPages());
		}
		
		// Source
		if (metadata.getSources() != null
				&& !metadata.getSources().isEmpty())
		{
			SourceVO source = metadata.getSources().get(0);
			// Genre dependent choice
			if (SourceVO.Genre.SERIES.equals(source.getGenre()))
			{
				// Source title
				cslItem.collectionTitle(source.getTitle().getValue());

				// Source creators
				List<CSLName> collectionEditorList = new ArrayList<CSLName>();
				for (CreatorVO sourceCreator : source.getCreators())
				{
					if (CreatorVO.CreatorRole.AUTHOR.equals(sourceCreator.getRole())
							|| CreatorVO.CreatorRole.EDITOR.equals(sourceCreator.getRole()))
					{
						collectionEditorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName()).family(sourceCreator.getPerson().getFamilyName()).build());
					}
				}
				if (collectionEditorList.size() > 0)
				{
					cslItem.collectionEditor(getCSLNameArrayFromList(collectionEditorList));
				}
			}
			else
			{
				// Source title
				cslItem.containerTitle(source.getTitle().getValue());
				
				// Source creators
				List<CSLName> containerAuthorList = new ArrayList<CSLName>();
				for (CreatorVO sourceCreator : source.getCreators())
				{
					if (CreatorVO.CreatorRole.AUTHOR.equals(sourceCreator.getRole())
							|| CreatorVO.CreatorRole.EDITOR.equals(sourceCreator.getRole()))
					{
						containerAuthorList.add(new CSLNameBuilder().given(sourceCreator.getPerson().getGivenName()).family(sourceCreator.getPerson().getFamilyName()).build());
					}
				}
				if (containerAuthorList.size() > 0)
				{
					cslItem.containerAuthor(getCSLNameArrayFromList(containerAuthorList));
				}
			}
			
			// Short title
			for (TextVO sourceAlternativeTitle : source.getAlternativeTitles())
			{
				if (SourceVO.AlternativeTitleType.ABBREVIATION.equals(sourceAlternativeTitle.getType())
						|| SourceVO.AlternativeTitleType.SUBTITLE.equals(sourceAlternativeTitle.getType())
						|| SourceVO.AlternativeTitleType.OTHER.equals(sourceAlternativeTitle.getType()))
				{
					cslItem.containerTitleShort(sourceAlternativeTitle.getValue());
					break;
				}
			}
			
			// Publisher / Publisher place / Edition (all from source)
			if((metadata.getPublishingInfo() == null
					|| metadata.getPublishingInfo().getPublisher() == null)
					&& (source.getPublishingInfo() != null 
					&& source.getPublishingInfo().getPublisher() != null))
			{
				cslItem.publisher(source.getPublishingInfo().getPublisher());
			}
			if((metadata.getPublishingInfo() == null
					|| metadata.getPublishingInfo().getPlace() == null)
					&& (source.getPublishingInfo() != null 
					&& source.getPublishingInfo().getPlace() != null))
			{
				cslItem.publisher(source.getPublishingInfo().getPlace());
			}
			if((metadata.getPublishingInfo() == null
					|| metadata.getPublishingInfo().getEdition() == null)
					&& (source.getPublishingInfo() != null 
					&& source.getPublishingInfo().getEdition() != null))
			{
				cslItem.publisher(source.getPublishingInfo().getEdition());
			}
			
			// Number of pages (from source)
			if (metadata.getTotalNumberOfPages() == null
					&& source.getTotalNumberOfPages() != null)
			{
				cslItem.publisher(source.getTotalNumberOfPages());
			}
			
			// Volume
			if (source.getVolume() != null) 
			{
				cslItem.volume(source.getVolume());
			}
			
			// Issue
			if (source.getIssue() != null)
			{
				cslItem.issue(source.getIssue());
			}
			
			// Startpage
			if (source.getStartPage() != null)
			{
				cslItem.pageFirst(source.getStartPage());
				// Combined Startpage - Endpage
				if (source.getEndPage() != null)
				{
					cslItem.page(source.getStartPage() + source.getEndPage());
				}
			}
			
			// Sequencenumber --> Locator
			if (source.getSequenceNumber() != null)
			{
				cslItem.page(source.getSequenceNumber());
			}
		}
		
		// Event
		if (metadata.getEvent() != null)
		{
			EventVO event = metadata.getEvent();
			// Event title
			if (event.getTitle() != null)
			{
				cslItem.event(event.getTitle().getValue());
			}
			if (event.getPlace() != null)
			{
				cslItem.eventplace(event.getPlace().getValue());
			}
			if (event.getStartDate() != null)
			{
				for (String formatString : dateFormats)
				{
					try
					{
						Date date = new SimpleDateFormat(formatString).parse(event.getStartDate());
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						cslItem.submitted(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					}
					catch (ParseException e)
					{
						logger.error("Error parsing date submitted");
					}
				}
			}
		}
		
		// build an return cslItem
		return cslItem.build();
	}
	
	private CSLType getCslGenre (Genre genre)
	{
		CSLType cslGenre = null;
		if (Genre.ARTICLE.equals(genre)
				|| Genre.EDITORIAL.equals(genre)
				|| Genre.PAPER.equals(genre)
				|| Genre.OTHER.equals(genre)) 
		{
			cslGenre = CSLType.ARTICLE;
		}
		else if (Genre.BOOK.equals(genre)
				|| Genre.COLLECTED_EDITION.equals(genre)
				|| Genre.COMMENTARY.equals(genre)
				|| Genre.ENCYCLOPEDIA.equals(genre)
				|| Genre.FESTSCHRIFT.equals(genre)
				|| Genre.HANDBOOK.equals(genre)
				|| Genre.ISSUE.equals(genre)
				|| Genre.JOURNAL.equals(genre)
				|| Genre.MANUAL.equals(genre)
				|| Genre.MONOGRAPH.equals(genre)
				|| Genre.MULTI_VOLUME.equals(genre)
				|| Genre.NEWSPAPER.equals(genre)
				|| Genre.PROCEEDINGS.equals(genre)
				|| Genre.SERIES.equals(genre))
		{
			cslGenre = CSLType.BOOK;
		}
		else if (Genre.BOOK_ITEM.equals(genre)
				|| Genre.CONTRIBUTION_TO_COLLECTED_EDITION.equals(genre)
				|| Genre.CONTRIBUTION_TO_COMMENTARY.equals(genre)
				|| Genre.CONTRIBUTION_TO_FESTSCHRIFT.equals(genre)
				|| Genre.CONTRIBUTION_TO_HANDBOOK.equals(genre))
		{
			cslGenre = CSLType.CHAPTER;
		}
		else if (Genre.BOOK_REVIEW.equals(genre))
		{
			cslGenre = CSLType.REVIEW_BOOK;
		}
		else if (Genre.CONFERENCE_PAPER.equals(genre)
				|| Genre.CONFERENCE_REPORT.equals(genre)
				|| Genre.MEETING_ABSTRACT.equals(genre))
		{
			cslGenre = CSLType.PAPER_CONFERENCE;
		}
		else if (Genre.CONTRIBUTION_TO_ENCYCLOPEDIA.equals(genre))
		{
			cslGenre = CSLType.ENTRY_ENCYCLOPEDIA;
		}
		else if (Genre.FILM.equals(genre))
		{
			cslGenre = CSLType.MOTION_PICTURE;
		}
		else if (Genre.MANUSCRIPT.equals(genre))
		{
			cslGenre = CSLType.MANUSCRIPT;
		}
		else if (Genre.NEWSPAPER_ARTICLE.equals(genre))
		{
			cslGenre = CSLType.ARTICLE_NEWSPAPER;
		}
		else if (Genre.PATENT.equals(genre))
		{
			cslGenre = CSLType.PATENT;
		}
		else if (Genre.REPORT.equals(genre))
		{
			cslGenre = CSLType.REPORT;
		}
		else if (Genre.TALK_AT_EVENT.equals(genre)
				|| Genre.POSTER.equals(genre)
				|| Genre.COURSEWARE_LECTURE.equals(genre))
		{
			cslGenre = CSLType.SPEECH;
		}
		else if (Genre.THESIS.equals(genre))
		{
			cslGenre = CSLType.THESIS;
		}
		else if (Genre.CASE_NOTE.equals(genre)
				|| Genre.CASE_STUDY.equals(genre)
				|| Genre.OPINION.equals(genre))
		{
			cslGenre = CSLType.LEGAL_CASE;
		}
		return cslGenre;
	}
	
	private static CSLName[] getCSLNameArrayFromList(List<CSLName> collectionEditorList)
	{
		CSLName[] creatorArray = new CSLName[collectionEditorList.size()];
		creatorArray = collectionEditorList.toArray(creatorArray);
		return creatorArray;
	}
	
	/**
	 * Comparator Implementation to sort Files for CSL-Output
	 * @author walter
	 *
	 */
	private class FileUrlPriorityComparator implements Comparator<FileVO>, Serializable {

		private static final long serialVersionUID = 3836103180718219384L;
		
		@Override
		public int compare(FileVO file1, FileVO file2) {
			if (file1.equals(file2))
			{
				return 0;
			}
			else 
			{
				if (file1.getVisibility().equals(file2.getVisibility()))
				{
					if (file1.getStorage().equals(file2.getStorage()))
					{
						if (file1.getContentCategory().equals(file2.getContentCategory()))
						{
							return 0;
						}
						else 
						{
							if ("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext"
									.equals(file1.getContentCategoryString()))
							{
								return -1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext"
									.equals(file2.getContentCategory()))
							{
								return 1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/post-print"
									.equals(file1.getContentCategory()))
							{
								return -1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/post-print"
									.equals(file2.getContentCategory()))
							{
								return 1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/pre-print"
									.equals(file1.getContentCategory()))
							{
								return -1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/pre-print"
									.equals(file2.getContentCategory()))
							{
								return 1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/publisher-version"
									.equals(file1.getContentCategory()))
							{
								return -1;
							}
							else if ("http://purl.org/escidoc/metadata/ves/content-categories/publisher-version"
									.equals(file2.getContentCategory()))
							{
								return 1;
							}
							else 
							{
								return 1;
							}
						}
					}
					else if (FileVO.Storage.EXTERNAL_URL.equals(file1.getStorage()))
					{
						return -1;
					}
					else 
					{
						return 1;
					}
				}
				else if (FileVO.Visibility.PUBLIC.equals(file1.getVisibility()))
				{
					return -1;
				}
				else if (FileVO.Visibility.AUDIENCE.equals(file1.getVisibility())
						&& FileVO.Visibility.PRIVATE.equals(file1.getVisibility()))
				{
					return -1;
				}
				else
				{
					return 1;
				}
				
			}
			
		}
		
	}
}
