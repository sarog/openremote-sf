/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
*
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
package org.openremote.manager.online.user.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User account.
 *
 * @author <a href="mailto:juha@juhalindfors">Juha Lindfors</a>
 */
@Entity public class User
{

  @Id @GeneratedValue
  private long id;

  private String loginName;

  private String email;


  // Constructors ---------------------------------------------------------------------------------

  protected User()
  {

  }

  public User(String loginName)
  {
    this();

    setLoginName(loginName);
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public String getLoginName()
  {
    return loginName;
  }

  protected void setLoginName(String loginName)
  {
    this.loginName = loginName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }


}
