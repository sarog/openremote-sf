/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.device.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
/**
 * Wrapper object for the command string that is sent to the protocol
 * provides functionality for converting the data to a Byte Array
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class Payload {
   public String content;
   public PayloadFormat format = PayloadFormat.TEXT;
   
   public Payload(Byte[] bytes)
   {
      //TODO: Convert Bytes to String representation
      format = PayloadFormat.BINARY;
   }
   
   public Payload(String content)
   {
      this(content, null);
   }
   
   public Payload(String content, PayloadFormat format)
   {
      this.content = content;
      if (format != null) this.format = format;
   }
   
   public Byte[] getBytes()
   {
      List<Byte> result = new ArrayList<Byte>();
      
      switch(format)
      {
      case TEXT:
         result.addAll(Arrays.asList(ArrayUtils.toObject(content.getBytes())));
         break;
      case BINARY:
      case MIXED:
         // TODO: Implement getBytes() method for BINARY AND MIXED Payloads
         // Split by spaces and try and convert blocks into bytes
         // Supported formats: HEX: 0xNN %NN \xNN 0hNN NN NNNNNNNN
         // Any blocks that aren't recognised as binary treat as text
         break;
      }
      
      return result.toArray(new Byte[0]);
   }
   
   public String getContent()
   {
      return content;
   }
   
   public void setContent(String content)
   {
      this.content = content;
   }
}
