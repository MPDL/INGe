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
package de.mpg.escidoc.services.search.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author endres
 *
 */
public class SearchDate
{      
    public enum DateType {
        Year_Month_Day,
        Year_Month,
        Year
    }
    
    private static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    private static final String DATE_FORMAT_MONTH = "yyyy-MM";
    private static final String DATE_FORMAT_YEAR = "yyyy";
    
    private DateType dateType = null;
    private Date dateValue = null;
    
    public SearchDate( String date ) throws ParseException {
        identifyDateType( date );
    }
    
    private void identifyDateType( String date ) throws ParseException {
        try {
            setDateValue(parseDate( date, DATE_FORMAT_DAY ));
            setDateType(DateType.Year_Month_Day);
            return;
        }
        catch( java.text.ParseException e ) {
            
        }
        try {
            setDateValue(parseDate( date, DATE_FORMAT_MONTH ));
            setDateType(DateType.Year_Month);
            return;
        }
        catch( java.text.ParseException e ) {
            
        }
        setDateValue(parseDate( date, DATE_FORMAT_YEAR ));
        setDateType(DateType.Year);
    }
    
    private Date parseDate(String dateString, String dateFormat) throws java.text.ParseException {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat( dateFormat );
        dt = df.parse( dateString );
        return dt;
    }

    /**
     * @param dateValue the dateValue to set
     */
    public void setDateValue(Date dateValue)
    {
        this.dateValue = dateValue;
    }

    /**
     * @return the dateValue
     */
    public Date getDateValue()
    {
        return dateValue;
    }

    /**
     * @param dateType the dateType to set
     */
    public void setDateType(DateType dateType)
    {
        this.dateType = dateType;
    }

    /**
     * @return the dateType
     */
    public DateType getDateType()
    {
        return dateType;
    }
    
    public String toString() {
        Calendar myCal = new GregorianCalendar();
        myCal.setTime( getDateValue() );
        StringBuffer buffer = new StringBuffer();
        switch( dateType ) {
            case Year_Month_Day:
                int month = myCal.get(Calendar.MONTH);
                month = month + 1;
                buffer.append( myCal.get(Calendar.YEAR) );
                buffer.append("-");
                buffer.append( month );
                buffer.append("-");
                buffer.append( myCal.get(Calendar.DAY_OF_MONTH) );
                return buffer.toString();
            case Year_Month:
                int m = myCal.get(Calendar.MONTH);
                m = m + 1;
                buffer.append( myCal.get(Calendar.YEAR) );
                buffer.append("-");
                buffer.append( m );
                return buffer.toString();
            case Year:
                 buffer.append( myCal.get(Calendar.YEAR) );
                 return buffer.toString();
            default: return null;
        }
    }
}
