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

package de.mpg.escidoc.services.cone.journalnames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

public class JournalSearchBean
{
    private List<ArrayList<String>> journalList;
    private List<String> rightsList;
    private ArrayList rowData;
    private String selectedTitle;
    private String selectedRights;
    private String selectedSubject;
    private String searchString;
    private String issn, title, subject;
    private JournalDBAO jdao;
    private JournalNameAR jnar;
    private HtmlDataTable journalTable;
    private HtmlPanelGrid journalDetails;
    private UIColumn journalIssnColumn;
    private UIColumn journalTitleColumn;
    private int selectedRowId = -1;
    private Collection<SelectItem> rightsItems;
    private static final Logger log = Logger.getLogger(JournalSearchBean.class);
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");
    
    public String getIssn()
    {
        return issn;
    }

    public void setIssn(String identifier)
    {
        this.issn = identifier;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSelectedTitle()
    {
        return selectedTitle;
    }

    public void setSelectedTitle(String journalTitle)
    {
        this.selectedTitle = journalTitle;
    }

    public String getSearchString()
    {
        return searchString;
    }

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public List<ArrayList<String>> getJournalList()
    {
        return journalList;
    }
    
    public int getRowCount()
    {
        return journalList.size();
    }

    public HtmlDataTable getJournalTable()
    {
        return journalTable;
    }

    public void setJournalTable(HtmlDataTable journalTable)
    {
        this.journalTable = journalTable;
    }

    public HtmlPanelGrid getJournalDetails()
    {
        return journalDetails;
    }

    public void setJournalDetails(HtmlPanelGrid journalDetails)
    {
        this.journalDetails = journalDetails;
    }

    public JournalNameAR getJnar()
    {
        return jnar;
    }
    
    public void setJnar(JournalNameAR jnar)
    {
        this.jnar = jnar;
    }
    
    public UIColumn getJournalIssnColumn()
    {
        return journalIssnColumn;
    }

    public void setJournalIssnColumn(UIColumn journalIssnColumn)
    {
        this.journalIssnColumn = journalIssnColumn;
    }
    
    public UIColumn getJournalTitleColumn()
    {
        return journalTitleColumn;
    }

    public void setJournalTitleColumn(UIColumn journalTitleColumn)
    {
        this.journalTitleColumn = journalTitleColumn;
    }

    public int getSelectedRowId()
    {
        return selectedRowId;
    }

    public void setSelectedRowId(int selectedRowId)
    {
        this.selectedRowId = selectedRowId;
    }

    public ArrayList getRowData()
    {
        return rowData;
    }

    public void setRowData(ArrayList rowData)
    {
        this.rowData = rowData;
    }

    public String getSelectedRights()
    {
        return selectedRights;
    }

    public void setSelectedRights(String selectedRights)
    {
        this.selectedRights = selectedRights;
    }

    public String getSelectedSubject()
    {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject)
    {
        this.selectedSubject = selectedSubject;
    }

    public Collection<SelectItem> getRightsItems()
    {
        jdao = new JournalDBAO();
        rightsList = jdao.populateRightsList();
        rightsItems = new ArrayList<SelectItem>();
        rightsItems.add(new SelectItem(""));
        if (rightsList != null)
        {
            for(int i = 0; i < rightsList.size(); i++)
            {
                rightsItems.add(new SelectItem(rightsList.get(i)));
            }
        }
        
        return rightsItems;
    }

    public void setRightsItems(Collection<SelectItem> rightsItems)
    {
        this.rightsItems = rightsItems;
    }

    public void findJournals()
    {
        selectedRowId = -1;
        jdao = new JournalDBAO();
        if ((issn != null && issn != "") && (title != null && title != ""))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "You either may search by ISSN OR by Title"));
        }
        else if ((issn == null || issn == "") && (title == null || title == ""))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Please enter ISSN or Title"));
        }
        else
        {
            if (issn != null && issn !="")
            {
                searchString = "select $s $o from " + mts.getString("journal.model") +
                " where $s "+mts.getString("dc.title.urn")+" $o and $s "+mts.getString("dc.identifier.urn")+" '"+issn+"' " +
                "in "+ mts.getString("journal.fulltext") +";";
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "searching for ISSN ", searchString));
            }
            if (title != null && title !="")
            {
                searchString = "select $s $o from " + mts.getString("journal.model") +
                " where $s "+mts.getString("dc.title.urn")+" $o and $s "+mts.getString("dc.title.urn")+" '"+title+"' " +
                "in "+ mts.getString("journal.fulltext") +";";
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "searching for Title ", searchString));
            }
            if (selectedRights != null && selectedRights != "")
            {
                searchString = searchString.substring(0, searchString.length() -1).concat(" and $s "+mts.getString("dc.rights.urn")+" '"+selectedRights+"' " +
                        "in "+ mts.getString("journal.fulltext") +";");
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "searching for ISSN/Title AND Rights", searchString));
            }
            if (subject != null && subject != "")
            {
                searchString = searchString.substring(0, searchString.length() -1).concat(" and $s "+mts.getString("dc.subject.urn")+" '"+subject+"' " +
                        "in "+ mts.getString("journal.fulltext") +";");
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "searching for ISSN/Title AND Rights AND Subject", searchString));
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Final searchString: "+searchString, ""));
            journalList = jdao.getJournalList(searchString);
        }
    }

    public void getDetails()
    {
        try
        {
            selectedRowId = journalTable.getRowIndex();
            rowData = (ArrayList)journalTable.getRowData();
            selectedTitle = rowData.get(0).toString();
            jdao = new JournalDBAO();
            jnar = jdao.getJournalRecordDetails(selectedTitle);
            if(jnar != null)
            {
                log.info("Successfully retrieved details for "+selectedTitle);
            }
            else
            {
                log.info("jnar is null");
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    public void changedRights(ValueChangeEvent vce)
    {
        this.setSelectedRights((String)vce.getNewValue());
        System.out.println("selected rights: " + vce.getNewValue().toString());
    }
    
    public List completeSubjectSuggestions(Object o)
    {
        jdao = new JournalDBAO();
        String subject = o.toString();
        String query = "select $o from "+mts.getString("journal.model")+" where $s "+mts.getString("dc.subject.urn")+" $o and $s "+mts.getString("dc.subject.urn")+" '"+subject+"' in "+
        mts.getString("journal.fulltext")+";";
        List<String> result = new ArrayList<String>();
        result = jdao.populateSuggestionList(query);
        return result;
    }
    
    public List completeTitleSuggestions(Object o)
    {
        jdao = new JournalDBAO();
        String title = o.toString();
        String query = "select $o from "+mts.getString("journal.model")+" where $s "+mts.getString("dc.title.urn")+" $o and $s "+mts.getString("dc.title.urn")+" '"+title+"' in "+
        mts.getString("journal.fulltext")+";";
        List<String> result = new ArrayList<String>();
        result = jdao.populateSuggestionList(query);
        return result;
    }
}
