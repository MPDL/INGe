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

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * Identifiers can be internal or external.
 *
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:59:09
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    BIORXIV("https://archive.org/services/purl/domain/BIORXIV"),
    BMC("http://purl.org/escidoc/metadata/terms/0.1/BMC"), //
    CHEMRXIV("https://archive.org/services/purl/domain/CHEMRXIV"),
    CONE("http://purl.org/escidoc/metadata/terms/0.1/CONE"), //
    DOI("http://purl.org/escidoc/metadata/terms/0.1/DOI"), //
    EARTHARXIV("https://archive.org/services/purl/domain/EARTHARXIV"),
    EDARXIV("https://archive.org/services/purl/domain/EDARXIV"),
    EDOC("http://purl.org/escidoc/metadata/terms/0.1/EDOC"), //
    ESCIDOC("http://purl.org/escidoc/metadata/terms/0.1/ESCIDOC"), // wegen Altdaten noch notwendig
    ESS_OPEN_ARCHIVE("https://archive.org/services/purl/domain/ESS_OPEN_ARCHIVE"),
    GRANT_ID("http://purl.org/escidoc/metadata/terms/0.1/GRANT-ID"), // anderer Zusammenhang (Projektinfo)
    ISBN("http://purl.org/escidoc/metadata/terms/0.1/ISBN"), //
    ISI("http://purl.org/escidoc/metadata/terms/0.1/ISI"), //
    ISSN("http://purl.org/escidoc/metadata/terms/0.1/ISSN"), //
    MDB_ID("http://purl.org/escidoc/metadata/terms/0.1/MDB-ID"), //
    MEDRXIV("https://archive.org/services/purl/domain/MEDRXIV"),
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
    PSYARXIV("https://archive.org/services/purl/domain/PSYARXIV"),
    PUBLISHER("http://purl.org/escidoc/metadata/terms/0.1/PUBLISHER"), //
    REGIONALK("http://purl.org/escidoc/metadata/terms/0.1/REGIONALK"), //
    REPORT_NR("http://purl.org/escidoc/metadata/terms/0.1/REPORT-NR"), //
    RESEARCHTK("http://purl.org/escidoc/metadata/terms/0.1/RESEARCHTK"), //
    RESEARCH_SQUARE("https://archive.org/services/purl/domain/RESEARCH_SQUARE"),
    SOCARXIV("https://archive.org/services/purl/domain/SOCARXIV"),
    SSRN("http://purl.org/escidoc/metadata/terms/0.1/SSRN"), //
    URI("http://purl.org/escidoc/metadata/terms/0.1/URI"), //
    URN("http://purl.org/escidoc/metadata/terms/0.1/URN"), //
    WORKINGGROUP("http://purl.org/escidoc/metadata/terms/0.1/WORKINGGROUP"), //
    ZDB("http://purl.org/escidoc/metadata/terms/0.1/ZDB");

  private final String uri;

  IdType(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return this.uri;
  }}

  private String id;

  @IgnoreForCleanup
  private IdType type;

  /**
   * Creates a new instance.
   */
  public IdentifierVO() {
  }

  /**
   * Creates a new instance with the given type and the given identifier.
   *
   * @param type
   * @param id
   */
  public IdentifierVO(IdType type, String id) {
    this.type = type;
    this.id = id;
  }

  /**
   * Delivers the identifier.
   */
  public String getId() {
    return this.id;
  }

  /**
   * Delivers the type of the identifier.
   */
  public IdType getType() {
    return this.type;
  }

  /**
   * Sets the identifier.
   *
   * @param newVal
   */
  public void setId(String newVal) {
    this.id = newVal;
  }

  /**
   * Sets the type of the identifier.
   *
   * @param newVal
   */
  public void setType(IdType newVal) {
    this.type = newVal;
  }

  public final IdentifierVO clone() {
    try {
      IdentifierVO clone = (IdentifierVO) super.clone();
      if (null != clone.type) {
        clone.type = this.type;
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.id) ? 0 : this.id.hashCode());
    result = prime * result + ((null == this.type) ? 0 : this.type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    IdentifierVO other = (IdentifierVO) obj;

    if (null == this.id) {
      if (null != other.id)
        return false;
    } else if (!this.id.equals(other.id))
      return false;

    if (this.type != other.type)
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
    if (null == this.type || null == this.type.toString()) {
      return "";
    }
    return this.type.toString();
  }

  /**
   * Sets the value of the type Enum by a String.
   *
   * @param newValString
   */
  @JsonIgnore
  public void setTypeString(String newValString) {
    if (null == newValString || newValString.isEmpty()) {
      this.type = null;
    } else {
      IdentifierVO.IdType newVal = IdentifierVO.IdType.valueOf(newValString);
      this.type = newVal;
    }
  }

}
