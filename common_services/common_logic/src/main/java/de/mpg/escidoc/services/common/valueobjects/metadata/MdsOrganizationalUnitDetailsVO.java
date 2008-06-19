package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.types.Coordinates;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MdsOrganizationalUnitDetailsVO extends MetadataSetVO
{
    private String city;
    private Coordinates coordinates;
    
    /**
     * These codes are the upper-case, two-letter codes as defined by ISO-3166. You can find a full list of these codes
     * at a number of sites, such as: http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-
     * en1.html
     */
    private String countryCode;
    private List<String> descriptions = new ArrayList<String>();
    
    /**
     * Identifier of an external resource.
     */
    private List<String> identifiers = new ArrayList<String>();

    /**
     * The unique name of the affiliation in the organizational structure.
     */
    private String name;
    private List<String> alternativeNames = new ArrayList<String>();

    private Date startDate;
    private Date endDate;
    
    /**
     * Derfault constructor.
     */
    public MdsOrganizationalUnitDetailsVO()
    {
        
    }
    
    /**
     * Clone constructor.
     * 
     * @param other The {@link MdsOrganizationalUnitDetailsVO} to be cloned.
     */
    public MdsOrganizationalUnitDetailsVO(MdsOrganizationalUnitDetailsVO other)
    {
        this.city = other.city;
        this.countryCode = other.countryCode;
        this.descriptions = other.descriptions;
        this.name = other.name;
        this.alternativeNames = other.alternativeNames;
        this.identifiers = other.identifiers;
        this.coordinates = other.coordinates;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getDescriptions()
    {
        return descriptions;
    }

    public List<String> getIdentifiers()
    {
        return identifiers;
    }

    public List<String> getAlternativeNames()
    {
        return alternativeNames;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
    
}
