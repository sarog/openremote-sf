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
package org.openremote.controller.protocol.knx;

/**
 * This interface represents a KNX datatype.  <p>
 *
 * KNX 1.1 Datatypes are specified in Volume 3: Systems Specifications, Part 7: Interworking,
 * Section 2: Datapoint Types.  <p>
 *
 * Specified datatypes are:
 *
 * <ol>
 * <li>Boolean</li>
 * <li>1-Bit Controlled</li>
 * <li>3-Bit Controller</li>
 * <li>Character Set</li>
 * <li>8-Bit Unsigned Value</li>
 * <li>8-Bit Signed Value</li>
 * <li>Status with Mode</li>
 * <li>2-Octet Unsigned Value</li>
 * <li>2-Octet Signed Value</li>
 * <li>2-Octet Float Value</li>
 * <li>Time</li>
 * <li>Date</li>
 * <li>4-Octet Unsigned Value</li>
 * <li>4-Octet Signed Value</li>
 * <li>4-Octet Float Value</li>
 * <li>Access</li>
 * <li>String</li>
 * </ol>
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
interface DataType 
{

  int getDataLength();

  byte[] getData();
}
