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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openremote.beehive.repo.DiffResult.Line;

/**
 * The Class HighlightUtil.
 * 
 * @author Tomsky
 */
public class HighlightUtil {

   /**
    * Instantiates a new highlight util.
    */
   private HighlightUtil() {
   }
   
   /**
    * Gets the lIRC highlight.
    * 
    * @param lines the lines
    * 
    * @return the lIRC highlight
    */
   public static List<String> getLIRCHighlight(List<String> lines){
      List<String> highLightLines = new ArrayList<String>();
      for (int i = 0; i < lines.size(); i++) {
         String line = StringEscapeUtils.escapeJava(StringEscapeUtils.escapeHtml(lines.get(i)));
         String trimLine = line.replace("\\t","    ").trim();
         
         if(trimLine.matches("\\s*")){ //""
            line = "<pre>&nbsp;</pre>";
         } else if(trimLine.startsWith("#")){  // comment
            line = "<pre class=\"comment\">"+line+"</pre>";
         } else if(trimLine.matches("begin\\s*remote|end\\s*remote|begin\\s*codes|end\\s*codes|begin\\s*raw_codes|end\\s*raw_codes")){ //keyword
            line = "<pre class=\"keyword\">"+line+"</pre>";
         } else if(trimLine.matches(getOptionKeyRegExp())){// options key
            String[] arr = trimLine.split("\\s+");
            line = "<pre>"+line.replaceFirst(arr[0], "<span class=\"keyname\">"+arr[0]+"</span>")+"</pre>";
         }else{
            String[] subStr = trimLine.split("\\s+");
            if(subStr.length >1 && subStr[1].startsWith("0x")){ //codes key
               line = line.replaceFirst(StringUtil.escapeRegexp(subStr[0]), "<span class=\"keyname\">"+subStr[0]+"</span>");
            }
            line = "<pre>"+line+"</pre>";
            
         }
         highLightLines.add(StringEscapeUtils.unescapeJava(line));
      }
      return highLightLines;
   }
   
   /**
    * Gets the option key reg exp.
    * 
    * @return the option key reg exp
    */
   private static String getOptionKeyRegExp(){
      String[] optionsKeys = new String[] { "name", "gap", "eps", "aeps", "flags", "bits", "zero", "one", "ptrail",
            "toggle_bit", "header", "pre_data_bits", "pre_data", "repeat", "min_repeat", "post_data_bits", "post_data",
            "plead", "frequency", "duty_cycle", "pre", "foot", "post", "three", "two", "repeat_gap", "tanmitter",
            "toggle_bit_mask", "repeat_bit", "toggle_mask", "rc6_mask", "serial_mode", "baud", "min_code_repeat" };
      StringBuffer optionKeyRegExp = new StringBuffer();
      for (int i = 0; i < optionsKeys.length; i++) {
         optionKeyRegExp.append(optionsKeys[i] + "\\s+.+");
         if (i < optionsKeys.length - 1) {
            optionKeyRegExp.append("|");
         }
      }
      return optionKeyRegExp.toString();
   }
   
   /**
    * Highlight diff lines.
    * 
    * @param lines the lines
    */
   public static void highlightDiffLines(List<Line> lines){
      List<String> newLines = new ArrayList<String>();
      for (Line line : lines) {
         newLines.add(line.getLine());
      }
      newLines = HighlightUtil.getLIRCHighlight(newLines);
      for (int i = 0; i < lines.size(); i++) {
         lines.get(i).setLine(newLines.get(i));
      }
   }
}
