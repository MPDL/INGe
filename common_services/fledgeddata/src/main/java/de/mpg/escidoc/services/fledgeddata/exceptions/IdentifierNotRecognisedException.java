package de.mpg.escidoc.services.fledgeddata.exceptions;

/**
 * Exception thrown if a source reports an error due to the given identifier.
 */
public class IdentifierNotRecognisedException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * IdentifierNotRecognisedException.
     */
    public IdentifierNotRecognisedException()
    {
        super();
    }

   
    /**
     * IdentifierNotRecognisedException.
     * @param message
     * @param cause
     */
    public IdentifierNotRecognisedException(String message, Throwable cause)
    {
        super(message, cause);
    }


    /**
     * IdentifierNotRecognisedException.
     * @param message
     */
    public IdentifierNotRecognisedException(String message)
    {
        super(message);
    }

    /**
     * IdentifierNotRecognisedException.
     * @param cause
     */
    public IdentifierNotRecognisedException(Throwable cause)
    {
        super(cause);
    }
}
