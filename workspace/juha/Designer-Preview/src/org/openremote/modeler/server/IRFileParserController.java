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

import java.util.ArrayList;

import org.openremote.modeler.client.rpc.IRFileParserRPCService;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.service.IRFileParserService;

import com.tinsys.pronto.irfiles.ProntoFileParser;

/**
 * @author william_work
 * 
 */
public class IRFileParserController extends BaseGWTSpringController implements IRFileParserRPCService {

  private static final long serialVersionUID = 1L;
  
   private IRFileParserService iRFileParserService;

   /**
    * ProntoFileParser injected by fileUploadController
    * 
    * @param prontoFileParser
    */
   public void setProntoFileParser(ProntoFileParser prontoFileParser) {
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
   public ArrayList<BrandInfo> getBrands() {
      return iRFileParserService.getBrands();
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getDevices(org
    * .openremote.modeler.irfileparser.BrandInfo)
    */
   public ArrayList<DeviceInfo> getDevices(BrandInfo bi) {
      return iRFileParserService.getDevices(bi);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getCodeSets(org
    * .openremote.modeler.irfileparser.DeviceInfo)
    */
   public ArrayList<CodeSetInfo> getCodeSets(DeviceInfo di) {
      return iRFileParserService.getCodeSets(di);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.client.rpc.IRFileParserRPCService#getIRCommands
    * (org.openremote.modeler.irfileparser.CodeSetInfo)
    */
   public ArrayList<IRCommandInfo> getIRCommands(CodeSetInfo csi) {
      return iRFileParserService.getIRCommands(csi);
   }

}
