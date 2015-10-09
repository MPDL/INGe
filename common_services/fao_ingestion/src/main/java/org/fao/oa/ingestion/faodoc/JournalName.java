package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import noNamespace.FAOJournalDocument.FAOJournal;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

/**
 * utility class to get the label, href and language for journals from a controlled vocabulary.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class JournalName
{
    public JournalName()
    {
    }

    public String[] get(String journalName)
    {
        String label_ot = null;
        String href = null;
        String lang = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> journalList = uris.getUriList(URI_TYPE.JOURNALS);
        for (Object journal : journalList)
        {
            if (((FAOJournal)journal).getAlternativeEN() != null)
            {
                if (((FAOJournal)journal).getAlternativeEN().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELEN();
                    href = ((FAOJournal)journal).getID();
                    lang = "en";
                }
            }
            if (((FAOJournal)journal).getAlternativeFR() != null)
            {
                if (((FAOJournal)journal).getAlternativeFR().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELFR();
                    href = ((FAOJournal)journal).getID();
                    lang = "fr";
                }
            }
            if (((FAOJournal)journal).getAlternativeES() != null)
            {
                if (((FAOJournal)journal).getAlternativeES().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELES();
                    href = ((FAOJournal)journal).getID();
                    lang = "es";
                }
            }
            if (((FAOJournal)journal).getAlternativeIT() != null)
            {
                if (((FAOJournal)journal).getAlternativeIT().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELIT();
                    href = ((FAOJournal)journal).getID();
                    lang = "it";
                }
            }
            if (((FAOJournal)journal).getAlternativeDE() != null)
            {
                if (((FAOJournal)journal).getAlternativeDE().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELDE();
                    href = ((FAOJournal)journal).getID();
                    lang = "de";
                }
            }
            if (((FAOJournal)journal).getAlternativePT() != null)
            {
                if (((FAOJournal)journal).getAlternativePT().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELPT();
                    href = ((FAOJournal)journal).getID();
                    lang = "pt";
                }
            }
            if (((FAOJournal)journal).getAlternativeSK() != null)
            {
                if (((FAOJournal)journal).getAlternativeSK().equalsIgnoreCase(journalName))
                {
                    label_ot = ((FAOJournal)journal).getLABELSK();
                    href = ((FAOJournal)journal).getID();
                    lang = "sk";
                }
            }
        }
        if (label_ot != null && href != null && lang != null)
        {
            return new String[] { label_ot, href, lang };
        }
        return null;
    }
}
