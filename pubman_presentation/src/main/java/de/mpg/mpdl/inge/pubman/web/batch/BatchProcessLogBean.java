package de.mpg.mpdl.inge.pubman.web.batch;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessItemVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * Bean for documenting the batch process
 *
 * @author walter
 *
 */

@ManagedBean(name = "BatchProcessLogBean")
@SessionScoped
@SuppressWarnings("serial")
public class BatchProcessLogBean extends FacesBean {
  private List<BatchProcessItemVO> batchProcessLogItemList;

  public enum Status
  {
    FINISHED, NOT_STARTED, RUNNING
  }

  private Status batchStatus;


  public BatchProcessLogBean () {
    this.batchStatus = Status.NOT_STARTED;
    this.batchProcessLogItemList = new ArrayList<>();
  }

  public Status getBatchStatus() {
    return batchStatus;
  }

  public List<BatchProcessItemVO> getBatchProcessLogItemList() {
    return this.batchProcessLogItemList;
  }

  public void setBatchStatus(Status batchStatus) {
    this.batchStatus = batchStatus;
  }

  public void setBatchProcessLogItemList(List<BatchProcessItemVO> batchProcessLogUtil) {
    this.batchProcessLogItemList = batchProcessLogUtil;
  }

}
