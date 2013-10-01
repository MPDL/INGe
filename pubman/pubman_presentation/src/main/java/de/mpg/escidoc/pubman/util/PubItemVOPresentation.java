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

package de.mpg.escidoc.pubman.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.pubman.viewItem.bean.FileBean;
import de.mpg.escidoc.pubman.viewItem.bean.SearchHitBean;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Wrapper class for items to be used in the presentation.
 * @author franke
 * @author $Author$
 * @version: $Revision$ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
public class PubItemVOPresentation extends PubItemVO implements Internationalized
{

    private boolean selected = false;
    private boolean shortView = true;
    private boolean released = false;

    private List<FileBean> fileBeanList;
    private List<FileBean> locatorBeanList;
    private String descriptionMetaTag;

    /**
     * True if the item is shown in the revisions list, additional information is displayed then (release date, description)
     */
    private boolean isRevisionView = false;

    /**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> organizationArray;

    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> organizationList;

    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> affiliatedOrganizationsList;

    /**
     * The list of formatted creators (persons) in an ArrayList.
     */
    private ArrayList<String> creatorArray;

    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;

    /**
     * The list of formatted creators (persons AND organizations) in an ArrayList.
     */
    private ArrayList<String> allCreatorsList;
    //= new ArrayList<String>();

    /**
     * the first source of the item (for display in the medium view)
     */
    private SourceVO firstSource;
    //= new SourceVO();

    /**
     * List of hits. Every hit in files contains the file reference and the text fragments of the search hit.
     */
    private java.util.List<SearchHitVO> searchHitList;
    //= new java.util.ArrayList<SearchHitVO>();

    /**
     * List of search hits wrapped in a display optimized bean
     */
    private List<SearchHitBean> searchHits;
    //= new ArrayList<SearchHitBean>();

    private boolean isSearchResult = false;

    private boolean isFromEasySubmission;

    private List<WrappedLocalTag> wrappedLocalTags;
    //= new ArrayList<WrappedLocalTag>();

    /**
     * Validation messages that should be displayed in item list
     */
    private ValidationReportVO validationReport;

    //For handling the resource bundles (i18n)
    //private Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...

    private final InternationalizationHelper i18nHelper = (InternationalizationHelper) FacesContext
    .getCurrentInstance()
    .getApplication().getVariableResolver()
    .resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private float score;

    public PubItemVOPresentation(PubItemVO item)
    {
        super(item);
        if (item instanceof PubItemResultVO)
        {
            this.searchHitList = new java.util.ArrayList<SearchHitVO>();
            this.searchHitList = ((PubItemResultVO) item).getSearchHitList();
            this.isSearchResult = true;
            this.score=((PubItemResultVO) item).getScore();
        }


        if (this.getVersion() != null && this.getVersion().getState() != null)
        {
            this.released = this.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString());
        }

        // get the first source of the item (if available)
        if (item.getMetadata() != null && item.getMetadata().getSources() != null && item.getMetadata().getSources().size() > 0)
        {
            this.firstSource = new SourceVO();
            this.firstSource = item.getMetadata().getSources().get(0);
        }
        // get the search result hits

        setSearchHitBeanList();

        //Check the local tags
        if (this.getLocalTags().isEmpty())
        {
            this.getLocalTags().add("");
        }
        
        wrappedLocalTags= new ArrayList<WrappedLocalTag>();
        for (int i = 0; i < this.getLocalTags().size(); i++)
        {
            WrappedLocalTag wrappedLocalTag = new WrappedLocalTag();
            wrappedLocalTag.setParent(this);
            wrappedLocalTag.setValue(this.getLocalTags().get(i));
            if(wrappedLocalTag.getValue().length()>0 || wrappedLocalTags.size()==0)
                wrappedLocalTags.add(wrappedLocalTag);
        }
        

    }

    public void setSearchHitBeanList()
    {
        initFileBeans();

        if (this.searchHitList != null && this.searchHitList.size() > 0)
        {
            String beforeSearchHitString;
            String searchHitString;
            String afterSearchHitString;

            // browse through the list of hits and set up the SearchHitBean list
            for (int i = 0; i < searchHitList.size(); i++)
            {
                if (searchHitList.get(i).getType() == SearchHitType.FULLTEXT)
                {
                    //The array list need to be initialized only if the item is part of the search result
                    //and only if there is fulltext search result
                    if (this.searchHits == null)
                    {
                        this.searchHits = new ArrayList<SearchHitBean>();
                    }

                    if (searchHitList.get(i).getHitReference() != null)
                    {
                        for (int j = 0; j < searchHitList.get(i).getTextFragmentList().size(); j++)
                        {
                            int startPosition = 0;
                            int endPosition = 0;

                            startPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
                            endPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;

                            beforeSearchHitString = "..." + searchHitList.get(i).getTextFragmentList().get(j).getData().substring(0, startPosition);
                            searchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition);
                            afterSearchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "...";

                            this.searchHits.add(new SearchHitBean(beforeSearchHitString, searchHitString, afterSearchHitString));
                        }

                    }

                }

            }
        }
    }

    public void initFileBeans()
    {


        if (this.getFiles().isEmpty())
        {
            return;
        }

        this.fileBeanList = new ArrayList<FileBean>();
        this.locatorBeanList = new ArrayList<FileBean>();

        //fileBeanList.clear();
        //locatorBeanList.clear();

        for(FileVO file : getFiles())
        {
            // add locators
            if (file.getStorage() == FileVO.Storage.EXTERNAL_URL)
            {
                this.locatorBeanList.add(new FileBean(file, getVersion().getState()));
            }
            // add files
            else
            {
                if( searchHitList!=null && searchHitList.size()>0 && !getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    this.fileBeanList.add(new FileBean(file, getVersion().getState(), searchHitList));
                }
                else
                {
                    this.fileBeanList.add(new FileBean(file, getVersion().getState()));
                }

            }
        }
    }

    public boolean getSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getShortView()
    {
        return shortView;
    }

    public void setShortView(boolean shortView)
    {
        this.shortView = shortView;
    }

    public boolean getMediumView()
    {
        return !shortView;
    }

    public void setMediumView(boolean mediumView)
    {
        this.shortView = !mediumView;
    }


    /**
     * Distinguish between Persons and organization as creators and returns them formatted as string.
     * @return String the  formatted creators
     */
    public String getCreators()
    {
        if (this.getMetadata() != null)
        {
            int creatorsNo = getMetadata().getCreators().size();
            return getCreators(creatorsNo);
        }
        else if (this.getYearbookMetadata() != null)
        {
            int creatorsNo = this.getYearbookMetadata().getCreators().size();
            return getCreators(creatorsNo);
        }
        else 
        {
            return "";
        }
    }

    /**
     * Formats the display of the creators (internal use only, used for different views)
     * @return String the  formatted creators
     */

    private String getCreators(int creatorMaximum)
    {
        StringBuffer creators = new StringBuffer();
        for (int i = 0; i < creatorMaximum; i++)
        {
            if (this.getMetadata() != null)
            {
                if (getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(getMetadata().getCreators().get(i).getPerson().getFamilyName());
                        if (getMetadata()
                                .getCreators()
                                .get(i)
                                .getPerson()
                                .getGivenName() != null)
                        {
                            creators.append(", ");
                            creators.append(getMetadata().getCreators().get(i).getPerson().getGivenName());
                        }
                    }
                }
                else if (getMetadata().getCreators().get(i).getOrganization() != null)
                {
                    creators.append(
                            getMetadata().getCreators().get(i).getOrganization().getName().getValue());
                }
            }
            else if (this.getYearbookMetadata() != null)
            {
                if (getYearbookMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (getYearbookMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(getYearbookMetadata().getCreators().get(i).getPerson().getFamilyName());
                        if (getYearbookMetadata()
                                .getCreators()
                                .get(i)
                                .getPerson()
                                .getGivenName() != null)
                        {
                            creators.append(", ");
                            creators.append(getYearbookMetadata().getCreators().get(i).getPerson().getGivenName());
                        }
                    }
                }
                else if (getYearbookMetadata().getCreators().get(i).getOrganization() != null)
                {
                    creators.append(
                            getYearbookMetadata().getCreators().get(i).getOrganization().getName().getValue());
                }
            }
            

            if (i < creatorMaximum - 1)
            {
                creators.append("; ");
            }

        }

        return creators.toString();
    }


    public String getCreatorsShort()
    {
        int creatorsMax = 4;
        int creatorsNo = 0;
        if (this.getMetadata() != null)
        {
            creatorsNo = getMetadata().getCreators().size();
        }
        else if (this.getYearbookMetadata() != null)
        {
            creatorsNo = getYearbookMetadata().getCreators().size();
        }
        String creators;

        if (creatorsNo <= creatorsMax)
        {
            creators=getCreators(creatorsNo);
        }
        else
        {
            creators=getCreators(creatorsMax);
            creators= creators.toString() + " ...";
        }

        return creators;
    }
    
    
    /**
     * Delivers all creators, which are part of the MPG
     */
    public List<CreatorVO> getMpgAuthors()
    {
        List<CreatorVO> creators = this.getMetadata().getCreators();
        List<CreatorVO> mpgCreators = new ArrayList<CreatorVO> ();
        boolean isMpgCreator = false;
        for (CreatorVO creator: creators)
        {
            if(creator.getType().equals(CreatorType.PERSON)
                    && creator.getPerson().getOrganizations() != null )
            {
                for (OrganizationVO organization : creator.getPerson().getOrganizations())
                {
                    if (organization.getName().toString().contains("Max Planck Society"))
                    {
                        isMpgCreator = true;
                    }
                }
                if (isMpgCreator)
                {
                    mpgCreators.add(creator);
                    isMpgCreator = false;
                }
            }
        }
        return mpgCreators;
    }

    /**
     * Returns the newest date of the metadata date section.
     * @return the latest date
     */
    public String getLatestDate()
    {
        if (getMetadata().getDatePublishedInPrint() != null && !"".equals(getMetadata().getDatePublishedInPrint()))
        {
            return getMetadata().getDatePublishedInPrint() + ", " + getLabel("ViewItem_lblDatePublishedInPrint");
        }
        else if (getMetadata().getDatePublishedOnline() != null && !"".equals(getMetadata().getDatePublishedOnline()))
        {
            return getMetadata().getDatePublishedOnline() + ", " + getLabel("ViewItem_lblDatePublishedOnline");
        }
        else if (getMetadata().getDateAccepted() != null && !"".equals(getMetadata().getDateAccepted()))
        {
            return getMetadata().getDateAccepted() + ", " + getLabel("ViewItem_lblDateAccepted");
        }
        else if (getMetadata().getDateSubmitted() != null && !"".equals(getMetadata().getDateSubmitted()))
        {
            return getMetadata().getDateSubmitted() + ", " + getLabel("ViewItem_lblDateSubmitted");
        }
        else if (getMetadata().getDateModified() != null && !"".equals(getMetadata().getDateModified()))
        {
            return getMetadata().getDateModified() + ", " + getLabel("ViewItem_lblDateModified");
        }
        else if (getMetadata().getDateCreated() != null && !"".equals(getMetadata().getDateCreated()))
        {
            return getMetadata().getDateCreated() + ", " + getLabel("ViewItem_lblDateCreated");
        }
        else
        {
            return null;
        }
    }

    public String getDatesAsString()
    {
        if ((getMetadata().getDateAccepted()==null) &&
                (getMetadata().getDateCreated()==null)  &&
                (getMetadata().getDateModified() ==null)  &&
                (getMetadata().getDatePublishedInPrint() ==null)&&
                (getMetadata().getDatePublishedOnline() ==null) &&
                (getMetadata().getDateSubmitted() ==null))
        {
            return "";
        }

        ArrayList<String> dates = new ArrayList<String>();

        if (getMetadata().getDateCreated()!=null && !getMetadata().getDateCreated().equals(""))
        {
            dates.add(getLabel("ViewItem_lblDateCreated") + ": " + getMetadata().getDateCreated());
        }
        if (getMetadata().getDateModified() != null && !getMetadata().getDateModified().equals(""))
        {
            dates.add(getLabel("ViewItem_lblDateModified") + ": " + getMetadata().getDateModified());
        }
        if (getMetadata().getDateSubmitted() != null && !getMetadata().getDateSubmitted().equals(""))
        {
            dates.add(getLabel("ViewItem_lblDateSubmitted") + ": " + getMetadata().getDateSubmitted());
        }
        if (getMetadata().getDateAccepted() != null && !getMetadata().getDateAccepted().equals(""))
        {
            dates.add(getLabel("ViewItem_lblDateAccepted") + ": " + getMetadata().getDateAccepted());
        }
        if (getMetadata().getDatePublishedOnline() != null && !getMetadata().getDatePublishedOnline().equals(""))
            dates.add(getLabel("ViewItem_lblDatePublishedOnline") + ": " + getMetadata().getDatePublishedOnline());

        if (getMetadata().getDatePublishedInPrint() != null && !getMetadata().getDatePublishedInPrint().equals(""))
            dates.add(getLabel("ViewItem_lblDatePublishedInPrint") + ": " + getMetadata().getDatePublishedInPrint());


        String allDates = "";

        for (String date : dates)
        {
            allDates = allDates + date + " | ";

        }

        //remove last two signs
        if (allDates.length()>2)
            allDates = allDates.substring(0, allDates.length()-2);

        return allDates;


    }

    public String getFormattedLatestReleaseModificationDate()
    {
        if (getLatestRelease().getModificationDate() != null)
        {
            return CommonUtils.format(getLatestRelease().getModificationDate());
        }
        else
        {
            return "-";
        }
    }

    /**
     * gets the genre of the item
     * @return String the genre of the item
     */
    public String getGenre()
    {
        String genre="";
        if(getMetadata().getGenre() != null)
        {
            genre = getLabel(this.i18nHelper.convertEnumToString(getMetadata().getGenre()));
        }
        return genre;
    }

    /**
     * gets the genre of the first source of the item
     * @return String the genre of the source
     */
    public String getSourceGenre()
    {
        String sourceGenre="";
        if(this.firstSource != null && this.firstSource.getGenre() != null)
        {
            sourceGenre = getLabel(this.i18nHelper.convertEnumToString(this.firstSource.getGenre()));
        }
        return sourceGenre;
    }

    /**
     * Returns a formatted String containing the start and the end page of the source
     * @return String the formatted start and end page
     */
    public String getStartEndPageSource()
    {
        if (this.firstSource==null)
        {
            return "";
        }
        StringBuffer startEndPage = new StringBuffer();
        //		if(this.firstSource != null)
        //		{
        if(this.firstSource.getStartPage() != null)
        {
            startEndPage.append(this.firstSource.getStartPage());
        }

        if(this.firstSource.getEndPage() != null && !this.firstSource.getEndPage().trim().equals(""))
        {
            startEndPage.append(" - ");
            startEndPage.append(this.firstSource.getEndPage());
        }
        //		}
        return startEndPage.toString();
    }

    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    public String getPublishingInfo()
    {
        if (getMetadata().getPublishingInfo()==null)
        {
            return "";
        }

        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        
        // Place
        if(getMetadata().getPublishingInfo().getPlace() != null)
        {
            publishingInfo.append(getMetadata().getPublishingInfo().getPlace().trim());
        }

        // colon
        if(getMetadata().getPublishingInfo().getPublisher() != null && !getMetadata().getPublishingInfo().getPublisher().trim().equals("") && getMetadata().getPublishingInfo().getPlace() != null && !getMetadata().getPublishingInfo().getPlace().trim().equals(""))
        {
            publishingInfo.append(" : ");
        }

        // Publisher
        if(getMetadata().getPublishingInfo().getPublisher() != null)
        {
            publishingInfo.append(getMetadata().getPublishingInfo().getPublisher().trim());
        }
        
        // Comma
        if((getMetadata().getPublishingInfo().getEdition() != null && !getMetadata().getPublishingInfo().getEdition().trim().equals("")) && ((getMetadata().getPublishingInfo().getPlace() != null && !getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (getMetadata().getPublishingInfo().getPublisher() != null && !getMetadata().getPublishingInfo().getPublisher().trim().equals(""))))
        {
            publishingInfo.append(", ");
        }

        // Edition
        if(getMetadata().getPublishingInfo().getEdition() != null)
        {
            publishingInfo.append(getMetadata().getPublishingInfo().getEdition());
        }
        
        return publishingInfo.toString();
    }

    /**
     * Returns the formatted Publishing Info of the source(!!!) according to filled elements
     * @return String the formatted Publishing Info of the source
     */
    public String getPublishingInfoSource()
    {
        if (this.firstSource == null)
        {
            return "";
        }

        StringBuffer publishingInfoSource = new StringBuffer();

        publishingInfoSource.append("");
        if(this.firstSource.getPublishingInfo() != null)
        {
            // Place
            if(this.firstSource.getPublishingInfo().getPlace() != null)
            {
                publishingInfoSource.append(this.firstSource.getPublishingInfo().getPlace().trim());
            }

            // colon
            if(this.firstSource.getPublishingInfo().getPublisher() != null && !this.firstSource.getPublishingInfo().getPublisher().trim().equals("") && this.firstSource.getPublishingInfo().getPlace() != null && !this.firstSource.getPublishingInfo().getPlace().trim().equals(""))
            {
                publishingInfoSource.append(" : ");
            }

            // Publisher
            if(this.firstSource.getPublishingInfo().getPublisher() != null)
            {
                publishingInfoSource.append(this.firstSource.getPublishingInfo().getPublisher().trim());
            }
            
            // Comma
            if((this.firstSource.getPublishingInfo().getEdition() != null && !this.firstSource.getPublishingInfo().getEdition().trim().equals("")) && ((this.firstSource.getPublishingInfo().getPlace() != null && !this.firstSource.getPublishingInfo().getPlace().trim().equals("")) || (this.firstSource.getPublishingInfo().getPublisher() != null && !this.firstSource.getPublishingInfo().getPublisher().trim().equals(""))))
            {
                publishingInfoSource.append(", ");
            }
            
            // Edition
            if(this.firstSource.getPublishingInfo().getEdition() != null)
            {
                publishingInfoSource.append(this.firstSource.getPublishingInfo().getEdition());
            }
        }
        return publishingInfoSource.toString();
    }

    /**
     * Returns the event title (50 Chars) and crops the last characters
     * @return String the event title
     */
    public String getEventTitle()
    {
        String eventTitle = "";
        if(getMetadata().getEvent() != null
                && getMetadata().getEvent().getTitle() != null
                && getMetadata().getEvent().getTitle().getValue() != null
                && !getMetadata().getEvent().getTitle().getValue().trim().equals(""))
        {
            if(getMetadata().getEvent().getTitle().getValue().length() > 50)
            {
                eventTitle = getMetadata().getEvent().getTitle().getValue().substring(0, 49) + "...";
            }
            else
            {
                eventTitle = getMetadata().getEvent().getTitle().getValue();
            }
        }
        return eventTitle;
    }

    /**
     * Returns the title (80 Chars) and crops the last characters.
     * Specification says 100 chars, but this is too long, 50 is too short.
     * @return String the title
     */
    public String getShortTitle()
    {
        if(getMetadata().getTitle() != null
                && getMetadata().getTitle().getValue() != null
                && !getMetadata().getTitle().getValue().trim().equals(""))
        {
            if(getMetadata().getTitle().getValue().length() > 80)
            {
                return getMetadata().getTitle().getValue().substring(0, 79) + "...";
            }
            else
            {
                return getMetadata().getTitle().getValue();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the first abstract (150 Chars) and crops the last characters.
     * Specification not available!
     * @return String the title
     */
    public String getShortAbstract()
    {
        if (getMetadata().getAbstracts().size() > 0)
        {
            if (getMetadata().getAbstracts().get(0).getValue().length() > 150)
            {
                return getMetadata().getAbstracts().get(0).getValue().substring(0, 149) + "...";
            }
            else
            {
                return getMetadata().getAbstracts().get(0).getValue();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * @return String the title
     */
    public String getFullTitle()
    {
        if (this.getMetadata() != null)
        {
            return getMetadata().getTitle().getValue();
        }
        else if (this.getYearbookMetadata() != null)
        {
            return this.getYearbookMetadata().getTitle().getValue();
        }
        else {
            return "";
        }
    }
    

    /**
     * Returns the source title (50 Chars) of the first source and crops the last characters
     * @return String the event title
     */
    public String getSourceTitle()
    {
        String sourceTitle = "";
        if(this.firstSource != null
                && this.firstSource.getTitle() != null
                && this.firstSource.getTitle().getValue() != null
                && !this.firstSource.getTitle().getValue().trim().equals(""))
        {
            if(this.firstSource.getTitle().getValue().length() > 50)
            {
                sourceTitle = this.firstSource.getTitle().getValue().substring(0, 49) + "...";
            }
            else
            {
                sourceTitle = this.firstSource.getTitle().getValue();
            }
        }
        return sourceTitle;
    }

    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean)FacesContext
        .getCurrentInstance()
        .getApplication().getVariableResolver()
        .resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.UIComponent, java.lang.String)
     */
    public void bindComponentLabel(UIComponent component, String placeholder)
    {
        ValueExpression value = FacesContext
        .getCurrentInstance()
        .getApplication()
        .getExpressionFactory()
        .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{lbl." + placeholder + "}", String.class);
        component.setValueExpression("value", value);
    }

    /**
     * This method examines the pubitem concerning its files and generates
     * a display string for the page according to the number of files detected.
     *
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of files
     */
    public String getFileInfo()
    {
        if (this.getFileList()==null)
        {
            return "";
        }

        StringBuffer files = new StringBuffer();

        //		if (this.getFileList() != null)
        //		{
        files.append(this.getFileList().size());

        // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
        if (this.getFileList().size() == 1)
        {
            files.append(" " + getLabel("ViewItemShort_lblFileAttached"));
        }
        else
        {
            files.append(" " + getLabel("ViewItemShort_lblFilesAttached"));
        }
        //		}
        return files.toString();
    }

    /**
     * This method examines the pubitem concerning its locators and generates
     * a display string for the page according to the number of locators detected.
     *
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of locators
     */
    public String getLocatorInfo()
    {
        if (this.getLocatorList()==null)
        {
            return "";
        }

        StringBuffer locators = new StringBuffer();

        //		if (this.getLocatorList() != null)
        //		{
        locators.append(this.getLocatorList().size());

        // if there is only 1 locator, display "Locator", otherwise display "Locators" (plural)
        if (this.getLocatorList().size() == 1)
        {
            locators.append(" " + getLabel("ViewItemShort_lblLocatorAttached"));
        }
        else
        {
            locators.append(" " + getLabel("ViewItemShort_lblLocatorsAttached"));
        }
        //		}
        return locators.toString();
    }

    /**
     * This method examines which file is really a file and not a locator and returns a list of native files
     * @return List<FileVO> file list
     */
    private List<FileVO> getFileList()
    {
        List<FileVO> fileList =null;
        if(this.getFiles() != null)
        {
            fileList = new ArrayList<FileVO>();
            for(int i = 0; i < this.getFiles().size(); i++)
            {
                if(this.getFiles().get(i).getStorage() == FileVO.Storage.INTERNAL_MANAGED)
                {
                    fileList.add(this.getFiles().get(i));
                }
            }
        }
        return fileList;
    }

    public int getNumberOfFiles()
    {
        return getFileList().size();
    }

    /**
     * This method examines which file is a locator and not a file and returns a list of locators
     * @return List<FileVO> locator list
     */
    private List<FileVO> getLocatorList()
    {
        List<FileVO> locatorList = null;
        if(this.getFiles() != null)
        {
            locatorList = new ArrayList<FileVO>();
            for(int i = 0; i < this.getFiles().size(); i++)
            {
                if(this.getFiles().get(i).getStorage() == FileVO.Storage.EXTERNAL_URL)
                {
                    locatorList.add(this.getFiles().get(i));
                }
            }
        }
        return locatorList;
    }

    public int getNumberOfLocators()
    {
        return getLocatorList().size();
    }

    /**
     * Counts the files and gives info back as int
     * @return int the amount of files belonging to this item
     */
    public int getAmountOfFiles()
    {
        int countedFiles = 0;

        if(this.getFileList() != null)
        {
            countedFiles = this.getFileList().size();
        }
        return countedFiles;
    }

    /**
     * Counts the locators and gives info back as int
     * @return int the amount of locators belonging to this item
     */
    public int getAmountOfLocators()
    {
        int countedLocators = 0;

        if(this.getLocatorList() != null)
        {
            countedLocators = this.getLocatorList().size();
        }
        return countedLocators;
    }

    /**
     * Counts the sources of the current item
     * @return int number of sources
     */
    public int getFurtherSources()
    {
        int furtherSources = 0;
        // get the  number of sources (if bigger than 1) minus the first one
        if(getMetadata().getSources() != null && getMetadata().getSources().size() > 1)
        {
            furtherSources = getMetadata().getSources().size() - 1;
        }
        return furtherSources;
    }

    /**
     * Counts the creators and returns the number as int (inportant for rendering in )
     * @return int number of creators
     */
    public int getCountCreators()
    {
        int creators = 0;
        if(this.creatorArray != null)
        {
            creators = creators + this.creatorArray.size();
        }
        if(this.creatorOrganizationsArray != null)
        {
            creators = creators + this.creatorOrganizationsArray.size();
        }
        return creators;
    }

    /**
     * Counts the affiliated organizations and returns the number as int (inportant for rendering in )
     * @return int number of organiozations
     */
    public int getCountAffiliatedOrganizations()
    {
        int organizations = 0;
        if(this.affiliatedOrganizationsList != null)
        {
            organizations = organizations + this.affiliatedOrganizationsList.size();
        }
        return organizations;
    }


    public void switchToMediumView()
    {
        shortView = false;
    }

    public void switchToShortView()
    {
        shortView = true;
    }

    public void select(ValueChangeEvent event)
    {
        selected = ((Boolean)event.getNewValue()).booleanValue();
    }

    public String getLink() throws Exception
    {
        if (this.getVersion() !=  null && this.getVersion().getObjectId() != null)
        {
            return PropertyReader.getProperty("escidoc.pubman.instance.url")
            + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
            + PropertyReader
            .getProperty("escidoc.pubman.item.pattern")
            .replaceAll("\\$1", this.getVersion().getObjectId()
                    + (this.getVersion().getVersionNumber() != 0 ? ":"
                            + this.getVersion().getVersionNumber() : ""));
        }
        else
        {
            return null;
        }
    }

    public String getLinkLatestRelease() throws Exception
    {
        if (this.getLatestRelease()!=null && this.getLatestRelease().getObjectId() != null)
        {
            return PropertyReader.getProperty("escidoc.pubman.instance.url")
            + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
            + PropertyReader
            .getProperty("escidoc.pubman.item.pattern")
            .replaceAll("\\$1", this.getLatestRelease().getObjectId()
                    + (this.getLatestRelease().getVersionNumber() != 0 ? ":"
                            + this.getLatestRelease().getVersionNumber() : ""));
        }
        else
        {
            return null;
        }
    }

    public boolean getShowCheckbox()
    {
        boolean showCheckbox = true;
        return showCheckbox;
    }

    public void writeBackLocalTags(ValueChangeEvent event)
    {
        this.getLocalTags().clear();
        for (WrappedLocalTag wrappedLocalTag : this.getWrappedLocalTags())
        {
            this.getLocalTags().add(wrappedLocalTag.getValue());
        }
    }

    /**
     * This method return the public state of the current item
     * @author Tobias Schraut
     * @return String public state of the current item
     */
    public String getItemPublicState()
    {
        String itemState="";
        if(this.getPublicStatus() != null)
        {
            itemState = getLabel(this.i18nHelper.convertEnumToString(this.getPublicStatus()));
        }
        return itemState;
    }

    /**
     * This method return the state of the current item version
     * @author Tobias Schraut
     * @return String state of the current item version
     */
    public String getItemState()
    {

        String itemState="";
        if(this.getVersion().getState() != null)
        {
            itemState = getLabel(this.i18nHelper.convertEnumToString(this.getVersion().getState()));
        }
        return itemState;
    }

    /**
     * This method return true if the item is withdrawn, otherwise false
     * @author Tobias Schraut
     * @return Boolean true if item is withdrawn
     */
    public boolean getIsStateWithdrawn()
    {
        return this.getPublicStatus().toString().equals(PubItemVO.State.WITHDRAWN.toString());
    }

    /**
     * This method return true if the item is submitted, otherwise false
     * @author Tobias Schraut
     * @return Boolean true if item is submitted
     */
    public boolean getIsStateSubmitted()
    {
        return this.getVersion().getState().toString().equals(PubItemVO.State.SUBMITTED.toString());
    }

    /**
     * This method return true if the item is released, otherwise false
     * @author Tobias Schraut
     * @return Boolean true if item is released
     */
    public boolean getIsStateReleased()
    {
        return this.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString());
    }

    /**
     * This method return true if the item is pending, otherwise false
     * @author Tobias Schraut
     * @return Boolean true if item is pending
     */
    public boolean getIsStatePending()
    {
        return this.getVersion().getState().toString().equals(PubItemVO.State.PENDING.toString());
    }

    /**
     * This method return true if the item is in revision, otherwise false
     * @author Tobias Schraut
     * @return Boolean true if item is in revision
     */
    public boolean getIsStateInRevision()
    {
        return this.getVersion().getState().toString().equals(PubItemVO.State.IN_REVISION.toString());
    }


    public java.util.List<SearchHitVO> getSearchHitList() {
        return searchHitList;
    }

    public void setSearchHitList(java.util.List<SearchHitVO> searchHitList) {
        this.searchHitList=searchHitList;
    }

    public ArrayList<String> getOrganizationArray() {
        return organizationArray;
    }

    public void setOrganizationArray(ArrayList<String> organizationArray) {
        this.organizationArray = organizationArray;
    }

    public ArrayList<ViewItemOrganization> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList) {
        this.organizationList = organizationList;
    }

    public List<OrganizationVO> getAffiliatedOrganizationsList() {
        return affiliatedOrganizationsList;
    }

    public void setAffiliatedOrganizationsList(
            List<OrganizationVO> affiliatedOrganizationsList) {
        this.affiliatedOrganizationsList = affiliatedOrganizationsList;
    }

    public ArrayList<String> getCreatorArray() {
        return creatorArray;
    }

    public void setCreatorArray(ArrayList<String> creatorArray) {
        this.creatorArray = creatorArray;
    }

    public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray() {
        return creatorOrganizationsArray;
    }

    public void setCreatorOrganizationsArray(
            ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray) {
        this.creatorOrganizationsArray = creatorOrganizationsArray;
    }

    public SourceVO getFirstSource() {
        return firstSource;
    }

    public void setFirstSource(SourceVO firstSource)
    {
        this.firstSource = firstSource;
    }

    public List<SearchHitBean> getSearchHits()
    {
        return searchHits;
    }

    public boolean getHasSearchHits()
    {
        return getSearchHits()!= null && getSearchHits().size() > 0;
    }

    public void setSearchHits(List<SearchHitBean> searchHits)
    {
        this.searchHits = searchHits;
    }

    public ArrayList<String> getAllCreatorsList()
    {
        return allCreatorsList;
    }

    public void setAllCreatorsList(ArrayList<String> allCreatorsList)
    {
        this.allCreatorsList = allCreatorsList;
    }

    public boolean getIsRevisionView()
    {
        return isRevisionView;
    }

    public void setIsRevisionView(boolean isRevisionView)
    {
        this.isRevisionView = isRevisionView;
    }

    public boolean isSearchResult()
    {
        return isSearchResult;
    }

    public void setSearchResult(boolean isSearchResult)
    {
        this.isSearchResult = isSearchResult;
    }

    public boolean getIsFromEasySubmission()
    {
        return isFromEasySubmission;
    }

    public void setFromEasySubmission(boolean isFromEasySubmission)
    {
        this.isFromEasySubmission = isFromEasySubmission;
    }

    public boolean getIsReleased()
    {
        return this.released;
    }

    public void setReleased(boolean released)
    {
        this.released = released;
    }

    public List<WrappedLocalTag> getWrappedLocalTags()
    {
        return wrappedLocalTags;
    }

    public int getNumberOfWrappedLocalTags()
    {
        return wrappedLocalTags.size();
    }

    public void setWrappedLocalTags(List<WrappedLocalTag> wrappedLocalTags)
    {
        this.wrappedLocalTags = wrappedLocalTags;
    }

    public class WrappedLocalTag implements Serializable
    {
        private String value;
        private PubItemVOPresentation parent;

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public PubItemVOPresentation getParent()
        {
            return parent;
        }

        public void setParent(PubItemVOPresentation parent)
        {
            this.parent = parent;
        }

        public String removeLocalTag()
        {
            parent.getWrappedLocalTags().remove(this);
            parent.writeBackLocalTags(null);
            return null;
        }

        public boolean getIsLast()
        {
            return (this == parent.getWrappedLocalTags().get(parent.getWrappedLocalTags().size() - 1));
        }

        public int getNumberOfAllTags()
        {
            return (parent.getWrappedLocalTags().size());
        }

        public boolean getIsSingle()
        {
            return (parent.getWrappedLocalTags().size() == 1);
        }
    }

    public String getIdentifier() throws Exception
    {
        String id = this.getLink().toString();
        String [] idSplit = id.split("/");

        return idSplit[idSplit.length - 1];
    }

    public String getOpenPDFSearchParameter()
    {
        return FileBean.getOpenPDFSearchParameter(searchHits);
    }

    public void setScore(float score)
    {
        this.score = score;
    }

    public float getScore()
    {
        return score;
    }

    public void setFileBeanList(List<FileBean> fileBeanList)
    {
        this.fileBeanList = fileBeanList;
    }

    public List<FileBean> getFileBeanList()
    {
        return fileBeanList;
    }
    
    /**
     * Delivers the FileBeans for all Files which have the content-category fulltext
     * @return List<FileBeans> which have the content-category fulltext
     */
    public List<FileBean> getFulltextFileBeanList()
    {
        List<FileBean> fulltexts = new ArrayList<FileBean> ();
        if (this.fileBeanList != null)
        {
            for (FileBean file : this.fileBeanList)
            {
                if ("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext".equals(file.getFile().getContentCategory())){
                    fulltexts.add(file);
                }
            }
        }
        return fulltexts;
    }
    
    /**
     * Delivers the FileBeans for all Files which have the content-category fulltext
     * @return List<FileBeans> which have the content-category fulltext
     */
    public List<FileBean> getSupplementaryMaterialFileBeanList()
    {
        List<FileBean> supplementaryMaterial = new ArrayList<FileBean> ();
        if (this.fileBeanList != null)
        {
            for (FileBean file : this.fileBeanList)
            {
                if ("http://purl.org/escidoc/metadata/ves/content-categories/supplementary-material".equals(file.getFile().getContentCategory())){
                    supplementaryMaterial.add(file);
                }
            }
        }
        return supplementaryMaterial;
    }

    public void setLocatorBeanList(List<FileBean> locatorBeanList)
    {
        this.locatorBeanList = locatorBeanList;
    }

    public List<FileBean> getLocatorBeanList()
    {
        return locatorBeanList;
    }
    public String getDescriptionMetaTag()
    {
        //add first creator to meta tag
        descriptionMetaTag = getLabel("ENUM_CREATORROLE_" + getMetadata().getCreators().get(0).getRoleString()) + ": " ;
        if(getMetadata().getCreators().get(0).getPerson() != null)
            descriptionMetaTag+= getMetadata().getCreators().get(0).getPerson().getFamilyName() +", " + getMetadata().getCreators().get(0).getPerson().getGivenName();
        else
            descriptionMetaTag += getMetadata().getCreators().get(0).getOrganization().getName();
        if(getMetadata().getCreators().size()>1)
            descriptionMetaTag += " et al.";
        //add genre information
        descriptionMetaTag += "; " + getLabel("ViewItemFull_lblGenre") + ": " + getLabel("ENUM_GENRE_"+getMetadata().getGenre()) ;
        //add published print date
        if(getMetadata().getDatePublishedInPrint()!= null && getMetadata().getDatePublishedInPrint()!="")
            descriptionMetaTag += "; " + getLabel("ViewItemShort_lblDatePublishedInPrint") + ": "+getMetadata().getDatePublishedInPrint();
        //add published online date if no publisched print date
        else if(getMetadata().getDatePublishedOnline()!= null && getMetadata().getDatePublishedOnline()!="")
            descriptionMetaTag += "; " + getLabel("ViewItemShort_lblDatePublishedOnline") + ": "+getMetadata().getDatePublishedOnline();

        //add open access component
        if (getFileBeanList() != null && getFileBeanList().size()>0) {
            for(FileBean file :getFileBeanList())
            {
                if(file.getIsVisible()==true)
                {
                    descriptionMetaTag += "; Open Access";
                    break;
                }
            }
        }
        //add keywords
        if(getMetadata().getFreeKeywords()!=null && getMetadata().getFreeKeywords().getValue() != null && getMetadata().getFreeKeywords().getValue()!="")
            descriptionMetaTag += "; Keywords: " + getMetadata().getFreeKeywords().getValue() ;
        //add title at the end of description meta tag
        if(getMetadata().getTitle() != null && getMetadata().getTitle().getValue()!=null && getMetadata().getTitle().getValue()!="")
        {
            descriptionMetaTag += "; " + getLabel("ViewItemFull_lblTitle") + ": " + getMetadata().getTitle().getValue() ;
        }

        descriptionMetaTag = CommonUtils.removeSubSupIfBalanced(descriptionMetaTag);
        descriptionMetaTag = CommonUtils.htmlEscape(descriptionMetaTag);
        return descriptionMetaTag;
    }

    public void setDescriptionMetaTag(String descriptionMetaTag)
    {
        this.descriptionMetaTag = descriptionMetaTag;
    }



    public int getNumberOfRelations()
    {
        if(getRelations()!=null)
        {
            return getRelations().size();
        }
        else return 0;

    }

    public void setValidationReport(ValidationReportVO validationReport) {
        this.validationReport = validationReport;
    }

    public ValidationReportVO getValidationReport() {
        return validationReport;
    }



}
