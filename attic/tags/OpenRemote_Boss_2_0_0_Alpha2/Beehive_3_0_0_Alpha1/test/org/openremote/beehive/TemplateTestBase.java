package org.openremote.beehive;

import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Icon;
import org.openremote.beehive.domain.Template;

/**
 * Test base for template-related test
 * 
 * @author Dan Cong
 *
 */
public class TemplateTestBase extends TestBase {
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();

      
      Account a = new Account();
      Template t1 = new Template();
      t1.setAccount(a);
      t1.setName("t1");
      t1.setContent("content");
      a.addTemplate(t1);
      Template t2 = new Template();
      t2.setAccount(a);
      t2.setName("t2");
      t2.setContent("content");
      a.addTemplate(t2);
      accountService.save(a);
      
      Icon i = new Icon();
      i.setFileName("menu.png");
      i.setName("menu");
      genericDAO.save(i);
   }

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
   }
   
   

}
