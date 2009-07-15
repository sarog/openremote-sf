/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.utils;

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolService;
import org.openremote.modeler.client.rpc.ProtocolServiceAsync;
import org.openremote.modeler.protocol.ProtocolDefinition;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Auto-generated Javadoc
/**
 * The Class Protocols. Used for get all protocol definitions from xml files.
 */
public class Protocols {
   
   /** The m_instance. */
   private static Map<String, ProtocolDefinition> m_instance;
   
   /** The Constant protocolService. */
   private static final ProtocolServiceAsync protocolService = (ProtocolServiceAsync) GWT.create(ProtocolService.class);
   
   /**
    * Instantiates a new protocols.
    */
   private Protocols() {
   }
   
   /**
    * Gets the single instance of Protocols.
    * 
    * @return single instance of Protocols
    */
   public synchronized static Map<String, ProtocolDefinition> getInstance() {
      if (m_instance == null) {
         protocolService.getProtocols(new AsyncCallback<Map<String, ProtocolDefinition>>() {
            public void onFailure(Throwable caught) {
               caught.printStackTrace();
               MessageBox.info("Error", "Can't get protocols from xml file!", null);
            }
            public void onSuccess(Map<String, ProtocolDefinition> protocols) {
               m_instance = protocols;
            }
         });
      }
      return m_instance;
   }
}
