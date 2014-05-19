package de.mpg.escidoc.main;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public interface IConsistencyCheckManager
{
    public void correctList(List<String> pids) throws Exception;

    public void verifyList(List<String> pidsCorrected) throws Exception;

    public List<String> getPidsToCorrect(File file) throws Exception, URISyntaxException;
}