package org.openremote.modeler.client.gxtExtends;

import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class TreePanelDragSourceMacroDragExt extends TreePanelDragSource{

   public TreePanelDragSourceMacroDragExt(TreePanel tree) {
      super(tree);
   }

   @Override
   protected void onDragDrop(DNDEvent event) {

   }
}
