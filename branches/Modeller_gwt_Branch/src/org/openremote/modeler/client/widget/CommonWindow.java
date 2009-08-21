package org.openremote.modeler.client.widget;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

public class CommonWindow extends Window{

   public CommonWindow() {
      super();
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
   }

}
