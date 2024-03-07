package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.xmltransforming.logging.Messages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_process_log_detail")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class BatchProcessLogDetailDbVO implements Serializable {

  public enum State
  {
    INITIALIZED,
    RUNNING,
    SUCCESS,
    ERROR
  }

  public enum Message implements Messages
  {
    // SUCCESS MESSAGE
    SUCCESS("batch_ProcessLog_Success"),

    // ERROR MESSAGES
    AUTHENTICATION_ERROR("batch_ProcessLog_AuthenticationError"),
    AUTHORIZATION_ERROR("lblBatchProceesLog_AuthorizationError"),
    CONTEXT_NOT_FOUND("batch_ProcessLog_ContextNotFoundError"),
    FILES_METADATA_OLD_VALUE_NOT_EQUAL("batch_ProcessLog_FileMetadataOldValueNotEqual"),
    INTERNAL_ERROR("batch_ProcessLog_InternalError"),
    ITEM_NOT_FOUND("batch_ProcessLog_ItemNotFoundError"),
    METADATA_CHANGE_VALUE_NOT_ALLOWED("batch_ProcessLog_MetadataChangeValueNotAllowed"),
    METADATA_CHANGE_VALUE_NOT_EQUAL("batch_ProcessLog_MetadataChangeValueNotEqual"),
    METADATA_CHANGE_VALUE_ORCID_NO_PERSON("batch_ProcessLog_MetadataChangeOrcidNoPerson"),
    METADATA_NO_CHANGE_VALUE("batch_ProcessLog_MetadataNoChangeValue"),
    METADATA_NO_NEW_VALUE_SET("batch_ProcessLog_MetadataNoNewValueSet"),
    METADATA_NO_SOURCE_FOUND("batch_ProcessLog_MetadataNoSourceFound"),
    STATE_WRONG("batch_ProcessLog_StateWrong"),
    VALIDATION_GLOBAL("batch_ProcessLog_ValidationGlobal"),
    VALIDATION_INVALID_ORCID("batch_ProcessLog_ValidationInvalidOrcid"),
    VALIDATION_IP_RANGE_NOT_PROVIDED("batch_ProcessLog_IpRangeNotProvided"),
    VALIDATION_NO_SOURCE("batch_ProcessLog_ValidationNoSource");

  private final String message;

  Message(String message) {
      this.message = message;
    }

  @Override
    public String getMessage() {
      return this.message;
    }}

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_process_log_id_gen")
  @SequenceGenerator(name = "batch_process_log_id_gen", sequenceName = "batch_process_log_id_seq", allocationSize = 1)
  @Column(name = "batch_process_log_detail_id", nullable = false)
  private long batchProcessLogDetailId;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = BatchProcessLogHeaderDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "batchProcess")
  @JoinColumn(name = "batch_process_log_header_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO;

  @Size(max = 255)
  @NotNull
  @Column(name = "item_objectid", nullable = false)
  private String itemObjectId;

  @Column(name = "item_versionnumber")
  private Integer itemVersionnumber;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  private BatchProcessLogDetailDbVO.State state;

  @Column(name = "message")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogDetailDbVO.Message message;

  @NotNull
  @Column(name = "start_date", columnDefinition = "TIMESTAMP", nullable = false)
  private Date startDate;

  @Column(name = "end_date", columnDefinition = "TIMESTAMP")
  private Date endDate;

  public BatchProcessLogDetailDbVO() {}

  public BatchProcessLogDetailDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String itemObjectId, Integer itemVersionnumber,
      BatchProcessLogDetailDbVO.State state, Date startDate) {
    this(batchProcessLogHeaderDbVO, itemObjectId, itemVersionnumber, state, null, startDate);
  }

  public BatchProcessLogDetailDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String itemObjectId, Integer itemVersionnumber,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message, Date startDate) {
    this.batchProcessLogHeaderDbVO = batchProcessLogHeaderDbVO;
    this.itemObjectId = itemObjectId;
    this.itemVersionnumber = itemVersionnumber;
    this.state = state;
    this.message = message;
    this.startDate = startDate;
  }

  public long getBatchProcessLogDetailId() {
    return this.batchProcessLogDetailId;
  }

  public void setBatchLogDetailId(long batchLogDetailId) {
    this.batchProcessLogDetailId = batchLogDetailId;
  }

  public BatchProcessLogDetailDbVO.State getState() {
    return this.state;
  }

  public void setState(BatchProcessLogDetailDbVO.State state) {
    this.state = state;
  }

  public BatchProcessLogDetailDbVO.Message getMessage() {
    return this.message;
  }

  public void setMessage(BatchProcessLogDetailDbVO.Message message) {
    this.message = message;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public BatchProcessLogHeaderDbVO getBatchProcessLogHeaderDbVO() {
    return this.batchProcessLogHeaderDbVO;
  }

  public void setBatchProcessLogHeaderDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO) {
    this.batchProcessLogHeaderDbVO = batchProcessLogHeaderDbVO;
  }

  public String getItemObjectId() {
    return this.itemObjectId;
  }

  public Integer getItemVersionnumber() {
    return this.itemVersionnumber;
  }

  public Date getStartDate() {
    return this.startDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.batchProcessLogDetailId, this.batchProcessLogHeaderDbVO, this.endDate, this.itemObjectId,
        this.itemVersionnumber, this.message, this.startDate, this.state);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (null == obj)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchProcessLogDetailDbVO other = (BatchProcessLogDetailDbVO) obj;
    return this.batchProcessLogDetailId == other.batchProcessLogDetailId
        && Objects.equals(this.batchProcessLogHeaderDbVO, other.batchProcessLogHeaderDbVO) && Objects.equals(this.endDate, other.endDate)
        && Objects.equals(this.itemObjectId, other.itemObjectId) && Objects.equals(this.itemVersionnumber, other.itemVersionnumber)
        && this.message == other.message && Objects.equals(this.startDate, other.startDate) && this.state == other.state;
  }
}
