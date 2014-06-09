package de.mpg.escidoc.main;

import java.io.File;
import java.util.Set;

public interface IConsistencyCheckManager
{
    /**
     * Utility method: reads the file returns as Set
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public Set<String> getObjectsToCorrect(File file) throws Exception;
    
    /**
     * Creates the Set or modifies the objects contained
     * @param pids
     * @throws Exception
     */
    public void createOrCorrectSet(Set<String> pids) throws Exception;

    /**
     * Checks if the Set entries are consistent
     * @param pidsCorrected
     * @throws Exception
     */
    public void verifySet(Set<String> pidsCorrected) throws Exception;

    
}