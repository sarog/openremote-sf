/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.modeler.server;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityRPCService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

public class AuthorityController extends BaseGWTSpringController implements AuthorityRPCService {
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
