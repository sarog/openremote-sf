package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public interface TemplatePanel {

  public interface Presenter {
    void onTemplateClicked(Template template);
  }
  
  TreePanel<BeanModel> getTemplateTree();
  SelectionServiceExt<BeanModel> getSelectionService();

  Button getDeleteButton();
  Button getEditButton();

  Template getTemplateInEditing();
  void setTemplateInEditing(Template templateInEditing);

  ScreenTab getEditTabItem();
  void setEditTabItem(ScreenTab editTabItem);
  
  El mask(String message);
  void unmask();

  void setPresenter(Presenter presenter);
}
