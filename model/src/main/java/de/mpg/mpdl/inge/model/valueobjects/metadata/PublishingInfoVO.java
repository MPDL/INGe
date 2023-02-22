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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:58
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class PublishingInfoVO extends ValueObject implements Cloneable {
  private String edition;
  private String place;
  private String publisher;

  /**
   * Delivers the version of the described item.
   */
  public String getEdition() {
    return edition;
  }

  /**
   * Delivers the place where the item has been published.
   */
  public String getPlace() {
    return place;
  }

  /**
   * Delivers the name of the institution who has published the item.
   */
  public String getPublisher() {
    return publisher;
  }

  /**
   * Sets the version of the described item.
   * 
   * @param newVal
   */
  public void setEdition(String newVal) {
    edition = newVal;
  }

  /**
   * Sets the place where the item has been published.
   * 
   * @param newVal
   */
  public void setPlace(String newVal) {
    place = newVal;
  }

  /**
   * Sets the name of the institution who has published the item.
   * 
   * @param newVal
   */
  public void setPublisher(String newVal) {
    publisher = newVal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    PublishingInfoVO vo = new PublishingInfoVO();
    vo.setEdition(getEdition());
    vo.setPlace(getPlace());
    vo.setPublisher(getPublisher());
    return vo;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((edition == null) ? 0 : edition.hashCode());
    result = prime * result + ((place == null) ? 0 : place.hashCode());
    result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
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

    PublishingInfoVO other = (PublishingInfoVO) obj;

    if (edition == null) {
      if (other.edition != null)
        return false;
    } else if (!edition.equals(other.edition))
      return false;

    if (place == null) {
      if (other.place != null)
        return false;
    } else if (!place.equals(other.place))
      return false;

    if (publisher == null) {
      if (other.publisher != null)
        return false;
    } else if (!publisher.equals(other.publisher))
      return false;

    return true;
  }

}
