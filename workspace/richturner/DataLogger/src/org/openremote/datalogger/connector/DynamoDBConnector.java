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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.openremote.datalogger.exception.DataConnectorException;
import org.openremote.datalogger.exception.DataSecurityException;
import org.openremote.datalogger.model.SensorOutputValue;
import org.openremote.datalogger.model.SensorValue;
import org.openremote.datalogger.model.User;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * Connector for Amazon Dynamo DB Service.
 * For this connector to work the tables and users must have already been setup.
 * Users Table - Stores user credentials
 * x_Sensors Table - Stores Sensor metadata for customer x
 * x_SensorValues Table - Stores Sensor values for customer x
 * See {@link #org.openremote.datalogger.DynamoDBInstaller} for more details
 * 
 * NOTE: set the endpoint property below if you wish to use with DynamoDB Local.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class DynamoDBConnector implements DataConnector {
  private DynamoDBMapper mapper;
  private static SimpleDateFormat dateFormatter;
  private AmazonDynamoDBClient client;
  
  static {
    dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  @Override
  public void init() throws DataConnectorException {
    // Read credentials
    AWSCredentials credentials;
    try {
      InputStream props = DynamoDBConnector.class.getResourceAsStream("/aws.properties");
      credentials = new PropertiesCredentials(props);
      props.close();
      
      ClientConfiguration config = new ClientConfiguration()
      .withConnectionTimeout(5000)
      .withSocketTimeout(5000)
      .withRetryPolicy(new RetryPolicy(new RetryPolicy.RetryCondition(){

        @Override
        public boolean shouldRetry(AmazonWebServiceRequest arg0, AmazonClientException arg1,
                int arg2) {
          if (arg1 instanceof AmazonServiceException) {
            if (arg2 < 5) {
              return false;
            }
            return true;
          }
          
          return false;
        }
      },null, 5, false));
      
      config.setConnectionTimeout(2000);
      config.setSocketTimeout(2000);
      client = new AmazonDynamoDBClient(credentials,config);
      
      // Set endpoint for DynamoDB Local
      //client.setEndpoint("http://localhost:8000");

      DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig(
              DynamoDBMapperConfig.SaveBehavior.CLOBBER,
              DynamoDBMapperConfig.ConsistentReads.CONSISTENT,
              null, //TableNameOverride - leaving this at default setting
              DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING
      );
                
      mapper = new DynamoDBMapper(client, mapperConfig);
    } catch (Exception e) {
      throw new DataConnectorException(e);
    }
  }

  @Override
  public void setSensorCurrentValue(String apiKey, String sensorName, String currentValue)
          throws DataSecurityException, DataConnectorException {
    addSensorValues(apiKey, sensorName, null, currentValue);
  }

  @Override
  public void addSensorValues(String apiKey, String sensorName, Set<SensorValue> values,
          String currentValue) throws DataSecurityException, DataConnectorException {
    if (client == null || mapper == null)
    {
      throw new DataConnectorException("Connector is not valid");
    }
    
    // Validate the user
    User user = getUser(apiKey, true);
    if (user == null) {
      throw new DataSecurityException("User is not valid");
    }
    
    List<SensorValue> writeValues = new ArrayList<SensorValue>();
    if (values != null) {
      writeValues.addAll(values);
    }
    
    if (currentValue != null && !currentValue.isEmpty()) {
      SensorValue current = new SensorValue();
      current.setSensorName(sensorName);
      current.setTimestamp(new Date());
      current.setValue(currentValue);
      writeValues.add(current);
    }
    
    // Add sensor values to this user's sensor values table
    String tableName = user.getUsername() + "_SensorValues";

    List<SensorValue> deleteValues = Collections.<SensorValue> emptyList();
    
    try {
      List<FailedBatch> fails = mapper.batchWrite(writeValues, deleteValues, new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(tableName)));
      if (fails.size() > 0) {
        throw new DataConnectorException("Failed to write one or more values to the database");
      }
    } catch(Exception e) {
      throw new DataConnectorException("Connector Error", e);
    }
  }

  @Override
  public Double getAverageSensorValue(String apiKey, String sensorName, Date fromTime, Date toTime)
          throws DataSecurityException, DataConnectorException {
    if (client == null || mapper == null)
    {
      throw new DataConnectorException("Connector is not valid");
    }
    
    // Validate the user
    User user = getUser(apiKey, false);
    if (user == null) {
      throw new DataSecurityException("User is not valid");
    }
    
    // TODO: Consider limiting timespan
    String tableName = user.getUsername() + "_SensorValues";
    SensorValue value = new SensorValue();
    value.setSensorName(sensorName);
    DynamoDBQueryExpression<SensorValue> queryExpression = new DynamoDBQueryExpression<SensorValue>()
            .withHashKeyValues(value);
    
    Condition timeCondition = new Condition();
    timeCondition.withComparisonOperator(ComparisonOperator.BETWEEN)
         .withAttributeValueList(new AttributeValue().withS(dateFormatter.format(fromTime)), new AttributeValue().withS(dateFormatter.format(toTime)));

    queryExpression
         .withRangeKeyCondition("Timestamp", timeCondition);
    
    try {
      List<SensorValue> values = mapper.query(SensorValue.class, queryExpression, new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(tableName)));
  
      double total = 0;
      int counter = 0;
      for (SensorValue val : values) {
        String valStr = val.getValue();
        try {
          total += Double.parseDouble(valStr);
          counter++;
        } catch (NumberFormatException e) {
        }
      }
      
      return counter == 0 ? 0d : total / counter;
    } catch(Exception e) {
      throw new DataConnectorException("Connector Error", e);
    }
  }  

  @Override
  public SensorOutputValue getLatestSensorValue(String apiKey, String sensorName)
          throws DataSecurityException, DataConnectorException {
    if (client == null || mapper == null)
    {
      throw new DataConnectorException("Connector is not valid");
    }
    
    // Validate the user
    User user = getUser(apiKey, false);
    if (user == null) {
      throw new DataSecurityException("User is not valid");
    }
    
    // Get the latest value for this sensor
    String tableName = user.getUsername() + "_SensorValues";
    SensorValue value = new SensorValue();
    value.setSensorName(sensorName);
    DynamoDBQueryExpression<SensorValue> queryExpression = new DynamoDBQueryExpression<SensorValue>()
            .withHashKeyValues(value);

    queryExpression
         .withScanIndexForward(false)
         .withLimit(1);
    
    try {
      List<SensorValue> values = mapper.query(SensorValue.class, queryExpression, new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(tableName)));
      SensorOutputValue output = new SensorOutputValue();
      if (values.size() > 0) {
        output.setName(values.get(0).getSensorName());
        output.setValue(values.get(0).getValue());
      } else {
        output.setName(sensorName);
      }
      return output;
    } catch(Exception e) {
      throw new DataConnectorException("Connector Error", e);
    }
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }
  
  private User getUser(String key, boolean isWrite) throws DataConnectorException {
    User user = new User();
    user.setKey(key);
    
    ComparisonOperator operator = isWrite ? ComparisonOperator.EQ : ComparisonOperator.LE;
    Condition rangeKeyCondition = new Condition();
    rangeKeyCondition.withComparisonOperator(operator)
         .withAttributeValueList(new AttributeValue().withN("1"));    

    DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
            .withHashKeyValues(user)
            .withRangeKeyCondition("Writable", rangeKeyCondition);
    
    try {
      List<User> matchedUsers = mapper.query(User.class, queryExpression);
      return matchedUsers.size() > 0 ? matchedUsers.get(0) : null;
    } catch(Exception e) {
      throw new DataConnectorException("Connector Error", e);
    }
  }
}
