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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mocks a RESTCall
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class MockRESTCall extends RESTCall {

   private String contents;
   private File file;

   public MockRESTCall(String contents) throws AgentException {
      this.contents = contents;
   }
   
   public MockRESTCall(File file) {
      this.file = file;
   }

   public MockRESTCall() {
   }

   @Override
   public void invoke() throws AgentException {
      // do nothing
   }
   
   @Override
   public void invoke(InputStream is) throws AgentException {
      // do nothing
   }
   
   @Override
   public InputStream getInputStream() throws IOException {
      return new FileInputStream(file);
   }
   
   @Override
   public String getResponse() throws AgentException {
      return contents;
   }
   
   @Override
   public void addHeader(String header, String value) {
      // do nothing
   }
   
   @Override
   public void disconnect() {
      // do nothing
   }
}