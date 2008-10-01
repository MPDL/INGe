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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.cone.journalnames;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.mulgara.itql.ItqlInterpreterBean;
import org.mulgara.itql.ItqlInterpreterException;
import org.mulgara.query.Answer;
import org.mulgara.query.TuplesException;

public class JournalDBAO
{
    private ArrayList<String> altTitles;
    private ArrayList<ArrayList<String>> journalList;
    private ArrayList<String> issnTitle;
    ItqlInterpreterBean interpreter;
    Answer answer;
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");
    private static final Logger log = Logger.getLogger(JournalDBAO.class);

    public JournalDBAO()
    {}

    public void done()
    {
        try
        {
            interpreter.close();
            interpreter = null;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error closing connection to MTS server"));
        }
    }

    public JournalNameAR getJournalRecordDetails(String id)
    {
        interpreter = new ItqlInterpreterBean();
        JournalNameAR jnar = new JournalNameAR(id);
        altTitles = new ArrayList<String>();
        try
        {
            String query = "select $p $o from " + mts.getString("journal.model")
                    + " where <" + id + "> $p $o order by $p;";
            
            log.debug("Query: " + query);
            
            answer = interpreter.executeQuery(query);
            if (answer.getRowCount() > 0)
            {
                while (answer.next())
                {
                    String predicate = answer.getObject(0).toString();
                    String object = answer.getObject(1).toString();
                    object = object.substring(1, object.length() - 1);
                    jnar.addProperty(predicate, object);
                }

                return jnar;
            }
            else
            {
                log.info("querying for " + id + " did not return any result!");
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "", "querying for " + id
                                + " did not return any result!"));
            }
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage());
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Could not retrieve details for " + id));
        }
        catch (TuplesException te)
        {
            log.error(te.getMessage());
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, te.getMessage(), "Could not retrieve details for " + id));
        }
        finally
        {
            altTitles = null;
            jnar = null;
            done();
        }
        return null;
    }

    public List<ArrayList<String>> getJournalList(String query)
    {
        
        log.debug("Query: " + query);
        
        interpreter = new ItqlInterpreterBean();
        journalList = new ArrayList<ArrayList<String>>();
        try
        {
            answer = interpreter.executeQuery(query);
            if (answer.getRowCount() > 0)
            {
                while (answer.next())
                {
                    String a0 = answer.getObject(0).toString();
                    String a1 = trimQuotes(answer.getObject(1).toString());
                    issnTitle = new ArrayList<String>();
                    issnTitle.add(a0);
                    issnTitle.add(a1);
                    journalList.add(issnTitle);
                }
                return journalList;
            }
            else
            {
                log.info("query " + query + " did not return any result!");
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, query, "no result(s) returned!"));
            }
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage(), iie);
            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Error getting journal list"));
        }
        catch (TuplesException te)
        {
            log.error(te.getMessage(), te);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, te.getMessage(), "Error getting journal list"));
        }
        finally
        {
            journalList = null;
            issnTitle = null;
            done();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void addJournalNamesAuthorityRecord(JournalNameAR jnar)
    {
        interpreter = new ItqlInterpreterBean();
        String subject = "<" + jnar.getId() + ">";
        
        try
        {
            String insert = "insert " + subject + " " + mts.getString("dc.identifier.urn") + " '" + jnar.getIdentifier()
                    + "' into " + mts.getString("journal.model") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert ="insert " + subject + " "
                    + mts.getString("dc.publisher.urn") + " '" + jnar.getPublisher() + "' into "
                    + mts.getString("journal.model") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject + " " + mts.getString("dc.rights.urn")
                    + " '" + jnar.getRights() + "' into " + mts.getString("journal.model") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject
                    + " " + mts.getString("dc.subject.urn") + " '" + jnar.getSubject() + "' into "
                    + mts.getString("journal.model") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject + " " + mts.getString("dc.title.urn")
                    + " '" + jnar.getTitle() + "' into " + mts.getString("journal.model") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject
                    + " " + mts.getString("dc.identifier.urn") + " '" + jnar.getIdentifier() + "' into "
                    + mts.getString("journal.fulltext") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject + " "
                    + mts.getString("dc.publisher.urn") + " '" + jnar.getPublisher() + "' into "
                    + mts.getString("journal.fulltext") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject + " "
                    + mts.getString("dc.rights.urn") + " '" + jnar.getRights() + "' into "
                    + mts.getString("journal.fulltext") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject + " " + mts.getString("dc.subject.urn")
                    + " '" + jnar.getSubject() + "' into " + mts.getString("journal.fulltext") + "; ";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            insert = "insert " + subject
                    + " " + mts.getString("dc.title.urn") + " '" + jnar.getTitle() + "' into "
                    + mts.getString("journal.fulltext") + ";";
            log.debug("Query: " + insert);
            interpreter.executeUpdate(insert);
            
            altTitles = new ArrayList<String>(jnar.getAltTitles());
            log.info(interpreter.getLastMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "", interpreter.getLastMessage()));
            if (altTitles.size() > 0)
            {
                for (String s : altTitles)
                {
                    if (s != "")
                    {
                        String insertalt = "insert " + subject + " " + mts.getString("dc.alternative.urn") + " '" + s
                                + "' into " + mts.getString("journal.model") + ";";
                        log.debug("Query: " + insertalt);
                        interpreter.executeUpdate(insertalt);
                        insertalt = "insert " + subject + " "
                                + mts.getString("dc.alternative.urn") + " '" + s + "' into "
                                + mts.getString("journal.fulltext") + ";";
                        log.debug("Query: " + insertalt);
                        interpreter.executeUpdate(insertalt);
                        log.info(interpreter.getLastMessage());
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_INFO, "", interpreter.getLastMessage()));
                    }
                }
            }
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage(), iie);
            // iie.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Error inserting new journal details"));
        }
        finally
        {
            subject = null;
            altTitles = null;
            done();
        }
     }

    public void updateJournalNamesAR(String ins, String ins_luc)
    {
        interpreter = new ItqlInterpreterBean();
        try
        {
            
            log.debug("Query: " + ins);
            log.debug("Query: " + ins_luc);
            
            interpreter.executeUpdate(ins);
            log.info(interpreter.getLastMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, ins, interpreter.getLastMessage()));
            interpreter.executeUpdate(ins_luc);
            log.info(interpreter.getLastMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, ins_luc, interpreter.getLastMessage()));
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage(), iie);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Unable to update Journal Name."));
        }
        finally
        {
            done();
        }
    }

    private String trimQuotes(String withQuotes)
    {
        String noQuotes = withQuotes.substring(1, withQuotes.length() - 1);
        return noQuotes;
    }

    public List<String> populateRightsList()
    {
        interpreter = new ItqlInterpreterBean();
        ArrayList<String> rightsList = new ArrayList<String>();
        try
        {
            String query = "select $o from " + mts.getString("journal.model") + " where $s "
                    + mts.getString("dc.rights.urn") + " $o;";
            
            log.debug("Query: " + query);
            
            answer = interpreter.executeQuery(query);
            if (answer.getRowCount() > 0)
            {
                while (answer.next())
                {
                    rightsList.add(trimQuotes(answer.getObject(0).toString()));
                }
                return rightsList;
            }
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage(), iie);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Error getting rights list."));
        }
        catch (TuplesException te)
        {
            log.error(te.getMessage(), te);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, te.getMessage(), "Error getting rights list."));
        }
        finally
        {
            rightsList = null;
            done();
        }
        return null;
    }
    
    public List<String> populateSuggestionList(String query)
    {
        interpreter = new ItqlInterpreterBean();
        ArrayList<String> subjectsList = new ArrayList<String>();
        try
        {
            answer = interpreter.executeQuery(query);
            if(answer.getRowCount() > 0)
            {
                while(answer.next())
                {
                    subjectsList.add(trimQuotes(answer.getObject(0).toString()));
                }
                return subjectsList;
            }
        }
        catch (ItqlInterpreterException iie)
        {
            log.error(iie.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, iie.getMessage(), "Error getting list of suggestions."));
        }
        catch (TuplesException te)
        {
            log.error(te.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, te.getMessage(), "Error getting list of suggestions."));
        }
        finally
        {
            subjectsList = null;
            done();
        }
        return null;
    }
    
    public boolean checkExistingJournalNameAR(String issn)
    {
        interpreter = new ItqlInterpreterBean();
        String query = "select $s from "+mts.getString("journal.model")+" where $s "+mts.getString("dc.identifier.urn")+" '"+issn+"';";
        try
        {
            answer = interpreter.executeQuery(query);
            if (answer.getRowCount() == 1)
            {
                return true;
            }
        }
        catch (ItqlInterpreterException e)
        {
            e.printStackTrace();
        }
        catch (TuplesException e)
        {
            e.printStackTrace();
        }
        finally
        {
            query = null;
            done();
        }
        return false;
        
    }
    
    public String getNewId()
    {
        interpreter = new ItqlInterpreterBean();
        String query = "select $o from " + mts.getString("journal.model") + " where <urn:cone:jnar:counter> " + mts.getString("dc.identifier.urn") + " $o;";
        try
        {
            answer = interpreter.executeQuery(query);
            if (answer.next())
            {
                String object = trimQuotes(answer.getObject(0).toString());
                int counter = Integer.parseInt(object) + 1;
                query = "delete <urn:cone:jnar:counter> " + mts.getString("dc.identifier.urn") + " '" + object + "' from " + mts.getString("journal.model") + ";";
                log.debug("Query: " + query);
                interpreter.executeUpdate(query);
                query = "insert <urn:cone:jnar:counter> " + mts.getString("dc.identifier.urn") + " '" + counter + "' into " + mts.getString("journal.model") + "; ";
                log.debug("Query: " + query);
                interpreter.executeUpdate(query);
                return "urn:cone:" + counter;
            }
        }
        catch (ItqlInterpreterException e)
        {
            e.printStackTrace();
        }
        catch (TuplesException e)
        {
            e.printStackTrace();
        }
        finally
        {
            query = null;
            done();
        }
        return null;
    }
}
