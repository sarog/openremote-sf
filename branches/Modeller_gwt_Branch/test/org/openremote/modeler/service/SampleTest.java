/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.service;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class SampleTest.
 * 
 * @author Dan 2009-7-22
 */
public class SampleTest {
   
   /**
    * Before.
    */
   @SuppressWarnings("unused")
   @BeforeClass
   private void before() {
      System.out.println("Test begins:");
   }

   /**
    * Hello world test.
    */
   @Test
   public void helloWorldTest() {
     System.out.println("Hello World");
//     throw new Error();
   }

   /**
    * Multi thread test.
    */
   @Test(threadPoolSize = 10, invocationCount = 5,  timeOut = 1000, groups = { "multiple" })
   public void multiThreadTest() {
      System.out.println("MultiThread test");
   }
   
   /**
    * Hello nature test.
    */
   @Test(dependsOnMethods = { "helloWorldTest" })
   public void helloNatureTest() {
      System.out.println("Hello Nature");
   }
   
   /**
    * After.
    */
   @SuppressWarnings("unused")
   @AfterClass
   private void after() {
      System.out.println("Test ends");
   }
 }
