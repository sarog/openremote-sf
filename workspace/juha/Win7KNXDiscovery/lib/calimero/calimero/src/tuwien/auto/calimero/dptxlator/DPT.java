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

/**
 * Stores information about a datapoint type.
 * <p>
 * Besides the subtype ID of the DPT, a general description, and the unit of measure (if
 * any), there are two additional informational values. They contain string
 * representations of translator values. The first one should contain the string
 * representation of a low value, if possible the lower bound of the supported value
 * range, and the second contains a corresponding upper bound string value representation.
 * <br>
 * Even though not enforced, the preferred way for identifying a DPT is to use a datapoint
 * type ID (dptID) value in the format "[main number].[sub number]".<br>
 * Instances of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class DPT
{
	private final String id;
	private final String desc;
	private final String v1;
	private final String v2;
	private final String unit;

	/**
	 * Creates a new datapoint type information structure.
	 * <p>
	 * 
	 * @param typeID datapoint type identifier
	 * @param description short textual description
	 * @param lower lower value information
	 * @param upper upper value information
	 * @param unit unit of measure, use "" or <code>null</code> for no unit
	 */
	public DPT(String typeID, String description, String lower, String upper, String unit)
	{
		id = typeID;
		desc = description;
		v1 = lower;
		v2 = upper;
		this.unit = unit == null ? "" : unit;
	}

	/**
	 * Creates a new datapoint type information structure for a DPT without a unit.
	 * <p>
	 * 
	 * @param typeID datapoint type identifier
	 * @param description short textual description
	 * @param lower lower value information
	 * @param upper upper value information
	 */
	public DPT(String typeID, String description, String lower, String upper)
	{
		this(typeID, description, lower, upper, "");
	}

	/**
	 * Returns the DPT identifier.
	 * <p>
	 * 
	 * @return ID as string
	 */
	public final String getID()
	{
		return id;
	}

	/**
	 * Returns the DPT description.
	 * <p>
	 * 
	 * @return description as string
	 */
	public final String getDescription()
	{
		return desc;
	}

	/**
	 * Returns the unit of measure for this DPT.
	 * <p>
	 * 
	 * @return unit as string, the empty string for no unit
	 */
	public final String getUnit()
	{
		return unit;
	}

	/**
	 * Returns a lower value information.
	 * <p>
	 * The value either contains a string value representation of some lower bound in the
	 * value range of this DPT, or a more general DPT encoding range information if
	 * practicable.
	 * 
	 * @return lower value as string
	 */
	public final String getLowerValue()
	{
		return v1;
	}

	/**
	 * Returns an upper value information.
	 * <p>
	 * It contains an upper value representation in the value range for this DPT. The
	 * corresponding method {@link #getLowerValue()} should contain the lower bound value
	 * then.<br>
	 * If no information about an upper value can be given, a DPT might set this
	 * information optional, and the empty string is returned.
	 * 
	 * @return value as string, or the empty string
	 */
	public final String getUpperValue()
	{
		return v2;
	}

	/**
	 * Returns the DPT's information in concise textual format.
	 * <p>
	 * 
	 * @return a string representation of the DPT
	 */
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(30);
		sb.append(id).append(": ").append(desc).append(", values ").append(v1);
		if (v2.length() > 0)
			sb.append(' ').append(v2);
		if (unit.length() > 0)
			sb.append(' ').append(unit);
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		return obj == this || (obj instanceof DPT) && ((DPT) obj).id.equals(id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return id.hashCode();
	}
}
