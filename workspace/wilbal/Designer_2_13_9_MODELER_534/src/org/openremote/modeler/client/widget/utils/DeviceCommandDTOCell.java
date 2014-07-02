package org.openremote.modeler.client.widget.utils;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DeviceCommandDTOCell extends AbstractCell<MacroItemDetailsDTO> {

  private static final Icons ICON = GWT.create(Icons.class);

  public DeviceCommandDTOCell() {
    }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context,
      MacroItemDetailsDTO value, SafeHtmlBuilder sb) {
    if (value != null) {
       String imageHtml;
       sb.appendHtmlConstant("<div>");
       if (value.getType()==MacroItemType.Delay) {
          imageHtml = ICON.delayIcon().getHTML();
       } else if (value.getType()==MacroItemType.Macro){
          imageHtml = ICON.macroIcon().getHTML();
       } else {
          imageHtml = ICON.deviceCmd().getHTML();
       }
       sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
       sb.appendEscaped(value.getDisplayName());
       sb.appendHtmlConstant("</div>");
    }
    
  }
  
}
