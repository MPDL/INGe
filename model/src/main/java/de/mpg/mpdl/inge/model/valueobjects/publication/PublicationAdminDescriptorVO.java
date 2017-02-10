/*
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

package de.mpg.mpdl.inge.model.valueobjects.publication;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.types.Validatable;
import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;

/**
 * Implementation of an admin descriptor for PubMan publications.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PublicationAdminDescriptorVO extends AdminDescriptorVO implements Validatable {

  public enum Workflow {
    STANDARD, SIMPLE
  }

  private List<MdsPublicationVO.Genre> allowedGenres = new ArrayList<MdsPublicationVO.Genre>();

  private List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications =
      new ArrayList<MdsPublicationVO.SubjectClassification>();

  private ItemRO templateItem;

  private String validationSchema;

  private String visibilityOfReferences;

  private Workflow workflow;

  private String contactEmail;

  public List<MdsPublicationVO.Genre> getAllowedGenres() {
    return allowedGenres;
  }

  public void setAllowedGenres(List<MdsPublicationVO.Genre> allowedGenres) {
    this.allowedGenres = allowedGenres;
  }

  /**
   * @return the allowedSubjectClassifications
   */
  public List<MdsPublicationVO.SubjectClassification> getAllowedSubjectClassifications() {
    return allowedSubjectClassifications;
  }

  /**
   * @param allowedSubjectClassifications the allowedSubjectClassifications to set
   */
  public void setAllowedSubjectClassifications(
      List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications) {
    this.allowedSubjectClassifications = allowedSubjectClassifications;
  }

  public ItemRO getTemplateItem() {
    return templateItem;
  }

  public void setTemplateItem(ItemRO templateItem) {
    this.templateItem = templateItem;
  }

  public String getValidationSchema() {
    return validationSchema;
  }

  public void setValidationSchema(String validationSchema) {
    this.validationSchema = validationSchema;
  }

  public String getVisibilityOfReferences() {
    return visibilityOfReferences;
  }

  public void setVisibilityOfReferences(String visibilityOfReferences) {
    this.visibilityOfReferences = visibilityOfReferences;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  /**
   * @return the contactEmail
   */
  public String getContactEmail() {
    return contactEmail;
  }

  /**
   * @param contactEmail the contactEmail to set
   */
  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((allowedGenres == null) ? 0 : allowedGenres.hashCode());
    result =
        prime
            * result
            + ((allowedSubjectClassifications == null) ? 0 : allowedSubjectClassifications
                .hashCode());
    result = prime * result + ((contactEmail == null) ? 0 : contactEmail.hashCode());
    result = prime * result + ((templateItem == null) ? 0 : templateItem.hashCode());
    result = prime * result + ((validationSchema == null) ? 0 : validationSchema.hashCode());
    result =
        prime * result + ((visibilityOfReferences == null) ? 0 : visibilityOfReferences.hashCode());
    result = prime * result + ((workflow == null) ? 0 : workflow.hashCode());
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

    PublicationAdminDescriptorVO other = (PublicationAdminDescriptorVO) obj;

    if (allowedGenres == null) {
      if (other.allowedGenres != null)
        return false;
    } else if (other.allowedGenres == null)
      return false;
    else if (!allowedGenres.containsAll(other.allowedGenres) //
        || !other.allowedGenres.containsAll(allowedGenres)) {
      return false;
    }

    if (allowedSubjectClassifications == null) {
      if (other.allowedSubjectClassifications != null)
        return false;
    } else if (other.allowedSubjectClassifications == null)
      return false;
    else if (!allowedSubjectClassifications.containsAll(other.allowedSubjectClassifications) //
        || !other.allowedSubjectClassifications.containsAll(allowedSubjectClassifications)) {
      return false;
    }

    if (contactEmail == null) {
      if (other.contactEmail != null)
        return false;
    } else if (!contactEmail.equals(other.contactEmail))
      return false;

    if (templateItem == null) {
      if (other.templateItem != null)
        return false;
    } else if (!templateItem.equals(other.templateItem))
      return false;

    if (validationSchema == null) {
      if (other.validationSchema != null)
        return false;
    } else if (!validationSchema.equals(other.validationSchema))
      return false;

    if (visibilityOfReferences == null) {
      if (other.visibilityOfReferences != null)
        return false;
    } else if (!visibilityOfReferences.equals(other.visibilityOfReferences))
      return false;

    if (workflow != other.workflow)
      return false;

    return true;
  }

}
