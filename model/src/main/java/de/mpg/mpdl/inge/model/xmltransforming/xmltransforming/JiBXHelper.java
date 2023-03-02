/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.types.Coordinates;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationPathVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.HitwordVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.LockStatus;
import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.TextFragmentVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.WrongDateException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.WrongEnumException;

/**
 * Class with helper methods for the JiBX-based XML-2-Java-Transforming.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 28.08.2007
 */
public class JiBXHelper {
  private JiBXHelper() {}

  //  public static final String DCTERMS_NAMESPACE_PREFIX = "dcterms:";
  public static final String IDTYPES_NAMESPACE_PREFIX = "eterms:";
  public static final String TYPES_NAMESPACE_PREFIX = "eterms:";

  /**
   * XML escaped characters mapping ("<" and "&" get escaped/unescaped automatically by JiBX) Note:
   * Only the characters "<" and "&" are strictly illegal in XML. Apostrophes, quotation marks and
   * greater than signs are legal, but it is a good habit to replace them.
   */
  private static final String problematicCharacters[] = {">", "\"", "\'"};
  private static final String escapedCharacters[] = {"&gt;", "&quot;", "&apos;"};

  /**
   * UTF-8 control characters that are illegal for XML according to W3C
   */
  private static final Pattern ILLEGAL_XML_CHARS =
      Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AffiliationPathVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AffiliationPathVO></code>
   */
  /*
  public static List<AffiliationPathVO> affiliationPathVOListFactory() {
    return new ArrayList<AffiliationPathVO>();
  }
  */

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AffiliationRO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AffiliationRO></code>
   */
  public static List<AffiliationRO> affiliationROListFactory() {
    return new ArrayList<AffiliationRO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AffiliationVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AffiliationVO></code>
   */
  /*
  public static List<AffiliationVO> affiliationVOListFactory() {
    return new ArrayList<AffiliationVO>();
  }
  */

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;String></code> as the implementation of
   * a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;String></code>
   */
  public static List<String> localTagsListFactory() {
    return new ArrayList<String>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;CreatorVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;CreatorVO></code>
   */
  public static List<CreatorVO> creatorVOListFactory() {
    return new ArrayList<CreatorVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;FormatVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;FormatVO></code>
   */
  public static List<FormatVO> formatVOListFactory() {
    return new ArrayList<FormatVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;GrantVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;GrantVO></code>
   */
  /*
  public static List<GrantVO> grantVOListFactory() {
    return new ArrayList<GrantVO>();
  }
  */

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AccountUserVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AccountUserVO></code>
   */
  /*
  public static List<AccountUserVO> accountUserVOListFactory() {
    return new ArrayList<AccountUserVO>();
  }
  */

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;Filter></code> as the implementation of
   * a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;Filter></code>
   */
  /*
  public static List<Filter> filterListFactory() {
    return new ArrayList<Filter>();
  }
  */

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;HitwordVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;HitwordVO></code>
   */
  public static List<HitwordVO> hitwordVOListFactory() {
    return new ArrayList<HitwordVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;IdentifierVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;IdentifierVO></code>
   */
  public static List<IdentifierVO> identifierVOListFactory() {
    return new ArrayList<IdentifierVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;MdsPublicationVO.Genre></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;MdsPublicationVO.Genre></code>
   */
  public static List<MdsPublicationVO.Genre> genreListFactory() {
    return new ArrayList<MdsPublicationVO.Genre>();
  }

  /**
   * Factory method to create a
   * <code>java.util.ArrayList&lt;MdsPublicationVO.SubjectClassification></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;MdsPublicationVO.SubjectClassification></code>
   */
  public static List<MdsPublicationVO.SubjectClassification> subjectClassificationListFactory() {
    return new ArrayList<MdsPublicationVO.SubjectClassification>();
  }



  /**
   * Factory method to create a <code>java.util.ArrayList&lt;OrganizationVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;OrganizationVO></code>
   */
  public static List<OrganizationVO> organizationVOListFactory() {
    return new ArrayList<OrganizationVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;ContextVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;ContextVO></code>
   */
  public static List<ContextVO> contextVOListFactory() {
    return new ArrayList<ContextVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;FileVO></code> as the implementation of
   * a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;FileVO></code>
   */
  public static List<FileVO> pubFileVOListFactory() {
    return new ArrayList<FileVO>();
  }

  public static List<ValueObject> memberListFactory() {
    return new ArrayList<ValueObject>();
  }

  /**
   * Factory method to create a {@link ArrayList} as the implementation of a {@link List}.
   * 
   * @return A new {@link ArrayList}.
   */
  public static List<?> adminDescriptorVOListFactory() {
    return new ArrayList<Object>();
  }


  public static List<ProjectInfoVO> projectInfoVOListFactory() {
    return new ArrayList<ProjectInfoVO>();
  }


  /**
   * Factory method to create a <code>java.util.ArrayList&lt;VersionHistoryEntryVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;VersionHistoryEntryVO></code>
   */
  public static List<VersionHistoryEntryVO> eventVOListFactory() {
    return new ArrayList<VersionHistoryEntryVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;ItemVO></code> as the implementation of
   * a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;ItemVO></code>
   */
  public static List<ItemVO> itemVOListFactory() {
    return new ArrayList<ItemVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;MetadataSetVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;MetadataSetVO></code>
   */
  public static List<MetadataSetVO> metadataSetVOListFactory() {
    return new ArrayList<MetadataSetVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;ItemRelationVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;ItemRelationVO></code>
   */
  public static List<ItemRelationVO> relationVOListFactory() {
    return new ArrayList<ItemRelationVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;SearchHitVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;SearchHitVO></code>
   */
  public static List<SearchHitVO> searchHitVOListFactory() {
    return new ArrayList<SearchHitVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;SourceVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;SourceVO></code>
   */
  public static List<SourceVO> sourceVOListFactory() {
    return new ArrayList<SourceVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;String></code> as the implementation of
   * a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;String></code>
   */
  public static List<String> stringListFactory() {
    return new ArrayList<String>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;TextFragmentVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;TextFragmentVO></code>
   */
  public static List<TextFragmentVO> textFragmentVOListFactory() {
    return new ArrayList<TextFragmentVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AlternativeTitleVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AlternativeTitleVO></code>
   */
  public static List<AlternativeTitleVO> alternativeTitleVOListFactory() {
    return new ArrayList<AlternativeTitleVO>();
  }

  /**
   * Factory method to create a <code>java.util.ArrayList&lt;AbstractVO></code> as the
   * implementation of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;AlternativeTitleVO></code>
   */
  public static List<AbstractVO> abstractVOListFactory() {
    return new ArrayList<AbstractVO>();
  }


  /**
   * Factory method to create a <code>java.util.ArrayList&lt;SubjectVO></code> as the implementation
   * of a <code>java.util.List</code>.
   * 
   * @return A new <code>java.util.ArrayList&lt;SubjectVO></code>
   */
  public static List<SubjectVO> subjectVOListFactory() {
    return new ArrayList<SubjectVO>();
  }

  public static List<SearchRetrieveRecordVO> searchRetrieveRecordVOListFactory() {
    return new ArrayList<SearchRetrieveRecordVO>();
  }

  /*
  public static List<UserAttributeVO> userAttributeVOListFactory() {
    return new ArrayList<UserAttributeVO>();
  }
  */


  /**
   * Change all occurrences of a given 'old' pattern in a String to a given 'new' pattern.
   * 
   * @param in The String to change
   * @param oldPat The old pattern
   * @param newPat The new pattern
   * @return The changed String
   */
  private static String change(String in, String oldPat, String newPat) {
    if (in == null) {
      return null;
    }
    if (oldPat.length() == 0) {
      return in;
    }
    if (oldPat.length() == 1 && newPat.length() == 1) {
      return in.replace(oldPat.charAt(0), newPat.charAt(0));
    }
    if (in.indexOf(oldPat) < 0) {
      return in;
    }
    int lastIndex = 0;
    int newIndex = 0;
    StringBuffer newString = new StringBuffer();
    for (;;) {
      newIndex = in.indexOf(oldPat, lastIndex);
      if (newIndex != -1) {
        newString.append(in.substring(lastIndex, newIndex) + newPat);
        lastIndex = newIndex + oldPat.length();
      } else {
        newString.append(in.substring(lastIndex));
        break;
      }
    }
    return newString.toString();
  }

  /**
   * Escapes unwanted XML characters.
   * 
   * @param cdata A String that might contain illegal XML characters.
   * @return The escaped String
   */
  public static String xmlEscape(String cdata) {
    // The escaping has to start with the ampersand (&amp;, '&') !
    for (int i = 0; i < problematicCharacters.length; i++) {
      cdata = change(cdata, problematicCharacters[i], escapedCharacters[i]);
    }
    return cdata;
  }

  /**
   * Unescapes the set of escaped special characters "greater than", apostrophe and quotation mark.
   * 
   * @param cdata A String that might contain illegal XML characters.
   * @return The unescaped String
   */
  public static String xmlUnescape(String cdata) {
    // The unescaping has to end with the ampersand (&amp;, '&')
    for (int i = escapedCharacters.length - 1; i >= 0; i--) {
      cdata = change(cdata, escapedCharacters[i], problematicCharacters[i]);
    }
    return cdata;
  }

  /**
   * Deserializes a String containing 'true' or 'false' to the corresponding boolean value.
   * 
   * @param booleanValue The String to deserialize
   * @return boolean The corresponding boolean
   * @throws WrongEnumException
   */
  public static boolean deserializeBoolean(String booleanValue) throws WrongEnumException {
    boolean bool = false;
    if (booleanValue == null) {
      throw new WrongEnumException("element is null.");
    } else {
      booleanValue = booleanValue.trim().toUpperCase();
      if (booleanValue.equals("TRUE")) {
        bool = true;
      } else if (booleanValue.equals("FALSE")) {
        bool = false;
      } else {
        throw new WrongEnumException("boolean value is '" + booleanValue + "'.");
      }
    }
    return bool;
  }

  /**
   * Serializes a boolean to a corresponding String ('true' or 'false').
   * 
   * @param bool The boolean to serialize
   * @return String The corresponding String ('true' or 'false')
   */
  public static String serializeBoolean(boolean bool) {
    return (bool == true ? "true" : "false");
  }

  /**
   * Deserializes a String containing a creator-role like defined in escidocenumtypes.xsd to the
   * corresponding CreatorVO.CreatorRole Enum.
   * 
   * @param enumValue The String to deserialize
   * @return The corresponding CreatorVO.CreatorRole Enum
   * @throws WrongEnumException
   */
  public static CreatorRole deserializeCreatorRoleEnum(String enumValue) throws WrongEnumException {
    if (enumValue == null) {
      throw new WrongEnumException("CreatorRoleEnum is null.");
    } else {
      for (CreatorRole cr : CreatorRole.values()) {
        if (enumValue.equals(cr.getUri())) {
          return cr;
        }
      }

      // throw exception if not found
      throw new WrongEnumException("CreatorRoleEnum value is '" + enumValue + "'.");

      /*
       * String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase(); try { creatorRole
       * = CreatorRole.valueOf(upperCaseText); } catch (IllegalArgumentException e) { throw new
       * WrongEnumException("CreatorRoleEnum value is '" + enumValue + "'.", e); }
       */
    }
    // return creatorRole;
  }

  /**
   * Serializes enums to strings
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeCreatorRoleEnumToString(CreatorRole enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.getUri();
    }
    return enumString;
  }



  /**
   * Deserializes a String containing an <code>xs:dateTime</code> to the corresponding
   * <code>java.util.Date</code>.
   * 
   * @param dateString The String to deserialize
   * @return The corresponding <code>java.util.Date</code>
   * @throws WrongDateException
   */
  public static Date deserializeDate(String dateString) throws WrongDateException {
    Date date = null;
    try {
      XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
      date = xmlGregorianCalendar.toGregorianCalendar().getTime();
    } catch (Exception e) {
      // if dateString==null, return null as result
      // this is a workaround, because JiBX 1.1.3 ignores the optional="true" flag if the field is
      // associated to a
      // JiBX <format>
      if (dateString != null) {
        throw new WrongDateException(dateString, e);
      }
    }
    return date;
  }

  /**
   * Deserializes a String containing an <code>xs:dateTime</code> to the corresponding
   * <code>java.util.Date</code>.
   * 
   * @param dateString The String to deserialize
   * @return The corresponding <code>java.util.Date</code>
   * @throws WrongDateException
   */
  public static Coordinates deserializeCoordinates(String coordString) throws Exception {
    return new Coordinates(coordString);
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

  /**
   * Deserializes a String to a <code>java.net.URL</code>.
   * 
   * @param urlString The URL String to deserialize
   * @return The corresponding <code>java.net.URL</code>
   */
  public static URL deserializeURL(String urlString) {
    java.net.URL url = null;
    try {
      url = new java.net.URL(urlString);
    } catch (MalformedURLException e) {
      // log it and give back a null URL
      StringBuffer sb = new StringBuffer();
      sb.append("MalformedURLException in " + JiBXHelper.class.getSimpleName() + ":deserializeURL(). ");
      sb.append("The affected url [String] was: '" + urlString + "'.");
      // logger.debug(sb.toString());
    }
    return url;
  }

  /**
   * Serializes a <code>java.net.URL</code> to a String.
   * 
   * @param url The <code>java.net.URL</code> to serialize
   * @return The corresponding String
   */
  public static String serializeURL(java.net.URL url) {
    return url.toString();
  }

  /**
   * Deserializes a String containing a degree type like defined in escidocenumtypes.xsd to the
   * corresponding MdsPublicationVO.DegreeType enum.
   * 
   * @param enumValue The String to deserialize
   * @return DegreeType The corresponding MdsPublicationVO.DegreeType Enum
   * @throws WrongEnumException
   */
  public static DegreeType deserializeDegreeTypeEnum(String enumValue) throws WrongEnumException {
    if (enumValue == null) {
      throw new WrongEnumException("degree is null.");
    } else {

      for (DegreeType dt : DegreeType.values()) {
        if (enumValue.equals(dt.getUri())) {
          return dt;
        }
      }

      // throw exception if not found
      throw new WrongEnumException("DegreeTypeEnum value is '" + enumValue + "'.");

      /*
       * String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase(); try { degreeType =
       * DegreeType.valueOf(upperCaseText); } catch (IllegalArgumentException e) { throw new
       * WrongEnumException("DegreeTypeEnum value is '" + enumValue + "'.", e); }
       */
    }

    // return degreeType;
  }

  /**
   * Serializes enums to strings
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeDegreeTypeEnumToString(DegreeType enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.getUri();
    }
    return enumString;
  }

  /**
   * Deserializes a String containing a visibility-type like defined in components.xsd to the
   * corresponding <code>FileVO.Visibility</code> Enum. As JiBX v1.1.3 calls the deserialization
   * method for all optional attributes also (see
   * http://www.mail-archive.com/jibx-users@lists.sourceforge.net/msg00003.html), a
   * <code>null</code> value is returned when this method is called with a <code>null</code>
   * parameter.
   * 
   * @param enumValue The String to deserialize
   * @return Visibility The corresponding <code>FileVO.Visibility</code> Enum (or <code>null</code>)
   * @throws WrongEnumException
   */
  public static Visibility deserializeFileVisibilityEnum(String enumValue) throws WrongEnumException {
    Visibility visibility = null;
    if ((enumValue == null) || enumValue.equals("")) {
      /*-
       * As JiBX v1.1.3 calls the deserialization method for all optional attributes also (see
       * {@link http://www.mail-archive.com/jibx-users@lists.sourceforge.net/msg00003.html}, a <code>null</code> value is returned
       * when this method is called with a <code>null</code> parameter.
       */
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        visibility = Visibility.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("FileVisibilityEnum value is '" + enumValue + "'.");
      }

    }
    return visibility;
  }

  /**
   * Deserializes a String containing a genre type like defined in escidocenumtypes.xsd to the
   * corresponding <code>MdsPublicationVO.Genre</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return Genre The corresponding <code>MdsPublicationVO.Genre</code> Enum
   * @throws WrongEnumException
   */
  public static MdsPublicationVO.Genre deserializeGenreEnum(String enumValue) throws WrongEnumException {
    if (enumValue == null) {
      throw new WrongEnumException("genre is null.");
    } else {

      for (MdsPublicationVO.Genre g : MdsPublicationVO.Genre.values()) {
        if (enumValue.equals(g.getUri())) {
          return g;
        }
      }

      // throw exception if not found
      // throw new WrongEnumException("GenreEnum value is '" + enumValue + "'.");

      /*
       * String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase(); try { genre =
       * MdsPublicationVO.Genre.valueOf(upperCaseText); } catch (IllegalArgumentException e) { throw
       * new WrongEnumException("GenreEnum value is '" + enumValue + "'."); }
       */
    }

    // Logger not working here
    System.out.println("Genre " + enumValue + " could not be found. Returning null.");
    return null;
  }

  public static MdsPublicationVO.SubjectClassification deserializeSubjectClassificationEnum(String enumValue) throws WrongEnumException {
    if (enumValue == null) {
      throw new WrongEnumException("SubjectClassification is null.");
    } else {

      for (MdsPublicationVO.SubjectClassification g : MdsPublicationVO.SubjectClassification.values()) {
        if (enumValue.equals(g.getUri())) {
          return g;
        }
      }
    }

    return null;
  }

  /**
   * Serializes enums to strings
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeGenreEnum(MdsPublicationVO.Genre enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.getUri();
    }
    return enumString;
  }

  public static String serializeSubjectClassificationEnum(MdsPublicationVO.SubjectClassification enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.name();
    }
    return enumString;
  }

  /**
   * Deserializes a String containing a <code>xsi:type</code> attribute of <dc:identifier> and
   * <escidoc:identifier> elements (referencing to the types defined in escidocidtypes.xsd and
   * dcterms.xsd) to the corresponding <code>IdentifierVO.IdType</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return IdType The corresponding <code>IdentifierVO.IdType</code> Enum
   * @throws WrongEnumException
   */
  public static IdType deserializeIdentifierTypeEnum(String enumValue) throws WrongEnumException {
    IdType idType = null;
    if (enumValue == null) {
      throw new WrongEnumException("'xsi:type' attribute of 'identifier' element is null.");
    } else {
      String upperCaseTextWithoutNamespace = enumValue.substring(enumValue.lastIndexOf(':') + 1).trim().toUpperCase();
      try {
        idType = IdType.valueOf(upperCaseTextWithoutNamespace);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("IdentifierTypeEnum value ('xsi:type' attribute of 'identifier' element) is '" + enumValue + "'.");
      }
    }
    return idType;
  }

  /**
   * Deserializes a String containing a <code>xsi:type</code> attribute of AlternativeTitleVO
   * elements (referencing to the types defined in escidoctypes.xsd) to the corresponding String.
   * 
   * @param value The String to deserialize
   * @return String The corresponding string
   */
  public static String deserializeTextTypeEnum(String value) {
    if (value == null) {
      throw new NullPointerException("'xsi:type' attribute of text element is null.");
    } else {
      String upperCaseTextWithoutNamespace = value.substring(value.lastIndexOf(':') + 1).trim().toUpperCase();
      return upperCaseTextWithoutNamespace;
    }
  }

  /**
   * Serializes an <code>IdentifierVO.IdType</code> Enum to a String that corresponds to the types
   * defined in escidocidtypes.xsd and dcterms.xsd.
   * 
   * @param idType The <code>IdentifierVO.IdType</code> to serialize
   * @return The corresponding 'xsi:type' String (cf. escidocidtypes.xsd and
   *         http://dublincore.org/schemas/xmls/qdc/2003/04/02/dcterms.xsd)
   */
  public static String serializeIdentifierTypeEnum(IdType idType) {

    if (idType == null) {
      return "";
    }

    return IDTYPES_NAMESPACE_PREFIX + idType.name();
  }

  /**
   * Serializes an <code>AlternativeTitleVO.Type</code> Enum to a String that corresponds to the
   * types defined in escidoctypes.xsd.
   * 
   * @param type The <code>AlternativeTitleVO.Type</code> to serialize
   * @return The corresponding 'xsi:type' String (cf. escidocidtypes.xsd and
   *         http://dublincore.org/schemas/xmls/qdc/2003/04/02/dcterms.xsd)
   */
  public static String serializeTextTypeEnum(String type) {

    if (type == null) {
      return "";
    }

    return TYPES_NAMESPACE_PREFIX + type;
  }

  /**
   * Deserializes a String containing an invitation status like defined in escidocenumtypes.xsd to
   * the corresponding <code>VersionHistoryEntryVO.InvitationStatus</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return InvitationStatus The corresponding <code>VersionHistoryEntryVO.InvitationStatus</code>
   *         Enum
   * @throws WrongEnumException
   */
  public static InvitationStatus deserializeInvitationStatusEnum(String enumValue) throws WrongEnumException {
    InvitationStatus invitationStatus = null;
    if (enumValue == null) {
      throw new WrongEnumException("invitation-status is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      if (upperCaseText.equals("INVITED")) {
        invitationStatus = InvitationStatus.INVITED;
      } else {
        throw new WrongEnumException("InvitationStatusEnum value is '" + enumValue + "'.");
      }
    }
    return invitationStatus;
  }

  /**
   * Deserializes a String containing a lock-status-type like defined in item.xsd to the
   * corresponding <code>ItemVO.LockStatus</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return ValidityStatus The corresponding <code>ItemVO.LockStatus</code> Enum
   * @throws WrongEnumException
   */
  public static LockStatus deserializeLockStatusEnum(String enumValue) throws WrongEnumException {
    LockStatus lockStatus = null;
    if (enumValue == null) {
      throw new WrongEnumException("lock-status is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        lockStatus = LockStatus.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("LockStatusEnum value is '" + enumValue + "'.", e);
      }
    }
    return lockStatus;
  }

  /**
   * Deserializes a String containing a ChecksumAlgorithm-type like defined in item.xsd to the
   * corresponding <code>FileVO.ChecksumAlgorithm</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return ChecksumAlgorithm The corresponding <code>FileVO.ChecksumAlgorithm</code> Enum
   * @throws WrongEnumException
   */
  public static ChecksumAlgorithm deserializeChecksumAlgorithmEnum(String enumValue) throws WrongEnumException {
    ChecksumAlgorithm checksumAlgorithm = null;
    if (enumValue == null) {
      throw new WrongEnumException("ChecksumAlgorithm is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        checksumAlgorithm = ChecksumAlgorithm.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("ChecksumAlgorithmEnum value is '" + enumValue + "'.", e);
      }
    }
    return checksumAlgorithm;
  }

  /**
   * Serializes a Java Enum of arbitrary type to the corresponding String representation according
   * to the following rules: Every upper case letter is replaced by a lower case letter and every
   * underscore is replaced by a hyphen. If theses rules are not sufficient, a specialized
   * serialization method has to be used instead.
   * 
   * @param enumeration The Enum to serialize
   * @return String The corresponding String
   */
  public static String serializeRegularEnumToString(Enum<?> enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.toString().replace('_', '-').toLowerCase();
    }
    return enumString;
  }

  /**
   * Serializes enums to strings leaving the cases as they are but replacing _ by -.
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeUppercaseEnumToString(Enum<?> enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.toString().replace('_', '-');
    }
    return enumString;
  }

  /**
   * Serializes a Java Enum of arbitrary type to the corresponding String representation according
   * to the following rules: Every upper case letter is replaced by a lower case letter and every
   * underscore is replaced by a hyphen. If theses rules are not sufficient, a specialized
   * serialization method has to be used instead.
   * 
   * @param enumeration The Enum to serialize
   * @return String The corresponding String
   */
  public static String serializeEnumLikeStringToString(String enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.replace('_', '-').toLowerCase();
    }
    return enumString;
  }

  /**
   * Deserializes a String containing a review-method like defined in escidocenumtypes.xsd to the
   * corresponding <code>MdsPublicationVO.ReviewMethod</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return ReviewMethod The corresponding <code>MdsPublicationVO.ReviewMethod</code> Enum
   * @throws WrongEnumException
   */
  public static ReviewMethod deserializeReviewMethodEnum(String enumValue) throws WrongEnumException {
    if (enumValue == null) {
      throw new WrongEnumException("review-method is null.");
    } else {

      for (ReviewMethod rm : ReviewMethod.values()) {
        if (enumValue.equals(rm.getUri())) {
          return rm;
        }
      }

      // throw exception if not found
      throw new WrongEnumException("ReviewMethodEnum value is '" + enumValue + "'.");

      /*
       * String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase(); try { reviewMethod
       * = ReviewMethod.valueOf(upperCaseText); } catch (IllegalArgumentException e) { throw new
       * WrongEnumException("ReviewMethodEnum value is '" + enumValue + "'."); }
       */

    }
    // return reviewMethod;
  }

  /**
   * Serializes enums to strings
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeReviewMethodEnumToString(ReviewMethod enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.getUri();
    }
    return enumString;
  }

  /**
   * Deserializes a String containing a type like defined in search-result.xsd to the corresponding
   * <code>SearchHitVO.SearchHitType</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return SearchHitType The corresponding <code>SearchHitVO.SearchHitType</code> Enum
   * @throws WrongEnumException
   */
  public static SearchHitType deserializeSearchHitTypeEnum(String enumValue) throws WrongEnumException {
    SearchHitVO.SearchHitType searchHitType = null;
    if (enumValue == null) {
      throw new WrongEnumException("search-hit.@type is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        searchHitType = SearchHitVO.SearchHitType.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("SearchHitTypeEnum value is '" + enumValue + "'.", e);
      }
    }
    return searchHitType;
  }

  /**
   * Deserializes a String containing a genre type like defined in escidocenumtypes.xsd to the
   * corresponding <code>SourceVO.Genre</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return Genre The corresponding <code>SourceVO.Genre</code> Enum (if set), null otherwise.
   * @throws WrongEnumException
   */
  public static SourceVO.Genre deserializeSourceGenreEnum(String enumValue) throws WrongEnumException {
    if (enumValue != null) {

      for (SourceVO.Genre g : SourceVO.Genre.values()) {
        if (enumValue.equals(g.getUri())) {
          return g;
        }
      }
    }
    // Logger not working here
    System.out.println("SourceGenre " + enumValue + " could not be found. Returning null.");
    return null; // null is a possible return value
  }

  /**
   * Serializes enums to strings
   * 
   * @param enumeration The enum
   * @return The searialized string
   */
  public static String serializeSourceGenreEnum(SourceVO.Genre enumeration) {
    String enumString = "";
    if (enumeration != null) {
      enumString = enumeration.getUri();
    }
    return enumString;
  }

  /**
   * Deserializes a String containing a genre type like defined in escidocenumtypes.xsd to the
   * corresponding <code>SourceVO.Genre</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return Genre The corresponding <code>SourceVO.Genre</code> Enum (if set), null otherwise.
   * @throws WrongEnumException
   */
  public static PublicationAdminDescriptorVO.Workflow deserializeWorkflowEnum(String enumValue) throws WrongEnumException {
    PublicationAdminDescriptorVO.Workflow workflow = null;
    if (enumValue != null) {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        workflow = PublicationAdminDescriptorVO.Workflow.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("WorkflowEnum value is '" + enumValue + "'.");
      }
    }
    return workflow; // null is a possible return value
  }

  /**
   * Deserializes a String containing a genre type like defined in escidocenumtypes.xsd to the
   * corresponding <code>SourceVO.Genre</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return Genre The corresponding <code>SourceVO.Genre</code> Enum (if set), null otherwise.
   * @throws WrongEnumException
   */
  public static EventLogEntryVO.EventType deserializeEventTypeEnum(String enumValue) throws WrongEnumException {
    EventLogEntryVO.EventType type = null;
    if ("create".equals(enumValue)) {
      return EventLogEntryVO.EventType.CREATE;
    } else if ("update".equals(enumValue)) {
      return EventLogEntryVO.EventType.UPDATE;
    } else if ("submitted".equals(enumValue)) {
      return EventLogEntryVO.EventType.SUBMIT;
    } else if ("released".equals(enumValue)) {
      return EventLogEntryVO.EventType.RELEASE;
    } else if ("withdrawn".equals(enumValue)) {
      return EventLogEntryVO.EventType.WITHDRAW;
    } else if ("in-revision".equals(enumValue)) {
      return EventLogEntryVO.EventType.IN_REVISION;
    } else if ("assignVersionPid".equals(enumValue)) {
      return EventLogEntryVO.EventType.ASSIGN_VERSION_PID;
    }

    return type; // null is a possible return value
  }

  /**
   * Deserializes a String containing a status-type like defined in context.xsd to the corresponding
   * <code>ContextVO.State</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return ContextVO.State The corresponding <code>ContextVO.State</code> Enum
   * @throws WrongEnumException
   */
  public static ContextVO.State deserializePubCollectionStateEnum(String enumValue) throws WrongEnumException {
    ContextVO.State state = null;
    if (enumValue == null) {
      throw new WrongEnumException("context status is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        state = ContextVO.State.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("context status value is '" + enumValue + "'.", e);
      }
    }
    return state;
  }

  /**
   * Deserializes a String containing a status-type like defined in item.xsd to the corresponding
   * <code>ItemVO.State</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return ItemVO.State The corresponding <code>ItemVO.State</code> Enum.
   * 
   * @throws WrongEnumException Thrown if string value does not match any value of the enum.
   */
  public static ItemVO.State deserializeItemStateEnum(String enumValue) throws WrongEnumException {
    ItemVO.State state = null;
    if (enumValue == null) {
      throw new WrongEnumException("item status is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        state = ItemVO.State.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("item status value is '" + enumValue + "'.", e);
      }
    }
    return state;
  }

  /**
   * Deserializes a String containing a visibility-type like defined in components.xsd to the
   * corresponding <code>FileVO.Visibility</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return State The corresponding <code>FileVO.Visibility</code> Enum
   * @throws WrongEnumException
   */
  public static Visibility deserializeVisibilityEnum(String enumValue) throws WrongEnumException {
    Visibility visibility = null;
    if (enumValue == null) {
      throw new WrongEnumException("visibility is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        visibility = Visibility.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("visibilityvalue  is '" + enumValue + "'.", e);
      }
    }
    return visibility;
  }


  /**
   * Deserializes a String containing a storage-attribute like defined in components.xsd to the
   * corresponding <code>FileVO.Storage</code> Enum.
   * 
   * @param enumValue The String to deserialize
   * @return Storage The corresponding <code>FileVO.Storage</code> Enum
   * @throws WrongEnumException
   */
  public static Storage deserializeStorageEnum(String enumValue) throws WrongEnumException {
    Storage storage = null;
    if (enumValue == null) {
      throw new WrongEnumException("storage is null.");
    } else {
      String upperCaseText = enumValue.trim().replace('-', '_').toUpperCase();
      try {
        storage = Storage.valueOf(upperCaseText);
      } catch (IllegalArgumentException e) {
        throw new WrongEnumException("Storage value  is '" + enumValue + "'.", e);
      }
    }
    return storage;
  }

  //  /**
  //   * Removes from a String everything before the last slash and the slash itself.
  //   * 
  //   * @param prefixedString The String to be freed from everyting before the last slash and the slash
  //   *        itself
  //   * @return The String without the prefix (everything before the last slash and the slash itself)
  //   */
  //  public static String removeLinkPrefix(String prefixedString) {
  //    return new String(prefixedString.substring(prefixedString.lastIndexOf('/') + 1));
  //  }

  //  /**
  //   * Adds the prefix '/ir/context/' to the given String and gives back the result.
  //   * 
  //   * @param unprefixedString A context String without the link prefix (&quot;/ir/context/&quot;)
  //   * @return The String with added prefix (&quot;/ir/context/&quot;)
  //   */
  //  public static String addContextLinkPrefix(String unprefixedString) {
  //    return new String("/ir/context/" + removeLinkPrefix(unprefixedString));
  //  }

  //  /**
  //   * Adds the prefix '/um/user-account/' to the given String and gives back the result.
  //   * 
  //   * @param unprefixedString A context String without the creator prefix
  //   *        (&quot;/um/user-account/&quot;)
  //   * @return The String with added prefix (&quot;/um/user-account/&quot;)
  //   */
  //  public static String addCreatorLinkPrefix(String unprefixedString) {
  //    return new String("/um/user-account/" + removeLinkPrefix(unprefixedString));
  //  }

  //  /**
  //   * Adds the prefix '/oum/organizational-unit/' to the given String and gives back the result.
  //   * 
  //   * @param unprefixedString A context String without the organizational unit prefix
  //   *        (&quot;/oum/organizational-unit/&quot;)
  //   * @return The String with added prefix (&quot;/oum/organizational-unit/&quot;)
  //   */
  //  public static String addOrganizationalUnitLinkPrefix(String unprefixedString) {
  //    return new String("/oum/organizational-unit/" + removeLinkPrefix(unprefixedString));
  //  }

  /**
   * Removes characters from a string that are illegal according to W3C spec
   * http://www.w3.org/TR/xml/#charsets
   * 
   * @param cdata
   * @return
   */
  public static String removeIllegalXmlCharacters(String cdata) {
    if (cdata != null) {
      return ILLEGAL_XML_CHARS.matcher(cdata).replaceAll("");
    }

    return cdata;
  }
}
