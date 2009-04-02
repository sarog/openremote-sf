/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
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
package org.openremote.irbuilder.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.openremote.irbuilder.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @author allen.wei
 */
@Controller
public class ScreenController {

   @RequestMapping(value = "/saveScreen.htm", method = RequestMethod.POST)
   public void getScreen(String data, HttpServletRequest req, HttpServletResponse resp) throws IOException {
      XStream xStream = new XStream(new JettisonMappedXmlDriver());
      // xStream.autodetectAnnotations(true);
      xStream.setMode(XStream.NO_REFERENCES);
      xStream.alias("activity", Activity.class);
      xStream.alias("screen", Screen.class);
      xStream.alias("button", IPhoneButton.class);

      xStream.addImplicitCollection(Activity.class, "screens", Screen.class);
      xStream.addImplicitCollection(Screen.class, "buttons", IPhoneButton.class);

      Activity activity = (Activity) xStream.fromXML(data);
      IPhoneWrapper iPhoneWrapper = new IPhoneWrapper();
       iPhoneWrapper.getActivities().add(activity);
      XStream xStream2 = new XStream();
      xStream2.autodetectAnnotations(true);
       String xmlStr = xStream2.toXML(iPhoneWrapper);
      resp.getWriter().print(xmlStr);

       String path = req.getRealPath("/")+"sample.xml";
       System.out.println(path);
       File file = new File(path);
       FileWriter fileWriter = new FileWriter(file);
       BufferedWriter buffreader = new BufferedWriter(fileWriter);
       PrintWriter printWriter = new PrintWriter(buffreader);
       printWriter.write(xmlStr);
       printWriter.flush();

       printWriter.close();
       buffreader.close();
       fileWriter.close();
   }

   @RequestMapping(value = "/saveEvents.htm", method = RequestMethod.POST)
   public void getEvents(String data, HttpServletResponse resp) throws IOException {
      XStream xStream = new XStream(new JettisonMappedXmlDriver());
      xStream.setMode(XStream.NO_REFERENCES);
      xStream.alias("openremote", ControllerWrapper.class);
      xStream.aliasField("events", ControllerWrapper.class, "eventsWrapper");
      xStream.alias("events", EventsWrapper.class);
      xStream.alias("irEvent", IREvent.class);
      xStream.alias("knxEvent", KNXEvent.class);
      xStream.alias("x10Event", X10Event.class);
      xStream.alias("button", ControllerButton.class);
      xStream.addImplicitCollection(ControllerButton.class, "eventIds", "event", Integer.class);
      ControllerWrapper controllerWrapper = (ControllerWrapper) xStream.fromXML(data);

      XStream xStream2 = new XStream();
      xStream2.autodetectAnnotations(true);
      resp.getWriter().print(xStream2.toXML(controllerWrapper));
   }
}
