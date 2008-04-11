package de.mpg.escidoc.pubman.util.statistics;

public class ReportDefinitionVO implements Comparable<ReportDefinitionVO>
{
    
    private String objectId;
    
    private String sql;

    private String name;

    private String scopeID;
    
    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getScopeID()
    {
        return scopeID;
    }

    public void setScopeID(String scopeID)
    {
        this.scopeID = scopeID;
    }

    @Override
    public int compareTo(ReportDefinitionVO o)
    {
        return sql.compareTo(o.getSql());
    }
    
    
}
