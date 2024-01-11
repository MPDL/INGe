package de.mpg.mpdl.inge.db.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserLoginRepository {

  private static final Logger logger = Logger.getLogger(UserLoginRepository.class);

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void insertLogin(String loginName, String encodedPassword, LocalDate lastPasswordChange, boolean passwordChangeFlag)
      throws IngeTechnicalException {
    int rows = entityManager
        .createNativeQuery(
            "INSERT INTO user_login(loginname, password, last_password_change, password_change_flag) VALUES (?1, ?2, ?3, ?4)")
        .setParameter(1, loginName).setParameter(2, encodedPassword).setParameter(3, lastPasswordChange).setParameter(4, passwordChangeFlag)
        .executeUpdate();
    if (rows != 1) {
      throw new IngeTechnicalException("Could not add login to table");
    }
  }

  @Transactional
  public void removeLogin(String loginName) throws IngeTechnicalException {
    entityManager.createNativeQuery("DELETE FROM user_login WHERE loginname=?1").setParameter(1, loginName).executeUpdate();
  }

  @Transactional
  public void updateLogin(String loginName, String encodedPassword, LocalDate lastPasswordChange, boolean passwordChangeFlag)
      throws IngeTechnicalException {
    logger.info("Connection: " + entityManager.getDelegate());
    int rows = entityManager
        .createNativeQuery("UPDATE user_login SET password=?2, last_password_change=?3, password_change_flag=?4 WHERE loginname=?1")
        .setParameter(1, loginName).setParameter(2, encodedPassword).setParameter(3, lastPasswordChange).setParameter(4, passwordChangeFlag)
        .executeUpdate();

    if (rows != 1) {
      throw new IngeTechnicalException("Could not update login, maybe key " + loginName + " does not exist in table");
    }
  }

  @Transactional(readOnly = true)
  @Query(value = "SELECT password FROM user_login WHERE loginname=:loginname", nativeQuery = true)
  public String findPassword(String loginName) {

    List<?> result =
        entityManager.createNativeQuery("SELECT password FROM user_login WHERE loginname=?1").setParameter(1, loginName).getResultList();
    if (!result.isEmpty()) {
      return (String) result.get(0);
    }

    return null;
  }



  @Transactional(readOnly = true)
  @Query(value = "SELECT last_password_change FROM user_login WHERE loginname=:loginname", nativeQuery = true)
  public LocalDate findLastPasswordChange(String loginName) {

    List<?> result = entityManager.createNativeQuery("SELECT last_password_change FROM user_login WHERE loginname=?1")
        .setParameter(1, loginName).getResultList();
    if (!result.isEmpty()) {
      return (LocalDate) result.get(0);
    }

    return null;
  }

  @Transactional(readOnly = true)
  @Query(value = "SELECT password_change_flag FROM user_login WHERE loginname=:loginname", nativeQuery = true)
  public boolean findPasswordChangeFlag(String loginName) {

    List<?> result = entityManager.createNativeQuery("SELECT password_change_flag FROM user_login WHERE loginname=?1")
        .setParameter(1, loginName).getResultList();
    if (!result.isEmpty()) {
      return (Boolean) result.get(0);
    }

    return false;
  }
}
