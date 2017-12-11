package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.Properties;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.util.PropertyReader;

public class GenreValidator {

  public static void checkGenre(MdsPublicationVO.Genre genre) throws ValidationException {
    String[] allowedGenres = PropertyReader.getProperty(Properties.YEARBOOK_ALLOWED_GENRES).split(",");

    boolean found = false;

    for (String allowedGenre : allowedGenres) {

      if (genre != null && genre.toString().equals(allowedGenre)) {

        found = true;
        break;

      } // if

    } // for

    if (!found) {

      final ValidationReportVO v = new ValidationReportVO();

      final ValidationReportItemVO item =
          new ValidationReportItemVO(ErrorMessages.NO_YEARBOOK_GENRE, ValidationReportItemVO.Severity.ERROR);
      item.setElement("genre");

      v.addItem(item);

      throw new ValidationException(v);

    } // if

  }

}
