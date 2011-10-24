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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.SessionFactory;
import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.exception.IrFileParserException;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.tinsys.ir.representations.IRCodeRepresentationFactory;
import com.tinsys.ir.representations.pronto.NecIRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RC5IRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RC5xIRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RawIRCodeRepresentationHandler;
import com.tinsys.pronto.irfiles.ProntoFileParser;


@ContextConfiguration
@TransactionConfiguration
@Transactional
public class IrFileParserServiceTest extends AbstractTransactionalJUnit4SpringContextTests{


   private DeviceService deviceService =
      (DeviceService) SpringTestContext.getInstance().getBean("deviceService");
   
   private IRFileParserService iRFileParserService =
      (IRFileParserService) SpringTestContext.getInstance().getBean("iRFileParserService");
   
   private DeviceCommandService deviceCommandService =
      (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
 
   private GenericDAO genericDAO =
      (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private UserService userService =
      (UserService) SpringTestContext.getInstance().getBean("userService");
   
   private ProntoFileParser prontoFileParser = new ProntoFileParser();
   private  HibernateTemplate ht;
   private SessionFactory sessionFactory;
   
   
   
   @BeforeClass

   public void mysetUp() throws IOException, ParserConfigurationException, SAXException {
      ZipFile file = new ZipFile(
            "./test/irfileparser_testdata/XCF Files/Scott Sovern-Front Room 8-TSU9400.xcf");
      IRCodeRepresentationFactory factory = new IRCodeRepresentationFactory();
      new RC5IRCodeRepresentationHandler().registerWithFactory(factory);
      new RC5xIRCodeRepresentationHandler().registerWithFactory(factory);
      new RawIRCodeRepresentationHandler().registerWithFactory(factory);
      new NecIRCodeRepresentationHandler().registerWithFactory(factory);
      prontoFileParser.setFactory(factory);
      prontoFileParser.parseFile(file);
      iRFileParserService.setProntoFileParser(prontoFileParser);
//      LocalSessionFactoryBean lsfb = (LocalSessionFactoryBean) SpringContext.getInstance().getBean("&sessionFactory");
//      System.out.println("----> "+lsfb.getHibernateProperties());
//      sessionFactory = lsfb.getConfiguration().buildSessionFactory();
//       ht = new HibernateTemplate(sessionFactory);
   }
   
   
   @Test
   public void getBrands() {
      List<BrandInfo> result = iRFileParserService.getBrands();
     Assert.assertEquals(6,result.size());
   }
   
   @Test(dependsOnMethods = "getBrands")
   public void saveCommand() throws IrFileParserException {
//      Session session = SessionFactoryUtils.getSession(ht.getSessionFactory(), true);
//      TransactionSynchronizationManager.bindResource(sessionFactory , new SessionHolder(session));

      Device device = new Device();
      device.setName("irFPTest");
      device.setModel("tv");
      device.setVendor("sony");
      deviceService.saveDevice(device);
      
      GlobalCache globalCache = new GlobalCache("127.0.0.1", "4998"
            , "4:1");
      IRTrans irTrans = new IRTrans();
      List<IRCommandInfo> selectedFunctions = new ArrayList<IRCommandInfo>();
      IRCommandInfo irCommand = iRFileParserService.getIRCommands(iRFileParserService.getCodeSets(iRFileParserService.getDevices(iRFileParserService.getBrands().get(0)).get(0)).get(0)).get(0);
      selectedFunctions.add(irCommand);

      
/*      iRFileParserService.saveCommands(device, globalCache, irTrans, selectedFunctions);
      Assert.assertEquals(deviceCommandService.loadById(1L).getName(), irCommand.getName());*/
      
      
   }
   

}
