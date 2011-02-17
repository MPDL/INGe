package org.fao.oa.ingestion.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * utility class to change the root element of the EIMS_CDR files.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class ChangeEimsResources
{
    static String eimsDir = IngestionProperties.get("eims.export.file.location");
    
    public static void main(String ... strings)
    {
        File dir = new File(eimsDir);
        for (File f : dir.listFiles())
        {
            try
            {
                System.out.println("reading file: " + f.getName());
                BufferedReader reader = new BufferedReader(new FileReader(f));
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(f.getPath().replace(f.getName(), "eims_" + f.getName()))));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("<resources"))
                    {
                        line = line.replace("<resources", "<eimsresources");
                    }
                    if (line.contains("</resources"))
                    {
                        line = line.replace("</resources", "</eimsresources");
                    }
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
                
            }
            catch (FileNotFoundException e)
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
