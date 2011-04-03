/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tuwien.auto.calimero;

import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * Priority of KNX messages for access on a KNX medium.
 * <p>
 * All possible priority values are supplied as immutable Priority constants.
 * 
 * @author B. Malinowsky
 */
public final class Priority
{
	/**
	 * System priority, reserved for high priority management and system configuration.
	 * <p>
	 */
	public static final Priority SYSTEM = new Priority(0x00);

	/**
	 * Urgent priority, for urgent frames.
	 * <p>
	 */
	public static final Priority URGENT = new Priority(0x02);

	/**
	 * Normal priority, the default for short frames.
	 * <p>
	 */
	public static final Priority NORMAL = new Priority(0x01);

	/**
	 * Low priority, used for long frames.
	 * <p>
	 */
	public static final Priority LOW = new Priority(0x03);

	/**
	 * Constant with the 2 Bit representation of this priority used in the message
	 * priority field.
	 * <p>
	 */
	public final byte value;

	private Priority(int v)
	{
		value = (byte) v;
	}

	/**
	 * Returns the priority of the supplied priority value code.
	 * <p>
	 * 
	 * @param value priority value code, 0 &lt;= value &lt;= 3
	 * @return the corresponding priority object
	 */
	public static Priority get(int value)
	{
		if (value == 1)
			return NORMAL;
		if (value == 0)
			return SYSTEM;
		if (value == 3)
			return LOW;
		if (value == 2)
			return URGENT;
		throw new KNXIllegalArgumentException("invalid priority value");
	}

	/**
	 * Returns the priority of the supplied priority value representation.
	 * <p>
	 * The following textual representations for <code>value</code> are allowed:
	 * <ul>
	 * <li>"system"</li>
	 * <li>"normal"</li>
	 * <li>"urgent"</li>
	 * <li>"low"</li>
	 * </ul>
	 * These are in accordance with the ones returned by {@link #toString()}.
	 * 
	 * @param value priority value in textual representation, value is treated case
	 *        insensitive
	 * @return the corresponding priority object
	 */
	public static Priority get(String value)
	{
		if ("system".equalsIgnoreCase(value))
			return SYSTEM;
		if ("normal".equalsIgnoreCase(value))
			return NORMAL;
		if ("urgent".equalsIgnoreCase(value))
			return URGENT;
		if ("low".equalsIgnoreCase(value))
			return LOW;
		throw new KNXIllegalArgumentException("invalid priority value");
	}

	/**
	 * Returns the priority in textual representation.
	 * <p>
	 * <ul>
	 * <li>{@link #SYSTEM} returns "system"</li>
	 * <li>{@link #NORMAL} returns "normal"</li>
	 * <li>{@link #LOW} returns "low"</li>
	 * <li>{@link #URGENT} returns "urgent"</li>
	 * </ul>
	 * 
	 * @return priority as string
	 */
	public String toString()
	{
		return value == 0 ? "system" : value == 1 ? "normal" : value == 2 ? "urgent"
			: "low";
	}
}
