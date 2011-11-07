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
package org.openremote.controller.deployer;


import org.jdom.Document;
import org.openremote.controller.exception.InitializationException;

/**
 * Model builders are sequences of actions which construct the controller's object model.
 * Therefore it implements a strategy pattern. Different model builders may act on differently
 * structured XML document instances. <p>
 *
 * The implementation of a model builder is expected not only to create the Java object instances
 * representing the object model, but also initialize, register and start all the created
 * resources as necessary. On returning from the {@link #buildModel()} method, the controller's
 * object model is expected to be running and fully functional.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface ModelBuilder
{
  /*
   *  TODO:
   *
   *    ORCJAVA-183 (http://jira.openremote.org/browse/ORCJAVA-183) : Refactor configuration
   *    service as part of the model
   *    ORCJAVA-185 (http://jira.openremote.org/browse/ORCJAVA-185) : Refactor object builders
   *    as dependants of ModelBuilder
   *    ORCJAVA-186 (http://jira.openremote.org/browse/ORCJAVA-186) : Introduce common superclass
   *
   *
   */


  /**
   * Responsible for constructing the controller's object model. Implementation details
   * vary depending on the schema and source of defining artifacts.
   */
  void buildModel();


  /**
   * Model builder (schema) specific implementation to determine whether the controller
   * definition artifacts have changed in such a way that should result in redeploying the
   * object model.
   *
   * @see org.openremote.controller.service.Deployer.ControllerDefinitionWatch
   *
   * @return  true if the object model should be reloaded, false otherwise
   */
  boolean hasControllerDefinitionChanged();


  /**
   * TODO :
   *
   *   This signature is temporary and will go away, see comments in Version20ModelBuilder,
   *   Deployer.queryElementByID() and Deployer.queryElementByName() methods.
   *
   */
  Document getControllerDocument() throws InitializationException;
}
