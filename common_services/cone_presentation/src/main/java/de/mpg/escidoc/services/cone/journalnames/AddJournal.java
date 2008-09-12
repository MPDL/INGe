package de.mpg.escidoc.services.cone.journalnames;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

public class AddJournal
{
    private String issn2add, title2add, subject2add, publisher2add, rights2add;
    private List<String> altTitles2add;
    private String old_altTitle, new_altTitle;
    private JournalNameAR jnar2add;
    private JournalDBAO jdao;
    // private static final Logger log = Logger.getLogger(AddJournal.class);
    
    public AddJournal()
    {}
    
    public void addNewJournal()
    {
        jdao = new JournalDBAO();
        String newId = jdao.getNewId();
        jnar2add = new JournalNameAR(newId, this.getIssn2add(), this.getPublisher2add(), this.getRights2add(), this.getSubject2add(), this.getTitle2add(), this.getAltTitles2add());
        
        jdao.addJournalNamesAuthorityRecord(jnar2add);
    }

    public List<String> getAltTitles2add()
    {
        if (altTitles2add == null)
        {
            altTitles2add = new ArrayList<String>();
        }
        return altTitles2add;
    }

    public void setAltTitles2add(List<String> altTitles2add)
    {
        this.altTitles2add = altTitles2add;
    }

    public String getIssn2add()
    {
        return issn2add;
    }

    public void setIssn2add(String issn2add)
    {
        this.issn2add = issn2add;
    }

    public JournalNameAR getJnar2add()
    {
        return jnar2add;
    }

    public void setJnar2add(JournalNameAR jnar2add)
    {
        this.jnar2add = jnar2add;
    }

    public String getPublisher2add()
    {
        return publisher2add;
    }

    public void setPublisher2add(String publisher2add)
    {
        this.publisher2add = publisher2add;
    }

    public String getRights2add()
    {
        return rights2add;
    }

    public void setRights2add(String rights2add)
    {
        this.rights2add = rights2add;
    }

    public String getSubject2add()
    {
        return subject2add;
    }

    public void setSubject2add(String subject2add)
    {
        this.subject2add = subject2add;
    }

    public String getTitle2add()
    {
        return title2add;
    }

    public void setTitle2add(String title2add)
    {
        this.title2add = title2add;
    }
    
    public String getNew_altTitle()
    {
        return new_altTitle;
    }

    public void setNew_altTitle(String new_altTitle)
    {
        this.new_altTitle = new_altTitle;
    }

    public String getOld_altTitle()
    {
        return old_altTitle;
    }

    public void setOld_altTitle(String old_altTitle)
    {
        this.old_altTitle = old_altTitle;
    }

    public void addAltTitle()
    {
        altTitles2add.add("");
    }
    
    public void inputChanged(ValueChangeEvent event)
    {
        setNew_altTitle(event.getNewValue().toString());
        setOld_altTitle(event.getOldValue().toString());
        // System.out.println("old: " + old_altTitle + " new: " + new_altTitle);
        int index = altTitles2add.indexOf(old_altTitle);
        altTitles2add.add(index, new_altTitle);
        altTitles2add.remove(old_altTitle);
    }
}
