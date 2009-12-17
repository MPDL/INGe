package org.fao.oa.ingestion.uris;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import noNamespace.FAOConferencesDocument;
import noNamespace.FAOCorporateBodiesDocument;
import noNamespace.FAOJournalsDocument;
import noNamespace.FAOProjectsDocument;
import noNamespace.FAOSeriesDocument;
import noNamespace.FAOConferenceDocument.FAOConference;
import noNamespace.FAOCorporateBodyDocument.FAOCorporateBody;
import noNamespace.FAOJournalDocument.FAOJournal;
import noNamespace.FAOProjectDocument.FAOProject;
import noNamespace.FAOSERIESDocument2.FAOSERIES;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.IngestionProperties;

public class FaoUris
{
    public static final String FAO_URIS_BASEDIR = IngestionProperties.get("fao.uris.file.location");
    public static final String FAO_CONFERENCES = IngestionProperties.get("fao.uris.conferences");
    public static final String FAO_CORPORATEBODIES = IngestionProperties.get("fao.uris.corporatebodies");
    public static final String FAO_JOURNALS = IngestionProperties.get("fao.uris.journals");
    public static final String FAO_PROJECTS = IngestionProperties.get("fao.uris.projects");
    public static final String FAO_SERIES = IngestionProperties.get("fao.uris.series");

    public enum URI_TYPE
    {
        CONFERENCES, CORPORATEBODIES, JOURNALS, PROJECTS, SERIES
    }

    public ArrayList<Object> getUriList(URI_TYPE uriType)
    {
        ArrayList<Object> uriList = new ArrayList<Object>();
        try
        {
            switch (uriType)
            {
                case CONFERENCES:
                    File conferencesFile = new File(FAO_URIS_BASEDIR + FAO_CONFERENCES);
                    FAOConferencesDocument conferencesDoc = FAOConferencesDocument.Factory.parse(conferencesFile);
                    FAOConference[] conferences = conferencesDoc.getFAOConferences().getFAOConferenceArray();
                    for (FAOConference conference : conferences)
                    {
                        uriList.add(conference);
                    }
                    break;
                case CORPORATEBODIES:
                    File corpBodyFile = new File(FAO_URIS_BASEDIR + FAO_CORPORATEBODIES);
                    FAOCorporateBodiesDocument corpBodyDoc = FAOCorporateBodiesDocument.Factory.parse(corpBodyFile);
                    FAOCorporateBody[] corpBodies = corpBodyDoc.getFAOCorporateBodies().getFAOCorporateBodyArray();
                    for (FAOCorporateBody corpBody : corpBodies)
                    {
                        uriList.add(corpBody);
                    }
                    break;
                case JOURNALS:
                    File journalFile = new File(FAO_URIS_BASEDIR + FAO_JOURNALS);
                    FAOJournalsDocument journalDoc = FAOJournalsDocument.Factory.parse(journalFile);
                    FAOJournal[] journals = journalDoc.getFAOJournals().getFAOJournalArray();
                    for (FAOJournal journal : journals)
                    {
                        uriList.add(journal);
                    }
                    break;
                case PROJECTS:
                    File projectFile = new File(FAO_URIS_BASEDIR + FAO_PROJECTS);
                    FAOProjectsDocument projectsDoc = FAOProjectsDocument.Factory.parse(projectFile);
                    FAOProject[] projects = projectsDoc.getFAOProjects().getFAOProjectArray();
                    for (FAOProject project : projects)
                    {
                        uriList.add(project);
                    }
                    break;
                case SERIES:
                    File seriesFile = new File(FAO_URIS_BASEDIR + FAO_SERIES);
                    FAOSeriesDocument seriesDoc = FAOSeriesDocument.Factory.parse(seriesFile);
                    FAOSERIES[] seriesArray = seriesDoc.getFAOSeries().getFAOSERIESArray();
                    for (FAOSERIES series : seriesArray)
                    {
                        uriList.add(series);
                    }
                    break;
            }
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return uriList;
    }
}
