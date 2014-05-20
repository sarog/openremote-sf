/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.datalogger.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openremote.datalogger.connector.DataConnector;
import org.openremote.datalogger.connector.DynamoDBConnector;
import org.openremote.datalogger.exception.*;
import org.openremote.datalogger.model.*;

/**
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class RESTAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataConnector connector;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RESTAPI() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException
    {
    	// Initialise the data connector - use DI long term
    	connector = new DynamoDBConnector();
    	try {
				connector.init();
			} catch (DataConnectorException e) {
				throw new ServletException("Failed to initialise the Data Connector");
			}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This is a request to get data
		initialiseResponse(response, "application/xml");
		
		// Get API Key
		String apiKey = request.getParameter("apiKey");
		apiKey = apiKey == null || apiKey.isEmpty() ? request.getHeader("X-ApiKey") : apiKey;
		apiKey = apiKey == null ? "" : apiKey.toLowerCase();
		String sensorName = request.getParameter("sensorName");
		sensorName = sensorName == null ? "" : sensorName.toLowerCase();
		String function = request.getParameter("function");
		function = function == null ? "" : function.toLowerCase();
		String intervalStr = request.getParameter("interval");
		String intervalUnitsStr = request.getParameter("intervalUnits");
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String format = request.getParameter("format");
		
		Date fromDate = null, toDate = new Date();
    Long interval = null;
    
    // Check API Key
    if (apiKey == null || apiKey.isEmpty()) {
      doResponse(response, ResponseType.WARNING, new DataSecurityException("No API Key provided"));
      return;
    }
    
    // Check sensor name
    if (sensorName == null || sensorName.isEmpty()) {
      doResponse(response, ResponseType.WARNING, new DataConnectorException("No Sensor name provided"));
      return;
    }
    
    // Check function
    List<String> functionTypes = new ArrayList<String>(Arrays.asList(new String[] {"latest", "average"}));
    
    if (function == null || function.isEmpty() || functionTypes.indexOf(function) > 0)
    {
      doResponse(response, ResponseType.WARNING, new DataConnectorException("A valid function type must be specified (valid values are: latest, average)"));
      return;
    }
    
    SensorOutputValue sensorOutput = null;
    
    try {
      if (function.equals("latest")) {
        sensorOutput = connector.getLatestSensorValue(apiKey, sensorName);
      } else if (function.equals("average")) {
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
          // Look for interval
          if (intervalStr == null || intervalStr.isEmpty()) {
            doResponse(response, ResponseType.WARNING, new Exception("Either interval or from / to parameters must be set"));
            return;
          }
          
          // Check interval parameter
          try {
            interval = Long.parseLong(intervalStr);
          } catch (NumberFormatException e) {
            doResponse(response, ResponseType.WARNING, new Exception("Interval must be an integer"));
            return;
          }
  
          // Check interval units
          if (intervalUnitsStr == null || intervalUnitsStr.isEmpty()) {
            intervalUnitsStr = "s";
          }
          
          intervalUnitsStr = intervalUnitsStr.toLowerCase();
          
  
          if (intervalUnitsStr.equals("s")) {
            interval = interval*1000;
          } else if (intervalUnitsStr.equals("m")) {
            interval = interval*60000;
          } else if (intervalUnitsStr.equals("h")) {
            interval = interval*3600000;
          } else {
            doResponse(response, ResponseType.WARNING, new Exception("Invalid interval units must be either s (seconds), m (minutes) or h (hours)"));
          }
          
          fromDate = new Date(System.currentTimeMillis() - interval);
        } else {
          try {
            SimpleDateFormat formatter = new SimpleDateFormat();
            toDate = formatter.parse(to);
            fromDate = formatter.parse(from);
          } catch (ParseException e) {
            doResponse(response, ResponseType.WARNING, new Exception("Invalid from or to date values"));
            return;
          }
        }
        
        if (fromDate == null || toDate == null) {
          return;
        }
      
        Double averageValue = connector.getAverageSensorValue(apiKey, sensorName, fromDate, toDate);
        averageValue = averageValue == null ? 0d : averageValue;
        sensorOutput = new SensorOutputValue();
        sensorOutput.setName(sensorName);
        String valStr = format != null ? String.format(format,averageValue) : Double.toString(averageValue); 
        sensorOutput.setValue(valStr);
      }
      
      if (sensorOutput != null) {
        JAXBContext jaxbContext = JAXBContext.newInstance(SensorOutputValue.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.marshal(sensorOutput, response.getOutputStream());
      }
        // Just use generic error response for now 
      } catch (DataSecurityException e) {
        doResponse(response, ResponseType.WARNING, e);
      } catch (DataConnectorException e) {
        doResponse(response, ResponseType.WARNING, e);
      } catch (Exception e) {
        doResponse(response, ResponseType.ERROR, e);
      }
    }

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This is a request to set data so process the request data
		initialiseResponse(response, "application/xml");
		
		// Get API Key
    String apiKey = request.getParameter("apiKey");
    apiKey = apiKey == null || apiKey.isEmpty() ? request.getHeader("X-ApiKey") : apiKey;
    apiKey = apiKey == null ? "" : apiKey.toLowerCase();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Sensors.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Sensors dataArr = (Sensors)jaxbUnmarshaller.unmarshal(request.getInputStream());
			
			if (dataArr != null) {
				// Update each sensor
				for(Sensor sensor : dataArr.getSensors()) {
					if (sensor != null) {
						updateSensor(apiKey, sensor);
					}
				}
			}
						
			response.setStatus(200);
			
			// Just use generic error response for now 
		} catch (JAXBException e) {
			doResponse(response, ResponseType.ERROR, e);
		} catch (DataSecurityException e) {
			doResponse(response, ResponseType.WARNING, e);
		} catch (DataConnectorException e) {
			doResponse(response, ResponseType.WARNING, e);
		}
	}
	
	/*
	 * Add the data to the logger via the data connector
	 */
	private void updateSensor(String apiKey, Sensor sensor) throws DataSecurityException, DataConnectorException {
		Set<SensorValue> sensorValues = new HashSet<SensorValue>();

		if (sensor != null) {
			String sensorName = sensor.getName();
			if (sensorName != null && !sensorName.isEmpty()) {
				if (sensor.getNewSensorValues() != null) {
					sensorValues.addAll(sensor.getNewSensorValues());
				}
				
				if (sensorValues.size() > 0 || (sensor.getCurrentValue() != null && !sensor.getCurrentValue().isEmpty())) {
					connector.addSensorValues(apiKey, sensorName, sensorValues, sensor.getCurrentValue());
				}
			}
		}
	}
	
	private void initialiseResponse(HttpServletResponse response, String contentType) {
		// Set character encoding...
    response.setCharacterEncoding("UTF-8");
    
    // Set response type
    if (contentType != null && !contentType.isEmpty()) {
    	response.setContentType(contentType);
    }
	}
	
	private void doResponse(HttpServletResponse response, ResponseType responseType, Exception e) {
		WarningResponse reply = null; 
		Class<?> replyClass = null;
		
		if (e != null) {
			switch(responseType) {
				case ERROR:
					ErrorResponse error = new ErrorResponse();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					error.setException(sw.toString());
					error.setMessage(e.getMessage());
					replyClass = ErrorResponse.class;
					reply = error;
					break;
				default:
					reply = new WarningResponse();
					reply.setMessage(e.getMessage());
					replyClass = WarningResponse.class;
					break;
			}
		}
			
		try {
			Marshaller jaxbMarshaller = getJAXBMarshaller(replyClass);
			jaxbMarshaller.marshal(reply, response.getOutputStream());
		}
		catch (Exception ex) {
			// TODO: Log this problem
		}
	}
	
	private Marshaller getJAXBMarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return jaxbMarshaller;
	}
}
