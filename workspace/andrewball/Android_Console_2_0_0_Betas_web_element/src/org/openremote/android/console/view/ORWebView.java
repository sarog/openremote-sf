/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.android.console.view;

import java.net.URL;

import org.openremote.android.console.bindings.Web;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * OpenRemote wrapper around the standard Android WebView widget
 *
 * @author Andrew D. Ball <aball@osintegrators.com>
 */
public class ORWebView extends ComponentView
{
  private android.webkit.WebView webView;

  /**
   * This is used to prevent clicking on links in the WebView from launching the system
   * browser.
   *
   * Adapted from http://developer.android.com/resources/tutorials/views/hello-webview.html
   */
  private class ORWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }
  }

  public ORWebView(Context context, Web web)
  {
    super(context);
    setComponent(web);
    if (web != null)
    {
      // initialize the standard view here
      webView = new WebView(context);
      webView.setWebViewClient(new ORWebViewClient());
      ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(web.getFrameWidth(), web.getFrameHeight());
      webView.setLayoutParams(layoutParams);
      webView.getSettings().setJavaScriptEnabled(true);
      URL url = web.getSrc();
      if (url != null) {
        webView.loadUrl(url.toString());
      }
      addView(webView);
    }
  }
}
