package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;

public interface IRFileParserService {

	List<Device> getDevices(Brand brand);
	List<CodeSet> getCodeSets(Device device);
	List<Brand> getBrands();
	List<IRCommand> getIRCommands(CodeSet codeset);
	void saveCommands(List<DeviceCommand> deviceCommands);
	
}
