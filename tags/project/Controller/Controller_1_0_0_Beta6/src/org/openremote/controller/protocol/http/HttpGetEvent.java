/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.protocol.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;
import org.openremote.controller.event.Event;

/**
 * The Socket Event.
 *
 * @author Marcus 2009-4-26
 */
public class HttpGetEvent extends Event {

   /** The logger. */
   private static Logger logger = Logger.getLogger(HttpGetEvent.class.getName());

   /** A name to identify event in controller.xml. */
   private String name;

   /** The url to perform the http get request on */
   private String url;

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    *
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Gets the url
    * @return the url
    */
   public String getUrl() {
	return url;
   }

   /**
    * Sets the url
    * @param url the new url
    */
   public void setUrl(String url) {
    this.url = url;
   }



   /**
    * {@inheritDoc}
    */
   @Override
   public void exec() {
	   BufferedReader in = null;
		try {
	        URL url = new URL(getUrl());
	        in = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer result = new StringBuffer();
	        String str;
	        while ((str = in.readLine()) != null)
	        {
	          result.append(str);
	        }
	        logger.info("received message: " + result);
		} catch (Exception e) {
			logger.error("HttpGetEvent could not execute", e);
		} finally {
			if (in != null)  {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("BufferedReader could not be closed", e);
				}
			}
		}
   }

//
//   private String readReplay(java.net.Socket socket) throws IOException {
//    	BufferedReader bufferedReader =
//    	    new BufferedReader(
//    		new InputStreamReader(
//    	  	    socket.getInputStream()));
//    	char[] buffer = new char[200];
//    	int readChars = bufferedReader.read(buffer, 0, 200); // blocks until message received
//    	String reply = new String(buffer, 0, readChars);
//    	return reply;
//       }

}
