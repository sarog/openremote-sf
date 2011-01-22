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

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 1, type <b>Boolean</b>.
 * <p>
 * The KNX data type width is the lowest bit of 1 byte.<br>
 * The default return value after creation is <code>0</code>, i.e. <code>false</code>
 * for DPT Boolean for example.
 * 
 * @author B. Malinowsky
 */
public class DPTXlatorBoolean extends DPTXlator
{
	/**
	 * DPT ID 1.001, Switch; values <b>off</b>, <b>on</b>.
	 * <p>
	 */
	public static final DPT DPT_SWITCH = new DPT("1.001", "Switch", "off", "on");

	/**
	 * DPT ID 1.002, Boolean; values <b>false</b>, <b>true</b>.
	 * <p>
	 */
	public static final DPT DPT_BOOL = new DPT("1.002", "Boolean", "false", "true");

	/**
	 * DPT ID 1.003, Enable; values <b>enable</b>, <b>disable</b>.
	 * <p>
	 */
	public static final DPT DPT_ENABLE = new DPT("1.003", "Enable", "disable", "enable");

	/**
	 * DPT ID 1.004, Ramp; values <b>no ramp</b>, <b>ramp</b>.
	 * <p>
	 */
	public static final DPT DPT_RAMP = new DPT("1.004", "Ramp", "no ramp", "ramp");

	/**
	 * DPT ID 1.005, Alarm; values <b>no alarm</b>, <b>alarm</b>.
	 * <p>
	 */
	public static final DPT DPT_ALARM = new DPT("1.005", "Alarm", "no alarm", "alarm");

	/**
	 * DPT ID 1.006, Binary value; values <b>low</b>, <b>high</b>.
	 * <p>
	 */
	public static final DPT DPT_BINARYVALUE =
		new DPT("1.006", "Binary value", "low", "high");

	/**
	 * DPT ID 1.007, Step; values <b>decrease</b>, <b>increase</b>.
	 * <p>
	 */
	public static final DPT DPT_STEP = new DPT("1.007", "Step", "decrease", "increase");

	/**
	 * DPT ID 1.008, Up/Down; values <b>up</b>, <b>down</b>.
	 * <p>
	 */
	public static final DPT DPT_UPDOWN = new DPT("1.008", "Up/Down", "up", "down");

	/**
	 * DPT ID 1.009, Open/Close; values <b>open</b>, <b>close</b>.
	 * <p>
	 */
	public static final DPT DPT_OPENCLOSE =
		new DPT("1.009", "Open/Close", "open", "close");

	/**
	 * DPT ID 1.010, Start; values <b>stop</b>, <b>start</b>.
	 * <p>
	 */
	public static final DPT DPT_START = new DPT("1.010", "Start", "stop", "start");

	/**
	 * DPT ID 1.011, State; values <b>inactive</b>, <b>active</b>.
	 * <p>
	 */
	public static final DPT DPT_STATE = new DPT("1.011", "State", "inactive", "active");

	/**
	 * DPT ID 1.012, Invert; values <b>not inverted</b>, <b>inverted</b>.
	 * <p>
	 */
	public static final DPT DPT_INVERT =
		new DPT("1.012", "Invert", "not inverted", "inverted");

	/**
	 * DPT ID 1.013, DimSendStyle; values <b>start/stop</b>, <b>cyclic</b>.
	 * <p>
	 */
	public static final DPT DPT_DIMSENDSTYLE =
		new DPT("1.013", "Dim send-style", "start/stop", "cyclic");

	/**
	 * DPT ID 1.014, Input source; values <b>fixed</b>, <b>calculated</b>.
	 * <p>
	 */
	public static final DPT DPT_INPUTSOURCE =
		new DPT("1.014", "Input source", "fixed", "calculated");

	/**
	 * DPT ID 1.015, Reset; values <b>no action</b> (dummy), <b>reset</b> (trigger).
	 * <p>
	 */
	public static final DPT DPT_RESET = new DPT("1.015", "Reset", "no action", "reset");

	/**
	 * DPT ID 1.016, Acknowledge; values <b>no action</b> (dummy), <b>acknowledge</b>
	 * (trigger).
	 * <p>
	 */
	public static final DPT DPT_ACK =
		new DPT("1.016", "Acknowledge", "no action", "acknowledge");

	/**
	 * DPT ID 1.017, Trigger; values <b>trigger</b>, <b>trigger</b>.
	 * <p>
	 */
	public static final DPT DPT_TRIGGER =
		new DPT("1.017", "Trigger", "trigger", "trigger");

	/**
	 * DPT ID 1.018, Occupancy; values <b>not occupied</b>, <b>occupied</b>.
	 * <p>
	 */
	public static final DPT DPT_OCCUPANCY =
		new DPT("1.018", "Occupancy", "not occupied", "occupied");

	/**
	 * DPT ID 1.019, Window/Door; values <b>closed</b>, <b>open</b>.
	 * <p>
	 */
	public static final DPT DPT_WINDOW_DOOR =
		new DPT("1.019", "Window/Door", "closed", "open");

	/**
	 * DPT ID 1.021, Logical function; values <b>OR</b>, <b>AND</b>.
	 * <p>
	 */
	public static final DPT DPT_LOGICAL_FUNCTION =
		new DPT("1.021", "Logical function", "OR", "AND");

	/**
	 * DPT ID 1.022, Scene A/B; values <b>scene A</b>, <b>scene B</b>.
	 * <p>
	 * Note, when displaying scene numbers, scene A is equal to number 1, scene B to
	 * number 2.
	 */
	public static final DPT DPT_SCENE_AB =
		new DPT("1.022", "Scene A/B", "scene A", "scene B");

	/**
	 * DPT ID 1.023, Shutter/Blinds mode; values <b>only move up/down mode</b> (shutter),
	 * <b>move up/down + step-stop mode</b> (blind).
	 * <p>
	 */
	public static final DPT DPT_SHUTTER_BLINDS_MODE =
		new DPT("1.023", "Shutter/Blinds mode", "only move up/down",
			"move up/down + step-stop");

	private static final Map types;

	/**
	 * Creates a translator for the given datapoint type.
	 * <p>
	 * 
	 * @param dpt the requested datapoint type
	 * @throws KNXFormatException on not supported or not available DPT
	 */
	public DPTXlatorBoolean(DPT dpt) throws KNXFormatException
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
	public DPTXlatorBoolean(String dptID) throws KNXFormatException
	{
		super(0);
		setTypeID(types, dptID);
		data = new short[1];
	}

	static {
		types = new HashMap(30);
		types.put(DPT_SWITCH.getID(), DPT_SWITCH);
		types.put(DPT_BOOL.getID(), DPT_BOOL);
		types.put(DPT_ENABLE.getID(), DPT_ENABLE);
		types.put(DPT_RAMP.getID(), DPT_RAMP);
		types.put(DPT_ALARM.getID(), DPT_ALARM);
		types.put(DPT_BINARYVALUE.getID(), DPT_BINARYVALUE);
		types.put(DPT_STEP.getID(), DPT_STEP);
		types.put(DPT_UPDOWN.getID(), DPT_UPDOWN);
		types.put(DPT_OPENCLOSE.getID(), DPT_OPENCLOSE);
		types.put(DPT_START.getID(), DPT_START);
		types.put(DPT_STATE.getID(), DPT_STATE);
		types.put(DPT_INVERT.getID(), DPT_INVERT);
		types.put(DPT_DIMSENDSTYLE.getID(), DPT_DIMSENDSTYLE);
		types.put(DPT_INPUTSOURCE.getID(), DPT_INPUTSOURCE);
		types.put(DPT_RESET.getID(), DPT_RESET);
		types.put(DPT_ACK.getID(), DPT_ACK);
		types.put(DPT_TRIGGER.getID(), DPT_TRIGGER);
		types.put(DPT_OCCUPANCY.getID(), DPT_OCCUPANCY);
		types.put(DPT_WINDOW_DOOR.getID(), DPT_WINDOW_DOOR);
		types.put(DPT_LOGICAL_FUNCTION.getID(), DPT_LOGICAL_FUNCTION);
		types.put(DPT_SCENE_AB.getID(), DPT_SCENE_AB);
		types.put(DPT_SHUTTER_BLINDS_MODE.getID(), DPT_SHUTTER_BLINDS_MODE);
	}

	/**
	 * Sets the translation value from a boolean.
	 * <p>
	 * Any other items in the translator are discarded.
	 * 
	 * @param value the boolean value
	 */
	public final void setValue(boolean value)
	{
		data = new short[] { (short) (value ? 1 : 0) };
	}

	/**
	 * Returns the first translation item formatted as boolean.
	 * <p>
	 * 
	 * @return boolean representation
	 */
	public final boolean getValueBoolean()
	{
		return (data[0] & 0x01) != 0 ? true : false;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getValue()
	 */
	public String getValue()
	{
		return fromDPT(0);
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
			this.data[i] = (short) (data[offset + i] & 0x01);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getData(byte[], int)
	 */
	public byte[] getData(byte[] dst, int offset)
	{
		final int end = Math.min(data.length, dst.length - offset);
		for (int i = 0; i < end; ++i)
			if (data[i] != 0)
				dst[offset + i] |= 1;
			else
				dst[offset + i] &= ~1;
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
	 * @return the subtypes of the boolean translator type
	 * @see DPTXlator#getSubTypesStatic()
	 */
	protected static Map getSubTypesStatic()
	{
		return types;
	}

	protected void toDPT(String value, short[] dst, int index) throws KNXFormatException
	{
		if (dpt.getLowerValue().equalsIgnoreCase(value))
			dst[index] = 0;
		else if (dpt.getUpperValue().equalsIgnoreCase(value))
			dst[index] = 1;
		else
			throw logThrow(LogLevel.WARN, "translation error for " + value,
				"value not recognized", value);
	}

	private String fromDPT(int index)
	{
		return data[index] != 0 ? dpt.getUpperValue() : dpt.getLowerValue();
	}
}
