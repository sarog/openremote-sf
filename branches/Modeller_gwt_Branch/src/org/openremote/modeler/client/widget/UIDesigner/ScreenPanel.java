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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.gxtExtends.ScreenDropTarget;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.client.model.Position;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Button;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UICommand;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
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
public class ScreenPanel extends TabItem {

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
      setStyleAttribute("position", "relative");
      setText(screen.getName());
      setClosable(true);
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

      LayoutContainer iphoneContainer = new LayoutContainer();
      LayoutContainer tableWapper = new LayoutContainer();
      FlexTable iphoneTable = new FlexTable();
      iphoneContainer.setSize(269, 500);
      iphoneContainer.addStyleName("iphone-background");
      iphoneTable.setPixelSize(196, 294);
      tableWapper.add(iphoneTable);
//      tableWapper.setPosition(0, 0);
//      tableWapper.addStyleName("absolute");
      tableWapper.setStyleAttribute("paddingLeft", "35");
      tableWapper.setStyleAttribute("paddingTop", "105");
      iphoneTable.setCellPadding(0);
      iphoneTable.setCellSpacing(0);
      iphoneTable.addStyleName("panel-table");
      iphoneContainer.add(tableWapper);
      final int width = (int)Math.round(196.0/screen.getColumnCount());
      final int height = (int)Math.round(294.0/screen.getRowCount());
      DNDListener dndListener = new DNDListener() {
         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer)e.getDropTarget().getComponent();
            Button button = null;
            Object data = e.getData();
            if (data instanceof Button) {
               button = (Button) data;
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) e.getData();
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  button = new Button(IDUtil.nextID());
                  button.setLabel(dataModel.get("name").toString());
                  UICommand uiCommand = null;
                  if (dataModel.getBean() instanceof DeviceCommand) {
                     uiCommand = new DeviceCommandRef((DeviceCommand) dataModel.getBean());
                  } else if (dataModel.getBean() instanceof DeviceMacro) {
                     uiCommand = new DeviceMacroRef((DeviceMacro) dataModel.getBean());
                  }
                  button.setUiCommand(uiCommand);
               }
            }
            button.setPosition((Position)targetCell.getData("position"));
            screen.addButton(button);
            LayoutContainer cell = createCell(button, width, height);
            cell.setData("button", button);
//            Resizable resizable = new Resizable(panel);  
//            resizable.setDynamic(false);
//            resizable.addResizeListener(new ResizeListener(){
//
//               @Override
//               public void resizeEnd(ResizeEvent re) {
//                  
//                  LayoutContainer simple = (LayoutContainer)re.getComponent();
////                  System.out.println("height: "+simple.getHeight());
//                  int size = (int) Math.round(simple.getHeight()/49.0);
////                  System.out.println("size: "+size);
//                  simple.setHeight(size*49);
//               }
//
//               @Override
//               public void resizeStart(ResizeEvent re) {
//                  LayoutContainer simple = (LayoutContainer)re.getComponent();
//                  System.out.println(simple.getHeight());
////                  super.resizeStart(re);
//               }
//
//            });
            add(cell);
            DragSource source = new DragSource(cell) {
               @Override
               protected void onDragStart(DNDEvent event) {
                  // by default drag is allowed
                  event.setData(event.getDragSource().getComponent().getData("button"));
                  event.getStatus().setStatus(true);
                  event.getStatus().update(uiDesignerImages.iphoneBtn().createImage().getElement());
               }

               @Override
               protected void onDragDrop(DNDEvent event) {
                  Button button = event.getDragSource().getComponent().getData("button");
                  screen.deleteButton(button);
                  remove(event.getDragSource().getComponent());
                  super.onDragDrop(event);
               }
               
            };
//            source.addDNDListener(new DNDListener(){
//
//               public void dragMove(DNDEvent e) {
//                  LayoutContainer simple = (LayoutContainer)e.getDropTarget().getComponent();
//                  int size = (int) Math.round(simple.getSize().height/49.0);
//                  System.out.println(size);
//                  simple.setHeight(size*49);
//                  super.dragMove(e);
//               }
//            });
            source.setGroup(Constants.BUTTON_DND_GROUP);
            cell.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
            layout();
            super.dragDrop(e);
         }
      };
      for (int i=0; i < screen.getRowCount(); i++) {
         for (int j=0; j < screen.getColumnCount(); j++) {
            LayoutContainer cellCont = new LayoutContainer();
            cellCont.setSize(width, height);
            iphoneTable.setWidget(i, j, cellCont);
            cellCont.setData("position", new Position(i, j));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cellCont);
            dropTarget.setGroup(Constants.BUTTON_DND_GROUP);
            dropTarget.setOverStyle("backgroud-yellow");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if(screen.getButtons().size() > 0){
         List<Button> buttons = screen.getButtons();
         for (Button button : buttons) {
            Position pos = button.getPosition();
            LayoutContainer cellCont = createCell(button, width, height);
            cellCont.setData("button", button);
            cellCont.setPosition(35+49*pos.getPosY()+pos.getPosY()+1, 105+49*pos.getPosX()+pos.getPosX()+1);
            add(cellCont);
            DragSource source = new DragSource(cellCont) {
               @Override
               protected void onDragStart(DNDEvent event) {
                  // by default drag is allowed
                  event.setData(event.getDragSource().getComponent().getData("button"));
                  event.getStatus().setStatus(true);
                  event.getStatus().update(uiDesignerImages.iphoneBtn().createImage().getElement());
               } 

               @Override
               protected void onDragDrop(DNDEvent event) {
                  Button button = event.getDragSource().getComponent().getData("button");
                  screen.deleteButton(button);
                  remove(event.getDragSource().getComponent());
                  super.onDragDrop(event);
               }
               
            };
            layout();
            source.setGroup(Constants.BUTTON_DND_GROUP);
         }
      }
      add(iphoneContainer);
      
   }
   
   private LayoutContainer createCell(Button button, int width, int height) {
      LayoutContainer panel = new LayoutContainer();
      panel.setToolTip(button.getName());
      panel.setLayout(new BorderLayout());
      panel.setSize(width, height);
      panel.setBorders(false);
      panel.addStyleName("absolute");
      panel.addStyleName("cursor-move");
      Label nameLabel = new Label(button.getLabel());
      nameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      nameLabel.addStyleName("font-white");
      Image image = uiDesignerImages.iphoneBtn().createImage();
      panel.add(nameLabel, new BorderLayoutData(LayoutRegion.CENTER));
      panel.add(image, new BorderLayoutData(LayoutRegion.CENTER));
      return panel;
   }
   
   public Screen getScreen(){
      return screen;
   }
}
