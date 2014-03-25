package de.mpg.escidoc.pubman.viewItem;
/**
 * 
 * @author yu
 *
 */
public class ViewItemCreators
{
    private String creatorType;
    private Object creatorObj;
    private String creatorRole;

    public enum Type
    {
        PERSON, ORGANIZATION;
    }

    public ViewItemCreators()
    {
    }

    public String getCreatorType()
    {
        return creatorType;
    }

    public void setCreatorType(String creatorType)
    {
        this.creatorType = creatorType;
    }

    public Object getCreatorObj()
    {
        return creatorObj;
    }

    public void setCreatorObj(Object creatorObj)
    {
        this.creatorObj = creatorObj;
    }

    public void setCreatorRole(String creatorRole)
    {
        this.creatorRole = creatorRole;
    }

    public String getCreatorRole()
    {
        return ViewItemFull.getLabelStatic("ENUM_CREATORROLE_"+creatorRole);
    }
}
