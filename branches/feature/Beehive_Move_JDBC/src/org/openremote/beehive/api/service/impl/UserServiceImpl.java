package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.api.service.UserService;
import org.openremote.beehive.domain.User;

public class UserServiceImpl extends BaseAbstractService<User> implements UserService {

   public User saveUser(User user) {
      genericDAO.save(user.getAccount());
      genericDAO.save(user);
      return user;
   }

   public User getUserById(long id) {
      return genericDAO.getById(User.class, id);
   }

   public User getUserByUsername(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username);
   }
   
   public void updateUser(User user) {
      genericDAO.update(user);
   }
   
   public void deleteUserById(long id) {
      genericDAO.delete(getUserById(id));
   }

}
