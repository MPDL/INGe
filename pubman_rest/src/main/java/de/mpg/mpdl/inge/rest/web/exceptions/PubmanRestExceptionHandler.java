package de.mpg.mpdl.inge.rest.web.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  private final static Logger logger = LogManager.getLogger(PubmanRestExceptionHandler.class);

  @ExceptionHandler(value = AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  VndErrors authc(AuthenticationException authcException) {
    if (authcException.getMessage() != null) {
      VndErrors authcErrors = new VndErrors("401", authcException.getMessage());
      authcErrors = addTheCause(authcErrors, authcException);
      return authcErrors;
    } else {
      return new VndErrors("401", authcException.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  @ExceptionHandler(value = AuthorizationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  VndErrors authz(AuthorizationException authzException) {
    if (authzException.getMessage() != null) {
      VndErrors authzErrors = new VndErrors("403", authzException.getMessage());
      authzErrors = addTheCause(authzErrors, authzException);
      return authzErrors;
    } else {
      return new VndErrors("403", authzException.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  @ExceptionHandler(value = IngeTechnicalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  VndErrors ingeTech(IngeTechnicalException iingeTechnicalException) {
    if (iingeTechnicalException.getMessage() != null) {
      VndErrors techErrors = new VndErrors("500", iingeTechnicalException.getMessage());
      techErrors = addTheCause(techErrors, iingeTechnicalException);
      return techErrors;
    } else {
      return new VndErrors("500", iingeTechnicalException.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  @ExceptionHandler(value = IngeApplicationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  VndErrors ingeApp(IngeApplicationException iingeApplicationException) {
    if (iingeApplicationException.getMessage() != null) {
      VndErrors appErrors = new VndErrors("400", iingeApplicationException.getMessage());
      appErrors = addTheCause(appErrors, iingeApplicationException);
      return appErrors;
    } else {
      return new VndErrors("400", iingeApplicationException.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  @ExceptionHandler(value = ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  VndErrors validation(ValidationException validationException) {
    if (validationException.getMessage() != null) {
      return new VndErrors("400", validationException.getMessage());
    } else {
      return new VndErrors("400", validationException.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  VndErrors genreic(Exception exception) {
    logger.error("pech!", exception);
    if (exception.getMessage() != null) {
      VndErrors genericErrors = new VndErrors("500", exception.getClass().getCanonicalName() + ": " + exception.getMessage());
      genericErrors = addTheCause(genericErrors, exception);
      return genericErrors;
    } else {
      return new VndErrors("500", exception.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  private VndErrors addTheCause(VndErrors errors, Throwable throwable) {
    Throwable cause = throwable;
    while (cause.getCause() != null) {
      cause = cause.getCause();
      if (cause.getMessage() != null) {
        errors.add(new VndError(cause.getClass().getCanonicalName(), cause.getMessage()));
      } else {
        errors.add(new VndError(cause.getClass().getCanonicalName(), "no message available!"));
      }
    }
    return errors;
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    VndError errorMessage;
    if (mostSpecificCause != null) {
      String exceptionName = mostSpecificCause.getClass().getName();
      String message = mostSpecificCause.getMessage();
      errorMessage = new VndError(exceptionName, message);
    } else {
      errorMessage = new VndError("cause?", ex.getMessage());
    }
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
    List<VndError> errors = new ArrayList<VndError>(fieldErrors.size() + globalErrors.size());
    VndError error;
    for (FieldError fieldError : fieldErrors) {
      error = new VndError("invalid!!!", fieldError.getField() + ", " + fieldError.getDefaultMessage());
      errors.add(error);
    }
    for (ObjectError objectError : globalErrors) {
      error = new VndError("invalid!!!", objectError.getObjectName() + ", " + objectError.getDefaultMessage());
      errors.add(error);
    }
    VndErrors errorMessage = new VndErrors(errors);
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

}
