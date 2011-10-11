package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.CodeSetInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.client.rpc.IRFileParserRPCService;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserController extends BaseGWTSpringController implements
		IRFileParserRPCService {

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
		if (xcfFileParser != null) {
			List<BrandInfo> brandInfo = new ArrayList<BrandInfo>();
			List<Brand> brands = xcfFileParser.getBrands();
			for (Brand brand : brands) {
				brandInfo.add(new BrandInfo(brand.getBrandName()));
			}
			return brandInfo;
		} else {
			return null;
		}
	}

	public List<DeviceInfo> getDevices(BrandInfo bi) {
		List<DeviceInfo> deviceInfo = new ArrayList<DeviceInfo>();
		Brand b = new Brand(bi.getBrandName());

		List<Device> devices = xcfFileParser.getDevices(b);
		for (Device device : devices) {
			deviceInfo.add(new DeviceInfo(new BrandInfo(device.getBrand()
					.getBrandName()), device.getModelName()));
		}
		return deviceInfo;
	}

	public List<CodeSetInfo> getCodeSets(DeviceInfo di) {
		List<CodeSetInfo> codeSetInfo = new ArrayList<CodeSetInfo>();

		Device d = new Device(new Brand(di.getBrandInfo().getBrandName()),
				di.getModelName());

		List<CodeSet> codeSets = xcfFileParser.getCodeSets(d);

		for (CodeSet codeSet : codeSets) {
			codeSetInfo.add(new CodeSetInfo(di, codeSet.getDescription(),
					codeSet.getCategory()));
		}
		return codeSetInfo;
	}

	public List<IRCommandInfo> getIRCommands(CodeSetInfo csi) {
		List<IRCommandInfo> iRCommandInfo = new ArrayList<IRCommandInfo>();
		Device d = new Device(new Brand(csi.getDeviceInfo().getBrandInfo()
				.getBrandName()), csi.getDeviceInfo().getModelName());
		CodeSet cs = new CodeSet(csi.getDeviceInfo().getBrandInfo()
				.getBrandName(), csi.getDeviceInfo().getModelName());
		List<IRCommand> iRcommands = xcfFileParser.getCodeSets(d)
				.get(xcfFileParser.getCodeSets(d).indexOf(cs)).getIRCommands();
		for (IRCommand irCommand : iRcommands) {
			IRCommandInfo iRCI;
			if (irCommand.getCode() != null) {
				iRCI = new IRCommandInfo(irCommand.getName(), irCommand
						.getCode().toString(), irCommand.getOriginalCodeString(),irCommand.getComment());
			} else {
				iRCI = new IRCommandInfo(irCommand.getName(), null,irCommand.getOriginalCodeString(),irCommand.getComment());
			}
			iRCommandInfo.add(iRCI);
		}
		return iRCommandInfo;
	}
}
