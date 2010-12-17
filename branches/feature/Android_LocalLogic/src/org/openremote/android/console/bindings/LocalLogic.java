package org.openremote.android.console.bindings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses and represents the locallogic part of the panel.xml file.
 * This configuration represents what needs to be performed locally in the Android Console.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
@SuppressWarnings("serial")
public class LocalLogic extends BusinessEntity {

	   private Map<Integer, LocalSensor> sensors = new HashMap<Integer, LocalSensor>();
	   private Map<Integer, LocalCommand> commands = new HashMap<Integer, LocalCommand>();
	   private Map<Integer, LocalTask> tasks = new HashMap<Integer, LocalTask>();

	   public LocalLogic(Node node) {
		   if (node.getNodeType() == Node.ELEMENT_NODE) {
		         NodeList sensorNodes = ((Element)node).getElementsByTagName("sensor");
		         for (int i = 0; i < sensorNodes.getLength(); i++) {
		        	 LocalSensor sensor = new LocalSensor(sensorNodes.item(i));
		        	 sensors.put(sensor.getId(), sensor);
		         }
		         
		         NodeList commandNodes = ((Element)node).getElementsByTagName("command");
		         for (int i = 0; i < commandNodes.getLength(); i++) {
		        	 LocalCommand command = new LocalCommand(commandNodes.item(i));
		        	 commands.put(command.getId(), command);
		         }
		         
		         NodeList taskNodes = ((Element)node).getElementsByTagName("task");
		         for (int i = 0; i < taskNodes.getLength(); i++) {
		        	 LocalTask task = new LocalTask(taskNodes.item(i));
		        	 tasks.put(task.getId(), task);
		         }
		   }
	   }

	   public LocalSensor getLocalSensor(Integer id) {
		   return sensors.get(id);
	   }
	   
	   public Collection<LocalSensor> getLocalSensors() {
		   return sensors.values();
	   }

	   public LocalCommand getLocalCommand(Integer id) {
		   return commands.get(id);
	   }
	   
	   public Collection<LocalCommand> getLocalComamnds() {
		   return commands.values();
	   }
	   
	   public LocalTask getLocalTask(Integer id) {
		   return tasks.get(id);
	   }
	   
	   public Collection<LocalTask> getLocalTasks() {
		   return tasks.values();
	   }
}
