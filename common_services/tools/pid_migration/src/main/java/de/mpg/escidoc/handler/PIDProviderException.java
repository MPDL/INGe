package de.mpg.escidoc.handler;

public class PIDProviderException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PIDProviderException(Exception e)
    {
        super(e);
    }
    
    public PIDProviderException(String message)
    {
        super(message);
    }
    
    public PIDProviderException(String message, Exception e)
    {
        super(message, e);
    }
}
