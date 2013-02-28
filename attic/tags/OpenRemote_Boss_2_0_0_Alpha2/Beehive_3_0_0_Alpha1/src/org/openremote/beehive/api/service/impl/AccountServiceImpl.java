package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.User;

import com.sun.syndication.io.impl.Base64;

public class AccountServiceImpl extends BaseAbstractService<Code> implements AccountService {

   @Override
   public void save(Account a) {
      genericDAO.save(a);
   }

   @Override
   public User loadByUsername(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username);
   }

   @Override
   public boolean isHTTPBasicAuthorized(String credentials) {
      if (credentials != null && credentials.startsWith(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX)) {
         credentials = credentials.replaceAll(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX, "");
         credentials = Base64.decode(credentials);
         String[] arr = credentials.split(":");
         if (arr.length == 2) {
            String username = arr[0];
            String password = arr[1];
            User user = loadByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
               return true;
            }
         }
      }

      return false;
   }

}
