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

package org.openremote.android.test.console.view;

import junitx.util.PrivateAccessor;

import org.openremote.android.console.bindings.Web;
import org.openremote.android.console.view.ORWebView;
import org.openremote.android.test.TestUtils;

import android.view.ViewGroup;
import android.webkit.WebView;
import android.test.AndroidTestCase;

/**
 * Tests the {@link org.openremote.android.console.view.ORWebView} class.
 *
 * @author <a href="mailto:aball@osintegrators.com">Andrew Ball</a>
 */
public class ORWebViewTest extends AndroidTestCase
{

  /** Tests constructing an ORWebView from a Web binding object. */
  public void testConstructor() throws NoSuchFieldException
  {
    final int id = 12;
    final String url = "http://google.com/";
    final String username = "fozzy";
    final String password = "bear";
    final String xmlText = "<web id='" + id + "' src='" + url + "' username='" + username +
        "' password='" + password + "' />";

    // The width and height of the Web binding object would typically be set by the
    // AbsoluteContainerView constructor.
    final int width = 200;
    final int height = 300;

    Web web = new Web(TestUtils.parseXml(xmlText));
    web.setFrameWidth(width);
    web.setFrameHeight(height);
    ORWebView orWebView = new ORWebView(getContext(), web);
    assertEquals(web, (Web) orWebView.getComponent());
    WebView webView = (WebView) PrivateAccessor.getField(orWebView, "webView");
    assertNotNull(webView);
    ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
    assertEquals(width, layoutParams.width);
    assertEquals(height, layoutParams.height);
    assertEquals(true, webView.getSettings().getJavaScriptEnabled());

    assertEquals(1, orWebView.getChildCount());
    assertEquals(webView, (WebView) orWebView.getChildAt(0));
  }

}
