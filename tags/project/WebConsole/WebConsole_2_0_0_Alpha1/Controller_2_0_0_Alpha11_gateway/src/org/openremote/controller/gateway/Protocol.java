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

import java.io.InputStream;
import java.io.OutputStream;
/**
 * 
 * @author Rich Turner 2011-02-11
 */
public abstract class Protocol implements ProtocolInterface
{
   protected InputStream inputStream;
   protected OutputStream outputStream;
   
   /**
    * Every protocol needs to make use of the input and output stream
    * defined here for sending and receiving data
    */
   
   /** The response buffer for any formatted text received from the server */
   private String responseBuffer;
   
   /**
    *
    * THE FOLLOWING METHODS ARE HERE TO MAINTAIN GATEWAY COMPATIBILITY FOR
    * CONNECTIONLESS PROTOCOLS; THIS AVOIDS THE NEED FOR HAVING TO IMPLEMENT
    * THESE METHODS IF THEY ARE NOT RELEVANT. IF THE PROTOCOL IS CONNECTION
    * BASED THEN THESE METHODS SHOULD BE OVERRIDEN BY THE IMPLEMENTATION SUBCLASS
    *
    */
   
   /**
    * This method deals with opening up communication with the server and 
    * should validate that connection is established and aim to return CONNECTED
    */
   public void connect(int timeOut) throws Exception {
      
   }
   
   /**
    * Cleans up and closes the connection to the server
    */
   public void disconnect() throws Exception {

   }

   /**
    * This method should determine the state of gateway at the particular moment it is called
    * the way in which the current connection state is determined will vary from one protocol
    * to the next
    */
}