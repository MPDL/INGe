package de.mpg.escidoc.main;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class LdhCheckManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager
{
    public LdhCheckManager()
    {
        super.init();
    }

    @Override
    public List<String> getPidsToCorrect(File pids) throws Exception, URISyntaxException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void correctList(List<String> pids) throws Exception
    {
        // TODO Auto-generated method stub
        
    }
}
