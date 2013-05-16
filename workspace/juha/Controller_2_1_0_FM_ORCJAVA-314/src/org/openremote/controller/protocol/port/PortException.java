/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.port;

/**
 * Exception related to ports.
 * 
 */
public class PortException extends Exception {
   private static final long serialVersionUID = 1L;
   public static final int INVALID_MESSAGE = -2;
   public static final int SERVICE_TIMEOUT = -3;
   public static final int SERVICE_FAILED = -4;
   public static final int ALREADY_LISTENING = -5;
   public static final int INVALID_CONFIGURATION = -6;
   private int code;
   private int rootCode;

   public PortException(int code) {
      this(code, 0);
   }

   public PortException(int code, int rootCode) {
      this.code = code;
      this.rootCode = rootCode;
   }

   public int getCode() {
      return this.code;
   }

   public int getRootCode() {
      return this.rootCode;
   }
}
