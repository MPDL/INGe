package de.mpg.mpdl.inge.rest.web.exceptions;

import java.text.ParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.impl.OrganizationServiceDbImpl;

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  private final static Logger logger = LogManager.getLogger(PubmanRestExceptionHandler.class);


  @ResponseBody
  @ExceptionHandler(AaException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError aaExceptionHandler(AaException aaException) {
    if (aaException.getMessage() != null) {
      return new VndError("400", aaException.getMessage());
    } else {
      return new VndError("400", aaException.getClass().getCanonicalName() + ": "
          + "no message available");
    }
  }

  @ResponseBody
  @ExceptionHandler(IngeServiceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError iingeServiceExceptionHandler(IngeServiceException iingeServicexception) {
    if (iingeServicexception.getMessage() != null) {
      return new VndError("400", iingeServicexception.getMessage());
    } else {
      return new VndError("400", iingeServicexception.getClass().getCanonicalName() + ": "
          + "no message available");
    }
  }

  @ResponseBody
  @ExceptionHandler(ItemInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError itemInvalidExceptionHandler(ItemInvalidException itemInvalidException) {
    if (itemInvalidException.getMessage() != null) {
      return new VndError("400", itemInvalidException.getMessage());
    } else {
      return new VndError("400", itemInvalidException.getClass().getCanonicalName() + ": "
          + "no message available");
    }
  }

  @ResponseBody
  @ExceptionHandler(ParseException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError parseExceptionHandler(ParseException parseException) {
    if (parseException.getMessage() != null) {
      return new VndError("400", parseException.getMessage());
    } else {
      return new VndError("400", parseException.getClass().getCanonicalName() + ": "
          + "no message available");
    }
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  VndError igeneralExceptionHandler(Exception exception) {
    logger.error("pech!", exception);
    if (exception.getMessage() != null) {
      return new VndError("500", exception.getClass().getCanonicalName() + ": "
          + exception.getMessage());
    } else {
      return new VndError("500", exception.getClass().getCanonicalName() + ": "
          + "no message available");
    }
  }

}
