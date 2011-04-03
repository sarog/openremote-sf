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

package tuwien.auto.calimero.exception;

/**
 * Thrown to indicate that a timeout occurred while waiting for an acknowledge response.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class KNXAckTimeoutException extends KNXTimeoutException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>KNXAckTimeoutException</code> without a detail message.
	 * <p>
	 */
	public KNXAckTimeoutException()
	{}

	/**
	 * Constructs a new <code>KNXAckTimeoutException</code> with the specified detail
	 * message.
	 * <p>
	 * 
	 * @param s the detail message
	 */
	public KNXAckTimeoutException(String s)
	{
		super(s);
	}
}
