package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Slider;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("slider.smvc")
public interface SliderRPCService extends RemoteService {
   
   void save(Slider slider);

   void delete(Slider slider);

   void update(Slider slider);

   List<Slider> loadAll();
}
