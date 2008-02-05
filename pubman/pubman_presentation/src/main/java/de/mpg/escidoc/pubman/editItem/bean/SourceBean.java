package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * POJO bean to deal with one source.
 * 
 * @author Mario Wagner
 */
public class SourceBean
{
	private SourceVO source;
	
	private CreatorCollection creatorCollection;
	private IdentifierCollection identifierCollection;
	private TitleCollection titleCollection;
	

    public SourceBean()
	{
		// ensure the parentVO is never null;
		this(new SourceVO());
	}

	public SourceBean(SourceVO source)
	{
		setSource(source);
	}

	public SourceVO getSource()
	{
		return source;
	}

	public void setSource(SourceVO source)
	{
		this.source = source;
		// initialize embedded collections
		creatorCollection = new CreatorCollection(source.getCreators());
		identifierCollection = new IdentifierCollection(source.getIdentifiers());
		titleCollection = new TitleCollection(source);
		
		if (source.getPublishingInfo() == null)
		{
			source.setPublishingInfo(new PublishingInfoVO());
		}
	}

	
	public CreatorCollection getCreatorCollection()
	{
		return creatorCollection;
	}

	public void setCreatorCollection(CreatorCollection creatorCollection)
	{
		this.creatorCollection = creatorCollection;
	}

	public IdentifierCollection getIdentifierCollection()
	{
		return identifierCollection;
	}

	public void setIdentifierCollection(IdentifierCollection identifierCollection)
	{
		this.identifierCollection = identifierCollection;
	}

	public TitleCollection getTitleCollection()
	{
		return titleCollection;
	}

	public void setTitleCollection(TitleCollection titleCollection)
	{
		this.titleCollection = titleCollection;
	}
	
	/**
	 * localized creation of SelectItems for the source genres available
	 * @return SelectItem[] with Strings representing source genres
	 */
	public SelectItem[] getSourceGenreOptions()
	{
	    InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
	    ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

	    SelectItem NO_ITEM_SET = new SelectItem("", bundleLabel.getString("EditItem_NO_ITEM_SET"));
	    SelectItem GENRE_BOOK = new SelectItem(SourceVO.Genre.BOOK, bundleLabel.getString("ENUM_GENRE_BOOK"));
	    SelectItem GENRE_ISSUE = new SelectItem(SourceVO.Genre.ISSUE, bundleLabel.getString("ENUM_GENRE_ISSUE"));
	    SelectItem GENRE_JOURNAL = new SelectItem(SourceVO.Genre.JOURNAL, bundleLabel.getString("ENUM_GENRE_JOURNAL"));
	    SelectItem GENRE_PROCEEDINGS = new SelectItem(SourceVO.Genre.PROCEEDINGS, bundleLabel.getString("ENUM_GENRE_PROCEEDINGS"));
	    SelectItem GENRE_SERIES = new SelectItem(SourceVO.Genre.SERIES, bundleLabel.getString("ENUM_GENRE_SERIES"));
	    return new SelectItem[] { NO_ITEM_SET, GENRE_BOOK, GENRE_ISSUE, GENRE_JOURNAL, GENRE_PROCEEDINGS, GENRE_SERIES };
	}



}
