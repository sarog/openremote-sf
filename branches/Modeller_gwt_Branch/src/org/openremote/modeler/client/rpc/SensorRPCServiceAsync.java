package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Sensor;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SensorRPCServiceAsync {

   void saveSensor(Sensor sensor, AsyncCallback<Sensor> async);
   
   void updateSensor(Sensor sensor, AsyncCallback<Sensor> async);
   
   void deleteSensor(long id, AsyncCallback<Boolean> async);
   
   void loadAll(AsyncCallback<List<Sensor>> async);
   
   void getById(long id, AsyncCallback<Sensor> async);
}
