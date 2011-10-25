/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

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
import com.tinsys.pronto.irfiles.ProntoFileParser;

/**
 * @author william_work
 * 
 */
public class IRFileParserController extends
      BaseGWTSpringControllerWithHibernateSupport implements
      IRFileParserRPCService {

   private ProntoFileParser prontoFileParser;
   private IRFileParserService iRFileParserService;
   private List<IRCommand> currentIRCommands;

   /**
    * ProntoFileParser injected by fileUploadController
    * 
    * @param prontoFileParser
    */
   public void setProntoFileParser(ProntoFileParser prontoFileParser) {
      this.prontoFileParser = prontoFileParser;
      this.iRFileParserService.setProntoFileParser(prontoFileParser);
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
