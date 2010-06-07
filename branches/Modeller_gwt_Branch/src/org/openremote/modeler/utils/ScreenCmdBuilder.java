package org.openremote.modeler.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.SensorOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;

/**
 * This class is used by TemplateService to integrate all devices, commands, sensors, sliders, switches and reset gesture & navigate.  
 * @author javenzhang
 *
 */
public class ScreenCmdBuilder {
   private Collection<Screen> screens = new ArrayList<Screen>();
   
   private Set<Device> devices = new HashSet<Device>();
   private Set<DeviceCommand> deviceCommands= new HashSet<DeviceCommand>();
   private Set<Sensor> sensors = new HashSet<Sensor>();
   private Set<Slider> sliders = new HashSet<Slider>();
   private Set<Switch> switches = new HashSet<Switch>();
   private Set<DeviceMacro> macros = new HashSet<DeviceMacro>();
   
   
   public ScreenCmdBuilder (Collection<Screen> screens) {
      this.screens = screens;
      this.init();
   }

   @SuppressWarnings("unchecked")
   private void init() {
      UIComponentBox box = initUIComponentBox();

      devices = collectDevices();
      deviceCommands = collectDeviceCommands();
      sliders = collectSliders((Collection<UISlider>) (box.getUIComponentsByType(UISlider.class)));
      switches = collectSwitches((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));
      sensors = collectSensors();
      Collection<Gesture> gestures = collectGestures(screens);
      macros = collectMacros(box, gestures);
      Set<UIButton> uiButtons = (Set<UIButton>) box.getUIComponentsByType(UIButton.class);
      resetNavigateForButtons(uiButtons);
   }
   private UIComponentBox initUIComponentBox() {
      UIComponentBox box = new UIComponentBox();

      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUiComponent();
            box.add(component);
         }
         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               box.add(cell.getUiComponent());
            }
         }
      }

      return box;
   }
   
   private Set<DeviceCommand> collectDeviceCommands() {
      UIComponentBox box = initUIComponentBox();
      Set<DeviceCommand> uiCmds = new HashSet<DeviceCommand>();
      
      collectDeviceCommandsFromSlider(box, uiCmds);
      collectDeviceCommandsFromSwitch(box, uiCmds);
      collectDeviceCommandsFromButton(box, uiCmds);
      
      Collection<Gesture> gestures = collectGestures(screens);
      collectDeviceCommandsFromGesture(gestures,uiCmds);
      collectDeviceCommandsFromSensor(uiCmds);
      
      return uiCmds;
   }

   private void collectDeviceCommandsFromSensor(Set<DeviceCommand> uiCmds) {
      Collection<Sensor> sensors = collectSensors();
      for (Sensor sensor: sensors) {
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(sensor.getSensorCommandRef().getDeviceCommand())) {
               sensor.getSensorCommandRef().setDeviceCommand(cmd);
               sensor.setDevice(cmd.getDevice());
            }
         }
         uiCmds.add(sensor.getSensorCommandRef().getDeviceCommand());
      }
   }

   @SuppressWarnings("unchecked")
   private void collectDeviceCommandsFromButton(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<UIButton> buttons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);
      for (UIButton btn : buttons) {
         UICommand cmd = btn.getUiCommand();
         if (cmd != null) {
            if (cmd instanceof DeviceCommandRef) {
               DeviceCommandRef cmdRef = (DeviceCommandRef) cmd;
               makeSureCommandBeCollected(cmdRef,uiCmds);
            } else if (cmd instanceof DeviceMacroRef) {
               DeviceMacroRef macroRef = (DeviceMacroRef) cmd;
               DeviceMacro macro = macroRef.getTargetDeviceMacro();
               if (macro != null) {
                  Collection<DeviceCommandRef> cmds = macro.getDeviceCommandsRefs();
                  for (DeviceCommandRef cmdFromMacro : cmds) {
                     makeSureCommandBeCollected(cmdFromMacro,uiCmds);
                  }
               }
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void collectDeviceCommandsFromSwitch(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<Switch> switchs = collectSwitches((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));

      for (Switch switchToggle : switchs) {
         DeviceCommand onCmd = switchToggle.getSwitchCommandOnRef().getDeviceCommand();
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(onCmd)) {
               switchToggle.getSwitchCommandOnRef().setDeviceCommand(cmd);
            }
         }
         uiCmds.add(onCmd);
         DeviceCommand offCmd = switchToggle.getSwitchCommandOffRef().getDeviceCommand();
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(offCmd)) {
               switchToggle.getSwitchCommandOffRef().setDeviceCommand(cmd);
            }
         }
         uiCmds.add(offCmd);
      }
   }

   @SuppressWarnings("unchecked")
   private void collectDeviceCommandsFromSlider(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<Slider> sliders = collectSliders((Collection<UISlider>) box.getUIComponentsByType(UISlider.class));
      for (Slider slider : sliders) {
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(slider.getSetValueCmd().getDeviceCommand())) {
               slider.getSetValueCmd().setDeviceCommand(cmd);
            }
         }
         uiCmds.add(slider.getSetValueCmd().getDeviceCommand());
      }
   }

   private Set<Device> collectDevices() {
      Set<Device> devices = new HashSet<Device>();
      // Because UICommand like the Slider, Switch can only select DeviceCommand from one device and the DeviceCommand only belongs to one device, the UICommand are in the same device as the DeviceCommand they have selected.
      // Therefore, we can get all the device by the DeviceCommand without get device from UICommand. 
      Collection<DeviceCommand> deviceCmds = collectDeviceCommands();

      for (DeviceCommand cmd : deviceCmds ) {
         Device device = cmd.getDevice();
         if (devices.contains(device)) {
            for (Device dvc : devices) {
               if (dvc.equals(device)) {
                  cmd.setDevice(dvc);
               }
            }
         }
         devices.add(device);
      }
      return devices;
   }

   private Set<Slider> collectSliders(Collection<UISlider> uiSliders) {
      Set<Slider> sliders = new HashSet<Slider>();
      for (UISlider uiSlider : uiSliders ) {
         Slider slider = uiSlider.getSlider();
         if (slider != null) {
            for (Slider sld : sliders) {
               if (slider.equals(sld)) {
                  uiSlider.setSlider(sld);
               }
            }
            sliders.add(slider);
         }
      }
      return sliders;
   }

   private Set<Switch> collectSwitches(Collection<UISwitch> uiSwitchs) {
      Set<Switch> switches = new HashSet<Switch>();
      for (UISwitch uiSwitch : uiSwitchs) {
         Switch switchToggle = uiSwitch.getSwitchCommand();
         if (switchToggle != null) {
            for (Switch swh: switches) {
               if (switchToggle.equals(swh)) {
                  uiSwitch.setSwitchCommand(swh);
               }
            }
            switches.add(switchToggle);
         }
      }
      return switches;
   }

   private Set<Sensor> collectSensors() {
      Set<Sensor> sensors = new HashSet<Sensor>();
      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUiComponent();
            collectSenosrFromUIComponent(component, sensors);
         }
         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               UIComponent component = cell.getUiComponent();
               collectSenosrFromUIComponent(component, sensors);
            }
         }
      }

      return sensors;
   }

   private void collectSenosrFromUIComponent(UIComponent component, Set<Sensor> sensors) {
      if (component instanceof SensorOwner) {
         SensorOwner sensorOwner = (SensorOwner) component;
         Sensor s = sensorOwner.getSensor();
         if (s != null) {
            for (Sensor sensor : sensors) {
               if (sensor.equals(sensorOwner.getSensor())) {
                  sensorOwner.setSensor(sensor);
               }
            }
            initSensorLinker(component, sensorOwner);
            sensors.add(s);
         }
      }
   }

   private void initSensorLinker(UIComponent component,SensorOwner sensorOwner) {
      if (component != null ) {
         if(component instanceof UILabel ) {
            UILabel uiLabel = (UILabel) component;
            if (uiLabel.getSensorLink() == null) {
               uiLabel.setSensorLink(new SensorLink(sensorOwner.getSensor()));
            }
            uiLabel.getSensorLink().setSensor(sensorOwner.getSensor());
         } else if (component instanceof UIImage) {
            UIImage uiImage = (UIImage) component;
            if (uiImage.getSensorLink() == null) {
               uiImage.setSensorLink(new SensorLink(sensorOwner.getSensor()));
            }
            uiImage.getSensorLink().setSensor(sensorOwner.getSensor());
         }
      }
   }

   @SuppressWarnings("unchecked")
   private Set<DeviceMacro> collectMacros(UIComponentBox box,Collection<Gesture> gestures) {
      Set<DeviceMacro> macros = new HashSet<DeviceMacro>();
      Collection<UIButton> uiButtons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);
      for (UIButton btn : uiButtons) {
         collectMacroFromButton(btn, macros);
      }
      if (gestures != null && gestures.size() >0) {
         for (Gesture gesture : gestures) {
            collectMacrosFromGesture(gesture, macros);
         }
      }
      return macros;
   }

   private void collectMacrosFromGesture(Gesture gesture, Set<DeviceMacro> macros) {
      UICommand uiCmd = gesture.getUiCommand();
      if ( uiCmd !=null && uiCmd instanceof DeviceMacroRef) {
         DeviceMacroRef macroRef = (DeviceMacroRef) gesture.getUiCommand();
         collectMacrosFromMacroRef(macroRef, macros);
      }
   }

   private void collectMacroFromButton(UIButton btn, Set<DeviceMacro> macros) {
      UICommand uiCmd = btn.getUiCommand();
      if (uiCmd !=null && uiCmd instanceof DeviceMacroRef) {
         DeviceMacroRef macroRef = (DeviceMacroRef) btn.getUiCommand();
         collectMacrosFromMacroRef(macroRef, macros);
      }
   }

   private void collectMacrosFromMacroRef(DeviceMacroRef macroRef, Set<DeviceMacro> macros) {
      if (macroRef.getTargetDeviceMacro() != null) {
         DeviceMacro macro = macroRef.getTargetDeviceMacro();
         macros.add(macro);
         macros.addAll(macro.getSubMacros());
      }
   }
   
   private void makeSureCommandBeCollected(DeviceCommandRef cmdRef,Set<DeviceCommand> uiCmds) {
      for (DeviceCommand cmdInCommandSet : uiCmds) {
         if (cmdRef.getDeviceCommand().equals(cmdInCommandSet)) {
            cmdRef.setDeviceCommand(cmdInCommandSet);
         }
      }
      uiCmds.add(cmdRef.getDeviceCommand());
   }
   
   private Collection<Gesture> collectGestures(Collection<Screen> screens) {
      Collection<Gesture> gestures = new ArrayList<Gesture>();
      for(Screen screen: screens) {
         Collection<Gesture> gstures  = screen.getGestures();
         if (gstures != null && gstures.size() > 0) {
            gestures.addAll(gstures);
         }
      }
      return gestures;
   }
   
   private void collectDeviceCommandsFromGesture(Collection<Gesture> gestures,Set<DeviceCommand> uiCmds) {
      if (gestures != null && gestures.size() >0 ) {
         for (Gesture gesture : gestures) {
            UICommand cmd = gesture.getUiCommand();
            if (cmd != null) {
               if (cmd instanceof DeviceCommandRef) {
                  DeviceCommandRef cmdRef = (DeviceCommandRef) cmd;
                  makeSureCommandBeCollected(cmdRef, uiCmds);
               } else if (cmd instanceof DeviceMacroRef) {
                  DeviceMacroRef macroRef = (DeviceMacroRef) cmd;
                  DeviceMacro macro = macroRef.getTargetDeviceMacro();
                  if (macro != null) {
                     Collection<DeviceCommandRef> cmds = macro.getDeviceCommandsRefs();
                     for (DeviceCommandRef cmdFromMacro : cmds) {
                        makeSureCommandBeCollected(cmdFromMacro,uiCmds);
                     }
                  }
               }
            }
         }
      }
   }
   
   private void resetNavigateForButtons(Collection<UIButton> uiBtns) {
      if (uiBtns != null && uiBtns.size() >0 ) {
         for(UIButton uiBtn : uiBtns) {
            uiBtn.resetNavigate();
         }
      }
   }
   
   public Set<Device> getDevices() {
      return devices;
   }
   public Set<DeviceCommand> getDeviceCommands() {
      return deviceCommands;
   }
   public Set<Sensor> getSensors() {
      return sensors;
   }
   public Set<Slider> getSliders() {
      return sliders;
   }
   public Set<Switch> getSwitches() {
      return switches;
   }
   public Set<DeviceMacro> getMacros() {
      return macros;
   }
   
}
