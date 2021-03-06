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
package org.openremote.controller.exception.roundrobin;

/**
 * This exception class is used to describe tcp client establish fail in roundrobin.
 * 
 * @author Handy.Wang 2009-12-22
 */
@SuppressWarnings("serial")
public class TCPClientEstablishException extends RoundRobinException {
   
   public TCPClientEstablishException() {
      super();
      setErrorCode(RoundRobinException.UDP_CLIENT_ESTABLISH_FAIL);
   }
   
   public TCPClientEstablishException(String exceptionMsg) {
      super(exceptionMsg);
      setErrorCode(RoundRobinException.UDP_CLIENT_ESTABLISH_FAIL);
   }
   
   public TCPClientEstablishException(Throwable cause) {
      super(cause);
      setErrorCode(RoundRobinException.UDP_CLIENT_ESTABLISH_FAIL);
   }
   
   public TCPClientEstablishException(String exceptionMsg, Throwable cause) {
      super(exceptionMsg, cause);
      setErrorCode(RoundRobinException.UDP_CLIENT_ESTABLISH_FAIL);
   }
}
