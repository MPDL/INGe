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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Main class for author string decoding.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AuthorDecoder
{

    private List<List<Author>> authorListList = new ArrayList<List<Author>>();
    private AuthorFormat bestFormat = null;

    private static Logger logger = Logger.getLogger(AuthorDecoder.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        //System.out.println(new LooseFormatSurnameFirst().getPattern());
        /*
        String[] prefixes1 =  "Damien van den Borgne".split(WesternFormat1.PREFIX, 3);
        String[] prefixes2 =  "Damien von Sudo".split(WesternFormat1.PREFIX, 3);
        
       for (String string : prefixes1)
       {
        System.out.println(string);
       } 
       
       for (String string : prefixes2)
    {
           System.out.println(string);
    }
       */
        if (args == null || args.length == 0)
        {
            System.out.println("usage: java de.mpg.escidoc.services.util.AuthorDecoder author_string");
            System.out.println("Please make sure your classpath points to a valid log4j configuration.");
        }
        else
        {
            AuthorDecoder authorDecoder = new AuthorDecoder(args[0]);
            authorDecoder.displayAuthors();
        }

    }

    /**
     * Constructor that starts the processing of a given author string.
     *
     * @param authors The author string to be parsed.
     * @throws Exception Any Exception.
     */
    public AuthorDecoder(String authors) throws Exception
    {

        
        //System.out.println(authors);
        //Replace newline with "and" (and, thus, interprete as new author) if no seperation like ", ; and und et" is found before or after
        //otherwise replace with "and" including the seperator
        authors = authors.replaceAll("((,|;| and| und| et))\\s*\\n\\s*", " and ").trim();
        authors = authors.replaceAll("\\s*\\n\\s*(,|;|and |und |et )", " and ").trim();
        authors = authors.replaceAll("\\n", " and ").trim();
        
        //normalize the string
        authors = authors.replaceAll("\\s+", " ").trim();

        //System.out.println(authors);
        
        logger.debug("Testing '" + authors + "'");

        AuthorFormat[] authorFormats = AuthorFormatList.getFormats();

        for (AuthorFormat authorFormat : authorFormats)
        {

            logger.debug(authorFormat.getName() + ": " + authorFormat.getPattern());
            try
            {
                Pattern pattern = Pattern.compile(authorFormat.getPattern());
                Matcher matcher = pattern.matcher(authors);
                if (matcher.find())
                {
                    logger.debug("Pattern found!");

                    List<Author> authorList = authorFormat.getAuthors(authors);
                    if (authorList != null)
                    {
                        authorListList.add(authorList);
                        if (bestFormat == null)
                        {
                            bestFormat = authorFormat;
                        }
                    }

                }
            }
            catch (StackOverflowError e)
            {
                logger.error("Could not apply format \"" + authorFormat.getName() +"\"", e);
            }
        }
    }

    public void displayAuthors()
    {
        if (bestFormat != null)
        {
            logger.info("Best result (" + bestFormat.getName() + "):");
            for (Author author : authorListList.get(0))
            {
                logger.info(author);
            }
    
            if (authorListList.size() > 1)
            {
                List<Author> bestList = authorListList.get(0);
    
                boolean alternative = false;
    
                for (List<Author> list : authorListList)
                {
                    if (list != bestList)
                    {
                        if (!alternative)
                        {
                            logger.info("There are alternative interpretations:");
                            alternative = true;
                        }
                        for (int i = 0; i < list.size(); i++)
                        {
                            logger.info(
                                    list.get(i));
                                    
                                    if (bestList.size()>i)
                                    {
                                        logger.info((list.get(i).equals(bestList.get(i)) ? "(identical)" : "(differing)"));
                                    }
                                    
                        }
                    }
                }
            }
        }
        else
        {
            logger.debug("Diese Eingabe entspricht keinem bekannten Format.");
        }
    }

    public List<List<Author>> getAuthorListList()
    {
        return authorListList;
    }

    public void setAuthorListList(List<List<Author>> authorListList)
    {
        this.authorListList = authorListList;
    }

    public AuthorFormat getBestFormat()
    {
        return bestFormat;
    }

    public void setBestFormat(AuthorFormat bestFormat)
    {
        this.bestFormat = bestFormat;
    }
    
    public List<Author> getBestAuthorList() 
    {
        for(List<Author> authorList : getAuthorListList())
        {
            Author author = authorList.get(0);
            if (author.getFormat().equals(getBestFormat()))
            {
                return authorList;
            }
        }
        return null;
    }

    /**
     * Parses the given author string and returns the result as DOM node.
     * The returned XML has the following structure:
     * <authors>
     *   <author>
     *     <familyname>Buxtehude-Mölln</familyname>
     *     <givenname>Heribert</givenname>
     *     <prefix>von und zu</prefix>
     *     <title>König</title>
     *   </author>
     *   <author>
     *     <familyname>Müller</familyname>
     *     <givenname>Peter</givenname>
     *   </author>
     * </authors>
     * 
     * @param authors
     * @return
     */
    public static Node parseAsNode(String authors)
    {
        DocumentBuilder documentBuilder;

        try
        {
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element element = document.createElement("authors");
        document.appendChild(element);
        
        try
        {
            AuthorDecoder authorDecoder = new AuthorDecoder(authors);
            List<Author> authorList = authorDecoder.getBestAuthorList();
            if (authorList != null)
            {
                for (Author author : authorList)
                {
                    Element authorElement = document.createElement("author");
                    element.appendChild(authorElement);
                    
                    if (author.getSurname() != null)
                    {
                        Element familyNameElement = document.createElement("familyname");
                        familyNameElement.setTextContent(author.getSurname());
                        authorElement.appendChild(familyNameElement);
                    }
                    
                    if (author.getGivenName() != null)
                    {
                        Element givenNameElement = document.createElement("givenname");
                        givenNameElement.setTextContent(author.getGivenName());
                        authorElement.appendChild(givenNameElement);
                    }
                    
                    if (author.getPrefix() != null)
                    {
                        Element prefixElement = document.createElement("prefix");
                        prefixElement.setTextContent(author.getPrefix());
                        authorElement.appendChild(prefixElement);
                    }
                    
                    if (author.getTitle() != null)
                    {
                        Element titleElement = document.createElement("title");
                        titleElement.setTextContent(author.getTitle());
                        authorElement.appendChild(titleElement);
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }
    
}
