package de.mpg.escidoc.main;

import java.io.IOException;

import javax.naming.NamingException;

import de.mpg.escidoc.handler.PIDProviderException;

public interface PIDProviderIf
{
    public void init() throws NamingException, IOException;

    public String updateComponentPid(String escidocId, String versionNumber, String componentId, String pid, String fileName) throws PIDProviderException;

    public int getTotalNumberOfPidsRequested();

}