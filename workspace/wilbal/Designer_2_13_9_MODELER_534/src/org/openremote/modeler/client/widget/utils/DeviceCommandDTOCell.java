package org.openremote.modeler.client.widget.utils;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DeviceCommandDTOCell extends AbstractCell<MacroItemDetailsDTO> {

  private static final Icons ICON = GWT.create(Icons.class);
  private final String imageHtml;

  public DeviceCommandDTOCell() {
    this.imageHtml = ICON.deviceCmd().getHTML();
    }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context,
      MacroItemDetailsDTO value, SafeHtmlBuilder sb) {
    if (value != null) {
       sb.appendHtmlConstant("<div style='margin:-1px -5px -5px -5px;' >");
       sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
       sb.appendEscaped(value.getDisplayName());
       sb.appendHtmlConstant("</div>");
    }
    
  }
  
}
