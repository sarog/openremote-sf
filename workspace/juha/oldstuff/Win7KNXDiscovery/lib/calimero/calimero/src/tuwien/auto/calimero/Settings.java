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

/**
 * General settings used in Calimero as well as Calimero related user information.
 * <p>
 * 
 * @author B. Malinowsky
 */
public final class Settings
{
	private static final String version = "2.0-alpha 3";
	private static final String calimero = "Calimero 2.0";

	private static final String tuwien = "Vienna University of Technology";
	private static final String group = "Automation Systems Group";
	private static final String copyright = "(C) 2007";

	// aligns the bundle package name following the friendly name,
	// works for friendly name with max length of 20 chars
	private static final String bundleAlignment = "                    ";
	// just use newline, it's easier to deal with
	private static final String sep = "\n";

	private static final ClassLoader cl = ClassLoader.getSystemClassLoader();

	private Settings()
	{}

	/**
	 * Returns the Calimero library version as string representation.
	 * <p>
	 * The returned version is formatted something similar to
	 * "main.minor[.milli][-phase]", for example "2.0.0-alpha".
	 * 
	 * @return version as string
	 */
	public static String getLibraryVersion()
	{
		return version;
	}

	/**
	 * Returns a default library header representation with general/usage information.
	 * <p>
	 * It includes stuff like the library name, library version, name and institute of the
	 * 'Vienna University of Technology' where the library was developed, and copyright.
	 * The returned information parts are divided using the newline ('\n') character.
	 * 
	 * @param verbose <code>true</code> to return all header information just mentioned,
	 *        <code>false</code> to only return library name and version comprised of
	 *        one line (no line separators)
	 * @return header as string
	 */
	public static String getLibraryHeader(boolean verbose)
	{
		if (!verbose)
			return calimero + " version " + version;
		final StringBuffer buf = new StringBuffer();
		buf.append(calimero).append(sep);
		buf.append("version ").append(version).append(sep);
		buf.append(tuwien).append(sep);
		buf.append(group).append(sep);
		buf.append(copyright);
		return buf.toString();
	}

	/**
	 * Returns a listing containing all library bundles stating the bundle's presence for
	 * use.
	 * <p>
	 * For loading a bundle, the default system class loader is used. A bundle is present
	 * if it can be loaded using the class loader, otherwise it is considered not
	 * available for use.<br>
	 * An available bundle entry starts with a '+' and consists of a short bundle name and
	 * the base package identifier string, a bundle not present starts with '-' and
	 * consists of a short name and is marked with the suffix "- not available".<br>
	 * The bundle entries in the returned string are separated using the newline ('\n')
	 * character.
	 * 
	 * @return the bundle listing as string
	 */
	public static String getBundleListing()
	{
		final StringBuffer buf = new StringBuffer();
		buf.append(getBundle("log service", "tuwien.auto.calimero.log.LogService", 1)
			+ sep);
		buf.append(getBundle("cEMI", "tuwien.auto.calimero.cemi.CEMI", 1)).append(sep);
		buf.append(getBundle("KNXnet/IP",
			"tuwien.auto.calimero.knxnetip.KNXnetIPConnection", 1)).append(sep);
		buf.append(getBundle("serial", "tuwien.auto.calimero.serial.FT12Connection", 1))
			.append(sep);
		buf.append(getBundle("KNX network link",
			"tuwien.auto.calimero.link.KNXNetworkLink", 1)).append(sep);
		buf.append(getBundle("DPT translator",
			"tuwien.auto.calimero.dptxlator.DPTXlator", 1)).append(sep);
		buf.append(getBundle("data points", "tuwien.auto.calimero.datapoint.Datapoint",
			1)).append(sep);
		buf.append(getBundle("network buffer",
			"tuwien.auto.calimero.buffer.NetworkBuffer", 1)).append(sep);
		buf.append(getBundle("process", "tuwien.auto.calimero.process."
			+ "ProcessCommunicator", 1)	+ sep);
		buf.append(getBundle("management", "tuwien.auto.calimero.mgmt.ManagementClient",
			1)).append(sep);
		buf.append(getBundle("XML", "tuwien.auto.calimero.xml.def.DefaultXMLReader", 2));
		return buf.toString();
	}

	/**
	 * This entry routine of the library prints information to the standard
	 * output stream (System.out), mainly for user information.
	 * <p>
	 * Recognized options for output:
	 * <ul>
	 * <li>no options: default library header information and bundle listing</li>
	 * <li>-v, --version: prints library name and version</li>
	 * </ul>
	 * 
	 * @param args argument list with options controlling output information
	 */
	public static void main(String[] args)
	{
		if (args.length > 0 && (args[0].equals("--version") || args[0].equals("-v")))
			out(getLibraryHeader(false));
		else {
			out(getLibraryHeader(true));
			out("available bundles:");
			out(getBundleListing());
		}
	}

	// for now, this works by loading one class as representative from a bundle
	// to check availability, then class name is truncated to bundle id
	private static String getBundle(String friendlyName, String className, int truncate)
	{
		try {
			cl.loadClass(className);
			int start = className.length();
			for (int i = 0; i < truncate; ++i)
				start = className.lastIndexOf('.', start - 1);
			final String bundle = className.substring(0, start);
			return "+ " + friendlyName + align(friendlyName) + "- " + bundle;
		}
		catch (final ClassNotFoundException e) {}
		catch (final NoClassDefFoundError e) {}
		return "- " + friendlyName + align(friendlyName) + "- not available";
	}

	private static String align(String friendlyName)
	{
		return bundleAlignment.substring(friendlyName.length());
	}

	private static void out(String s)
	{
		System.out.println(s);
	}
}
