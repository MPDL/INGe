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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.browseBy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.BrowseByPage;
import de.mpg.escidoc.pubman.BrowseByPage.LinkVO;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

/**
 * 
 * Session Bean for Browse By
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BrowseBySessionBean extends FacesBean
{
    public static final String BEAN_NAME = "BrowseBySessionBean";
    private final int maxDisplay = 100;

    private static Logger logger = Logger.getLogger(BrowseBySessionBean.class);
    
    private boolean showChars = true;
    List <LinkVO> searchResults;
    List <String> allResults;
    private String currentCharacter = "A";
    private String selectedValue ="persons";
    private String searchIndex = MetadataSearchCriterion.getINDEX_PERSON_IDENTIFIER();
    private int yearStart;
    private String query ="q";
    private String dateType = "published";
    private String pubContentModel = "";

    private List<String> browseByYears;
    private HtmlSelectOneRadio dateSelect = new HtmlSelectOneRadio();
    private SelectItem[] DATE_OPTIONS;


    /**
     * Public constructor.
     */
    public BrowseBySessionBean()
    {
        try
        {
            this.pubContentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
            ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("BrowseBy");
        }
        catch (Exception e)
        {
            this.logger.warn("Could not read property content model.", e);
        }
    }

    public void clear()
    {
        this.currentCharacter ="A";
        this.selectedValue ="persons";
        this.searchIndex = MetadataSearchCriterion.getINDEX_PERSON_IDENTIFIER();
        this.showChars = true;
        this.query = "q";
        this.dateType = "published";
    }

    public List<String> getControlledVocabs()
    {
        List <String> vocabs = new ArrayList<String>();
        try
        {    
            String vocabsStr = PropertyReader.getProperty("escidoc.cone.subjectVocab");
            String [] vocabsArr = vocabsStr.split(";");
            for (int i = 0; i< vocabsArr.length; i++)
            {
                vocabs.add(vocabsArr[i].trim());
            }
        }
        catch(Exception e)
        {
            this.logger.error("Could not read Property: 'escidoc.cone.subjectVocab'", e);
        }
        return vocabs;
    }
    
    public List<LinkVO> getSearchResults()
    {
        return this.searchResults;
    }

    public void setSearchResults(List<LinkVO> searchResults)
    {
        this.searchResults = searchResults;
    }

    public String getCurrentCharacter()
    {
        return currentCharacter;
    }

    public void setCurrentCharacter(String currentCharacter)
    {
        this.currentCharacter = currentCharacter;
    }
    
    public String getSelectedValue()
    {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue)
    {
        this.selectedValue = selectedValue;
    }
    
    public int getMaxDisplay()
    {
        return maxDisplay;
    }
    
    /**
     * This method checks weather the searchResult from CoNE has to be
     * devided into character, according to the value of 'maxDisplay'
     * @return
     */
    public boolean isShowChars()
    {
        return this.showChars;
    }
    
    public void setShowChars()
    {
        if (!this.selectedValue.equals("year"))
        {
            if (this.getConeAll().size() > this.getMaxDisplay())
            {
                this.showChars = true;
            }
            else this.showChars = false;
        }
    }
    
    /**
     * Call the cone service to retrive all browse by values.
     * @param type, type of the cone request (persons, subjects, journals)
     * @return
     */
    public List<LinkVO> getConeAll ()
    {
        List<LinkVO> links = new ArrayList<LinkVO>();
        BrowseByPage bbPage = new BrowseByPage();
        
        try
        {
            URL coneUrl = new URL (PropertyReader.getProperty("escidoc.cone.service.url") +this.selectedValue + "/all?format=options");           
            URLConnection conn = coneUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            
            switch (responseCode)
            {
                case 200:
                    logger.debug("Cone Service responded with 200.");
                    break;
                default:
                    throw new RuntimeException("An error occurred while calling Cone Service: "
                            + responseCode + ": " + httpConn.getResponseMessage());
            }

            InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
            BufferedReader bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
                String[] parts = line.split("\\|");
                if (parts.length == 2)
                {
                    LinkVO link = bbPage.new LinkVO(parts[1], parts[0]);
                    links.add(link);
                }
            }
            isReader.close();
            httpConn.disconnect();
            
        }
        catch (Exception e)
        {
            this.logger.warn("An error occurred while calling the Cone service.",e);
            return null;
        }
        return links;
    }
    
    
    public String getSearchIndex()
    {
        return searchIndex;
    }

    public void setSearchIndex(String searchIndex)
    {
        this.searchIndex = searchIndex;
    }

    public int getYearStart()
    {
        return this.yearStart;
    }

    public void setYearStart(int year)
    {
        this.yearStart = year;
    }
    
    public List<String> getYearRange()
    {
        List<String> years = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int yearTmp = currentYear;

        while (this.getYearStart() <= yearTmp)
        {
            years.add(yearTmp + "");
            yearTmp--;
        }
        
        return years;
    }
    
    /**
     * Searches the rep for the oldest year.
     */
    public void setYearPublished()
    {     
        int yearPublishedPrint = -1;
        int yearPublishedOnline = -1;

        yearPublishedPrint = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_ISSUED(), "print");
        yearPublishedOnline = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_PUBLISHED_ONLINE(), "online");

        if ((yearPublishedPrint == -1))
        {
            this.yearStart = yearPublishedOnline;
        }
        else 
        {
            if ((yearPublishedOnline == -1))
            {
                this.yearStart = yearPublishedPrint;
            }
            else if (yearPublishedPrint < yearPublishedOnline)
                {
                   this.yearStart = yearPublishedPrint;
                }
                else
                {
                    this.yearStart = yearPublishedOnline;
                }
        }

    }
    
    public void setYearStartAny()
    {
        List years = new ArrayList();
        int yearPublishedPrint = -1;
        int yearPublishedOnline =-1;
        int yearAccepted =-1;
        int yearSubmitted =-1;
        int yearModified =-1;
        int yearCreated =-1;
        
        int oldestYear =-1;

        yearPublishedPrint = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_ISSUED(), "print");
        years.add(yearPublishedPrint);
        yearPublishedOnline = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_PUBLISHED_ONLINE(), "online");
        years.add(yearPublishedOnline);
        yearAccepted = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_ACCEPTED(), "accepted");
        years.add(yearAccepted);
        yearSubmitted = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_SUBMITTED(), "submitted");
        years.add(yearSubmitted);
        yearModified = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_MODIFIED(), "modified");
        years.add(yearModified);
        yearCreated = this.getOldestYear(MetadataSearchCriterion.getINDEX_DATE_CREATED(), "created");
        years.add(yearCreated);

        for (int i =0; i<years.size(); i++)
        {
            int tmp = Integer.parseInt(years.get(i)+"");
            if (oldestYear == -1 && tmp != -1)
            {
                oldestYear = tmp;
            }
            if (oldestYear != -1 && tmp != -1 && oldestYear > tmp)
            {
                oldestYear = tmp;
            }
        }
        this.yearStart = oldestYear;
    }
    
    private int getOldestYear (String index, String type)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        PubItemVOPresentation item;   
        String yearStr = "";
        int year = -1;
        
        PlainCqlQuery query = new PlainCqlQuery("escidoc.content-model.objid="+this.getPubContentModel()+" and "+ index +" > ''");
        query.setSortKeysAndOrder("sort." + index, SortingOrder.ASCENDING);
        query.setStartRecord("1");
        query.setMaximumRecords("1");
        
        ItemContainerSearchResult result;
        try
        {
            InitialContext ic = new InitialContext();
            Search search = (Search) ic.lookup(Search.SERVICE_NAME);
            
            result = search.searchForItemContainer(query);
            pubItemList =  extractItemsOfSearchResult(result);            
            
            item = pubItemList.get(0);
            if (type.equals("print"))
            {
                yearStr = item.getMetadata().getDatePublishedInPrint();
            }
            if (type.equals("online"))
            {
                yearStr = item.getMetadata().getDatePublishedOnline();
            }
            if (type.equals("created"))
            {
                yearStr = item.getMetadata().getDateCreated();
            }
            if (type.equals("accepted"))
            {
                yearStr = item.getMetadata().getDateAccepted();
            }
            if (type.equals("modified"))
            {
                yearStr = item.getMetadata().getDateModified();
            }
            if (type.equals("submitted"))
            {
                yearStr = item.getMetadata().getDateSubmitted();
            }
            if (yearStr != null)
            {
                //Take only first part of date string = year.
                String [] yearArr = yearStr.split("-");
                if (yearArr.length > 1)
                {
                    year = Integer.parseInt(yearArr[0]);
                }
                else
                {
                    year = Integer.parseInt(yearStr);
                }
            }

        }
        catch (Exception e)
        {
            logger.warn("Error computing starting year.", e);
        }      
        return year;
    }
    
    /**
     * Helper method that transforms the result of the search into a list of PubItemVOPresentation objects.
     * @param result
     * @return
     */
    private ArrayList<PubItemVOPresentation> extractItemsOfSearchResult( ItemContainerSearchResult result ) { 
        
        List<SearchResultElement> results = result.getResultList();
        
        ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        for( int i = 0; i < results.size(); i++ ) {
            //check if we have found an item
            if( results.get( i ) instanceof ItemResultVO ) {
                // cast to PubItemResultVO
                ItemResultVO item = (ItemResultVO)results.get( i );
                PubItemResultVO pubItemResult = new PubItemResultVO( item, item.getSearchHitList() ) ;
                PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);
                pubItemList.add( pubItemPres );
            }
        }
        return pubItemList;
    }
    

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }
    
    
    public List<String> getBrowseByYears()
    {
        return browseByYears;
    }

    public void setBrowseByYears(List<String> browseByYears)
    {
        this.browseByYears = browseByYears;
    }

    public HtmlSelectOneRadio getDateSelect()
    {
        return dateSelect;
    }

    public void setDateSelect(HtmlSelectOneRadio dateSelect)
    {
        this.dateSelect = dateSelect;
    }

    public SelectItem[] getDATE_OPTIONS()
    {
        return DATE_OPTIONS;
    }

    public void setDATE_OPTIONS(SelectItem[] date_options)
    {
        DATE_OPTIONS = date_options;
    }
    
    
    public String getDateType()
    {
        return dateType;
    }

    public void setDateType(String dateType)
    {
        this.dateType = dateType;
    }

    public String getPubContentModel()
    {
        return pubContentModel;
    }

}
