/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.cache;

import java.util.Set;
import java.io.InputStream;

import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;

/**
 * Interface to define designer resource cache -- for storing images and other artifacts
 * from user's account in the designer application.  <p>
 *
 * This interface is generic and does not enforce any particular cache storage mechanism,
 * file-based, in-memory, etc.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface ResourceCache
{


  /**
   * Returns an open input stream from a zip compressed archive that contains all account
   * artifacts (xml files, images, other configuration files) that have been built based
   * on the current in-memory object model of the designer. <p>
   *
   * Note that the interface definition currently relies on the default (non-versioned)
   * object model definition. <p>
   *
   * The input stream may be backed by an in-memory archive, file-based archive or other type
   * of archive storage depending on the resource cache implementation.
   *
   * @param     panels  current, in-memory state of the account's object model
   *
   * @return    an open input stream from a zip compressed archive
   *
   * @throws CacheOperationException
   *            If there's an error that is specific to the particular resource cache
   *            implementation. Resource cache implementations may subclass this exception
   *            type for more specific error handling. The errors may also be specific
   *            just to a single account rather than to the entire Designer application.
   *
   * @throws ConfigurationException
   *            If there's a misconfiguration of the designer that prevents the cache
   *            from operating correctly. Usually this type of exception indicates an issue
   *            that requires re-deployment of the designer application and is likely to
   *            impact multiple accounts.
   */
  InputStream getExportArchiveInputStream(Set<Panel> panels)
      throws CacheOperationException, ConfigurationException;

  /**
   * Updates the resource cache state from the Beehive server.
   *
   * @throws CacheOperationException
   *            If there's an error that is specific to the particular resource cache
   *            implementation. Resource cache implementations may subclass this exception
   *            type for more specific error handling. The errors may also be specific
   *            just to a single account rather than to the entire Designer application.
   *
   * @throws NetworkException
   *            If any errors occur with the network connection to Beehive server. It may be
   *            possible to recover from network exceptions by retrying the operation.
   *            Note that the exception class provides a severity level which indicates the
   *            severity of the network error and the likelyhood it can be recovered from.
   *
   * @throws ConfigurationException
   *            If there's a misconfiguration of the designer that prevents the cache
   *            from operating correctly. Usually this type of exception indicates an issue
   *            that requires re-deployment of the designer application and is likely to
   *            impact multiple accounts.
   */
  void update()
      throws CacheOperationException, NetworkException, ConfigurationException;
}
