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
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.exception.InvalidCommandTypeException;
import org.openremote.controller.rest.support.json.JSONTranslator;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.service.ServiceContext;

/**
 * TODO
 *
 * @author Handy.Wang
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 */
@SuppressWarnings("serial")
public class ControlCommandRESTServlet extends HttpServlet
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(Constants.REST_COMPONENT_ACTION_LOG_CATEGORY);

  private final static ControlCommandService componentControlService =
    ServiceContext.getComponentControlService();


  // Servlet Overrides ----------------------------------------------------------------------------

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
     throws ServletException, IOException
  {
    doPost(req, resp);
  }

  @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {

    // Set response MIME type and character encoding...

    response.setContentType(Constants.MIME_APPLICATION_XML);
    response.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF8);

    // Get the 'accept' header from client -- this will indicate whether we will send
    // application/xml or application/json response...
    
    String acceptHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER);

    String pathinfo = request.getPathInfo();
    String componentID = null;
    String commandParam = null;
    StringTokenizer st = new StringTokenizer(pathinfo, "/");

    if (st.hasMoreTokens())
    {
      componentID = st.nextToken();
    }

    if (st.hasMoreTokens())
    {
      commandParam = st.nextToken();
    }

    PrintWriter output = response.getWriter();
      
    try
    {
      if (isNotEmpty(componentID) && isNotEmpty(commandParam))
      {
        componentControlService.trigger(componentID, commandParam);

        output.print(JSONTranslator.translateXMLToJSON(
            acceptHeader, response, 200, RESTAPI.composeXMLErrorDocument(200, "SUCCESS"))
        );
      }

      else
      {
        throw new InvalidCommandTypeException(commandParam);
      }
    }

    catch (ControlCommandException e)
    {
      logger.error("Error in executing component control : {0}", e, e.getMessage());

      output.print(JSONTranslator.translateXMLToJSON(
          acceptHeader, response, e.getErrorCode(),
          RESTAPI.composeXMLErrorDocument(e.getErrorCode(), e.getMessage()))
      );
    }

    output.flush();
  }
   
  /**
   * Checks if String parameter is not empty.
   *
   * @param param the param
   *
   * @return true, if parameter is not empty
   */
  private boolean isNotEmpty(String param)
  {
    return (param != null && !"".equals(param)) ? true : false;
  }

}
