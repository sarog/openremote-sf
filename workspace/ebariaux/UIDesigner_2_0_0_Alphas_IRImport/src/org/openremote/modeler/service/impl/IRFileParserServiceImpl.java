package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.openremote.modeler.client.CodeSetInfo;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRTrans;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.IRFileParserService;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.ir.codes.IRCode;
import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.ir.representations.gc.GCIRCodeRepresentationHandler;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserServiceImpl extends
		BaseAbstractService<DeviceCommand> implements
		IRFileParserService {

	private XCFFileParser xcfFileParser;
	private List<IRCommand> currentCodeSet;
	private static Logger log = Logger.getLogger(IRFileParserServiceImpl.class);

	public void setXcfFileParser(XCFFileParser xcfFileParser) {
		this.xcfFileParser = xcfFileParser;
	}

	@Override
	public List<Device> getDevices(Brand brand) {

		return xcfFileParser.getDevices(brand);
	}

	@Override
	public List<CodeSet> getCodeSets(Device device) {
		System.out.println(device);
		return xcfFileParser.getCodeSets(device);
	}

	@Override
	public List<Brand> getBrands() {
		return xcfFileParser.getBrands();
	}

	@Override
	public List<IRCommand> getIRCommands(CodeSet codeset) {
		return codeset.getIRCommands();
	}

	@Override
	@Transactional
	public void saveCommands(org.openremote.modeler.domain.Device device,
			GlobalCache globalCache, IRTrans irTrans,
			List<IRCommandInfo> selectedFunctions) {
		/*CodeSetInfo csi = selectedFunctions.get(0).getCodeSet();
		System.out.println("csi : "+csi.getDeviceInfo().getModelName());
		CodeSet cs= new CodeSet(csi.getDeviceInfo().getBrandInfo().getBrandName(), csi.getDeviceInfo().getModelName());
		System.out.println(cs+"  "+cs.getDevice()+" ");
		currentCodeSet = getIRCommands(cs);
		System.out.println("saving device euh");

		System.out.println("current cs"+currentCodeSet);
		System.out.println("current cs"+currentCodeSet.size());
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
				String codeString;
				for (IRCommand commands : currentCodeSet) {
					if (commands.getName()==irCommandInfo.getName() && commands.getOriginalCodeString()==irCommandInfo.getOriginalCodeString()){
						currentCom = commands.getCode();
					}
				}

				commandAttr.setName("command");
				if (currentCom.requiresToggle()){
					boolean toggle = true;
					codeString= new GCIRCodeRepresentationHandler().getRepresentationFromCode(currentCom.getRawCode(toggle)).getStringRepresentation();
					commandAttr.setValue( "sendir,"+globalCache.getConnector()+",1,"+codeString);
					commandAttr.setProtocol(protocol);
					protocol.getAttributes().add(commandAttr);
					
				       DeviceCommand deviceCommand = new DeviceCommand();
				       deviceCommand.setDevice(device);
				       deviceCommand.setProtocol(protocol);
				       deviceCommand.setName(irCommandInfo.getName()+"_ToggleOn");

				       protocol.setDeviceCommand(deviceCommand);
				       device.getDeviceCommands().add(deviceCommand);
					   deviceCommands.add(deviceCommand);
					   toggle = false;
						codeString= new GCIRCodeRepresentationHandler().getRepresentationFromCode(currentCom.getRawCode(toggle)).getStringRepresentation();
						commandAttr.setValue( "sendir,"+globalCache.getConnector()+",1,"+codeString);
						commandAttr.setProtocol(protocol);
						protocol.getAttributes().add(commandAttr);
						
					       deviceCommand = new DeviceCommand();
					       deviceCommand.setDevice(device);
					       deviceCommand.setProtocol(protocol);
					       deviceCommand.setName(irCommandInfo.getName()+"_ToggleOff");

					       protocol.setDeviceCommand(deviceCommand);
					       device.getDeviceCommands().add(deviceCommand);
						   deviceCommands.add(deviceCommand);   
				}else{
					boolean toggle = true;
					codeString= new GCIRCodeRepresentationHandler().getRepresentationFromCode(currentCom.getRawCode(toggle)).getStringRepresentation();
					commandAttr.setValue( "sendir,"+globalCache.getConnector()+",1,"+codeString);
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
				
				
				

			} else {

			}
			for (DeviceCommand command : deviceCommands){
				System.out.println("Saving command : " + command);
		        
		         genericDAO.save(command);
			}*/
		}

		// TODO create a DeviceCommand based on
		// -the device
		// the IRCommandInfo : find a way to map the IRCommandInfo to the actual
		// IRCommand and then use
		// the other infos (still to be added) to create the commands.
		/*
		 * TEST device = new
		 * org.openremote.modeler.domain.Device("test","test","test");
		 * 
		 * genericDAO.save(device); Hibernate.initialize(device.getSensors());
		 * Hibernate.initialize(device.getSwitchs()); List<DeviceCommand>
		 * deviceCommands = device.getDeviceCommands(); for(DeviceCommand cmd :
		 * deviceCommands ) {
		 * Hibernate.initialize(cmd.getProtocol().getAttributes()); }
		 * Hibernate.initialize(device.getSliders());
		 * Hibernate.initialize(device.getDeviceAttrs());
		 */
	}

}
