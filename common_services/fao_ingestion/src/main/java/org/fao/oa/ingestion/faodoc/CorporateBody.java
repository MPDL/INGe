package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import noNamespace.FAOCorporateBodyDocument.FAOCorporateBody;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

public class CorporateBody
{
    public CorporateBody()
    {
        
    }
    
    public String[] getEnglish(String au_cor)
    {
        String label_en = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> corpBodyList = uris.getUriList(URI_TYPE.CORPORATEBODIES);
        
        for (Object corpBody : corpBodyList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOCorporateBody)corpBody).getAlternativeEN1() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeEN1());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeEN2() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeEN2());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeEN3() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeEN3());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeEN4() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeEN4());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeEN5() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeEN5());
            }
            if (alternatives.contains(au_cor))
            {
                label_en = ((FAOCorporateBody)corpBody).getLABELEN();
                href = ((FAOCorporateBody)corpBody).getURI();
            }
        }
        if (label_en != null && href != null)
        {
            return new String[] {label_en, href};
        }
        return null;
    }
    
    public String[] getFrench(String au_cor)
    {
        String label_fr = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> corpBodyList = uris.getUriList(URI_TYPE.CORPORATEBODIES);
        
        for (Object corpBody : corpBodyList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOCorporateBody)corpBody).getAlternativeFR1() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeFR1());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeFR2() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeFR2());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeFR3() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeFR3());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeFR4() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeFR4());
            }
            if (alternatives.contains(au_cor))
            {
                label_fr = ((FAOCorporateBody)corpBody).getLABELFR();
                href = ((FAOCorporateBody)corpBody).getURI();
            }
        }
        if (label_fr != null && href != null)
        {
            return new String[] {label_fr, href};
        }
        return null;
    }
    
    public String[] getSpanish(String au_cor)
    {
        String label_es = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> corpBodyList = uris.getUriList(URI_TYPE.CORPORATEBODIES);
        for (Object corpBody : corpBodyList)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOCorporateBody)corpBody).getAlternativeES1() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES1());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES2() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES2());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES3() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES3());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES4() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES4());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES5() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES5());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES6() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES6());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES7() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES7());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES8() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES8());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES9() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES9());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES10() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES10());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES11() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES11());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES12() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES12());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES13() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES13());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES14() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES14());
            }
            if (((FAOCorporateBody)corpBody).getAlternativeES15() != null)
            {
                alternatives.add(((FAOCorporateBody)corpBody).getAlternativeES15());
            }
            if (alternatives.contains(au_cor))
            {
                label_es = ((FAOCorporateBody)corpBody).getLABELES();
                href = ((FAOCorporateBody)corpBody).getURI();
            }
        }
        if (label_es != null && href != null)
        {
            return new String[] {label_es, href};
        }
        return null;
    }
    
    public String[] getOther(String au_cor)
    {
        String label = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> corpBodyList = uris.getUriList(URI_TYPE.CORPORATEBODIES);
        for (Object corpBody : corpBodyList)
        {
            if (((FAOCorporateBody)corpBody).getAlternativeIT1() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeIT1().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELIT();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeIT2() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeIT2().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELIT();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativePT1() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativePT1().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELPT();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeRO() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeRO().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELRO();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativePL() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativePL().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELPL();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeTR() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeTR().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELTR();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeNL() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeNL().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELNL();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeHU() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeHU().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELHU();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeCA() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeCA().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELCA();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeID() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeID().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELID();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeDE() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeDE().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELDE();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeSV() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeSV().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELSV();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeMS() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeMS().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELMS();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }if (((FAOCorporateBody)corpBody).getAlternativeNO() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeNO().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELNO();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeUK() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeUK().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELUK();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeDA() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeDA().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELDA();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeFJ() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeFJ().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELFJ();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeSL() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeSL().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELSL();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeBS() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeBS().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELBS();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeCS() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeCS().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELCS();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeHR() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeHR().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELHR();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeSK() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeSK().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELSK();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeSR() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeSR().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELSR();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeMK() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeMK().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELMK();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
            if (((FAOCorporateBody)corpBody).getAlternativeML() != null)
            {
                if (((FAOCorporateBody)corpBody).getAlternativeML().equalsIgnoreCase(au_cor))
                {
                    label = ((FAOCorporateBody)corpBody).getLABELML();
                    href = ((FAOCorporateBody)corpBody).getURI();
                }
            }
        }
        if (label != null && href != null)
        {
            return new String[] {label, href};
        }
        return null;
    }
}
