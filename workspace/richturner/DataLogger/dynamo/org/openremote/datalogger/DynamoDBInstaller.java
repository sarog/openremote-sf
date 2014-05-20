package org.openremote.datalogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

// This is a simple installer for Local DB
public class DynamoDBInstaller {
  public static void main(String[] args) throws Exception {
    // Read credentials
    AWSCredentials credentials = null;
    try {
      InputStream props = DynamoDBInstaller.class.getResourceAsStream("/aws.properties");
      credentials = new PropertiesCredentials(props);
      props.close();
    } catch (IOException e) {
    }

    if (credentials == null)
    {
      return;
    }
    
    AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
    client.setEndpoint("http://localhost:8000");
    
    // Create User table
    {
      ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
      attributeDefinitions.add(new AttributeDefinition().withAttributeName("Key").withAttributeType("S"));
      attributeDefinitions.add(new AttributeDefinition().withAttributeName("Writable").withAttributeType("N"));
      
      ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
      ks.add(new KeySchemaElement().withAttributeName("Key").withKeyType(KeyType.HASH));
      ks.add(new KeySchemaElement().withAttributeName("Writable").withKeyType(KeyType.RANGE));
        
      ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
          .withReadCapacityUnits(10L)
          .withWriteCapacityUnits(10L);
              
      CreateTableRequest request = new CreateTableRequest()
          .withTableName("Users")
          .withAttributeDefinitions(attributeDefinitions)
          .withKeySchema(ks)
          .withProvisionedThroughput(provisionedThroughput);
          
      CreateTableResult result = client.createTable(request);
    }
    
    // Create Test Customer's Sensors table
    {
      ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
      attributeDefinitions.add(new AttributeDefinition().withAttributeName("SensorName").withAttributeType("S"));
      
      ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
      ks.add(new KeySchemaElement().withAttributeName("SensorName").withKeyType(KeyType.HASH));
        
      ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
          .withReadCapacityUnits(10L)
          .withWriteCapacityUnits(10L);
              
      CreateTableRequest request = new CreateTableRequest()
          .withTableName("Test_Sensors")
          .withAttributeDefinitions(attributeDefinitions)
          .withKeySchema(ks)
          .withProvisionedThroughput(provisionedThroughput);
          
      CreateTableResult result = client.createTable(request);
    }

    // Create Test Customer's Sensor Values table
    {
      ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
      attributeDefinitions.add(new AttributeDefinition().withAttributeName("SensorName").withAttributeType("S"));
      attributeDefinitions.add(new AttributeDefinition().withAttributeName("Timestamp").withAttributeType("S"));
      
      ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
      ks.add(new KeySchemaElement().withAttributeName("SensorName").withKeyType(KeyType.HASH));
      ks.add(new KeySchemaElement().withAttributeName("Timestamp").withKeyType(KeyType.RANGE));
        
      ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
          .withReadCapacityUnits(10L)
          .withWriteCapacityUnits(10L);
              
      CreateTableRequest request = new CreateTableRequest()
          .withTableName("Test_SensorValues")
          .withAttributeDefinitions(attributeDefinitions)
          .withKeySchema(ks)
          .withProvisionedThroughput(provisionedThroughput);
          
      CreateTableResult result = client.createTable(request);
    }
    
    // Create Test Customer
    {
      UUID uuid = UUID.randomUUID();
      String key = uuid.toString().replace("-", "");
      
      Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
      item.put("Username", new AttributeValue().withS("Test"));
      item.put("Key", new AttributeValue().withS(key));
      item.put("Writable", new AttributeValue().withN("1"));

      PutItemRequest putItemRequest = new PutItemRequest()
        .withTableName("Users")
        .withItem(item);
      PutItemResult result = client.putItem(putItemRequest);
      
      System.out.println("USER KEY: " + key);
    }
    
    // List Tables
    {
      String lastEvaluatedTableName = null;
      do {
          
          ListTablesRequest listTablesRequest = new ListTablesRequest()
          .withLimit(10)
          .withExclusiveStartTableName(lastEvaluatedTableName);
          
          ListTablesResult result = client.listTables(listTablesRequest);
          lastEvaluatedTableName = result.getLastEvaluatedTableName();
          System.out.println("TABLE NAMES:");
          
          for (String name : result.getTableNames()) {
              System.out.println(name);
          }
          
      } while (lastEvaluatedTableName != null);
    }
  }
}
