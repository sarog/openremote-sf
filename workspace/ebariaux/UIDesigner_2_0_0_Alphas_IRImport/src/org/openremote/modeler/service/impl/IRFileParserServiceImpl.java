package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.exception.IrFileParserException;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.IRFileParserService;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.ir.codes.IRCode;
import com.tinsys.ir.codes.InvalidIRCodeException;
import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.ir.representations.gc.GCIRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RawIRCodeRepresentationHandler;
import com.tinsys.pronto.irfiles.ProntoFileParser;

public class IRFileParserServiceImpl extends BaseAbstractService<DeviceCommand>
      implements IRFileParserService {

   private ProntoFileParser prontoFileParser;
   private List<IRCommand> currentIRCommands;

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#setXcfFileParser(com
    * .tinsys.pronto.irfiles.XCFFileParser)
    */
   public void setProntoFileParser(ProntoFileParser prontoFileParser) {
      this.prontoFileParser = prontoFileParser;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#getDevices(org.openremote
    * .modeler.irfileparser.BrandInfo)
    */
   @Override
   public List<DeviceInfo> getDevices(BrandInfo brand) {

      List<DeviceInfo> deviceInfo = new ArrayList<DeviceInfo>();
      Brand b = new Brand(brand.getBrandName());

      List<Device> devices = prontoFileParser.getDevices(b);
      for (Device device : devices) {
         deviceInfo.add(new DeviceInfo(new BrandInfo(device.getBrand()
               .getBrandName()), device.getModelName()));
      }
      return deviceInfo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#getCodeSets(org.openremote
    * .modeler.irfileparser.DeviceInfo)
    */
   @Override
   public List<CodeSetInfo> getCodeSets(DeviceInfo di) {
      List<CodeSetInfo> codeSetInfo = new ArrayList<CodeSetInfo>();

      Device d = new Device(new Brand(di.getBrandInfo().getBrandName()),
            di.getModelName());

      List<CodeSet> codeSets = prontoFileParser.getCodeSets(d);
      int index = 0;
      for (CodeSet codeSet : codeSets) {
         codeSetInfo.add(new CodeSetInfo(di, codeSet.getDescription(), codeSet
               .getCategory(), index));
         index++;
      }
      return codeSetInfo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.IRFileParserService#getBrands()
    */
   @Override
   public List<BrandInfo> getBrands() {
      if (prontoFileParser != null) {
         List<BrandInfo> brandInfo = new ArrayList<BrandInfo>();
         List<Brand> brands = prontoFileParser.getBrands();
         for (Brand brand : brands) {
            brandInfo.add(new BrandInfo(brand.getBrandName()));
         }
         return brandInfo;
      } else {
         return null;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.IRFileParserService#getIRCommands(org.
    * openremote.modeler.irfileparser.CodeSetInfo)
    */
   @Override
   public List<IRCommandInfo> getIRCommands(CodeSetInfo csi) {
      List<IRCommandInfo> iRCommandInfo = new ArrayList<IRCommandInfo>();
      Device d = new Device(new Brand(csi.getDeviceInfo().getBrandInfo()
            .getBrandName()), csi.getDeviceInfo().getModelName());
      List<IRCommand> iRcommands = prontoFileParser.getCodeSets(d)
            .get(csi.getIndex().intValue()).getIRCommands();

      for (IRCommand irCommand : iRcommands) {
         IRCommandInfo iRCI;
         if (irCommand.getCode() != null) {
            iRCI = new IRCommandInfo(irCommand.getName(), irCommand.getCode()
                  .toString(), irCommand.getOriginalCodeString(),
                  irCommand.getComment(), csi);
         } else {
            iRCI = new IRCommandInfo(irCommand.getName(), null,
                  irCommand.getOriginalCodeString(), irCommand.getComment(),
                  csi);
         }
         iRCommandInfo.add(iRCI);
      }
      return iRCommandInfo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#saveCommands(org.openremote
    * .modeler.domain.Device, org.openremote.modeler.irfileparser.GlobalCache,
    * org.openremote.modeler.irfileparser.IRTrans, java.util.List)
    */
   @Override
   @Transactional
   public List<DeviceCommand> saveCommands(
         org.openremote.modeler.domain.Device device, GlobalCache globalCache,
         IRTrans irTrans, List<IRCommandInfo> selectedFunctions)
         throws IrFileParserException {

      CodeSetInfo csi = selectedFunctions.get(0).getCodeSet();
      CodeSet cs = new CodeSet(csi.getDeviceInfo().getBrandInfo()
            .getBrandName(), csi.getDeviceInfo().getModelName());
      currentIRCommands = prontoFileParser.getCodeSets(cs.getDevice())
            .get(csi.getIndex()).getIRCommands();
      List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
      for (IRCommandInfo irCommandInfo : selectedFunctions) {
         Protocol protocol = new Protocol();
         if (globalCache != null) {
            protocol.setType("TCP/IP");
            ProtocolAttr ipAttr = new ProtocolAttr();
            ipAttr.setName("ipAddress");
            ipAttr.setValue(globalCache.getIpAddress());
            ipAttr.setProtocol(protocol);
            protocol.getAttributes().add(ipAttr);
            ProtocolAttr portAttr = new ProtocolAttr();
            portAttr.setName("port");
            portAttr.setValue(globalCache.getTcpPort());
            portAttr.setProtocol(protocol);
            protocol.getAttributes().add(portAttr);
            ProtocolAttr commandAttr = new ProtocolAttr();
            commandAttr.setName("command");
            IRCode currentCom = null;
            String codeString = "";
            for (IRCommand commands : currentIRCommands) {
               if (commands.getName().equals(irCommandInfo.getName())
                     && commands.getOriginalCodeString().equals(
                           irCommandInfo.getOriginalCodeString())) {
                  currentCom = commands.getCode();
                  break;
               }
            }
            currentIRCommands.remove(currentCom);
            codeString = new GCIRCodeRepresentationHandler()
                  .getRepresentationFromCode(currentCom)
                  .getStringRepresentation();
            commandAttr.setValue("sendir," + globalCache.getConnector() + ",1,"
                  + codeString);
            commandAttr.setProtocol(protocol);
            protocol.getAttributes().add(commandAttr);

            DeviceCommand deviceCommand = new DeviceCommand();
            deviceCommand.setDevice(device);
            deviceCommand.setProtocol(protocol);
            deviceCommand.setName(irCommandInfo.getName());

            protocol.setDeviceCommand(deviceCommand);
            device.getDeviceCommands().add(deviceCommand);
            deviceCommands.add(deviceCommand);

         } else if (irTrans != null) {

            protocol.setType("UDP");
            ProtocolAttr ipAttr = new ProtocolAttr();
            ipAttr.setName("ipAddress");
            ipAttr.setValue(irTrans.getIpAdress());
            ipAttr.setProtocol(protocol);
            protocol.getAttributes().add(ipAttr);
            ProtocolAttr portAttr = new ProtocolAttr();

            portAttr.setName("port");
            portAttr.setValue(irTrans.getUdpPort());
            portAttr.setProtocol(protocol);
            protocol.getAttributes().add(portAttr);

            ProtocolAttr commandAttr = new ProtocolAttr();
            commandAttr.setName("command");
            IRCode currentCom = null;
            String codeString = "";
            for (IRCommand commands : currentIRCommands) {
               if (commands.getName().equals(irCommandInfo.getName())
                     && commands.getOriginalCodeString().equals(
                           irCommandInfo.getOriginalCodeString())) {
                  currentCom = commands.getCode();
                  break;
               }
            }
            currentIRCommands.remove(currentCom);
            if (currentCom != null) {
               if (currentCom.requiresToggle()) {
                  boolean toggle = true;
                  try {
                     codeString = new RawIRCodeRepresentationHandler()
                           .getRepresentationFromCode(
                                 currentCom.getRawCode(toggle))
                           .getStringRepresentation();

                  } catch (InvalidIRCodeException e) {
                     throw new IrFileParserException("error in parsing code :"
                           + irCommandInfo.getName());
                  }
                  commandAttr.setValue("sndccf " + codeString + ",l"
                        + irTrans.getIrLed());
                  System.out.println("   " + codeString + " "
                        + irTrans.getIrLed());
                  commandAttr.setProtocol(protocol);
                  protocol.getAttributes().add(commandAttr);

                  DeviceCommand deviceCommand = new DeviceCommand();
                  deviceCommand.setDevice(device);
                  deviceCommand.setProtocol(protocol);
                  deviceCommand.setName(irCommandInfo.getName() + "_ToggleOn");

                  protocol.setDeviceCommand(deviceCommand);
                  device.getDeviceCommands().add(deviceCommand);
                  deviceCommands.add(deviceCommand);
                  toggle = false;
                  try {
                     codeString = new RawIRCodeRepresentationHandler()
                           .getRepresentationFromCode(
                                 currentCom.getRawCode(toggle))
                           .getStringRepresentation();
                  } catch (InvalidIRCodeException e) {
                     throw new IrFileParserException("error in parsing code :"
                           + irCommandInfo.getName());
                  }
                  commandAttr.setValue("sndccf " + codeString + ",l"
                        + irTrans.getIrLed());

                  commandAttr.setProtocol(protocol);
                  protocol.getAttributes().add(commandAttr);

                  deviceCommand = new DeviceCommand();
                  deviceCommand.setDevice(device);
                  deviceCommand.setProtocol(protocol);
                  deviceCommand.setName(irCommandInfo.getName() + "_ToggleOff");

                  protocol.setDeviceCommand(deviceCommand);
                  device.getDeviceCommands().add(deviceCommand);
                  deviceCommands.add(deviceCommand);
               } else {
                  boolean toggle = true;
                  try {
                     codeString = new RawIRCodeRepresentationHandler()
                           .getRepresentationFromCode(
                                 currentCom.getRawCode(toggle))
                           .getStringRepresentation();
                  } catch (InvalidIRCodeException e) {
                     throw new IrFileParserException("error in parsing code :"
                           + irCommandInfo.getName());
                  }
                  commandAttr.setValue("sndccf " + codeString + ",l"
                        + irTrans.getIrLed());
                  commandAttr.setProtocol(protocol);
                  protocol.getAttributes().add(commandAttr);

                  DeviceCommand deviceCommand = new DeviceCommand();
                  deviceCommand.setDevice(device);
                  deviceCommand.setProtocol(protocol);
                  deviceCommand.setName(irCommandInfo.getName());

                  protocol.setDeviceCommand(deviceCommand);
                  device.getDeviceCommands().add(deviceCommand);
                  deviceCommands.add(deviceCommand);
               }
            }
         }
      }
      if (deviceCommands.size() > 0) {
         for (DeviceCommand command : deviceCommands) {
            genericDAO.save(command);
         }
         return deviceCommands;
      } else {

         throw new IrFileParserException("No commands to save");

      }

   }

}
