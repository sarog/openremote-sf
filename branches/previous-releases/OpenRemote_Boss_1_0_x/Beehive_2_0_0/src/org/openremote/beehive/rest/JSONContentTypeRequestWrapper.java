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
package org.openremote.beehive.rest;

import org.apache.commons.lang.enums.EnumUtils;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterConfig;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA. User: finalist Date: Mar 5, 2009 Time: 2:17:53 PM To change this template use File |
 * Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class JSONContentTypeRequestWrapper extends HttpServletRequestWrapper {

   FilterConfig myFilterConfig;

   public JSONContentTypeRequestWrapper(HttpServletRequest request, FilterConfig filterConfig) {
      super(request);
      myFilterConfig = filterConfig;
   }

   @Override
   public String getHeader(String name) {
      if ("Accept".toLowerCase().equals(name.toLowerCase())) {
         return "application/json";
      } else {
         return super.getHeader(name);
      }
   }

   @Override
   public Enumeration getHeaders(String s) {
      if ("accept".equals(s.toLowerCase())) {
         Vector<String> headers = new Vector();
         headers.add("application/json");
         return headers.elements();
      } else {
         return super.getHeaders(s);
      }

   }
}
