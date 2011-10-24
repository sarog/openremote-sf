package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.exception.IrFileParserException;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;

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
	List<DeviceInfo> getDevices(BrandInfo brand);
	
   /** 
    * returns the code set information to the client side for the given device
	 * @param device
	 * @return List<CodeSetInfo>
	 */
	List<CodeSetInfo> getCodeSets(DeviceInfo device);
	
	/**
    * returns the brand information to the client side
	 * @return
	 */
	List<BrandInfo> getBrands();
	
	/**
    * returns the IrCommand information to the client side for the given device
	 * @param codeset
	 * @return
	 */
	List<IRCommandInfo> getIRCommands(CodeSetInfo codeset);
	
	/**
    * export the selected commands
	 * @param device
	 * @param globalCache
	 * @param irTrans
	 * @param selectedFunctions
	 * @return List<DeviceCommand>
	 * @throws IrFileParserException
	 */
	List<DeviceCommand> saveCommands(
         org.openremote.modeler.domain.Device device, GlobalCache globalCache,
         IRTrans irTrans, List<IRCommandInfo> selectedFunctions) throws IrFileParserException;
	
}
