package org.openremote.android.console.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class FileUtil {

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
   
   public static void WriteSettings(Context context, String data) {
      FileOutputStream fOut = null;
      OutputStreamWriter osw = null;
      context.deleteFile("settings.dat");
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
}
