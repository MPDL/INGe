package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

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
	
	public void valueChanged(ValueChangeEvent event)
	{
		stringList.set(index, event.getNewValue().toString());
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
    	for (int i = index + 2; i < itemList.size(); i++) {
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
