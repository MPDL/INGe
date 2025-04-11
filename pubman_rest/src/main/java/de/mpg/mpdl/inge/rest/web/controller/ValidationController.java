package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
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
  private final UtilServiceBean utilServiceBean;

  public ValidationController(ItemValidatingService itemValidatingService, UtilServiceBean utilServiceBean) {
    this.itemValidatingService = itemValidatingService;
    this.utilServiceBean = utilServiceBean;
  }

  @RequestMapping(value = "/validateEventTitleRequired", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateEventTitleRequired( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody EventVO eventVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateEventTitleRequired(eventVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }
}
