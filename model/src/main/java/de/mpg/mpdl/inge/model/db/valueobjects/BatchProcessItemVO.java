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

import java.text.MessageFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class BatchProcessItemVO {

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
    SUCCESS("lblBatchProceesLog_success"),
    // INFO MESSAGES
    METADATA_LOST("lblBatchProceesLog_metadataLost"),
    // WARNING MESSAGES


    // ERROR MESSAGES
    INTERNAL_ERROR("lblBatchProceesLog_internalError");

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
