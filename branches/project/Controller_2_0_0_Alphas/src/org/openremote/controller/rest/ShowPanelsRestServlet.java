/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.rest.support.xml.RESTfulErrorCodeComposer;
import org.openremote.controller.rest.support.json.JSONTranslator;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.spring.SpringContext;

/**
 * TODO : Show all available panels.
 * 
 * @author Javen, Dan Cong
 *
 */
public class ShowPanelsRestServlet extends HttpServlet
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for HTTP REST API.
   */
  private final static Logger logger = Logger.getLogger(Constants.REST_ALL_PANELS_LOG_CATEGORY);


  private final static ProfileService profileService = (ProfileService) SpringContext.getInstance().getBean(
       "profileService");

  private final static long serialVersionUID = 1L;


  // Servlet Implementation -----------------------------------------------------------------------

  @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {

    // Set response MIME type and character encoding...

    response.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF8);
    response.setContentType(Constants.MIME_APPLICATION_XML);


    // Write response...

    PrintWriter out = response.getWriter();

    try
    {
      String panelsXML = profileService.getAllPanels();
      out.print(JSONTranslator.toDesiredData(request, response, panelsXML));
    }

    catch (ControlCommandException e)
    {
      logger.error("failed to get all the panels", e);

      response.setStatus(e.getErrorCode());

      out.write(JSONTranslator.toDesiredData(request, response, e.getErrorCode(),
          RESTfulErrorCodeComposer.composeXMLFormatStatusCode(e.getErrorCode(), e.getMessage())));
    }

    finally
    {
      out.flush();
    }
  }

}
