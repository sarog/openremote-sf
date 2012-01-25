package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.domain.BusinessEntity;

public interface ScreenPanel {
  
  public interface Presenter {
    
  }

  ScreenTab getScreenItem();
  void setScreenItem(ScreenTab screenItem);
  void closeCurrentScreenTab();
  void onUIElementEdited(BusinessEntity element);

  void setPresenter(Presenter presenter);
}
