package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation")
@Tag(name = "Validation")
public class ValidationController {

  private static final String GENRE = "genre";

  private final ItemValidatingService itemValidatingService;
  private final UtilServiceBean utilServiceBean;

  public ValidationController(ItemValidatingService itemValidatingService, UtilServiceBean utilServiceBean) {
    this.itemValidatingService = itemValidatingService;
    this.utilServiceBean = utilServiceBean;
  }

  @RequestMapping(value = "/validateAbstract", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateAbstract( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody AbstractVO abstractVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateAbstract(abstractVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateAlternativeTitle", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateAlternativeTitle( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody AlternativeTitleVO alternativeTitleVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateAlternativeTitle(alternativeTitleVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateComponent", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateComponent( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody FileDbVO fileDbVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateComponent(fileDbVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateCreator", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateCreator( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody CreatorVO creatorVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateCreator(creatorVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateEvent", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateEvent( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody EventVO eventVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateEvent(eventVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateIdentifier", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateIdentifier( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody IdentifierVO identifierVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateIdentifier(identifierVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateLanguages", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateLanguages( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody List<String> languages) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateLanguages(languages);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateMdsPublication", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateMdsPublication( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody MdsPublicationVO mdsPublicationVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateMdsPublication(mdsPublicationVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateSource", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateSource( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(GENRE) MdsPublicationVO.Genre genre, //
      @RequestBody SourceVO sourceVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateSource(genre, sourceVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateSubject", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateSubject( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody SubjectVO subjectVO) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateSubject(subjectVO);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/validateTitle", method = RequestMethod.POST)
  public ResponseEntity<ValidationReportVO> validateTitle( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody String title) throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);

    ValidationReportVO validationReportVO = this.itemValidatingService.validateTitle(title);

    return new ResponseEntity<>(validationReportVO, HttpStatus.OK);
  }
}
