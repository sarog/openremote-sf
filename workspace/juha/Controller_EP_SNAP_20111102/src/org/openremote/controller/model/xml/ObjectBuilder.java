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
package org.openremote.controller.model.xml;

import org.openremote.controller.service.Deployer;
import org.openremote.controller.exception.InitializationException;
import org.jdom.Element;

/**
 * Abstract superclass for implementations that participate in XML to Java mapping.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class ObjectBuilder<T>
{

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Deployer instance this builder is registered with.
   */
  private Deployer deployer;

  
  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new builder and automatically registers it with a given deployer.
   *
   * @param deployer    deployer instance to register with
   */
  protected ObjectBuilder(Deployer deployer)
  {
    this.deployer = deployer;

    deployer.registerObjectBuilder(this);
  }


  // Abstract Methods -----------------------------------------------------------------------------

  /**
   * The actual builder implementation. Currently with JDOM dependency.
   *
   * @param segmentChildElements  root element to parse Java object from
   *
   * @return  Java instance
   *
   * @throws InitializationException
   *              if the build process encounters an irrecovable error
   */
  public abstract T build(Element segmentChildElements) throws InitializationException;

  /**
   * Returns the version of the schema the builder should be associated with.
   *
   * @see org.openremote.controller.service.Deployer.ControllerSchemaVersion
   *
   * @return  schema version
   */
  public abstract Deployer.ControllerSchemaVersion getSchemaVersion();

  /**
   * Returns the XML segment this builder uses to construct Java instances.
   *
   * @see org.openremote.controller.service.Deployer.XMLSegment
   *
   * @return  XML segment identifier
   */
  public abstract Deployer.XMLSegment getRootSegment();



  // Protected Instance Methods -------------------------------------------------------------------


  /**
   * Returns a reference to the deployer instance this builder is registered with.
   *
   * @return  deployer reference
   */
  protected Deployer getDeployer()
  {
    return deployer;
  }
}

