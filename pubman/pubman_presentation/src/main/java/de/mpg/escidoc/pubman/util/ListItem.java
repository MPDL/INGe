/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;

public class ListItem
{
    private int index;
    private String value;
    private List<String> stringList;
    private List<ListItem> itemList;

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public List<String> getStringList() {
        return stringList;
    }
    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
    public List<ListItem> getItemList() {
        return itemList;
    }
    public void setItemList(List<ListItem> itemList) {
        this.itemList = itemList;
    }
    
    public String getAlternativeValue() throws Exception
    {
        String locale = ((InternationalizationHelper)InternationalizedImpl.getSessionBean(InternationalizationHelper.class)).getLocale();
        return CommonUtils.getConeLanguageName(value, locale);
    }

    public void valueChanged(ValueChangeEvent event)
    {
        String newVal = "";
        if(event != null && event.getNewValue() != null)
        {
            newVal = event.getNewValue().toString();
        }
        stringList.set(index, newVal);
    }
    
    public String addItem()
    {
        stringList.add(index + 1, "");
        ListItem item = new ListItem();
        item.setValue("");
        item.setIndex(index + 1);
        item.setStringList(stringList);
        item.setItemList(itemList);
        itemList.add(index + 1, item);
        for (int i = index + 2; i < itemList.size(); i++)
        {
            itemList.get(i).setIndex(i);
        }
        return null;
    }
    
    public String removeItem()
    {
        stringList.remove(index);
        itemList.remove(index);
        for (int i = index; i < itemList.size(); i++) {
            itemList.get(i).setIndex(i);
        }
        return null;
    }
    
    public boolean getMoreThanOne()
    {
        return (stringList.size() > 1);
    }
    
    public String toString()
    {
        return value;
    }
}
