package de.mpg.mpdl.inge.rest.web.exceptions;

import java.text.ParseException;

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

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler(AaException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError aaExceptionHandler(AaException aaException) {
    return new VndError("error", aaException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(IngeServiceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError iingeServiceExceptionHandler(IngeServiceException iingeServicexception) {
    return new VndError("error", iingeServicexception.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(ItemInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError itemInvalidExceptionHandler(ItemInvalidException itemInvalidException) {
    return new VndError("error", itemInvalidException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(ParseException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError parseExceptionHandler(ParseException parseException) {
    return new VndError("error", parseException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndError igeneralExceptionHandler(Exception exception) {
    return new VndError("java.lang.Exception", exception.getMessage());
  }

}
