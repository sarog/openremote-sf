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
package org.openremote.controller.gateway;

import java.util.Map;
/**
 * 
 * @author Rich Turner 2011-02-11
 */
public interface ProtocolInterface
{
   /**
    * This is a method for getting a unique identifier string for the protocol
    * used for identification in log messages etc. If no name available then
    * one should be built from unique protocol parameters i.e. Telnet 192.168.1.1:0000
    */
   public String getName();
   
   /**
    * Opens connection to server and sets input and output streams
    */
   public void connect(int timeOut) throws Exception;
   
   /**
    * Cleans up and closes the connection to the server
    */
   public void disconnect() throws Exception;
   
   public Boolean validateSendAction(String value, Map<String, String> args);
}
