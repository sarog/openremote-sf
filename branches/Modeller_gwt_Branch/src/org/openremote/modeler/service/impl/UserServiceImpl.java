/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.service.impl;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.UserService;
import org.springframework.security.context.SecurityContextHolder;

/**
 * The service for User.
 *
 * @author Dan 2009-7-14
 */
public class UserServiceImpl extends BaseAbstractService<User> implements UserService {

    /**
     * Gets the current account.
     *
     * @return the account
     */
    public Account getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return genericDAO.getByNonIdField(User.class, "username", username).getAccount();
    }

    /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.UserRPCService#saveUser(org.openremote.modeler.domain.User)
    */
    public void saveUser(User user) {
        genericDAO.save(user);
    }
}
