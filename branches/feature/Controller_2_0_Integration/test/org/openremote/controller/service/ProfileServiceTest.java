package org.openremote.controller.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.TestConstraint;
import org.openremote.controller.utils.SpringTestContext;

public class ProfileServiceTest {

   private ProfileService service;
   private String xmlPath = null;

   @Before
   public void setUp() throws Exception {
      service = (ProfileService) SpringTestContext.getInstance().getBean("profileService");
      xmlPath = this.getClass().getClassLoader().getResource(TestConstraint.FIXTURE_DIR + "panel.xml").getFile();
   }

   public String generateXMLByPanelID(String panelID) {
      return service.getProfileByPanelID(xmlPath, panelID);
   }

   public String generateXMLByPanelName(String panelName) {
      return service.getProfilByName(xmlPath, panelName);
   }

   @Test
   public void testGenerateXMLToShowAllPanels() {
      System.out.println(service.getAllPanels());
   }

   /* =========================ID:=================== */
   @Test
   public void testMyIphone() {
      String result = generateXMLByPanelID("MyIphone");
      System.out.println(result);
   }

   @Test
   public void testMyAndroid() {
      String result = generateXMLByPanelID("MyAndroid");
      System.out.println(result);
   }

   @Test
   public void testID1() {
      String result = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");
      System.out.println(result);
   }

   /* =========================ID:=================== */

   /* =************************Name:******************= */
   @Test
   public void testIDVsName1() {
      String nameResult = generateXMLByPanelName("father");
      String idResult = generateXMLByPanelID("MyIphone");

      Assert.assertEquals(nameResult, idResult);
      // System.out.println(result);
   }

   @Test
   public void testIDVsName2() {
      String nameResult = generateXMLByPanelName("mother");
      String idResult = generateXMLByPanelID("MyAndroid");
      Assert.assertEquals(nameResult, idResult);
      // System.out.println(result);
   }

   @Test
   public void testIDVsName3() {
      String nameResult = generateXMLByPanelName("me");
      String idResult = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");

      Assert.assertEquals(nameResult, idResult);
   }

   /* =************************Name:******************= */
   @Test
   public void testNoPanel() {
      String result = null;
      try {
         result = generateXMLByPanelName("meHaha");
      } catch (Exception e) {
         Assert.assertNotNull(e);
      }

      Assert.assertNull(result);
   }
}
