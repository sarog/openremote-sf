/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.exception.IrFileParserException;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface is for managing slider entity.
 */
@RemoteServiceRelativePath("irfile.smvc")
public interface IRFileParserRPCService extends RemoteService {

   /**
    * Returns the CodeSets
    * 
    * @param device
    * @return List<CodeSetInfo>
    */
   List<CodeSetInfo> getCodeSets(DeviceInfo device);

   /**
    * Returns the brands
    * 
    * @return List<BrandInfo>
    */
   List<BrandInfo> getBrands();

   /**
    * Returns the devices linked to a brand
    * 
    * @param brand
    * @return List<DeviceInfo>
    */
   List<DeviceInfo> getDevices(BrandInfo brand);

   /**
    * Returns the IR commands from a code set
    * 
    * @param codeset
    * @return List<IRCommandInfo>
    */
   List<IRCommandInfo> getIRCommands(CodeSetInfo codeset);

   /**
    * Export the selected commands to the database.
    * 
    * @param device
    * @param globalCache
    * @param irTrans
    * @param selectedFunctions
    * @return List<DeviceCommand>
    * @throws IrFileParserException
    */
   List<DeviceCommand> saveCommands(Device device, GlobalCache globalCache,
         IRTrans irTrans, List<IRCommandInfo> selectedFunctions)
         throws IrFileParserException;
}
