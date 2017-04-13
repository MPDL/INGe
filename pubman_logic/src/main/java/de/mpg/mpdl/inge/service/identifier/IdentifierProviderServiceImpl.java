package de.mpg.mpdl.inge.service.identifier;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IdentifierProviderServiceImpl {

  @PersistenceContext
  EntityManager entityManager;


  @Transactional
  public String getNewId() {
    IngeIdentifier id =
        entityManager
            .createQuery("SELECT i FROM IngeIdentifier i WHERE i.type=?1", IngeIdentifier.class)
            .setParameter(1, "pure").getSingleResult();
    entityManager
        .createNativeQuery("UPDATE id_provider SET current_id=current_id+1 WHERE type = ?1")
        .setParameter(1, id.getType()).executeUpdate();
    return new StringBuilder("pure_").append(id.getIdentifier()).toString();

  }


}
