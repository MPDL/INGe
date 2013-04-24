package de.mpg.escidoc.main;

import javax.naming.NamingException;

import de.mpg.escidoc.handler.PIDProviderException;
import de.mpg.escidoc.handler.PreHandler.Type;

public interface PIDProviderIf
{
    public void init() throws NamingException;

    public String getPid(String escidocId, Type objectType, String title) throws PIDProviderException;
}