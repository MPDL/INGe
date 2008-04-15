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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.common.util.creators;

public class Author {
	
	private String surname = null;
	private String givenName = null;
	private String initial = null;
	private String title = null;
	private String prefix = null;
	private AuthorFormat format = null;

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
		if (givenName.contains("-"))
		{
			String[] names = givenName.split("-");
			String initial = "";
			for (String name : names) {
				initial += name.substring(0, 1) + ".-";
			}
			this.initial = initial.substring(0, initial.length() - 1);
		}
		else if (givenName.contains(" "))
		{
			String[] names = givenName.split(" |\\.");
			String initial = "";
			for (String name : names) {
				initial += name.substring(0, 1) + ". ";
			}
			this.initial = initial.trim();
		}
		else
		{
			this.initial = givenName.substring(0, 1) + ".";
		}
		
		
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

	public String toString()
	{
		return "[Author: givenName=" + givenName + ", initial=" + initial + ", sn=" + surname + "(" + format.getName() + ")]";
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Author)
		{
			Author other = (Author) obj;
			
			// givenName
			if (this.givenName == null)
			{
				if (other.givenName != null)
				{
					return false;
				}
			}
			else
			{
				if (!this.givenName.equals(other.givenName))
				{
					return false;
				}
			}
			
			// surname
			if (this.surname == null)
			{
				if (other.surname != null)
				{
					return false;
				}
			}
			else
			{
				if (!this.surname.equals(other.surname))
				{
					return false;
				}
			}
			
			// initial
			if (this.initial == null)
			{
				if (other.initial != null)
				{
					return false;
				}
			}
			else
			{
				if (!this.initial.equals(other.initial))
				{
					return false;
				}
			}
			
			return true;
			
		}
		else
		{
			return false;
		}
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public AuthorFormat getFormat() {
		return format;
	}

	public void setFormat(AuthorFormat format) {
		this.format = format;
	}
	
}