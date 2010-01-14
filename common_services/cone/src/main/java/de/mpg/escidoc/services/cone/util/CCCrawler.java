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

package de.mpg.escidoc.services.cone.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CCCrawler
{
    
    private static final String ccUrl = "http://creativecommons.org/license/results-one?"
    		+ "q_1=2&q_1=1&field_format=&field_worktitle=&field_attribute_to_name=&"
    		+ "field_attribute_to_url=&field_sourceurl=&field_morepermissionsurl=&n_questions=3";
    
    public enum YesNo
    {
        n, yes;
        
        public String toBoolean()
        {
            return (this == YesNo.yes) + "";
        }
    }
    
    public enum Language
    {
        af, bg, ca, cs, da, de, en, en_CA, en_GB, en_HK, en_SG, en_US, eo, es, es_AR, es_CL, es_CO, es_EC, es_GT, es_MX, es_PE, eu, fi, fr, fr_CA, gl, he, hr, hu, it, ja, ko, mk, ms, nl, no, nso, pl, pt, ro, sl, sr, sr_LATN, st, sv, th, zh, zh_HK, zh_TW, zu
    }
    
    public enum Jurisdiction
    {
        ar, at, au, be, bg, br, ca, ch, cl, cn, co, cz, de, dk, ec, es, fi, fr, gr, gt, hk, hr, hu, il, in, it, jp, kr, lu, mk, mt, mx, my, nl, no, nz, pe, ph, pl, pr, pt, ro, rs, scotland, se, sg, si, th, tw, uk, us, za
    }
        
//        &field_commercial=yes
//        &field_derivatives=yes
//        &field_jurisdiction=de
    
//        &lang=de_DE
//        &language=de_DE
//        
    
    private static Querier querier = null;
    
    public static void main(String[] args) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        querier = QuerierFactory.newQuerier();
        
        // field_commercial
        for (YesNo fieldCommercial : YesNo.values())
        {
            // field_derivatives
            for (YesNo fieldDerivatives : YesNo.values())
            {
             // field_derivatives
                for (Jurisdiction fieldJurisdiction : Jurisdiction.values())
                {
                    String licenceUrl = ccUrl
                        + "&field_commercial="
                        + fieldCommercial.toString()
                        + "&field_derivatives="
                        + fieldDerivatives.toString()
                        + "&field_jurisdiction="
                        + fieldJurisdiction.toString()
                        + "&lang=de_DE";
                    System.out.println(licenceUrl);
                    GetMethod method = new GetMethod(licenceUrl);
                    httpClient.executeMethod(method);
                    
                    if (method.getStatusCode() == 200)
                    {
                        TreeFragment fragment = new TreeFragment();
                        
                        String key1 = "urn:cone:commercial";
                        String key3 = "urn:cone:jurisdiction";

                        List<LocalizedTripleObject> list = new ArrayList<LocalizedTripleObject>();
                        list.add(new LocalizedString(fieldCommercial.toBoolean()));
                        fragment.put(key1, list);

                        List<LocalizedTripleObject> list2 = new ArrayList<LocalizedTripleObject>();
                        list2.add(new LocalizedString(fieldDerivatives.toBoolean()));
                        fragment.put("urn:cone:derivatives", list2);
                        
                        List<LocalizedTripleObject> list3 = new ArrayList<LocalizedTripleObject>();
                        list3.add(new LocalizedString(fieldJurisdiction.toString()));
                        fragment.put(key3, list3);
                        
                        String codeToCopy = extractCode(method);
                        
                        Pattern urlPattern = Pattern.compile("href=\"([^\"]+)\"");
                        Matcher urlMatcher = urlPattern.matcher(codeToCopy);
                        if (urlMatcher.find())
                        {
                            String url = urlMatcher.group(1); 
                            fragment.setSubject(url);
                            
                            Pattern versionPattern = Pattern.compile("/(\\d+\\.\\d+)/[^/]+/$");
                            Matcher versionMatcher = versionPattern.matcher(url);
                            if (versionMatcher.find())
                            {
                                list = new ArrayList<LocalizedTripleObject>();
                                list.add(new LocalizedString(versionMatcher.group(1)));
                                fragment.put("urn:cone:version", list);
                            }

                            Pattern imgPattern = Pattern.compile("src=\"([^\"]+)\"");
                            Matcher imgMatcher = imgPattern.matcher(codeToCopy);
                            if (imgMatcher.find())
                            {
                                list = new ArrayList<LocalizedTripleObject>();
                                list.add(new LocalizedString(imgMatcher.group(1)));
                                fragment.put("http://xmlns.com/foaf/0.1/depiction", list);
                            }
                            
                            GetMethod method2 = new GetMethod(url);
                            httpClient.executeMethod(method2);
                            String page = method2.getResponseBodyAsString();
                            
                            Pattern namePattern = Pattern.compile("<h2 property=\"dc:title\">([^<]+)</h2>");
                            Matcher nameMatcher = namePattern.matcher(page);
                            if (nameMatcher.find())
                            {
                                list = new ArrayList<LocalizedTripleObject>();
                                list.add(new LocalizedString(nameMatcher.group(1)));
                                fragment.put("http://purl.org/dc/elements/1.1/title", list);
                            }
                            
                            List<LocalizedTripleObject> languages = extractLanguages(page, url);
                            
                            fragment.put("urn:cone:translation", languages);
                            
                            querier.delete("cclicences", url);
                            querier.create("cclicences", url, fragment);
                        }
                        
                    }
                    else
                    {
                        System.out.println("Not found: " + licenceUrl);
                    }
                }
            }
        }
    }

    private static List<LocalizedTripleObject> extractLanguages(String page, String baseURL) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        List<LocalizedTripleObject> result = new ArrayList<LocalizedTripleObject>();
        
        Pattern pattern = Pattern.compile("<a\\s+href=\"./([^\"]+)\"\\s+title=\"([^\"]+)\"\\s+hreflang=\"([^\"]+)\"\\s+rel=\"alternate nofollow\"\\s+(xml:)?lang=\"([^\"]+)\">");
        Matcher matcher = pattern.matcher(page);
        int start = 0;
        while (matcher.find(start))
        {
            String genid = querier.createUniqueIdentifier(null);
            TreeFragment treeFragment = new TreeFragment(genid);
            
            String locale = matcher.group(3);
            
            treeFragment.setLanguage(locale.split("_")[0]);
            
            String url = baseURL + matcher.group(1);
            List<LocalizedTripleObject> list = new ArrayList<LocalizedTripleObject>();
            list.add(new LocalizedString(url));
            treeFragment.put("http://purl.org/dc/elements/1.1/identifier", list);
            
            list = new ArrayList<LocalizedTripleObject>();
            list.add(new LocalizedString(matcher.group(2)));
            treeFragment.put("http://purl.org/dc/elements/1.1/title", list);
            
            list = new ArrayList<LocalizedTripleObject>();
            list.add(new LocalizedString(locale));
            treeFragment.put("urn:cone:locale", list);
            
            GetMethod method = new GetMethod(url);
            httpClient.executeMethod(method);
            String translation = method.getResponseBodyAsString();
            
            Pattern namePattern = Pattern.compile("<h2 property=\"dc:title\">([^<]+)</h2>");
            Matcher nameMatcher = namePattern.matcher(translation);
            if (nameMatcher.find())
            {
                list = new ArrayList<LocalizedTripleObject>();
                list.add(new LocalizedString(nameMatcher.group(1)));
                treeFragment.put("http://purl.org/dc/elements/1.1/title", list);
            }
            
            result.add(treeFragment);
            
            start = matcher.end();
        }
        return result;
    }

    /**
     * @param method
     * @throws IOException
     */
    private static String extractCode(GetMethod method) throws IOException
    {
        String page = method.getResponseBodyAsString();
        if (page.contains("<textarea id=\"codetocopy\""))
        {
            int start = page.indexOf("<textarea id=\"codetocopy\"") + 25;
            start = page.indexOf(">", start) + 1;
            int end = page.indexOf("</textarea>", start);
            String result = page.substring(start, end);
            result = decode(result);
            return result;
           
        }
        else
        {
            System.out.println("codetocopy not found: " + method.getPath());
            return null;
        }
    }
    
    private static String decode(String result)
    {
        result = result.replace("&lt;", "<");
        result = result.replace("&gt;", ">");
        result = result.replace("&amp;", "&");
        return result;
    }
        
}
