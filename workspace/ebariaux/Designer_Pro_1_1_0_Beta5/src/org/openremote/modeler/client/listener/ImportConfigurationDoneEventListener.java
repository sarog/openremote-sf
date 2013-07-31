/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.client.listener;

import org.openremote.modeler.client.event.ImportConfigurationDoneEvent;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * Listener to handle event indicating successful import of complete OpenRemote configuration file.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public abstract class ImportConfigurationDoneEventListener implements Listener<ImportConfigurationDoneEvent> {

  /**
   * {@inheritDoc}
   * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
   */
  public void handleEvent(ImportConfigurationDoneEvent event) {
     if (event.getType() == ImportConfigurationDoneEvent.IMPORT_CONFIGURATION_DONE) {
       configurationImported(event);
     }
  }

  public abstract void configurationImported(ImportConfigurationDoneEvent event);

}