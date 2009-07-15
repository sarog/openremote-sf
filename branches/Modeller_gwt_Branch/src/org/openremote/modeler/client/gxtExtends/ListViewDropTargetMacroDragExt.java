package org.openremote.modeler.client.gxtExtends;

import java.util.List;

import org.openremote.modeler.client.model.DeviceMacroItemModel;
import org.openremote.modeler.client.model.TreeDataModel;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.user.client.Element;

public class ListViewDropTargetMacroDragExt extends ListViewDropTarget {

   public ListViewDropTargetMacroDragExt(ListView listView) {
      super(listView);
   }

   private boolean before;

   @Override
   protected void onDragDrop(DNDEvent e) {
      boolean handle = false;
      int idx = listView.getStore().indexOf(activeItem);
      if (!before) idx++;
      if (e.getData() instanceof List) {
         List<ModelData> models = (List<ModelData>) e.getData();
         for (ModelData modelData : models) {
            if (modelData.get("model") instanceof TreeDataModel) {
               TreeDataModel treeDataModel = modelData.get("model");
               if (treeDataModel.getData() instanceof DeviceCommand) {
                  DeviceCommand command = (DeviceCommand) treeDataModel.getData();
                  DeviceCommandRef commandRef = new DeviceCommandRef(command);
                  DeviceMacroItemModel itemModel = new DeviceMacroItemModel(command.getName(), commandRef);
                  listView.getStore().insert(itemModel, idx);
                  handle = true;
               } else if (treeDataModel.getData() instanceof DeviceMacro) {
                  DeviceMacro deviceMacro = (DeviceMacro) treeDataModel.getData();
                  DeviceMacroRef deviceMacroRef = new DeviceMacroRef(deviceMacro);
                  DeviceMacroItemModel itemModel = new DeviceMacroItemModel(deviceMacro.getName(), deviceMacroRef);
                  listView.getStore().insert(itemModel, idx);
                  handle = true;
               }
            }
         }
      }
      if (!handle) {
         super.onDragDrop(e);
      }
   }

   // All the code below is just copy&paste , only for before attribute.
   @Override
   protected void showFeedback(DNDEvent event) {
      if (feedback == Feedback.INSERT) {
         event.getStatus().setStatus(true);
         Element row = listView.findElement(event.getTarget()).cast();

         if (row == null && listView.getStore().getCount() > 0) {
            row = listView.getElement(listView.getStore().getCount() - 1).cast();
         }

         if (row != null) {
            int height = row.getOffsetHeight();
            int mid = height / 2;
            mid += row.getAbsoluteTop();
            int y = event.getClientY();
            before = y < mid;
            int idx = listView.indexOf(row);
            insertIndex = before ? idx : idx + 1;
            activeItem = listView.getStore().getAt(idx);
            if (before) {
               showInsert(event, row, true);
            } else {
               showInsert(event, row, false);
            }
         } else {
            insertIndex = 0;
         }
      }
   }

   private void showInsert(DNDEvent event, Element row, boolean before) {
      Insert insert = Insert.get();
      insert.setVisible(true);
      Rectangle rect = El.fly(row).getBounds();
      int y = !before ? (rect.y + rect.height - 4) : rect.y - 2;
      insert.el().setBounds(rect.x, y, rect.width, 6);
   }

}
