/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.selenium;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.thoughtworks.selenium.DefaultSelenium;

// TODO: Auto-generated Javadoc
/**
 * The TestNG TestBase for Selenium Test. This Class registers the website and browser to be tested.
 * 
 * @author Dan 2009-7-31
 */
public class SeleniumTestNGBase {
   
   /** The selenium. */
   protected DefaultSelenium selenium;
   
   /** The Constant DEBUG_ID_PREFIX, GWT will add this prefix before real debug id. */
   protected static final String DEBUG_ID_PREFIX = "gwt-debug-";
   
   /**
    * Sets the up.
    */
   @BeforeClass(alwaysRun = true)
   public void setUp() {
      selenium = new DefaultSelenium("localhost", 4444, Browser.FIREFOX, "http://localhost:8080/");
      selenium.start();
      selenium.setTimeout("10000");
//      selenium.setSpeed("1000");
      login();
   }

   /**
    * Login.
    */
   private void login(){
      selenium.open("modeler/login.jsp");
      selenium.waitForPageToLoad("3000");
      selenium.windowMaximize();
      selenium.type("username", "super");
      selenium.type("password", "123");
//      selenium.click("rememberme");
      selenium.click("//input[@value='Login']");
      selenium.waitForPageToLoad("30000");
      
   }
   
   /**
    * Stop test.
    */
   @AfterClass(alwaysRun = true)
   public void stopTest() {
      selenium.stop();
   }
   
   /**
    * Sleeps for the specified number of milliseconds.
    * 
    * @param millisecs
    *           the millisecs
    */
   public void pause(int millisecs) {
       try {
           Thread.sleep(millisecs);
       } catch (InterruptedException e) {
       }
   }
   
   /**
    * Adds the debug id prefix "gwt-debug-".
    * 
    * @param debugId
    *           the debug id
    * 
    * @return the string
    */
   protected String addDebugIdPrefix(String debugId){
      return DEBUG_ID_PREFIX + debugId;
   }

   /**
    * Click the locator button.
    * 
    * @param locator
    *           the locator
    */
   protected void click(String locator){
      selenium.click(addDebugIdPrefix(locator));
   }

   /**
    * Type the value to locator widget.
    * 
    * @param locator
    *           the locator
    * @param value
    *           the value
    */
   protected void type(String locator,String value) {
      selenium.type(addDebugIdPrefix(locator), value);
   }
   
   /**
    * Gets the value of locator widget.
    * 
    * @param locator
    *           the locator
    * 
    * @return the value
    */
   protected String getValue(String locator) {
      return selenium.getValue(addDebugIdPrefix(locator));
   }
   
}
