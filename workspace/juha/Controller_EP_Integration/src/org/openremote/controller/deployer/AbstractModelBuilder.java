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

import java.util.List;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.InitializationException;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 * A common superclass implementation for different model builder implementations to share/reuse
 * code.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
abstract class AbstractModelBuilder implements ModelBuilder
{

  /*
   *  TODO:
   *
   *    ORCJAVA-183 (http://jira.openremote.org/browse/ORCJAVA-183) : Refactor configuration
   *    service as part of the model
   *
   */


  // Constants ------------------------------------------------------------------------------------


  /**
   * XML namespace definition for OpenRemote XML elements.
   */
  public final static Namespace OPENREMOTE_NAMESPACE = Namespace.getNamespace(
      "or",                            // prefix
      "http://www.openremote.org"      // namespace identifier
  );



  // Class Members --------------------------------------------------------------------------------


  /**
   * Common log category for startup logging, with a specific sub-category for deployer.
   */
  protected final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);




  // Protected Instance Methods -------------------------------------------------------------------


  /**
   * Utility method to execute a given XPath expression on the controller's XML definition.
   * This implementation is limited to XPath expressions that target XML elements only.
   *
   * @param   xPath   XPath expression to return a single XML element
   *
   * @return  One XML element or <tt>null</tt> if nothing was found
   *
   * @throws org.openremote.controller.exception.XMLParsingException   if there were errors creating the XPath expression
   *                                or executing it
   */
  protected Element queryElementFromXML(String xPath) throws InitializationException
  {
    Document doc = getControllerXMLDefinition();

    if (doc == null)
    {
      throw new XMLParsingException(
          "Cannot execute XPath expression ''{0}'' -- XML document instance was null.", xPath
      );
    }

    if (xPath == null || xPath.equals(""))
    {
      throw new XMLParsingException(
          "Null or empty XPath expression for document {0}", doc
      );
    }

    try
    {
      XPath xpath = XPath.newInstance(xPath);
      //xpath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
      xpath.addNamespace(OPENREMOTE_NAMESPACE);

      List elements = xpath.selectNodes(doc);

      if (!elements.isEmpty())
      {
        if (elements.size() > 1)
        {
          throw new XMLParsingException(
              "Expression ''{0}'' matches more than one element : {1}",
              xPath, elements.size()
          );
        }

        Object o = elements.get(0);

        if (o instanceof Element)
        {
          return (Element)o;
        }

        else
        {
          throw new XMLParsingException(
              "XPath query is expected to only return Element types, got ''{0}''", o.getClass()
          );
        }
      }

      else
      {
        return null;
      }
    }

    catch (JDOMException e)
    {
      throw new XMLParsingException(
          "XPath evaluation ''{0}'' failed : {1}", e, xPath, e.getMessage()
      );
    }
  }


  /**
   * Subclasses should implement to return an XML document instance that contains the controller's
   * XML definition. This is used by methods that operate on the XML document, such as
   * {@link #queryElementFromXML(String)}.
   *
   * @return    reference to XML document instance that contains controller's definition
   *
   *
   * @throws    InitializationException   if the XML document reference cannot be retrieved for
   *                                      any reason
   */
  protected abstract Document getControllerXMLDefinition() throws InitializationException;

}

