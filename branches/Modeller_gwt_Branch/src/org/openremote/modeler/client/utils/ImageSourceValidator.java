package org.openremote.modeler.client.utils;


public class ImageSourceValidator {
//   public static final String CHROME_NAVIGATOR_NAME = "CHROME";
   public static String validate(String resultHtml) {
      String result = resultHtml;
      if (resultHtml != null) {
         result = resultHtml.replaceAll("^<pre[^>]*>", "").replaceAll("</pre>$", "");
      }
      System.out.println("result : "+result);
      return result;
   }
   
}
