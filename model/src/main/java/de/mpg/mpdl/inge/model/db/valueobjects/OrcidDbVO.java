package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "orcid", indexes = {@Index(name = "orcid_idx_cone_id_author", columnList = "cone_id_author"),
    @Index(name = "orcid_idx_item_id", columnList = "item_id")})
public class OrcidDbVO {

  public enum Status
  {
    CREATE,
    UPDATE
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "cone_id_author", referencedColumnName = "cone_id_author")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private OrcidAuthorizationDbVO orcidAuthorizationDbVO;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orcid_id_gen")
  @SequenceGenerator(name = "orcid_id_gen", sequenceName = "orcid_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;
  @Column(name = "status")
  private String status;
  @Column(name = "item_id")
  private String itemId;
  @Column(name = "put_code")
  private String putCode;
  @Column(name = "response_code")
  private String responseCode;
  @Column(name = "developer_message")
  private String developerMessage;
  @Column(name = "date", columnDefinition = "TIMESTAMP")
  private Date date;

  public OrcidDbVO(String status, String itemId) {
    this.status = status;
    this.itemId = itemId;
    this.date = new Date();
  }

  public OrcidDbVO() {}

  public Integer getId() {
    return this.id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public String getConeIdAuthor() {
    return this.orcidAuthorizationDbVO != null ? this.orcidAuthorizationDbVO.getConeIdAuthor() : null;
  }

  public String getItemId() {
    return this.itemId;
  }

  public void setItemId(final String itemId) {
    this.itemId = itemId;
  }

  public String getPutCode() {
    return this.putCode;
  }

  public void setPutCode(final String putCode) {
    this.putCode = putCode;
  }

  public String getResponseCode() {
    return this.responseCode;
  }

  public void setResponseCode(final String responseCode) {
    this.responseCode = responseCode;
  }

  public String getDeveloperMessage() {
    return this.developerMessage;
  }

  public void setDeveloperMessage(final String developerMessage) {
    this.developerMessage = developerMessage;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(final Date date) {
    this.date = date;
  }
}
