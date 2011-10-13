package org.openremote.modeler.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.IRFileParserService;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserServiceImpl extends BaseAbstractService<org.openremote.modeler.domain.Device> implements IRFileParserService {

	private XCFFileParser xcfFileParser;
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
	public void saveCommands(org.openremote.modeler.domain.Device device, List<IRCommandInfo> selectedFunctions) {
		log.info("saving "+selectedFunctions.size()+" commands for device ");
		System.out.println("saving device");
		// TODO create a DeviceCommand based on 
			// -the device 
			//the IRCommandInfo : find a way to map the IRCommandInfo to the actual IRCommand and then use
			// the other infos (still to be added) to create the commands.
/*		TEST
       device = new org.openremote.modeler.domain.Device("test","test","test");

		genericDAO.save(device);
	      Hibernate.initialize(device.getSensors());
	      Hibernate.initialize(device.getSwitchs());
	      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
	      for(DeviceCommand cmd : deviceCommands ) {
	         Hibernate.initialize(cmd.getProtocol().getAttributes());
	      }
	      Hibernate.initialize(device.getSliders());
	      Hibernate.initialize(device.getDeviceAttrs());*/
	}

}
