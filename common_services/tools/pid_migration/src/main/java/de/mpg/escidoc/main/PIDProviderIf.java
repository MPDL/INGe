package de.mpg.escidoc.main;

import java.io.IOException;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpException;

public interface PIDProviderIf
{
    public String getPid() throws HttpException, IOException;

    public void init() throws NamingException;
}