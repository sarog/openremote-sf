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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 10, type <b>time</b>.
 * <p>
 * The KNX data type width is 3 bytes.<br>
 * The type contains time information (hour, minute, second), together with an (optional)
 * day of week. If no day of week is supplied or available, the value encoding "no-day" is
 * used and returned by default. Otherwise, short day name values are used (Mon, Tue, Wed,
 * Thu, Fri, Sat, Sun). Take care when applying user defined time formats, that the
 * "no-day" identifier is not a supported calendar time format symbol, only a KNX DPT
 * specific.<br>
 * Where required in time calculation, the used calendar is based on the current time in
 * the default time zone with the default locale. All time information behaves in
 * non-lenient mode, i.e. no value overflow is allowed and values are not normalized or
 * adjusted using the next, larger field.<br>
 * <p>
 * The default return value after creation is <code>no-day, 00:00:00</code>.
 * 
 * @author B. Malinowsky
 */
public class DPTXlatorTime extends DPTXlator
{
	/**
	 * DPT ID 10.001, Time of day; values from <b>no-day, 00:00:00</b> to <b>Sun,
	 * 23:59:59</b>.
	 * <p>
	 */
	public static final DPT DPT_TIMEOFDAY =
		new DPT("10.001", "Time of day", "no-day, 00:00:00", "Sun, 23:59:59",
			"dow, hh:mm:ss");

	private static final int DOW = 0;
	private static final int HOUR = 0;
	private static final int MINUTE = 1;
	private static final int SECOND = 2;

	// if we use "no day" without hyphen, parsing to DPT have a maximum of 5 tokens
	private static final String[] DAYS =
		{ "no-day", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

	private static Calendar c;
	private static SimpleDateFormat sdf;
	private static final Map types;

	static {
		types = new HashMap(3);
		types.put(DPT_TIMEOFDAY.getID(), DPT_TIMEOFDAY);
	}

	/**
	 * Creates a translator for the given datapoint type.
	 * <p>
	 * 
	 * @param dpt the requested datapoint type
	 * @throws KNXFormatException on not supported or not available DPT
	 */
	public DPTXlatorTime(DPT dpt) throws KNXFormatException
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
	public DPTXlatorTime(String dptID) throws KNXFormatException
	{
		super(3);
		setTypeID(types, dptID);
		data = new short[3];
	}

	/**
	 * Sets a user defined time value format used by all instances of this class.
	 * <p>
	 * The pattern is specified according to {@link SimpleDateFormat}. Subsequent time
	 * information, supplied and returned in textual representation, will use a layout
	 * formatted according this pattern.<br>
	 * Note, the format will rely on calendar default time symbols (i.e. language for
	 * example), and does not support the KNX DPT identifier "no-day" for day of week.
	 * This identifier can not be used therefore.
	 * 
	 * @param pattern the new pattern specifying the value time format, <code>null</code>
	 *        to reset to default value format
	 */
	public static final synchronized void useValueFormat(String pattern)
	{
		if (pattern == null)
			sdf = null;
		else if (sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			sdf.setCalendar(getCalendar());
		}
		else
			sdf.applyPattern(pattern);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getAllValues()
	 */
	public String[] getAllValues()
	{
		final String[] buf = new String[data.length / 3];
		for (int i = 0; i < buf.length; ++i)
			buf[i] = fromDPT(i);
		return buf;
	}

	/**
	 * Sets the day of week, hour, minute and second information for the first time item.
	 * <p>
	 * Any other items in the translator are discarded on successful set.<br>
	 * A day of week value of 0 corresponds to "no-day", indicating the day of week is not
	 * used. The first day of week is Monday with a value of 1, the last day is Sunday
	 * with a value of 7. <br>
	 * 
	 * @param dayOfWeek day of week, 0 &lt;= day &lt;= 7
	 * @param hour hour value, 0 &lt;= hour &lt;= 23
	 * @param minute minute value, 0 &lt;= minute &lt;= 59
	 * @param second second value, 0 &lt;= second &lt;= 59
	 */
	public final void setValue(int dayOfWeek, int hour, int minute, int second)
	{
		data = set(dayOfWeek, hour, minute, second, new short[3], 0);
	}

	/**
	 * Returns the day of week information.
	 * <p>
	 * The return of 0 corresponds to "no-day", indicating the day of week is not used.
	 * The first day of week is Monday with a value of 1, Sunday has a value of 7.
	 * 
	 * @return day of week value, 0 &lt;= day of week &lt;= 7
	 */
	public final byte getDayOfWeek()
	{
		return (byte) (data[DOW] >> 5);
	}

	/**
	 * Returns the hour information.
	 * <p>
	 * 
	 * @return hour value, 0 &lt;= hour &lt;= 23
	 */
	public final byte getHour()
	{
		return (byte) (data[HOUR] & 0x1F);
	}

	/**
	 * Returns the minute information.
	 * <p>
	 * 
	 * @return minute value, 0 &lt;= minute &lt;= 59
	 */
	public final byte getMinute()
	{
		return (byte) data[MINUTE];
	}

	/**
	 * Returns the second information.
	 * <p>
	 * 
	 * @return second value, 0 &lt;= second &lt;= 59
	 */
	public final byte getSecond()
	{
		return (byte) data[SECOND];
	}

	/**
	 * Sets the time for the first item using UTC millisecond information.
	 * <p>
	 * The <code>milliseconds</code> is interpreted with the translator default
	 * calendar.
	 * 
	 * @param milliseconds time value in milliseconds, as used in {@link Calendar}
	 */
	public final void setValue(long milliseconds)
	{
		data = toDPT(milliseconds, new short[3], 0);
	}

	/**
	 * Returns the time information in UTC milliseconds, using the translator
	 * default calendar.
	 * <p>
	 * The method uses day of week, hour, minute and second information for calculation.
	 * <br>
	 * Note, since this is UTC time information, the initially returned local time
	 * 00:00:00 does therefore not corresponding to 0 milliseconds, except in the case
	 * when the local time zone is GMT.
	 * 
	 * @return the time in milliseconds as long, as used in {@link Calendar}
	 */
	public final long getValueMilliseconds()
	{
		return fromDPTMillis(0);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#setData(byte[], int)
	 */
	public void setData(byte[] data, int offset)
	{
		if (offset < 0 || offset > data.length)
			throw new KNXIllegalArgumentException("illegal offset " + offset);
		final int size = (data.length - offset) / 3 * 3;
		if (size == 0)
			throw new KNXIllegalArgumentException("data length " + size
				+ " < KNX data type width " + Math.max(1, getTypeSize()));
		final short[] buf = new short[size];
		int item = 0;
		for (int i = offset; i < size + offset; i += 3) {
			set((data[i + DOW] & 0xE0) >> 5, data[i + HOUR] & 0x1F,
				data[i + MINUTE] & 0x3F, data[i + SECOND] & 0x3F, buf, item++);
			// check reserved bits
			if ((data[i + MINUTE] & ~0x3F) + (data[i + SECOND] & ~0x3F) != 0)
				logger.warn(dpt.getID() + " - reserved bit not 0");
		}
		this.data = buf;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getData(byte[], int)
	 */
	public byte[] getData(byte[] dst, int offset)
	{
		final int end = Math.min(data.length, dst.length - offset) / 3 * 3;
		for (int i = 0; i < end; ++i)
			dst[offset + i] = (byte) data[i];
		return dst;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getSubTypes()
	 */
	public Map getSubTypes()
	{
		return types;
	}

	/**
	 * @return the subtypes of the time translator type
	 * @see DPTXlator#getSubTypesStatic()
	 */
	protected static Map getSubTypesStatic()
	{
		return types;
	}

	private String fromDPT(int index)
	{
		if (sdf != null)
			synchronized (DPTXlatorTime.class) {
				return sdf.format(new Date(fromDPTMillis(index)));
			}
		final int i = index * 3;
		// dow, hh:mm:ss
		final int h = data[i + HOUR] & 0x1F;
		final int m = data[i + MINUTE];
		final int s = data[i + SECOND];
		return DAYS[data[i + DOW] >> 5] + ", " + align(h) + h + ':' + align(m) + m + ':'
			+ align(s) + s;
	}

	private long fromDPTMillis(int index)
	{
		synchronized (DPTXlatorTime.class) {
			getCalendar().clear();
			final int i = index * 3;
			final int dow = data[i] >> 5 & 0x7;
			if (dow > 0)
				c.set(Calendar.DAY_OF_WEEK, dow == 7 ? 1 : dow + 1);
			c.set(Calendar.HOUR_OF_DAY, data[i + HOUR] & 0x1F);
			c.set(Calendar.MINUTE, data[i + MINUTE]);
			c.set(Calendar.SECOND, data[i + SECOND]);
			long ms = 0;
			try {
				ms = c.getTimeInMillis();
			}
			catch (final IllegalArgumentException e) {
				DPTXlator.logger.error("from DPT in milliseconds not possible");
			}
			return ms;
		}
	}

	protected void toDPT(String value, short[] dst, int index) throws KNXFormatException
	{
		if (sdf != null)
			synchronized (DPTXlatorTime.class) {
				toDPT(parse(value), dst, index);
				return;
			}
		final StringTokenizer t = new StringTokenizer(value, ": ,");
		final int maxTokens = 4;
		final String[] tokens = new String[maxTokens];
		// extract tokens
		int count = 0;
		for (; count < maxTokens && t.hasMoreTokens(); ++count)
			tokens[count] = t.nextToken();
		// we allow day of week to be omitted in value
		if (count < 3)
			throw logThrow(LogLevel.WARN, "invalid time " + value, null, value);
		// on 4 tokens, day of week is included
		final int dow = count == 4 ? getDOW(tokens[0]) : 0;
		try {
			final int s = Short.parseShort(tokens[--count]);
			final int m = Short.parseShort(tokens[--count]);
			final int h = Short.parseShort(tokens[--count]);
			set(dow, h, m, s, dst, index);
		}
		catch (final KNXIllegalArgumentException e) {
			throw logThrow(LogLevel.WARN, "invalid time " + value, e.getMessage(), value);
		}
		catch (final NumberFormatException e) {
			throw logThrow(LogLevel.WARN, "invalid number in " + value, null, value);
		}
	}

	private short[] toDPT(long milliseconds, short[] dst, int index)
	{
		synchronized (DPTXlatorTime.class) {
			getCalendar().clear();
			c.setTimeInMillis(milliseconds);
			final int dow = c.get(Calendar.DAY_OF_WEEK);
			set(dow == Calendar.SUNDAY ? 7 : dow - 1, c
				.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c
				.get(Calendar.SECOND), dst, index);
		}
		return dst;
	}

	private short[] set(int dow, int hour, int minute, int second, short[] dst, int index)
	{
		if (dow < 0 || dow > 7)
			throw new KNXIllegalArgumentException("day of week out of range [0..7]");
		if (hour < 0 || hour > 23)
			throw new KNXIllegalArgumentException("hour out of range [0..23]");
		if (minute < 0 || minute > 59)
			throw new KNXIllegalArgumentException("minute out of range [0..59]");
		if (second < 0 || second > 59)
			throw new KNXIllegalArgumentException("second out of range [0..59]");
		final int i = 3 * index;
		dst[i + DOW] = (short) (dow << 5 | hour);
		dst[i + MINUTE] = (short) minute;
		dst[i + SECOND] = (short) second;
		return dst;
	}

	private int getDOW(String dow) throws KNXFormatException
	{
		for (int i = 0; i < DAYS.length; ++i)
			if (DAYS[i].equalsIgnoreCase(dow))
				return i;
		throw logThrow(LogLevel.WARN, "invalid day of week " + dow, null, dow);
	}

	private long parse(String value) throws KNXFormatException
	{
		try {
			return sdf.parse(value).getTime();
		}
		catch (final ParseException e) {
			throw logThrow(LogLevel.WARN, "invalid time format", e.getMessage(), value);
		}
	}

	private static String align(int number)
	{
		return number > 9 ? "" : "0";
	}

	private static Calendar getCalendar()
	{
		// don't need to synchronize, it's harmless if we have 2 instances
		// and we synchronize on class anyway
		if (c == null) {
			final Calendar calendar = Calendar.getInstance();
			calendar.setLenient(false);
			c = calendar;
		}
		return c;
	}
}
