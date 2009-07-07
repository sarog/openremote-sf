package org.openremote.modeler.server;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

public class AuthorityServiceImpl extends BaseGWTSpringController implements AuthorityService {
   /**
    * 
    */
   private static final long serialVersionUID = 9014403953508227827L;

   public Authority getAuthoritication() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      Authority authority = new Authority();
      if (auth != null) {
         authority.setUsername(auth.getName());
         GrantedAuthority[] authorities = auth.getAuthorities();
         for (int i = 0; i < authorities.length; i++) {
            authority.addRole(authorities[i].getAuthority());
         }
      } else {
         authority = null;
      }
      return authority;
   }
}
