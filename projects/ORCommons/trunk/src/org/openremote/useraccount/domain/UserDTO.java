/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.useraccount.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus
 */
public class UserDTO implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 4157203580282782479L;

  /** The oid. */
  private Long oid;
  
  private String username;

  private String password;

  private String email;
  
  private boolean valid;

  private transient Timestamp registerTime;

  /** The account containing all business entities. */
  private AccountDTO account;

  private List<RoleDTO> roles;

  private String token;

  /**
   * Instantiates a new user.
   */
  public UserDTO()
  {
    account = new AccountDTO();
    roles = new ArrayList<RoleDTO>();
  }

  /**
   * Gets the username.
   * 
   * @return the username
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * Sets the username.
   * 
   * @param username
   *          the new username
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  /**
   * Gets the password.
   * 
   * @return the password
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Sets the password.
   * 
   * @param password
   *          the new password
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * Gets the account.
   * 
   * @return the account
   */
  public AccountDTO getAccount()
  {
    return account;
  }

  /**
   * Sets the account.
   * 
   * @param account
   *          the new account
   */
  public void setAccount(AccountDTO account)
  {
    this.account = account;
  }

  /**
   * Gets the roles.
   * 
   * @return the roles
   */
  public List<RoleDTO> getRoles()
  {
    return roles;
  }

  /**
   * Sets the roles.
   * 
   * @param roles
   *          the new roles
   */
  public void setRoles(List<RoleDTO> roles)
  {
    this.roles = roles;
  }

  /**
   * Adds the role.
   * 
   * @param role
   *          the role
   */
  public void addRole(RoleDTO role)
  {
    roles.add(role);
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public boolean isValid()
  {
    return valid;
  }

  public void setValid(boolean valid)
  {
    this.valid = valid;
  }

  public Timestamp getRegisterTime()
  {
    return registerTime;
  }

  public void setRegisterTime(Timestamp registerTime)
  {
    this.registerTime = registerTime;
  }

  public String getRegisterTimeAsString() {
    return registerTime.toString().replaceAll("\\.\\d+", "");
  }
  
  public String getToken()
  {
    return token;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public Long getOid()
  {
    return oid;
  }

  public void setOid(Long oid)
  {
    this.oid = oid;
  }
  
  public String getRole() {
     List<String> roleStrs = new ArrayList<String>();
     for (RoleDTO role : roles) {
        roleStrs.add(role.getName());
     }
     String userRole = null;
     if (roleStrs.contains(RoleDTO.ROLE_ADMIN)) {
        userRole = RoleDTO.ROLE_ADMIN_DISPLAYNAME;
     } else if(roleStrs.contains(RoleDTO.ROLE_MODELER) && roleStrs.contains(RoleDTO.ROLE_DESIGNER)) {
        userRole = RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME;
     } else if (roleStrs.contains(RoleDTO.ROLE_MODELER)) {
        userRole = RoleDTO.ROLE_MODELER_DISPLAYNAME;
     } else if(roleStrs.contains(RoleDTO.ROLE_DESIGNER)) {
        userRole = RoleDTO.ROLE_DESIGNER_DISPLAYNAME;
     }
     return userRole;
  }
}
