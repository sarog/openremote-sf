package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.rpc.AuthorityService;
import org.openremote.modeler.domain.Authority;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

public class AuthorityServiceImpl extends BaseGWTSpringController implements AuthorityService {
   /**
    * 
    */
   private static final long serialVersionUID = 9014403953508227827L;

   public Authority getAuthoritication() {
      UserDetails auth = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Authority authority = new Authority();
      if(auth != null){
         authority.setUsername(auth.getUsername());
         GrantedAuthority[] authorities = auth.getAuthorities();
         for (int i=0; i < authorities.length; i++) {
            authority.addRole(authorities[i].getAuthority());
         }
      }
      return authority;
   }
}
