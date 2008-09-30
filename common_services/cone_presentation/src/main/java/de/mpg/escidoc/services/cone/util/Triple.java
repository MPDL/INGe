package de.mpg.escidoc.services.cone.util;


public class Triple
{
    private String subject;
    private String predicate;
    private String object;
    
    public Triple()
    {
        
    }
    
    public Triple(String subject, String predicate, String object)
    {
        this.object = object;
        this.predicate = predicate;
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    public String getPredicate()
    {
        return predicate;
    }
    public void setPredicate(String predicate)
    {
        this.predicate = predicate;
    }
    public String getObject()
    {
        return object;
    }
    public void setObject(String object)
    {
        this.object = object;
    }
}