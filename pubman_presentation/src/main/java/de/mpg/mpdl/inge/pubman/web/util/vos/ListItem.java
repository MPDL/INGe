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
package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.List;

import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import jakarta.faces.event.ValueChangeEvent;

public class ListItem {
  private int index;
  private String value;
  private List<String> stringList;
  private List<ListItem> itemList;

  private final InternationalizationHelper i18nHelper = FacesTools.findBean("InternationalizationHelper");

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<String> getStringList() {
    return this.stringList;
  }

  public void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public List<ListItem> getItemList() {
    return this.itemList;
  }

  public void setItemList(List<ListItem> itemList) {
    this.itemList = itemList;
  }

  public String getAlternativeValue() throws Exception {
    String locale = this.i18nHelper.getLocale();

    return CommonUtils.getConeLanguageName(this.value, locale);
  }

  public void valueChanged(ValueChangeEvent event) {
    String newVal = "";
    if (null != event && null != event.getNewValue()) {
      newVal = event.getNewValue().toString();
    }

    this.stringList.set(this.index, newVal);
  }

  public void addItem() {
    this.stringList.add(this.index + 1, "");
    ListItem item = new ListItem();
    item.value = "";
    item.index = this.index + 1;
    item.stringList = this.stringList;
    item.itemList = this.itemList;
    this.itemList.add(this.index + 1, item);

    for (int i = this.index + 2; i < this.itemList.size(); i++) {
      this.itemList.get(i).index = i;
    }
  }

  public void removeItem() {
    this.stringList.remove(this.index);
    this.itemList.remove(this.index);

    for (int i = this.index; i < this.itemList.size(); i++) {
      this.itemList.get(i).index = i;
    }
  }

  public boolean getMoreThanOne() {
    return (1 < this.stringList.size());
  }

  @Override
  public String toString() {
    return this.value;
  }
}
