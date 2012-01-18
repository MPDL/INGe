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
 * f&#252;r wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur F&#246;rderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.http;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jdom.Document;
import org.jdom.Element;

import de.mpg.escidoc.util.Util;

/**
 * The UserGroup-Class implements the actions available on UserGroups.
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserGroup
{
	private final String FRAMEWORK_URL;
	private final String USER_HANDLE;
	private HttpClient client;

	public UserGroup(final HttpClient httpClient, final String frameworkUrl, final String userHandle)
	{
		this.FRAMEWORK_URL = frameworkUrl;
		this.USER_HANDLE = userHandle;
		this.client = httpClient;
	}

	// returns all UserGroups
	public void getAllUserGroups()
	{
		Document responseXML = null;
		if (this.USER_HANDLE != null)
		{
			GetMethod get = new GetMethod(FRAMEWORK_URL + "/aa/user-groups");
			get.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				this.client.executeMethod(get);
				System.out.println("Server response: ");
				responseXML = Util.inputStreamToXmlDocument(get.getResponseBodyAsStream());
				Util.xmlToString(responseXML);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in getAllUserGroups: No userHandle available");
		}

	}

	// creates a new UserGroup
	@SuppressWarnings("deprecation")
	public Boolean createUserGroups()
	{
		String userGroupName = Util.input("Name of the UserGroup to create: ");
		String userGroupLabel = Util.input("Label of the UserGroup to create: ");
		Document responseXML = null;
		if (this.USER_HANDLE != null)
		{
			PutMethod put = new PutMethod(this.FRAMEWORK_URL + "/aa/user-group");
			put.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				put.setRequestEntity(new StringRequestEntity(Util.getCreateXml(userGroupName, userGroupLabel)));
				this.client.executeMethod(put);
				if (put.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + put.getStatusCode());
					return false;
				}
				System.out.println("Server response: ");
				responseXML = Util.inputStreamToXmlDocument(put.getResponseBodyAsStream());
				Util.xmlToString(responseXML);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in creatUserGroup: No userHandle available");
		}
		return true;
	}

	// returns info for one specific UserGroup
	public Boolean getSpecificUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID to view grants:");
		// String userGroupID = "escidoc:27004";
		Document userGroupXml = this.getUserGroupXML(userGroupID);
		if (userGroupXml == null)
		{
			return false;
		}
		return true;
	}

	// deletes a specific UserGroup
	public Boolean deleteUserGroup()
	{
		String userGroupID = Util.input("ID of the UserGroup to be deleted: ");
		// String userGroupID = "escidoc:27001";
		if (this.USER_HANDLE != null)
		{
			DeleteMethod delete = new DeleteMethod(FRAMEWORK_URL + "/aa/user-group/" + userGroupID);
			delete.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				this.client.executeMethod(delete);
				if (delete.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + delete.getStatusCode());
					return false;
				}
				System.out.println("Usergroup " + userGroupID + " deleted");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in deleteUserGroup: No userHandle available");
		}
		return true;
	}

	// returns all grants for the given UserGroup
	public Boolean getGrantsOfUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID to view grants:");
		Document responseXML = null;
		if (this.USER_HANDLE != null)
		{
			GetMethod get = new GetMethod(FRAMEWORK_URL + "/aa/user-group/" + userGroupID + "/resources/current-grants");
			get.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				this.client.executeMethod(get);
				if (get.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + get.getStatusCode());
					return false;
				}
				System.out.println("Server response: ");
				responseXML = Util.inputStreamToXmlDocument(get.getResponseBodyAsStream());
				Util.xmlToString(responseXML);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in getGrantsOfUserGroup: No userHandle available");
		}
		return true;
	}

	// sets Grants for a specific UserGroup
	@SuppressWarnings("deprecation")
	public boolean setGrantsOfUserGroup()
	{
		// String userGroupID = "escidoc:27004";
		// System.out.println("Set userGroupID:" + userGroupID);
		// String grantedRoles =
		// "escidoc:role-ou-administrator,escidoc:role-administrator";
		// System.out.println("Set Grants to: " + grantedRoles);
		String userGroupID = Util.input("Enter UserGroupID whose grants will be modified:");
		String grantedRoles = Util.input("Enter the Grants which will be added to the Usergroup( " + userGroupID
		        + ") (comma separated):");
		List<String> grantedRolesList = Util.stringToList(grantedRoles);
		Document responseXML = null;

		for (String role : grantedRolesList)
		{
			if (this.USER_HANDLE != null)
			{
				PutMethod put = new PutMethod(FRAMEWORK_URL + "/aa/user-group/" + userGroupID
				        + "/resources/grants/grant");
				put.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
				try
				{
					System.out.println("Request body sent to Server: ");
					put.setRequestEntity(new StringRequestEntity(Util.getGrantXml(userGroupID, role)));
					this.client.executeMethod(put);
					if (put.getStatusCode() != 200)
					{
						System.out.println("Server StatusCode: " + put.getStatusCode());
						return false;
					}
					System.out.println("Server response: ");
					responseXML = Util.inputStreamToXmlDocument(put.getResponseBodyAsStream());
					Util.xmlToString(responseXML);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Error in setGrantsOfUserGroup: No userHandle available");
			}
		}
		return true;
	}

	// Revokes a Grant from a UserGroup
	@SuppressWarnings("deprecation")
	public Boolean revokeGrantFromUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID where you want to revoke a grant:");
		String grantID = Util.input("Enter GrantID to remove the grant in the UserGroup: ");
		// String userGroupID = "escidoc:27004";
		// String grantID = "escidoc:27011";
		Document userGroupXML = this.getUserGroupXML(userGroupID);
		if (userGroupXML == null) 
		{
			return false;
		}
		Element rootElement = userGroupXML.getRootElement();
		String lastModificationDate = rootElement.getAttributeValue("last-modification-date");
		System.out.println("lastModificationDate: " + lastModificationDate);
		if (this.USER_HANDLE != null)
		{
			PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/user-group/" + userGroupID
			        + "/resources/grants/grant/" + grantID + "/revoke-grant");
			post.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				post.setRequestEntity(new StringRequestEntity(Util.getParamXml(Util.OPTION_REMOVE_SELECTOR,
				        lastModificationDate, grantID)));
				this.client.executeMethod(post);
				if (post.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + post.getStatusCode());
					return false;
				}
				System.out.println("Grant " + userGroupID + " revoked from " + userGroupID);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in revokeGrantFromUserGroup: No userHandle available");
		}
		return true;
	}

	// Adds selectors to a specific UserGroup
	@SuppressWarnings("deprecation")
	public Boolean addSelectorToUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID whose Selectors you want to edit:");
		String selectorType = Util
		        .input("Which kind of Selectors do you want to add (\"user-account\" / \"user-group\" / \"organizational-unit\"): ");
		String selectors = Util.input("Enter the UserIDs you want to add as Selectors (comma separated): ");
		// String userGroupID = "escidoc:27004";
		// String selectors = "escidoc:exuser1,escidoc:3029";
		Document userGroupXML = this.getUserGroupXML(userGroupID);
		if (userGroupXML == null) 
		{
			return false;
		}
		Element rootElement = userGroupXML.getRootElement();
		String lastModificationDate = rootElement.getAttributeValue("last-modification-date");
		if (this.USER_HANDLE != null)
		{
			Document responseXML = null;
			PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/user-group/" + userGroupID + "/selectors/add");
			post.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				post.setRequestEntity(new StringRequestEntity(Util.getParamXml(Util.OPTION_ADD_SELECTOR,
				        lastModificationDate, selectors, (selectorType.equalsIgnoreCase("organizational-unit") ? "o"
				                : selectorType), selectorType.equalsIgnoreCase("organizational-unit") ? "user-attribute"
				                : "internal")));
				this.client.executeMethod(post);
				if (post.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + post.getStatusCode());
					return false;
				}
				System.out.println("Server response: ");
				responseXML = Util.inputStreamToXmlDocument(post.getResponseBodyAsStream());
				Util.xmlToString(responseXML);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in addSelectorToUserGroup: No userHandle available");
		}
		return true;
	}

	// Removes selectors from a specific UserGroup
	@SuppressWarnings("deprecation")
	public Boolean removeSelectorFromUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID whose Selectors you want to edit:");
		String selectors = Util.input("Enter SelectorIDs to remove from the UserGroup (comma separated): ");
		// String userGroupID = "escidoc:27004";
		// String selectors = "escidoc:27014,escidoc:27013";
		Document userGroupXML = this.getUserGroupXML(userGroupID);
		if (userGroupXML == null) 
		{
			return false;
		}
		Element rootElement = userGroupXML.getRootElement();
		String lastModificationDate = rootElement.getAttributeValue("last-modification-date");
		if (this.USER_HANDLE != null)
		{
			Document responseXML = null;
			PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/user-group/" + userGroupID + "/selectors/remove");
			post.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				post.setRequestEntity(new StringRequestEntity(Util.getParamXml(Util.OPTION_REMOVE_SELECTOR,
				        lastModificationDate, selectors)));
				this.client.executeMethod(post);
				if (post.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + post.getStatusCode());
					return false;
				}
				System.out.println("Server response: ");
				responseXML = Util.inputStreamToXmlDocument(post.getResponseBodyAsStream());
				Util.xmlToString(responseXML);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in removeSelectorFromUserGroup: No userHandle available");
		}
		return true;
	}

	// Activates a specific UserGroup
	@SuppressWarnings("deprecation")
	public Boolean activateUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID which you want to activate:");
		// String userGroupID = "escidoc:27004";
		Document userGroupXML = this.getUserGroupXML(userGroupID);
		if (userGroupXML == null) 
		{
			return false;
		}
		Element rootElement = userGroupXML.getRootElement();
		String lastModificationDate = rootElement.getAttributeValue("last-modification-date");
		if (this.USER_HANDLE != null)
		{
			PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/user-group/" + userGroupID + "/activate");
			post.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				post.setRequestEntity(new StringRequestEntity(Util.getParamXml(Util.OPTION_REMOVE_SELECTOR,
				        lastModificationDate, "")));
				this.client.executeMethod(post);
				if (post.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + post.getStatusCode());
					return false;
				}
				System.out.println("Usergroup " + userGroupID + " activated");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in activateUserGroup: No userHandle available");
		}
		this.getUserGroupXML(userGroupID);
		return true;
	}

	// Deactivates a specific UserGroup
	@SuppressWarnings("deprecation")
	public Boolean deactivateUserGroup()
	{
		String userGroupID = Util.input("Enter UserGroupID which you want to deactivate:");
		// String userGroupID = "escidoc:27004";
		Document userGroupXML = this.getUserGroupXML(userGroupID);
		if (userGroupXML == null) 
		{
			return false;
		}
		Element rootElement = userGroupXML.getRootElement();
		String lastModificationDate = rootElement.getAttributeValue("last-modification-date");
		if (this.USER_HANDLE != null)
		{
			PostMethod post = new PostMethod(this.FRAMEWORK_URL + "/aa/user-group/" + userGroupID + "/deactivate");
			post.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
			try
			{
				System.out.println("Request body sent to Server: ");
				post.setRequestEntity(new StringRequestEntity(Util.getParamXml(Util.OPTION_REMOVE_SELECTOR,
				        lastModificationDate, "")));
				this.client.executeMethod(post);
				if (post.getStatusCode() != 200)
				{
					System.out.println("Server StatusCode: " + post.getStatusCode());
					return false;
				}
				System.out.println("Usergroup " + userGroupID + " deactivated");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Error in deactivateUserGroup: No userHandle available");
		}
		this.getUserGroupXML(userGroupID);
		return true;
	}

	// Returns a JDOM Document representation of a specific UserGroup
	private Document getUserGroupXML(final String userGroupID)
	{
		Document responseXML = null;
		GetMethod get = new GetMethod(FRAMEWORK_URL + "/aa/user-group/" + userGroupID);
		get.setRequestHeader("Cookie", "escidocCookie=" + this.USER_HANDLE);
		try
		{
			this.client.executeMethod(get);if (get.getStatusCode() != 200)
			{
				System.out.println("Server StatusCode: " + get.getStatusCode());
				return null;
			}
			System.out.println("Server response: ");
			responseXML = Util.inputStreamToXmlDocument(get.getResponseBodyAsStream());
			Util.xmlToString(responseXML);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return responseXML;
	}
}
