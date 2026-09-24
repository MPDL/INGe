package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "orcid_authorization")
public class OrcidAuthorizationDbVO {

  public enum ErrorCode {
    WARN_DIFFERENT_ORCID,
    WARN_DIFFERENT_NAME,
    ERROR_CONE_ID_ALREADY_EXITS
  }

  public enum Status {
    CREATED,
    EMAIL_SENT_LINK,
    CODE_RECEIVED,
    TOKEN_RECEIVED,
    TOKEN_RECEIVED_WITH_WARNING,
    EMAIL_SENT_BIBO,
    CANCELED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orcid_id_gen")
  @SequenceGenerator(name = "orcid_id_gen", sequenceName = "orcid_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;
  @Column(name = "user_id")
  private String userIdBibo;
  @Column(name = "cone_id_author")
  private String coneIdAuthor;
  @Column(name = "orcid_author")
  private String orcidAuthor;
  @Column(name = "name_author")
  private String nameAuthor;
  @Column(name = "scope_requested")
  private String scopeRequested;
  @Column(name = "secret")
  private String secret;
  @Column(name = "email_author")
  private String emailAuthor;
  @Column(name = "email_bibo")
  private String emailBibo;
  @Column(name = "email_link", columnDefinition = "TEXT")
  private String emailLink;
  @Column(name = "redirect_uri")
  private String redirectUri;
  @Column(name = "date_created", columnDefinition = "TIMESTAMP")
  private Date dateCreated;
  @Column(name = "date_email_sent_link", columnDefinition = "TIMESTAMP")
  private Date dateEmailSentLink;
  @Column(name = "date_code_received", columnDefinition = "TIMESTAMP")
  private Date dateCodeReceived;
  @Column(name = "code_received")
  private String codeReceived;
  @Column(name = "date_token_received", columnDefinition = "TIMESTAMP")
  private Date dateTokenReceived;
  @Column(name = "access_token")
  private String accessToken;
  @Column(name = "refresh_token")
  private String refreshToken;
  @Column(name = "scope")
  private String scope;
  @Column(name = "name")
  private String name;
  @Column(name = "orcid")
  private String orcid;
  @Column(name = "token_type")
  private String tokenType;
  @Column(name = "expires_in")
  private String expiresIn;
  @Column(name = "verified")
  private Boolean verified;
  @Column(name = "date_email_sent_bibo", columnDefinition = "TIMESTAMP")
  private Date dateEmailSentBibo;
  @Column(name = "error_codes")
  private String errorCodes;
  @Column(name = "canceled")
  private Boolean canceled;

  public OrcidAuthorizationDbVO(String userIdBibo, String coneIdAuthor, String orcidAuthor, String nameAuthor, String secret, String scopeRequested,
      String emailLink, String emailBibo, String emailAuthor, String redirectUri) {
    this.status = Status.CREATED;
    this.dateCreated = new Date();
    this.userIdBibo = userIdBibo;
    this.coneIdAuthor = coneIdAuthor;
    this.orcidAuthor = orcidAuthor;
    this.nameAuthor = nameAuthor;
    this.secret = secret;
    this.scopeRequested = scopeRequested;
    this.emailLink = emailLink;
    this.emailBibo = emailBibo;
    this.emailAuthor = emailAuthor;
    this.redirectUri = redirectUri;
    this.verified = false;
  }

  public OrcidAuthorizationDbVO() {
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Boolean getCanceled() { return canceled; }

  public void setCanceled(Boolean canceled) { this.canceled = canceled; }

  public String getCodeReceived() {
    return codeReceived;
  }

  public void setCodeReceived(String codeReceived) {
    this.codeReceived = codeReceived;
  }

  public String getConeIdAuthor() {
    return coneIdAuthor;
  }

  public void setConeIdAuthor(String coneIdAuthor) {
    this.coneIdAuthor = coneIdAuthor;
  }

  public Date getDateCodeReceived() {
    return dateCodeReceived;
  }

  public void setDateCodeReceived(Date date_codeReceived) {
    this.dateCodeReceived = date_codeReceived;
  }

  public Date getDateEmailSentBibo() {
    return dateEmailSentBibo;
  }

  public void setDateEmailSentBibo(Date dateEmailSentBibo) {
    this.dateEmailSentBibo = dateEmailSentBibo;
  }

  public Date getDateEmailSentLink() {
    return dateEmailSentLink;
  }

  public void setDateEmailSentLink(Date dateEmailSentLink) {
    this.dateEmailSentLink = dateEmailSentLink;
  }

  public Date getDateTokenReceived() {
    return dateTokenReceived;
  }

  public void setDateTokenReceived(Date dateTokenReceived) {
    this.dateTokenReceived = dateTokenReceived;
  }

  public String getEmailAuthor() {
    return emailAuthor;
  }

  public void setEmailAuthor(String emailAuthor) {
    this.emailAuthor = emailAuthor;
  }

  public String getEmailBibo() {
    return emailBibo;
  }

  public void setEmailBibo(String emailBibo) {
    this.emailBibo = emailBibo;
  }

  public String getEmailLink() {
    return emailLink;
  }

  public void setEmailLink(String emailLink) {
    this.emailLink = emailLink;
  }

  public String getErrorCodes() {
    return errorCodes;
  }

  public void setErrorCodes(String errorCode) {
    this.errorCodes = errorCode;
  }

  public String getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(String expiresIn) {
    this.expiresIn = expiresIn;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameAuthor() {
    return nameAuthor;
  }

  public void setNameAuthor(String nameAuthor) {
    this.nameAuthor = nameAuthor;
  }

  public String getOrcid() {
    return orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

  public String getOrcidAuthor() {
    return orcidAuthor;
  }

  public void setOrcidAuthor(String orcidAuthor) {
    this.orcidAuthor = orcidAuthor;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getScopeRequested() {
    return scopeRequested;
  }

  public void setScopeRequested(String scopeRequested) {
    this.scopeRequested = scopeRequested;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getUserIdBibo() {
    return userIdBibo;
  }

  public void setUserIdBibo(String userIdBibo) {
    this.userIdBibo = userIdBibo;
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

}
