package de.mpg.mpdl.inge.pubman.web.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.model.util.BatchProcessLogUtil;

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
  public List<BatchProcessLogUtil> batchProcessLogUtil;

  public enum Status
  {
    FINISHED, NOT_STARTED, RUNNING
  }

  private Status batchStatus;


  public BatchProcessLogBean () {
    this.batchStatus = Status.NOT_STARTED;
    this.batchProcessLogUtil = new ArrayList<BatchProcessLogUtil>();
  }

  public Status getBatchStatus() {
    return batchStatus;
  }

  public List<BatchProcessLogUtil> getProcessLog() {
    return this.batchProcessLogUtil;
  }

  public void setBatchStatus(Status batchStatus) {
    this.batchStatus = batchStatus;
  }

  public void setProcessLog(List<BatchProcessLogUtil> batchProcessLogUtil) {
    this.batchProcessLogUtil = batchProcessLogUtil;
  }

}
