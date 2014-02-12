package org.openremote.controller.protocol.isy994;

import java.util.StringTokenizer;

public class ISYInsteonValueParser {

   private InsteonDeviceAddress address;
   private String actionCodeString;
   private String value;

   // "[   1F 5 6F 1]       ST   0"
   public ISYInsteonValueParser(String input) {
      int indexofOpenBracket = input.indexOf("[");
      int indexofCloseBracket = input.indexOf("]");
      address = new InsteonDeviceAddress(input.substring(indexofOpenBracket + 1, indexofCloseBracket).trim());
      StringTokenizer tokenizer = new StringTokenizer(input.substring(indexofCloseBracket + 1), " ");
      if (tokenizer.countTokens() == 2) {
         actionCodeString = tokenizer.nextToken().trim();
         value = tokenizer.nextToken().trim();
      } else
         throw new IllegalArgumentException("Expect something like: '" + "[   1F 5 6F 1]       ST   0', got: " + input);

   }

   public String actionCode() {
      return actionCodeString;
   }

   public String value() {
      return value;
   }

   public InsteonDeviceAddress address() {
      return address;
   }

   public String toString() {
      return address + ":" + actionCodeString + ":" + value;
   }

   public static void main(String[] args) {
      System.out.println(new ISYInsteonValueParser("[   1F 5 6F 1]       ST   0").toString());

   }

}
