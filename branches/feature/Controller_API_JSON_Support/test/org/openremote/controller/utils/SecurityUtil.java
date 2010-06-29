package org.openremote.controller.utils;

import javax.servlet.http.HttpServletRequest;

import org.dbunit.util.Base64;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;

public class SecurityUtil {
   
   public static WebRequest getSecuredRequest(WebConversation wc, String requestURL) {
      WebRequest pollingGetMethodRequest = new GetMethodWebRequest(requestURL);
      String usernameAndPassword = "dan:dan";
      String encodedUsernameAndPassword = Base64.encodeString(usernameAndPassword);
      pollingGetMethodRequest.setHeaderField("Authorization", HttpServletRequest.BASIC_AUTH + " " + encodedUsernameAndPassword);
      return pollingGetMethodRequest;
   }

}
