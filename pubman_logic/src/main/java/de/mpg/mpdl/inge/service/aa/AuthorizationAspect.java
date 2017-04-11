package de.mpg.mpdl.inge.service.aa;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.exceptions.AaException;

@Aspect
@Service
public class AuthorizationAspect {

  @Before("execution(public * de.mpg.mpdl.inge.service.pubman*.*(..))")
  private void checkAa() {

    System.out.println("check AA!!!!!!!!!!!!!!!!!!!!");
  }
  
  
private void roleInUserVO(PredefinedRoles role, String contextId, AccountUserVO user) throws AaException {
    
    for(GrantVO grant : user.getGrants())
    {
      if (grant.getRole().equals(role.frameworkValue())){
        return;
      }
    }
    
    throw new AaException("No sufficient priviliges.");
    
  }
  
  
  private void contextIdInUserVO(PredefinedRoles role, String contextId, AccountUserVO user) throws AaException {
    
    for(GrantVO grant : user.getGrants())
    {
      if (grant.getRole().equals(role.frameworkValue()) && grant.getGrantedTo()!=null && grant.getGrantedTo().equals(contextId)){
        return;
      }
    }
    
    throw new AaException("No sufficient priviliges.");
    
  }

}
