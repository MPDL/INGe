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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;

public abstract class AuthorFormat implements Comparable<AuthorFormat>
{
	private static Logger logger = Logger.getLogger(AuthorFormat.class);
	
	protected static final String SYLLABLE = "([A-ZÄÖÜ][a-zäöüß]+)";
	protected static final String WORD = "((O')?" + SYLLABLE + "(" + SYLLABLE + ")*)";
	protected static final String NAME = "(" + WORD + "(-" + WORD + ")*)";
	protected static final String INITIAL ="(([A-Z]|Ch|Sch|Th|Chr)\\.?)";
	protected static final String INITIALS ="(" + INITIAL + "(-" + INITIAL + ")*)";
	protected static final String TITLE = "(Dr\\.|Doktor|Doctor|Prof\\.|Professor)";
	protected static final String PREFIX = "(von|von und zu|zu|de la|la|de|du|of)";
	protected static final String GIVEN_NAME_FORMAT = "(" + NAME + "( ?(" + NAME + "|" + INITIALS + "))*)";
	
	protected Set<String> givenNames = null;
	protected Set<String> surnames = null;
	
	public abstract List<Author> getAuthors(String authorString) throws Exception;
	
	public abstract String getPattern();
	
	public abstract int getSignificance();

	public abstract String getName();
	
	public abstract String getDescription();
	
	public abstract String getWarning();
	
	public int compareTo(AuthorFormat o) {
		return getSignificance() - o.getSignificance();
	}
	
	public boolean isGivenName(String name) throws Exception
	{
		boolean result = getGivenNames().contains(name);
		System.out.println(name + " is " + (result ? "" : "not ") + "a given name");
		return result;
	}
	
	public boolean isSurname(String name) throws Exception
	{
		boolean result = getSurnames().contains(name);
		System.out.println(name + " is " + (result ? "" : "not ") + "a surname");
		return result;
	}

	public Set<String> getGivenNames() throws Exception {
		if (givenNames == null)
		{
			givenNames = getNamesFromFile("metadata/names/givennames.txt");
		}
		return givenNames;
	}

	public void setGivenNames(Set<String> givenNames) {
		this.givenNames = givenNames;
	}

	public Set<String> getSurnames() throws Exception {
		if (surnames == null)
		{
			surnames = getNamesFromFile("metadata/names/surnames.txt");
		}
		return surnames;
	}

	public void setSurnames(Set<String> surnames) {
		this.surnames = surnames;
	}
	
	public static Set<String> getNamesFromFile(String filename) throws Exception
	{
		InputStream file = ResourceUtil.getResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		String name = "";
		Set<String> result = new HashSet<String>();
		while ((name = br.readLine()) != null)
		{
			result.add(name);
		}
		return result;
	}
	
	/**
	 * Parses authors in the following formats:
	 * "Peter Müller" or "Linda McCartney" or "John Gabriel Smith-Wesson" or "Karl H. Meiser"
	 * 
	 * Returns false results with e.g.
	 * "Harald Grün Haselstein" or "Karl Kardinal Lehmann" or "Ban Ki Moon"
	 * 
	 * @param authors The authors as string array.
	 * @return The authors as list of author objects.
	 */
	public List<Author> getAuthorListNormalFormat(String[] authors) {
		return getAuthorListNormalFormat(authors, " ");
	}
	
	/**
	 * Parses authors in the following formats:
	 * "Peter Müller" or "Linda McCartney" or "John Gabriel Smith-Wesson" or "Karl H. Meiser"
	 * 
	 * Returns false results with e.g.
	 * "Harald Grün Haselstein" or "Karl Kardinal Lehmann" or "Ban Ki Moon"
	 * 
	 * @param authors The authors as string array.
	 * @param separator The separator between first names and lastnames.
	 * 
	 * @return The authors as list of author objects.
	 */
	public List<Author> getAuthorListNormalFormat(String[] authors, String separator) {

		List<Author> result = new ArrayList<Author>();
		for (String authorString : authors) {
			int lastSpace = authorString.lastIndexOf(separator);
			Author author = new Author();
			
			author.setGivenName(authorString.substring(0, lastSpace));
			author.setSurname(authorString.substring(lastSpace + 1));
			author.setFormat(this);
			result.add(author);
		}
		
		return result;
	}

	/**
	 * Parses authors in the following formats:
	 * "P. Müller" or "L. McCartney" or "J.-P. Smith-Wesson" or "K. H. Meiser" or "R-X Wang"
	 * 
	 * @param authors The authors as string array.
	 * @return The authors as list of author objects.
	 */
	public List<Author> getAuthorListWithInitials(String[] authors) {
		List<Author> result = new ArrayList<Author>();
		for (String authorString : authors) {
			
			logger.debug("Testing " + authorString);
			
			int limit = authorString.lastIndexOf(". ");
			
			logger.debug("Limit " + limit);
			
			Author author = new Author();
			author.setInitial(authorString.substring(0, limit + 1));
			author.setSurname(authorString.substring(limit + 2));
			author.setFormat(this);
			result.add(author);
		}
		
		return result;
	}

	public List<Author> getAuthorListLeadingSurname(String[] authors, String limit) {
		List<Author> result = new ArrayList<Author>();
		for (String authorString : authors) {
			int delimiter = authorString.indexOf(limit);
			Author author = new Author();
			
			logger.debug("delimiter: " + delimiter);
			
			author.setGivenName(authorString.substring(delimiter + 1).trim());
			author.setSurname(authorString.substring(0, delimiter).trim());
			author.setFormat(this);
			result.add(author);
		}
		
		return result;
	}

	public List<Author> getAuthorListCheckingGivenNames(String[] authors)
			throws Exception {
		List<Author> result = new ArrayList<Author>();
		for (String authorString : authors) {
			int lastSpace = authorString.lastIndexOf(" ");
			Author author = new Author();
			
			String givenName = authorString.substring(0, lastSpace);
			String[] names = givenName.split(" |-");
			for (int i = 0; i < names.length; i++) {
				if (!isGivenName(names[i]))
				{
					return null;
				}
			}
			String surname = authorString.substring(lastSpace + 1);
			
			author.setGivenName(givenName);
			author.setSurname(surname);
			author.setFormat(this);
			result.add(author);
		}
		
		return result;
	}
	
	public List<Author> getAuthorListCheckingNames(String authorsString,
			String[] authors) throws Exception {
		List<Author> result = new ArrayList<Author>();
		for (String authorString : authors) {
			
			Author author = new Author();
			
			String[] names = authorString.split(" ");
			int part = 0;
			while (isGivenName(names[part]))
			{
				part++;
				if (part == names.length)
				{
					return null;
				}
			}
			for (int i = part; i < names.length; i++)
			{
				if (!isSurname(names[i]))
				{
					return null;
				}
			}
			
			logger.debug("part: " + authorsString.indexOf(names[part]));
			
			if (part == 0)
			{
				return null;
			}
			String givenName = authorString.substring(0, authorsString.indexOf(names[part]) - 1);
			String surname = authorString.substring(authorsString.indexOf(names[part]));
			
			author.setGivenName(givenName);
			author.setSurname(surname);
			author.setFormat(this);
			result.add(author);
		}
		
		return result;
	}
}
