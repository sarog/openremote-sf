package org.openremote.controller.servlet.sip;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

@javax.servlet.sip.annotation.SipServlet
public class PanelProxySipServlet extends SipServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(PanelProxySipServlet.class);
    private static final String CONTACT_HEADER = "Contact";
    Map<String, List<URI>> registeredUsers = null;
    
    public PanelProxySipServlet() {}

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
      if(logger.isInfoEnabled()) {
        logger.info("the panel proxy sip servlet has been started");
      }
      super.init(servletConfig);
    }

    @Override
    protected void doInvite(SipServletRequest request) throws ServletException,
        IOException {
      if(logger.isInfoEnabled()) {
        logger.info("Got request:\n" + request.toString());   
      }
      SipFactory sipFactory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
      List<URI> contactAddresses  = new ArrayList<URI>();
      @SuppressWarnings("unchecked")
      Set<String> addresses = (Set<String>) getServletContext().getAttribute("panelUsersMap");
      if (logger.isInfoEnabled()) {
        logger.info("Registered panel addresses : " + addresses);
      }
      for (String address : addresses) {
        contactAddresses.add(sipFactory.createURI(address));
      }
          
      if(contactAddresses != null && contactAddresses.size() > 0) {     
        Proxy proxy = request.getProxy();
        proxy.setRecordRoute(false); // was true
        proxy.setParallel(true);
        proxy.setSupervised(false); // was false
        proxy.proxyTo(contactAddresses);    
      } else {
        if(logger.isInfoEnabled()) {
          logger.info("No panels currently registered");
        }
        SipServletResponse sipServletResponse = 
          request.createResponse(SipServletResponse.SC_MOVED_PERMANENTLY, "Moved Permanently");
        sipServletResponse.send();
      }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void doRegister(SipServletRequest req) throws ServletException,
        IOException {
      if(logger.isInfoEnabled()) {
        logger.info("Received register request: " + req.getFrom());
      }
      
      boolean isPanelUser = false;
      if (!req.getFrom().getURI().isSipURI()) {
        // Only SIP supported
        SipServletResponse sipServletResponse = req.createResponse(SipServletResponse.SC_NOT_IMPLEMENTED);
        sipServletResponse.send();
      } else {
        logger.info("User " + ((SipURI)req.getFrom().getURI()).getUser());
        logger.info("Host " + ((SipURI)req.getFrom().getURI()).getHost());
        if ("panel".equals(((SipURI)req.getFrom().getURI()).getUser())) {
          isPanelUser = true;
        }
      }
      
      int response = SipServletResponse.SC_OK;
      SipServletResponse resp = req.createResponse(response);
      
      
      /*
      HashMap<String, String> users;
      if (isPanelUser) {
        users = (HashMap<String, String>) getServletContext().getAttribute("panelUsersMap");
        if (users == null) {
          users = new HashMap<String, String>();
          getServletContext().setAttribute("panelUsersMap", users);
        }
      } else {
        users= (HashMap<String, String>) getServletContext().getAttribute("registeredUsersMap");
        if (users == null) {
          users = new HashMap<String, String>();
          getServletContext().setAttribute("registeredUsersMap", users);
        }
      }
      
      Address address = req.getAddressHeader(CONTACT_HEADER);
      String fromURI = req.getFrom().getURI().toString();
      
      int expires = address.getExpires();
      if(expires < 0) {
        expires = req.getExpires();
      }
      if(expires == 0) {
        users.remove(fromURI);
        if(logger.isInfoEnabled()) {
          logger.info("User " + fromURI + " unregistered");
        }
      } else {
        resp.setAddressHeader(CONTACT_HEADER, address);
        users.put(fromURI, address.getURI().toString());
        if(logger.isInfoEnabled()) {
          logger.info("User " + fromURI + 
            " registered with an Expire time of " + expires);
        }
      }
      */
      
      Set<String> users;
      if (isPanelUser) {
        users = (Set<String>) getServletContext().getAttribute("panelUsersMap");
        if (users == null) {
          users = new HashSet<String>();
          getServletContext().setAttribute("panelUsersMap", users);
        }
      } else {
        users= (Set<String>) getServletContext().getAttribute("registeredUsersMap");
        if (users == null) {
          users = new HashSet<String>();
          getServletContext().setAttribute("registeredUsersMap", users);
        }
      }
      
      Address address = req.getAddressHeader(CONTACT_HEADER);
      
      int expires = address.getExpires();
      if(expires < 0) {
        expires = req.getExpires();
      }
      if(expires == 0) {
        users.remove(address.getURI().toString());
        if(logger.isInfoEnabled()) {
          logger.info("Address " + address.getURI() + " unregistered");
        }
      } else {
        resp.setAddressHeader(CONTACT_HEADER, address);
        users.add(address.getURI().toString());
        if(logger.isInfoEnabled()) {
          logger.info("Address " + address.getURI() + 
            " registered with an Expire time of " + expires);
        }
      }      
      resp.send();
    }

    @Override
    protected void doSubscribe(SipServletRequest req) throws ServletException, IOException {
      SipServletResponse resp = req.createResponse(SipServletResponse.SC_NOT_IMPLEMENTED);
      resp.send();
    }
    
    /**
     * {@inheritDoc}
     */
    protected void doResponse(SipServletResponse response)
        throws ServletException, IOException {
      if(logger.isInfoEnabled()) {
        logger.info("SimpleProxyServlet: Got response:\n" + response);
      }
      // session should not be invalidated so fast
//      if(SipServletResponse.SC_OK == response.getStatus() && "BYE".equalsIgnoreCase(response.getMethod())) {
//        SipSession sipSession = response.getSession(false);
//        if(sipSession != null) {
//          SipApplicationSession sipApplicationSession = sipSession.getApplicationSession();
//          sipSession.invalidate();
//          sipApplicationSession.invalidate();
//        }     
//      }
    }
    
  }
