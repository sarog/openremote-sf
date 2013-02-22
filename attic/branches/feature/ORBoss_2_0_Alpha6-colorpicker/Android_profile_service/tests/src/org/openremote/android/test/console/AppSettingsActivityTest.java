package org.openremote.android.test.console;

import org.openremote.android.console.AppSettingsActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class AppSettingsActivityTest extends ActivityInstrumentationTestCase2<AppSettingsActivity> {

   public AppSettingsActivityTest() {
      super("org.openremote.android.console", AppSettingsActivity.class);
   }
   
   @MediumTest
   public void testSum() {
      Log.e("==========", "-----------------");
   }
}
