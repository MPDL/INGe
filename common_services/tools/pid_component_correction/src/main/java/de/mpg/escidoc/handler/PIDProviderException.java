package de.mpg.escidoc.handler;

public class PIDProviderException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String escidocId = "";

    public PIDProviderException(Exception e)
    {
        super(e);
    }
    
    public PIDProviderException(String message)
    {
        super(message);
    }
    
    public PIDProviderException(String message, String escidocId)
    {
        super(message);
        this.escidocId = escidocId;
    }
    
    public PIDProviderException(String message, Exception e)
    {
        super(message, e);
    }
    
    public String getEscidocId()
    {
        return this.escidocId;
    }
}
