/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

package org.openremote.android.controller.command;


import android.content.Context;

import org.xml.sax.InputSource;
import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.openremote.android.console.util.NamespaceContextImpl;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;

import java.util.Hashtable;
/*
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.Configuration;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.InvalidControllerXMLException;
import org.openremote.controller.utils.PathUtil;
*/

/**
 * The controller.xml Parser Based on DOM/SAX for Android.
 * 
 * @author marcf@openremote.org
 * 
 */

public class RemoteActionXMLParser {
	
	private Context context;
	
	private NamespaceContextImpl namespace;
	
	public RemoteActionXMLParser(Context context) 
	{	
		this.context = context;
		
		namespace = new NamespaceContextImpl();
		
		namespace.setNamespaceURI("or","http://www.openremote.org");
	
	}
	
	public Element getElementById(int controlID) { return getElementById((new Integer(controlID)).toString()); }
	
	public Element getElementById(String controlID) 
	 {

		try {
			
			XPath xpath = XPathFactory.newInstance().newXPath();
				
			xpath.setNamespaceContext(namespace);
			
			String expression = "//or:*[@id='" + controlID + "']";
		
			InputSource source = new InputSource(context.openFileInput("controller.xml"));
				
			Node node = (Node) xpath.evaluate(expression, source, XPathConstants.NODE);
			
			return (Element) node;
			
		}
 		 catch (Exception e) {System.out.println("COULDNT FIND THE EXPRESSION");e.printStackTrace(); return null;}		 
	 } 
}
