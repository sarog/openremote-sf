/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
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
