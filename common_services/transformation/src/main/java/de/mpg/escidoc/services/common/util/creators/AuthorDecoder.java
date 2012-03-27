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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.util.creators;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Main class for author string decoding.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4134 $ $LastChangedDate: 2011-09-22 18:21:00 +0200 (Do, 22 Sep 2011) $
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
         this(authors, false);
    }
    
    /**
     * Constructor that starts the processing of a given author string.
     *
     * @param authors The author string to be parsed.
     * @param bestOnly Indicates if only the first (and best) result should be evaluated.
     * @throws Exception Any Exception.
     */
    public AuthorDecoder(String authors, boolean bestOnly) throws Exception
    {
        //Remove newlines that have a separator before or after it

//        //replace newlines before or after commas
//        authors = authors.replaceAll(",\\s*\\n\\s*", ", ");
//        authors = authors.replaceAll("\\s*\\n\\s*,", ",");
//        
//        //replace newlines before or after semicolons
//        authors = authors.replaceAll(";\\s*\\n\\s*", "; ");
//        authors = authors.replaceAll("\\s*\\n\\s*;", ";");
//        
//       //replace newlines before or after "and"
//        authors = authors.replaceAll(" and\\s*\\n\\s*", " and ");
//        authors = authors.replaceAll("\\s*\\n\\s*and ", " and ");
//        
//        //replace newlines before or after "und"
//        authors = authors.replaceAll(" und\\s*\\n\\s*", " und ");
//        authors = authors.replaceAll("\\s*\\n\\s*und ", " und ");
//        
//        //replace newlines before or after "et"
//        authors = authors.replaceAll(" et\\s*\\n\\s*", " et ");
//        authors = authors.replaceAll("\\s*\\n\\s*et ", " et ");
//        
//        //replace ", and"
//        authors = authors.replaceAll(",\\s*and\\s+", ", ");
//        authors = authors.replaceAll(";\\s*and\\s+", "; ");
// 
//        //normalize the string
//        //authors = authors.replaceAll("(\\s)+", " ").trim();
//        authors = authors.trim();
        
        logger.debug("Testing '" + authors + "'");
        
        //remove "et al." from the authorsString
        if (authors.contains("et al.")){
            authors = authors.substring( 0, authors.indexOf("et al.")).trim();
        }

        AuthorFormat[] authorFormats = AuthorFormatList.getFormats();

        for (AuthorFormat authorFormat : authorFormats)
        {

            logger.debug(authorFormat.getName());
            try
            {
                //Pattern pattern = Pattern.compile(authorFormat.getPattern());
                //Matcher matcher = pattern.matcher(authors);

                List<Author> authorList = authorFormat.getAuthors(authorFormat.normalize(authors));
                if (authorList != null)
                {
                    logger.debug("Pattern found!");
                    analyzeAuthors(authorList);
                    authorListList.add(authorList);
                    if (bestFormat == null)
                    {
                        bestFormat = authorFormat;
                        if (bestOnly) {
                            break;
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

    private void analyzeAuthors(List<Author> authorList)
    {
        for (Author author : authorList)
        {
            if (author.getSurname().endsWith("{}"))
            {
                author.setSurname(author.getSurname().replace("{}", ""));
                author.getTags().put("brackets", "true");
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
