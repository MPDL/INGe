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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The Util-class offers some static functions that are often needed
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Util
{
	public static final String OPTION_ADD_SELECTOR = "add";
	public static final String OPTION_REMOVE_SELECTOR = "remove";
	public static final String OPTION_REVOKE_GRANT = "remove";
	public static final String OPTION_ACTIVATE_USER_GROUP = "activate";

	private static final String XML_LOG_FILE = "logXML.xml";

	private static final String PROPERTIES_FILE = "src/main/resources/UgTool.properties";

	private static Properties properties = null;

	public static String getProperty(final String key)
	{
		if (properties == null)
		{
			loadProperties();
		}
		if (System.getProperty(key) != null)
		{
			properties.put(key, System.getProperty(key));
		}

		return properties.getProperty(key);
	}

	public static void loadProperties()
	{
		ClassLoader loader = null;
		InputStream in = null;
		try
		{
			if (loader == null)
			{
				loader = Util.class.getClassLoader();
			}
			InputStream is = loader.getResourceAsStream(PROPERTIES_FILE);
			if (is != null)
			{
				properties = new Properties();
				properties.load(is);
			}
		}
		catch (Exception e)
		{
			properties = null;
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Throwable ignore)
				{
					ignore.printStackTrace();
				}
			}
		}
	}

	// transforms an InputString into a String
	public static String inputStreamToString(InputStream in)
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		try
		{
			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line + "\n");
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	// delivers an Instance of HttpClient
	public static HttpClient getHttpClient()
	{
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		return httpClient;
	}

	// Prints the prompt on the console and returns the input as String
	public static String input(String prompt)
	{
		String input;
		System.out.println(prompt);
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextLine();
		return input.trim();
	}

	// Transforms a given String which is separated by "," into a List<String>
	public static List<String> stringToList(final String stringToTransform)
	{
		List<String> list = new ArrayList<String>();
		String transformingString = stringToTransform;
		int index;
		if (transformingString.indexOf(",") != -1)
		{
			while ((index = transformingString.indexOf(",")) != -1)
			{
				list.add(transformingString.substring(0, index).trim());
				transformingString = transformingString.substring(index + 1);
			}
			list.add(transformingString.trim());
		}
		else if (transformingString.length() != 0)
		{
			list.add(transformingString.trim());
		}
		return list;
	}

	// Parses an InputStream and returns a JDOM XML-Document
	public static Document inputStreamToXmlDocument(final InputStream stream)
	{
		SAXBuilder responseParser = new SAXBuilder();
		Document responseXML = null;
		try
		{
			responseXML = responseParser.build(stream);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return responseXML;
	}

	// converts a XML-Object into a String. The String is written to the
	// console.
	public static String xmlToString(final Document xmlDocument)
	{
		XMLOutputter outputter = new XMLOutputter();
		FileOutputStream output;
		try
		{
			output = new FileOutputStream(XML_LOG_FILE);
			outputter.output(xmlDocument, output);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		outputter.setFormat(Format.getPrettyFormat());
		String s = outputter.outputString(xmlDocument);
		System.out.println(s);
		return s;
	}

	// generates the XML-File for creating a UserGroup
	public static String getCreateXml(final String userGroupName, final String userGroupLabel)
	{
		Document doc = new Document();
		Element root = new Element("user-group", "user-group", "http://www.escidoc.de/schemas/usergroup/0.6");
		doc.addContent(root);
		Element properties = new Element("properties", "user-group", "http://www.escidoc.de/schemas/usergroup/0.6");
		root.addContent(properties);
		Element name = new Element("name", "prop", "http://escidoc.de/core/01/properties/");
		name.setText(userGroupName);
		properties.addContent(name);
		Element label = new Element("label", "prop", "http://escidoc.de/core/01/properties/");
		label.setText(userGroupLabel);
		properties.addContent(label);
		Element resources = new Element("resources", "user-group", "http://www.escidoc.de/schemas/usergroup/0.6");
		root.addContent(resources);
		Element grant = new Element("current-grants", "user-group", "http://www.escidoc.de/schemas/usergroup/0.6");
		resources.addContent(grant);

		return xmlToString(doc);
	}

	// calls the getParamXml with 4 parameters (<param> XML-File is genereated
	// depending on the option)
	public static String getParamXml(final String option, final String lastModificationDate, final String ids)
	{
		return Util.getParamXml(option, lastModificationDate, ids, "", "");
	}

	// Generates the <param> XML-File for each option
	public static String getParamXml(final String option, final String lastModificationDate, final String ids,
	        final String nameValue, final String typeValue)
	{
		List<String> idList = stringToList(ids);
		Document doc = new Document();
		Element root = new Element("param");
		root.setAttribute("last-modification-date", lastModificationDate);
		doc.addContent(root);
		for (int index = 0; index < idList.size(); index++)
		{
			if (option.compareTo(OPTION_ADD_SELECTOR) == 0)
			{
				Element selector = new Element("selector");
				selector.setAttribute("name", nameValue);
				selector.setAttribute("type", typeValue);
				selector.setText(idList.get(index));
				root.addContent(selector);
			}
			else if (option.compareTo(OPTION_REMOVE_SELECTOR) == 0)
			{
				Element id = new Element("id");
				id.setText(idList.get(index));
				root.addContent(id);
			}
			else if (option.compareTo(OPTION_REVOKE_GRANT) == 0)
			{
				Element revocationRemark = new Element("revocation-remark");
				revocationRemark.setText("Grant: " + idList.get(index) + " revoked.");
				root.addContent(revocationRemark);
			}
			else if (option.compareTo(OPTION_ACTIVATE_USER_GROUP) == 0)
			{
				break;
			}
			else
			{
				System.out.println("Falsche Option an getSelectorXml uebergeben!");
			}
		}
		return xmlToString(doc);
	}

	// Generates the grantXML
	public static String getGrantXml(final String userGroupID, final String grant)
	{
		Document doc = new Document();

		Element root = new Element("grant", "grants", "http://www.escidoc.de/schemas/grants/0.5");
		doc.addContent(root);

		Element properties = new Element("properties", "grants", "http://www.escidoc.de/schemas/grants/0.5");
		root.addContent(properties);

		Element grantedTo = new Element("granted-to", "srel", "http://escidoc.de/core/01/structural-relations/");
		grantedTo.setAttribute("href", "/aa/user-group/" + userGroupID,
		        Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink"));
		properties.addContent(grantedTo);

		Element role = new Element("role", "srel", "http://escidoc.de/core/01/structural-relations/");
		role.setAttribute("href", "/aa/role/" + grant, Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink"));
		properties.addContent(role);

		// Implementation for the assigning grants to specific Contents
		// Element assignedOn = new Element("assigned-on", "srel",
		// "http://escidoc.de/core/01/structural-relations/");
		// assignedOn.setAttribute("href", "/aa/user-group/" + userGroupIDs,
		// Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink"));
		// properties.addContent(assignedOn);
		return xmlToString(doc);
	}

	// Writes the main selection menu to the console
	public static void printMainMenu()
	{
		System.out.println("\nCOMMANDLIST\n\n" + "Get all UserGroups:                      \"getUG\"\n"
		        + "Create new UserGroups:                   \"createUG\"\n"
		        + "Get a specific UserGroup:                \"getSpecificUG\"\n"
		        + "Delete a Usergroup:                      \"deleteUG\"\n"
		        + "Show grants for specific UserGroup:      \"getGrantUG\"\n"
		        + "Set grants for a specific UserGroup:     \"setGrantUG\"\n"
		        + "Revoke grant from a specific UserGroup:  \"revokeGrantUG\"\n"
		        + "Add selectors to the UserGroup:          \"addSelectorUG\"\n"
		        + "Remove selectors from a UserGroup:       \"removeSelectorUG\"\n"
		        + "Activate UserGroup:                      \"activateUG\"\n"
		        + "Deactivate UserGroup:                    \"deactivateUG\"\n"
		        + "Show Commands:                           \"?\"\n"
		        + "To quit the Tool:                        \"quit\"\n");
	}
}
