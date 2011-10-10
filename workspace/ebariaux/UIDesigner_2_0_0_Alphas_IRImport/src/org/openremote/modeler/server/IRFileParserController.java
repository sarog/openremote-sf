package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.rpc.IRFileParserRPCService;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserController extends BaseGWTSpringController implements IRFileParserRPCService {

	
	private XCFFileParser xcfFileParser;
	 
	public void setXcfFileParser(XCFFileParser xcfFileParser) {
		this.xcfFileParser = xcfFileParser;
	}

	public List<Device> getDevices(Brand brand) {

		return xcfFileParser.getDevices(brand);
	}


	public List<CodeSet> getCodeSets(Device device) {
		return xcfFileParser.getCodeSets(device);
	}


	public List<BrandInfo> getBrands() {
		if (xcfFileParser != null){
		List<BrandInfo> brandInfo = new ArrayList<BrandInfo>();
		List<Brand> brands= xcfFileParser.getBrands();
		for (Brand brand : brands) {
			brandInfo.add(new BrandInfo(brand.getBrandName()));
		}
		return brandInfo;} else{
			return null;
		}
	}
	
	public List<DeviceInfo> getDevices(BrandInfo bi) {
		List<DeviceInfo> deviceInfo = new ArrayList<DeviceInfo>();
		Brand b = new Brand(bi.getBrandName());
		
		List<Device> devices = xcfFileParser.getDevices(b);
		for (Device device : devices) {
			deviceInfo.add(new DeviceInfo(new BrandInfo(device.getBrand().getBrandName()), device.getModelName()));
		}
		return deviceInfo;
	}
}
