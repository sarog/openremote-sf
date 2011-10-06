package org.openremote.modeler.service;

import java.util.List;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;

public interface IRFileParserService {

	List<Device> getDevices(Brand brand);
	List<CodeSet> getCodeSets(Device device);
}
