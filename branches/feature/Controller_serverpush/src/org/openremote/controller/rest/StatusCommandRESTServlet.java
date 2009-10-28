/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.exception.ButtonCommandException;
import org.openremote.controller.service.StatusCommandService;
import org.openremote.controller.spring.SpringContext;

/**
 * The Class Status Command REST Servlet.
 * 
 * This servlet is responsible for 
 *   parsing RESTful url "http://xxx.xxx.xxx/controller/rest/status/{control_id},{control_id}...",
 *   building Event,
 *   status query with stateful KNXStatusEvent,
 *   and conpose status result into XML formatted data to RESTful service caller.
 * 
 * @author Handy.Wang
 */
@SuppressWarnings("serial")
public class StatusCommandRESTServlet extends HttpServlet {

    /** The logger. */
    private Logger logger = Logger.getLogger(StatusCommandRESTServlet.class.getName());

    /** The control command service. */
    private static StatusCommandService statusCommandService = (StatusCommandService) SpringContext.getInstance().getBean("statusCommandService");

    /*
     * (non-Javadoc)
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doPost(req, resp);
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String url = request.getRequestURL().toString();
        String regexp = "rest\\/status\\/(.*)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(url);
        String unParsedcontrolIDs = null;

        if (matcher.find()) {
            unParsedcontrolIDs = matcher.group(1);
            try {
                if (unParsedcontrolIDs != null && !"".equals(unParsedcontrolIDs)) {
                    PrintWriter printWriter = response.getWriter();
                    printWriter.write(statusCommandService.readFromCache(unParsedcontrolIDs));
                }
            } catch (ButtonCommandException e) {
                logger.error("CommandException occurs", e);
                response.sendError(e.getErrorCode(), e.getMessage());
            }
        } else {
            response.sendError(400, "Bad REST Request, should be /rest/status/{button_id},{button_id}...");
        }
    }
}
