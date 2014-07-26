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
package org.openremote.modeler.openwebnet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class is used for containing <b>OpenWebNetDefinition</b>.</br>
 * The container defined as a hash map, it use OWN who as the key, openWebNetWho
 * as the value.</br></br>
 * 
 * @author Marco Miccini
 */
public class OpenWebNetDefinition implements Serializable
{
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 5945619609197354248L;

   /** The OWN definition. */
   private static Map<String, OpenWebNetWho> whos = new HashMap<String, OpenWebNetWho>();

   /**
    * Gets the OpenWebNet whos.
    *
    * @return the OpenWebNet whos
    */
   public  Map<String, OpenWebNetWho> getWhos()
   {
      return whos;
   }

   /**
    * Sets the OpenWebNet whos.
    * 
    * @param whos the OpenWebNet whos
    */
   public  void setWhos(Map<String, OpenWebNetWho> whos)
   {
      OpenWebNetDefinition.whos = whos;
   }


   /** The instance. */
   private static OpenWebNetDefinition instance = new OpenWebNetDefinition();

   private OpenWebNetDefinition() {}


   /**
    * Gets the single instance of OpenWebNetDefinition.
    *
    * @return single instance of OpenWebNetDefinition
    */
   public static synchronized OpenWebNetDefinition getInstance()
   {
      return instance;
   }

   /**
    * Gets an OpenWebNet who.
    * 
    * @param who the who
    * 
    * @return the OpenWebNet who
    */
   public OpenWebNetWho get(String who)
   {
      if (whos.containsKey(who))
      {
         return whos.get(who);
      }
      return null;
   }
}
