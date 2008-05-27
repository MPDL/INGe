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

package de.mpg.escidoc.pubman.viewItem.ui;

import javax.faces.component.html.HtmlOutputText;

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;

/**
 * UI class for generating an CoiNS span element.
 *
 * @author: Tobias Schraut, created 09.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class COinSUI
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
    
    private HtmlOutputText text;
    
    /**
     * Public constructor.
     */
    public COinSUI()
    {        
    }
    
    /**
     * Generates the desired unescaped HTML Span Tag for COinS.
     *
     * @param pubitemVO the complete pub item
     * @return HtmlOutputText a HtmlOutputText component which contains the unescaped span tag
     */
    public HtmlOutputText getCOinSTag(PubItemVO pubitemVO)
    {
        StringBuffer coinsContent = new StringBuffer();
        
        if(pubitemVO != null)
        {
            if(pubitemVO.getMetadata() != null)
            {
                String rft_genre = "";
                String rft_atitle = "";
                String rft_title = "";
                String rft_aulast = "";
                String rft_aufirst = "";
                String rft_au = "";
                String rft_issn = "";
                String rft_isbn = "";
                String rft_id = "";
                String rft_volume = "";
                String rft_issue = "";
                String rft_pages = "";
                String rft_date = "";
                
                if(pubitemVO.getMetadata().getGenre() != null)
                {
                    rft_genre = RFT_GENRE_PREFIX + pubitemVO.getMetadata().getGenre().name();
                }
                
                if(pubitemVO.getMetadata().getTitle() != null)
                {
                    rft_atitle = RFT_ATIITLE_PREFIX + pubitemVO.getMetadata().getTitle().getValue();
                    rft_atitle = rft_atitle != null ? rft_atitle.replace(" ", "+") : "";
                }
                
                // add the title of the first source
                if(pubitemVO.getMetadata().getSources() != null)
                {
                    if(pubitemVO.getMetadata().getSources().size() > 0)
                    {
                        if(pubitemVO.getMetadata().getSources().get(0).getTitle() != null)
                        {
                        	rft_title = RFT_TITLE_PREFIX + pubitemVO.getMetadata().getSources().get(0).getTitle().getValue();
                        	rft_title = rft_title != null ? rft_title.replace(" ", "+") : "";
                        }
                    }
                }
                // examine if the desired attributes are filled or not
                // if not the prefix should be also omitted 
                if(!getLastName(pubitemVO).equals(""))
                {
                    rft_aulast = RFT_AULAST_PREFIX + getLastName(pubitemVO);
                }
                
                if(!getFirstName(pubitemVO).equals(""))
                {
                    rft_aufirst = RFT_AUFIRST_PREFIX + getFirstName(pubitemVO);
                }
                
                if(!getCompleteNames(pubitemVO).equals(""))
                {
                    rft_au = RFT_AU_PREFIX + getCompleteNames(pubitemVO);
                }
                
                if(!getISSN(pubitemVO).equals(""))
                {
                    rft_issn = RFT_ISSN_PREFIX + getISSN(pubitemVO);
                }
                
                if(!getISBN(pubitemVO).equals(""))
                {
                    rft_isbn = RFT_ISBN_PREFIX + getISBN(pubitemVO);
                }
                
                if(!getSourceIdentifier(pubitemVO).equals(""))
                {
                    rft_id = RFT_ID_PREFIX + getSourceIdentifier(pubitemVO);
                }
                
                if(!getSourceVolume(pubitemVO).equals(""))
                {
                    rft_volume = RFT_VOLUME_PREFIX + getSourceVolume(pubitemVO);
                }
                
                if(!getSourceIssue(pubitemVO).equals(""))
                {
                    rft_issue = RFT_ISSUE_PREFIX + getSourceIssue(pubitemVO);
                }
                
                if(!getSourcePages(pubitemVO).equals(""))
                {
                    rft_pages = RFT_PAGES_PREFIX + getSourcePages(pubitemVO);
                }
                
                if(!getDate(pubitemVO).equals(""))
                {
                    rft_date = RFT_DATE_PREFIX + getDate(pubitemVO);
                }
                
                // Then put all fields together
                coinsContent.append(COINS_START_TAG);
                coinsContent.append(CTX_VER);
                coinsContent.append(RFT_VAL_FMT);
                coinsContent.append(RFR_ID);
                coinsContent.append(rft_genre);
                coinsContent.append(rft_atitle);
                coinsContent.append(rft_title);
                coinsContent.append(rft_aulast);
                coinsContent.append(rft_aufirst);
                coinsContent.append(rft_au);
                coinsContent.append(rft_issn);
                coinsContent.append(rft_isbn);
                coinsContent.append(rft_id);
                coinsContent.append(rft_volume);
                coinsContent.append(rft_issue);
                coinsContent.append(rft_pages);
                coinsContent.append(rft_date);
                coinsContent.append(COINS_END_TAG);
            }
        }
        
            
        this.text = new HtmlOutputText();
        this.text.setId(CommonUtils.createUniqueId(this.text));
        this.text.setEscape(false);
        this.text.setValue(coinsContent.toString());
        
        return this.text;
    }
    
    /**
     * Generates the desired unescaped HTML Span Tag for COinS.
     *
     * @param pubitemVO the complete pub item
     * @return HtmlOutputText a HtmlOutputText component which contains the unescaped span tag
     */
    public String getCOinSString(PubItemVO pubitemVO)
    {
        StringBuffer coinsContent = new StringBuffer();
        
        if(pubitemVO != null)
        {
            if(pubitemVO.getMetadata() != null)
            {
                String rft_genre = "";
                String rft_atitle = "";
                String rft_title = "";
                String rft_aulast = "";
                String rft_aufirst = "";
                String rft_au = "";
                String rft_issn = "";
                String rft_isbn = "";
                String rft_id = "";
                String rft_volume = "";
                String rft_issue = "";
                String rft_pages = "";
                String rft_date = "";
                
                if(pubitemVO.getMetadata().getGenre() != null)
                {
                    rft_genre = RFT_GENRE_PREFIX + pubitemVO.getMetadata().getGenre().name();
                }
                
                if(pubitemVO.getMetadata().getTitle() != null)
                {
                    rft_atitle = RFT_ATIITLE_PREFIX + CommonUtils.htmlEscape(pubitemVO.getMetadata().getTitle().getValue());
                    rft_atitle = rft_atitle != null ? rft_atitle.replace(" ", "+") : "";
                }
                
                // add the title of the first source
                if(pubitemVO.getMetadata().getSources() != null)
                {
                    if(pubitemVO.getMetadata().getSources().size() > 0)
                    {
                        if(pubitemVO.getMetadata().getSources().get(0).getTitle() != null)
                        {
                        	rft_title = RFT_TITLE_PREFIX + CommonUtils.htmlEscape(pubitemVO.getMetadata().getSources().get(0).getTitle().getValue());
                        	rft_title = rft_title != null ? rft_title.replace(" ", "+") : "";
                        }
                    }
                }
                // examine if the desired attributes are filled or not
                // if not the prefix should be also omitted 
                if(!getLastName(pubitemVO).equals(""))
                {
                    rft_aulast = RFT_AULAST_PREFIX + CommonUtils.htmlEscape(getLastName(pubitemVO));
                }
                
                if(!getFirstName(pubitemVO).equals(""))
                {
                    rft_aufirst = RFT_AUFIRST_PREFIX + CommonUtils.htmlEscape(getFirstName(pubitemVO));
                }
                
                if(!getCompleteNames(pubitemVO).equals(""))
                {
                    rft_au = RFT_AU_PREFIX + CommonUtils.htmlEscape(getCompleteNames(pubitemVO));
                }
                
                if(!getISSN(pubitemVO).equals(""))
                {
                    rft_issn = RFT_ISSN_PREFIX + CommonUtils.htmlEscape(getISSN(pubitemVO));
                }
                
                if(!getISBN(pubitemVO).equals(""))
                {
                    rft_isbn = RFT_ISBN_PREFIX + CommonUtils.htmlEscape(getISBN(pubitemVO));
                }
                
                if(!getSourceIdentifier(pubitemVO).equals(""))
                {
                    rft_id = RFT_ID_PREFIX + CommonUtils.htmlEscape(getSourceIdentifier(pubitemVO));
                }
                
                if(!getSourceVolume(pubitemVO).equals(""))
                {
                    rft_volume = RFT_VOLUME_PREFIX + CommonUtils.htmlEscape(getSourceVolume(pubitemVO));
                }
                
                if(!getSourceIssue(pubitemVO).equals(""))
                {
                    rft_issue = RFT_ISSUE_PREFIX + CommonUtils.htmlEscape(getSourceIssue(pubitemVO));
                }
                
                if(!getSourcePages(pubitemVO).equals(""))
                {
                    rft_pages = RFT_PAGES_PREFIX + CommonUtils.htmlEscape(getSourcePages(pubitemVO));
                }
                
                if(!getDate(pubitemVO).equals(""))
                {
                    rft_date = RFT_DATE_PREFIX + CommonUtils.htmlEscape(getDate(pubitemVO));
                }
                
                // Then put all fields together
                coinsContent.append(COINS_START_TAG);
                coinsContent.append(CTX_VER);
                coinsContent.append(RFT_VAL_FMT);
                coinsContent.append(RFR_ID);
                coinsContent.append(rft_genre);
                coinsContent.append(rft_atitle);
                coinsContent.append(rft_title);
                coinsContent.append(rft_aulast);
                coinsContent.append(rft_aufirst);
                coinsContent.append(rft_au);
                coinsContent.append(rft_issn);
                coinsContent.append(rft_isbn);
                coinsContent.append(rft_id);
                coinsContent.append(rft_volume);
                coinsContent.append(rft_issue);
                coinsContent.append(rft_pages);
                coinsContent.append(rft_date);
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
        if(pubitemVO.getMetadata().getCreators() != null)
        {
            for(int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if(pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if(pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
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
        if(pubitemVO.getMetadata().getCreators() != null)
        {
            for(int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if(pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if(pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
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
     * @param pubitemVO the pubitem in which all complete names of all creators (persons and organizations) shouldbe found
     * @return all concatinated and ; separated complete names
     */
    private String getCompleteNames(PubItemVO pubitemVO)
    {
        StringBuffer completeNames = new StringBuffer();
        if(pubitemVO.getMetadata().getCreators() != null)
        {
            for(int i = 0; i < pubitemVO.getMetadata().getCreators().size(); i++)
            {
                if(i > 0)
                {
                    completeNames.append(";+");
                }
                if(pubitemVO.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if(pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getPerson().getGivenName());
                        completeNames.append("+");
                    }
                    if(pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                }
                else if(pubitemVO.getMetadata().getCreators().get(i).getOrganization() != null && pubitemVO.getMetadata().getCreators().get(i).getOrganization().getName() != null)
                {
                    completeNames.append(pubitemVO.getMetadata().getCreators().get(i).getOrganization().getName().getValue());
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
        if(pubitemVO.getMetadata().getIdentifiers() != null)
        {
            for(int i = 0; i < pubitemVO.getMetadata().getIdentifiers().size(); i++)
            {
                if(pubitemVO.getMetadata().getIdentifiers().get(i).getType() != null)
                {
                    if(pubitemVO.getMetadata().getIdentifiers().get(i).getType().equals(IdentifierVO.IdType.ISSN))
                    {
                        if(pubitemVO.getMetadata().getIdentifiers().get(i).getId() != null)
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
        if(pubitemVO.getMetadata().getIdentifiers() != null)
        {
            for(int i = 0; i < pubitemVO.getMetadata().getIdentifiers().size(); i++)
            {
                if(pubitemVO.getMetadata().getIdentifiers().get(i).getType() != null)
                {
                    if(pubitemVO.getMetadata().getIdentifiers().get(i).getType().equals(IdentifierVO.IdType.ISBN))
                    {
                        if(pubitemVO.getMetadata().getIdentifiers().get(i).getId() != null)
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
        if(pubitemVO.getMetadata().getSources() != null)
        {
            if(pubitemVO.getMetadata().getSources().size() > 0)
            {
                if(pubitemVO.getMetadata().getSources().get(0).getIdentifiers() != null)
                {
                    if(pubitemVO.getMetadata().getSources().get(0).getIdentifiers().size() > 0)
                    {
                        for(int i = 0; i < pubitemVO.getMetadata().getSources().get(0).getIdentifiers().size(); i++)
                        {
                            if(pubitemVO.getMetadata().getSources().get(0).getIdentifiers().get(i).getType() != null)
                            {
                                if(pubitemVO.getMetadata().getSources().get(0).getIdentifiers().get(i).getType().equals(IdentifierVO.IdType.DOI))
                                {
                                    sourceIdentifier = pubitemVO.getMetadata().getSources().get(0).getIdentifiers().get(i).getId();
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
        if(pubitemVO.getMetadata().getSources() != null)
        {
            if(pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getVolume() != null) 
            {
                if(pubitemVO.getMetadata().getSources().get(0).getVolume() != null)
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
        if(pubitemVO.getMetadata().getSources() != null)
        {
            if(pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getIssue() != null)
            {
                if(pubitemVO.getMetadata().getSources().get(0).getIssue() != null)
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
        if(pubitemVO.getMetadata().getSources() != null)
        {
            if(pubitemVO.getMetadata().getSources().size() > 0
                    && pubitemVO.getMetadata().getSources().get(0).getStartPage() != null)
            {                
                if(pubitemVO.getMetadata().getSources().get(0).getStartPage() != null)
                {
                    sourcePages = pubitemVO.getMetadata().getSources().get(0).getStartPage();
                }
                if(pubitemVO.getMetadata().getSources().get(0).getEndPage() != null)
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
        if(pubitemVO.getMetadata().getDatePublishedInPrint() != null)
        {
            date = pubitemVO.getMetadata().getDatePublishedInPrint();
            return date;
        }
        if(pubitemVO.getMetadata().getDatePublishedOnline() != null)
        {
            date = pubitemVO.getMetadata().getDatePublishedOnline();
            return date;
        }
        if(pubitemVO.getMetadata().getDateAccepted() != null)
        {
            date = pubitemVO.getMetadata().getDateAccepted();
            return date;
        }
        if(pubitemVO.getMetadata().getDateSubmitted() != null)
        {
            date = pubitemVO.getMetadata().getDateSubmitted();
            return date;
        }
        if(pubitemVO.getMetadata().getDateModified() != null)
        {
            date = pubitemVO.getMetadata().getDateModified();
            return date;
        }
        if(pubitemVO.getMetadata().getDateCreated() != null)
        {
            date = pubitemVO.getMetadata().getDateCreated();
            return date;
        }
        return date;
    }
}
