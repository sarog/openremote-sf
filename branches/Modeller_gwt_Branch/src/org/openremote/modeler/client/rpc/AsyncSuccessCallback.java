package org.openremote.modeler.client.rpc;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncSuccessCallback<T> implements AsyncCallback<T>{

   public void onFailure(Throwable caught) {
      MessageBox.alert("ERROR", caught.getLocalizedMessage(), null);
   }

   public abstract void onSuccess(T result);

}
