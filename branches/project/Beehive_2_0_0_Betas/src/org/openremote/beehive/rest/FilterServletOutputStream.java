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

import javax.servlet.ServletOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: finalist Date: Mar 5, 2009 Time: 2:11:00 PM To change this template use File |
 * Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class FilterServletOutputStream extends ServletOutputStream {
   private DataOutputStream stream;

   public FilterServletOutputStream(OutputStream output) {
      stream = new DataOutputStream(output);
   }

   public void write(int b) throws IOException {
      stream.write(b);
   }

   public void write(byte[] b) throws IOException {
      stream.write(b);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      stream.write(b, off, len);
   }
}
