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
package org.openremote.datalogger.connector;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openremote.datalogger.exception.*;
import org.openremote.datalogger.model.*;
import org.openremote.datalogger.rest.EMF;

/**
 * Connector for SQL DBs using the Java Persistence API.
 * For this connector to work the tables and users must have already been setup.
 * Users Table - Stores user credentials
 * Sensors Table - Stores Sensor metadata
 * SensorValues Table - Stores Sensor values
 * 
 * See sql/Install.sql for more details
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class HibernateDataConnector implements DataConnector {
	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#init()
	 */
	@Override
	public void init() throws DataConnectorException {
		// Try and get the entity manager
		EntityManager em = EMF.createEntityManager();
		
		if (em == null) {
			throw new DataConnectorException("Failed to get entity manager");
		}
	}

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#setCurrentFeedValue(java.lang.String, java.lang.Double)
	 */
	@Override
	public void setSensorCurrentValue(String apiKey, String sensorName, String currentValue) throws DataSecurityException, DataConnectorException {
		addSensorValues(apiKey, sensorName, null, currentValue);
	}

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#addFeedValues(java.lang.String, java.util.HashMap)
	 */
	@Override
	public void addSensorValues(String apiKey, String sensorName, Set<SensorValue> values, String currentValue) throws DataSecurityException, DataConnectorException {
		if (values == null) {
			values = new HashSet<SensorValue>();
		}
		
		EntityManager em = EMF.createEntityManager();
		
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			
			// Get the user for this apiKey
			CriteriaQuery<User> uCriteria = builder.createQuery(User.class);
			Root<User> userRoot = uCriteria.from(User.class);
			uCriteria.select(userRoot);
			uCriteria.where(
					builder.equal(userRoot.get("writeKey"), apiKey),
					builder.equal(userRoot.get("status"), true)
					);
			User user;
					
			try {
				user = em.createQuery(uCriteria).getSingleResult();
			} catch (NoResultException e) {
				throw new DataSecurityException("Invalid API Key provided");
			}
			
			// Get the sensor object (create if not already defined)
			CriteriaQuery<Sensor> sCriteria = builder.createQuery(Sensor.class);
			Root<Sensor> sensorRoot = sCriteria.from(Sensor.class);
			sCriteria.select(sensorRoot);
			sCriteria.where(builder.equal(sensorRoot.get("name"), sensorName),
											builder.equal(sensorRoot.get("user"), user.getId()));
			Sensor sensor;
			
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			
			try {
				sensor = em.createQuery(sCriteria).getSingleResult();
			} catch (NoResultException e) {
				sensor = new Sensor();
				sensor.setName(sensorName);
				sensor.setUser(user);
				em.persist(sensor);
			}
			
			if (sensorName == null || sensorName.isEmpty()) {
				throw new DataConnectorException(String.format("Unable to get sensor object from DB for sensor name '%1$s'", sensorName));
			}
	
			// Update currentValue
			if (currentValue != null && !currentValue.isEmpty()) {
					SensorValue current = new SensorValue();
					current.setTimestamp(new Date());
					current.setValue(currentValue);
					values.add(current);
	
					sensor.setCurrentValue(currentValue);
					em.merge(sensor);
			}
			
			// Add values
			for (SensorValue value : values) {
				value.setSensor(sensor);
				em.persist(value);
			}
			
			transaction.commit();
		} catch (Exception e) {
			throw new DataConnectorException(e);
		} finally {
			em.close();	
		}		
	}
	
  /* (non-Javadoc)
   * @see org.openremote.datalogger.connector.DataConnector#getAverageSensorValue(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
   */
  @Override
  public Double getAverageSensorValue(String apiKey, String sensorName, Date fromTime, Date toTime) throws DataSecurityException, DataConnectorException {
    EntityManager em = EMF.createEntityManager();
    
    try {
      CriteriaBuilder builder = em.getCriteriaBuilder();
      
      // Get the user for this apiKey
      CriteriaQuery<User> uCriteria = builder.createQuery(User.class);
      Root<User> userRoot = uCriteria.from(User.class);
      uCriteria.select(userRoot);
      uCriteria.where(
          builder.equal(userRoot.get("readKey"), apiKey),
          builder.equal(userRoot.get("status"), true)
          );
      User user;
          
      try {
        user = em.createQuery(uCriteria).getSingleResult();
      } catch (NoResultException e) {
        throw new DataSecurityException("Invalid API Key provided");
      }
      
      // Get the sensor object
      CriteriaQuery<Sensor> sCriteria = builder.createQuery(Sensor.class);
      Root<Sensor> sensorRoot = sCriteria.from(Sensor.class);
      sCriteria.select(sensorRoot);
      sCriteria.where(builder.equal(sensorRoot.get("name"), sensorName),
                      builder.equal(sensorRoot.get("user"), user));
      Sensor sensor;      

      try {
        sensor = em.createQuery(sCriteria).getSingleResult();
      }  catch (NoResultException e) {
        return 0d; // Kludge to prevent controller from freezing
        //throw new DataConnectorException(String.format("Sensor '%1$s' does not exist", sensorName));
      }

      EntityTransaction transaction = em.getTransaction();
      transaction.begin();
            
      // This should be changed to a JPA compliant query but this query is pretty standard SQL and should work on most DBs
      Query query = em.createNativeQuery("SELECT AVG(value) FROM " +
          "(SELECT cast(value as float) " +
            "FROM sensorValues " +
            "WHERE isnumeric(value) AND timestamp >= '" + fromTime + "' AND timestamp <= '" + toTime + "' AND sensorId = " + sensor.getId() + ") RangeValues");
      Object result = query.getSingleResult();
      transaction.commit();
      return (Double)result;
    } catch (DataSecurityException e) {
      throw e;
    } catch (Exception e) {
      throw new DataConnectorException(e);
    } finally {
      em.close(); 
    } 
  }

  @Override
  public SensorOutputValue getLatestSensorValue(String apiKey, String sensorName)
          throws DataSecurityException, DataConnectorException {
    EntityManager em = EMF.createEntityManager();
    
    try {
      CriteriaBuilder builder = em.getCriteriaBuilder();
      
      // Get the user for this apiKey
      CriteriaQuery<User> uCriteria = builder.createQuery(User.class);
      Root<User> userRoot = uCriteria.from(User.class);
      uCriteria.select(userRoot);
      uCriteria.where(
          builder.equal(userRoot.get("readKey"), apiKey),
          builder.equal(userRoot.get("status"), true)
          );
      User user;
          
      try {
        user = em.createQuery(uCriteria).getSingleResult();
      } catch (NoResultException e) {
        throw new DataSecurityException("Invalid API Key provided");
      }
      
      // Get the sensor object
      CriteriaQuery<Sensor> sCriteria = builder.createQuery(Sensor.class);
      Root<Sensor> sensorRoot = sCriteria.from(Sensor.class);
      sCriteria.select(sensorRoot);
      sCriteria.where(builder.equal(sensorRoot.get("name"), sensorName),
                      builder.equal(sensorRoot.get("user"), user));

      Sensor sensor = null;      
      SensorOutputValue output = new SensorOutputValue();
      output.setName(sensorName);
      
      try {
        sensor = em.createQuery(sCriteria).getSingleResult();
      }  catch (NoResultException e) {
      }
      
      // Get latest value for this sensor
      CriteriaQuery<SensorValue> vCriteria = builder.createQuery(SensorValue.class);
      Root<SensorValue> valueRoot = sCriteria.from(SensorValue.class);
      vCriteria.select(valueRoot);
      vCriteria.where(builder.equal(valueRoot.get("sensor"), sensor),
                      builder.equal(sensorRoot.get("user"), user));
      vCriteria.orderBy(builder.desc(valueRoot.get("timestamp")));

      try {
        SensorValue val = em.createQuery(vCriteria).getSingleResult();
        output.setValue(val.getValue());
      }  catch (NoResultException e) {
      }
      
      return output;
    } catch (DataSecurityException e) {
      throw e;
    } catch (Exception e) {
      throw new DataConnectorException(e);
    } finally {
      em.close(); 
    } 
  }

	@Override
	public void destroy() {
		// Nothing to do here
	}
}
