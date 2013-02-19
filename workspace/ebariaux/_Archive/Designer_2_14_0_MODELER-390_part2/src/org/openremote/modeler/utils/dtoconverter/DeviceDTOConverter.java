package org.openremote.modeler.utils.dtoconverter;

import java.util.ArrayList;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

public class DeviceDTOConverter {

  public static DeviceDetailsWithChildrenDTO createDeviceDetailsWithChildrenDTO(Device device) {
    DeviceDetailsWithChildrenDTO deviceDTO = new DeviceDetailsWithChildrenDTO(device.getOid(), device.getName(), device.getVendor(), device.getModel());

    ArrayList<DeviceCommandDetailsDTO> deviceCommandDTOs = new ArrayList<DeviceCommandDetailsDTO>();
    for (DeviceCommand dc : device.getDeviceCommands()) {
      deviceCommandDTOs.add(dc.getDeviceCommandDetailsDTO());
    }
    deviceDTO.setDeviceCommands(deviceCommandDTOs);
    
    ArrayList<SensorDetailsDTO> sensorDTOs = new ArrayList<SensorDetailsDTO>();
    for (Sensor s : device.getSensors()) {
      sensorDTOs.add(s.getSensorDetailsDTO());
    }
    deviceDTO.setSensors(sensorDTOs);
    
    ArrayList<SwitchDetailsDTO> switchDTOs = new ArrayList<SwitchDetailsDTO>();
    for (Switch s : device.getSwitchs()) {
      switchDTOs.add(SwitchDTOConverter.createSwitchDetailsDTO(s));
    }

    ArrayList<SliderDetailsDTO> sliderDTOs = new ArrayList<SliderDetailsDTO>();
    for (Slider s : device.getSliders()) {
      sliderDTOs.add(s.getSliderDetailsDTO());
    }
    return deviceDTO;
  }
}
