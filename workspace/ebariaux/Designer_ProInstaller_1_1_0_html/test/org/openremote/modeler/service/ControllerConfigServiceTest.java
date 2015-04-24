package org.openremote.modeler.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ControllerConfigServiceTest {
   
   private ControllerConfigService configService = (ControllerConfigService) SpringTestContext.getInstance().getBean("controllerConfigService");
   private UserService userService = (UserService) SpringTestContext.getInstance().getBean("userService");
   private Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
   private Set<ControllerConfig> configs = new HashSet<ControllerConfig>();
   
   @BeforeClass
   public void saveFromDefault(){
            
      userService.createUserAccount("test", "test", UserServiceTest.TEST_EMAIL);
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
      XmlParser.initControllerConfig(categories, configs);
      configService.saveAll(configs);
      
   }
   @Test
   public void getAllConfigs(){
      Set<String> categoryNames= new HashSet<String>();
      for(ConfigCategory category : categories){
         categoryNames.add(category.getName());
      }
      Assert.assertTrue(categoryNames.size()>0);
      for(String categoryName : categoryNames){
         Collection<ControllerConfig> cfgs = configService.listAllConfigsByCategoryNameForAccount(categoryName, userService.getAccount());
         Assert.assertTrue(cfgs.size()>=1);
         for(ControllerConfig cfg : cfgs){
            Assert.assertNotNull(cfg.getName());
            Assert.assertNotNull(cfg.getValue());
            Assert.assertNotNull(cfg.getHint());
            Assert.assertNotNull(cfg.getValidation());
            Assert.assertNotNull(cfg.getOptions());
//            System.out.println(cfg);
         }
      }
   }
   @Test
   public void update(){
      String addStr = "...updated";
      Set<ControllerConfigDTO> configDTOs = configService.listAllConfigDTOsByCategory("advance");
      Assert.assertTrue(configDTOs.size()>0);
      for(ControllerConfigDTO cfg : configDTOs){
         if(cfg.getOptions().equals("")&&(addStr+cfg.getValue()).matches(cfg.getValidation())){
            cfg.setValue(cfg.getValue()+addStr);
         }
      }
      
      configService.saveAllDTOs(configDTOs);
      
      Collection<ControllerConfigDTO> configDTOs2 = configService.listAllConfigDTOsByCategory("advance");
      Assert.assertTrue(configDTOs2.size()>0);
      for(ControllerConfigDTO cfg : configDTOs2){
         if(cfg.getValue().endsWith(addStr)){
            Assert.assertTrue(cfg.getValue().substring(0,cfg.getValue().indexOf(addStr)-1).matches(cfg.getValidation()));
         }
      }
   }
   
   @Test
   public void testMissingConfigByCategoryName() {
     String testCategoryName = "advance";
     List<String> valueNames = new ArrayList<String>();
     
     // Collect all config names for given category
     for (ControllerConfig config : configs) {
       if (config.getCategory().equals(testCategoryName)) {
         valueNames.add(config.getName());
       }
     }
     
     Set<ControllerConfigDTO> existingConfigs = configService.listAllConfigDTOsByCategory(testCategoryName);
     Set<ControllerConfigDTO> missingConfigs = configService.listMissedConfigDTOsByCategoryName(testCategoryName);
     
     Assert.assertEquals(missingConfigs.size() + existingConfigs.size(), valueNames.size(), "Total of missing and existing config entries should be equal to total number of possible entries.");
     for (ControllerConfigDTO dto : existingConfigs) {
       Assert.assertTrue(valueNames.contains(dto.getName()), "Invalid name for an existing config entry");
     }
     for (ControllerConfigDTO dto : missingConfigs) {
       Assert.assertTrue(valueNames.contains(dto.getName()), "Invalid name for a missing config entry");
     }
   }
}
