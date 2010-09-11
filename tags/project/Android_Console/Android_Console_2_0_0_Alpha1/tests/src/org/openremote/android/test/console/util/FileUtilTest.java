package org.openremote.android.test.console.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import junit.framework.TestCase;

import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.util.FileUtil;

public class FileUtilTest extends TestCase {
// temp use junit test.
   public void testParsePanelXML() {
      File file = new File(FileUtilTest.class.getResource("test.xml").getFile());
      try {
         FileInputStream fIn = new FileInputStream(file);
         FileUtil.parsePanelXMLInputStream(fIn);
         System.out.println("----------------screen size:"+XMLEntityDataBase.screens.size());
         System.out.println("----------------group size:"+XMLEntityDataBase.groups.size());
         Set<Integer> keys = XMLEntityDataBase.screens.keySet();
         for (Integer integer : keys) {
            System.out.println("screen:" + XMLEntityDataBase.screens.get(integer).getName());
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
}
