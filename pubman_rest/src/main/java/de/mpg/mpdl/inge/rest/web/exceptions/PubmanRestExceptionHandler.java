package de.mpg.mpdl.inge.rest.web.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = Logger.getLogger(PubmanRestExceptionHandler.class);


  private static void buildExceptionMessage(Throwable e, Map<String, Object> messageMap, HttpStatus status) {
    logger.error("Error in REST", e);
    if (status != null) {
      messageMap.put("timestamp", LocalDateTime.now());
      messageMap.put("status", status.value());
      messageMap.put("error", status.getReasonPhrase());
    }

    messageMap.put("exception", e.getClass().getCanonicalName());
    if (e.getMessage() != null) {
      messageMap.put("message", e.getMessage());
    }


    if (e.getCause() != null) {
      Map<String, Object> subMap = new LinkedHashMap<>();
      messageMap.put("cause", subMap);
      buildExceptionMessage(e.getCause(), subMap, null);
    }

  }

  private static ResponseEntity<Object> buildExceptionResponseEntity(Throwable e, HttpHeaders headers, HttpStatus status) {
    Map<String, Object> jsonException = new LinkedHashMap<>();
    buildExceptionMessage(e, jsonException, status);
    return new ResponseEntity<Object>(jsonException, headers, status);
  }

  @ExceptionHandler(value = NotFoundException.class)
  protected ResponseEntity<Object> handleNotFoundException(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationException(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = AuthorizationException.class)
  protected ResponseEntity<Object> handleAuthorizationException(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = IngeTechnicalException.class)
  protected ResponseEntity<Object> handleTechnicalxception(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = IngeApplicationException.class)
  protected ResponseEntity<Object> handleApplicationException(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = Exception.class)
  protected ResponseEntity<Object> handleAnyException(Exception e, WebRequest req) {
    return buildExceptionResponseEntity(e, null, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    return buildExceptionResponseEntity(ex, headers, status);
  }

  /*
  @ExceptionHandler(value = AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  VndErrors authc(AuthenticationException authcException) {
    logger.error("Error in REST service", authcException);
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
    logger.error("Error in REST service", iingeTechnicalException);
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
    logger.error("Error in REST service", iingeApplicationException);
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
    logger.error("Error in REST service", exception);
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
  
  */

}
