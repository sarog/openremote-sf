package org.openremote.modeler.client.widget.uidesigner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportConfigurationWindow extends DialogBox {

  private static ImportConfigurationWindowUiBinder uiBinder = GWT.create(ImportConfigurationWindowUiBinder.class);

  interface ImportConfigurationWindowUiBinder extends UiBinder<Widget, ImportConfigurationWindow> {
  }

  @UiFactory
  DialogBox itself() {
    return this;
  }

  public ImportConfigurationWindow() {
    uiBinder.createAndBindUi(this);
  }

  @UiField FormPanel form;
  /*
  @UiHandler("importButton")
  void importConfiguration(ClickEvent event) {
    form.submit();
  }
  
  @UiHandler("cancelButton")
  void cancel(ClickEvent event) {
    this.hide();
  }*/

}
