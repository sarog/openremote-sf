package org.openremote.modeler.client.widget.utils;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DeviceDTOCell extends AbstractCell<DeviceDTO> {

  private static final Icons ICON = GWT.create(Icons.class);
  private final String imageHtml;

  public DeviceDTOCell() {
    this.imageHtml = ICON.device().getHTML();
    }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context,
      DeviceDTO value, SafeHtmlBuilder sb) {
    if (value != null) {
      sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
      sb.appendEscaped(value.getDisplayName());
    }
    
  }
 

}
