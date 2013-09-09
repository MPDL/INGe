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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Useful HTML functionalities.
 *
 * @author Vlad Makarenko (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 */
public class HtmlUtils
{
	
	private static final Pattern SUBS_OR_SUPS = Pattern.compile(
			"\\<(\\/?su[bp])\\>",			
			Pattern.DOTALL
	);
	
	private static final Pattern SUBS_OR_SUPS_2 = Pattern.compile(
			"\\<(\\/?)(su[bp])\\>",			
			Pattern.DOTALL
	);	
	
	/**
	 * Check of the balanced tags sup/sub
	 * @param snippet
	 * @return <code>true</code> if balanced, <code>false</code> otherwise 
	 */
	public static boolean isBalanced(String snippet)
	{
		if (snippet == null)
			return true; 
		
		Stack<String> s = new Stack<String>();
		Matcher m = SUBS_OR_SUPS.matcher(snippet.toLowerCase());
		while (m.find()) 
		{
			String tag = m.group(1);
			if( tag.startsWith("su") )
			{
				s.push(tag);
			}
			else 
			{
				if ( s.empty() || !tag.equals("/" + s.pop()) )
				{
					return false;
				}
			}
		}
		
		return s.empty();
	} 
	
	/**
	 * Shortens a string to the given length and makes sure that the given html tags are still balanced in the resulting string, if they were balanced in the full string.
	 * @param snippet
	 * @param length
	 * @param tagsNotToBeEscaped
	 * @return
	 */
	public static String getShortenedHtmlSnippetWithBalancedTagsAndEscaping(String snippet, int length, List<String> tagsNotToBeEscaped)
	{
		boolean balanced = true;
		int removeLastCharacters = 0;
		
		Stack<SubSupTag> s = new Stack<SubSupTag>();
		Matcher m = Pattern.compile("\\<(\\/?)(\\S+?)\\>",	Pattern.DOTALL).matcher(snippet.toLowerCase());
		
		List<SubSupTag> tagListToBeClosed = new ArrayList<HtmlUtils.SubSupTag>();

		while (m.find()) 
		{
			String slash = m.group(1);
			String tag = m.group(2);
			
			if(tagsNotToBeEscaped.contains(tag.toLowerCase()))
			{
			
				TagType tagType = null;
	
				if(slash==null || slash.isEmpty())
				{
					tagType = TagType.BEGIN;
					
				}
				else if (slash.equals("/"))
				{
					tagType = TagType.END;
				}
	
				SubSupTag subSupTag = new HtmlUtils().new SubSupTag(tag.toLowerCase(), tagType, m.start(), m.end());
				
				if(TagType.BEGIN.equals(subSupTag.getTagType()))
				{
					s.push(subSupTag);
				}
				else if(TagType.END.equals(subSupTag.getTagType()))
				{
					if(s.isEmpty())
					{
						balanced = false;
						break;
					}
					
					SubSupTag beginTag = s.pop();
					if(!TagType.BEGIN.equals(beginTag.getTagType()) || !beginTag.getTagContent().equals(subSupTag.getTagContent()))
					{
						balanced = false;
						break;
					}
					
					//End tag ends after desired length, start tag ends before length => Tag is completely started, but not ended
					if(m.end()>=length && beginTag.getEndPosition()<length)
					{
						tagListToBeClosed.add(subSupTag);
					}
	
				}
				
				if(m.start()<length && m.end()>=length)
				{
					removeLastCharacters = length - m.start();
				}
				
			}
		}
		
		if(s.isEmpty() && balanced)
		{
			StringBuffer result = new StringBuffer(snippet.substring(0, length - removeLastCharacters));
			for(SubSupTag tag : tagListToBeClosed)
			{
				result.append(tag.toHtml());
			}
			return escapeHtmlExcept(result.toString(), tagsNotToBeEscaped);
			
		}
		
		
		snippet = snippet.substring(0, length);
		return escapeHtmlExcept(snippet, null);
		
		
	} 
	
	 public static String escapeHtmlExcept(String snippet, List<String> tagNameExceptions)
	 {

		 	snippet = Pattern.compile("\\&(?!amp;)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet).replaceAll("&amp;");

	    	StringBuffer exceptions = new StringBuffer();
	    	if(tagNameExceptions!=null)
	    	{
		    	for(int i=0; i<tagNameExceptions.size(); i++)
		    	{
		    		if(i>0)
		    		{
		    			exceptions.append("|");
		    		}
		    		exceptions.append(tagNameExceptions.get(i));
		    	}
	    	}
	    	
	    	if(tagNameExceptions!=null && !tagNameExceptions.isEmpty())
	    	{
	    		snippet = Pattern.compile("\\<(?!(\\/?("+ exceptions.toString() +")))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet).replaceAll("&lt;");
	    	}
	    	else
	    	{
	    		snippet = Pattern.compile("\\<", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet).replaceAll("&lt;");
	    	}
			
	    	return snippet;
			
		}
	
	
	
	
	
	private enum TagType
	{
		BEGIN, END;
	}
	
	private class SubSupTag
	{
		
		private int startPosition;
		
		private int endPosition;
		
		private String tagContent;
		
		private TagType tagType;
		
		
		
		public SubSupTag(String tagContent, TagType tagType, int startPosition, int endPosition) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.tagContent = tagContent;
			this.tagType = tagType;
		}

		public int getStartPosition() {
			return startPosition;
		}

		public void setStartPosition(int startPosition) {
			this.startPosition = startPosition;
		}

		public int getEndPosition() {
			return endPosition;
		}

		public void setEndPosition(int endPosition) {
			this.endPosition = endPosition;
		}

		
		public TagType getTagType() {
			return tagType;
		}

		public void setTagType(TagType tagType) {
			this.tagType = tagType;
		}

		public String toHtml()
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("<");
			if(TagType.END.equals(tagType))
			{
				buffer.append("/");
			}
			buffer.append(tagContent);
			buffer.append(">");
			return buffer.toString();
			
		}

		public String getTagContent() {
			return tagContent;
		}

		public void setTagContent(String tagContent) {
			this.tagContent = tagContent;
		}
		
	}
	
	
	public static void main(String[] args)
	{
		
		String testTitle = "Dies ist ein Testtitel mit viel H<sub>2</sub>O und 3<sup>2</sup> und so <sup>höher</sup> und <h>bla</h> und auch <SUB>tiefer</SUB>";
		List<String> tags = new ArrayList<String>();
		tags.add("sup");
		tags.add("sub");

		
		System.out.println(getShortenedHtmlSnippetWithBalancedTagsAndEscaping(testTitle, 80, tags)+"...");
	}
	
}
