package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.IRFileParserRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.exception.IrFileParserException;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;
import org.openremote.modeler.service.IRFileParserService;

import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.XCFFileParser;

/**
 * @author william_work
 * 
 */
public class IRFileParserController extends
      BaseGWTSpringControllerWithHibernateSupport implements
      IRFileParserRPCService {

   private XCFFileParser xcfFileParser;
   private IRFileParserService iRFileParserService;
   private List<IRCommand> currentIRCommands;

   /**
    * XcfFileParser injected by fileUploadController
    * 
    * @param xcfFileParser
    */
   public void setXcfFileParser(XCFFileParser xcfFileParser) {
      this.xcfFileParser = xcfFileParser;
      this.iRFileParserService.setXcfFileParser(xcfFileParser);
   }

   /**
    * @param iRFileParserService
    */
   public void setiRFileParserService(IRFileParserService iRFileParserService) {
      this.iRFileParserService = iRFileParserService;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.IRFileParserRPCService#getBrands()
    */
   public List<BrandInfo> getBrands() {
      return iRFileParserService.getBrands();
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getDevices(org
    * .openremote.modeler.irfileparser.BrandInfo)
    */
   public List<DeviceInfo> getDevices(BrandInfo bi) {
      return iRFileParserService.getDevices(bi);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getCodeSets(org
    * .openremote.modeler.irfileparser.DeviceInfo)
    */
   public List<CodeSetInfo> getCodeSets(DeviceInfo di) {
      return iRFileParserService.getCodeSets(di);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getIRCommands
    * (org.openremote.modeler.irfileparser.CodeSetInfo)
    */
   public List<IRCommandInfo> getIRCommands(CodeSetInfo csi) {
      return iRFileParserService.getIRCommands(csi);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#saveCommands(
    * org.openremote.modeler.domain.Device,
    * org.openremote.modeler.irfileparser.GlobalCache,
    * org.openremote.modeler.irfileparser.IRTrans, java.util.List)
    */
   public List<DeviceCommand> saveCommands(
         org.openremote.modeler.domain.Device device, GlobalCache globalCache,
         IRTrans irTrans, List<IRCommandInfo> selectedFunctions)
         throws IrFileParserException {
      return iRFileParserService.saveCommands(device, globalCache, irTrans,
            selectedFunctions);

   }

}
