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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.file.LIRCElement;

/**
 * The Class LIRCrawler.
 * 
 * @author Tomsky
 */
public class LIRCrawler {
   
   private static HttpClient httpClient = createHttpClient();
   
   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(LIRCrawler.class.getName());
   
   /** The Constant TR_REGEX. */
   private final static String TR_REGEX = "<tr><td valign=\"top\">" +
   		"<img src=\"/icons/(folder|text|script)\\.gif\" alt=\"\\[[\\s\\w]+\\]\">" +
   		"</td><td><a href=\"(.*?)/?\">.*?/?</a></td><td align=\"right\">" +
   		"(\\d\\d-\\w\\w\\w-\\d\\d\\d\\d\\s\\d\\d:\\d\\d)  </td>" +
   		"<td align=\"right\">\\s*[-\\w\\.]+\\s*</td></tr>";
   
   
   private static HttpClient createHttpClient(){
      HttpClient httpClient = new HttpClient();
      httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
      return httpClient;
   }
   
   /**
    * List.
    * 
    * @param lircUrl the lirc url
    * 
    * @return the list< lirc element>
    */
   public static List<LIRCElement> list(String lircUrl){
      List<LIRCElement> lircs = new ArrayList<LIRCElement>();
      Pattern pattern = Pattern.compile(TR_REGEX);
      Matcher matcher = pattern.matcher(getPageContent(lircUrl));
      while (matcher.find()) {
         LIRCElement lirc = new LIRCElement();
         String path = StringEscapeUtils.unescapeHtml(matcher.group(2));
         if(!FileUtil.isImage(path)){
            if (matcher.group(1).equals("text") || matcher.group(1).equals("script")) {
               lirc.setModel(true);
            }
            lirc.setPath(StringUtil.appendFileSeparator(lircUrl) + path);
            lirc.setUploadDate(matcher.group(3));
            lircs.add(lirc);
         }
      }
      return lircs;
   }
   
   /**
    * Write model.
    * 
    * @param lirc the lirc
    */
   public static void writeModel(LIRCElement lirc) {
      String modelContent = getPageContent(lirc.getPath());
      if(!modelContent.equals("")){
         FileUtil.writeStringToFile(modelContent, 
               StringUtil.appendFileSeparator(FileUtil.configuration.getWorkCopyDir())+lirc.getRelativePath());
      }
   }
   
   /**
    * Gets the page content. When the network is bad, retry 100 times.
    * 
    * @param url the url
    * 
    * @return the page content
    */
   private static String getPageContent(String url){
      String content = null;
      int retryCount = -1;
      while(content == null){
         content = getHtmlBody(url);
         retryCount++;
         if(retryCount > 100){
            content = "";
         }else if(retryCount != 0){
            LOGGER.error("try " + url + " " + retryCount +" times.");
            try {
               Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
      return content;
   }
   
   /**
    * Gets the html body.
    * 
    * @param url the url
    * 
    * @return the html body
    */
   private static String getHtmlBody(String url){
      String responseBody = "";
      GetMethod getMethod = new GetMethod(url);
      getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
      getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
      try {
         int statusCode = httpClient.executeMethod(getMethod);
         if (statusCode != HttpStatus.SC_OK) {
            LOGGER.error("Method failed: " + getMethod.getStatusLine());
         }
         responseBody = getMethod.getResponseBodyAsString();
      } catch (HttpException e) {
         LOGGER.error("Please check your provided http address " + url, e);
         return null;
      } catch (IOException e) {
         LOGGER.error("Occur the network exception, maybe the url [" + url + "] is unreachable.", e);
         return null;
      } finally {
         getMethod.releaseConnection();
      }
      return responseBody;
   }
   
}
