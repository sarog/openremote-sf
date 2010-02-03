package org.openremote.beehive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.openremote.beehive.api.service.impl.GenericDAO;

public class LIRCTestBase extends TestBase {
   
   
   protected void setUp() throws Exception {
      super.setUp();
      FileReader reader;
      String sql = null;   
      try {
         String sqlFilePath = this.getClass().getClassLoader().getResource(TestConstraint.FIXTURE_DIR + "test.sql").getFile();;
         reader = new FileReader(sqlFilePath);
         BufferedReader br = new BufferedReader(reader);   
         while((sql = br.readLine()) != null) {   
            //
         }     
         br.close();   
         reader.close(); 
      } catch (IOException e) {
         e.printStackTrace();
      }   
      GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
      genericDAO.runRawSql(sql);
      
   }

}
