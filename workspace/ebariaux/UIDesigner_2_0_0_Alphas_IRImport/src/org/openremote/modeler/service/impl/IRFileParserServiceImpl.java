package org.openremote.modeler.service.impl;

import java.util.List;

import org.openremote.modeler.service.IRFileParserService;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserServiceImpl implements IRFileParserService {

	private XCFFileParser xcfFileParser;
	 
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

}
