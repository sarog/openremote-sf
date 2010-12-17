package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Parses and represents the task configuration in the locallogic part of the panel.xml file.
 * Tasks are executed periodically as a call to a static method on the indicated class.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
@SuppressWarnings("serial")public class LocalTask extends BusinessEntity {

	private int id;
	private String className;
	private String methodName;
	private long frequency;
	
	public LocalTask(Node node) {
	      NamedNodeMap nodeMap = node.getAttributes();
	      this.id = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
	      this.className = nodeMap.getNamedItem("class").getNodeValue();
	      this.methodName = nodeMap.getNamedItem("method").getNodeValue();
	      if (nodeMap.getNamedItem("frequency") != null) {
	    	  this.frequency = Long.valueOf(nodeMap.getNamedItem("frequency").getNodeValue());
	      } else {
	    	  this.frequency = 0; // Do not repeat by default
	      }	      
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

	public long getFrequency() {
		return frequency;
	}

}
