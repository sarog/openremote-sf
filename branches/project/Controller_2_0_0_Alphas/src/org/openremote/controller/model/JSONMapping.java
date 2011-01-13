/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.model;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import net.sf.json.util.JSONTokener;

/**
 * JSON utilities for model classes. This implementation uses the json-lib API.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class JSONMapping
{

  /*
   * IMPLEMENTATION NOTE:
   *
   *   Android has natively org.json API available which we should migrate to here too
   *   for ease of portability and to keep binary size to a minimum. The APIs are very
   *   similar although not exactly identical. Switch over to another API should be
   *   fairly trivial work.
   *                                                                                  [JPL]
   */


  public static Object getJSONRoot(InputStream in) throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuffer buffer = new StringBuffer(8192);

    while (true)
    {
      String nextLine = reader.readLine();

      if (nextLine == null)
        break;

      buffer.append(nextLine);
    }

    JSONTokener t = new JSONTokener(buffer.toString());

    return t.nextValue();
  }
}

