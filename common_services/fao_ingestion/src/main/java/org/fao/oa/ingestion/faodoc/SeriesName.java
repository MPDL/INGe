package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import noNamespace.FAOCorporateBodyDocument.FAOCorporateBody;
import noNamespace.FAOSERIESDocument2.FAOSERIES;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

public class SeriesName
{
    public SeriesName()
    {
    }

    public String[] getEnglish(String serTitle)
    {
        String label_en = null;
        String href = null;
        String lang = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> seriesList = uris.getUriList(URI_TYPE.SERIES);
        
        for (Object series : seriesList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOSERIES)series).getAlternativeEN1() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeEN1());
            }
            if (((FAOSERIES)series).getAlternativeEN2() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeEN2());
            }
            if (((FAOSERIES)series).getAlternativeEN3() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeEN3());
            }
            if (alternatives.size() > 0)
            {
                for (String s : alternatives)
                {
                    if (s.contains(serTitle))
                    {
                        label_en = ((FAOSERIES)series).getLABELEN();
                        href = ((FAOSERIES)series).getID();
                        lang = "en";
                    }
                }
            }
        }
        if (label_en != null && href != null && lang != null)
        {
            return new String[] { label_en, href, lang };
        }
        return null;
    }

    public String[] getFrench(String serTitle)
    {
        String label_fr = null;
        String href = null;
        String lang = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> seriesList = uris.getUriList(URI_TYPE.SERIES);
        
        for (Object series : seriesList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOSERIES)series).getAlternativeFR1() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeFR1());
            }
            if (((FAOSERIES)series).getAlternativeFR2() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeFR2());
            }
            if (alternatives.size() > 0)
            {
                for (String s : alternatives)
                {
                    if (s.contains(serTitle))
                    {
                        label_fr = ((FAOSERIES)series).getLABELFR();
                        href = ((FAOSERIES)series).getID();
                        lang = "fr";
                    }
                }
            }
        }
        if (label_fr != null && href != null && lang != null)
        {
            return new String[] { label_fr, href, lang };
        }
        return null;
    }

    public String[] getSpanish(String serTitle)
    {
        String label_es = null;
        String href = null;
        String lang = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> seriesList = uris.getUriList(URI_TYPE.SERIES);
        
        for (Object series : seriesList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOSERIES)series).getAlternativeES1() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeES1());
            }
            if (((FAOSERIES)series).getAlternativeES2() != null)
            {
                alternatives.add(((FAOSERIES)series).getAlternativeES2());
            }
            if (alternatives.size() > 0)
            {
                for (String s : alternatives)
                {
                    if (s.contains(serTitle))
                    {
                        label_es = ((FAOSERIES)series).getLABELES();
                        href = ((FAOSERIES)series).getID();
                        lang = "es";
                    }
                }
            }
        }
        if (label_es != null && href != null && lang != null)
        {
            return new String[] { label_es, href, lang };
        }
        return null;
    }

    public String[] getOther(String serTitle)
    {
        String label_ot = null;
        String href = null;
        String lang = "ot";
        FaoUris uris = new FaoUris();
        ArrayList<Object> seriesList = uris.getUriList(URI_TYPE.SERIES);
        for (Object series : seriesList)
        {
            if (((FAOSERIES)series).getAlternativeZH1() != null)
            {
                if (((FAOSERIES)series).getAlternativeZH1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELZH();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeAR1() != null)
            {
                if (((FAOSERIES)series).getAlternativeAR1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELAR();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeRU1() != null)
            {
                if (((FAOSERIES)series).getAlternativeRU1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELRU();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeIT1() != null)
            {
                if (((FAOSERIES)series).getAlternativeIT1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELIT();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeDE1() != null)
            {
                if (((FAOSERIES)series).getAlternativeDE1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELDE();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativePT1() != null)
            {
                if (((FAOSERIES)series).getAlternativePT1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELPT();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeSV1() != null)
            {
                if (((FAOSERIES)series).getAlternativeSV1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELSV();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeVI1() != null)
            {
                if (((FAOSERIES)series).getAlternativeVI1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELVI();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeFI1() != null)
            {
                if (((FAOSERIES)series).getAlternativeFI1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELFI();
                    href = ((FAOSERIES)series).getID();
                }
            }
            if (((FAOSERIES)series).getAlternativeSR1() != null)
            {
                if (((FAOSERIES)series).getAlternativeSR1().equalsIgnoreCase(serTitle))
                {
                    label_ot = ((FAOSERIES)series).getLABELSR();
                    href = ((FAOSERIES)series).getID();
                }
            }
        }
        if (label_ot != null && href != null)
        {
            return new String[] { label_ot, href, lang };
        }
        return null;
    }
}
