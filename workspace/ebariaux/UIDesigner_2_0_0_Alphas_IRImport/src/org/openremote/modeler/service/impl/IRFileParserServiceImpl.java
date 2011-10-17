package org.openremote.modeler.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.IRFileParserService;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserServiceImpl extends
		BaseAbstractService<DeviceCommand> implements
		IRFileParserService {

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
	public void saveCommands(List<DeviceCommand> deviceCommands) {
		for (DeviceCommand command : deviceCommands){
			System.out.println("Saving command : " + command);
	        
	         genericDAO.save(command);
		}
	}

}
