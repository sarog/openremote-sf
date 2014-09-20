/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.web.console.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet acts as a simple image proxy that supports HTTP Basic
 * authentication through GET parameters. Images are returned in PNG
 * format.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class ImageProxyService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1924138127602370008L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		// Get username, password and image url from GET parameters
		String userpass = request.getParameter("userpass");
		String urlStr = request.getParameter("url");
		
		// Get the image
		try {
	    URL url = new URL(urlStr);
	    URLConnection connection = url.openConnection();
      connection.setDoInput(true);
      
	    if (userpass != null) {
  			String basicAuth = "Basic " + userpass;
  			connection.setRequestProperty ("Authorization", basicAuth);
	    }

	    InputStream is = connection.getInputStream();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	      outputStream.write(b, 0, length);
	    }

	    is.close();
	    outputStream.close();
	    
      response.getOutputStream().write(outputStream.toByteArray());
      response.getOutputStream().flush();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
