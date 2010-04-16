package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.exception.NoSuchPanelException;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.spring.SpringContext;

/**
 * Get panel.xml by profile (panel name).
 * 
 * @author Javen, Dan Cong
 *
 */
public class GetProfileRestServlet extends HttpServlet {
   private static final Logger logger = Logger.getLogger(GetProfileRestServlet.class);
   private static final ProfileService profileService = (ProfileService) SpringContext.getInstance().getBean("profileService");

   private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   response.setCharacterEncoding("utf8");
	   PrintWriter out = response.getWriter();
      String url = request.getRequestURL().toString().trim();
      String regexp = "rest\\/panel\\/(.*)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      
      if(matcher.find()){
         try{
            String panelName = matcher.group(1);
            String decodedPanelName = panelName;
            decodedPanelName = URLDecoder.decode(panelName, "UTF-8");
            String panleXML = profileService.getProfileByPanelName(decodedPanelName);
            out.print(panleXML);
            out.flush();
            out.close();
         } catch (NoSuchPanelException e) {
            logger.error("failed to extract panel.xml for panel : " + e.getLocalizedMessage());
            response.sendError(e.getErrorCode(),e.getMessage());
         }
      } else {
         response.sendError(400,"Bad REST Request, should be /rest/panel/{panelName}");
      }
	}

}
