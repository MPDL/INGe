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

package de.mpg.mpdl.inge.model.valueobjects;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Parameters for task oriented framework methods.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 05.09.2007
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)

public class TaskParamVO extends ValueObject {
  protected Date lastModificationDate;
  protected String comment;
  protected String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Creates a new instance with the given modification date.
   * 
   * @param lastModificationDate The date of the last modification.
   */
  public TaskParamVO(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  /**
   * Creates a new instance with the given modification date and comment.
   * 
   * @param lastModificationDate The date of the last modification.
   * @param comment A comment.
   */
  public TaskParamVO(Date lastModificationDate, String comment) {
    this.lastModificationDate = lastModificationDate;
    this.comment = comment;
  }

  /**
   * Delivers the last modification date.
   * 
   * @return the lastModificationDate
   */
  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  /**
   * Sets the last modification date.
   * 
   * @param lastModificationDate the lastModificationDate to set
   */
  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  /**
   * Delivers the comment on this task param.
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment on this task param.
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

}
