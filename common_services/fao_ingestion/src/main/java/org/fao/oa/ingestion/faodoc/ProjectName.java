package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import noNamespace.FAOProjectDocument.FAOProject;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

/**
 * utility class to get the label, href and language for projects from a controlled vocabulary.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class ProjectName
{
    public ProjectName()
    {
        
    }
    
    public String[] checkLabel(String pname)
    {
        String label = null;
        String href = null;
        String lang = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> projectNames = uris.getUriList(URI_TYPE.PROJECTS);
        for (Object project : projectNames)
        {
            if (((FAOProject)project).getLABELEN() != null)
            {
                if (((FAOProject)project).getLABELEN().equals(pname))
                {
                    label = ((FAOProject)project).getLABELEN();
                    href = ((FAOProject)project).getID();
                    lang = "en";
                }
            }
            if (((FAOProject)project).getLABELFR() != null)
            {
                if (((FAOProject)project).getLABELFR().equals(pname))
                {
                    label = ((FAOProject)project).getLABELFR();
                    href = ((FAOProject)project).getID();
                    lang = "fr";
                }
            }
            if (((FAOProject)project).getLABELES() != null)
            {
                if (((FAOProject)project).getLABELES().equals(pname))
                {
                    label = ((FAOProject)project).getLABELES();
                    href = ((FAOProject)project).getID();
                    lang = "es";
                }
            }
            if (((FAOProject)project).getLABELPT() != null)
            {
                if (((FAOProject)project).getLABELPT().equals(pname))
                {
                    label = ((FAOProject)project).getLABELPT();
                    href = ((FAOProject)project).getID();
                    lang = "pt";
                }
            }
        }
        if (label != null && href != null && lang != null)
        {
            return new String[] {label, href, lang};
        }
        return null;
    }
}
