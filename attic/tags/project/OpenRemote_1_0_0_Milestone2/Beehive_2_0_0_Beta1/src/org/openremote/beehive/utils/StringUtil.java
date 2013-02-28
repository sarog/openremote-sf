/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
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

   private StringUtil() {
   }

   /**
    * Escapes a SQL string
    * 
    * @param src
    *           the string to escape
    * @return the escaped string
    */
   public static String escapeSql(String src) {
      if (src.indexOf('\\') != -1) {
         src = src.replace("\\", "\\\\");
      }
      return StringEscapeUtils.escapeSql(src);
   }

   /**
    * Parses the <code>Model</code> name in a comment
    * 
    * @param comment
    *           the comment to parse
    * @return <code>Model</code> name
    */
   public static String parseModelNameInComment(String comment) {
      String regexpLine = "^\\s*#\\s*(model|model\\s*no\\.\\s*of\\s*remote\\s*control)\\s*:.*?$";
      Pattern patLine = Pattern.compile(regexpLine, Pattern.MULTILINE);
      Matcher m = patLine.matcher(comment);
      String targetLine = "";
      while (m.find()) {
         targetLine = m.group(0);
         break;
      }
      String name = targetLine.substring(targetLine.indexOf(":") + 1).trim();
      int braceIndex = name.indexOf('(');
      if (braceIndex != -1) {
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
    * 
    * @param key
    *           the key
    * @return the rest of the space
    */
   public static String remainedTabSpace(String key) {
      String space = "";
      if (key.length() <= 24) {
         for (int i = 0; i < 24 - key.length(); i++) {
            space += " ";
         }
      } else {
         space = "\t";
      }
      return space;
   }

   /**
    * Converts '\\' to '/' in URL
    * 
    * @param url
    *           the URL to convert
    * @return the target URL
    */
   public static String toUrl(String url) {
      return url.replace("\\", "/");
   }

   /**
    * Appends a File.Separator to a string
    * 
    * @param src
    *           a string to append
    * @return the target string
    */
   public static String appendFileSeparator(String src) {
      return src.endsWith("/") ? src : src + "/";
   }

   public static Date String2Date(String strDate, String format, Locale locale) {
      DateFormat fmt = new SimpleDateFormat(format, Locale.ENGLISH);
      Date date = new Date();
      try {
         date = fmt.parse(strDate);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return date;
   }

   public static StringBuffer readStringInInputStream(InputStream is) {
      StringBuffer strBuffer = new StringBuffer();
      byte[] buffer = null;
      int count = 0;
      try {
         do {
            buffer = new byte[1024];
            count = is.read(buffer, 0, buffer.length);
            strBuffer.append(new String(buffer));
         } while (count != -1);
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return strBuffer;
   }


   /**
    * Parse a String contrain some long to array.
    *   If some of long parse fail, this method will ignore it can continue.
    * @param str String
    * @param seperator seperator
    * @return ArrayList<Long>
    */
   public static ArrayList<Long> parseStringIds(String str,String seperator) {
      ArrayList<Long> result = new ArrayList<Long>();
      String[] ids =  str.split(seperator);
      for (String id : ids) {
         long l = 0;
         try {
            l = Long.parseLong(id);
         } catch (NumberFormatException e) {
            continue;
         }
         result.add(l);
      }
      return result;
   }
}
