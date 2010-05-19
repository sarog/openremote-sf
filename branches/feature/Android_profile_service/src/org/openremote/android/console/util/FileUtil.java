/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.android.console.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openremote.android.console.Constants;
import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.TabBar;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
/**
 * File I/O utility.
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 *
 */
public class FileUtil {
   
   /**
    * Checks whether a file exists by file name.
    * 
    * @param context
    *           Context instance
    * @param fileName
    *           file name
    * @return true if exists.
    */
   public static boolean checkFileExists(Context context, String fileName) {
      return context.getFileStreamPath(fileName).exists();
   }

   // unused
   public static String ReadSettings(Context context) {
      StringBuffer strBuffer = new StringBuffer();
      if (context.getFileStreamPath("settings.dat").exists()) {
         FileInputStream fIn = null;
         byte[] inputBuffer = null;
         int count = 0;
         try {
            fIn = context.openFileInput("settings.dat");
            Log.d("FileUtil", "Settings read");
            do {
               inputBuffer = new byte[512];
               count = fIn.read(inputBuffer, 0, inputBuffer.length);
               strBuffer.append(new String(inputBuffer));
            } while (count != -1);
         } catch (FileNotFoundException e) {
            Log.e("FileUtil", "settings.dat not found.");
            Toast.makeText(context, "Settings not stored", Toast.LENGTH_SHORT).show();
            return null;
         } catch (IOException e) {
            Log.e("FileUtil", "Failed to read servers from settings.dat.");
            Toast.makeText(context, "Failed to read servers from settings.dat.", Toast.LENGTH_SHORT).show();
         } finally {
            try {
               fIn.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      return strBuffer.toString().trim();
   }
   //unused
   public static void WriteSettings(Context context, String data) {
      FileOutputStream fOut = null;
      OutputStreamWriter osw = null;
         try {
            fOut = context.openFileOutput("settings.dat", Context.MODE_PRIVATE);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               osw.close();
               fOut.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
   }
   
   public static void parsePanelXML(Context context) {
      if (context.getFileStreamPath(Constants.PANEL_XML).exists()) {
         try {
            parsePanelXMLInputStream(context.openFileInput(Constants.PANEL_XML));
         } catch (FileNotFoundException e) {
            Log.e("FileUtil", "panel.xml not found.", e);
         }
      }
   }
   
   public static void parsePanelXMLInputStream(InputStream fIn) {
      try {
//         Log.d("FileUtil", "Settings read");
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(fIn);
         Element root = dom.getDocumentElement();
         NodeList nodes = root.getChildNodes();
         int nodeLength = nodes.getLength();
         XMLEntityDataBase.globalTabBar = null;
         for (int i = 0; i < nodeLength; i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE && "tabbar".equals(nodes.item(i).getNodeName())) {
               XMLEntityDataBase.globalTabBar = new TabBar(nodes.item(i));
            }
         }

         XMLEntityDataBase.screens.clear();
         NodeList screenNodes = root.getElementsByTagName("screen");
         int screenNum = screenNodes.getLength();
         for (int i = 0; i < screenNum; i++) {
            Screen screen = new Screen(screenNodes.item(i));
            XMLEntityDataBase.screens.put(screen.getScreenId(), screen);
         }

         XMLEntityDataBase.groups.clear();
         NodeList groupNodes = root.getElementsByTagName("group");
         int groupNum = groupNodes.getLength();
         for (int i = 0; i < groupNum; i++) {
            Group group = new Group(groupNodes.item(i));
            XMLEntityDataBase.groups.put(group.getGroupId(), group);
         }
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            fIn.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   public static void clearImagesInCache(Context context) {
      String[] fileNames = context.fileList();
      for (int i = 0; i < fileNames.length; i++) {
         if (fileNames[i].toLowerCase().matches("^.+\\.(png|gif|jpg|bmp)$")) {
            Log.i("CLEAR IMAGE", fileNames[i]);
            context.deleteFile(fileNames[i]);
         }
      }
   }
   
}
