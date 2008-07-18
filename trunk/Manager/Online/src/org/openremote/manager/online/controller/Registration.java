/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.manager.online.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller servlet which handles the REST API implementation for home controller registration.
 *
 * TODO : document the API
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 * @version $Id: $
 */
public class Registration extends HttpServlet
{

  @Override public void doPut(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      System.out.println(request);
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }
}
