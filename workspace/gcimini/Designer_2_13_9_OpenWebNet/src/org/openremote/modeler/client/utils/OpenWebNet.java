/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.modeler.client.utils;

import java.util.Map;

import org.openremote.modeler.client.rpc.OpenWebNetRPCService;
import org.openremote.modeler.client.rpc.OpenWebNetRPCServiceAsync;
import org.openremote.modeler.openwebnet.OpenWebNetWho;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class OpenWebNet. Used for get the OpenWebNet definition from xml file.
 */
public class OpenWebNet
{
   /** The m_instance. */
   private static Map<String, OpenWebNetWho> instanceMap;

   /** The Constant openWebNetService. */
   private static final OpenWebNetRPCServiceAsync openWebNetService = (OpenWebNetRPCServiceAsync) GWT.create(OpenWebNetRPCService.class);

   /**
    * Instantiates a new OpenWebNet.
    */
   private OpenWebNet() {}

   /**
    * Gets the single instance of OpenWebNet.
    *
    * @return single instance of OpenWebNet
    */
   public static synchronized Map<String, OpenWebNetWho> getInstance()
   {
      if (instanceMap == null)
      {
         openWebNetService.getOWNDefinition(new AsyncCallback<Map<String, OpenWebNetWho>>()
         {
            public void onFailure(Throwable caught)
            {
               MessageBox.info("Error", "Can't get OpenWebNet definition from xml file!", null);
            }
            public void onSuccess(Map<String, OpenWebNetWho> whos)
            {
               instanceMap = whos;
            }
         });
      }
      return instanceMap;
   }
}
