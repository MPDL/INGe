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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

public class EditJournal
{
    private Logger log = Logger.getLogger(EditJournal.class);
    
    private String issn2edit, title2edit, subject2edit, publisher2edit, rights2edit;
    private List<String> altTitles2edit;
    private List<String> oldAltTitles;
    private String old_altTitle, new_altTitle;
    private JournalNameAR jnar2edit;
    private JournalDBAO jdao;
    // private HtmlDataTable altTitlesTable;
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");

    public JournalNameAR getJnar2edit()
    {
        return jnar2edit;
    }

    public void setJnar2edit(JournalNameAR jnar2edit)
    {
        this.jnar2edit = jnar2edit;
    }

    public void action(ActionEvent ae)
    {
        // log.debug("issn " + issn2edit);
        // log.debug("alt titles from obj " + jnar2edit.getAltTitles());
        if (altTitles2edit != null && altTitles2edit.size() > 0)
        {
            // log.debug("alt titles from List " + altTitles2edit);
            oldAltTitles = new ArrayList<String>();
            // log.debug("size of old list "+oldAltTitles.size());
            oldAltTitles.addAll(altTitles2edit);
            // log.debug("old alt titles in list " + oldAltTitles);
        }
        else
        {
            oldAltTitles = new ArrayList<String>();
        }
    }

    public List<String> getAltTitles2edit()
    {
        return altTitles2edit;
    }

    public void setAltTitles2edit(List<String> altTitles2edit)
    {
        this.altTitles2edit = altTitles2edit;
    }

    public List<String> getOldAltTitles()
    {
        return oldAltTitles;
    }

    public void setOldAltTitles(List<String> oldAltTitles)
    {
        this.oldAltTitles = oldAltTitles;
    }

    public String getIssn2edit()
    {
        return issn2edit;
    }

    public void setIssn2edit(String issn2edit)
    {
        this.issn2edit = issn2edit;
    }

    public String getPublisher2edit()
    {
        return publisher2edit;
    }

    public void setPublisher2edit(String publisher2edit)
    {
        this.publisher2edit = publisher2edit;
    }

    public String getRights2edit()
    {
        return rights2edit;
    }

    public void setRights2edit(String rights2edit)
    {
        this.rights2edit = rights2edit;
    }

    public String getSubject2edit()
    {
        return subject2edit;
    }

    public void setSubject2edit(String subject2edit)
    {
        this.subject2edit = subject2edit;
    }

    public String getTitle2edit()
    {
        return title2edit;
    }

    public void setTitle2edit(String title2edit)
    {
        this.title2edit = title2edit;
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

    /*
     * public HtmlDataTable getAltTitlesTable() { return altTitlesTable; } public void setAltTitlesTable(HtmlDataTable
     * altTitlesTable) { this.altTitlesTable = altTitlesTable; }
     */
    public void update()
    {
        String id = "<" + jnar2edit.getId() + ">";
        if (title2edit.equals(jnar2edit.getTitle()))
        {
            log.debug("no need to update title: " + title2edit);
        }
        else
        {
            log.debug("updating " + id + " with new title " + title2edit);
            jdao = new JournalDBAO();
            String delete = "delete " + id + " " + mts.getString("dc.title.urn") + " '" + jnar2edit.getTitle()
                    + "' from " + mts.getString("journal.model") + "; ";
            String insert = "insert " + id + " " + mts.getString("dc.title.urn") + " '" + title2edit
                    + "' into " + mts.getString("journal.model") + ";";
            String delete_lucene = "delete " + id + " " + mts.getString("dc.title.urn") + " '"
                    + jnar2edit.getTitle() + "' from " + mts.getString("journal.fulltext") + "; ";
            String insert_lucene = "insert " + id + " " + mts.getString("dc.title.urn") + " '"
                    + title2edit + "' into " + mts.getString("journal.fulltext") + ";";
            jdao.updateJournalNamesAR(delete, delete_lucene);
            jdao.updateJournalNamesAR(insert, insert_lucene);
        }
        if (issn2edit.equals(jnar2edit.getIdentifier()))
        {
            log.debug("no need to update issn: " + issn2edit);
        }
        else
        {
            log.debug("updating " + id + " with new issn " + issn2edit);
            jdao = new JournalDBAO();
            String delete = "delete " + id + " " + mts.getString("dc.identifier.urn") + " '" + jnar2edit.getTitle()
                    + "' from " + mts.getString("journal.model") + "; ";
            String insert = "insert " + id + " " + mts.getString("dc.identifier.urn") + " '" + issn2edit
                    + "' into " + mts.getString("journal.model") + ";";
            String delete_lucene = "delete " + id + " " + mts.getString("dc.identifier.urn") + " '"
                    + jnar2edit.getTitle() + "' from " + mts.getString("journal.fulltext") + "; ";
            String insert_lucene = "insert " + id + " " + mts.getString("dc.identifier.urn") + " '"
                    + issn2edit + "' into " + mts.getString("journal.fulltext") + ";";
            jdao.updateJournalNamesAR(delete, delete_lucene);
            jdao.updateJournalNamesAR(insert, insert_lucene);
        }
        if (subject2edit.equals(jnar2edit.getSubject()))
        {
            log.debug("no need to update subject: " + subject2edit);
        }
        else
        {
            log.debug("updating " + id + " with new subject " + subject2edit);
            jdao = new JournalDBAO();
            
            String delete = "delete " + id + " " + mts.getString("dc.subject.urn") + " '" + jnar2edit.getSubject()
                    + "' from " + mts.getString("journal.model") + "; ";
            String insert = "insert " + id + " " + mts.getString("dc.subject.urn") + " '" + subject2edit
                    + "' into " + mts.getString("journal.model") + ";";
            String delete_lucene = "delete " + id + " " + mts.getString("dc.subject.urn") + " '"
                    + jnar2edit.getSubject() + "' from " + mts.getString("journal.fulltext") + "; ";
            String insert_lucene = "insert " + id + " " + mts.getString("dc.subject.urn") + " '"
                    + subject2edit + "' into " + mts.getString("journal.fulltext") + ";";
            jdao.updateJournalNamesAR(delete, delete_lucene);
            jdao.updateJournalNamesAR(insert, insert_lucene);
        }
        if (publisher2edit.equals(jnar2edit.getPublisher()))
        {
            log.debug("no need to update publisher: " + publisher2edit);
        }
        else
        {
            log.debug("updating " + id + " with new publisher " + publisher2edit);
            jdao = new JournalDBAO();
            
            String delete = "delete " + id + " " + mts.getString("dc.publisher.urn") + " '"
                    + jnar2edit.getPublisher() + "' from " + mts.getString("journal.model") + "; ";
            String insert = "insert " + id + " " + mts.getString("dc.publisher.urn") + " '"
                    + publisher2edit + "' into " + mts.getString("journal.model") + ";";
            String delete_lucene = "delete " + id + " " + mts.getString("dc.publisher.urn") + " '"
                    + jnar2edit.getPublisher() + "' from " + mts.getString("journal.fulltext") + "; ";
            String insert_lucene = "insert " + id + " " + mts.getString("dc.publisher.urn") + " '"
                    + publisher2edit + "' into " + mts.getString("journal.fulltext") + ";";
            jdao.updateJournalNamesAR(delete, delete_lucene);
            jdao.updateJournalNamesAR(insert, insert_lucene);
        }
        if (rights2edit.equals(jnar2edit.getRights()))
        {
            log.debug("no need to update rights: " + rights2edit);
        }
        else
        {
            log.debug("updating " + id + " with new rights " + rights2edit);
            jdao = new JournalDBAO();
            
            String delete = "delete " + id + " " + mts.getString("dc.rights.urn") + " '" + jnar2edit.getRights()
                    + "' from " + mts.getString("journal.model") + "; ";
            String insert = "insert " + id + " " + mts.getString("dc.rights.urn") + " '" + rights2edit
                    + "' into " + mts.getString("journal.model") + ";";
            String delete_lucene = "delete " + id + " " + mts.getString("dc.rights.urn") + " '"
                    + jnar2edit.getRights() + "' from " + mts.getString("journal.fulltext") + "; ";
            String insert_lucene = "insert " + id + " " + mts.getString("dc.rights.urn") + " '"
                    + rights2edit + "' into " + mts.getString("journal.fulltext") + ";";
            jdao.updateJournalNamesAR(delete, delete_lucene);
            jdao.updateJournalNamesAR(insert, insert_lucene);
        }
        if (altTitles2edit != null && altTitles2edit.size() > 0)
        {
            log.debug("alt titles from list: " + altTitles2edit);
            // log.debug("alt titles from object: " + jnar2edit.getAltTitles());
            log.debug("alt titles in old list: " + oldAltTitles);
            for (int row = 0; row < oldAltTitles.size(); row++)
            {
                String modelData = altTitles2edit.get(row);
                if (modelData.equals(oldAltTitles.get(row)))
                {
                    log.debug("no need to update alternative : " + oldAltTitles.get(row) + " with "
                            + modelData);
                }
                else
                {
                    if (modelData == "")
                    {
                        log.debug("removing " + oldAltTitles.get(row) + " from " + id);
                        jdao = new JournalDBAO();
                        
                        String delete = "delete " + id + " " + mts.getString("dc.alternative.urn") + " '"
                        + oldAltTitles.get(row) + "' from " + mts.getString("journal.model") + "; ";
                        String delete_lucene = "delete " + id + " " + mts.getString("dc.alternative.urn") + " '"
                        + oldAltTitles.get(row) + "' from " + mts.getString("journal.fulltext") + "; ";
                        jdao.updateJournalNamesAR(delete, delete_lucene);
                    }
                    else
                    {
                        log.debug("updating " + id + " with new alt title " + modelData);
                        jdao = new JournalDBAO();
                        
                        String delete = "delete " + id + " " + mts.getString("dc.alternative.urn") + " '"
                                + oldAltTitles.get(row) + "' from " + mts.getString("journal.model") + "; ";
                        String insert = "insert " + id + " " + mts.getString("dc.alternative.urn") + " '"
                                + modelData + "' into " + mts.getString("journal.model") + ";";
                        String delete_lucene = "delete " + id + " " + mts.getString("dc.alternative.urn") + " '"
                                + oldAltTitles.get(row) + "' from " + mts.getString("journal.fulltext") + "; ";
                        String insert_lucene = "insert " + id + " "
                                + mts.getString("dc.alternative.urn") + " '" + modelData + "' into "
                                + mts.getString("journal.fulltext") + ";";
                        jdao.updateJournalNamesAR(delete, delete_lucene);
                        jdao.updateJournalNamesAR(insert, insert_lucene);
                    }
                }
            }
            if (oldAltTitles.size() >= 0 && altTitles2edit.size() > oldAltTitles.size())
            {
                for (int row = oldAltTitles.size(); row < altTitles2edit.size(); row++)
                {
                    String modelData = altTitles2edit.get(row);
                    if (modelData == "")
                    {
                        log.debug("empty alt title will NOT be added");
                    }
                    else
                    {
                        log.debug("adding new alt Title " + modelData + " to " + issn2edit);
                        jdao = new JournalDBAO();
                        
                        String insert = "insert " + id + " " + mts.getString("dc.alternative.urn") + " '"
                                + modelData + "' into " + mts.getString("journal.model") + ";";
                        String insert_lucene = "insert " + id + " " + mts.getString("dc.alternative.urn") + " '"
                                + modelData + "' into " + mts.getString("journal.fulltext") + ";";
                        jdao.updateJournalNamesAR(insert, insert_lucene);
                    }
                }
            }
        }
    }

    public void inputChanged(ValueChangeEvent event)
    {
        setNew_altTitle(event.getNewValue().toString());
        setOld_altTitle(event.getOldValue().toString());
        // log.debug("old: " + old_altTitle + " new: " + new_altTitle);
        int index = altTitles2edit.indexOf(old_altTitle);
        altTitles2edit.add(index, new_altTitle);
        altTitles2edit.remove(old_altTitle);
    }

    public void addAltTitle()
    {
        if (altTitles2edit == null)
        {
            altTitles2edit = new ArrayList<String>();
        }
        altTitles2edit.add("");
    }
}
