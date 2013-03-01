package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

/**
 * Parses and represents the sensor configuration in the locallogic part of the panel.xml file.
 * Local sensors provide their feedback as a result of calling a static method on the indicated class.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
@SuppressWarnings("serial")
public class LocalSensor extends BusinessEntity {

	private int id;
	private String className;
	private String methodName;
	private long refreshRate;
	
	public LocalSensor(Node node) {
	      NamedNodeMap nodeMap = node.getAttributes();
	      this.id = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
	      this.className = nodeMap.getNamedItem("class").getNodeValue();
	      this.methodName = nodeMap.getNamedItem("method").getNodeValue();
	      if (nodeMap.getNamedItem("refreshRate") != null) {
	    	  this.refreshRate = Long.valueOf(nodeMap.getNamedItem("refreshRate").getNodeValue());
	      } else {
	    	  this.refreshRate = 10000; // Every 10 seconds by default
	      }
	      
	      Log.i("PARSE", "sensor " + id + "," + className + "," + methodName + "," + refreshRate);
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
	
	public long getRefreshRate() {
		return refreshRate;
	}
	
}
