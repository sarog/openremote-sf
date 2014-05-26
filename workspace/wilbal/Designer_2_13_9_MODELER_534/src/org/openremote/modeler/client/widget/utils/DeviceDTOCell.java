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
    super("dragstart","dragover","drop","dragleave");
    this.imageHtml = ICON.device().getHTML();
    }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context,
      DeviceDTO value, SafeHtmlBuilder sb) {
    if (value != null) {
      sb.appendHtmlConstant("<div draggable=\"true\">").appendHtmlConstant(imageHtml).appendEscaped(" ");
      sb.appendEscaped(value.getDisplayName()).appendHtmlConstant("</div>");
    }
    
  }
  
  @Override
  public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
      Element parent, DeviceDTO value, NativeEvent event,
      ValueUpdater<DeviceDTO> valueUpdater) {
    // TODO Auto-generated method stub
    event.getDataTransfer().setDragImage(parent, 10, 10);
    event.getDataTransfer().setData("deviceCommandId", Long.toString(value.getOid()));
  }

}
