package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

public interface SensorLinkOwner {

  SensorLink getSensorLink();
  void setSensorLink(SensorLink sensorLinker);

  void setSensorDTOAndInitSensorLink(SensorWithInfoDTO sensorDTO);

}
