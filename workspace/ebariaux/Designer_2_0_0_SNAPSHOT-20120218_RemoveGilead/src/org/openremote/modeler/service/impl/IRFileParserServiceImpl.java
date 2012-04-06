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
package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.IRFileParserService;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.pronto.irfiles.ProntoFileParser;

public class IRFileParserServiceImpl extends BaseAbstractService<DeviceCommand>
      implements IRFileParserService {

   private ProntoFileParser prontoFileParser;
   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#setXcfFileParser(com
    * .tinsys.pronto.irfiles.XCFFileParser)
    */
   public void setProntoFileParser(ProntoFileParser prontoFileParser) {
      this.prontoFileParser = prontoFileParser;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#getDevices(org.openremote
    * .modeler.irfileparser.BrandInfo)
    */
   @Override
   public ArrayList<DeviceInfo> getDevices(BrandInfo brand) {

     ArrayList<DeviceInfo> deviceInfo = new ArrayList<DeviceInfo>();
      Brand b = new Brand(brand.getBrandName());

      List<Device> devices = prontoFileParser.getDevices(b);
      for (Device device : devices) {
         deviceInfo.add(new DeviceInfo(new BrandInfo(device.getBrand()
               .getBrandName()), device.getModelName()));
      }
      return deviceInfo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.modeler.service.IRFileParserService#getCodeSets(org.openremote
    * .modeler.irfileparser.DeviceInfo)
    */
   @Override
   public ArrayList<CodeSetInfo> getCodeSets(DeviceInfo di) {
     ArrayList<CodeSetInfo> codeSetInfo = new ArrayList<CodeSetInfo>();

      Device d = new Device(new Brand(di.getBrandInfo().getBrandName()),
            di.getModelName());

      List<CodeSet> codeSets = prontoFileParser.getCodeSets(d);
      int index = 0;
      for (CodeSet codeSet : codeSets) {
         codeSetInfo.add(new CodeSetInfo(di, codeSet.getDescription(), codeSet
               .getCategory(), index));
         index++;
      }
      return codeSetInfo;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.IRFileParserService#getBrands()
    */
   @Override
   public ArrayList<BrandInfo> getBrands() {
      if (prontoFileParser != null) {
        ArrayList<BrandInfo> brandInfo = new ArrayList<BrandInfo>();
         List<Brand> brands = prontoFileParser.getBrands();
         for (Brand brand : brands) {
            brandInfo.add(new BrandInfo(brand.getBrandName()));
         }
         return brandInfo;
      } else {
         return null;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.IRFileParserService#getIRCommands(org.
    * openremote.modeler.irfileparser.CodeSetInfo)
    */
   @Override
   public ArrayList<IRCommandInfo> getIRCommands(CodeSetInfo csi) {
     ArrayList<IRCommandInfo> iRCommandInfo = new ArrayList<IRCommandInfo>();
      Device d = new Device(new Brand(csi.getDeviceInfo().getBrandInfo()
            .getBrandName()), csi.getDeviceInfo().getModelName());
      List<IRCommand> iRcommands = prontoFileParser.getCodeSets(d)
            .get(csi.getIndex().intValue()).getIRCommands();

      for (IRCommand irCommand : iRcommands) {
         IRCommandInfo iRCI;
         if (irCommand.getCode() != null) {
            iRCI = new IRCommandInfo(irCommand.getName(), irCommand.getCode()
                  .toString(), irCommand.getOriginalCodeString(),
                  irCommand.getComment(), csi);
         } else {
            iRCI = new IRCommandInfo(irCommand.getName(), null,
                  irCommand.getOriginalCodeString(), irCommand.getComment(),
                  csi);
         }
         iRCommandInfo.add(iRCI);
      }
      return iRCommandInfo;
   }

}
