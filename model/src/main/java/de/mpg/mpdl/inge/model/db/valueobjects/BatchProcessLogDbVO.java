package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name = "batch_log")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "batchLog")
public class BatchProcessLogDbVO implements Serializable {

  @Id
  @Column(name = "user_account_id")
  private String userId;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "batch_join")
  public List<BatchProcessItemVO> batchProcessLogItemList;


  public BatchProcessLogDbVO() {
    this.batchProcessLogItemList = new ArrayList<BatchProcessItemVO>();
  }

  public BatchProcessLogDbVO(AccountUserDbVO accountUser) {
    this.batchProcessLogItemList = new ArrayList<BatchProcessItemVO>();
    this.userId = accountUser.getObjectId();
  }

  public List<BatchProcessItemVO> getBatchProcessLogItemList() {
    return this.batchProcessLogItemList;
  }

  public void setBatchProcessLogItemList(List<BatchProcessItemVO> batchProcessLogItemList) {
    this.batchProcessLogItemList = batchProcessLogItemList;
  }
}
