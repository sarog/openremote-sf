package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.spring.SpringContext;

public class ShowPanelsRestServlet extends HttpServlet {
   
   private static final Log logger = LogFactory.getLog(ShowPanelsRestServlet.class);
	private static final ProfileService profileService = (ProfileService) SpringContext.getInstance().getBean("profileService");
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   logger.info("user want to get all the panels.....");
	   PrintWriter out = response.getWriter();
	   String url = request.getRequestURL().toString();
      String regexp = "rest\\/panels";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      
      if(matcher.find()){
         try{
            String panlesXML = profileService.getPanelsXML();
            out.print(panlesXML);
            out.flush();
            out.close();
         } catch(ControlCommandException e){
            logger.error("failed to get all the panels",e);
            response.sendError(e.getErrorCode(),e.getMessage());
         }
      }
	}

}
