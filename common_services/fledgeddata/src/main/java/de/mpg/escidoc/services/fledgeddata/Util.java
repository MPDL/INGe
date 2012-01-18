/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util
{
	
	public static List createSets (Document doc, List nodeList, String type)
	{
		if (nodeList == null) {nodeList= new ArrayList();}
		
		if (type.equals("collection"))
		{
			NodeList collNodes = doc.getElementsByTagName("imeji:collection");
			NodeList titleNodes = doc.getElementsByTagName("dcterms:title");
			NodeList descNodes = doc.getElementsByTagName("dcterms:description");
			for (int i=0; i< collNodes.getLength(); i++)
			{
				HashMap<String, String> nodeMap = new HashMap<String, String>();
				nodeMap.put("setSpec", collNodes.item(i).getAttributes().item(0).getNodeValue());
				nodeMap.put("setName", titleNodes.item(i).getTextContent());
				nodeMap.put("setDescription", descNodes.item(i).getTextContent());
				nodeList.add(nodeMap);
			}
		}
		
		if (type.equals("album"))
		{
			NodeList albNodes = doc.getElementsByTagName("imeji:album");
			NodeList titleNodes = doc.getElementsByTagName("dcterms:title");
			NodeList descNodes = doc.getElementsByTagName("dcterms:description");
			for (int i=0; i< albNodes.getLength(); i++)
			{
				HashMap<String, String> nodeMap = new HashMap<String, String>();
				nodeMap.put("setSpec", albNodes.item(i).getAttributes().item(0).getNodeValue());
				nodeMap.put("setName", titleNodes.item(i).getTextContent());
				nodeMap.put("setDescription", descNodes.item(i).getTextContent());
				nodeList.add(nodeMap);
			}
		}
		
		return nodeList;
	}
	
}