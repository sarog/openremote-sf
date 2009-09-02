/**
 * 
 */
package org.openremote.console.web.client.def;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Parses the iphone.xml into an instance of UiDef.
 * 
 * @author David Reines
 */
// TODO: create unit test
public class UiXmlParser {

	private String xml;

	public UiXmlParser(String xml) {
		this.xml = xml;
	}

	public UiDef parse() {
		UiDef uiDef = new UiDef();
		Document doc = XMLParser.parse(xml);
		parseActivities(doc, uiDef);
		return uiDef;
	}

	private void parseActivities(Document doc, UiDef uiDef) {
		NodeList activityNodes = doc.getElementsByTagName("activity");
		for (int i = 0; i < activityNodes.getLength(); i++) {
			Node activityNode = activityNodes.item(i);
			Integer id = Integer.parseInt(getAttrValue(activityNode, "id"));
			String name = getAttrValue(activityNode, "name");
			ActivityDef activity = new ActivityDef(id, name);
			uiDef.addActivityDef(activity);
			parseScreens(activityNode, activity);
		}
	}

	private void parseScreens(Node activityNode, ActivityDef activity) {
		NodeList screenNodes = activityNode.getChildNodes();
		for (int i = 0; i < screenNodes.getLength(); i++) {
			Node screenNode = screenNodes.item(i);
			if (isElement(screenNode)) {
				GWT.log(screenNode.getNodeName(), null);
				Integer id = Integer.parseInt(getAttrValue(screenNode, "id"));
				String name = getAttrValue(screenNode, "name");
				Integer row = Integer.parseInt(getAttrValue(screenNode, "row"));
				Integer column = Integer.parseInt(getAttrValue(screenNode,
						"col"));
				ScreenDef screen = new ScreenDef(id, name, row, column);
				activity.addScreenDef(screen);
				parseButtonList(screenNode, screen);
			}
		}
	}

	private void parseButtonList(Node screenNode, ScreenDef screen) {
		NodeList buttonListNodes = screenNode.getChildNodes();
		for (int i = 0; i < buttonListNodes.getLength(); i++) {
			Node buttonListNode = buttonListNodes.item(i);
			if (isElement(buttonListNode)) {
				parseButtons(buttonListNode, screen);
			}
		}
	}

	private void parseButtons(Node buttonListNode, ScreenDef screen) {
		NodeList buttonNodes = buttonListNode.getChildNodes();
		for (int i = 0; i < buttonNodes.getLength(); i++) {
			Node buttonNode = buttonNodes.item(i);
			if (isElement(buttonNode)) {
				Integer id = Integer.parseInt(getAttrValue(buttonNode, "id"));
				String label = getAttrValue(buttonNode, "label");
				Integer x = Integer.parseInt(getAttrValue(buttonNode, "x"));
				Integer y = Integer.parseInt(getAttrValue(buttonNode, "y"));
				Integer width = Integer.parseInt(getAttrValue(buttonNode,
						"width"));
				Integer height = Integer.parseInt(getAttrValue(buttonNode,
						"height"));
				ButtonDef button = new ButtonDef(id, label, x, y, width, height);
				String icon = getAttrValue(buttonNode, "icon");
				// icon is optional
				button.setIcon(icon);
				screen.addButtonDef(button);
			}
		}
	}

	// private void parseRows(Node gridNode, Grid grid) {
	// NodeList rowNodes = gridNode.getChildNodes();
	// for (int i = 0; i < rowNodes.getLength(); i++) {
	// Node rowNode = rowNodes.item(i);
	// Row row = new Row();
	// grid.addRow(row);
	// parseCells(rowNode, row);
	// }
	// }
	//
	// private void parseCells(Node rowNode, Row row) {
	// NodeList cellNodes = rowNode.getChildNodes();
	// for (int i = 0; i < cellNodes.getLength(); i++) {
	// Node cellNode = cellNodes.item(i);
	// Cell cell = new Cell();
	// Node iconNode = cellNode.getFirstChild();
	// if (iconNode != null) {
	// Icon icon = new Icon();
	// icon.setId(getAttrValue(iconNode, "id"));
	// icon.setSource(getAttrValue(iconNode, "source"));
	// cell.setIcon(icon);
	// }
	// row.addCell(cell);
	// }
	// }

	private String getAttrValue(Node node, String name) {
		Node attr = node.getAttributes().getNamedItem(name);
		if (attr != null) {
			return attr.getNodeValue();
		}
		return null;
	}

	private boolean isElement(Node node) {
		return node.getNodeType() == Node.ELEMENT_NODE;
	}

}
