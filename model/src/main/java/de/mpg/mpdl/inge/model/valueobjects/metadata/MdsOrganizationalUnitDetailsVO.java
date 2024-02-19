package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class MdsOrganizationalUnitDetailsVO extends MetadataSetVO {

  private String city;
  private Coordinates coordinates;

  /**
   * These codes are the upper-case, two-letter codes as defined by ISO-3166. You can find a full
   * list of these codes at a number of sites, such as:
   * http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list- en1.html
   */
  private String countryCode;
  private List<String> descriptions = new ArrayList<>();

  /**
   * Identifier of an external resource.
   */
  private List<IdentifierVO> identifiers = new ArrayList<>();

  /**
   * The unique name of the affiliation in the organizational structure.
   */
  private String name;
  private List<String> alternativeNames = new ArrayList<>();
  private String type;
  private String startDate;
  private String endDate;

  /**
   * Default constructor.
   */
  public MdsOrganizationalUnitDetailsVO() {}

  /**
   * Clone constructor.
   *
   * @param other The to be cloned.
   */
  public MdsOrganizationalUnitDetailsVO(MdsOrganizationalUnitDetailsVO other) {
    this.city = other.city;
    this.countryCode = other.countryCode;
    this.descriptions = other.descriptions;
    this.name = other.name;
    this.alternativeNames = other.alternativeNames;
    this.identifiers = other.identifiers;
    this.coordinates = other.coordinates;
    this.setStartDate(other.getStartDate());
    this.setEndDate(other.getEndDate());
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDescriptions() {
    return descriptions;
  }

  public List<IdentifierVO> getIdentifiers() {
    return identifiers;
  }

  public List<String> getAlternativeNames() {
    return alternativeNames;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEndDate() {
    return endDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((alternativeNames == null) ? 0 : alternativeNames.hashCode());
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
    result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
    result = prime * result + ((descriptions == null) ? 0 : descriptions.hashCode());
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (!super.equals(obj))
      return false;

    if (getClass() != obj.getClass())
      return false;

    MdsOrganizationalUnitDetailsVO other = (MdsOrganizationalUnitDetailsVO) obj;

    if (alternativeNames == null) {
      if (other.alternativeNames != null)
        return false;
    } else if (other.alternativeNames == null)
      return false;
    else if (!new HashSet<>(alternativeNames).containsAll(other.alternativeNames) //
        || !new HashSet<>(other.alternativeNames).containsAll(alternativeNames)) {
      return false;
    }

    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;

    if (coordinates == null) {
      if (other.coordinates != null)
        return false;
    } else if (!coordinates.equals(other.coordinates))
      return false;

    if (countryCode == null) {
      if (other.countryCode != null)
        return false;
    } else if (!countryCode.equals(other.countryCode))
      return false;

    if (descriptions == null) {
      if (other.descriptions != null)
        return false;
    } else if (other.descriptions == null)
      return false;
    else if (!new HashSet<>(descriptions).containsAll(other.descriptions) //
        || !new HashSet<>(other.descriptions).containsAll(descriptions)) {
      return false;
    }

    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
      return false;

    if (identifiers == null) {
      if (other.identifiers != null)
        return false;
    } else if (other.identifiers == null)
      return false;
    else if (!new HashSet<>(identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(identifiers)) {
      return false;
    }

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (startDate == null) {
      if (other.startDate != null)
        return false;
    } else if (!startDate.equals(other.startDate))
      return false;

    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;

    return true;
  }

}
