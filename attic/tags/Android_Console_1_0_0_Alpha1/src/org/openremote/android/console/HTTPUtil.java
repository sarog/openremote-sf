/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
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
package org.openremote.android.console;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Does the HTTP stuff, anything related to HttpClient should go here.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class HTTPUtil {

    public static final String CLICK = "click";
    public static final String PRESS = "press";
    public static final String RELEASE = "release";

    /**
     * Sends a button action of some kind
     * 
     * @param url
     * @param id
     * @param command
     * @return http_status_code
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static int sendButton(String url, String id, String command)
            throws ClientProtocolException, IOException {
        String connectString = url + "/rest/button/" + id + "/" + command;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(connectString);
        HttpResponse response = client.execute(post);
        int code = response.getStatusLine().getStatusCode();
        return code;
    }

}
