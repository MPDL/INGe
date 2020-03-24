package de.mpg.mpdl.inge.model.db.valueobjects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "batch_log")
//@TypeDef(name = "StringListJsonUserType", typeClass = StringListJsonUserType.class)
public class BatchProcessLogDbVO {

  @Id
  @Column(name = "user_account_id")
  private String userId;

  //  @OneToOne
  //  @PrimaryKeyJoinColumn(name = "user_account_id", referencedColumnName = "objectId")
  //  private AccountUserDbVO accountUser;

  @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinTable(name = "batch_join")
  //@LazyCollection(LazyCollectionOption.FALSE)
  //  @JoinTable(name = "batch_join", joinColumns = @JoinColumn(name = "user_account_id"), inverseJoinColumns = @JoinColumn(name = "objectId"))
  //    @JoinColumn(name = "batch_process_log_item_object_id")
  //  @ElementCollection
  //  @CollectionTable(name = "batch_process_log_item", joinColumns = @JoinColumn(name = "object_id"))
  //  @AttributeOverrides({@AttributeOverride(name = "batchProcessMessage", column = @Column(name = "batchProcessMessage")),
  //      @AttributeOverride(name = "batchProcessMessageType", column = @Column(name = "batchProcessMessageType"))})
  public List<BatchProcessItemVO> batchProcessLogItemList;


  public BatchProcessLogDbVO() {
    this.batchProcessLogItemList = new ArrayList<BatchProcessItemVO>();
  }

  public BatchProcessLogDbVO(AccountUserDbVO accountUser) {
    this.batchProcessLogItemList = new ArrayList<BatchProcessItemVO>();
    //    this.accountUser = accountUser;
    this.userId = accountUser.getObjectId();
  }

  public List<BatchProcessItemVO> getBatchProcessLogItemList() {
    return this.batchProcessLogItemList;
  }

  public void setBatchProcessLogItemList(List<BatchProcessItemVO> batchProcessLogItemList) {
    this.batchProcessLogItemList = batchProcessLogItemList;
  }
}
