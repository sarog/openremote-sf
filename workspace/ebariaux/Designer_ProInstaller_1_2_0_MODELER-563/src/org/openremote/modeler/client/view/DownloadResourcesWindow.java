package org.openremote.modeler.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class DownloadResourcesWindow extends DialogBox {

  private static DownloadResourcesWindowUiBinder uiBinder = GWT.create(DownloadResourcesWindowUiBinder.class);

  interface DownloadResourcesWindowUiBinder extends UiBinder<DialogBox, DownloadResourcesWindow> {
  }

  @UiField
  Button okButton;

  @UiFactory
  DialogBox itself() {
    return this;
  }

  public DownloadResourcesWindow() {
    uiBinder.createAndBindUi(this);
/*    importButton.setEnabled(false);
    mainLayout.setSize("50em", "20em");*/
    center();
  }

  @UiHandler("okButton")
  public void okClicked(ClickEvent event) {
    this.hide();
  }
}
