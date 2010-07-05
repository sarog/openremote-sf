/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.client.polling;


import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * The Class SimpleScriptTagProxy.
 */
public class SimpleScriptTagProxy {

   private static int ID = 0;
   private String url;
   private Element head = XDOM.getHead();
   private JsonResultReader reader;
   
   public SimpleScriptTagProxy(String url, JsonResultReader reader) {
     this.url = url;
     this.reader = reader;
   }

   /**
    * The method is for create a cross-domain method to controller, and get jsonp data.
    */
   public void load() {

     String transId = "transId" + ID++;
     String prepend = url.indexOf("?") != -1 ? "&" : "?";
     String u = url + prepend + "callback=" + transId;

     createCallback(this, transId);

     Element script = DOM.createElement("script");
     script.setAttribute("src", u);
     script.setAttribute("id", transId);
     script.setAttribute("type", "text/javascript");
     script.setAttribute("language", "JavaScript");

     head.appendChild(script);
   }

   protected void destroyTrans(String id) {
     head.removeChild(DOM.getElementById(id));
   }

   protected void onReceivedData(String transId, JavaScriptObject jso) {
      reader.read(new JSONObject(jso));
      destroyTrans(transId);
   }

   private native void createCallback(SimpleScriptTagProxy proxy, String transId) /*-{
       cb = function( j ){
       proxy.@org.openremote.web.console.client.polling.SimpleScriptTagProxy::onReceivedData(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(transId, j);
       };
       $wnd[transId]=cb;
       }-*/;

}
