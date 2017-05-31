package de.mpg.mpdl.inge.db.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserLoginRepository {

  @PersistenceContext
  private EntityManager entityManager;



  @Transactional
  public void insertLogin(String loginName, String encodedPassword) {
    entityManager.createNativeQuery("INSERT INTO user_login(loginname, password) VALUES (?1, ?2)")
        .setParameter(1, loginName).setParameter(2, encodedPassword).executeUpdate();
  }

  @Transactional
  public void updateLogin(String loginName, String encodedPassword) {
    entityManager.createNativeQuery("UPDATE user_login SET password=?2 WHERE loginname=?1")
        .setParameter(1, loginName).setParameter(2, encodedPassword).executeUpdate();
  }

  @Transactional(readOnly = true)
  @Query(value = "SELECT password FROM user_login WHERE loginname=:loginname", nativeQuery = true)
  public String findPassword(String loginName) {

    List result =
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
