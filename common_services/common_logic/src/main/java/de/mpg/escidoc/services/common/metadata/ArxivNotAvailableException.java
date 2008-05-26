package de.mpg.escidoc.services.common.metadata;

import java.util.Date;

public class ArxivNotAvailableException extends Exception
{
    
    private Date retryAfter = null;
    
    public ArxivNotAvailableException()
    {
        
    }

    public ArxivNotAvailableException(String message)
    {
        super(message);
    }

    public ArxivNotAvailableException(Throwable cause)
    {
        super(cause);
    }

    public ArxivNotAvailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArxivNotAvailableException(Date retryAfter)
    {
        super();
        this.retryAfter = retryAfter;
    }

    public Date getRetryAfter()
    {
        return retryAfter;
    }

    public void setRetryAfter(Date retryAfter)
    {
        this.retryAfter = retryAfter;
    }

}
