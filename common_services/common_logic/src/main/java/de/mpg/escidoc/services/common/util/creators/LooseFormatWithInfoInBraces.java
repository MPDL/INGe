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
import java.util.List;

/**
 * Loose parser that accepts many citation errors for comma or semicolon seperated authors
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class LooseFormatWithInfoInBraces extends AuthorFormat {
    
    public final String BRACES_WITH_ANY_CONTENT = "\\s+\\(\\s*(\\s*\\S+)+\\s*\\)\\s*";
    @Override
    public String getPattern() {
        return "^\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE + ")*(" + BRACES_WITH_ANY_CONTENT + ")*(\\s*(,|;| and | und | et )\\s*" + LOOSE_SYLLABLE + "(\\s+" + LOOSE_SYLLABLE + ")*(" + BRACES_WITH_ANY_CONTENT + ")*)*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString) throws Exception
    {
        
        List<String> parts = new ArrayList<String>();
        String currentString = "";
        String currentStringWithoutBracketContent="";
        
        String openedBracketsRegEx = "(\\(|\\{|\\[)";
        String closedBracketsRegEx = "(\\)|\\}|\\])";
        String seperatorsRegEX = "(,|;)";
        int brackets = 0;
        
        //split string by commas and semicolons (if they are not inside a bracket)
        for (int i = 0; i < authorsString.length(); i++)
        {
            String currentChar = String.valueOf(authorsString.charAt(i));
            if (currentChar.matches(openedBracketsRegEx))
            {
                brackets+=1;
                currentString+=currentChar;
                
            }
            else if (currentChar.matches(closedBracketsRegEx))
            {
                brackets-=1;
                currentString+=currentChar;
            }
            else if (currentChar.matches(seperatorsRegEX) && brackets == 0)
            {
                parts.add(new String(currentString));
                currentString="";
                
            }
            else
            {
                currentString+=currentChar;
            }
            
        }
        // add last Part
        parts.add(currentString);
        
        
        //split strings by rest of seperators
        String seperatorsRegEX2 = "( and | und | et )";
        List<String> parts2 = new ArrayList<String>();
        for (int i = 0; i < parts.size(); i++)
        {
            String part = parts.get(i);
            String[] newSeps = part.split(seperatorsRegEX2);
            for (int j = 0; j < newSeps.length; j++)
            {
                parts2.add(newSeps[j]);
            }
        }
       
        
        String[] authors = parts2.toArray(new String[0]);
        
        return getAuthorListLooseFormat(authors);
    }

    

    @Override
    public int getSignificance() {
        return 15;
    }

    @Override
    public String getDescription() {
        return "VorName prefix nachname (info, info2)[, vorname nachname, prefix nachname (info)]";
    }

    @Override
    public String getName() {
        return "Loose format with additional optional information in brackets for each autor, comma or semicolon seperated, accepts and ignores line breaks and tabs, case insensitive, takes last word as surname, the words before as prefix or given name";
    }

    @Override
    public String getWarning() {
        return null;
    }

}
