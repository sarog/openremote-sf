/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.openremote.controller.Constants;
import org.openremote.controller.rest.support.json.JSONTranslator;

/**
 * This superclass contains the common implementation elements for the OpenRemote Controller
 * HTTP/REST API.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class RESTAPI extends HttpServlet
{

  public static enum ResponseType { APPLICATION_XML, APPLICATION_JSON }


  private ResponseType responseType = ResponseType.APPLICATION_XML;


  // Servlet Implementation -----------------------------------------------------------------------

  @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    // Get the 'accept' header from client -- this will indicate whether we will send
    // application/xml or application/json response...

    String acceptHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER);

    // Set the response content type to either 'application/xml' or 'application/json'
    // according to client's 'accept' header...

    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptHeader))
    {
      response.setContentType(Constants.MIME_APPLICATION_JSON);
      responseType = ResponseType.APPLICATION_JSON;
    }

    else
    {
      // Currently if we don't recognize accept type, default to 'application/xml'...

      response.setContentType(Constants.MIME_APPLICATION_XML);

      responseType = ResponseType.APPLICATION_XML;
    }

    handleRequest(request, response);
  }



  protected abstract void handleRequest(HttpServletRequest request, HttpServletResponse response);

  protected void sendResponse(HttpServletResponse response, String xml) throws IOException
  {
    switch (responseType)
    {
      case APPLICATION_JSON:

        response.getWriter().print(JSONTranslator.translateXMLToJSON(response, xml));

        break;


      case APPLICATION_XML:
      default:

        response.getWriter().print(xml);

        break;
    }
  }

  
}

