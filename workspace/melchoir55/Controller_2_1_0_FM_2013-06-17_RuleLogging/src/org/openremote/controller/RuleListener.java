/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
 * */

package org.openremote.controller;

import java.util.Map;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.openremote.controller.utils.Logger;

/**
 * This class logs Drools rule execution. It is intended to assist the user in debugging Drools
 * behavior.
 * @author Isaac Martin
 *
 */

public class RuleListener extends DefaultAgendaEventListener {

   private Logger log;
    
   public RuleListener(){
      super();
      log = Logger.getLogger(
         Constants.RUNTIME_EVENTPROCESSOR_LOG_CATEGORY + ".drools");
   }
   
   @Override
   public void beforeActivationFired(BeforeActivationFiredEvent ruleEvent){
      final Rule rule = ruleEvent.getActivation().getRule();
      String ruleName = rule.getName();
      String rulePackage = rule.getPackageName();
      Map<String, Object> metaData = rule.getMetaData();
      
      log.trace(String.format("Rule Activation Imminent: /n" +
      		                  "Rule: %s/n" +
      		                  "", ruleName));
      
   }
   
   public Logger getLogger(){
      return log;
   }
   
}
