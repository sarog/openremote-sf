package org.openremote.controller.statuscache;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class CSVDataLogger extends EventProcessor {

   @Override
   public void push(EventContext ctx) {
      String sensorName = ctx.getEvent().getSource();
      
      if (sensorName == null) {
         return;
      }
      if (!sensorName.startsWith("LOG-")) {
         return;
      }

      Writer output = null;
      PrintWriter pw = null;
      try {
         output = new BufferedWriter(new FileWriter("sensor_values.csv", true));
         pw = new PrintWriter(output);
         
         long newUpdate = System.currentTimeMillis();
         pw.println(sensorName + "," + Long.toString(newUpdate) + "," + ctx.getEvent().getValue());
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } finally {
         if (pw != null) {
            pw.close();
         }
        if (output != null) {
          try {
            output.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         }
      }
   }

   @Override
   public String getName() {
      return "CSVDataLogger";
   }

}
