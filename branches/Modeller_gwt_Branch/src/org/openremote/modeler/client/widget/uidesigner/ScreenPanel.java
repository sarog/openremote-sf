/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.gxtextends.ScreenDropTarget;
import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.client.model.Position;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.domain.UICommand;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * The Class ScreenPanel.
 */
public class ScreenPanel extends LayoutContainer {

   /** The screen. */
   private Screen screen;

   /** The ui designer images. */
   private UIDesignerImages uiDesignerImages = GWT.create(UIDesignerImages.class);
   
   private static final String POSITION = "position";
   
   private static final int TABLE_WIDTH = 196;
   private static final int TABLE_HEIGHT = 294;
   private static final int PADDING_LEFT = 35;
   private static final int PADDING_TOP = 105;
   
   /** The selected button. */
   private ScreenButton selectedButton;
   
   /**
    * Instantiates a new screen panel.
    * 
    * @param s the s
    */
   public ScreenPanel(Screen s) {
      screen = s;
      addStyleName("screen-background");
      setStyleAttribute("paddingLeft", String.valueOf(PADDING_LEFT));
      setStyleAttribute("paddingTop", String.valueOf(PADDING_TOP));
      createTable();
      
   }

   /**
    * Creates the table.
    */
   private void createTable() {
//      LayoutContainer tableWapper = new LayoutContainer();
      FlexTable iphoneTable = new FlexTable();
      iphoneTable.setCellPadding(0);
      iphoneTable.setCellSpacing(0);
      iphoneTable.addStyleName("panel-table");
      iphoneTable.setPixelSize(TABLE_WIDTH, TABLE_HEIGHT);
      add(iphoneTable);
      final int width = (int)Math.round((float)TABLE_WIDTH/screen.getColumnCount());
      final int height = (int)Math.round((float)TABLE_HEIGHT/screen.getRowCount());
      DNDListener dndListener = new DNDListener() {
         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer)e.getDropTarget().getComponent();
            UIButton button = null;
            Object data = e.getData();
            if (data instanceof UIButton) {
               button = (UIButton) data;
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) e.getData();
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  button = new UIButton(IDUtil.nextID());
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
            button.setPosition((Position)targetCell.getData(POSITION));
            screen.addButton(button);
            ScreenButton screenBtn = createScreenButton(width, height, button);
            screenBtn.addStyleName("button-border");
            if(selectedButton != null){
               selectedButton.removeStyleName("button-border");
            }
            selectedButton = screenBtn;
            Resizable resizable = new Resizable(screenBtn); 
            final Position sourcePosition = new Position(0, 0);
            resizable.addResizeListener(new ResizeListener(){

               @Override
               public void resizeEnd(ResizeEvent re) {
                  
                  LayoutContainer simple = (LayoutContainer)re.getComponent();
                  int lSize = (int) Math.round((sourcePosition.getPosX()-simple.getAbsoluteLeft())/49.0);
                  int tSize = (int) Math.round((sourcePosition.getPosY()-simple.getAbsoluteTop())/49.0);
                  simple.setPagePosition(sourcePosition.getPosX()-lSize*49-lSize, sourcePosition.getPosY()-tSize*49-tSize);
                  
                  int vSize = (int) Math.round(simple.getHeight()/49.0);
                  int hSize = (int) Math.round(simple.getWidth()/49.0);
                  simple.setHeight(vSize*49+vSize-1);
                  simple.setWidth(hSize*49+hSize-1);
               }

               @Override
               public void resizeStart(ResizeEvent re) {
                  LayoutContainer simple = (LayoutContainer)re.getComponent();
                  sourcePosition.setPosX(simple.getAbsoluteLeft());
                  sourcePosition.setPosY(simple.getAbsoluteTop());
//                  super.resizeStart(re);
               }

            });
            add(screenBtn);
            DragSource source = createDragSource(screenBtn);
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
            screenBtn.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
            layout();
            super.dragDrop(e);
         }
      };
      for (int i=0; i < screen.getRowCount(); i++) {
         for (int j=0; j < screen.getColumnCount(); j++) {
            LayoutContainer cell = new LayoutContainer();
            cell.setSize(width, height);
            iphoneTable.setWidget(i, j, cell);
            cell.setData(POSITION, new Position(i, j));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cell);
            dropTarget.setGroup(Constants.BUTTON_DND_GROUP);
            dropTarget.setOverStyle("background-color");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if(screen.getButtons().size() > 0){
         List<UIButton> buttons = screen.getButtons();
         for (UIButton button : buttons) {
            Position pos = button.getPosition();
            ScreenButton screenBtn = createScreenButton(width, height, button);
            screenBtn.setPosition(PADDING_LEFT+height*pos.getPosY()+pos.getPosY()+1, PADDING_TOP+width*pos.getPosX()+pos.getPosX()+1);
            add(screenBtn);
            DragSource source = createDragSource(screenBtn);
            layout();
            source.setGroup(Constants.BUTTON_DND_GROUP);
         }
      }
      
   }

   /**
    * Creates the drag source.
    * 
    * @param screenBtn the screen btn
    * 
    * @return the drag source
    */
   private DragSource createDragSource(ScreenButton screenBtn) {
      DragSource source = new DragSource(screenBtn) {
         @Override
         protected void onDragStart(DNDEvent event) {
            // by default drag is allowed
            event.setData(event.getDragSource().getComponent().getData(ScreenButton.DATA_BUTTON));
            event.getStatus().setStatus(true);
            event.getStatus().update(uiDesignerImages.iphoneBtn().createImage().getElement());
         } 

         @Override
         protected void onDragDrop(DNDEvent event) {
            UIButton button = event.getDragSource().getComponent().getData(ScreenButton.DATA_BUTTON);
            screen.deleteButton(button);
            remove(event.getDragSource().getComponent());
            super.onDragDrop(event);
         }
         
      };
      return source;
   }

   /**
    * Creates the screen button.
    * 
    * @param width the width
    * @param height the height
    * @param button the button
    * 
    * @return the screen button
    */
   private ScreenButton createScreenButton(final int width, final int height, UIButton button) {
      ScreenButton screenBtn = new ScreenButton(button, width, height){
         @Override
         public void onBrowserEvent(Event event) {
            if(event.getTypeInt() == Event.ONMOUSEDOWN){
               this.addStyleName("button-border");
               if(selectedButton != null && (ScreenButton)this != selectedButton){
                  selectedButton.removeStyleName("button-border");
               }
               selectedButton = (ScreenButton)this;
            }
            super.onBrowserEvent(event);
         }
      };
      return screenBtn;
   }
   
   /**
    * Gets the selected button.
    * 
    * @return the selected button
    */
   public ScreenButton getSelectedButton(){
      return selectedButton;
   }
}
