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

package tuwien.auto.calimero.xml.def;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tuwien.auto.calimero.xml.EntityResolver;
import tuwien.auto.calimero.xml.KNXMLException;

/**
 * Default entity resolver.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class DefaultEntityResolver implements EntityResolver
{
	// IANA to Java encoding names map, used to specify existing charset decoders,
	// only IANA names which are different from the java encoding names are listed
	private static final Map javaNames = new HashMap();

	static {
		// add a new mapping of names, if value from "encoding"
		// pseudo-attribute in declaration is not known by default
		javaNames.put("LATIN1", "ISO-8859-1");
		javaNames.put("ISO-10646-UCS-2", "UTF-16");
		javaNames.put("EBCDIC-CP-US", "CP037");
		javaNames.put("EBCDIC-CP-CA", "CP037");
		javaNames.put("EBCDIC-CP-NL", "CP037");
		javaNames.put("EBCDIC-CP-WT", "CP037");
	}

	/**
	 * Creates a new entity resolver.
	 * <p>
	 */
	public DefaultEntityResolver()
	{}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.xml.EntityResolver#resolveInput(java.lang.String)
	 */
	public InputStream resolveInput(String systemID) throws KNXMLException
	{
		try {
			try {
				final URL loc = new URL(systemID);
				return loc.openConnection().getInputStream();
			}
			catch (final MalformedURLException e) {
				return new FileInputStream(systemID);
			}
		}
		catch (final IOException e) {
			throw new KNXMLException("error opening " + systemID + ", " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.xml.EntityResolver#resolveOutput(java.lang.String)
	 */
	public OutputStream resolveOutput(String systemID) throws KNXMLException
	{
		try {
			try {
				final URL loc = new URL(systemID);
				return loc.openConnection().getOutputStream();
			}
			catch (final MalformedURLException e) {
				return new FileOutputStream(systemID);
			}
		}
		catch (final IOException e) {
			throw new KNXMLException("error opening " + systemID + ", " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.xml.EntityResolver#getInputReader(java.io.InputStream)
	 */
	public Reader getInputReader(InputStream is) throws KNXMLException
	{
		InputStream in = null;
		try {
			in = new BufferedInputStream(is);
			in.mark(9000);
			// deduce encoding from first 4 bytes of stream
			final byte[] start = new byte[4];
			final int count = in.read(start);
			in.reset();
			final String encoding = getEncodingName(in, start, count);
			in.mark(9000);
			final Reader r = new InputStreamReader(in, encoding);
			// search xml declaration
			final char[] decl = new char[5];
			r.read(decl);
			if (!new String(decl).equals("<?xml")) {
				in.reset();
				return new InputStreamReader(in, encoding);
			}
			final String[] att = readXMLDeclaration(r);
			in.reset();
			// check for "encoding" pseudo-attribute
			String javaEncoding = encoding;
			if (att[1] != null) {
				final String ianaEncoding = att[1].toUpperCase(Locale.ENGLISH);
				javaEncoding = (String) javaNames.get(ianaEncoding);
				if (javaEncoding == null)
					javaEncoding = ianaEncoding;
			}
			return new InputStreamReader(in, javaEncoding);
		}
		catch (final IOException e) {
			throw new KNXMLException(e.getMessage());
		}
	}

	// returns the encoding name in IANA format, detected by evaluating the bytes.
	// the detection is done equally to the one in xerces parser
	private static String getEncodingName(InputStream is, byte[] start, int count)
	{
		// '<' = 0x3c, '?' = 0x3f
		final int b0 = start[0] & 0xFF;
		final int b1 = start[1] & 0xFF;
		final int b2 = start[2] & 0xFF;
		// check BOM
		if (count > 1) {
			// UTF-16, big-endian
			if (b0 == 0xFE && b1 == 0xFF)
				return "UTF-16BE";
			// UTF-16, little-endian
			if (b0 == 0xFF && b1 == 0xFE)
				return "UTF-16LE";
		}
		if (count >= 3)
			// UTF-8 with a BOM
			if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
				try {
					is.skip(3);
				}
				catch (final IOException e) {}
				return "UTF-8";
			}
		if (count == 4) {
			final byte[][] arrays = {
			// UCS-4, big endian
				{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3C },
				// UCS-4, little endian
				{ (byte) 0x3C, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				// UTF-16, big-endian
				{ (byte) 0x00, (byte) 0x3C, (byte) 0x00, (byte) 0x3F },
				// UTF-16, little-endian
				{ (byte) 0x3C, (byte) 0x00, (byte) 0x3F, (byte) 0x00 },
				// EBCDIC, returns CP037 like xerces
				{ (byte) 0x4C, (byte) 0x6F, (byte) 0xA7, (byte) 0x94 } };
			final String[] encodings = {
				"ISO-10646-UCS-4", "ISO-10646-UCS-4", "UTF-16BE", "UTF-16LE", "CP037" };
			for (int i = 0; i < encodings.length; ++i)
				if (Arrays.equals(arrays[i], start))
					return encodings[i];
		}
		// default encoding
		return "UTF-8";
	}

	// returns array with length 3 and optional entries version, encoding, standalone
	private String[] readXMLDeclaration(Reader r) throws KNXMLException
	{
		final StringBuffer buf = new StringBuffer(100);
		try {
			for (int c = 0; (c = r.read()) != -1 && c != '?';)
				buf.append((char) c);
		}
		catch (final IOException e) {
			throw new KNXMLException("reading XML declaration, " + e.getMessage(), buf
				.toString(), 0);
		}
		String s = buf.toString().trim();

		String version = null;
		String encoding = null;
		String standalone = null;

		for (int state = 0; state < 3; ++state)
			if (state == 0 && s.startsWith("version")) {
				version = getAttValue(s = s.substring(7));
				s = s.substring(s.indexOf(version) + version.length() + 1).trim();
			}
			else if (state == 1 && s.startsWith("encoding")) {
				encoding = getAttValue(s = s.substring(8));
				s = s.substring(s.indexOf(encoding) + encoding.length() + 1).trim();
			}
			else if (state == 1 || state == 2) {
				if (s.startsWith("standalone")) {
					standalone = getAttValue(s);
					if (!standalone.equals("yes") && !standalone.equals("no"))
						throw new KNXMLException("invalid standalone pseudo-attribute",
							standalone, 0);
					break;
				}
			}
			else
				throw new KNXMLException("unknown XML declaration pseudo-attribute", s, 0);
		return new String[] { version, encoding, standalone };
	}

	private String getAttValue(String s) throws KNXMLException
	{
		final String att = s.trim();
		if (att.charAt(0) == '=' && att.length() > 2) {
			final String v = att.substring(1).trim();
			if (v.length() > 1 && (v.charAt(0) == '\'' || v.charAt(0) == '\"')) {
				final int end = v.indexOf(v.charAt(0), 1);
				if (end != -1)
					return v.substring(1, end);
			}
		}
		throw new KNXMLException("no pseudo-attribute value found", att, 0);
	}
}
