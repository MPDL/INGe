package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class MdsFileVO extends MetadataSetVO {
  private String contentCategory;
  private String description;

  /**
   * Identifier of an external resource.
   */
  private List<IdentifierVO> identifiers = new ArrayList<>();

  private List<FormatVO> formats = new ArrayList<>();

  /**
   * Please use FileDbVO.size instead
   */
  @Deprecated
  private int size;

  private String embargoUntil;
  private String copyrightDate;
  private String rights;
  private String license;
  @Enumerated(EnumType.STRING)
  private OA_STATUS oaStatus;

  public enum OA_STATUS
  {
		NOT_SPECIFIED, GOLD, HYBRID, GREEN, MISCELLANEOUS, CLOSED_ACCESS
	}

  /**
	 * Default constructor.
	 */
	public MdsFileVO() {

	}

  /**
	 * Clone constructor.
	 *
	 * @param other The  to be cloned.
	 */
	public MdsFileVO(MdsFileVO other) {
		super(other.getTitle());

		this.identifiers = other.identifiers;
		this.description = other.description;
		this.contentCategory = other.contentCategory;
		this.formats = other.formats;
		this.size = other.size;
		this.embargoUntil = other.embargoUntil;
		this.copyrightDate = other.copyrightDate;
		this.rights = other.rights;
		this.license = other.license;
		this.oaStatus = other.oaStatus;
	}

  /**
	 * Please use FileDbVO.size instead
	 */
	@Deprecated
	public int getSize() {
		return size;
	}

  /**
   * Please use FileDbVO.size instead
   */
  @Deprecated
  public void setSize(int size) {
    this.size = size;
  }

  public String getContentCategory() {
    return contentCategory;
  }

  public void setContentCategory(String contentCategory) {
    this.contentCategory = contentCategory;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<IdentifierVO> getIdentifiers() {
    return identifiers;
  }

  public List<FormatVO> getFormats() {
    return formats;
  }

  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  /**
   * @return the embargoUntil
   */
  public String getEmbargoUntil() {
    return embargoUntil;
  }

  /**
   * @param embargoUntil the embargoUntil to set
   */
  public void setEmbargoUntil(String embargoUntil) {
    this.embargoUntil = embargoUntil;
  }

  /**
   * @return the copyrightDate
   */
  public String getCopyrightDate() {
    return copyrightDate;
  }

  /**
   * @param copyrightDate the copyrightDate to set
   */
  public void setCopyrightDate(String copyrightDate) {
    this.copyrightDate = copyrightDate;
  }

  /**
   * @return the license
   */
  public String getLicense() {
    return license;
  }

  /**
   * @param license the license to set
   */
  public void setLicense(String license) {
    this.license = license;
  }

  public OA_STATUS getOaStatus() {
    return oaStatus;
  }

  public void setOaStatus(OA_STATUS oaStatus) {
    this.oaStatus = oaStatus;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((contentCategory == null) ? 0 : contentCategory.hashCode());
    result = prime * result + ((copyrightDate == null) ? 0 : copyrightDate.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((embargoUntil == null) ? 0 : embargoUntil.hashCode());
    result = prime * result + ((formats == null) ? 0 : formats.hashCode());
    result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
    result = prime * result + ((license == null) ? 0 : license.hashCode());
    result = prime * result + ((rights == null) ? 0 : rights.hashCode());
    result = prime * result + ((oaStatus == null) ? 0 : oaStatus.hashCode());
    result = prime * result + size;
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

    MdsFileVO other = (MdsFileVO) obj;

    if (contentCategory == null) {
      if (other.contentCategory != null)
        return false;
    } else if (!contentCategory.equals(other.contentCategory))
      return false;

    if (copyrightDate == null) {
      if (other.copyrightDate != null)
        return false;
    } else if (!copyrightDate.equals(other.copyrightDate))
      return false;

    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;

    if (embargoUntil == null) {
      if (other.embargoUntil != null)
        return false;
    } else if (!embargoUntil.equals(other.embargoUntil))
      return false;

    if (formats == null) {
      if (other.formats != null)
        return false;
    } else if (other.formats == null)
      return false;
    else if (!new HashSet<>(formats).containsAll(other.formats) //
        || !new HashSet<>(other.formats).containsAll(formats)) {
      return false;
    }

    if (identifiers == null) {
      if (other.identifiers != null)
        return false;
    } else if (other.identifiers == null)
      return false;
    else if (!new HashSet<>(identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(identifiers)) {
      return false;
    }

    if (license == null) {
      if (other.license != null)
        return false;
    } else if (!license.equals(other.license))
      return false;

    if (rights == null) {
      if (other.rights != null)
        return false;
    } else if (!rights.equals(other.rights))
      return false;

    if (oaStatus == null) {
      if (other.oaStatus != null)
        return false;
    } else if (!oaStatus.equals(other.oaStatus))
      return false;

    if (size != other.size)
      return false;

    return true;
  }
}
