/*
 * CommPortOwnershipListener.java
 * Copyright (C) 2004 The Free Software Foundation
 *
 * This file is part of GNU CommAPI, a library.
 *
 * GNU CommAPI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GNU CommAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package javax.comm;

import java.util.EventListener;

/**
 * This interface is used to receive notifications of communications port
 * ownership events.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @version 2.0.3
 */
public interface CommPortOwnershipListener extends EventListener
{

    /**
     * The port changed to the owned state.
     */
    static final int PORT_OWNED = 1;

    /**
     * The port changed to the unowned state.
     */
    static final int PORT_UNOWNED = 2;

    /**
     * The port is already owned, and another application is requesting
     * ownership.
     */
    static final int PORT_OWNERSHIP_REQUESTED = 3;

    /**
     * Notifies this listener of an ownership change event.
     * @param type one of PORT_OWNED, PORT_UNOWNED, or
     * PORT_OWNERSHIP_REQUESTED
     */
    void ownershipChange(int type);
    
}
