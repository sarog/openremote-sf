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
package org.openremote.controller.protocol.http;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.CodingErrorAction;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import org.openremote.controller.protocol.DeviceProtocol;
import org.openremote.controller.protocol.CommandProperties;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.exception.ProtocolException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Base64;
import org.openremote.controller.Constants;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class HttpProtocol extends DeviceProtocol
{

  private static enum HttpProperty { URL, METHOD, USERNAME, PASSWORD, ACCEPT }

  private static enum HttpMethod
  {
    GET, POST, PUT, DELETE, TRACE, HEAD, OPTIONS;

    private static HttpMethod resolve(String httpMethodString) throws ConversionException
    {
      if (httpMethodString == null)
      {
        throw new ConversionException(""); // TODO
      }

      httpMethodString = httpMethodString.trim().toUpperCase();

      if (httpMethodString.equalsIgnoreCase("POST"))
      {
        return POST;
      }

      else if (httpMethodString.equals("GET"))
      {
        return GET;
      }

      else if (httpMethodString.equals("PUT"))
      {
        return PUT;
      }

      else if (httpMethodString.equals("DELETE"))
      {
        return DELETE;
      }

      else if (httpMethodString.equals("TRACE"))
      {
        return TRACE;
      }

      else if (httpMethodString.equals("HEAD"))
      {
        return HEAD;
      }

      else if (httpMethodString.equals("OPTIONS"))
      {
        return OPTIONS;
      }

      else
      {
        throw new ConversionException(""); // TODO
      }
    }
  }


  private final static Logger log =
      Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "http");


  private URL commandURL;
  private HttpMethod httpMethod = HttpMethod.GET;
  private String httpBasicAuth = null;
  private String accept = null;

  private int connectionTimeoutInMillis = 30000;
  private int inputReadTimeout = 30000;
  private boolean followRedirects = HttpURLConnection.getFollowRedirects();
  private boolean useCaches = true;


  public void setConnectionTimeout(int timeoutInMillis)
  {
    if (timeoutInMillis < 0)
    {
      log.warn("");   // TODO

      timeoutInMillis = 0;
    }

    this.connectionTimeoutInMillis = timeoutInMillis;
  }

  public void setInputReadTimeout(int timeoutInMillis)
  {
    if (timeoutInMillis < 0)
    {
      log.warn("");   // TODO

      timeoutInMillis = 0;
    }

    this.inputReadTimeout = timeoutInMillis;
  }


  public void setFollowRedirects(boolean b)
  {
    this.followRedirects = b;
  }


  public void enableCacheReadThrough(boolean b)
  {
    this.useCaches = !b;
  }

  @Override public void send()
  {
    try
    {
      HttpURLConnection connection = (HttpURLConnection)commandURL.openConnection();

      connection.setConnectTimeout(connectionTimeoutInMillis);
      connection.setRequestMethod(httpMethod.name());
      connection.setInstanceFollowRedirects(followRedirects);
      connection.setReadTimeout(inputReadTimeout);
      connection.setDoInput(true);

      if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT )
      {
        connection.setDoOutput(true);
      }

      connection.setUseCaches(useCaches);

      // TODO : ifModifiedSince

      if (httpBasicAuth != null)
      {
        connection.setRequestProperty("Authorization", "Basic " + httpBasicAuth);
      }

      if (accept != null)
      {
        connection.setRequestProperty("Accept", accept);
      }

      connection.connect();
    }

    catch (ClassCastException e)
    {

    }

    catch (IOException e)
    {

    }
  }



  protected Command createCommand(CommandProperties commandProperties) throws ProtocolException
  {
    String urlString = commandProperties.getMandatoryProperty(HttpProperty.URL.name());
    this.commandURL = validateURL(urlString);

    String method = commandProperties.getOptionalProperty(HttpProperty.METHOD.name());
    this.httpMethod = validateHttpMethod(method);

    String username = commandProperties.getOptionalProperty(HttpProperty.USERNAME.name());

    if (username != null)
    {
      String password = commandProperties.getMandatoryProperty(HttpProperty.PASSWORD.name());

      this.httpBasicAuth = buildHttpBasicAuth(username, password);
    }

    String acceptHeader = commandProperties.getOptionalProperty(HttpProperty.ACCEPT.name());
    this.accept = validateAcceptHeader(acceptHeader);

    return this;
  }



  private HttpMethod validateHttpMethod(String methodString)
  {
    if (methodString == null || methodString.equals(""))
    {
      return httpMethod;
    }

    try
    {
      return HttpMethod.resolve(methodString);
    }

    catch (ConversionException e)
    {
      return httpMethod;
    }
  }


  private URL validateURL(String urlString) throws ProtocolException
  {
    try
    {
      URL url = new URL(urlString);

      String protocol = url.getProtocol();

      if (protocol.equals("http") || protocol.equals("https"))
      {
        return url;
      }

      else
      {
        throw new ProtocolException(""); // TODO
      }
    }

    catch (MalformedURLException e)
    {
      throw new ProtocolException("");  // TODO
    }
  }


  private String buildHttpBasicAuth(String username, String password) throws ProtocolException
  {
    String httpUserPass = username + ":" + password;

    final String ISO_LATIN_1 = "ISO-8859-1";

    try
    {
      Charset latin_1 = Charset.forName("ISO-8859-1");

      CharsetEncoder encoder = latin_1.newEncoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT);

      CharBuffer charbuf = CharBuffer.wrap(httpUserPass);
      ByteBuffer bytebuf = encoder.encode(charbuf);

      byte[] buffer = new byte[bytebuf.limit()];
      int index = 0;

      while (bytebuf.hasRemaining())
      {
        buffer[index++] = bytebuf.get();
      }

      return Base64.encodeBytes(buffer);
    }

    catch (UnmappableCharacterException e)
    {
      throw new ProtocolException(e,
          "Unable to map username or password characters to {0} charset " +
          "required by HTTP header: {1}", ISO_LATIN_1, e.getMessage()
      );
    }

    catch (CharacterCodingException e)
    {
      throw new ProtocolException(e,
          "Failed to convert username or password to {0} charset: {1}",
          ISO_LATIN_1, e.getMessage()
      );
    }

    catch (IllegalCharsetNameException e)
    {
      throw new ProtocolException(e,
          "Implementation Error: Cannot create encoder for charset {0} : {1}",
          ISO_LATIN_1, e.getMessage()
      );
    }

    catch (UnsupportedCharsetException e)
    {
      throw new ProtocolException(e,
          "Runtime Error: {0} charset should always be supported : {1}",
          ISO_LATIN_1, e.getMessage()
      );
    }
  }

  private String validateAcceptHeader(String headerString)
  {

    if (headerString == null)
    {
      return null;
    }
    
    // TODO

    return headerString;
  }

}

