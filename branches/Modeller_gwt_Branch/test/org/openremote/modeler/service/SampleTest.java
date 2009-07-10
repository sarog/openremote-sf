package org.openremote.modeler.service;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SampleTest {
   
   @BeforeClass
   private void before(){
      System.out.println("Test begins:");
   }

   @Test
   public void helloWorldTest() {
     System.out.println("Hello World");
//     throw new Error();
   }

   @Test(threadPoolSize = 10, invocationCount = 5,  timeOut = 1000, groups = { "multiple" })
   public void multiThreadTest() {
      System.out.println("MultiThread test");
   }
   
   @Test(dependsOnMethods = {"helloWorldTest"})
   public void helloNatureTest() {
      System.out.println("Hello Nature");
   }
   @AfterClass
   private void after(){
      System.out.println("Test ends");
   }
 }
