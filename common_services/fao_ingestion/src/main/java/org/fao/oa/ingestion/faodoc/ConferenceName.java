package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import noNamespace.CONFERENCEType;
import noNamespace.FAOConferenceDocument.FAOConference;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

/**
 * utility class to get the (re)formatted name and hrefs for conferences from a controlled vocabulary.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class ConferenceName
{
    public ConferenceName()
    {
    }

    public String[] getEnglish(CONFERENCEType conference, String name)
    {
        String label_en = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELEN() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELEN());
            }
            if (((FAOConference)conf).getAlternativeEN1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeEN1());
            }
            if (((FAOConference)conf).getAlternativeEN2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeEN2());
            }
            if (alternatives.contains(name))
            {
                label_en = ((FAOConference)conf).getLABELEN();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_en != null && href != null)
        {
            String confName = conferenceName(conference, label_en);
            return new String[] { confName, href };
        }
        return null;
    }

    public String[] getFrench(CONFERENCEType conference, String name)
    {
        String label_fr = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELFR() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELFR());
            }
            if (((FAOConference)conf).getAlternativeFR1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR1());
            }
            if (((FAOConference)conf).getAlternativeFR2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR2());
            }
            if (((FAOConference)conf).getAlternativeFR3() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR3());
            }
            if (alternatives.contains(name))
            {
                label_fr = ((FAOConference)conf).getLABELFR();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_fr != null && href != null)
        {
            String confName = conferenceName(conference, label_fr);
            return new String[] { confName, href };
        }
        return null;
    }

    public String[] getOther(CONFERENCEType conference, String name)
    {
        String label = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        for (Object conf : conferences)
        {
            if (((FAOConference)conf).getLABELRU() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELRU())
                        || name.equalsIgnoreCase(((FAOConference)conf).getAlternativeRU()))
                {
                    label = ((FAOConference)conf).getLABELRU();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELAR() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELAR()))
                {
                    label = ((FAOConference)conf).getLABELAR();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELIT() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELIT()))
                {
                    label = ((FAOConference)conf).getLABELIT();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELZH() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELZH()))
                {
                    label = ((FAOConference)conf).getLABELZH();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELPT() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELPT()))
                {
                    label = ((FAOConference)conf).getLABELPT();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELDE() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELDE()))
                {
                    label = ((FAOConference)conf).getLABELDE();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELTR() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELTR()))
                {
                    label = ((FAOConference)conf).getLABELTR();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELID() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELID()))
                {
                    label = ((FAOConference)conf).getLABELID();
                    href = ((FAOConference)conf).getID();
                }
            }
        }
        if (label != null && href != null)
        {
            String confName = conferenceName(conference, label);
            return new String[] { confName, href };
        }
        return null;
    }

    public String[] getSpanish(CONFERENCEType conference, String name)
    {
        String label_es = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELES() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELES());
            }
            if (((FAOConference)conf).getAlternativeES1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES1());
            }
            if (((FAOConference)conf).getAlternativeES2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES2());
            }
            if (((FAOConference)conf).getAlternativeES3() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES3());
            }
            if (((FAOConference)conf).getAlternativeES4() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES4());
            }
            if (alternatives.contains(name))
            {
                label_es = ((FAOConference)conf).getLABELES();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_es != null && href != null)
        {
            String confName = conferenceName(conference, label_es);
            return new String[] { confName, href };
        }
        return null;
    }

    /**
     * utility method to format the conference name.
     * @param conference {@link CONFERENCEType}
     * @param name {@link String}
     * @return {@link String}
     */
    public String conferenceName(CONFERENCEType conference, String name)
    {
        StringBuilder sb = new StringBuilder(name);
        sb.append(" (");
        if (conference.isSetCONFNO())
        {
            sb.append(conference.getCONFNO() + ": ");
        }
        if (conference.isSetCONFDATE())
        {
            sb.append(conference.getCONFDATE() + " : ");
        }
        if (conference.isSetCONFPLACE())
        {
            String confCity = null;
            String confState = null;
            String confCountry = null;
            String confPlace = conference.getCONFPLACE();
            if (!confPlace.contains("("))
            {
                confCountry = confPlace;
            }
            else
            {
                if (confPlace.startsWith("("))
                {
                    confCountry = confPlace.substring(confPlace.indexOf("(") + 1, confPlace.indexOf(")") + 1);
                }
                else
                {
                    if (!confPlace.substring(1, confPlace.indexOf("(")).contains(","))
                    {
                        confCity = confPlace.split("\\s\\(")[0];
                        confCountry = confPlace.substring(confPlace.indexOf("(") + 1, confPlace.indexOf(")") + 1);
                    }
                    else
                    {
                        confCity = confPlace.split(",")[0];
                        confState = confPlace.split(",")[1].substring(1, confPlace.split(",")[1].indexOf("("));
                        confCountry = confPlace.split(",")[1].substring(confPlace.split(",")[1].indexOf("(") + 1,
                                confPlace.split(",")[1].indexOf(")") + 1);
                    }
                }
            }
            if (confCity != null && confState != null)
            {
                sb.append(confCity + " (" + confState + "), " + confCountry);
            }
            else
            {
                if (confCity != null)
                {
                    sb.append(confCity + ", " + confCountry);
                }
                else
                {
                    sb.append(confCountry);
                }
            }
        }
        return sb.toString();
    }
}
