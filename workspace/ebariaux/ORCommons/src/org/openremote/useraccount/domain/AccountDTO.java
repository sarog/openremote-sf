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
import java.util.ArrayList;
import java.util.List;

/**
 * The Account DTO
 * 
 * @author Marcus
 */
public class AccountDTO implements Serializable
{

  private static final long serialVersionUID = 7809757129338695202L;

  /** The oid. */
  private Long oid;
  
  /** The users. */
  private List<UserDTO> users;

  private List<ControllerConfigDTO> configs;
  
  private List<ControllerDTO> controller;

  /**
   * Instantiates a new account.
   */
  public AccountDTO()
  {
    configs = new ArrayList<ControllerConfigDTO>();
    users = new ArrayList<UserDTO>();
    controller = new ArrayList<ControllerDTO>();
  }

  public List<UserDTO> getUsers()
  {
    return users;
  }

  public void setUsers(List<UserDTO> users)
  {
    this.users = users;
  }

  public List<ControllerConfigDTO> getConfigs()
  {
    return configs;
  }

  public void setConfigs(List<ControllerConfigDTO> configs)
  {
    this.configs = configs;
  }

  public List<ControllerDTO> getController()
  {
    return controller;
  }

  public void setController(List<ControllerDTO> controller)
  {
    this.controller = controller;
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
