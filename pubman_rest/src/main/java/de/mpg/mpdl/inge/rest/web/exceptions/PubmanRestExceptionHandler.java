package de.mpg.mpdl.inge.rest.web.exceptions;

import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.service.exceptions.AaException;

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler(AaException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndErrors aaExceptionHandler(AaException aaException) {
    return new VndErrors("error", aaException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(IngeServiceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndErrors iingeServiceExceptionHandler(IngeServiceException iingeServicexception) {
    return new VndErrors("error", iingeServicexception.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(ItemInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndErrors itemInvalidExceptionHandler(ItemInvalidException itemInvalidException) {
    return new VndErrors("error", itemInvalidException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  VndErrors igeneralExceptionHandler(Exception exception) {
    return new VndErrors("java.lang.Exception", exception.getMessage());
  }
}
