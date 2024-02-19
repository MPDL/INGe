package de.mpg.mpdl.inge.db.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class IdentifierProviderServiceImpl {

  @PersistenceContext
  EntityManager entityManager;

  public enum ID_PREFIX
  {
    CONTEXT("ctx"), FILES("file"), ITEM("item"), OU("ou"), USER("user");

  private final String prefix;

  ID_PREFIX(String prefix) {
      this.prefix = prefix;
    }

  public String getPrefix() {
      return this.prefix;
    }

  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public String getNewId(ID_PREFIX prefix) {

    Long res = (Long) this.entityManager.createNativeQuery("SELECT current_id FROM id_provider FOR UPDATE;").getSingleResult();
    this.entityManager.createNativeQuery("UPDATE id_provider SET current_id=current_id+1;").executeUpdate();

    return prefix.getPrefix() + "_" + res.intValue();
  }
}
