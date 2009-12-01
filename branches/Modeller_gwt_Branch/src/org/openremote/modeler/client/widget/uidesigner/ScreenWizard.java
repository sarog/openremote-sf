package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class ScreenWizard extends Dialog {
   
   public static final String SCREEN_NAME_FIELD = "name";
   
   
   
   private TextField<String> nameField = null;
   private BeanModel selectItem = null;
   private FormPanel form = new FormPanel();
   private boolean editMode = false;
   private TreePanel<BeanModel> groupSelectTree = null;
   public ScreenWizard(ScreenTab screenTab,BeanModel selectItem,boolean editMode){
      super();
      this.editMode = editMode;
      this.selectItem = selectItem;
      setSize(330, 270);
      setHeading("New Screen");
      setLayout(new FillLayout());
      setModal(true);
      createFields(screenTab);
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      setBodyBorder(false);
      add(form);
      show();
   }
   public ScreenWizard(ScreenTab screenTab,BeanModel selectItem){
      this(screenTab,selectItem,false);
   }
   
   
   public void createFields(final ScreenTab screenTab) {
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setLabelWidth(60);
      nameField = new TextField<String>();
      nameField.setAllowBlank(false);
      nameField.setFieldLabel("Name");
      nameField.setName(SCREEN_NAME_FIELD);
      
      AdapterField adapterField = new AdapterField(createGroupTreeView(screenTab));
      adapterField.setFieldLabel("Group");
      
      form.add(nameField);
      form.add(adapterField);

      addBeforHideListener(screenTab);
   }
   
   private void addBeforHideListener(final ScreenTab screenTab) {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         @Override
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
                  BeanModel groupModel = groupSelectTree.getSelectionModel().getSelectedItem();
                  if (groupModel == null || !(groupModel.getBean() instanceof GroupRef)||nameField.getValue()==null||nameField.getValue().trim().equals("")) {
                     if(editMode){
                        MessageBox.alert("New Screen Error", "Please input screen name.", null);
                     } else {
                        MessageBox.alert("New Screen Error", "Please input screen name and select a group.", null);
                     }
                     be.cancelBubble();
                     return;
                  }
                  Object bean = groupModel.getBean();
                  if (bean != null && bean instanceof GroupRef) {
                     GroupRef groupRef = (GroupRef) bean;
                     if (!editMode) {                                                           // new a screen.
                        createScreen(groupSelectTree, groupModel, groupRef);
                        
                     } else {                                                                   // update a screen.
                        ScreenRef screenRef = (ScreenRef) selectItem.getBean();
                        screenRef.getScreen().setName(nameField.getValue());
                        screenRef.setGroup(groupRef.getGroup());
                        fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
                     }
                  }
            }

         }

      });
   }

   private void createScreen(final TreePanel<BeanModel> groupSelectTree,
         BeanModel groupModel, GroupRef selectedGroup) {
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
//      String screenName = nameField.getValue() == null?"untitled":nameField.getValue().trim().equals("")?"untitled":nameField.getValue();
//      screen.setName(screenName);
      screen.setName(nameField.getValue());
      screen.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      BeanModelDataBase.screenTable.insert(screen.getBeanModel());
      screen.setAbsoluteLayout(true);
      ScreenRef screenRef = new ScreenRef(screen);
      selectedGroup.getGroup().addScreenRef(screenRef);
      fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
   }

   private ContentPanel createGroupTreeView(ScreenTab screenTab) {
      ContentPanel groupTreeContainer = new ContentPanel();
      groupTreeContainer.setHeaderVisible(false);
      groupTreeContainer.setSize(210, 150);
      groupTreeContainer.setLayout(new FitLayout());
      groupTreeContainer.setScrollMode(Scroll.AUTO);
      List<BeanModel> panels = BeanModelDataBase.panelTable.loadAll();
      groupSelectTree = buildGroupSelectTree(panels);
      groupTreeContainer.add(groupSelectTree);
      groupTreeContainer.setEnabled(!editMode);
      
      if (null != this.selectItem) {
         if (this.selectItem.getBean() instanceof GroupRef && !editMode) {
            groupSelectTree.getSelectionModel().select(selectItem, false);
         } else if(selectItem.getBean() instanceof ScreenRef && editMode){
            ScreenRef screenRef = (ScreenRef) selectItem.getBean();
            nameField.setValue(screenRef.getScreen().getName());
            BeanModel selectedGroup = TreePanelBuilder.buildPanelTree(screenTab).getStore().getParent(selectItem);
            groupSelectTree.getSelectionModel().select(selectedGroup, false);
         }
      }
      return groupTreeContainer;
   }

   private TreePanel<BeanModel> buildGroupSelectTree(List<BeanModel> panels) {
      TreeStore<BeanModel> groups = new TreeStore<BeanModel>();
      TreePanel<BeanModel> groupTree = TreePanelBuilder.buildPanelTree(groups);
      groups.add(panels, false);
      for (BeanModel panelModel : panels) {
         Panel panel = panelModel.getBean();
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for (GroupRef ref : groupRefs) {
            groups.add(panelModel, ref.getBeanModel(), false);
         }
      }
      return groupTree;
   }

   public BeanModel getSelectItem() {
      return selectItem;
   }

   public void setSelectItem(BeanModel selectItem) {
      this.selectItem = selectItem;
   }

   public boolean isEditMode() {
      return editMode;
   }

   public void setEditMode(boolean editMode) {
      this.editMode = editMode;
   }

   public BeanModel getSelectedGroupRefModel() {
      return (BeanModel)groupSelectTree.getSelectionModel().getSelectedItem();
   }
   public TreePanel<BeanModel> getGroupSelectTree() {
      return groupSelectTree;
   }
  
   
}
