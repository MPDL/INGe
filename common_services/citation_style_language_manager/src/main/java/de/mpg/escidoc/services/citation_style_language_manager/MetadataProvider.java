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
			CSLName[] authorArray = new CSLName[authorList.size()];
			authorArray = authorList.toArray(authorArray);
			cslItem.author(authorArray);
		}
		if (editorList.size() > 0)
		{
			CSLName[] editorArray = new CSLName[editorList.size()];
			editorArray = editorList.toArray(editorArray);
			cslItem.editor(editorArray);
		}
		if (directorList.size() > 0)
		{
			CSLName[] directorArray = new CSLName[directorList.size()];
			directorArray = directorList.toArray(directorArray);
			cslItem.director(directorArray);
		}
		if (illustratorList.size() > 0)
		{
			CSLName[] illustratorArray = new CSLName[illustratorList.size()];
			illustratorArray = illustratorList.toArray(illustratorArray);
			cslItem.illustrator(illustratorArray);
		}
		if (translatorList.size() > 0)
		{
			CSLName[] translatorArray = new CSLName[translatorList.size()];
			translatorArray = translatorList.toArray(translatorArray);
			cslItem.translator(translatorArray);
		}
		if (composerList.size() > 0)
		{
			CSLName[] composerArray = new CSLName[composerList.size()];
			composerArray = composerList.toArray(composerArray);
			cslItem.composer(composerArray);
		}
		
		// Date
		String[] dateFormats = { "YYYY-MM-DD" , "YYYY-MM", "YYYY" };
		if (metadata.getDateSubmitted() != null)
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
					logger.error("Error parsing date submitted");
				}
			}
			
		}
		else if (metadata.getDatePublishedInPrint() != null)
		{
			for (String formatString : dateFormats)
			{
				try
				{
					Date date = new SimpleDateFormat(formatString).parse(metadata.getDatePublishedInPrint());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					cslItem.submitted(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				}
				catch (ParseException e)
				{
					logger.error("Error parsing date issued");
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
		
		// Source
		if (metadata.getSources().get(0) != null)
		{
			SourceVO source = metadata.getSources().get(0);
			// Genre dependent choice
			if (SourceVO.Genre.SERIES.equals(source.getGenre()))
			{
				cslItem.collectionTitle(source.getTitle().getValue());
				// Source creators
				for (CreatorVO sourceCreator : source.getCreators())
				{
					cslItem.collectionEditor(sourceCreator.getPerson().getGivenName(), sourceCreator.getPerson().getFamilyName());
				}
			}
			else
			{
				cslItem.containerTitle(source.getTitle().getValue());
				// Source creators
				for (CreatorVO sourceCreator : source.getCreators())
				{
					cslItem.containerAuthor(sourceCreator.getPerson().getGivenName(), sourceCreator.getPerson().getFamilyName());
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
