/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;

/*
 * Sessions listener ensuring that only the last session created for a given OR account "survives".
 *
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ExclusiveAccountSessionListener implements HttpSessionListener {

  private static final Map<Long, HttpSession> sessionsPerAccount = new ConcurrentHashMap<Long, HttpSession>();
  
  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger for user related activities.
   */
  private final static LogFacade userLog =  LogFacade.getInstance(LogFacade.Category.USER);
  
  @Override
  public void sessionCreated(HttpSessionEvent sessionEvent) {
    HttpSession session = sessionEvent.getSession();
    userLog.debug("Session created with id " + session.getId());
    
    // Some sessions are created "outside" of regular logins,
    // those don't have a authentication in their security context
    // and we just ignore them
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      Account account = getSessionAccount(session);
      
      HttpSession existingSession = sessionsPerAccount.remove(account.getOid());
      if (existingSession != null) {
        userLog.debug("Existing session (" + existingSession.getId() + ") for same account will get invalidated");
        existingSession.invalidate();
      }
      
      sessionsPerAccount.put(account.getOid(), session);
    }
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent sessionEvent) {
    HttpSession session = sessionEvent.getSession();
    
    userLog.debug("Session (" + session.getId() + ") destroyed");
    
    Long accountIdOfSessionToRemove = null;

    // For regular logouts / session timeouts, need to remove the session
    // from the map that keeps track of session per account
    // (invalidation because of duplicate login is handled when kick out).
    // Can't get the account anymore, as the session is already destroyed,
    // so iterate over all the sessions we know about and remove match.
    for (Map.Entry<Long, HttpSession> e : sessionsPerAccount.entrySet()) {
      if (e.getValue().getId().equals(session.getId())) {
        accountIdOfSessionToRemove = e.getKey();
        break;
      }
    }
    if (accountIdOfSessionToRemove != null) {
      userLog.debug("Removing session " + sessionsPerAccount.get(accountIdOfSessionToRemove).getId() + " from list");
      sessionsPerAccount.remove(accountIdOfSessionToRemove);
    }
  }

  private Account getSessionAccount(HttpSession session) {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
    
    UserService userService = (UserService) ctx.getBean("userService");
    return userService.getAccount();
  }

}
