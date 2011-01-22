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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 19, type <b>date with time</b>.
 * <p>
 * The KNX data type width is 8 bytes.<br>
 * The type contains date information (month, day of month, day of week), time information
 * (hour, minute, second), additional time information (work-day, daylight saving time
 * (DST)) and clock information (faulty clock, external clock synchronization signal). The
 * field usage of work-day, year, date, time, day of week field is optional. <br>
 * By default, on setting date/time information, only general range checks are performed,
 * no check is done whether the information corresponds to a valid calendar time (see
 * {@link #validate()}. All field-methods behave in non-lenient time value mode, i.e. no
 * value overflow is allowed and values are not normalized or adjusted using the next,
 * larger field. (e.g. February, 29th will not result in March, 1st on no leap year).<br>
 * This type permits the hour set to 24 (with minute and second only valid if 0),
 * representing midnight of the old day, to handle time information used in schedule
 * programs.
 * <p>
 * The default return value after creation is the calendar value equal to
 * <code>1900/1/1 00:00:00</code> (year/month/day hh:mm:ss), no clock fault, not in
 * daylight saving time, no external clock synchronization signal, day of week and work
 * day fields are not used.
 * 
 * @author B. Malinowsky
 */
public class DPTXlatorDateTime extends DPTXlator
{
	/**
	 * DPT ID 19.001, Date with time; values from <b>1900, 01/01 00:00:00</b> to
	 * <b>2155, 12/31 24:00:00</b>.
	 * <p>
	 */
	public static final DPT DPT_DATE_TIME =
		new DPT("19.001", "Date with time", "1900, 01/01 00:00:00",
			"2155, 12/31 24:00:00", "yr/mth/day hr:min:sec");

	/**
	 * Field number for <code>get</code> and <code>set</code> indicating whether the
	 * year field is used.
	 * <p>
	 * 
	 * @see #setValidField(int, boolean)
	 * @see #isValidField(int)
	 */
	public static final int YEAR = 0;

	/**
	 * Field number for <code>get</code> and <code>set</code> indicating whether the
	 * date field (month and day of month) is used.
	 * <p>
	 * 
	 * @see #setValidField(int, boolean)
	 * @see #isValidField(int)
	 */
	public static final int DATE = 1;

	/**
	 * Field number for <code>get</code> and <code>set</code> indicating whether the
	 * time field (hour, minute and second) is used.
	 * <p>
	 * 
	 * @see #setValidField(int, boolean)
	 * @see #isValidField(int)
	 */
	public static final int TIME = 2;

	/**
	 * Field number for <code>get</code> and <code>set</code> indicating whether the
	 * day of week field is used.
	 * <p>
	 * 
	 * @see #setValidField(int, boolean)
	 * @see #isValidField(int)
	 */
	public static final int DAY_OF_WEEK = 3;

	/**
	 * Field number for <code>get</code> and <code>set</code> indicating whether the
	 * work-day field is used; furthermore it is used to change the corresponding work-day
	 * value.
	 * <p>
	 * The work-day information can be specified in string values by using "workday" to
	 * denote a working day.
	 * 
	 * @see #setValidField(int, boolean)
	 * @see #isValidField(int)
	 * @see #setDateTimeFlag(int, boolean)
	 * @see #getDateTimeFlag(int)
	 */
	public static final int WORKDAY = 4;

	/**
	 * Field number to query or change the daylight saving time (DST) information.
	 * <p>
	 * This field is for user information solely, any DST offset is already considered in
	 * the time field (i.e. the hour field contains the adjusted value).
	 * <p>
	 * If daylight saving time is used, this information can be specified in string values
	 * by using "DST" to denote daylight saving time.
	 * 
	 * @see #setDateTimeFlag(int, boolean)
	 * @see #getDateTimeFlag(int)
	 */
	public static final int DAYLIGHT = 5;

	/**
	 * Field number to query or change the clock fault information.
	 * <p>
	 * A clock fault indicates one or more corrupted date/time fields, for example due to
	 * power down of device, not configured clock, no reception of synchronization
	 * message, ... .
	 * 
	 * @see #setDateTimeFlag(int, boolean)
	 * @see #getDateTimeFlag(int)
	 */
	public static final int CLOCK_FAULT = 6;

	/**
	 * Field number to query or change the external clock synchronization signal
	 * information.
	 * <p>
	 * In string values, this information can be specified by using "in sync" or "no sync"
	 * to set clock synchronization. Omitting this information defaults to no external
	 * clock synchronization signal.
	 * 
	 * @see #setDateTimeFlag(int, boolean)
	 * @see #getDateTimeFlag(int)
	 */
	public static final int CLOCK_SYNC = 7;

	/**
	 * Minimum year representable by this type, year = {@value #MIN_YEAR}.
	 * <p>
	 */
	public static final int MIN_YEAR = 1900;

	/**
	 * Maximum year representable by this type, year = {@value #MAX_YEAR}.
	 * <p>
	 */
	public static final int MAX_YEAR = MIN_YEAR + 0xff;

	private static final int MONTH = 1;
	private static final int DAY = 2;
	private static final int HOUR = 3;
	private static final int MINUTE = 4;
	private static final int SECOND = 5;
	private static final int DOW = 6;

	private static final String DAYLIGHT_SIGN = "DST";
	private static final String WORKDAY_SIGN = "workday";
	private static final String SYNC_SIGN = "sync";

	private static final String[] DAYS =
		{ "Any day", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
	private static final String[] FIELDS =
		{ "year", "month", "day", "hour", "minute", "second" };
	private static final int[] MIN_VALUES = { MIN_YEAR, 1, 1, 0, 0, 0, 0 };
	private static final int[] MAX_VALUES = { MAX_YEAR, 12, 31, 24, 59, 59, 7 };

	// standard field bit masks
	private static final int DST = 0x01;
	private static final int NO_TIME = 0x02;
	private static final int NO_DOW = 0x04;
	private static final int NO_DATE = 0x08;
	private static final int NO_YEAR = 0x10;
	private static final int NO_WD = 0x20;
	private static final int WD = 0x40;
	private static final int FAULT = 0x80;
	// extended field bit masks
	private static final int QUALITY = 0x80;

	private static final int[] FIELD_MASKS = { NO_YEAR, NO_DATE, NO_TIME, NO_DOW, NO_WD };

	// fault, workday, DST, quality
	private static final int[] FLAG_MASKS = { WD, DST, FAULT, QUALITY };

	private static Calendar c;
	private static final Map types;

	private boolean extFormat = true;

	static {
		types = new HashMap(3);
		types.put(DPT_DATE_TIME.getID(), DPT_DATE_TIME);
	}

	/**
	 * Creates a translator for the given datapoint type.
	 * <p>
	 * 
	 * @param dpt the requested datapoint type
	 * @throws KNXFormatException on not supported or not available DPT
	 */
	public DPTXlatorDateTime(DPT dpt) throws KNXFormatException
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
	public DPTXlatorDateTime(String dptID) throws KNXFormatException
	{
		super(8);
		setTypeID(types, dptID);
		data = new short[] { 0, 1, 1, 0, 0, 0, NO_WD | NO_DOW, 0 };
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getAllValues()
	 */
	public String[] getAllValues()
	{
		final String[] buf = new String[data.length / 8];
		for (int i = 0; i < buf.length; ++i)
			buf[i] = fromDPT(i);
		return buf;
	}

	/**
	 * Sets year, month and day of month information of the first date/time item.
	 * <p>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param year year value, 1900 &lt;= year &lt;= 2155
	 * @param month month value, 1 &lt;= month &lt;= 12
	 * @param day day value, 1 &lt;= day &lt;= 31
	 */
	public final void setDate(int year, int month, int day)
	{
		set(data, 0, YEAR, year);
		set(data, 0, MONTH, month);
		set(data, 0, DAY, day);
	}

	/**
	 * Returns the year information.
	 * <p>
	 * 
	 * @return year value, 0 &lt;= second &lt;= 59
	 */
	public final short getYear()
	{
		return (short) (data[YEAR] + MIN_YEAR);
	}

	/**
	 * Returns the month information.
	 * <p>
	 * 
	 * @return month value, 0 &lt; month &lt;= 12, might be 0 on unused field
	 */
	public final byte getMonth()
	{
		return (byte) data[MONTH];
	}

	/**
	 * Returns the day of month information.
	 * <p>
	 * The first day of month equals 1.<br>
	 * 
	 * @return day value, 0 &lt; day &lt;= 31, might be 0 on unused field
	 */
	public final byte getDay()
	{
		return (byte) data[DAY];
	}

	/**
	 * Sets the day of week of the first date/time item.
	 * <p>
	 * A day of week value of 0 corresponds to "any day", indicating the day of week is
	 * variable (used in scheduling). The first day of week is Monday with a value of 1.
	 * <br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param day day of week value, 0 &lt;= day &lt;= 7
	 */
	public final void setDayOfWeek(int day)
	{
		set(data, 0, DOW, day);
	}

	/**
	 * Returns the day of week information.
	 * <p>
	 * The return of 0 corresponds to "any day", indicating the day of week is variable
	 * (used in scheduling). The first day of week is Monday with a value of 1.
	 * 
	 * @return day of week value, 0 &lt;= day of week &lt;= 7
	 */
	public final byte getDayOfWeek()
	{
		return (byte) (data[3] >> 5);
	}

	/**
	 * Sets the hour, minute and second information for the first date/time item.
	 * <p>
	 * On a hour value of 24, values of minute and second have to be 0.<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param hour hour value, 0 &lt;= hour &lt;= 24
	 * @param minute minute value, 0 &lt;= minute &lt;= 59
	 * @param second second value, 0 &lt;= second &lt;= 59
	 */
	public final void setTime(int hour, int minute, int second)
	{
		if (!check24Hours(hour, minute, second))
			throw new KNXIllegalArgumentException("incorrect time");
		set(data, 0, HOUR, hour);
		set(data, 0, MINUTE, minute);
		set(data, 0, SECOND, second);
	}

	/**
	 * Returns the hour information.
	 * <p>
	 * An hour value of 24 represents midnight of the old day, the corresponding minute
	 * and second are always 0.
	 * 
	 * @return hour value, 0 &lt;= hour &lt;= 24
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
	 * Sets the date/time for the first date/time item using UTC millisecond information.
	 * <p>
	 * Following fields are set: year, month, day, day of week, hour, minute, second,
	 * daylight time. All according fields are set to used state.<br>
	 * The <code>value</code> is interpreted by a calendar obtained by
	 * {@link Calendar#getInstance()}.<br>
	 * The new value item replaces any other items contained in this translator.
	 * 
	 * @param milliseconds time value in milliseconds, as used by {@link Calendar}
	 */
	public final void setValue(long milliseconds)
	{
		initCalendar();
		synchronized (c) {
			c.clear();
			c.setTimeInMillis(milliseconds);
			data = new short[8];
			set(data, 0, YEAR, c.get(Calendar.YEAR));
			set(data, 0, MONTH, c.get(Calendar.MONTH) + 1);
			set(data, 0, DAY, c.get(Calendar.DAY_OF_MONTH));
			set(data, 0, HOUR, c.get(Calendar.HOUR_OF_DAY));
			set(data, 0, MINUTE, c.get(Calendar.MINUTE));
			set(data, 0, SECOND, c.get(Calendar.SECOND));
			final int dow = c.get(Calendar.DAY_OF_WEEK);
			set(data, 0, DOW, dow == Calendar.SUNDAY ? 7 : dow - 1);
			setBit(0, DST, c.get(Calendar.DST_OFFSET) != 0);
			data[6] &= ~(NO_YEAR | NO_DATE | NO_TIME | NO_DOW);
		}
	}

	/**
	 * Returns the date and time information of the first date/time item in UTC
	 * milliseconds, using the system default {@link Calendar}.
	 * <p>
	 * The method uses the year, month, day, DST and, optionally, hour, minute, second and
	 * day of week field for calculation.<br>
	 * On unused time field, 00:00:00 is used. If year, month or day field is unused (see
	 * {@link #isValidField(int)}, or the field values do not represent a valid calendar
	 * time, an exception is thrown. If time equals 24:00:00, it is represented as
	 * 23:59:59.999, since the time ambiguity of midnight is resolved as midnight
	 * belonging to the next day (00:00:00) in the used calendar. However, no field values
	 * itself is changed permanently.<br>
	 * The used calendar is obtained by {@link Calendar#getInstance()}, and the
	 * calculation is done in non-lenient mode.
	 * 
	 * @return the date/time in milliseconds as long,
	 * @throws KNXFormatException on required, but not set fields, if date/time
	 *         information does not represent a valid calendar time,
	 */
	public final long getValueMilliseconds() throws KNXFormatException
	{
		return fromDPTMilliseconds(0);
	}

	/**
	 * Sets date/time information for the given field of the first date/time item.
	 * <p>
	 * Allowed fields are {@link #CLOCK_FAULT}, {@link #CLOCK_SYNC}, {@link #WORKDAY}
	 * and {@link #DAYLIGHT}.<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param field field number
	 * @param value <code>true</code> to set the information flag, <code>false</code>
	 *        to clear
	 */
	public final void setDateTimeFlag(int field, boolean value)
	{
		if (field == CLOCK_SYNC) {
			setBitEx(0, QUALITY, value);
			return;
		}
		final int f = field - WORKDAY;
		if (f < 0 || f >= FLAG_MASKS.length)
			throw new KNXIllegalArgumentException("illegal field");
		setBit(0, FLAG_MASKS[f], value);
	}

	/**
	 * Returns the information value of <code>field</code>.
	 * <p>
	 * Allowed fields are {@link #CLOCK_FAULT}, {@link #CLOCK_SYNC}, {@link #WORKDAY}
	 * and {@link #DAYLIGHT}.
	 * 
	 * @param field field number to query
	 * @return the field value as boolean, <code>true</code> if set, <code>false</code>
	 *         otherwise
	 */
	public final boolean getDateTimeFlag(int field)
	{
		return getDateTimeFlag(0, field);
	}

	/**
	 * Sets the clock fault information field of the first date/time item.
	 * <p>
	 * Equal to invoking {@link #setDateTimeFlag(int, boolean)} with field
	 * {@link #CLOCK_FAULT}.<br>
	 * This method does not reset other item data or discard other translation items.
	 * 
	 * @param fault <code>true</code> if clock is faulty, <code>false</code> otherwise
	 * @see #setDateTimeFlag(int, boolean)
	 * @see #CLOCK_FAULT
	 */
	public final void setFaultyClock(boolean fault)
	{
		setBit(0, FAULT, fault);
	}

	/**
	 * Returns whether the date/time information is marked as corrupted.
	 * <p>
	 * It should always queried first, before accessing further date/time information, to
	 * assure correct values.
	 * 
	 * @return <code>true</code> on clock fault, <code>false</code> otherwise
	 * @see #getDateTimeFlag(int)
	 * @see #CLOCK_FAULT
	 */
	public final boolean isFaultyClock()
	{
		return getBit(0, FAULT);
	}

	/**
	 * Sets a date/time field of the first translation item to valid or not valid.
	 * <p>
	 * A field which is valid, i.e. contains valid data, has to be set <code>true</code>,
	 * otherwise the field should be set not valid with <code>false</code>.<br>
	 * Possible fields allowed to be set valid or not valid are {@link #YEAR},
	 * {@link #DATE}, {@link #TIME}, {@link #DAY_OF_WEEK} and {@link #WORKDAY}.
	 * 
	 * @param field field number
	 * @param valid <code>true</code> if field is supported and contains valid data,
	 *        <code>false</code> otherwise
	 */
	public final void setValidField(int field, boolean valid)
	{
		if (field < 0 || field >= FIELD_MASKS.length)
			throw new KNXIllegalArgumentException("illegal field");
		setBit(0, FIELD_MASKS[field], !valid);
	}

	/**
	 * Returns whether a field is valid, i.e. supported and used for date/time
	 * information.
	 * <p>
	 * Only data of valid fields are required to be set or examined. A field set to not
	 * valid shall be ignored, it is not supported and contains no valid data.<br>
	 * Possible fields allowed to be set valid or not valid are {@link #YEAR},
	 * {@link #DATE}, {@link #TIME}, {@link #DAY_OF_WEEK} and {@link #WORKDAY}.
	 * 
	 * @param field field number
	 * @return <code>true</code> if field is supported and in use, <code>false</code>
	 *         otherwise
	 */
	public final boolean isValidField(int field)
	{
		if (field < 0 || field >= FIELD_MASKS.length)
			throw new KNXIllegalArgumentException("illegal field");
		return !getBit(0, FIELD_MASKS[field]);
	}

	/**
	 * Does a complete validity check on the contained date/time items.
	 * <p>
	 * An item is valid if all used fields correspond to a valid calendar time. The check
	 * is only performed if at least year and date fields are used.<br>
	 * After modifying date/time fields, this method has to be called again if validation
	 * is required.<br>
	 * It returns the result of the validity checks performed on the date/time items.
	 * <p>
	 * Comparison of the two kinds of validity:<br>
	 * When setting individual parts of a date/time, only the KNX requirements for the DPT
	 * are checked, as requested by the DPT specification. For example, this includes a
	 * common range check of a value. So set items always match these KNX value ranges.<br>
	 * This validation method checks if the date/time fields represent a valid calendar
	 * time, which is a much stronger requirement.
	 * 
	 * @return <code>true</code> if date/times are valid, <code>false</code> otherwise
	 */
	public final boolean validate()
	{
		try {
			for (int i = 0; i < data.length / 8; ++i)
				if (isValidField(i, YEAR) && isValidField(i, DATE))
					fromDPTMilliseconds(i);
		}
		catch (final KNXFormatException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#setData(byte[], int)
	 */
	public void setData(byte[] data, int offset)
	{
		if (offset < 0 || offset > data.length)
			throw new KNXIllegalArgumentException("illegal offset " + offset);
		final int size = (data.length - offset) & ~7;
		if (size == 0)
			throw new KNXIllegalArgumentException("data length " + size
				+ " < KNX data type width " + Math.max(1, getTypeSize()));
		final short[] buf = new short[size];
		final int[] mask = { 0xFF, 0x0F, 0x1F, 0xFF, 0x3F, 0x3F, 0xFF, 0x80 };
		for (int i = 0; i < size; ++i) {
			final int field = i & 0x07;
			buf[i] = (short) (data[offset + i] & mask[field]);
			// check reserved bits
			if ((ubyte(data[offset + i]) & ~mask[field]) != 0)
				logger.warn(dpt.getID() + " - reserved bit not 0");
			// check range on set fields
			if (field == 6 && (buf[i] & NO_DATE) == 0) {
				checkRange(MONTH, buf[i - 5]);
				checkRange(DAY, buf[i - 4]);
			}
			if (field == 6 && (buf[i] & NO_TIME) == 0) {
				checkRange(HOUR, buf[i - 3] & 0x1F);
				checkRange(MINUTE, buf[i - 2]);
				checkRange(SECOND, buf[i - 1]);
				if (!check24Hours(buf[i - 3] & 0x1F, buf[i - 2], buf[i - 1]))
					throw new KNXIllegalArgumentException("incorrect time");
			}
		}
		this.data = buf;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getData(byte[], int)
	 */
	public byte[] getData(byte[] dst, int offset)
	{
		final int end = Math.min(data.length, dst.length - offset) & ~7;
		for (int i = 0; i < end; ++i)
			dst[offset + i] = (byte) data[i];
		return dst;
	}

	/**
	 * Specifies the formatting to use for date/time string representations returned by
	 * the translator.
	 * <p>
	 * A string in extended format contains all valid information of the date/time type.
	 * In extended format, additionally the day of week, daylight time, work-day and clock
	 * synchronization signal information is considered in the output format. Otherwise
	 * this fields are always ignored, i.e only clock fault, year, month, day, hour,
	 * minute and second will get used, if valid.
	 * <p>
	 * The used format is extended by default.
	 * 
	 * @param extended string format to use, <code>true</code> for extended format
	 */
	public final void useValueFormat(boolean extended)
	{
		extFormat = extended;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.dptxlator.DPTXlator#getSubTypes()
	 */
	public Map getSubTypes()
	{
		return types;
	}

	/**
	 * @return the subtypes of the date with time translator type
	 * @see DPTXlator#getSubTypesStatic()
	 */
	protected static Map getSubTypesStatic()
	{
		return types;
	}

	private void checkRange(int field, int v)
	{
		if (v < MIN_VALUES[field] || v > MAX_VALUES[field])
			throw new KNXIllegalArgumentException(FIELDS[field] + " out of range: " + v);
	}

	// check on hour = 24, minutes and seconds have to be 0
	private boolean check24Hours(int hr, int min, int sec)
	{
		if (hr != 24 || min == 0 && sec == 0)
			return true;
		return false;
	}

	private String fromDPT(int index)
	{
		if (getBit(index, FAULT))
			return "corrupted date/time";
		final StringBuffer sb = new StringBuffer(20);
		final int i = index * 8;
		// year
		if (!getBit(index, NO_YEAR))
			sb.append(data[i] + MIN_YEAR);
		if (!getBit(index, NO_DATE)) {
			// month
			sb.append('/').append(data[i + 1]);
			// day
			sb.append('/').append(data[i + 2]);
		}
		if (extFormat) {
			if (!getBit(index, NO_DOW))
				sb.append(", ").append(DAYS[data[i + 3] >> 5]);
			if (!getBit(index, NO_WD))
				sb.append(getBit(index, WD) ? " (" : " (no ").append(WORKDAY_SIGN)
					.append(')');
		}
		if (!getBit(index, NO_TIME)) {
			// hr:min:sec
			sb.append(' ').append(data[i + 3] & 0x1F);
			sb.append(':').append(data[i + 4]).append(':').append(data[i + 5]);
			// daylight saving time
			if (extFormat && getBit(index, DST))
				sb.append(' ').append(DAYLIGHT_SIGN);
		}
		if (extFormat)
			sb.append(getBitEx(index, QUALITY) ? ", in " : ", no ").append(SYNC_SIGN);
		return sb.toString();
	}

	private long fromDPTMilliseconds(int index) throws KNXFormatException
	{
		if (getBit(index, FAULT) || !isValidField(index, YEAR)
			|| !isValidField(index, DATE))
			throw new KNXFormatException("insufficient information for calendar");
		initCalendar();
		final int i = index * 8;
		synchronized (c) {
			c.clear();
			c.set(data[i] + MIN_YEAR, data[i + 1] - 1, data[i + 2]);
			if (isValidField(index, TIME)) {
				// we use 23:59:59.999 in calendar for 24:00:00
				final boolean cheat = (data[i + 3] & 0x1F) == 24;
				c.set(Calendar.HOUR_OF_DAY, cheat ? 23 : data[i + 3] & 0x1F);
				c.set(Calendar.MINUTE, cheat ? 59 : data[i + 4]);
				c.set(Calendar.SECOND, cheat ? 59 : data[i + 5]);
				if (cheat)
					c.set(Calendar.MILLISECOND, 999);
			}
			try {
				final long ms = c.getTimeInMillis();
				if (isValidField(index, DAY_OF_WEEK)) {
					int day = c.get(Calendar.DAY_OF_WEEK);
					day = day == Calendar.SUNDAY ? 7 : day - 1;
					if ((data[i + 3] >> 5) != day)
						throw new KNXFormatException("differing day of week");
				}
				if (getDateTimeFlag(index, DAYLIGHT) != (c.get(Calendar.DST_OFFSET) != 0))
					throw new KNXFormatException("differing daylight saving time");
				return ms;
			}
			catch (final IllegalArgumentException e) {
				throw new KNXFormatException("invalid calendar value " + e.getMessage());
			}
		}
	}

	private boolean getDateTimeFlag(int index, int field)
	{
		if (field == CLOCK_SYNC)
			return getBitEx(index, QUALITY);
		final int f = field - WORKDAY;
		if (f < 0 || f >= FLAG_MASKS.length)
			throw new KNXIllegalArgumentException("illegal field");
		return getBit(index, FLAG_MASKS[f]);
	}

	private boolean isValidField(int index, int field)
	{
		return !getBit(index, FIELD_MASKS[field]);
	}

	private boolean getBit(int index, int mask)
	{
		return (data[8 * index + 6] & mask) != 0;
	}

	private boolean getBitEx(int index, int mask)
	{
		return (data[8 * index + 7] & mask) != 0;
	}

	private void set(short[] dst, int index, int field, int v)
	{
		checkRange(field, v);
		final int i = 8 * index + field;
		if (field == YEAR)
			dst[i] = (short) (v - MIN_YEAR);
		else if (field == DOW)
			// NOTE: DoW id is not field index
			dst[i - 3] = (short) (v << 5 | (dst[i - 3] & 0x1F));
		else if (field == HOUR)
			dst[i] = (short) (v | (dst[i] & 0xE0));
		else
			dst[i] = (short) v;
	}

	private void setBit(int index, int mask, boolean bit)
	{
		setBit(data, index, mask, bit);
	}

	private void setBit(short[] dst, int index, int mask, boolean bit)
	{
		if (bit)
			dst[8 * index + 6] |= mask;
		else
			dst[8 * index + 6] &= ~mask;
	}

	private void setBitEx(int index, int mask, boolean bit)
	{
		setBitEx(data, index, mask, bit);
	}

	private void setBitEx(short[] v, int index, int mask, boolean bit)
	{
		if (bit)
			v[8 * index + 7] |= mask;
		else
			v[8 * index + 7] &= ~mask;
	}

	// dst is assumed to be cleared
	protected void toDPT(String value, short[] dst, int index) throws KNXFormatException
	{
		final StringTokenizer t = new StringTokenizer(value, ":-/ (,.)");
		final int k = 8 * index;
		// dpt fields: yr mth day [doW hr] min sec [dst wd] sync
		// mark all fields not used
		dst[k + 6] = NO_WD | NO_YEAR | NO_DATE | NO_DOW | NO_TIME;

		int sync = 0;
		int day = 0;
		final int maxTokens = 10;
		final short[] numbers = new short[maxTokens];
		int count = 0;
		// parse whole string, only handle word and year tokens, store numbers
		for (int i = 0; i < maxTokens && t.hasMoreTokens(); ++i) {
			final String s = t.nextToken();
			try {
				final short no = Short.parseShort(s);
				if (no < 0)
					throw logThrow(LogLevel.WARN, "negative date/time " + s, null, s);
				if (no >= MIN_YEAR && no <= MAX_YEAR) {
					set(dst, index, YEAR, no);
					setBit(dst, index, NO_YEAR, false);
				}
				else
					numbers[count++] = no;
			}
			catch (final NumberFormatException e) {
				// parse number failed, check for word token
				if (s.equalsIgnoreCase(DAYLIGHT_SIGN))
					setBit(dst, index, DST, true);
				else if (s.equalsIgnoreCase(WORKDAY_SIGN)) {
					setBit(dst, index, NO_WD, false);
					setBit(dst, index, WD, true);
				}
				else if (s.equalsIgnoreCase("no") || s.equalsIgnoreCase("in")) {
					if (++sync > 1 || !t.hasMoreTokens()
						|| !t.nextToken().equalsIgnoreCase(SYNC_SIGN))
						throw logThrow(LogLevel.WARN, s + ": '" + SYNC_SIGN
							+ "' expected", null, s);
					// check sync'd or not sync'd
					if (s.charAt(0) == 'i' || s.charAt(0) == 'I')
						setBitEx(dst, index, QUALITY, true);
				}
				else if (s.length() == 3 && ++day == 1) {
					final String prefix = s.toLowerCase();
					int dow = DAYS.length - 1;
					while (dow >= 0 && !DAYS[dow].toLowerCase().startsWith(prefix))
						--dow;
					final boolean anyday = dow == 0 && t.hasMoreTokens()
						&& t.nextToken().equalsIgnoreCase("day");
					if (dow <= 0 && !anyday)
						throw logThrow(LogLevel.WARN, s + ": wrong weekday", null, s);
					set(dst, index, DOW, dow);
					setBit(dst, index, NO_DOW, false);
				}
				else
					throw logThrow(LogLevel.WARN, "wrong date/time " + s, null, s);
			}
		}
		// find out date/time combination, and store numbers into fields
		if (count == 0)
			return;
		if (count == 1 || count == 4)
			throw logThrow(LogLevel.WARN, "ambiguous date/time " + value, null, value);
		int field = count == 3 ? HOUR : MONTH;
		if (field == HOUR || count == 5)
			setBit(dst, index, NO_TIME, false);
		if (field == MONTH)
			setBit(dst, index, NO_DATE, false);
		for (int i = 0; i < count; ++i, ++field)
			set(dst, index, field, numbers[i]);
		// check time field, if set
		if (field == SECOND + 1
			&& !check24Hours(numbers[count - 3], numbers[count - 2], numbers[count - 1]))
			throw logThrow(LogLevel.WARN, "incorrect time " + value, null, value);
	}

	private static synchronized void initCalendar()
	{
		if (c == null) {
			c = Calendar.getInstance();
			c.setLenient(false);
		}
	}
}
