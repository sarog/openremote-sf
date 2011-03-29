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
package org.openremote.controller.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * Cheap and dirty HTTP client for the agent
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class RESTCall {
   
   private Logger log = Logger.getLogger(Constants.AGENT_LOG_CATEGORY);

   private HttpURLConnection connection;

   // for tests
   protected RESTCall(){}
   
   public RESTCall(String path, String user, String password) throws AgentException {
      this("GET", path, user, password);
   }
   
   public RESTCall(String method, String path, String user, String password) throws AgentException{
      URL url;
      try {
         url = new URL(path);
      } catch (MalformedURLException e) {
         throw new AgentException("Malformed URL: "+path, e);
      }
      try {
         connection = (HttpURLConnection) url.openConnection();
      } catch (IOException e) {
         // should not happen unless this is a bad protocol
         throw new AgentException("Failed to open URL connection to "+path, e);
      }
      try {
         connection.setRequestMethod(method);
      } catch (ProtocolException e) {
         throw new AgentException("Failed to use "+method+" method", e);
      }
      addAuth(user, password);
   }

   public void invoke() throws AgentException {
      try {
         connection.connect();
         if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
            throw new AgentException("Failed to execute REST call at "+
                  connection.getURL()+": "+
                  connection.getResponseCode()+" "+connection.getResponseMessage());
         }
      } catch (ConnectException e) {
         throw new AgentException("Failed to connect to beehive at "+connection.getURL());
      } catch (IOException e) {
         throw new AgentException("Failed to connect to beehive", e);
      }
   }

   public String getResponse() throws AgentException {
      InputStream is;
      try {
         is = connection.getInputStream();
      } catch (IOException e) {
         throw new AgentException("Failed to read data from connection", e);
      }
      try {
         return IOUtils.toString(is);
      } catch (IOException e) {
         throw new AgentException("Failed to read data from connection", e);
      }finally{
         try {
            is.close();
         } catch (IOException e) {
            // let's not throw on that one, especially in a finally
            log.error("Failed to close input stream", e);
         }
         connection.disconnect();
      }
   }

   private void addAuth(String user, String password) {
      String userpass = user +":"+password;
      String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
      addHeader("Authorization", basicAuth);
   }

   public void addHeader(String header, String value) {
      connection.setRequestProperty(header, value);
   }

   public void disconnect() {
      connection.disconnect();
   }

   public InputStream getInputStream() throws IOException {
      return connection.getInputStream();
   }

}
