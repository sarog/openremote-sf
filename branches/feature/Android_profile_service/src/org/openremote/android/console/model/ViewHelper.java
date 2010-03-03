package org.openremote.android.console.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ViewHelper {

   public static void showAlertViewWithTitle(Context context, String title, String message) {
      AlertDialog alertDialog = new AlertDialog.Builder(context).create();
      alertDialog.setTitle(title);
      alertDialog.setMessage(message);
      alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          return;
        } }); 
      alertDialog.show();
   }
}
