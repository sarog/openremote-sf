/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.irbuilder.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.openremote.irbuilder.domain.*;
import org.openremote.irbuilder.service.XMLService;
import org.openremote.irbuilder.utils.HtmlUtils;
import org.openremote.irbuilder.exception.FileOperationException;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Repository
public class XMLServiceImpl implements XMLService {
   /**
    * {@inheritDoc}
    */
   public String controllerXmlBuilder(String data) {
      ControllerWrapper controllerWrapper = parseJSON(data);
      XStream xStream2 = new XStream();
      xStream2.autodetectAnnotations(true);
      return HtmlUtils.unEncoderHTML(xStream2.toXML(controllerWrapper));
   }

   private ControllerWrapper parseJSON(String data) {
      XStream xStream = new XStream(new JettisonMappedXmlDriver());
      xStream.alias("openremote", ControllerWrapper.class);
      xStream.aliasField("events", ControllerWrapper.class, "eventsWrapper");
      xStream.alias("events", EventsWrapper.class);
      xStream.alias("irEvent", IREvent.class);
      xStream.alias("knxEvent", KNXEvent.class);
      xStream.alias("x10Event", X10Event.class);
      xStream.alias("button", ControllerButton.class);
      xStream.addImplicitCollection(ControllerButton.class, "eventIds", "event", Integer.class);
      return  (ControllerWrapper) xStream.fromXML(data);
   }

   /**
    * {@inheritDoc}
    */
   public String iPhoneXmlBuilder(String data) {
      Activity activity =  parseActivity(data);
      IPhoneWrapper iPhoneWrapper = new IPhoneWrapper();
      iPhoneWrapper.getActivities().add(activity);
      XStream xStream2 = new XStream();
      xStream2.autodetectAnnotations(true);
      return HtmlUtils.unEncoderHTML(xStream2.toXML(iPhoneWrapper));
   }

   private Activity parseActivity(String data) {
      XStream xStream = new XStream(new JettisonMappedXmlDriver());

      xStream.setMode(XStream.NO_REFERENCES);
      xStream.alias("activity", Activity.class);
      xStream.alias("screen", Screen.class);
      xStream.alias("button", IPhoneButton.class);

      xStream.addImplicitCollection(Activity.class, "screens", Screen.class);
      xStream.addImplicitCollection(Screen.class, "buttons", IPhoneButton.class);

      return  (Activity) xStream.fromXML(data);
   }

   /**
    * {@inheritDoc}
    */
   public String readLircFile(String url) {
      URL lircUrl;
      try {
         lircUrl = new URL(url);
      } catch (MalformedURLException e) {
         e.printStackTrace();
         throw new IllegalArgumentException("Lirc file url is invalid", e);
      }
      BufferedReader in;
      try {
         in = new BufferedReader(new InputStreamReader(lircUrl.openStream()));
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("reader stream from url occured IOException", e);
      }
      StringBuffer lircStrBuffer = new StringBuffer();
      String tmp;
      try {
         while ((tmp = in.readLine()) != null) {
            lircStrBuffer.append(tmp).append(System.getProperty("line.separator"));
         }
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("reader stream from BufferedReader occured IOException", e);
      } finally {
         try {
            in.close();
         } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("close BufferedReader occured IOException", e);
         }
      }
      return lircStrBuffer.toString();
   }
}
