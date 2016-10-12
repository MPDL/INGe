/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * TODO Description
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class DateUtilities {

  /**
   * Deserializes a String containing an <code>xs:dateTime</code> to the corresponding
   * <code>java.util.Date</code>.
   * 
   * @param dateString The String to deserialize
   * @return The corresponding <code>java.util.Date</code>
   * @throws Exception
   */
  public static Date deserializeDate(String dateString) throws Exception {
    Date date = null;
    try {
      XMLGregorianCalendar xmlGregorianCalendar =
          DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
      date = xmlGregorianCalendar.toGregorianCalendar().getTime();
    } catch (Exception e) {
      // if dateString==null, return null as result
      // this is a workaround, because JiBX 1.1.3 ignores the optional="true" flag if the field is
      // associated to a
      // JiBX <format>
      if (dateString != null) {
        throw new Exception(dateString, e);
      }
    }
    return date;
  }

  /**
   * Serializes a <code>java.util.Date</code> to a String. The format of the String is
   * "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".
   * 
   * @see java.text.SimpleDateFormat
   * @param date The Date to serialize
   * @return String The corresponding String
   */
  public static String serializeDate(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    String dateString = simpleDateFormat.format(date);
    return dateString;
  }
}
