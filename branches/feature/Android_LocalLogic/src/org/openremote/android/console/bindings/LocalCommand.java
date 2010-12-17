package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Parses and represents the command configuration in the locallogic part of the panel.xml file.
 * Such a command is executed locally by calling a static method on the indicated class.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
@SuppressWarnings("serial")
public class LocalCommand extends BusinessEntity {

	private int id;
	private String className;
	private String methodName;
	
	public LocalCommand(Node node) {
	      NamedNodeMap nodeMap = node.getAttributes();
	      this.id = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
	      this.className = nodeMap.getNamedItem("class").getNodeValue();
	      this.methodName = nodeMap.getNamedItem("method").getNodeValue();
	      
	      // TODO: do we need to have child nodes for parameters ?
	      
	}

	public int getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}
	

}
