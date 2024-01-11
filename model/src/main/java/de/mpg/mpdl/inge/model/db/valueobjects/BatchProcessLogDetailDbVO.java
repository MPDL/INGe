package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_process_log_detail")
public class BatchProcessLogDetailDbVO implements Serializable {

  public enum State
  {
    INITIALIZED,
    RUNNING,
    SUCCESS,
    ERROR,
    EXCEPTION
  }

  public enum Message implements Messages
  {
    // SUCCESS MESSAGES
    SUCCESS("batch_ProcessLog_Success"),
    // ERROR MESSAGES
    STATE_WRONG("batch_ProcessLog_StateWrong"),
    FILES_METADATA_OLD_VALUE_NOT_EQUAL("batch_ProcessLog_FileMetadataOldValueNotEqual"),
    METADATA_CHANGE_VALUE_NOT_ALLOWED("batch_ProcessLog_MetadataChangeValueNotAllowed"),
    METADATA_NO_CHANGE_VALUE("batch_ProcessLog_MetadataNoChangeValue"),
    METADATA_NO_NEW_VALUE_SET("batch_ProcessLog_MetadataNoNewValueSet"),
    METADATA_NO_SOURCE_FOUND("batch_ProcessLog_MetadataNoSourceFound"),
    METADATA_CHANGE_VALUE_NOT_EQUAL("batch_ProcessLog_MetadataChangeValueNotEqual"),
    METADATA_CHANGE_VALUE_ORCID_NO_PERSON("batch_ProcessLog_MetadataChangeOrcidNoPerson"),
    VALIDATION_GLOBAL("batch_ProcessLog_ValidationGlobal"),
    VALIDATION_NO_SOURCE("batch_ProcessLog_ValidationNoSource"),
    // EXCEPTION ERROR MESSAGES
    ITEM_NOT_FOUND("batch_ProcessLog_ItemNotFoundError"),
    INTERNAL_ERROR("batch_ProcessLog_InternalError"),
    AUTHENTICATION_ERROR("batch_ProcessLog_AuthenticationError"),
    AUTHORIZATION_ERROR("lblBatchProceesLog_AuthorizationError");

  private String message;

  Message(String message) {
      this.message = message;
    }

  @Override
    public String getMessage() {
      return message;
    }}

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "batch_process_log_detail_id")
  private long batchProcessLogDetailId;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = BatchProcessLogHeaderDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "batchProcess")
  @JoinColumn(name = "batch_log_header_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO;

  @Column(name = "item_objectid")
  private String itemObjectId;

  @Column(name = "item_versionnumber")
  private Integer itemVersionnumber;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogDetailDbVO.State state;

  @Column(name = "message")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogDetailDbVO.Message message;

  @Column(name = "start_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime startDate;

  @Column(name = "end_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime endDate;

  public BatchProcessLogDetailDbVO() {}

  public BatchProcessLogDetailDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String itemObjectId, Integer itemVersionnumber,
      BatchProcessLogDetailDbVO.State state, LocalDateTime startDate) {
    this(batchProcessLogHeaderDbVO, itemObjectId, itemVersionnumber, state, (BatchProcessLogDetailDbVO.Message) null, startDate);
  }

  public BatchProcessLogDetailDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String itemObjectId, Integer itemVersionnumber,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message, LocalDateTime startDate) {
    this.batchProcessLogHeaderDbVO = batchProcessLogHeaderDbVO;
    this.itemObjectId = itemObjectId;
    this.itemVersionnumber = itemVersionnumber;
    this.state = state;
    this.message = message;
    this.startDate = startDate;
  }

  public long getBatchLogDetailId() {
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

  public LocalDateTime getEndDate() {
    return this.endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public BatchProcessLogHeaderDbVO getBatchProcessLogHeaderDbVO() {
    return this.batchProcessLogHeaderDbVO;
  }

  public String getItemObjectId() {
    return this.itemObjectId;
  }

  public Integer getItemVersionnumber() {
    return this.itemVersionnumber;
  }

  public LocalDateTime getStartDate() {
    return this.startDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchProcessLogDetailId, batchProcessLogHeaderDbVO, endDate, itemObjectId, itemVersionnumber, message, startDate,
        state);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchProcessLogDetailDbVO other = (BatchProcessLogDetailDbVO) obj;
    return batchProcessLogDetailId == other.batchProcessLogDetailId
        && Objects.equals(batchProcessLogHeaderDbVO, other.batchProcessLogHeaderDbVO) && Objects.equals(endDate, other.endDate)
        && Objects.equals(itemObjectId, other.itemObjectId) && Objects.equals(itemVersionnumber, other.itemVersionnumber)
        && message == other.message && Objects.equals(startDate, other.startDate) && state == other.state;
  }
}
