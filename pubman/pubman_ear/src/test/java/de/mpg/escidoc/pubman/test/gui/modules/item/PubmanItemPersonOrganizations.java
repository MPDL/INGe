package de.mpg.escidoc.pubman.test.gui.modules.item;

import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorRole;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorType;



public class PubmanItemPersonOrganizations
{
    public String multipleAuthors = null;
    public boolean overwriteOldOnes = false;
    public String multipleOrgaName = null;
    public String multipleOrgaAddress = null;
    
    public CreatorRole creatorRole = null;
    public CreatorType creatorType = null;
    
    public String firstName = null;
    public String lastName = null;
    public String orgaName = null;
    public String orgaAddress = null;
    
    public PubmanItemPersonOrganizations( String multipleAuthors, boolean overwriteOldOnes, 
            String multipleOrgaName, String multipleOrgaAddress, CreatorRole creatorRole,
            String firstName, String lastName, String orgaName, String orgaAddress) {
        this.multipleAuthors = multipleAuthors;
        this.overwriteOldOnes = overwriteOldOnes;
        this.multipleOrgaName = multipleOrgaName;
        this.multipleOrgaAddress = multipleOrgaAddress;
        this.creatorRole = creatorRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.orgaName = orgaName;
        this.orgaAddress = orgaAddress;
    }

    public String getMultipleAuthors()
    {
        return multipleAuthors;
    }

    public boolean isOverwriteOldOnes()
    {
        return overwriteOldOnes;
    }

    public String getMultipleOrgaName()
    {
        return multipleOrgaName;
    }

    public String getMultipleOrgaAddress()
    {
        return multipleOrgaAddress;
    }

    public CreatorRole getCreatorRole()
    {
        return creatorRole;
    }

    public CreatorType getCreatorType()
    {
        return creatorType;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getOrgaName()
    {
        return orgaName;
    }

    public String getOrgaAddress()
    {
        return orgaAddress;
    }
}
