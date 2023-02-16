package de.mpg.mpdl.inge.db.repository;

import java.math.BigInteger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IdentifierProviderServiceImpl {

  @PersistenceContext
  EntityManager entityManager;

  public enum ID_PREFIX
  {
    CONTEXT("ctx"), FILES("file"), ITEM("item"), OU("ou"), USER("user");

  private String prefix;

  ID_PREFIX(String prefix) {
      this.prefix = prefix;
    }

  public String getPrefix() {
      return prefix;
    }

  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public String getNewId(ID_PREFIX prefix) {

    Long res = (Long) entityManager.createNativeQuery("SELECT current_id FROM id_provider FOR UPDATE;").getSingleResult();
    entityManager.createNativeQuery("UPDATE id_provider SET current_id=current_id+1;").executeUpdate();

    return new StringBuilder(prefix.getPrefix()).append("_").append(res.intValue()).toString();
  }
}
