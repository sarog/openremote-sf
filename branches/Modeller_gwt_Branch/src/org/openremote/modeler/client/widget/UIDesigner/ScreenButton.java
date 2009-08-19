package org.openremote.modeler.client.widget.UIDesigner;

import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.domain.UIButton;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ScreenButton extends LayoutContainer {
   
   private UIDesignerImages uiDesignerImages = GWT.create(UIDesignerImages.class);
   private Label nameLabel;
   public ScreenButton(UIButton button, int width, int height) {
      initial(button, width, height);
   }
   
   private void initial(UIButton button, int width, int height){
      setData("button", button);
      setToolTip(button.getName());
      setLayout(new BorderLayout());
      setSize(width, height);
//      setBorders(false);
      addStyleName("absolute");
      addStyleName("cursor-move");
      nameLabel = new Label(button.getLabel());
      nameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      nameLabel.addStyleName("font-white");
      Image image = uiDesignerImages.iphoneBtn().createImage();
      add(nameLabel, new BorderLayoutData(LayoutRegion.CENTER));
      add(image, new BorderLayoutData(LayoutRegion.CENTER));
   }
   
   public void setLabel(String label){
      nameLabel.setText(label);
   }
}
