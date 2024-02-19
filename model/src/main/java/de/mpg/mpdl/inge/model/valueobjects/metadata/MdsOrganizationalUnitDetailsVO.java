package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    return this.city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Coordinates getCoordinates() {
    return this.coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public String getCountryCode() {
    return this.countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDescriptions() {
    return this.descriptions;
  }

  public List<IdentifierVO> getIdentifiers() {
    return this.identifiers;
  }

  public List<String> getAlternativeNames() {
    return this.alternativeNames;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStartDate() {
    return this.startDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEndDate() {
    return this.endDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((null == this.alternativeNames) ? 0 : this.alternativeNames.hashCode());
    result = prime * result + ((null == this.city) ? 0 : this.city.hashCode());
    result = prime * result + ((null == this.coordinates) ? 0 : this.coordinates.hashCode());
    result = prime * result + ((null == this.countryCode) ? 0 : this.countryCode.hashCode());
    result = prime * result + ((null == this.descriptions) ? 0 : this.descriptions.hashCode());
    result = prime * result + ((null == this.endDate) ? 0 : this.endDate.hashCode());
    result = prime * result + ((null == this.identifiers) ? 0 : this.identifiers.hashCode());
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    result = prime * result + ((null == this.startDate) ? 0 : this.startDate.hashCode());
    result = prime * result + ((null == this.type) ? 0 : this.type.hashCode());
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

    if (null == this.alternativeNames) {
      if (null != other.alternativeNames)
        return false;
    } else if (null == other.alternativeNames)
      return false;
    else if (!new HashSet<>(this.alternativeNames).containsAll(other.alternativeNames) //
        || !new HashSet<>(other.alternativeNames).containsAll(this.alternativeNames)) {
      return false;
    }

    if (null == this.city) {
      if (null != other.city)
        return false;
    } else if (!this.city.equals(other.city))
      return false;

    if (null == this.coordinates) {
      if (null != other.coordinates)
        return false;
    } else if (!this.coordinates.equals(other.coordinates))
      return false;

    if (null == this.countryCode) {
      if (null != other.countryCode)
        return false;
    } else if (!this.countryCode.equals(other.countryCode))
      return false;

    if (null == this.descriptions) {
      if (null != other.descriptions)
        return false;
    } else if (null == other.descriptions)
      return false;
    else if (!new HashSet<>(this.descriptions).containsAll(other.descriptions) //
        || !new HashSet<>(other.descriptions).containsAll(this.descriptions)) {
      return false;
    }

    if (null == this.endDate) {
      if (null != other.endDate)
        return false;
    } else if (!this.endDate.equals(other.endDate))
      return false;

    if (null == this.identifiers) {
      if (null != other.identifiers)
        return false;
    } else if (null == other.identifiers)
      return false;
    else if (!new HashSet<>(this.identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(this.identifiers)) {
      return false;
    }

    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;

    if (null == this.startDate) {
      if (null != other.startDate)
        return false;
    } else if (!this.startDate.equals(other.startDate))
      return false;

    if (null == this.type) {
      if (null != other.type)
        return false;
    } else if (!this.type.equals(other.type))
      return false;

    return true;
  }

}
