package de.mpg.escidoc.main;

import java.io.IOException;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpException;

import de.mpg.escidoc.handler.PreHandler.Type;

public interface PIDProviderIf
{
    public String getPid(String escidocId, Type type) throws HttpException, IOException;

    public void init() throws NamingException;
}