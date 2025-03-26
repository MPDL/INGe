package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation")
@Tag(name = "Validation")
public class ValidationController {

  private final ItemValidatingService itemValidatingService;

  public ValidationController(ItemValidatingService itemValidatingService) {
    this.itemValidatingService = itemValidatingService;
  }

  @RequestMapping(value = "/validateEventTitleRequired", method = RequestMethod.POST)
  public ResponseEntity<?> validateEventTitleRequired( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody EventVO eventVO) //
      throws ValidationException, ValidationServiceException {

    this.itemValidatingService.validateEventTitleRequired(eventVO);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
