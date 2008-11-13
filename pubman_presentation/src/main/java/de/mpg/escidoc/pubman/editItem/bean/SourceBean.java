package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.trinidad.component.core.nav.CoreCommandButton;

import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.framework.PropertyReader;

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
	
	private boolean autosuggestJournals = false;
	
	private CoreCommandButton btn_chooseCollection = new CoreCommandButton();
	

    public SourceBean()
	{
		// ensure the parentVO is never null;
		this(new SourceVO());
		this.btn_chooseCollection.setId("Source1");
	}

	public SourceBean(SourceVO source)
	{
		setSource(source);
		this.btn_chooseCollection.setId("Source1");
		if(source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL))
		{
			this.autosuggestJournals = true;
		}
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
		
		if(source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL))
		{
			this.autosuggestJournals = true;
		}
	}
	
	public void chooseSourceGenre(ValueChangeEvent event)
    {
    	String sourceGenre = event.getNewValue().toString();
    	System.out.println(sourceGenre);
    	if(sourceGenre.equals(SourceVO.Genre.JOURNAL.toString()))
    	{
    		this.autosuggestJournals = true;
    	}
    }

	public String chooseGenre()
	{
		if(this.source.getGenre() != null && this.source.getGenre().equals(SourceVO.Genre.JOURNAL))
		{
			this.autosuggestJournals = true;
		}
		return EditItem.LOAD_EDITITEM;
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

	public boolean getAutosuggestJournals() {
		return autosuggestJournals;
	}

	public void setAutosuggestJournals(boolean autosuggestJournals) {
		this.autosuggestJournals = autosuggestJournals;
	}

	public CoreCommandButton getBtn_chooseCollection() {
		return btn_chooseCollection;
	}

	public void setBtn_chooseCollection(CoreCommandButton btn_chooseCollection) {
		this.btn_chooseCollection = btn_chooseCollection;
	}

	


}
