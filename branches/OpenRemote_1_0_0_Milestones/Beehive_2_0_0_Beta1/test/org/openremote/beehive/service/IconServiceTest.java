package org.openremote.beehive.service;

import java.util.List;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.dto.IconDTO;
import org.openremote.beehive.api.service.IconService;
import org.openremote.beehive.spring.SpringContext;

public class IconServiceTest extends TestBase {

   private IconService service = (IconService) SpringContext.getInstance().getBean("iconService");

   public void testFindByName(){
      List<IconDTO> iconDTOs = service.findIconsByName("Menu");
      if(!iconDTOs.isEmpty()){
         for (IconDTO iconDTO : iconDTOs) {
            System.out.println("fileName="+iconDTO.getFileName()+" name="+iconDTO.getName());
         }
      }
   }
}
