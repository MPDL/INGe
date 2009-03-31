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
    * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
    * für wissenschaftlich-technische Information mbH and Max-Planck-
    * Gesellschaft zur Förderung der Wissenschaft e.V.
    * All rights reserved. Use is subject to license terms.
    */ 

package de.mpg.escidoc.services.transformation.transformations.microFormats;


import javax.naming.InitialContext;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * 
 * This class provides transforming a escidoc xml into the coins microformat.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CoinsTransformation
{
    private static final String DELIMITER = "&amp;";
    private static final String COINS_START_TAG = "<span class='Z3988' title='";
    private static final String COINS_END_TAG = "'></span>";
    private static final String CTX_VER = "ctx_ver=Z39.88-2004";
    private static final String RFT_VAL_FMT = DELIMITER + "rft_val_fmt=info:ofi/fmt:kev:mtx:journal";
    private static final String RFR_ID = DELIMITER + "rfr_id=info:sid/escidoc.mpg.de:pubman";
    private static final String RFT_GENRE_PREFIX = DELIMITER + "rft.genre=";
    private static final String RFT_ATIITLE_PREFIX = DELIMITER + "rft.atitle=";
    private static final String RFT_TITLE_PREFIX = DELIMITER + "rft.title=";
    private static final String RFT_AULAST_PREFIX = DELIMITER + "rft.aulast=";
    private static final String RFT_AUFIRST_PREFIX = DELIMITER + "rft.aufirst=";
    private static final String RFT_AU_PREFIX = DELIMITER + "rft.au=";
    private static final String RFT_ISSN_PREFIX = DELIMITER + "rft.issn=";
    private static final String RFT_ISBN_PREFIX = DELIMITER + "rft.isbn=";
    private static final String RFT_ID_PREFIX = DELIMITER + "rft_id=info:doi/";
    private static final String RFT_VOLUME_PREFIX = DELIMITER + "rft.volume=";
    private static final String RFT_ISSUE_PREFIX = DELIMITER + "rft.issue=";
    private static final String RFT_PAGES_PREFIX = DELIMITER + "rft.pages=";
    private static final String RFT_DATE_PREFIX = DELIMITER + "rft.date=";
        
    /**
    * Public constructor.
    */
    public CoinsTransformation()
    {        
    }
    
    /**
     * Generates the HTML Span Tag for COinS.
     * @param source byte[]
     * @return String HTML Span Tag
     */
    public String getCOinS(byte[] source)
    {
        String coins = null;
        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(source));
            coins = this.getCOinS(itemVO);
        }
        catch (Exception e) 
        { throw new RuntimeException(e); }
        return coins;
    }

    /**
    * Generates the HTML Span Tag for COinS.
    *
    * @param pubitemVO the complete pub item
    * @return String HTML Span Tag
    */
    public String getCOinS(PubItemVO pubitemVO)
    {
        StringBuffer coinsContent = new StringBuffer();           
        if (pubitemVO != null)
        {
            if (pubitemVO.getMetadata() != null)
            {
                String rftGenre = "";
                String rftAtitle = "";
                String rftTitle = "";
                String rftAulast = "";
                String rftAufirst = "";
                String rftAu = "";
                String rftIssn = "";
                String rftIsbn = "";
                String rftId = "";
                String rftVolume = "";
                String rftIssue = "";
                String rftPages = "";
                String rftDate = "";
                    
                if (pubitemVO.getMetadata().getGenre() != null)
                {
                    rftGenre = RFT_GENRE_PREFIX + pubitemVO.getMetadata().getGenre().name();
                }                    
                if (pubitemVO.getMetadata().getTitle() != null)
                {
                    rftAtitle = RFT_ATIITLE_PREFIX + this.htmlEscape(pubitemVO.getMetadata().getTitle().getValue());
                    rftAtitle = rftAtitle != null ? rftAtitle.replace(" ", "+") : "";
                }
                   
                // add the title of the first source
                if (pubitemVO.getMetadata().getSources() != null)
                {
                    if (pubitemVO.getMetadata().getSources().size() > 0)
                    {
                        if (pubitemVO.getMetadata().getSources().get(0).getTitle() != null)
                        {
                            rftTitle = RFT_TITLE_PREFIX + this.htmlEscape(pubitemVO.getMetadata().
                                    getSources().get(0).getTitle().getValue());
                            rftTitle = rftTitle != null ? rftTitle.replace(" ", "+") : "";
                        }
                    }
                }
                if (!getLastName(pubitemVO).equals(""))
                {
                    rftAulast = RFT_AULAST_PREFIX + this.htmlEscape(getLastName(pubitemVO));
                }                   
                if (!getFirstName(pubitemVO).equals(""))
                {
                    rftAufirst = RFT_AUFIRST_PREFIX + this.htmlEscape(getFirstName(pubitemVO));
                }                    
                if (!getCompleteNames(pubitemVO).equals(""))
                {
                    rftAu = RFT_AU_PREFIX + this.htmlEscape(getCompleteNames(pubitemVO));
                }                 
                if (!getISSN(pubitemVO).equals(""))
                {
                    rftIssn = RFT_ISSN_PREFIX + this.htmlEscape(getISSN(pubitemVO));
                }                   
                if (!getISBN(pubitemVO).equals(""))
                {
                    rftIsbn = RFT_ISBN_PREFIX + this.htmlEscape(getISBN(pubitemVO));
                }                    
                if (!getSourceIdentifier(pubitemVO).equals(""))
                {
                    rftId = RFT_ID_PREFIX + this.htmlEscape(getSourceIdentifier(pubitemVO));
                }                   
                if (!getSourceVolume(pubitemVO).equals(""))
                {
                    rftVolume = RFT_VOLUME_PREFIX + this.htmlEscape(getSourceVolume(pubitemVO));
                }                    
                if (!getSourceIssue(pubitemVO).equals(""))
                {
                    rftIssue = RFT_ISSUE_PREFIX + this.htmlEscape(getSourceIssue(pubitemVO));
                }                    
                if (!getSourcePages(pubitemVO).equals(""))
                {
                    rftPages = RFT_PAGES_PREFIX + this.htmlEscape(getSourcePages(pubitemVO));
                }                    
                if (!getDate(pubitemVO).equals(""))
                {
                    rftDate = RFT_DATE_PREFIX + this.htmlEscape(getDate(pubitemVO));
                }                    
                // put all fields together
                coinsContent.append(COINS_START_TAG + CTX_VER + RFT_VAL_FMT + RFR_ID);
                coinsContent.append(rftGenre + rftAtitle + rftTitle + rftAulast + rftAufirst + rftAu + rftIssn
                        + rftIsbn + rftId + rftVolume + rftIssue + rftPages + rftDate);
                coinsContent.append(COINS_END_TAG);     
            }
        }
        return coinsContent.toString();
    }
        
    /**
     * @param pubitemVO the pubitem in which the firstname of the first creator (person should be found)
     * @return the firstname of the first creator
     */
    private String getFirstName(PubItemVO pubitemVO)
    {
        String firstName = "";
        if (pubitemVO.getMetadata().getCreators() != null)
        {
            for (int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if (pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        firstName = pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName();
                        return firstName;
                    }
                    
                }
            }
        }
        return firstName;
    }
    
    /**
     * @param pubitemVO the pubitem in which the lastname of the first creator (person should be found)
     * @return the lastname of the first creator
     */
    private String getLastName(PubItemVO pubitemVO)
    {
        String lastame = "";
        if (pubitemVO.getMetadata().getCreators() != null)
        {
            for (int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if (pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        lastame = pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName();
                        return lastame;
                    }
                    
                }
            }
        }
        return lastame;
    }
    
    /**
     * @param pubitemVO 
     * @return all concatinated and ; separated complete names
     */
    private String getCompleteNames(PubItemVO pubitemVO)
    {
        StringBuffer completeNames = new StringBuffer();
        if (pubitemVO.getMetadata().getCreators() != null)
        {
            for (int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if (i > 0)
                {
                    completeNames.append(";+");
                }
                if (pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName());
                        completeNames.append("+");
                    }
                    if (pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                }
                else if (pubitemVO.getMetadata().getCreators().get(i).getOrganization() != null 
                            && pubitemVO.getMetadata().getCreators().
                            get(i).getOrganization().getName() != null)
                {
                    completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getOrganization().
                            getName().getValue());
                }
            }
        }
        return completeNames.toString();
    }
    
    /**
     * @param pubitemVO the pubitem in which the identifier of the type ISSN should be found
     * @return the identifier
     */
    private String getISSN(PubItemVO pubitemVO)
    {
        String issn = "";
        if (pubitemVO.getMetadata().getIdentifiers() != null)
        {
            for (int i = 0; i < pubitemVO.getMetadata().getIdentifiers().size(); i++)
            {
                if (pubitemVO.getMetadata().getIdentifiers().get(i).getType() != null)
                {
                    if (pubitemVO.getMetadata().getIdentifiers().get(i).getType().equals(IdentifierVO.IdType.ISSN))
                    {
                        if (pubitemVO.getMetadata().getIdentifiers().get(i).getId() != null)
                        {
                            issn = pubitemVO.getMetadata().getIdentifiers().get(i).getId();
                        }
                        return issn;
                    }
                }
            }
        }
        return issn;
    }
    
    /**
     * @param pubitemVO the pubitem in which the identifier of the type ISSN should be found
     * @return the identifier
     */
    private String getISBN(PubItemVO pubitemVO)
    {
        String isbn = "";
        if (pubitemVO.getMetadata().getIdentifiers() != null)
        {
            for (int i = 0; i < pubitemVO.getMetadata().getIdentifiers().size(); i++)
            {
                if (pubitemVO.getMetadata().getIdentifiers().get(i).getType() != null)
                {
                    if (pubitemVO.getMetadata().getIdentifiers().get(i).getType().equals(IdentifierVO.IdType.ISBN))
                    {
                        if (pubitemVO.getMetadata().getIdentifiers().get(i).getId() != null)
                        {
                            isbn = pubitemVO.getMetadata().getIdentifiers().get(i).getId();
                        }
                        return isbn;
                    }
                }
            }
        }
        return isbn;
    }
    
    /**
     * @param pubitemVO the pubitem in which the identifier of the first source should be found
     * @return the identifier
     */
    private String getSourceIdentifier(PubItemVO pubitemVO)
    {
        String sourceIdentifier = "";
        if (pubitemVO.getMetadata().getSources() != null)
        {
            if (pubitemVO.getMetadata().getSources().size() > 0)
            {
                if (pubitemVO.getMetadata().getSources().get(0).getIdentifiers() != null)
                {
                    if (pubitemVO.getMetadata().getSources().get(0).getIdentifiers().size() > 0)
                    {
                        for (int i = 0; i < pubitemVO.getMetadata().getSources().get(0).getIdentifiers().size(); i++)
                        {
                            if (pubitemVO.getMetadata().getSources().get(0).getIdentifiers().get(i).getType() != null)
                            {
                                if (pubitemVO.getMetadata().getSources().get(0).getIdentifiers().get(i).getType().
                                        equals(IdentifierVO.IdType.DOI))
                                {
                                    sourceIdentifier = pubitemVO.getMetadata().getSources().get(0).getIdentifiers().
                                        get(i).getId();
                                    return sourceIdentifier;
                                }
                            }
                        }
                    }
                }
            }
        }
        return sourceIdentifier;
    }
    
    /**
     * @param pubitemVO the pubitem in which the volume of the first source should be found
     * @return the identifier
     */
    private String getSourceVolume(PubItemVO pubitemVO)
    {
        String sourceVolume = "";
        if (pubitemVO.getMetadata().getSources() != null)
        {
            if (pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getVolume() != null) 
            {
                if (pubitemVO.getMetadata().getSources().get(0).getVolume() != null)
                {
                    sourceVolume = pubitemVO.getMetadata().getSources().get(0).getVolume();
                }
            }
        }
        return sourceVolume;
    }
    
    /**
     * @param pubitemVO the pubitem in which the issue of the first source should be found
     * @return the identifier
     */
    private String getSourceIssue(PubItemVO pubitemVO)
    {
        String sourceIssue = "";
        if (pubitemVO.getMetadata().getSources() != null)
        {
            if (pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getIssue() != null)
            {
                if (pubitemVO.getMetadata().getSources().get(0).getIssue() != null)
                {
                    sourceIssue = pubitemVO.getMetadata().getSources().get(0).getIssue();
                }
            }
        }
        return sourceIssue;
    }
    
    /**
     * @param pubitemVO the pubitem in which the pages of the first source should be found
     * @return the identifier
     */
    private String getSourcePages(PubItemVO pubitemVO)
    {
        String sourcePages = "";
        if (pubitemVO.getMetadata().getSources() != null)
        {
            if (pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getStartPage() != null)
            {                
                if (pubitemVO.getMetadata().getSources().get(0).getStartPage() != null)
                {
                    sourcePages = pubitemVO.getMetadata().getSources().get(0).getStartPage();
                }
                if (pubitemVO.getMetadata().getSources().get(0).getEndPage() != null)
                {
                    sourcePages = sourcePages + "-" + pubitemVO.getMetadata().getSources().get(0).getEndPage();
                }
            }
        }
        return sourcePages;
    }
    
    /**
     * @param pubitemVO the pubitem in which the one date should be found (the order is important)
     * @return the identifier
     */
    private String getDate(PubItemVO pubitemVO)
    {
        String date = "";
        if (pubitemVO.getMetadata().getDatePublishedInPrint() != null)
        {
            date = pubitemVO.getMetadata().getDatePublishedInPrint();
            return date;
        }
        if (pubitemVO.getMetadata().getDatePublishedOnline() != null)
        {
            date = pubitemVO.getMetadata().getDatePublishedOnline();
            return date;
        }
        if (pubitemVO.getMetadata().getDateAccepted() != null)
        {
            date = pubitemVO.getMetadata().getDateAccepted();
            return date;
        }
        if (pubitemVO.getMetadata().getDateSubmitted() != null)
        {
            date = pubitemVO.getMetadata().getDateSubmitted();
            return date;
        }
        if (pubitemVO.getMetadata().getDateModified() != null)
        {
            date = pubitemVO.getMetadata().getDateModified();
            return date;
        }
        if (pubitemVO.getMetadata().getDateCreated() != null)
        {
            date = pubitemVO.getMetadata().getDateCreated();
            return date;
        }
        return date;
    }
    
    /**
     * Escapes problematic HTML characters ("less than", "greater than", ampersand, apostrophe and quotation mark).
     *
     * @param cdata A String that might contain problematic HTML characters.
     * @return The escaped string.
     */
    public String htmlEscape(String cdata)
    {
        String[] problematicCharacters = {"&", ">", "<", "\"", "\'", "\n", "\r"};
        String[] escapedCharacters = {"&amp;", "&gt;", "&lt;", "&quot;", "&apos;", "<br/>", "<br/>"};
        
        // The escaping has to start with the ampsersand (&amp;, '&') !
        for (int i = 0; i < problematicCharacters.length; i++)
        {
            cdata = change(cdata, problematicCharacters[i], escapedCharacters[i]);

        }
        return cdata;
    }
    
    /**
     * Changes all occurrences of oldPat to newPat.
     *
     * @param in A String that might contain problematic HTML characters.
     * @param oldPat the old pattern to be escaped.
     * @param newPat the new pattern to escape with.
     * @return The escaped string.
     */
    private String change(String in, String oldPat, String newPat)
    {
        if (in == null)
        {
            return null;
        }
        if (oldPat.length() == 0)
        {
            return in;
        }
        if (oldPat.length() == 1 && newPat.length() == 1)
        {
            return in.replace(oldPat.charAt(0), newPat.charAt(0));
        }
        if (in.indexOf(oldPat) < 0)
        {
            return in;
        }
        int lastIndex = 0;
        int newIndex = 0;
        StringBuffer newString = new StringBuffer();
        for (;;)
        {
            newIndex = in.indexOf(oldPat, lastIndex);
            if (newIndex != -1)
            {
                newString.append(in.substring(lastIndex, newIndex) + newPat);
                lastIndex = newIndex + oldPat.length();

            }
            else
            {
                newString.append(in.substring(lastIndex));
                break;
            }
        }
        return newString.toString();
    }
}
