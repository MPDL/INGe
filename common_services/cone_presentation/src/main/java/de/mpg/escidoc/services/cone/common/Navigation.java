package de.mpg.escidoc.services.cone.common;

import javax.faces.component.html.HtmlCommandLink;

public class Navigation
{
    private HtmlCommandLink lnkHome = new HtmlCommandLink();
    private HtmlCommandLink lnkJournalNames = new HtmlCommandLink();
    private HtmlCommandLink lnkPersonNames = new HtmlCommandLink();
    private HtmlCommandLink lnkOrgUnitNames = new HtmlCommandLink();
    private HtmlCommandLink lnkJournalSearch = new HtmlCommandLink();
    private HtmlCommandLink lnkJournalAdd = new HtmlCommandLink();
    private HtmlCommandLink lnkJournalEdit = new HtmlCommandLink();
    
    public Navigation()
    {
    }

    public void init()
    {
    }

    public String loadJournalNames()
    {
        return "loadJournalNames";
    }
    public String loadPersonNames()
    {
        return "loadPersonNames";
    }
    public String loadOrgUnitNames()
    {
        return "loadOrgUnitNames";
    }
    public String loadHome()
    {
        return "loadHome";
    }
    public String loadJournalSearch()
    {
        return "loadJournalSearch";
    }
    public String loadJournalAdd()
    {
        return "loadJournalAdd";
    }
    public String loadJournalEdit()
    {
        return "loadJournalEdit";
    }

    public HtmlCommandLink getLnkHome()
    {
        return lnkHome;
    }

    public void setLnkHome(HtmlCommandLink lnkHome)
    {
        this.lnkHome = lnkHome;
    }

    public HtmlCommandLink getLnkJournalNames()
    {
        return lnkJournalNames;
    }

    public void setLnkJournalNames(HtmlCommandLink lnkJournalNames)
    {
        this.lnkJournalNames = lnkJournalNames;
    }

    public HtmlCommandLink getLnkPersonNames()
    {
        return lnkPersonNames;
    }

    public void setLnkPersonNames(HtmlCommandLink lnkPersonNames)
    {
        this.lnkPersonNames = lnkPersonNames;
    }

    public HtmlCommandLink getLnkOrgUnitNames()
    {
        return lnkOrgUnitNames;
    }

    public void setLnkOrgUnitNames(HtmlCommandLink lnkOrgUnitNames)
    {
        this.lnkOrgUnitNames = lnkOrgUnitNames;
    }

    public HtmlCommandLink getLnkJournalSearch()
    {
        return lnkJournalSearch;
    }

    public void setLnkJournalSearch(HtmlCommandLink lnkJournalSearch)
    {
        this.lnkJournalSearch = lnkJournalSearch;
    }

    public HtmlCommandLink getLnkJournalAdd()
    {
        return lnkJournalAdd;
    }

    public void setLnkJournalAdd(HtmlCommandLink lnkJournalAdd)
    {
        this.lnkJournalAdd = lnkJournalAdd;
    }

    public HtmlCommandLink getLnkJournalEdit()
    {
        return lnkJournalEdit;
    }

    public void setLnkJournalEdit(HtmlCommandLink lnkJournalEdit)
    {
        this.lnkJournalEdit = lnkJournalEdit;
    }
    
}

