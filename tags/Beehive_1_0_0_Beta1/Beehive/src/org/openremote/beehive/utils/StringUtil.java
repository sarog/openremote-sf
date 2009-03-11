package org.openremote.beehive.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * Utility class for String
 * 
 * @author Dan 2009-2-16
 *
 */
public class StringUtil {

    private StringUtil(){}
	
	/**
	 * Escapes a SQL string
	 * @param src the string to escape
	 * @return the escaped string 
	 */
	public static String escapeSql(String src){
		if (src.indexOf('\\') != -1) {
			src = src.replace("\\", "\\\\");
		}
		return StringEscapeUtils.escapeSql(src);
	}
	/**
	 * Parses the <code>Model</code> name in a comment
	 * @param comment the comment to parse
	 * @return <code>Model</code> name
	 */
	public static String parseModelNameInComment(String comment){
		String regexpLine = "^\\s*#\\s*(model|model\\s*no\\.\\s*of\\s*remote\\s*control)\\s*:.*?$"; 
		Pattern patLine = Pattern.compile(regexpLine,Pattern.MULTILINE); 
		Matcher m = patLine.matcher(comment);
		String targetLine = "";
		while (m.find()){ 
			targetLine = m.group(0);
			break;
		}
		String name = targetLine.substring(targetLine.indexOf(":") + 1).trim();
		int braceIndex = name.indexOf('(');
		if (braceIndex != -1){
			name = name.substring(0, name.indexOf('(')).trim();
		}
		return name.replace(" ", "_");
	}
	/**
	 * Line.Separator of current OS
	 * 
	 * @return Line.Separator
	 */
	public static String lineSeparator() {
		return System.getProperty("line.separator");
	}
	/**
	 * Two adjacent Line.Separator of current OS
	 * 
	 * @return two adjacent Line.Separator
	 */
	public static String doubleLineSeparator() {
		String sep = System.getProperty("line.separator");
		return sep + sep;
	}
	/**
	 * Calculate the rest of the space between a key and a value
	 * @param key the key 
	 * @return the rest of the space
	 */
	public static String remainedTabSpace(String key){
		String space = "";
		if (key.length() <= 24){
			for (int i = 0; i < 24-key.length(); i++) {
				space += " ";
			}
		}else{
			space = "\t";
		}
		return space;
	}
	/**
	 * Converts '\\' to '/' in URL
	 * 
	 * @param url the URL to convert 
	 * @return the target URL
	 */
	public static String toUrl(String url){
		return url.replace("\\", "/");
	}
	/**
	 * Appends a File.Separator to a string
	 * @param src a string to append
	 * @return the target string
	 */
	public static String appendFileSeparator(String src) {
		return src.endsWith("/") ? src : src + "/";
	}

}
