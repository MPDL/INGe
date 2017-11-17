package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@Service
public class UserLoginRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void insertLogin(String loginName, String encodedPassword) throws IngeTechnicalException {
    int rows =
        entityManager
            .createNativeQuery("INSERT INTO user_login(loginname, password) VALUES (?1, ?2)")
            .setParameter(1, loginName).setParameter(2, encodedPassword).executeUpdate();
    if (rows != 1) {
      throw new IngeTechnicalException("Could not add login to table");
    }
  }

  @Transactional
  public void updateLogin(String loginName, String encodedPassword) throws IngeTechnicalException {
    int rows =
        entityManager.createNativeQuery("UPDATE user_login SET password=?2 WHERE loginname=?1")
            .setParameter(1, loginName).setParameter(2, encodedPassword).executeUpdate();

    if (rows != 1) {
      throw new IngeTechnicalException("Could not update login, maybe key " + loginName
          + " does not exist in table");
    }
  }

  @Transactional(readOnly = true)
  @Query(value = "SELECT password FROM user_login WHERE loginname=:loginname", nativeQuery = true)
  public String findPassword(String loginName) {

    List<?> result =
        entityManager.createNativeQuery("SELECT password FROM user_login WHERE loginname=?1")
            .setParameter(1, loginName).getResultList();
    if (!result.isEmpty()) {
      return (String) result.get(0);
    }

    return null;
    /*
     * return
     * (String)entityManager.createNativeQuery("SELECT password FROM user_login WHERE loginname=?1")
     * .setParameter(1, loginName).get
     */
  }
}
