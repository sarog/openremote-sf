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
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.exception.NoSuchPanelException;
import org.openremote.controller.rest.support.xml.RESTfulErrorCodeComposer;
import org.openremote.controller.rest.support.json.JSONTranslator;
import org.openremote.controller.rest.support.xml.RESTfulErrorCodeComposer;
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

	    response.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF8);
	    response.setContentType(Constants.HTTP_HEADER_ACCEPT_XML_TYPE);

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
            out.print(JSONTranslator.toDesiredData(request, response, panleXML));
         } catch (ControlCommandException e) {
            logger.error("failed to extract panel.xml for panel : " + e.getMessage(), e);
            response.setStatus(e.getErrorCode());
            out.print(JSONTranslator.toDesiredData(request, response, e.getErrorCode(), RESTfulErrorCodeComposer.composeXMLFormatStatusCode(e.getErrorCode(), e.getMessage())));
         }
      } else {
         response.setStatus(400);
         out.print(JSONTranslator.toDesiredData(request, response, 400, RESTfulErrorCodeComposer.composeXMLFormatStatusCode(400, "Bad REST Request, should be /rest/panel/{panelName}")));
      }
      out.flush();
	}

}
