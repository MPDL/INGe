package de.mpg.mpdl.inge.db.repository;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IdentifierProviderServiceImpl {

  @PersistenceContext
  EntityManager entityManager;



  public enum ID_PREFIX {
    ITEM("item"), OU("ou"), CONTEXT("ctx"), USER("user");

    private String prefix;

    ID_PREFIX(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }

  }

  @Transactional
  public String getNewId(ID_PREFIX prefix) {

    BigInteger res =
        (BigInteger) entityManager.createNativeQuery("SELECT current_id FROM id_provider;")
            .getSingleResult();
    entityManager.createNativeQuery("UPDATE id_provider SET current_id=current_id+1;")
        .executeUpdate();

    return new StringBuilder(prefix.getPrefix()).append("_").append(res.intValue()).toString();

  }


}
