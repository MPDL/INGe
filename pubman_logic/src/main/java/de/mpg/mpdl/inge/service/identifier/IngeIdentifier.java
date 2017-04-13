package de.mpg.mpdl.inge.service.identifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "id_provider")
public class IngeIdentifier {

  @Id
  private String type;

  @Column(name = "current_id")
  private long identifier;

  public long getIdentifier() {
    return identifier;
  }

  public void setIdentifier(long identifier) {
    this.identifier = identifier;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
