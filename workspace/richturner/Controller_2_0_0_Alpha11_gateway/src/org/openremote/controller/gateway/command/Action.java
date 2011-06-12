/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway.command;

import java.util.Map;
import java.util.HashMap;
import org.jdom.Element;
/**
 * 
 * @author Rich Turner 2011-02-26
 */
public class Action
{
   /* The action value */
   private String value;
      
   /**
    * The type of action
    */
   private EnumCommandActionType type;
   
   /* The action arguments */
   private Map<String, String> args = new HashMap<String, String> ();
   
   /* Constructor */   
   public Action(String value, EnumCommandActionType type, Map<String, String> args) {
      this.value = value;
      this.type = type;
      this.args = args;
   }
   
   /* Return the action value */
   public String getValue() {
      return this.value;   
   }

   /* Return the action type */
   public EnumCommandActionType getType() {
      return this.type;
   }

   /* Return the action args */
   public Map<String, String> getArgs() {
      return this.args;   
   }
}