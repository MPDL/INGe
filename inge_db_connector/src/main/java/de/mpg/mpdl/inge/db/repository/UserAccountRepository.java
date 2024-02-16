package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;

public interface UserAccountRepository extends JpaRepository<AccountUserDbVO, String> {

  /*
   * @Query(value= "INSERT INTO user_login(loginname, password) VALUES (:loginname, :password)",
   * nativeQuery=true) public void insertLogin(@Param("loginname") String loginName,
   *
   * @Param("password") String encodedPassword);
   *
   * @Query(value= "UPDATE user_login SET password=:password WHERE loginname=:loginname",
   * nativeQuery=true) public void updateLogin(@Param("loginname") String loginName,
   *
   * @Param("password") String encodedPassword);
   *
   * @Query(value= "SELECT password FROM user_login WHERE loginname=:loginname", nativeQuery=true)
   * public String findPassword(@Param("loginname") String loginName);
   */
  AccountUserDbVO findByLoginname(@Param("loginname") String loginname);
}
