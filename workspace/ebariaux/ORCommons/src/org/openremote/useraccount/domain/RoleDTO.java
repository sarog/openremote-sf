/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
import java.util.List;

/**
 * The Role DTO
 * 
 * @author Marcus
 */
public class RoleDTO implements Serializable
{

  private static final long serialVersionUID = -9067404807528205076L;

  /** The oid. */
  private Long oid;
  
  /** The name. */
  private String name;

  /** The users. */
  private List<UserDTO> users;

  
  public RoleDTO()
  {
  }
  
  public RoleDTO(String roleName)
  {
    this.name = roleName;
  }
  
  public RoleDTO(String roleName, Long oid)
  {
    this.name = roleName;
    this.oid = oid;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the users.
   * 
   * @return the users
   */
  public List<UserDTO> getUsers()
  {
    return users;
  }

  /**
   * Sets the users.
   * 
   * @param users
   *          the new users
   */
  public void setUsers(List<UserDTO> users)
  {
    this.users = users;
  }

  public Long getOid()
  {
    return oid;
  }

  public void setOid(Long oid)
  {
    this.oid = oid;
  }

  
}
