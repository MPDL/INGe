package org.fao.oa.ingestion.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import gov.loc.mods.v3.ModsDocument;

public class CheckMergedResources
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        checkArticles();
    }
    
    private static void checkArticles()
    {
        int num = 0;
        File foxmlDir = new File("/home/frank/data/AGRIS_FAO/FOXML_articles");
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File file, String name)
            {
                String regex = ".*_.*";
                return name.matches(regex);
            }
        };
        for (File f : foxmlDir.listFiles(filter))
        {
            try
            {
                DigitalObjectDocument fox = DigitalObjectDocument.Factory.parse(f);
                DatastreamType[] streams = fox.getDigitalObject().getDatastreamArray();
                for (DatastreamType stream : streams)
                {
                    if (stream.getID().equalsIgnoreCase("MODS"))
                    {
                        XmlCursor cursor = stream.newCursor();
                        cursor.toFirstChild(); //goto version
                        cursor.toFirstChild(); //goto content
                        cursor.toFirstChild(); //goto mods

                        ModsDocument mods = ModsDocument.Factory.parse(cursor.xmlText());
                        String title = mods.getMods().getRelatedItemArray(0).getTitleInfoArray(0).getTitleArray(0);
                        System.out.println(num++ + "  " + f.getName() + " series title: " + title);
                    }
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
            
        }
    }
}
