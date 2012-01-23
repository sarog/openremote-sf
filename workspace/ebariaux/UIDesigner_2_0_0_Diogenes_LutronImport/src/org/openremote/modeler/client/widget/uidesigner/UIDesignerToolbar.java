package org.openremote.modeler.client.widget.uidesigner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class UIDesignerToolbar extends Composite {

  private static UIDesignerToolbarUiBinder uiBinder = GWT.create(UIDesignerToolbarUiBinder.class);

  interface UIDesignerToolbarUiBinder extends UiBinder<Widget, UIDesignerToolbar> {
  }

  public UIDesignerToolbar() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiField
  Button leftAlignButton;
  @UiField
  Button middleAlignButton;
  @UiField
  Button rightAlignButton;

  public UIDesignerToolbar(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("leftAlignButton")
  void onClick(ClickEvent e) {
    Window.alert("Hello!");
  }

}
