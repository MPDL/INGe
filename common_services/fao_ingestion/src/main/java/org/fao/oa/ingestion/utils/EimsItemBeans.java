package org.fao.oa.ingestion.utils;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import noNamespace.EimsresourcesDocument;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.ResourcesDocument;

public class EimsItemBeans
{
    public static void main(String... strings) throws XmlException, IOException
    {
        File eims = new File(IngestionProperties.get("eims.export.file.location") + "eimsexport.xml");
        File faodoc = new File("/home/frank/data/AGRIS_FAO/20090910-FaodocExport/M-2.xml");
        EimsresourcesDocument eimsrd = EimsresourcesDocument.Factory.parse(eims);
        ResourcesDocument rd = ResourcesDocument.Factory.parse(faodoc);
        ITEMType[] faodocitems = rd.getResources().getITEMArray();
        ItemType[] eimsitems = eimsrd.getEimsresources().getItemArray();
        for (ITEMType item : faodocitems)
        {
            if (item.sizeOfJNArray() > 0)
            {
                String[] jns = item.getJNArray();
                if (item.sizeOfJNArray() > 1)
                {
                    for (String s : jns)
                    {
                        String arn = item.getARNArray(0);
                        String lang = item.getLANGArray(0);
                        for (ItemType i : eimsitems)
                        {
                            String id = i.getIdentifier();
                            if (i.getJobno() != null)
                            {
                                if (s.equalsIgnoreCase(i.getJobno()) && lang.startsWith(i.getLangkey()))
                                {
                                    System.out.println("FAODOC item " + arn + " with LANG=" + lang + " and EIMS item " + id
                                            + " with langkey=" + i.getLangkey() + " have both job nr: " + s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
