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
package de.mpg.escidoc.pubman.searchNew.criterions.dates;

import java.util.Calendar;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public class DateSearchCriterion extends SearchCriterionBase {

	private String from;
	
	private String to;
	
	public DateSearchCriterion(SearchCriterion type)
	{
		super(type);
	}
	
	@Override
	public String toCqlString() {
		
		return composeCqlFragments(getCQLSearchIndexes(), getFrom(), getTo());
	}

	@Override
	public String toQueryString() {
		return getSearchCriterion().name() + "=\"" + escapeForQueryString(from) + "|" + escapeForQueryString(to) + "\""; 
	}

	@Override
	public void parseQueryStringContent(String content) {
		//Split by '||', which have no backslash before
		String[] dateParts = content.split("(?<!\\\\)\\|");
		this.from = unescapeForQueryString(dateParts[0]);
		if(dateParts.length>1)
		{
			this.to = unescapeForQueryString(dateParts[1]);
		}
		

	}
	
	

	@Override
	public boolean isEmpty() {
		return (from==null || from.trim().isEmpty()) && (to==null || to.trim().isEmpty());
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	
	
	
	
	private String[] getCQLSearchIndexes()
	{
		switch(getSearchCriterion())
		{
			case ANYDATE : return new String[] {"escidoc.publication.published-online", "escidoc.publication.issued", "escidoc.publication.dateAccepted", 
								"escidoc.publication.dateSubmitted", "escidoc.publication.modified", "escidoc.publication.created"};
			case PUBLISHED : return new String[] {"escidoc.publication.published-online"};
			case PUBLISHEDPRINT : return new String[] {"escidoc.publication.issued"};
			case ACCEPTED : return new String[] {"escidoc.publication.dateAccepted"};
			case SUBMITTED : return new String[] {"escidoc.publication.dateSubmitted"};
			case MODIFIED : return new String[] {"escidoc.publication.modified"};
			case CREATED :  return new String[] {"escidoc.publication.created"};
			case EVENT_STARTDATE :  return new String[] {"escidoc.publication.event.start-date"};
			case EVENT_ENDDATE :  return new String[] {"escidoc.publication.event.end-date"};
		}
		
		return null;
	}
	
	
	private String composeCqlFragments(String[] searchIndexes, String minor, String major) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ( ");
        try {
			for (int i = 0; i < searchIndexes.length; i++)
			{
			    if (i == (searchIndexes.length - 1))
			    {
			       
			        buffer.append(createCqlFragment(searchIndexes[i], minor, major));
			    }
			    else 
			    {
			        buffer.append(createCqlFragment(searchIndexes[i], minor, major));
			        buffer.append(" or ");
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        buffer.append(" ) ");
        return buffer.toString();
    }
    
    private String createCqlFragment(String index, String minor, String major) throws Exception
    {
        String fromQuery = null;
        String toQuery = null;
        if (minor != null && !minor.trim().isEmpty())
        {
            minor = normalizeFromQuery(minor);
            
            fromQuery = index + ">=\"" + escapeForCql(minor) + "\""; 
            
            /*
            QueryParser parserFrom = new QueryParser(minor,
                   ">=");
            parserFrom.addCQLIndex(index);
            fromQuery = parserFrom.parse();
            */
        }
        if (major != null && !major.trim().isEmpty())
        {
            String[] majorParts = normalizeToQuery(major);
            toQuery = index + "<=\"" + escapeForCql(majorParts[0]) + "\"";
           /*
            QueryParser parserTo = new QueryParser(majorParts[0], 
                    "<=");
            parserTo.addCQLIndex(index);
            toQuery = parserTo.parse();
            */
            
            for (int i = 1; i < majorParts.length; i++)
            {	
            	String toSubQuery = index + "=\"" + escapeForCql(majorParts[i]) + "\"";
            	toQuery += " not ( " + toSubQuery + " ) ";
            	
            	/*
                QueryParser parserNotTo = new QueryParser(majorParts[i], 
                        "=");
                parserNotTo.addCQLIndex(index);
                toQuery += " " + "not" + " ( " + parserNotTo.parse() + " ) ";
                */
            }
        }
        
        StringBuffer buffer = new StringBuffer();
        
        if (fromQuery == null) 
        {
            buffer.append(" ( " + toQuery + " ) ");
        }
        else if (toQuery == null)
            
        {
            buffer.append(" ( " + fromQuery + " ) ");
        }
        else
        {
            buffer.append(" ( " + fromQuery + " and ( " + toQuery + " ) ) ");
        }
        return buffer.toString();
    }
    
    public String normalizeFromQuery(String fromQuery)
    {
        if (fromQuery == null)
        {
            return null;
        }
        else if (fromQuery.matches("\\d\\d\\d\\d"))
        {
            return fromQuery;
        }
        else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d"))
        {
            String[] parts = fromQuery.split("-");
            if ("01".equals(parts[1]))
            {
                return parts[0];
            }
            else
            {
                return fromQuery;
            }
        }
        else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d"))
        {
            String[] parts = fromQuery.split("-");
            if ("01".equals(parts[2]))
            {
                if ("01".equals(parts[1]))
                {
                    return parts[0];
                }
                else
                {
                    return parts[0] + "-" + parts[1];
                }
            }
            else
            {
                return fromQuery;
            }
        }
        else
        {
            return fromQuery;
        }
    }
    
    public String[] normalizeToQuery(String toQuery)
    {
        if (toQuery == null)
        {
            return null;
        }
        else if (toQuery.matches("\\d\\d\\d\\d"))
        {
            return new String[]{toQuery + "-12-31"};
        }
        else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d"))
        {
            String[] parts = toQuery.split("-");
            if ("12".equals(parts[1]))
            {
                return new String[]{toQuery + "-31"};
            }
            else
            {
                return new String[]{toQuery + "-31", parts[0]};
            }
        }
        else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d"))
        {
            String[] parts = toQuery.split("-");
            // Get last day of month
            if ("31".equals(parts[2]) && "12".equals(parts[1]))
            {
                return new String[]{toQuery};
            }
            else
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                int maximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (Integer.parseInt(parts[2]) == maximumDay)
                {
                    return new String[]{toQuery, parts[0]};
                }
                else
                {
                    return new String[]{toQuery, parts[0], parts[0] + "-" + parts[1]};
                }
            }
        }
        else
        {
            return new String[]{toQuery};
        }
    }

    /*
	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
	}
	*/

	

}
