/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.xmltransforming.logging.Messages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_log_detail")
@JsonInclude(value = Include.NON_EMPTY)
public class BatchProcessLogDetailDbVO implements Serializable {

  public enum State {
    INITIALIZED,
    RUNNING,
    SUCCESS,
    ERROR,
    EXCEPTION
  }

  public enum Message implements Messages {
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
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "batch_log_detail_id")
  private long batchLogDetailId;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = BatchProcessLogHeaderDbVO.class)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO;

  @OneToOne(fetch = FetchType.EAGER, targetEntity = ItemVersionVO.class)
  private ItemVersionVO itemVersionVO;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogDetailDbVO.State state;

  @Column(name = "message")
  private BatchProcessLogDetailDbVO.Message logMessage;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "end_date")
  private Date endDate;

  public BatchProcessLogDetailDbVO() {}

  public BatchProcessLogDetailDbVO(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO.State state, Date startDate) {
    this.batchProcessLogHeaderDbVO = batchProcessLogHeaderDbVO;
    this.itemVersionVO = itemVersionVO;
    this.state = state;
    this.startDate = startDate;
  }

  public long getBatchLogDetailId() {
    return this.batchLogDetailId;
  }

  public void setBatchLogDetailId(long batchLogDetailId) {
    this.batchLogDetailId = batchLogDetailId;
  }

  public BatchProcessLogDetailDbVO.State getState() {
    return this.state;
  }

  public void setState(BatchProcessLogDetailDbVO.State state) {
    this.state = state;
  }

  public BatchProcessLogDetailDbVO.Message getLogMessage() {
    return this.logMessage;
  }

  public void setLogMessage(BatchProcessLogDetailDbVO.Message logMessage) {
    this.logMessage = logMessage;
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

  public ItemVersionVO getItemVersionVO() {
    return this.itemVersionVO;
  }

  public Date getStartDate() {
    return this.startDate;
  }
}
