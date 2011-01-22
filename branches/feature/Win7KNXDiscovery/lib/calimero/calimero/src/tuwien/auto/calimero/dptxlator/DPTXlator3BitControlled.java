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

package tuwien.auto.calimero.dptxlator;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 3, type <b>3 Bit controlled</b>.
 * <p>
 * The KNX data type width is 3 Bits, using the lowest 3 bits of 1 byte.<br>
 * The type structure is [1 Bit control field][3 Bit stepcode field]. The default return
 * value after creation is control flag set to false (0), stepcode 0 (break).
 * <p>
 * In value methods expecting string types, the item is composed of the corresponding
 * subtype control field representation, followed by whitespace and the stepcode.<br>
 * It might be formatted using decimal, hexadecimal, and octal numbers, distinguished by
 * using these prefixes:
 * <dd>no prefix for decimal numeral
 * <dd><code>0x</code>, <code>0X</code> or <code>#</code> for hexadecimal
 * <dd><code>0</code> for octal numeral
 */
public class DPTXlator3BitControlled extends DPTXlator
{
	/**
	 * A DPT for the 3 Bit controlled, with additional access to the control information
	 * DPT.
	 * <p>
	 * 
	 * @author B. Malinowsky
	 */
	public static class DPT3BitControlled extends DPT
	{
		private final DPT ctrl;

		/**
		 * Creates a new datapoint type information structure for the 3 Bit controlled
		 * DPT.
		 * <p>
		 * Such a DPT has a value range from the lower value information like returned
		 * from the control DPT information and stepcode 7, to the upper value control DPT
		 * and stepcode 7.
		 * 
		 * @param typeID {@inheritDoc}
		 * @param description {@inheritDoc}
		 * @param control the DPT of the control information
		 */
		public DPT3BitControlled(String typeID, String description, DPT control)
		{
			super(typeID, description, control.getLowerValue() + " 7", control
				.getUpperValue()
				+ " 7");
			ctrl = control;
		}

		/**
		 * Returns the DPT used to represent the control information of this DPT.
		 * <p>
		 * 
		 * @return the DPT for the control information
		 */
		public final DPT getControlDPT()
		{
			return ctrl;
		}
	}

	/**
	 * DPT ID 3.007, Dimming control; values are {@link DPTXlatorBoolean#DPT_STEP} for
	 * control and 3 Bit stepcode.
	 * <p>
	 */
	public static final DPT DPT_CONTROL_DIMMING =
		new DPT3BitControlled("3.007", "Dimming", DPTXlatorBoolean.DPT_STEP);

	/**
	 * DPT ID 3.008, Blinds control; values are {@link DPTXlatorBoolean#DPT_UPDOWN} for
	 * control and 3 Bit stepcode.
	 * <p>
	 */
	public static final DPT DPT_CONTROL_BLINDS =
		new DPT3BitControlled("3.008", "Blinds", DPTXlatorBoolean.DPT_UPDOWN);

	private static final Map types;

	static {
		types = new HashMap(3);
		types.put(DPT_CONTROL_DIMMING.getID(), DPT_CONTROL_DIMMING);
		types.put(DPT_CONTROL_BLINDS.getID(), DPT_CONTROL_BLINDS);
	}

	/**
	 * Creates a translator for the given datapoint type.
	 * <p>
	 * 
	 * @param dpt the requested datapoint type
	 * @throws KNXFormatException on not supported or not available DPT
	 */
	public DPTXlator3BitControlled(DPT dpt) throws KNXFormatException
	{
		this(dpt.getID());
	}

	/**
	 * Creates a translator for the given datapoint type ID.
	 * <p>
	 * 
	 * @param dptID available implemented datapoint type ID
	 * @throws KNXFormatException on wrong formatted or not expected (available)
	 *         <code>dptID</code>
	 */
	public DPTXlator3BitControlled(String dptID) throws KNXFormatException
	{
		super(0);
		setTypeID(types, dptID);
		data = new short[1];
	}

	/**
	 * Sets one new translation item with the given signed value, replacing any old items.
	 * <p>
	 * The <code>value</code>'s absolute value represents the stepcode, and the sign
	 * represents the control flag, a positive control flag is shown by a positive sign,
	 * control flag 0 by a negative sign. A <code>value</code> of 0 is interpreted as
	 * stepcode 0 with positive control flag.
	 * 
	 * @param value 3 Bit controlled value in signed representation
	 * @see #getValueSigned()
	 */
	public final void setValue(int value)
	{
		setValue(value >= 0, Math.abs(value));
	}

	/**
	 * Returns the value of the first translation item, with the absolute value
	 * representing the stepcode, and the sign representing the control bit.
	 * <p>
	 * A positive control flag (true) is shown by a positive sign, a control flag of 0
	 * (false) by a negative sign in the returned value.<br>
	 * In state "break" (stepcode 0), no distinction is made between control flag set /
	 * not set, both share the value 0.
	 * 
	 * @return 3 Bit controlled value as signed byte
	 */
	public final byte getValueSigned()
	{
		return control(0) ? stepcode(0) : (byte) -stepcode(0);
	}

	/**
	 * Sets one new translation item, replacing any old items.
	 * <p>
	 * 
	 * @param control control direction
	 * @param stepcode the stepcode, 0 &lt;= value &lt;= 7
	 * @see #setControlBit(boolean)
	 * @see #setStepCode(int)
	 */
	public final void setValue(boolean control, int stepcode)
	{
		data = new short[1];
		setControlBit(control);
		setStepCode(stepcode);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getAllValues()
	 */
	public String[] getAllValues()
	{
		final String[] buf = new String[data.length];
		for (int i = 0; i < data.length; ++i)
			buf[i] = fromDPT(i);
		return buf;
	}

	/**
	 * Sets the control field to the specified direction for the first translation item.
	 * <p>
	 * A value of <code>false</code> stands for decrease/up, <code>true</code> for
	 * increase/down.<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param value control direction
	 */
	public final void setControlBit(boolean value)
	{
		if (value)
			data[0] |= 0x08;
		else
			data[0] &= ~0x08;
	}

	/**
	 * Returns the control field of the first translation item.
	 * <p>
	 * A value of <code>false</code> stands for decrease / up, <code>true</code> for
	 * increase / down.
	 * 
	 * @return control bit as boolean
	 */
	public final boolean getControlBit()
	{
		return control(0);
	}

	/**
	 * Sets the stop code for the first translation item.
	 * <p>
	 * The stepcode is the encoded representation of the number of intervals. A stepcode
	 * of 0 is used for step break indication.<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param value the stepcode, 0 &lt;= value &lt;= 7
	 * @see #setIntervals(int)
	 */
	public final void setStepCode(int value)
	{
		if (value < 0 || value > 7)
			throw new KNXIllegalArgumentException("stepcode out of range [0..7]");
		data[0] = (short) ((data[0] & 0x08) | value);
	}

	/**
	 * Returns the stepcode of the first translation item.
	 * <p>
	 * 
	 * @return stepcode value, 0 &lt;= value &lt;= 7
	 * @see #getIntervals()
	 */
	public final byte getStepCode()
	{
		return stepcode(0);
	}

	/**
	 * Sets the stepcode of the first translation item to represent the given desired
	 * number of intervals.
	 * <p>
	 * This number specifies the intervals the range between 0 % and 100 % should get
	 * divided.<br>
	 * The value of <code>numberOfIntervals</code> is rounded to the nearest intervals
	 * representable with a stepcode. A mean value exact in between two intervals is
	 * rounded off (e.g 48 rounded of to 32, 3 rounded of to 2).<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param numberOfIntervals desired intervals, 1 &lt;= intervals &lt;= 64
	 */
	public final void setIntervals(int numberOfIntervals)
	{
		if (numberOfIntervals < 1 || numberOfIntervals > 64)
			throw new KNXIllegalArgumentException("intervals out of range [1..64]");
		int code = 7;
		for (int thres = 0x30; thres >= numberOfIntervals; thres >>= 1)
			--code;
		setStepCode(code);
	}

	/**
	 * Returns the number of intervals specified by the stepcode of the first translation
	 * item.
	 * <p>
	 * If a step break indication is set, the return value is 0.
	 * 
	 * @return number of intervals in the range 1 &lt;= intervals &lt;= 64, or 0
	 */
	public final byte getIntervals()
	{
		return (byte) (1 << (stepcode(0) - 1));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#setData(byte[], int)
	 */
	public void setData(byte[] data, int offset)
	{
		if (offset < 0 || offset > data.length)
			throw new KNXIllegalArgumentException("illegal offset " + offset);
		final int size = data.length - offset;
		if (size == 0)
			throw new KNXIllegalArgumentException("data length " + size
				+ " < KNX data type width " + Math.max(1, getTypeSize()));
		this.data = new short[size];
		for (int i = 0; i < size; ++i)
			this.data[i] = (short) (data[offset + i] & 0x0F);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getData(byte[], int)
	 */
	public byte[] getData(byte[] dst, int offset)
	{
		final int end = Math.min(data.length, dst.length - offset);
		for (int i = 0; i < end; ++i)
			dst[offset + i] = (byte) ((dst[offset + i] & 0xF0) | data[i]);
		return dst;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getSubTypes()
	 */
	public final Map getSubTypes()
	{
		return types;
	}

	/**
	 * @return the subtypes of the 3 Bit controlled translator type
	 * @see DPTXlator#getSubTypesStatic()
	 */
	protected static Map getSubTypesStatic()
	{
		return types;
	}

	private byte stepcode(int index)
	{
		return (byte) (data[index] & 0x07);
	}

	private boolean control(int index)
	{
		return (data[index] & 0x08) != 0 ? true : false;
	}

	private String fromDPT(int index)
	{
		final StringBuffer sb = new StringBuffer();
		final DPT dptCtrl = ((DPT3BitControlled) dpt).getControlDPT();
		sb.append(control(index) ? dptCtrl.getUpperValue() : dptCtrl.getLowerValue());
		sb.append(' ');
		final int steps = stepcode(index);
		if (steps == 0)
			return sb.append("break").toString();
		return sb.append(steps).append(" steps").toString();
	}

	protected void toDPT(String value, short[] dst, int index) throws KNXFormatException
	{
		final StringTokenizer token = new StringTokenizer(value);
		if (token.countTokens() < 2)
			throw logThrow(LogLevel.WARN, "wrong value format " + value, null, value);
		int ctrl = 0;
		String s = token.nextToken();
		final DPT dptCtrl = ((DPT3BitControlled) dpt).getControlDPT();
		if (s.equalsIgnoreCase(dptCtrl.getUpperValue()))
			ctrl = 0x08;
		else if (!s.equalsIgnoreCase(dptCtrl.getLowerValue()))
			throw logThrow(LogLevel.WARN, "translation error for " + s,
				"unknown control value string", s);
		try {
			s = token.nextToken();
			final short code = Short.decode(s).shortValue();
			if (code >= 0 && code < 8) {
				dst[index] = (short) (ctrl + code);
				return;
			}
		}
		catch (final NumberFormatException e) {}
		throw logThrow(LogLevel.WARN, "invalid stepcode " + s, null, s);
	}
}
