package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Slider;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SliderRPCServiceAsync {

   void save(Slider slider, AsyncCallback<Void> callback);

   void delete(Slider slider, AsyncCallback<Void> callback);

   void update(Slider slider, AsyncCallback<Void> callback);

   void loadAll(AsyncCallback<List<Slider>> callback);

}
