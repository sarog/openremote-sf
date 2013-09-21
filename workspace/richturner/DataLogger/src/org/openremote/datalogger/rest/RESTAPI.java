package org.openremote.datalogger.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.openremote.datalogger.data.*;
import org.openremote.datalogger.exception.DataConnectorException;


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
    	if (!connector.init()) {
    		throw new ServletException("Failed to initialise the Data Connector");
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This is a request to get data
		
		// Validate API Key
		String apiKey = request.getHeader("X-ApiKey");
		
		response.getWriter().print("GET DATA");
		initialiseResponse(response, "application/xml");
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This is a request to set data so process the request data
		
		// Validate API Key
		String apiKey = request.getHeader("X-ApiKey");
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Eeml.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Eeml dataArr = (Eeml)jaxbUnmarshaller.unmarshal(request.getInputStream());
			
			if (dataArr != null) {
				// Process the data
				for(Data data : dataArr.getDatas()) {
					if (data == null) {
						processDataItem(apiKey, data);
					}
				}
					
			}
/*			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(data, response.getOutputStream());*/
			
			response.setStatus(200);
		} catch (JAXBException e) {
			doErrorResponse(response, e);
		} catch (DataConnectorException e) {
				doErrorResponse(response, e);
			}
	}
	
	/*
	 * Add the data to the logger via the data connector
	 */
	private void processDataItem(String apiKey, Data data) throws DataConnectorException {
		HashMap<Date, String> dataPoints = new HashMap<Date, String>();

		if (data != null) {
			String feedId = data.getId();
			if (feedId != null && !feedId.isEmpty()) {
				if (data.getDataPoints() != null) {
					for(DataPoint dp : data.getDataPoints()) {
						dataPoints.put(dp.getAt(), dp.getVal());
					}
				}
				String currentVal = data.getCurrentValue();
				if (currentVal != null) {
					Date currentTime = new Date();
					dataPoints.put(currentTime, currentVal);
				}
				
				if (dataPoints.size() > 0) {
					connector.addFeedValues(apiKey, feedId, dataPoints);
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
	
	private void doErrorResponse(HttpServletResponse response, Exception e) {
		org.openremote.datalogger.data.Error error = new org.openremote.datalogger.data.Error();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		error.setException(sw.toString());
		error.setMessage(e.getMessage());
		
		try {
			Marshaller jaxbMarshaller = getJAXBMarshaller(org.openremote.datalogger.data.Error.class);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(error, response.getOutputStream());
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
