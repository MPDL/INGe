/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.IgnoreForCleanup;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * Identifiers can be internal or external.
 * 
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:59:09
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class IdentifierVO extends ValueObject implements Cloneable {
  /**
   * The possible types of the identifier.
   * 
   * @updated 05-Sep-2007 12:59:09
   */
  public enum IdType
  {
    ADS("https://archive.org/services/purl/domain/ads"),
    ARXIV("http://purl.org/escidoc/metadata/terms/0.1/ARXIV"), //
    BIBTEX_CITEKEY("http://purl.org/escidoc/metadata/terms/0.1/BIBTEX-CITEKEY"), //
    BMC("http://purl.org/escidoc/metadata/terms/0.1/BMC"), //
    CONE("http://purl.org/escidoc/metadata/terms/0.1/CONE"), //
    DOI("http://purl.org/escidoc/metadata/terms/0.1/DOI"), //
    EDOC("http://purl.org/escidoc/metadata/terms/0.1/EDOC"), //
    ESCIDOC("http://purl.org/escidoc/metadata/terms/0.1/ESCIDOC"), // wegen Altdaten noch notwendig
    GRANT_ID("http://purl.org/escidoc/metadata/terms/0.1/GRANT-ID"), // anderer Zusammenhang (Projektinfo)
    ISBN("http://purl.org/escidoc/metadata/terms/0.1/ISBN"), //
    ISI("http://purl.org/escidoc/metadata/terms/0.1/ISI"), //
    ISSN("http://purl.org/escidoc/metadata/terms/0.1/ISSN"), //
    MDB_ID("http://purl.org/escidoc/metadata/terms/0.1/MDB-ID"), //
    MODELMETHOD("http://purl.org/escidoc/metadata/terms/0.1/MODELMETHOD"), //
    OATYPE("http://purl.org/escidoc/metadata/terms/0.1/OATYPE"), //
    OPEN_AIRE("http://purl.org/escidoc/metadata/terms/0.1/OPEN-AIRE"), // anderer Zusammenhang (Fundingorganization)
    ORGANISATIONALK("http://purl.org/escidoc/metadata/terms/0.1/ORGANISATIONALK"), //
    OTHER("http://purl.org/escidoc/metadata/terms/0.1/OTHER"), //
    PATENT_APPLICATION_NR("http://purl.org/escidoc/metadata/terms/0.1/PATENT-APPLICATION-NR"), //
    PATENT_NR("http://purl.org/escidoc/metadata/terms/0.1/PATENT-NR"), //
    PATENT_PUBLICATION_NR("http://purl.org/escidoc/metadata/terms/0.1/PATENT-PUBLICATION-NR"), //
    PII("http://purl.org/escidoc/metadata/terms/0.1/PII"), //
    PMC("http://purl.org/escidoc/metadata/terms/0.1/PMC"), //
    PMID("http://purl.org/escidoc/metadata/terms/0.1/PMID"), //
    PND("http://purl.org/escidoc/metadata/terms/0.1/PND"), //
    PUBLISHER("http://purl.org/escidoc/metadata/terms/0.1/PUBLISHER"), //
    REGIONALK("http://purl.org/escidoc/metadata/terms/0.1/REGIONALK"), //
    REPORT_NR("http://purl.org/escidoc/metadata/terms/0.1/REPORT-NR"), //
    RESEARCHTK("http://purl.org/escidoc/metadata/terms/0.1/RESEARCHTK"), //
    SSRN("http://purl.org/escidoc/metadata/terms/0.1/SSRN"), //
    URI("http://purl.org/escidoc/metadata/terms/0.1/URI"), //
    URN("http://purl.org/escidoc/metadata/terms/0.1/URN"), //
    WORKINGGROUP("http://purl.org/escidoc/metadata/terms/0.1/WORKINGGROUP"), //
    ZDB("http://purl.org/escidoc/metadata/terms/0.1/ZDB");

  private String uri;

  private IdType(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return uri;
  }}

  private String id;

  @IgnoreForCleanup
  private IdType type;

  /**
   * Creates a new instance.
   */
  public IdentifierVO() {
    super();
  }

  /**
   * Creates a new instance with the given type and the given identifier.
   * 
   * @param type
   * @param id
   */
  public IdentifierVO(IdType type, String id) {
    super();
    this.type = type;
    this.id = id;
  }

  /**
   * Delivers the identifier.
   */
  public String getId() {
    return id;
  }

  /**
   * Delivers the type of the identifier.
   */
  public IdType getType() {
    return type;
  }

  /**
   * Sets the identifier.
   * 
   * @param newVal
   */
  public void setId(String newVal) {
    id = newVal;
  }

  /**
   * Sets the type of the identifier.
   * 
   * @param newVal
   */
  public void setType(IdType newVal) {
    type = newVal;
  }

  public Object clone() {
    IdentifierVO clone = new IdentifierVO();
    clone.setId(getId());
    clone.setType(getType());
    return clone;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    IdentifierVO other = (IdentifierVO) obj;

    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;

    if (type != other.type)
      return false;

    return true;
  }

  /**
   * Returns the value of the type Enum as a String. If the Enum is not set, an empty String is
   * returned.
   * 
   * @return the value of the type Enum
   */
  @JsonIgnore
  public String getTypeString() {
    if (getType() == null || getType().toString() == null) {
      return "";
    }
    return getType().toString();
  }

  /**
   * Sets the value of the type Enum by a String.
   * 
   * @param newValString
   */
  @JsonIgnore
  public void setTypeString(String newValString) {
    if (newValString == null || newValString.length() == 0) {
      setType(null);
    } else {
      IdentifierVO.IdType newVal = IdentifierVO.IdType.valueOf(newValString);
      setType(newVal);
    }
  }

}
