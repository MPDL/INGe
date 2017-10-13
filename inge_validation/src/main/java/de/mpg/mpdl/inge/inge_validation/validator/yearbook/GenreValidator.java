package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.Properties;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/*
 * <iso:rule context="publication:publication"> <iso:assert test="@type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/article' or @type =
 * 'http://purl.org/eprint/type/Book' or @type = 'http://purl.org/eprint/type/BookItem' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/book-review' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/case-note' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/case-study' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/collected-edition' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/commentary' or @type =
 * 'http://purl.org/eprint/type/ConferencePaper' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/conference-report' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition' or
 * 
 * @type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary' or
 * 
 * @type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia' or
 * 
 * @type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift' or
 * 
 * @type = 'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook' or
 * 
 * @type = 'http://purl.org/escidoc/metadata/ves/publication-types/editorial' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/festschrift' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/handbook' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/issue' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/journal' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/monograph' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/opinion' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/paper' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' or @type =
 * 'http://purl.org/eprint/type/Report' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/series' or @type =
 * 'http://purl.org/eprint/type/Thesis' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/webpage' ">NoYearbookGenre</iso:assert>
 * </iso:rule>
 */

public class GenreValidator {

  public static void checkGenre(MdsPublicationVO.Genre genre) throws ValidationException {
    String[] allowedGenres =
        PropertyReader.getProperty(Properties.YEARBOOK_ALLOWED_GENRES).split(",");

    boolean found = false;

    for (String allowedGenre : allowedGenres) {
      if (genre != null && genre.toString().equals(allowedGenre)) {
        found = true;
        break;
      }
    }

    if (!found) {
      final ValidationReportVO v = new ValidationReportVO();

      final ValidationReportItemVO item =
          new ValidationReportItemVO(ErrorMessages.NO_YEARBOOK_GENRE,
              ValidationReportItemVO.Severity.ERROR);
      item.setElement("genre");

      v.addItem(item);

      throw new ValidationException(v);
    }
  }

}
