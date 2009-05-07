/*
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * Abstract superclass for author string decoding formats. Provides basic functionality.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class AuthorFormat implements Comparable<AuthorFormat>
{
    private static Logger logger = Logger.getLogger(AuthorFormat.class);

    protected static final String SYLLABLE = "([A-ZÄÖÜ][a-zäöüßáâàéêèíîìôçø]+)";
    //protected static final String LOOSE_SYLLABLE = "([\\w\\.'-]+)";
    protected static final String LOOSE_SYLLABLE = "([\\p{L}\\d\\.'\\-\\*\\(\\)\\[\\]\\{\\}@!\\$§%&/=\\+\\?¤]+)";
    protected static final String WORD = "((O')?" + SYLLABLE + "(" + SYLLABLE + ")*)";
    protected static final String NAME = "(" + WORD + "( *- *" + WORD + ")*)";
    protected static final String INITIAL = "(([A-Z]|Ch|Sch|Th|Chr)\\.?)";
    protected static final String INITIALS = "(" + INITIAL + "( *-? *" + INITIAL + ")*)";
    protected static final String TITLE = "(Dr\\.|Doktor|Doctor|Prof\\.|Professor|Kardinal|Geheimrat|Bischof|)";
    protected static final String PREFIX = "(von|vom|von +und +zu|zu|de +la|dela|la|d e|du|of|van|van +der|van +den|den|der|und|le|Le|La)";
    protected static final String MIDDLEFIX = "(y|dela|de la)";
    protected static final String GIVEN_NAME_FORMAT = "(" + NAME + "( *(" + NAME + "|" + INITIALS + "))*( +" + PREFIX + ")?)";
    protected static final String GIVEN_NAME_FORMAT_MIXED = "((" + NAME + "|" + INITIALS + ")( *(" + NAME + "|" + INITIALS + "))*)";
    
    protected static final String FORBIDDEN_CHARACTERS = "(\\d|\\*|\\(|\\)|\\[|\\]|\\{|\\}|!|\\$|§|%|&|/|=|\\+|\\?|¤|email|written|et al)";
    protected static final String IGNORE_CHARACTERS = ".*(@).*";
    

    protected Set<String> givenNames = null;
    protected Set<String> surnames = null;

    /**
     * This method is called to execute the parser.
     * @param authorString
     * @return A {@link List} of {@link Author} beans.
     * @throws Exception Any exception.
     */
    public abstract List<Author> getAuthors(String authorString) throws Exception;

    /**
     * Returns the regular expression to identify a string this Class can probably handle.
     *
     * @return A string containing a regular expression.
     */
    public abstract String getPattern();

    /**
     * This method is called to get an integer value that indicates how reliable the result of this parser is.
     * @return An integer value between 1 (highly reliable) and {@link Integer.MAX_VALUE} (not reliable at all).
     */
    public abstract int getSignificance();

    /**
     * Returns the name of this parser.
     * @return The name
     */
    public abstract String getName();

    /**
     * Returns a description what kind of format this parser analyzes.
     * @return The description.
     */
    public abstract String getDescription();

    /**
     * Should be implemented in case the format the parser analyzes is
     * very special or is covered by other Parsers, too.
     * @return A warning message why the result of this parser might be problematic.
     */
    public abstract String getWarning();

    /**
     * {@inheritDoc}
     */
    public int compareTo(AuthorFormat o)
    {
        return getSignificance() - o.getSignificance();
    }

    /**
     * Checks a controlled vocabulary, if the given string is a given name.
     * @param name The given string.
     * @return <code>true</code> if the given string is contained in the list of given names.
     * @throws Exception Any {@link Exception}.
     */
    public boolean isGivenName(String name) throws Exception
    {
        boolean result = getGivenNames().contains(name);
        return result;
    }

    /**
     * Checks a controlled vocabulary, if the given string is a surname.
     * @param name The given string.
     * @return <code>true</code> if the given string is contained in the list of surnames.
     * @throws Exception Any {@link Exception}.
     */
    public boolean isSurname(String name) throws Exception
    {
        boolean result = getSurnames().contains(name);
        return result;
    }

    /**
     * Returns the list of given names. If the list is not initialized yet, this is done.
     * @return The list of given names.
     * @throws Exception Any {@link Exception}.
     */
    public Set<String> getGivenNames() throws Exception
    {
        if (givenNames == null)
        {
            givenNames = getNamesFromFile("metadata/names/givennames.txt");
        }
        return givenNames;
    }

    public void setGivenNames(Set<String> givenNames)
    {
        this.givenNames = givenNames;
    }

    /**
     * Returns the list of surnames. If the list is not initialized yet, this is done.
     * @return The list of surnames.
     * @throws Exception Any {@link Exception}.
     */
    public Set<String> getSurnames() throws Exception
    {
        if (surnames == null)
        {
            surnames = getNamesFromFile("metadata/names/surnames.txt");
        }
        return surnames;
    }

    public void setSurnames(Set<String> surnames)
    {
        this.surnames = surnames;
    }

    /**
     * Reads words from a file into a {@link Set}.
     * @param filename The name of the file relative or absolute.
     * The file should contain lines each with one line.
     * @return A {@link Set} containing the words in a file.
     * @throws Exception Any {@link Exception}.
     */
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
    public List<Author> getAuthorListNormalFormat(String[] authors)
    {
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
    public List<Author> getAuthorListNormalFormat(String[] authors, String separator)
    {

        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {
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
    public List<Author> getAuthorListWithInitials(String[] authors)
    {
        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {

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

    /**
     * Parses authors in the following formats:
     * "Müller, Herbert", "Meier-Schmitz, K.L." etc.
     *
     * @param authors The authors as string array.
     * @return The authors as list of author objects.
     */
    public List<Author> getAuthorListLeadingSurname(String[] authors, String limit)
    {
        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {
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
        throws Exception
    {
        List<Author> result = new ArrayList<Author>();
      
        
        int prefixPosition = -1;
        for (String authorString : authors)
        {
            Author author = new Author();
            //check for prefix
            String[] parts = authorString.split(" ");
           
            //check middle parts
            if (parts.length > 2)
            {
                for (int i = 1; i < parts.length - 1; i++)
                {
                    if (parts[i].matches(" *" + PREFIX + " *"))
                    {
                        author.setPrefix(parts[i]);
                        prefixPosition = i;
                    }
                }
            }
            
            String givenName = "";
            String surname = "";
            
            if (prefixPosition == -1)
            {
                int lastSpace = authorString.lastIndexOf(" ");
                givenName = authorString.substring(0, lastSpace);
                surname = authorString.substring(lastSpace + 1);
            }
            else 
            {
                surname = parts[parts.length - 1];
                for (int i = 0; i < prefixPosition; i++)
                {
                    givenName+=parts[i];
                }
                
            }
            
            
            String[] names = givenName.split(" |-");
            for (int i = 0; i < names.length; i++)
            {
                if (!isGivenName(names[i]))
                {
                    return null;
                }
            }
            

            author.setGivenName(givenName);
            author.setSurname(surname);
            author.setFormat(this);
            result.add(author);
        }

        return result;
    }
    
    
    

    /**
     * Parses authors using controlled vocabularies.
     *
     * @param authorsString The complete author string.
     * @param authors A string array holding the strings of single authors.
     * @return A {@link List} of {@link Author} beans.
     * @throws Exception Any {@link Exception}
     */
    public List<Author> getAuthorListCheckingNames(String authorsString,
        String[] authors) throws Exception
    {
        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {

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
            String givenName = authorString.substring(0, authorString.indexOf(names[part]) - 1);
            String surname = authorString.substring(authorString.indexOf(names[part]));

            author.setGivenName(givenName);
            author.setSurname(surname);
            author.setFormat(this);
            result.add(author);
        }

        return result;
    }
    
    public List<Author> getAuthorListLooseFormat(String[] authors)
    {
        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {
            Author author = new Author();
           
            
            
            //parse information in brackets, if available
            int openBracketIndex = authorString.indexOf("(");
            int closingBracketIndex = authorString.indexOf(")");
            
            if (openBracketIndex != -1 && closingBracketIndex != -1)
            {
                String additionalInfo = authorString.substring(openBracketIndex + 1, closingBracketIndex);
                authorString = authorString.substring(0, openBracketIndex);
            }
            
            
         
               //remove forbidden characters 
            authorString = authorString.replaceAll(FORBIDDEN_CHARACTERS, "");
            
            //split the rest of the string and parse it
            String[] parts = authorString.split("\\s");
            
            if (parts.length > 1)
            {
                
                String surname = parts[parts.length - 1];
                String prefix = "";
                String givenName = "";
                String title = "";
                for (int i = 0; i < parts.length - 1; i++)
                {
                    String part = parts[i];
                    if (part.matches(PREFIX) && !givenName.trim().equals(""))
                    {
                        prefix += part + " ";
                    }
                    else if (part.matches(TITLE) && givenName.trim().equals(""))
                    {
                        title += part + " ";
                    }
                    else if (part.matches(IGNORE_CHARACTERS))
                    {
                        //ignore whole string
                    }
                   
                    else
                    {
                        givenName += part + " ";
                    }
                    
                    
                }
                
                author.setGivenName(givenName.trim());
                author.setSurname(prefix.trim() + " " + surname.trim());
                author.setTitle(title.trim());
                author.setFormat(this);
                result.add(author);
               
            }
            else if (parts.length==1 && !parts[0].equals(""))
            {
               
                    author.setSurname(parts[0].trim());
                    author.setFormat(this);
                    result.add(author);
                
            }
            else
            {
                //do nothing
            }
           

        
           
        }

        return result;
       
    }
    
    protected List<Author> getAuthorListLooseFormatSurnameFirst(String[] authors)
    {
        List<Author> result = new ArrayList<Author>();
        for (String authorString : authors)
        {
            Author author = new Author();
            String[] parts = authorString.split(",");
            
            
            
            
            
            if (parts.length>1)
            {
                
                String[] surnameParts = parts[0].split("\\s");
                String[] givenNameParts = parts[1].split("\\s");
                                              
                String surname = "";
                String prefix = "";
                String givenName="";
                String title = "";
                
                for (int i = 0; i < surnameParts.length; i++)
                {
                    if (surnameParts[i].toLowerCase().matches(PREFIX))
                    {
                        prefix+=surnameParts[i] + " ";
                    }
                    else if (surnameParts[i].matches(TITLE))
                    {
                        title+=surnameParts[i]+" ";
                    }
                    else if (surnameParts[i].matches(FORBIDDEN_CHARACTERS))
                    {
                        //ignore part
                    }
                    else
                    {
                        surname+=surnameParts[i]+" ";
                    }
                    
                }
                
                for (int i = 0; i < givenNameParts.length; i++)
                {
                    if (givenNameParts[i].matches(FORBIDDEN_CHARACTERS))
                    {
                        //ignore part
                    }
                    else
                    {
                        givenName+=givenNameParts[i]+" ";
                    }
                    
                }
                
                author.setGivenName(givenName.trim());
                author.setSurname(prefix.trim() + " " + surname.trim());
                author.setTitle(title.trim());
               
            }
            else 
            {
                author.setSurname(parts[0].trim());
            }
           

        
            author.setFormat(this);
            result.add(author);
        }

        return result;
        
    }
}
