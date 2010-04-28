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

package org.openremote.android.console.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * This is ORConnectionDelegate implementation for GetPanels.
 * 
 * @deprecated
 * @author handy 2010-04-27
 *
 */
public class ORGetPanelsConnectionDelegate implements ORConnectionDelegate {
	
	List<String> panelsName = new ArrayList<String>();
	
	@Override
	public void urlConnectionDidFailWithException(Exception e) {

	}

	@Override
	public void urlConnectionDidReceiveData(InputStream data) {
		try{
		   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		   DocumentBuilder builder = factory.newDocumentBuilder();
		   Document dom = builder.parse(data);
		   Element root = dom.getDocumentElement();
		   
		   NodeList nodeList = root.getElementsByTagName("panel");
		   int nodeNums = nodeList.getLength();
		   for (int i = 0; i < nodeNums; i++) {
			   panelsName.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
		   }       
		} catch (IOException e) {
			Log.e("ERROR", "The data is from ORConnection is bad", e);
		} catch (ParserConfigurationException e) {
			Log.e("ERROR", "Cant build new Document builder", e);
		} catch (SAXException e) {
			Log.e("ERROR", "Parse data error", e);
		}
	}

	@Override
	public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {

	}

	public List<String> getPanelsName() {
		return panelsName;
	}

}
