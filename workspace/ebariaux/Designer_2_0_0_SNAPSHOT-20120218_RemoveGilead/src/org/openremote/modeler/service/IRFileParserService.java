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

package org.openremote.modeler.service;

import java.util.ArrayList;

import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.IRCommandInfo;

import com.tinsys.pronto.irfiles.ProntoFileParser;

public interface IRFileParserService {

   /** 
    * sets the prontoFileParser
    * @param prontoFileParser
    */
   void setProntoFileParser(ProntoFileParser prontoFileParser);
   
	/** 
	 * returns the device information to the client side for the given brand
	 * @param brand
	 * @return List<DeviceInfo>
	 */
   ArrayList<DeviceInfo> getDevices(BrandInfo brand);
	
   /** 
    * returns the code set information to the client side for the given device
	 * @param device
	 * @return List<CodeSetInfo>
	 */
   ArrayList<CodeSetInfo> getCodeSets(DeviceInfo device);
	
	/**
    * returns the brand information to the client side
	 * @return
	 */
   ArrayList<BrandInfo> getBrands();
	
	/**
    * returns the IrCommand information to the client side for the given device
	 * @param codeset
	 * @return
	 */
   ArrayList<IRCommandInfo> getIRCommands(CodeSetInfo codeset);
	
	/**
    * export the selected commands
	 * @param device
	 * @param globalCache
	 * @param irTrans
	 * @param selectedFunctions
	 * @return List<DeviceCommand>
	 * @throws IrFileParserException
	 */
   /*
	List<DeviceCommand> saveCommands(
         org.openremote.modeler.domain.Device device, GlobalCache globalCache,
         IRTrans irTrans, List<IRCommandInfo> selectedFunctions) throws IrFileParserException;
         */
	
}
