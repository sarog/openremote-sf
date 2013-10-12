/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.cbus;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.openremote.modeler.cbus.ImportException;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.server.cbus.importmodel.Application;
import org.openremote.modeler.server.cbus.importmodel.GroupAddress;
import org.openremote.modeler.server.cbus.importmodel.Network;
import org.openremote.modeler.server.cbus.importmodel.Project;
import org.springframework.web.util.HtmlUtils;

import flexjson.HTMLEncoder;
/**
 * Imports CBus project files
 *  
 * @author Jamie Turner
 */
public class CBusImporter
{
    private final static LogFacade LOGGER =
	      LogFacade.getInstance(LogFacade.Category.SERVICE);
    
    /**
     * Imports a CBus project file
     * 
     * @param configurationStream
     * The stream linked to the project file
     * 
     * @return a project
     * @throws ImportException
     */
    public static Project importXMLConfiguration(InputStream configurationStream) throws ImportException 
    {
	Document protocolDoc = null;
	SAXReader reader = new SAXReader();
	SAXParserFactory factory = SAXParserFactory.newInstance();
	factory.setValidating(true);
	factory.setNamespaceAware(true);

	Project project = null;

	try 
	{
	    protocolDoc = reader.read(configurationStream);

	    //get the project first
	    Element projectElement = (Element) protocolDoc.selectSingleNode("//Installation/Project");
	    if (projectElement == null || projectElement.element("TagName") == null) 
	    {
		throw new ImportException("Invalid file format", null);
	    }

	    String projectName = projectElement.valueOf("TagName");
	    if(projectName != null && !containsIllegalChars(projectName))
	    {
		project = new Project(projectName);
		LOGGER.debug(new StringBuilder("CBUS Project: ").append(projectName).toString());

		//get the network
		List networkResult =  projectElement.selectNodes("Network");
		if(networkResult != null)
		{
		    for (Iterator nIterator = networkResult.iterator(); nIterator.hasNext(); )
		    {
			Element networkElement = (Element) nIterator.next();
			String networkName = networkElement.valueOf("TagName");
			if(networkName != null && !containsIllegalChars(networkName))
			{

			    Network network = new Network(networkName);
			    project.addNetwork(network);

			    LOGGER.debug(new StringBuilder("CBUS Network: ").append(networkName).toString());

			    // Query all GroupAddress elements for the network
			    List applicationResult =  networkElement.selectNodes("Application");
			    for (Iterator aIterator = applicationResult.iterator(); aIterator.hasNext(); )
			    {
				Element appElement = (Element) aIterator.next();
				String applicationName = appElement.valueOf("TagName");
				
				if(applicationName != null && !containsIllegalChars(applicationName))
				{

				    Application app = new Application(applicationName);
				    network.addApplication(app);

				    LOGGER.debug(new StringBuilder("CBUS Application: ").append(applicationName).toString());

				    List groupAddressResult =  appElement.selectNodes("Group");
				    for (Iterator gIterator = groupAddressResult.iterator(); gIterator.hasNext(); )
				    {
					Element groupElement = (Element) gIterator.next();
					String groupName = groupElement.valueOf("TagName");
					if(groupName != null && !containsIllegalChars(groupName))
					{
					    String groupAddress = groupElement.valueOf("Address");

					    GroupAddress group = new GroupAddress(groupName, groupAddress);
					    app.addGroupAddress(group);


					    LOGGER.debug(new StringBuilder("Created CBUS Group: ").append(groupName).append(" addr: ").append(groupAddress).toString());		    
					}
					else
					    LOGGER.warn("Group name contains illegal chars and can't be imported: " + groupName);
				    }

				}
				else
				    LOGGER.warn("Application name contains illegal chars and can't be imported: " + applicationName);
			    }
			}
			else
			    LOGGER.warn("Network name contains illegal chars and can't be imported: " + networkName);
		    }

		}
		else 
		    throw new ImportException("No networks found in project file!", null);
	    }
	    else
		throw new ImportException("Project name contain illegal characters: " + projectName, null);
	} 
	catch (DocumentException e) 
	{
	    throw new ImportException("Error parsing file", e);
	}

	return project;

    }

    /**
     * Check for illegal characters in CBus tag names that can cause issues with JSON parsing
     * @param test
     * @return
     */
    private static boolean containsIllegalChars(String test)
    {
	return test.contains("<") || test.contains(">");
    }
}

