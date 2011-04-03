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
 * Translator for KNX DPTs with main number 11, type <b>date</b>.
 * <p>
 * The KNX data type width is 3 bytes.<br>
 * The type contains the date information year, month, and day of month. <br>
 * Where required in time calculation, the used calendar is based on the current time in
 * the default time zone with the default locale. All time information behaves in
 * non-lenient mode, i.e. no value overflow is allowed and values are not normalized or
 * adjusted using the next, larger field.<br>
 * <p>
 * The default return value after creation is <code>2000-01-01</code>.
 * 
 * @author B. Malinowsky
 */
public class DPTXlatorDate extends DPTXlator
{
	/**
	 * DPT ID 11.001, Date; values from <b>1990-01-01</b> to <b>2089-12-31</b>.
	 * <p>
	 */
	public static final DPT DPT_DATE =
		new DPT("11.001", "Date", "1990-01-01", "2089-12-31", "yyyy-mm-dd");

	private static final int DAY = 0;
	private static final int MONTH = 1;
	private static final int YEAR = 2;

	private static Calendar c;
	private static SimpleDateFormat sdf;
	private static final Map types;

	static {
		types = new HashMap(3);
		types.put(DPT_DATE.getID(), DPT_DATE);
	}

	/**
	 * Creates a translator for the given datapoint type.
	 * <p>
	 * 
	 * @param dpt the requested datapoint type
	 * @throws KNXFormatException on not supported or not available DPT
	 */
	public DPTXlatorDate(DPT dpt) throws KNXFormatException
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
	public DPTXlatorDate(String dptID) throws KNXFormatException
	{
		super(3);
		setTypeID(types, dptID);
		data = new short[] { 1, 1, 0 };
	}

	/**
	 * Sets a user defined date value format used by all instances of this class.
	 * <p>
	 * The pattern is specified according to {@link SimpleDateFormat}. Subsequent date
	 * information, supplied and returned in textual representation, will use a layout
	 * formatted according this pattern.<br>
	 * Note, the format will rely on calendar default time symbols (i.e. language for
	 * example).<br>
	 * If requesting a textual date representation, and using this value format leads to
	 * errors due to an invalid calendar date, a short error message string will be
	 * returned.
	 * 
	 * @param pattern the new pattern specifying the value date format, <code>null</code>
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
	 * Sets the year, month, and day of month for the first date item.
	 * <p>
	 * Any other items in the translator are discarded on successful set.<br>
	 * 
	 * @param year year value, 1990 &lt;= year &lt;= 2089
	 * @param month month value, 1 &lt;= month &lt;= 12
	 * @param day day value, 1 &lt;= day &lt;= 31
	 */
	public final void setValue(int year, int month, int day)
	{
		data = set(year, month, day, new short[3], 0);
	}

	/**
	 * Returns the day information.
	 * <p>
	 * 
	 * @return day of month value, 1 &lt;= day &lt;= 31
	 */
	public final byte getDay()
	{
		return (byte) (data[DAY]);
	}

	/**
	 * Returns the month information.
	 * <p>
	 * 
	 * @return month value, 1 &lt;= month &lt;= 12
	 */
	public final byte getMonth()
	{
		return (byte) (data[MONTH]);
	}

	/**
	 * Returns the year information.
	 * <p>
	 * 
	 * @return year value, 1990 &lt;= year &lt;= 2089
	 */
	public final short getYear()
	{
		return absYear(data[YEAR]);
	}

	/**
	 * Sets the date for the first item using UTC millisecond information.
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
	 * Returns the date information in UTC milliseconds, using the translator default
	 * calendar.
	 * <p>
	 * The method uses year, month and day information for calculation. Any finer time
	 * granularity defaults to 0.<br>
	 * 
	 * @return the date as time in milliseconds as long, as used in {@link Calendar}
	 * @throws KNXFormatException on invalid calendar date
	 */
	public final long getValueMilliseconds() throws KNXFormatException
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
			set(absYear(data[i + YEAR] & 0x7F), data[i + MONTH] & 0x0F,
				data[i + DAY] & 0x1F, buf, item++);
			// check reserved bits
			if ((data[i + YEAR] & ~0x7F) + (data[i + MONTH] & ~0x0F) +
				(data[i + DAY] & ~0x1F) != 0)
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
	 * @return the subtypes of the date translator type
	 * @see DPTXlator#getSubTypesStatic()
	 */
	protected static Map getSubTypesStatic()
	{
		return types;
	}

	private String fromDPT(int index)
	{
		if (sdf != null)
			synchronized (DPTXlatorDate.class) {
				try {
					return sdf.format(new Date(fromDPTMillis(index)));
				}
				catch (final KNXFormatException e) {}
				return "invalid date";
			}
		final int i = index * 3;
		// return year-month-day
		final int d = data[i + DAY];
		final int m = data[i + MONTH];
		return Short.toString(absYear(data[i + YEAR])) + '-' + align(m) + m + '-'
			+ align(d) + d;
	}

	private long fromDPTMillis(int index) throws KNXFormatException
	{
		synchronized (DPTXlatorDate.class) {
			getCalendar().clear();
			final int i = index * 3;
			c.set(Calendar.DAY_OF_MONTH, data[i + DAY]);
			c.set(Calendar.MONTH, data[i + MONTH] - 1);
			c.set(Calendar.YEAR, absYear(data[i + YEAR]));
			try {
				return c.getTimeInMillis();
			}
			catch (final IllegalArgumentException e) {
				throw new KNXFormatException(e.getMessage());
			}
		}
	}

	protected void toDPT(String value, short[] dst, int index) throws KNXFormatException
	{
		if (sdf != null)
			synchronized (DPTXlatorDate.class) {
				toDPT(parse(value), dst, index);
				return;
			}
		final StringTokenizer t = new StringTokenizer(value, "- ");
		final int maxTokens = 3;
		final int[] tokens = new int[maxTokens];
		try {
			int count = 0;
			for (; count < maxTokens && t.hasMoreTokens(); ++count)
				tokens[count] = Short.parseShort(t.nextToken());
			if (count < 3)
				throw logThrow(LogLevel.WARN, "invalid date " + value, null, value);
			set(tokens[0], tokens[1], tokens[2], dst, index);
		}
		catch (final KNXIllegalArgumentException e) {
			throw logThrow(LogLevel.WARN, "invalid date " + value, e.getMessage(), value);
		}
		catch (final NumberFormatException e) {
			throw logThrow(LogLevel.WARN, "invalid number in " + value, null, value);
		}
	}

	private short[] toDPT(long milliseconds, short[] dst, int index)
	{
		synchronized (DPTXlatorDate.class) {
			getCalendar().clear();
			c.setTimeInMillis(milliseconds);
			set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c
				.get(Calendar.DAY_OF_MONTH), dst, index);
		}
		return dst;
	}

	private short[] set(int year, int month, int day, short[] dst, int index)
	{
		if (year < 1990 || year > 2089)
			throw new KNXIllegalArgumentException("year out of range [1990..2089]");
		if (month < 1 || month > 12)
			throw new KNXIllegalArgumentException("month out of range [1..12]");
		if (day < 1 || day > 31)
			throw new KNXIllegalArgumentException("day out of range [1..31]");
		final int i = 3 * index;
		dst[i + DAY] = (short) day;
		dst[i + MONTH] = (short) month;
		dst[i + YEAR] = (short) (year % 100);
		return dst;
	}

	private short absYear(int relative)
	{
		if (relative > 99)
			throw new KNXIllegalArgumentException("relative year out of range [0..99]");
		return (short) (relative + (relative < 90 ? 2000 : 1900));
	}

	private long parse(String value) throws KNXFormatException
	{
		try {
			return sdf.parse(value).getTime();
		}
		catch (final ParseException e) {
			throw logThrow(LogLevel.WARN, "invalid date format", e.getMessage(), value);
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
