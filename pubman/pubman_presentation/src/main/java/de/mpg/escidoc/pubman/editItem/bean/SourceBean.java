package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.trinidad.component.core.nav.CoreCommandButton;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection.IdentifierManager;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection.AlternativeTitleManager;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;

/**
 * POJO bean to deal with one source.
 * 
 * @author Mario Wagner
 */
public class SourceBean extends FacesBean
{
    public final static String HIDDEN_DELIMITER = "\\|\\|##\\|\\|";
    public final static String HIDDEN_IDTYPE_DELIMITER = "\\|";
    
	private SourceVO source;
	
	private CreatorCollection creatorCollection;
	private IdentifierCollection identifierCollection;
	private TitleCollection titleCollection;
	
	private boolean autosuggestJournals = false;
	
	private CoreCommandButton btn_chooseCollection = new CoreCommandButton();
	
	private String hiddenAlternativeTitlesField;
	
	private String hiddenIdsField;
	
	private String creatorParseString;
    private boolean overwriteCreators;
	
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
	
	   /**
     * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the model.
     * Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3
     * Format of ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
     * @return
     */
    public String parseAndSetAlternativeTitlesAndIds()
    {
        if (!getHiddenAlternativeTitlesField().trim().equals(""))
        {
            AlternativeTitleManager altTitleManager =  getTitleCollection().getAlternativeTitleManager();
            altTitleManager.getObjectList().clear();
            altTitleManager.getObjectList().addAll(parseAlternativeTitles(getHiddenAlternativeTitlesField()));
            
        }
        
        
        if (!getHiddenIdsField().trim().equals(""))
        {
            IdentifierManager idManager = getIdentifierCollection().getIdentifierManager();
            idManager.getObjectList().clear();
            //idManager.getDataListFromVO().clear();
            
            idManager.getObjectList().addAll(parseIdentifiers(getHiddenIdsField()));
        }
        
        return "";
    }
    
    public static List<TextVO> parseAlternativeTitles(String titleList)
    {
        List<TextVO> list = new ArrayList<TextVO>();
        
        String[] alternativeTitles = titleList.split(HIDDEN_DELIMITER);
        for (int i = 0; i < alternativeTitles.length; i++)
        {
            String alternativeTitle = alternativeTitles[i].trim();
            if (!alternativeTitle.equals(""))
            {
                
                TextVO textVO = new TextVO(alternativeTitle);
                list.add(textVO);
               
            }
        }
        return list;
    }
    
    public static List<IdentifierVO> parseIdentifiers(String idList)
    {
        List<IdentifierVO> list = new ArrayList<IdentifierVO>();
  
        String[] ids = idList.split(HIDDEN_DELIMITER);
        for (int i = 0; i < ids.length; i++)
        {
            String idComplete = ids[i].trim();
            
            String[] idParts = idComplete.split(HIDDEN_IDTYPE_DELIMITER);
            
            //id has no type, use type 'other'
            if (idParts.length==1 && !idParts[0].equals(""))
            {
                IdentifierVO idVO = new IdentifierVO(IdType.OTHER, idParts[0].trim());
                list.add(idVO);
            }
            
            //Id has a type
            else if (idParts.length==2)
            {
                IdentifierVO idVO = new IdentifierVO(IdType.valueOf(idParts[0]), idParts[1].trim());
                list.add(idVO);
            }
        }  
        return list;
        
    }

    public void setHiddenIdsField(String hiddenIdsField)
    {
        this.hiddenIdsField = hiddenIdsField;
    }

    public String getHiddenIdsField()
    {
        return hiddenIdsField;
    }

    public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField)
    {
        this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
    }

    public String getHiddenAlternativeTitlesField()
    {
        return hiddenAlternativeTitlesField;
    }
    
    public String addCreatorString()
    {
        try
        {
            EditItem.parseCreatorString(getCreatorParseString(), getCreatorCollection(), getOverwriteCreators());
            setCreatorParseString("");

            return null;
        }
        catch (Exception e)
        {
            error(getMessage("ErrorParsingCreatorString"));
            return null;
            
        }
    }

    public void setCreatorParseString(String creatorParseString)
    {
        this.creatorParseString = creatorParseString;
    }

    public String getCreatorParseString()
    {
        return creatorParseString;
    }

    public void setOverwriteCreators(boolean overwriteCreators)
    {
        this.overwriteCreators = overwriteCreators;
    }

    public boolean getOverwriteCreators()
    {
        return overwriteCreators;
    }

}
