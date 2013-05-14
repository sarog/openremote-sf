/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.client.event;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.MacroDTO;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event indicating that macros have been deleted.
 * Event usually contains the list of macros that have been deleted,
 * but by convention, if the list is null or empty, we consider that all macros have been deleted.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class MacrosDeletedEvent  extends GwtEvent<MacrosDeletedEventHandler> {

  public static Type<MacrosDeletedEventHandler> TYPE = new Type<MacrosDeletedEventHandler>();

  private final ArrayList<MacroDTO> macros;

  public MacrosDeletedEvent() {
    super();
    this.macros = null;
  }
  
  public MacrosDeletedEvent(MacroDTO macro) {
    super();
    this.macros = new ArrayList<MacroDTO>();
    this.macros.add(macro);
  }
  
  public MacrosDeletedEvent(ArrayList<MacroDTO> macros) {
    super();
    this.macros = macros;
  }

  public ArrayList<MacroDTO> getMacros() {
    return macros;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<MacrosDeletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MacrosDeletedEventHandler handler) {
    handler.onMacrosDeleted(this);
  }

}