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
package org.openremote.controller.rest.support.json;

import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.openremote.controller.Constants;

/**
 * TODO : This is responsible for translating xml data to json data.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-06-28
 */
public class JSONTranslator
{


  public static String translateXMLToJSON(String acceptTypeInHeader, HttpServletResponse response, String xml)
  {
    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptTypeInHeader))
    {
      response.setContentType(Constants.MIME_APPLICATION_JSON);
      return translate(response, xml);
    }

    else
    {
      response.setContentType(Constants.MIME_APPLICATION_XML);
      return xml;
    }
  }


  public static String translateXMLToJSON(String acceptTypeInHeader, HttpServletResponse response, int errorCode, String xml)
  {
    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptTypeInHeader))
    {
      response.setContentType(Constants.MIME_APPLICATION_JSON);
      return translate(response, xml);
    }

    else
    {
      response.setContentType(Constants.MIME_APPLICATION_XML);
      response.setStatus(errorCode);
      return xml;
    }
  }


  private static String translate(HttpServletResponse response, String xml)
  {
    if (response != null)
    {
      response.setStatus(HttpURLConnection.HTTP_OK);
    }

    xml = xml.replaceAll("xsi:schemaLocation=\".*\"", " ");
    XMLSerializer xmlSerializer = new XMLSerializer();
    xmlSerializer.setTypeHintsEnabled(false);
    xmlSerializer.setTypeHintsCompatibility(false);
    xmlSerializer.setSkipNamespaces(true);

    JSON json = xmlSerializer.read(xml);

    return json.toString(3);
  }

}
