package org.openremote.controller.protocol.lutron;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lutron address format:
 * optionnaly enclosed in square brackets
 * 3 to five components
 * separated by period, colon, slash, backslash or dash
 * letters and spaces are ignored whitespaceAndNewlineCharacterSet letterCharacterSet
 *
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class LutronHomeWorksAddress {

	  // Instance Fields ------------------------------------------------------------------------------

	private byte addressElements[];

	private static Pattern addressPattern = Pattern.compile("\\x5b?(\\d{1,2})[.:/\\-](\\d{1,2})[.:/\\-](\\d{1,2})(?:[.:/\\-](\\d{1,2}))?(?:[.:/\\-](\\d{1,2}))?\\x5d?");
	
	
	  // Constructors ---------------------------------------------------------------------------------

	public LutronHomeWorksAddress(String addressRepresentation) throws InvalidLutronHomeWorksAddressException {
		Matcher matcher = addressPattern.matcher(addressRepresentation);
		if (!matcher.matches()) {
		      throw new InvalidLutronHomeWorksAddressException("Incorrect format for Lutron address (" + addressRepresentation + ")", addressRepresentation);
		}
		if (addressRepresentation.startsWith("[") && !addressRepresentation.endsWith("]")) {
		      throw new InvalidLutronHomeWorksAddressException("Lutron address starting with [ must end with ] (" + addressRepresentation + ")", addressRepresentation);
		}
		
		// Note that last groups can be null and group count will be 5
		// Check for non null groups in order and have that the number of elements
		int numElements = 0;
		for (int i = 1; i <= matcher.groupCount(); i++) {
			if (matcher.group(i) != null) {
				numElements++;
			}
		}

		if (numElements < 3 || numElements > 5) {
		      throw new InvalidLutronHomeWorksAddressException("Incorrect number of elements in Lutron address (" + addressRepresentation + ")", addressRepresentation);
		}
		
		System.out.println("Matcher count / num elements " + matcher.groupCount() + " / " + numElements);
		for (int i = 0; i <= matcher.groupCount(); i++) {
			System.out.println("group " + i + " " + matcher.group(i));
		}

		addressElements = new byte[numElements];
		for (int i = 1; i <= numElements; i++) {
			addressElements[i - 1] = Byte.parseByte(matcher.group(i));
		}
		
	}
	
	  /**
	   * Returns string representation of this group address with the following conventions
	   * - address enclosed between [ and ]
	   * - using semi-colon as separator
	   * - elements on 2 digits, 0 padded
	   *
	   * @return Lutron address appropriately formatted
	   */
	  @Override public String toString() {
		  StringBuffer temp = new StringBuffer("[");
		  for (int i = 0; i < addressElements.length; i++) {
			  temp.append(String.format("%02d",addressElements[i]));
			  if (i != addressElements.length - 1) {
				  temp.append(":");
			  }
		  }
		  temp.append("]");
		  return temp.toString();
	  }

}
