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
import java.text.MessageFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.xmltransforming.logging.Messages;

/**
 * Messages used in batch processing.
 * 
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
/**
 * @author walter
 *
 */

@Entity
@Table(name = "batch_process_log_item")
@JsonInclude(value = Include.NON_EMPTY)
public class BatchProcessItemVO implements Serializable {
  @Id
  @GeneratedValue
  public long objectId;
  public BatchProcessMessages batchProcessMessage;
  public BatchProcessMessagesTypes batchProcessMessageType;
  public ItemVersionVO itemVersionVO;

  public BatchProcessItemVO() {

  }

  public BatchProcessItemVO(ItemVersionVO itemVersionVO, BatchProcessMessages batchProcessMessage,
      BatchProcessMessagesTypes batchProcessMessageType) {
    this.itemVersionVO = itemVersionVO;
    this.batchProcessMessage = batchProcessMessage;
    this.batchProcessMessageType = batchProcessMessageType;
  }

  public enum BatchProcessMessagesTypes
  {
    INFO, ERROR, SUCCESS, WARNING;
  }

  public BatchProcessMessages getBatchProcessMessage() {
    return batchProcessMessage;
  }

  public void setBatchProcessMessage(BatchProcessMessages batchProcessMessage) {
    this.batchProcessMessage = batchProcessMessage;
  }

  public BatchProcessMessagesTypes getBatchProcessMessageType() {
    return batchProcessMessageType;
  }

  public void setBatchProcessMessageType(BatchProcessMessagesTypes batchProcessMessageType) {
    this.batchProcessMessageType = batchProcessMessageType;
  }

  public ItemVersionVO getItemVersionVO() {
    return itemVersionVO;
  }

  public void setItemVersionVO(ItemVersionVO itemVersionVO) {
    this.itemVersionVO = itemVersionVO;
  }

  public enum BatchProcessMessages implements Messages
  {

    // ///////////////////////////////////////////
    // MESSAGE DEFINITIONS

    // SUCCESS MESSAGES
    SUCCESS("batch_ProcessLog_Success"),
    // INFO MESSAGES
    
    // WARNING MESSAGES
    

    // ERROR MESSAGES
    STATE_WRONG("batch_ProcessLog_StateWrong"),
    FILES_METADATA_NOT_CHANGED("batch_ProcessLog_FileMetadataNotChanged"),
    FILES_METADATA_OLD_VALUE_NOT_EQUAL("batch_ProcessLog_FileMetadataOldValueNotEqual"),
    METADATA_CHANGE_VALUE_NOT_ALLOWED("batch_ProcessLog_MetadataChangeValueNotAllowed"),
    METADATA_NOT_CHANGED("batch_ProcessLog_MetadataNotChanged"),
    METADATA_NO_CHANGE_VALUE("batch_ProcessLog_MetadataNoChangeValue"),
    METADATA_NO_NEW_VALUE_SET("batch_ProcessLog_MetadataNoNewValueSet"),
    METADATA_NO_SOURCE_FOUND("batch_ProcessLog_MetadataNoSourceFound"),
    METADATA_CHANGE_VALUE_NOT_EQUAL("batch_ProcessLog_MetadataChangeValueNotEqual"),
    METADATA_CHANGE_VALUE_ORCID_NO_PERSON("batch_ProcessLog_MetadataChangeOrcidNoPerson"),
    METADATA_LOST("batch_ProcessLog_MetadataLost"),
    VALIDATION_GLOBAL("batch_ProcessLog_ValidationGlobal"),
    VALIDATION_NO_SOURCE("batch_ProcessLog_ValidationNoSource"),
    
    // EXCEPTION ERROR MESSAGES
    ITEM_NOT_FOUND("batch_ProcessLog_ItemNotFoundError"),
    INTERNAL_ERROR("batch_ProcessLog_InternalError"),
    AUTHENTICATION_ERROR("batch_ProcessLog_AuthenticationError"),
    AUTHORIZATION_ERROR("lblBatchProceesLog_AuthorizationError");

  /**
     * The message pattern. For syntax definition see {@link MessageFormat}.
     */
    private String message;

  /**
     * Creates a new instance with the given message template.
     * 
     * @param messageTemplate The message template
     */
    BatchProcessMessages(String messageTemplate) {
      this.message = messageTemplate;
    }

  @Override
  public String getMessage() {
    return message;
  }
}

}
