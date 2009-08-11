package org.openremote.modeler.client.widget;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

public class FormWindow extends Window {
   protected FormPanel form = new FormPanel();
   public FormWindow() {
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setButtonAlign(HorizontalAlignment.CENTER);
   }
   
   @Override
   public void show() {
      setFocusWidget(form.getWidget(0));
      super.show();
   }
   
}
