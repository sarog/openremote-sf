/**
 * 
 */
package org.openremote.console.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * The purpose of this class is to proxy all controller requests from the
 * browser to the controller. This is a simple method of getting around the
 * same-origin policy of browsers. The logic currently assumes the controller is
 * running on port 8080 of the same machine as the web console.
 * 
 * @author David Reines
 */
public class ControllerProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// TODO: create unit tests
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO: this is a quick implementation to get things working, need to
		// support more urls and create a more robust proxy
		HttpClient client = null;
		BufferedReader reader = null;
		try {
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet(
					"http://localhost:8080/controller/iphone.xml");
			HttpResponse httpResponse = client.execute(get);
			HttpEntity entity = httpResponse.getEntity();
			reader = new BufferedReader(new InputStreamReader(entity
					.getContent()));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				resp.getWriter().write(line);
			}
		} finally {
			close(reader, client);
		}
	}

	private void close(Reader reader, HttpClient client) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
		}
		try {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO add code to support POSTs.
		super.doPost(req, resp);
	}

}
