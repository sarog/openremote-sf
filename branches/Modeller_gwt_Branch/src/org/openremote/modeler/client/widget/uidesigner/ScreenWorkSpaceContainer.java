package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.touchpanel.TouchPanelGridDefinition;

import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class ScreenWorkSpaceContainer extends LayoutContainer {

   private Point absolutePosition = null;
   
   private LayoutContainer moveBackGround = new LayoutContainer();
   
   public ScreenWorkSpaceContainer(UIScreen screen) {
      TouchPanelGridDefinition touchPanelGridDefinition = screen.getTouchPanelDefinition().getGrid();
      setSize(touchPanelGridDefinition.getWidth(), touchPanelGridDefinition.getHeight());
      setBorders(true);
      addBtns(screen);
      moveBackGround.setStyleAttribute("background-color", "yellow");
      moveBackGround.setStyleAttribute("position", "relative");
      moveBackGround.hide();
      add(moveBackGround);
      
   }
   
   private void addBtns(UIScreen screen) {
      final ScreenWorkSpaceContainer tar = this;
      DropTarget target = new DropTarget(tar);
      target.addDNDListener(new DNDListener() {
         
         @Override
         public void dragMove(DNDEvent e) {
            Point position = getPosition(e);
            moveBackGround.setPosition(position.x, position.y);
            moveBackGround.show();
            super.dragMove(e);
         }

         @Override
         public void dragDrop(DNDEvent e) {
            if (absolutePosition == null) {
               absolutePosition = new Point(tar.getAbsoluteLeft(), tar.getAbsoluteTop());
            }
            LayoutContainer lay = e.getData();
            tar.add(lay);
            Point position = getPosition(e);
            lay.setPosition(position.x, position.y);
            moveBackGround.hide();
            layout();
            super.dragDrop(e);
         }
         
      });
      target.setGroup("test");
      if(!screen.isAbsoluteLayout()) {
         final LayoutContainer tt = new GridLayoutContainer(screen);
         add(tt);
         DragSource source = new DragSource(tt){
            @Override
            protected void onDragStart(DNDEvent event) {
               if (absolutePosition == null) {
                  absolutePosition = new Point(tar.getAbsoluteLeft(), tar.getAbsoluteTop());
               }
               moveBackGround.setSize(tt.getWidth(), tt.getHeight());
               Point mousePoint = event.getXY();
               Point distance = new Point(mousePoint.x - tt.getAbsoluteLeft(), mousePoint.y - tt.getAbsoluteTop());
               tt.setData("distance", distance);
               event.setData(tt);
               event.getStatus().setStatus(true);
               event.getStatus().update("drop here");
               
            }
         };
         source.setGroup("test");
      }
   }
   
   private Point getPosition(DNDEvent event) {
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer)event.getData()).getData("distance");
      int left = mousePoint.x - distance.x - absolutePosition.x;
      int top = mousePoint.y - distance.y - absolutePosition.y;
      return new Point(left, top);
   }
}
