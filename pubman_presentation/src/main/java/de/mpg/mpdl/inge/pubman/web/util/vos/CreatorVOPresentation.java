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
 * Copyright 2006-20117 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * Presentation wrapper for CreatorVO
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class CreatorVOPresentation extends CreatorVO {
  private static final Logger logger = LogManager.getLogger(CreatorVOPresentation.class);

  private static Properties properties;

  private final EditItemBean bean;
  private final List<CreatorVOPresentation> list;
  private String ouNumbers;

  private PersonVO surrogatePerson;
  private OrganizationVO surrogateOrganization;

  private String autoPasteValue;

  public CreatorVOPresentation(List<CreatorVOPresentation> list, EditItemBean bean) {
    this.list = list;
    this.bean = bean;
  }

  public CreatorVOPresentation(List<CreatorVOPresentation> list, EditItemBean bean, CreatorVO creatorVO) {
    this.list = list;
    this.bean = bean;
    if (null != creatorVO) {
      this.surrogateOrganization = creatorVO.getOrganization();
      this.surrogatePerson = creatorVO.getPerson();
      this.setRole(creatorVO.getRole());
      this.setType(creatorVO.getType());

      if (null != this.surrogateOrganization && null == this.surrogateOrganization.getName()) {
        this.surrogateOrganization.setName("");
      }

      if (null != this.surrogatePerson && null == this.surrogatePerson.getIdentifier()) {
        this.surrogatePerson.setIdentifier(new IdentifierVO());
      }
    }
  }

  /**
   * get the negative list of creator roles as Map for this (server-) instance, depending on the
   * author_roles.properties definitions
   *
   * @return Map filled with all creator roles, which will be excluded
   */
  public static Map<String, String> getCreatorRoleMap() {
    if (null == CreatorVOPresentation.properties || CreatorVOPresentation.properties.isEmpty()) {
      CreatorVOPresentation.properties = CreatorVOPresentation.loadCreatorRoleProperties();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    Map<String, String> propertiesMap = new HashMap<String, String>((Map) CreatorVOPresentation.properties);
    return propertiesMap;
  }

  /**
   * get the negative list of creator roles as properties for this (server-) instance, depending on
   * the author_roles.properties definitions
   *
   * @return Properties filled with all creator roles, which will be excluded
   */
  private static Properties loadCreatorRoleProperties() {
    CreatorVOPresentation.properties = new Properties();
    URL contentCategoryURI = null;
    try {
      contentCategoryURI = CreatorVOPresentation.class.getClassLoader().getResource("author_roles.properties");
      if (null != contentCategoryURI) {
        LogManager.getLogger(CreatorVOPresentation.class).info("Author-Roles properties URI is " + contentCategoryURI);
        InputStream in = contentCategoryURI.openStream();
        CreatorVOPresentation.properties.load(in);
        in.close();
        LogManager.getLogger(CreatorVOPresentation.class).info("Author-Roles properties loaded from " + contentCategoryURI);
      } else {
        LogManager.getLogger(CreatorVOPresentation.class).debug("Author-Roles properties file not found.");
      }
    } catch (Exception e) {
      LogManager.getLogger(CreatorVOPresentation.class).warn("WARNING: Author-Roles properties not found: " + e.getMessage());
    }
    return CreatorVOPresentation.properties;
  }

  /**
   * Add a new creator to the list after this creator.
   *
   * @return Always empty
   */
  public void add() {
    CreatorVOPresentation creatorVOPresentation = new CreatorVOPresentation(this.list, this.bean);
    creatorVOPresentation.init(this.getType());
    creatorVOPresentation.setRole(CreatorRole.AUTHOR);
    int index = this.list.indexOf(this);
    this.list.add(index + 1, creatorVOPresentation);
  }

  public void remove() {
    this.list.remove(this);
  }

  @Override
  public String getTypeString() {
    return (null == this.getType() ? "" : this.getType().toString());
  }

  @Override
  public void setTypeString(String value) {
    if (null != value) {
      this.init(CreatorType.valueOf(value));
    }
  }

  @Override
  public String getRoleString() {
    return (null == this.getRole() ? "" : this.getRole().toString());
  }

  @Override
  public void setRoleString(String value) {
    if (null == value || value.isEmpty()) {
      this.setRole(null);
    } else {
      this.setRole(CreatorRole.valueOf(value));
    }
  }

  public boolean isPersonType() {
    return (CreatorType.PERSON == this.getType());
  }

  public boolean isOrganizationType() {
    return (CreatorType.ORGANIZATION == this.getType());
  }

  public boolean isLast() {
    return (this.equals(this.list.get(this.list.size() - 1)));
  }

  public boolean isSingleCreator() {
    return (1 == this.list.size());
  }

  public String getAutoPasteValue() {
    // Always empty
    return this.autoPasteValue;
  }

  public void setAutoPasteValue(String value) {
    this.autoPasteValue = value;
  }

  public void addOrganization() {
    if (!"".equals(this.autoPasteValue)) {
      logger.debug("Creating new OU from: " + this.autoPasteValue);
      this.bean.setOrganizationPasted(true);
      String[] values = this.autoPasteValue.split(EditItem.AUTOPASTE_INNER_DELIMITER);
      List<OrganizationVOPresentation> creatorOrganizations = this.bean.getCreatorOrganizations();
      OrganizationVOPresentation newOrg = new OrganizationVOPresentation();
      newOrg.setName(values[1]);
      newOrg.setIdentifier(values[0]);
      newOrg.setBean(this.bean);
      creatorOrganizations.add(newOrg);
      this.ouNumbers = creatorOrganizations.size() + "";
      this.autoPasteValue = "";
    }

  }

  public String getOuNumbers() {
    if (this.isPersonType() && null == this.ouNumbers) {
      List<OrganizationVOPresentation> creatorOrganizations = this.bean.getCreatorOrganizations();
      for (OrganizationVO organization : this.surrogatePerson.getOrganizations()) {
        if (null == this.ouNumbers) {
          this.ouNumbers = "";
        } else {
          this.ouNumbers += ",";
        }
        if (creatorOrganizations.contains(organization)) {
          this.ouNumbers += creatorOrganizations.indexOf(organization) + 1;
        }
      }
    }
    return this.ouNumbers;
  }

  public int[] getOus() {
    if (null != this.getOuNumbers() && !"".equals(this.getOuNumbers())) {
      String[] orgArr = this.getOuNumbers().split(",");
      int[] result = new int[orgArr.length];

      try {
        for (int i = 0; i < orgArr.length; i++) {
          if (!"".equals(orgArr[i])) {
            int orgNr = Integer.parseInt(orgArr[i]);
            result[i] = orgNr;
          }
        }
      } catch (NumberFormatException nfe) {
        EditItem editItem = getEditItem();
        editItem.error(editItem.getMessage("EntryIsNotANumber").replace("$1", this.getOuNumbers()));
      } catch (IndexOutOfBoundsException ioobe) {
        EditItem editItem = getEditItem();
        editItem.error(editItem.getMessage("EntryIsNotInValidRange").replace("$1", this.getOuNumbers()));
      } catch (Exception e) {
        EditItem editItem = getEditItem();
        editItem.error(editItem.getMessage("ErrorInOrganizationAssignment").replace("$1", this.getOuNumbers()));
      }
      return result;
    } else {
      return new int[] {};
    }
  }

  public void setOus(int[] values) {
    String result = "";
    for (int i = 0; i < values.length; i++) {
      if (0 < i) {
        result += ",";
      }
      result += values[i];
    }
    this.ouNumbers = result;
  }

  public void setOuNumbers(String ouNumbers) {
    this.ouNumbers = ouNumbers;
  }

  private void init(CreatorType type) {
    if (CreatorType.PERSON == type) {
      this.setType(CreatorType.PERSON);
      this.surrogatePerson = new PersonVO();
      this.surrogatePerson.setIdentifier(new IdentifierVO());
      this.surrogatePerson.getIdentifier().setType(IdentifierVO.IdType.CONE);
      this.surrogatePerson.setOrganizations(new ArrayList<>());
    } else if (CreatorType.ORGANIZATION == type) {
      this.setType(CreatorType.ORGANIZATION);
      this.surrogateOrganization = new OrganizationVO();
      this.surrogateOrganization.setName("");
    }
  }

  @Override
  public PersonVO getPerson() {
    return this.surrogatePerson;
  }

  @Override
  public void setPerson(PersonVO surrogatePerson) {
    this.surrogatePerson = surrogatePerson;
  }

  @Override
  public OrganizationVO getOrganization() {
    return this.surrogateOrganization;
  }

  @Override
  public void setOrganization(OrganizationVO surrogateOrganization) {
    this.surrogateOrganization = surrogateOrganization;
  }

  @Override
  public boolean equals(Object obj) {
    return (this == obj);
  }

  private EditItem getEditItem() {
    return FacesTools.findBean("EditItem");
  }
}
