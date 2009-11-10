package org.openremote.controller.service;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.openremote.controller.spring.SpringContext;

public class ProfileServiceTest extends TestCase {

   private ProfileService service ;
   private String xmlPath = null;
   
   protected void setUp() throws Exception {
       service = (ProfileService) SpringContext.getInstance().getBean("profileService");
       xmlPath = this.getClass().getClassLoader().getResource("./fixture/panel.xml").getFile();
       super.setUp();
   }
   
   
   public String generateXMLByPanelID(String panelID){
      return  service.getProfileByPanelID(xmlPath,panelID);
   }
   
   public String generateXMLByPanelName(String panelName){
      return service.getProfilByName(xmlPath,panelName);
   }
   
   public String testGenerateXMLToShowAllPanels(){
      return service.getPanelsXML();
   }
   /*=========================ID:===================*/
   public void testMyIphone(){
      String result = generateXMLByPanelID("MyIphone");
      System.out.println(result);
   }
   
   public void testMyAndroid(){
      String result = generateXMLByPanelID("MyAndroid");
      System.out.println(result);
   }
   
   public void testID1(){
      String result = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");
      System.out.println(result);
   }
   /*=========================ID:===================*/
   
   
   /*=************************Name:******************=*/
   public void testIDVsName1(){
      String nameResult = generateXMLByPanelName("father");
      String idResult = generateXMLByPanelID("MyIphone");
            
      Assert.assertEquals(nameResult, idResult);
//      System.out.println(result);
   }
   
   public void testIDVsName2(){
      String nameResult = generateXMLByPanelName("mother");
      String idResult = generateXMLByPanelID("MyAndroid");
      Assert.assertEquals(nameResult, idResult);
//      System.out.println(result);
   }
   
   public void testIDVsName3(){
      String nameResult = generateXMLByPanelName("me");
      String idResult = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");
            
      Assert.assertEquals(nameResult, idResult);
   }
   /*=************************Name:******************=*/

   public void testNoPanel(){
      String result = null;
      try{
         result  = generateXMLByPanelName("meHaha");
      } catch(Exception e){
         Assert.assertNotNull(e);
      }
      
      Assert.assertNull(result);
   }
}
