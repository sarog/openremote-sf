package org.openremote.beehive.file;

/**
 * Enum of charset.
 * 
 * Created by IntelliJ IDEA. User: AllenWei Date: 2008-6-6 Time: 16:30:08
 * 
 */
public enum EnumCharset {
	
	UTF_8("UTF-8"),
	ISO_8859_1("ISO-8859-1"),
	US_ASCII("US-ASCII"),
	UTF_16("UTF-16"),
	UTF_16BE("UTF-16BE"),
	UTF_16LE("UTF-16LE");
	
	EnumCharset(String str) {
		this.value = str;
	}

	private String value;

	public String getValue() {
		return value;
	}
}
