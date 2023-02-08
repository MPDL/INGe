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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;

/**
 * An account user is a user who is registered by username (i. e. userID) and password.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:46
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class AccountUserVO extends ValueObject {
  private boolean active;
  /**
   * The references of the affiliations the account user is associated to.
   */
  private List<AffiliationRO> affiliations = new java.util.ArrayList<AffiliationRO>();
  private String email;
  private Date lastModificationDate;


  /**
   * name + surname
   */
  private String name;
  private String password;
  private AccountUserRO reference;
  private String userid;

  /**
   * User attributes. Attributes have to be retrieved and set manually, as they are not part of the
   * default user-account.xml
   */
  private List<UserAttributeVO> attributes = new ArrayList<UserAttributeVO>();


  /**
   * The handle for the authenticated user, given by the framework.
   */
  private String handle;
  /**
   * Caution: This list is NOT filled automatically by JiBX or the AccountUserVO class itself when
   * creating the VO.
   */
  private List<GrantVO> grants = new java.util.ArrayList<GrantVO>();
  private List<GrantVO> grantsWithoutAudience = new ArrayList<GrantVO>();

  private AccountUserRO creator;

  private java.util.Date creationDate;

  private AccountUserRO modifiedBy;

  /**
   * Delivers the active flag of the user account. The active flag is true if the user account can
   * be used, false otherwise.
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Delivers true if the granted role is of type 'depositor' for any object.
   */
  @JsonIgnore
  public boolean isDepositor() {
    boolean depositor = false;
    for (GrantVO grant : this.grants) {
      if (PredefinedRoles.DEPOSITOR.frameworkValue().contentEquals(grant.getRole())) {
        depositor = true;
      }
    }
    return depositor;
  }

  /**
   * Delivers true if the granted role is of type 'moderator' for any object.
   */
  @JsonIgnore
  public boolean isModerator() {
    boolean moderator = false;
    for (GrantVO grant : this.grants) {

      if (PredefinedRoles.MODERATOR.frameworkValue().contentEquals(grant.getRole())) {
        moderator = true;
        break;
      }
    }
    return moderator;
  }


  /**
   * Delivers true if the granted role is of type 'reporter' for any object.
   */
  @JsonIgnore
  public boolean isReporter() {
    boolean reporter = false;
    for (GrantVO grant : this.grants) {
      if (grant.getRole().equals(PredefinedRoles.REPORTER.frameworkValue())) {
        reporter = true;
        break;
      }
    }
    return reporter;
  }

  /**
   * Delivers true if the granted role is of type 'moderator' for the given object (normally a
   * PubCollection).
   * 
   * @param refObj true, if the user has the moderator role
   * @return true if the granted role is of type 'moderator' for the given object
   */
  public boolean isModerator(ReferenceObject refObj) {
    if (refObj == null) {
      throw new IllegalArgumentException(getClass().getSimpleName() + ":isModerator:objectRef is null");
    }
    boolean moderator = false;
    for (GrantVO grant : this.grants) {
      // every system administrator is a moderator, too
      //      if (grant.getRole().equals("escidoc:role-system-administrator")) {
      if (PredefinedRoles.SYSADMIN.frameworkValue().contentEquals(grant.getRole())) {
        moderator = true;
      }

      if (PredefinedRoles.MODERATOR.frameworkValue().contentEquals(grant.getRole())) {
        if (grant.getObjectRef() != null && grant.getObjectRef().equals(refObj.getObjectId())) {
          moderator = true;
        }
      }
    }
    return moderator;
  }

  /**
   * Delivers the list of affiliations the account user is affiliated to.
   */
  public List<AffiliationRO> getAffiliations() {
    return affiliations;
  }

  /**
   * Delivers the email address of the account user.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Delivers the list of the account users' grants. Caution: This list is NOT filled automatically
   * by JiBX or the AccountUserVO class itself when creating the VO.
   */
  public List<GrantVO> getGrants() {
    return grants;
  }

  /**
   * @ return the userGrtants without grants of type audience
   */
  @JsonIgnore
  public List<GrantVO> getGrantsWithoutAudienceGrants() {
    return this.grantsWithoutAudience;
  }

  /**
   * Delivers the handle for the authenticated user, given back by the framework.
   */
  public String getHandle() {
    return handle;
  }

  /**
   * Delivers the name of the account user, i. e. first and last name.
   */
  public String getName() {
    return name;
  }

  /**
   * Delivers the password of the account user. The password has to be encrypted.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Delivers the account users' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public AccountUserRO getReference() {
    return reference;
  }

  /**
   * Delivers the user-id of the account user. The user-id is a unique id for the user within the
   * system.
   */
  public String getUserid() {
    return userid;
  }

  /**
   * Sets the active flag of the user account. The active flag is true if the user account can be
   * used, false otherwise.
   * 
   * @param newVal newVal
   */
  public void setActive(boolean newVal) {
    this.active = newVal;
  }

  /**
   * Sets the email address of the account user.
   * 
   * @param newVal newVal
   */
  public void setEmail(String newVal) {
    this.email = newVal;
  }

  /**
   * Sets the handle for the authenticated user, given back by the framework.
   * 
   * @param newVal
   */
  public void setHandle(String newVal) {
    this.handle = newVal;
  }

  /**
   * Sets the name of the account user, i. e. first and last name.
   * 
   * @param newVal newVal
   */
  public void setName(String newVal) {
    this.name = newVal;
  }

  /**
   * Sets the password of the account user. The password has to be encrypted.
   * 
   * @param newVal newVal
   */
  public void setPassword(String newVal) {
    this.password = newVal;
  }

  /**
   * Sets the account users' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * @param newVal newVal
   */
  public void setReference(AccountUserRO newVal) {
    this.reference = newVal;
  }

  /**
   * Sets the user-id of the account user. The user-id is a unique id for the user within the
   * system.
   * 
   * @param newVal newVal
   */
  public void setUserid(String newVal) {
    this.userid = newVal;
  }

  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public void setAttributes(List<UserAttributeVO> attributes) {
    this.attributes = attributes;
  }

  /**
   * User attributes. Attributes have to be retrieved and set seperately, as they are not part of
   * the default user-account.xml
   */
  public List<UserAttributeVO> getAttributes() {
    return attributes;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((affiliations == null) ? 0 : affiliations.hashCode());
    result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((grants == null) ? 0 : grants.hashCode());
    result = prime * result + ((grantsWithoutAudience == null) ? 0 : grantsWithoutAudience.hashCode());
    result = prime * result + ((handle == null) ? 0 : handle.hashCode());
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((reference == null) ? 0 : reference.hashCode());
    result = prime * result + ((userid == null) ? 0 : userid.hashCode());
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

    AccountUserVO other = (AccountUserVO) obj;

    if (active != other.active)
      return false;

    if (affiliations == null) {
      if (other.affiliations != null)
        return false;
    } else if (other.affiliations == null)
      return false;
    else if (!affiliations.containsAll(other.affiliations) //
        || !other.affiliations.containsAll(affiliations)) {
      return false;
    }

    if (attributes == null) {
      if (other.attributes != null)
        return false;
    } else if (other.attributes == null)
      return false;
    else if (!attributes.containsAll(other.attributes) //
        || !other.attributes.containsAll(attributes)) {
      return false;
    }

    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;

    if (grants == null) {
      if (other.grants != null)
        return false;
    } else if (other.grants == null)
      return false;
    else if (!grants.containsAll(other.grants) //
        || !other.grants.containsAll(grants)) {
      return false;
    }

    if (grantsWithoutAudience == null) {
      if (other.grantsWithoutAudience != null)
        return false;
    } else if (other.grantsWithoutAudience == null)
      return false;
    else if (!grantsWithoutAudience.containsAll(other.grantsWithoutAudience) //
        || !other.grantsWithoutAudience.containsAll(grantsWithoutAudience)) {
      return false;
    }

    if (handle == null) {
      if (other.handle != null)
        return false;
    } else if (!handle.equals(other.handle))
      return false;

    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;

    if (reference == null) {
      if (other.reference != null)
        return false;
    } else if (!reference.equals(other.reference))
      return false;

    if (userid == null) {
      if (other.userid != null)
        return false;
    } else if (!userid.equals(other.userid))
      return false;

    return true;
  }

  public AccountUserRO getCreator() {
    return creator;
  }

  public void setCreator(AccountUserRO creator) {
    this.creator = creator;
  }

  public java.util.Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(java.util.Date creationDate) {
    this.creationDate = creationDate;
  }

  public AccountUserRO getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(AccountUserRO modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   */
  public boolean alreadyExistsInFramework() {
    return (this.reference != null);
  }


}
