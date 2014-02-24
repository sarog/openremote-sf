/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.isy99;

/**
 * Ensure format is followed for Insteon Device addresses
 * 
 * @author craigh
 * 
 */
public class InsteonDeviceAddress {

   private String mAddress;
// TODO: Make sure this doesn't parse 'Light: 1F 5 6F 1'
   public InsteonDeviceAddress(String address) {
      // TODO implement validation logic
      mAddress = address;
   }

   public String toString() {
      return mAddress;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof InsteonDeviceAddress)) return false;
      return mAddress.equals(((InsteonDeviceAddress) obj).mAddress);
   }

   @Override
   public int hashCode() {
      return mAddress.hashCode();
   }
}
