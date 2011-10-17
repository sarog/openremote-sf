package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.CodeSetInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.client.rpc.IRFileParserRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRTrans;
import org.openremote.modeler.service.IRFileParserService;
import org.openremote.modeler.service.impl.IRFileParserServiceImpl;

import com.tinsys.ir.codes.IRCode;
import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.ir.representations.gc.GCIRCodeRepresentationHandler;
import com.tinsys.pronto.irfiles.XCFFileParser;

public class IRFileParserController extends BaseGWTSpringControllerWithHibernateSupport implements
		IRFileParserRPCService {

	private XCFFileParser xcfFileParser;
	private IRFileParserService iRFileParserService;
	private List<IRCommand> currentCodeSet;

	private static Logger log = Logger.getLogger(IRFileParserController.class); 

	public void setXcfFileParser(XCFFileParser xcfFileParser) {
		this.xcfFileParser = xcfFileParser;
	}
	public void setiRFileParserService(IRFileParserService iRFileParserService) {
		this.iRFileParserService = iRFileParserService;
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
						.getCode().toString(), irCommand.getOriginalCodeString(),irCommand.getComment(),csi);
			} else {
				iRCI = new IRCommandInfo(irCommand.getName(), null,irCommand.getOriginalCodeString(),irCommand.getComment(),csi);
			}
			iRCommandInfo.add(iRCI);
		}
		return iRCommandInfo;
	}
	
	public void saveCommands(org.openremote.modeler.domain.Device device, GlobalCache globalCache,IRTrans irTrans,
	List<IRCommandInfo> selectedFunctions){
		log.info("saving "+selectedFunctions.size()+" commands for device ");
		System.out.println("saving "+selectedFunctions.size()+" commands for device ");
		System.out.println("IRFileParserController xcf :"+xcfFileParser.getDevices().get(0));
		
		CodeSetInfo csi = selectedFunctions.get(0).getCodeSet();
		System.out.println("csi : "+csi.getDeviceInfo().getModelName());
		CodeSet cs= new CodeSet(csi.getDeviceInfo().getBrandInfo().getBrandName(), csi.getDeviceInfo().getModelName());
		System.out.println(cs+"  "+cs.getDevice()+" ");
		currentCodeSet = xcfFileParser.getCodeSets(cs.getDevice()).get(0).getIRCommands();
		System.out.println("xcfFileParser  "+ xcfFileParser);
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
		
		
		
	iRFileParserService.saveCommands(deviceCommands);

	}}
}
