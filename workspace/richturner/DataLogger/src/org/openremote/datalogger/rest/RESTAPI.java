package org.openremote.datalogger.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.openremote.datalogger.connector.HibernateDataConnector;
import org.openremote.datalogger.exception.*;
import org.openremote.datalogger.model.*;
import org.openremote.datalogger.model.ErrorResponse;


/**
 * Servlet implementation class RESTAPI
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
    	connector = new HibernateDataConnector();
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
		
		// Get API Key
		String apiKey = request.getHeader("X-ApiKey");
		
		response.getWriter().print("GET DATA");
		initialiseResponse(response, "application/xml");
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This is a request to set data so process the request data
		
		// Get API Key
		String apiKey = request.getHeader("X-ApiKey");
		
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
			
/*			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(data, response.getOutputStream());*/
			
			response.setStatus(200);
			
			// Just use generic error response for now 
		} catch (JAXBException e) {
			doResponse(response, ResponseType.ERROR, e);
		} catch (DataSecurityException e) {
			doResponse(response, ResponseType.WARNING, e);
		} catch (DataConnectorException e) {
			doResponse(response, ResponseType.ERROR, e);
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
				
				if (sensorValues.size() > 0) {
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
		Class replyClass = null;
		
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
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(reply, response.getOutputStream());
		}
		catch (Exception ex) {
			// TODO: Log this problem
		}
	}
	
	private Marshaller getJAXBMarshaller(Class clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return jaxbMarshaller;
	}
}
