/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;
import android.widget.EditText;
import android.view.KeyEvent;
import android.content.Context;
import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.R;
import org.openremote.android.console.model.AppSettingsModel;
import junit.framework.Assert;

/**
 * UI tests for application settings.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class AppSettingsActivityTest extends ActivityInstrumentationTestCase2<AppSettingsActivity>
{

  private EditText sslEditField;
  private Context ctx;

  public AppSettingsActivityTest()
  {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }

  @Override protected void setUp()
  {
    Activity activity = this.getActivity();
    ctx = getInstrumentation().getTargetContext();

    sslEditField = (EditText)activity.findViewById(R.id.ssl_port);
  }


  /**
   * Tests SSL valid port values through R.id.ssl_port EditField UI widget.
   */
  public void testValidSSLPorts()
  {
    sslEditField.setText("1234");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    Assert.assertTrue(AppSettingsModel.getSSLPort(ctx) == 1234);

    sslEditField.setText("8443");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    Assert.assertTrue(AppSettingsModel.getSSLPort(ctx) == 8443);

    sslEditField.setText("443");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    Assert.assertTrue(AppSettingsModel.getSSLPort(ctx) == 443);
  }

  /**
   * Test SSL port value limits through R.id.ssl_port EditField UI widget.
   */
  public void testSSLPortLimits()
  {
    sslEditField.setText("0");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    Assert.assertTrue(AppSettingsModel.getSSLPort(ctx) == 0);

    sslEditField.setText("65535");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    Assert.assertTrue(AppSettingsModel.getSSLPort(ctx) == 65535);
  }

  /**
   * Test SSL port values out of bounds -- incorrect user input should not allow settings
   * to change.
   */
  public void testOutOfBoundsSSLPorts()
  {
    AppSettingsModel.setSSLPort(ctx, 8443);

    sslEditField.setText("100000");
    sslEditField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

    assertTrue(AppSettingsModel.getSSLPort(ctx) == 8443);
  }
}

