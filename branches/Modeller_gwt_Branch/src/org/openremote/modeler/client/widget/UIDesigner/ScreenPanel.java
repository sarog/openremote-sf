/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.UIDesigner;

import java.util.List;

import org.openremote.modeler.client.gxtExtends.ScreenDropTarget;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * The Class ScreenPanel.
 */
public class ScreenPanel extends LayoutContainer {

   /** The screen. */
   private Screen screen;

   /** The designer images. */
   private Icons icons = GWT.create(Icons.class);
   private UIDesignerImages uiDesignerImages = GWT.create(UIDesignerImages.class);

   /**
    * Instantiates a new screen panel.
    * 
    * @param s
    *           the s
    */
   public ScreenPanel(Screen s) {
      screen = s;
      setLayout(new FlowLayout());
      createToolBar();

      createScreenBackground();

   }

   /**
    * Creates the tool bar.
    */
   private void createToolBar() {

   }

   /**
    * Creates the screen background.
    */
   private void createScreenBackground() {

//       Image image = uiDesignerImages.iphone_background().createImage();
      LayoutContainer iphoneContainer = new LayoutContainer();
      LayoutContainer tableWapper = new LayoutContainer();
      FlexTable iphoneTable = new FlexTable();
      // panel.add(image);
      iphoneContainer.setSize(269, 500);
      iphoneContainer.addStyleName("iphone-background");
      iphoneTable.setPixelSize(196, 294);
      tableWapper.add(iphoneTable);
      tableWapper.setPosition(35, 105);
      tableWapper.addStyleName("absolute");
      iphoneTable.setCellPadding(0);
      iphoneTable.setCellSpacing(0);
//      flexTable.setBorderWidth(1);
      iphoneTable.addStyleName("panel-table");
      iphoneContainer.add(tableWapper);
      for (int i=0; i < 6; i++) {
         for (int j=0; j <4; j++) {
            final LayoutContainer cellCont = new LayoutContainer();
            cellCont.setSize(49, 49);
            iphoneTable.setWidget(i, j, cellCont);
            
            ScreenDropTarget dropTarget = new ScreenDropTarget(cellCont);
            dropTarget.setOverStyle("backgroud-yellow");
            dropTarget.addDNDListener(new DNDListener() {
               @SuppressWarnings("unchecked")
               public void dragDrop(DNDEvent e) {
                  Object data = e.getData();
                  BeanModel dataModel = null;
                  if (data instanceof BeanModel) {
                     dataModel = (BeanModel)data;
                  } else if (data instanceof List) {
                     List<ModelData> models = (List<ModelData>) e.getData();
                     if(models.size()>0){
                        dataModel = models.get(0).get("model");
                     }
                  }
                  LayoutContainer targetCell = (LayoutContainer)e.getDropTarget().getComponent();
                  final LayoutContainer button = createBtn(dataModel.get("name").toString());
                  button.setSize(49, 49);
                  button.setBorders(false);
                  button.addStyleName("absolute");
                  button.setData("command", dataModel);
//                  Resizable resizable = new Resizable(panel);  
//                  resizable.setDynamic(false);
//                  resizable.addResizeListener(new ResizeListener(){
//
//                     @Override
//                     public void resizeEnd(ResizeEvent re) {
//                        
//                        LayoutContainer simple = (LayoutContainer)re.getComponent();
////                        System.out.println("height: "+simple.getHeight());
//                        int size = (int) Math.round(simple.getHeight()/49.0);
////                        System.out.println("size: "+size);
//                        simple.setHeight(size*49);
//                     }
//
//                     @Override
//                     public void resizeStart(ResizeEvent re) {
//                        LayoutContainer simple = (LayoutContainer)re.getComponent();
//                        System.out.println(simple.getHeight());
////                        super.resizeStart(re);
//                     }
//
//                  });
                  add(button);
                  DragSource source = new DragSource(button) {
                     @Override
                     protected void onDragStart(DNDEvent event) {
                        // by default drag is allowed
                        event.setData(event.getDragSource().getComponent().getData("command"));
                        event.getStatus().setStatus(true);
                        event.getStatus().update(uiDesignerImages.iphoneBtn().createImage().getElement());
                     }

                     @Override
                     protected void onDragDrop(DNDEvent event) {
                        remove(event.getDragSource().getComponent());
                        super.onDragDrop(event);
                     }
                     
                  };
//                  source.addDNDListener(new DNDListener(){
//
//                     public void dragMove(DNDEvent e) {
//                        LayoutContainer simple = (LayoutContainer)e.getDropTarget().getComponent();
//                        int size = (int) Math.round(simple.getSize().height/49.0);
//                        System.out.println(size);
//                        simple.setHeight(size*49);
//                        super.dragMove(e);
//                     }
//                     
//                  });
                  
                  button.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
                  layout();
                  super.dragDrop(e);
               }

            });
            add(iphoneContainer);
         }
      }
      
   }
   
   private LayoutContainer createBtn(String name) {
      LayoutContainer panel = new LayoutContainer();
      panel.setLayout(new BorderLayout());
      Label nameLabel = new Label(name);
      nameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      nameLabel.addStyleName("font-white");
      Image image = uiDesignerImages.iphoneBtn().createImage();
      panel.add(nameLabel, new BorderLayoutData(LayoutRegion.CENTER));
      panel.add(image, new BorderLayoutData(LayoutRegion.CENTER));
      return panel;
   }

}
