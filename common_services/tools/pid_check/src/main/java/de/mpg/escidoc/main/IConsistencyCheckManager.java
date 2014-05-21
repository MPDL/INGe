package de.mpg.escidoc.main;

import java.io.File;
import java.util.List;

public interface IConsistencyCheckManager
{
    /**
     * Utility method: reads the file returns as List
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public List<String> getObjectsToCorrect(File file) throws Exception;
    
    /**
     * Generates the List or modifies the objects contained
     * @param pids
     * @throws Exception
     */
    public void generateOrCorrectList(List<String> pids) throws Exception;

    /**
     * Checks if the List entries are consistent
     * @param pidsCorrected
     * @throws Exception
     */
    public void verifyList(List<String> pidsCorrected) throws Exception;

    
}