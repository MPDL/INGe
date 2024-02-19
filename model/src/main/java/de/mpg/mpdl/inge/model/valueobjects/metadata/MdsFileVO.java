package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
		return this.size;
	}

  /**
   * Please use FileDbVO.size instead
   */
  @Deprecated
  public void setSize(int size) {
    this.size = size;
  }

  public String getContentCategory() {
    return this.contentCategory;
  }

  public void setContentCategory(String contentCategory) {
    this.contentCategory = contentCategory;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<IdentifierVO> getIdentifiers() {
    return this.identifiers;
  }

  public List<FormatVO> getFormats() {
    return this.formats;
  }

  public String getRights() {
    return this.rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  /**
   * @return the embargoUntil
   */
  public String getEmbargoUntil() {
    return this.embargoUntil;
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
    return this.copyrightDate;
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
    return this.license;
  }

  /**
   * @param license the license to set
   */
  public void setLicense(String license) {
    this.license = license;
  }

  public OA_STATUS getOaStatus() {
    return this.oaStatus;
  }

  public void setOaStatus(OA_STATUS oaStatus) {
    this.oaStatus = oaStatus;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((null == this.contentCategory) ? 0 : this.contentCategory.hashCode());
    result = prime * result + ((null == this.copyrightDate) ? 0 : this.copyrightDate.hashCode());
    result = prime * result + ((null == this.description) ? 0 : this.description.hashCode());
    result = prime * result + ((null == this.embargoUntil) ? 0 : this.embargoUntil.hashCode());
    result = prime * result + ((null == this.formats) ? 0 : this.formats.hashCode());
    result = prime * result + ((null == this.identifiers) ? 0 : this.identifiers.hashCode());
    result = prime * result + ((null == this.license) ? 0 : this.license.hashCode());
    result = prime * result + ((null == this.rights) ? 0 : this.rights.hashCode());
    result = prime * result + ((null == this.oaStatus) ? 0 : this.oaStatus.hashCode());
    result = prime * result + this.size;
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

    if (null == this.contentCategory) {
      if (null != other.contentCategory)
        return false;
    } else if (!this.contentCategory.equals(other.contentCategory))
      return false;

    if (null == this.copyrightDate) {
      if (null != other.copyrightDate)
        return false;
    } else if (!this.copyrightDate.equals(other.copyrightDate))
      return false;

    if (null == this.description) {
      if (null != other.description)
        return false;
    } else if (!this.description.equals(other.description))
      return false;

    if (null == this.embargoUntil) {
      if (null != other.embargoUntil)
        return false;
    } else if (!this.embargoUntil.equals(other.embargoUntil))
      return false;

    if (null == this.formats) {
      if (null != other.formats)
        return false;
    } else if (null == other.formats)
      return false;
    else if (!new HashSet<>(this.formats).containsAll(other.formats) //
        || !new HashSet<>(other.formats).containsAll(this.formats)) {
      return false;
    }

    if (null == this.identifiers) {
      if (null != other.identifiers)
        return false;
    } else if (null == other.identifiers)
      return false;
    else if (!new HashSet<>(this.identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(this.identifiers)) {
      return false;
    }

    if (null == this.license) {
      if (null != other.license)
        return false;
    } else if (!this.license.equals(other.license))
      return false;

    if (null == this.rights) {
      if (null != other.rights)
        return false;
    } else if (!this.rights.equals(other.rights))
      return false;

    if (null == this.oaStatus) {
      if (null != other.oaStatus)
        return false;
    } else if (!this.oaStatus.equals(other.oaStatus))
      return false;

    if (this.size != other.size)
      return false;

    return true;
  }
}
