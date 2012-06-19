package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.domain.BusinessEntity;

public interface ScreenPanel {
  
  public interface Presenter {
    
    void onRightKeyPressed();
    void onLeftKeyPressed();
    void onUpKeyPressed();
    void onDownKeyPressed();
    
  }

  ScreenTab getScreenItem();
  void setScreenItem(ScreenTab screenItem);
  void closeCurrentScreenTab();
  boolean isShiftKeyDown();
  
  void onUIElementEdited(BusinessEntity element);

  void setPresenter(Presenter presenter);
}
