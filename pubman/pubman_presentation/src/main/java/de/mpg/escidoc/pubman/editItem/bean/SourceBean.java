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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.EditItemBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection.IdentifierManager;
import de.mpg.escidoc.pubman.editItem.bean.TitleCollection.AlternativeTitleManager;
import de.mpg.escidoc.pubman.util.CreatorVOPresentation;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.SourceVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * POJO bean to deal with one source.
 * 
 * @author Mario Wagner
 */
public class SourceBean extends EditItemBean
{
    public static final String HIDDEN_DELIMITER = " \\|\\|##\\|\\| ";
    public static final String HIDDEN_INNER_DELIMITER = " @@~~@@ ";
    public static final String HIDDEN_IDTYPE_DELIMITER = "\\|";
    private SourceVO source;
    private CreatorCollection creatorCollection;
    private IdentifierCollection identifierCollection;
    private TitleCollection titleCollection;
    private boolean autosuggestJournals = false;
    //private CoreCommandButton btnChooseCollection = new CoreCommandButton();
    private String hiddenAlternativeTitlesField;
    private String hiddenIdsField;

    private List<SourceBean> list;

    /**
     * Create a source bean using a given {@link SourceVO}.
     * 
     * @param source The original source vo.
     */
    public SourceBean(SourceVO source, List<SourceBean> list)
    {
        this.list = list;
        setSource(source);
        //this.btnChooseCollection.setId("Source1");
        if (source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL))
        {
            this.autosuggestJournals = true;
        }
    }

    public SourceVO getSource()
    {
        return source;
    }

    /**
     * Set the source and initialize collections.
     * 
     * @param source The original source vo.
     */
    public void setSource(SourceVO source)
    {
        this.source = source;
        // initialize embedded collections
        if (getCreators().size() == 0)
        {
            bindCreatorsToBean(source.getCreators());
        }
        identifierCollection = new IdentifierCollection(source.getIdentifiers());
        titleCollection = new TitleCollection(source);
        if (source.getPublishingInfo() == null)
        {
            source.setPublishingInfo(new PublishingInfoVO());
        }
        if (source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL))
        {
            this.autosuggestJournals = true;
        }
    }

    public void chooseSourceGenre(ValueChangeEvent event)
    {
        String sourceGenre = event.getNewValue().toString();
        if (sourceGenre.equals(SourceVO.Genre.JOURNAL.toString()))
        {
            this.autosuggestJournals = true;
        }
    }

    /**
     * If genre is journal, activate auto suggest.
     * 
     * @return EditItem page.
     */
    public String chooseGenre()
    {
        if (this.source.getGenre() != null && this.source.getGenre().equals(SourceVO.Genre.JOURNAL))
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
     * localized creation of SelectItems for the source genres available.
     * 
     * @return SelectItem[] with Strings representing source genres
     */
    public SelectItem[] getSourceGenreOptions()
    {

        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                InternationalizationHelper.BEAN_NAME);
        ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        Map <String, String> excludedSourceGenres = SourceVOPresentation.getExcludedSourceGenreMap();
        List <SelectItem> sourceGenres = new ArrayList <SelectItem>();
        sourceGenres.add(new SelectItem("", bundleLabel.getString("EditItem_NO_ITEM_SET")));
        for (SourceVO.Genre value : SourceVO.Genre.values())
        {
            sourceGenres.add(new SelectItem(value, bundleLabel.getString("ENUM_GENRE_" + value.name())));
        }
        
        String uri = "";
        int i = 0;
        while  ( i < sourceGenres.size() )
        {
            if (sourceGenres.get(i).getValue() != null && !("").equals(sourceGenres.get(i).getValue())){
                uri = ((SourceVO.Genre)sourceGenres.get(i).getValue()).getUri();
            }
            
            if (excludedSourceGenres.containsValue(uri))
            {
                sourceGenres.remove(i);
            }
            else 
            {
                i++;
            }
        }
        return sourceGenres.toArray(new SelectItem[sourceGenres.size()]);
    }

    public boolean getAutosuggestJournals()
    {
        return autosuggestJournals;
    }

    public void setAutosuggestJournals(boolean autosuggestJournals)
    {
        this.autosuggestJournals = autosuggestJournals;
    }

    /*
    public CoreCommandButton getBtnChooseCollection()
    {
        return btnChooseCollection;
    }

    public void setBtnChooseCollection(CoreCommandButton btnChooseCollection)
    {
        this.btnChooseCollection = btnChooseCollection;
    }

*/
    /**
     * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the model. Format of
     * alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format of ids: URN|urn:221441 ||##||
     * URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
     * 
     * @return
     */
    public String parseAndSetAlternativeTitlesAndIds()
    {
        //clear old alternative titles
        AlternativeTitleManager altTitleManager = getTitleCollection().getAlternativeTitleManager();
        altTitleManager.getObjectList().clear();

        //clear old identifiers
        IdentifierManager idManager = getIdentifierCollection().getIdentifierManager();
        idManager.getObjectList().clear();

        if (!getHiddenAlternativeTitlesField().trim().equals(""))
        {
            altTitleManager.getObjectList().addAll(parseAlternativeTitles(getHiddenAlternativeTitlesField()));
        }
        if (!getHiddenIdsField().trim().equals(""))
        {
            // idManager.getDataListFromVO().clear();
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
            String[] parts = alternativeTitles[i].trim().split(HIDDEN_INNER_DELIMITER);
            String alternativeTitleType = parts[0].trim();
            String alternativeTitle = parts[1].trim();
            if (!alternativeTitle.equals(""))
            {
                TextVO textVO = new TextVO(alternativeTitle);
                textVO.setType(alternativeTitleType);
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
            // id has no type, use type 'other'
            if (idParts.length == 1 && !idParts[0].equals(""))
            {
                IdentifierVO idVO = new IdentifierVO(IdType.OTHER, idParts[0].trim());
                list.add(idVO);
            }
            // Id has a type
            else if (idParts.length == 2)
            {
                IdType idType = IdType.OTHER;

                for (IdType id : IdType.values())
                {
                    if (id.getUri().equals(idParts[0]))
                    {
                        idType = id;
                    }
                }

                IdentifierVO idVO = new IdentifierVO(idType, idParts[1].trim());
                list.add(idVO);
            }
        }
        return list;
    }

    public int getPosition()
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i) == this)
            {
                return i;
            }
        }
        return -1;
    }

    public String add()
    {

        SourceVO sourceVO = new SourceVO();
        if(sourceVO.getIdentifiers().size()==0)
        {
            sourceVO.getIdentifiers().add(new IdentifierVO());
        }

        SourceBean newSourceBean = new SourceBean(sourceVO, this.list);
        CreatorVOPresentation newSourceCreator = new CreatorVOPresentation(newSourceBean.getCreators(), newSourceBean);
        newSourceCreator.setType(CreatorType.PERSON);
        newSourceCreator.setPerson(new PersonVO());
        newSourceCreator.getPerson().setIdentifier(new IdentifierVO());
        newSourceCreator.getPerson().setOrganizations(new ArrayList<OrganizationVO>());
        OrganizationVO newCreatorOrganization = new OrganizationVO();
        newCreatorOrganization.setName(new TextVO());
        newSourceCreator.getPerson().getOrganizations().add(newCreatorOrganization);
        newSourceBean.getCreators().add(newSourceCreator);
        list.add(getPosition() + 1, newSourceBean);
        newSourceBean.initOrganizationsFromCreators();
        return "";
    }

    public String remove()
    {
        list.remove(this);
        return "";
    }

    public boolean isSingleElement()
    {
        return (this.list.size() == 1);
    }

    public String getJournalSuggestClass()
    {
        if (source.getGenre() == Genre.JOURNAL)
        {
            return " journalSuggest";
        }
        else
        {
            return "";
        }
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

    public List<SourceBean> getList()
    {
        return list;
    }

    public void setList(List<SourceBean> list)
    {
        this.list = list;
    }

}
