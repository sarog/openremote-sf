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
package org.openremote.datalogger.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
@Entity
@Table(name = "dataUsers", uniqueConstraints={@UniqueConstraint(columnNames={"username"})})
@DynamoDBTable(tableName="Users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="status")
	private boolean status;
	
	@Column(name="username")
	private String username;
	
	@Column(name="readKey")
	private String readKey;
	
	@Column(name="writeKey")
	private String writeKey;
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Set<Sensor> sensors;
	
	private Boolean write;
	private String key;
	
  @DynamoDBHashKey(attributeName="Key")
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
	
	@DynamoDBIgnore
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@DynamoDBIgnore
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	@DynamoDBAttribute(attributeName="Username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@DynamoDBIgnore  
	public String getReadKey() {
		return readKey != null ? readKey : key;
	}	
	public void setReadKey(String readKey) {
		this.readKey = readKey;
	}

	@DynamoDBIgnore
	public String getWriteKey() {
		return writeKey != null ? writeKey : key;
	}

	public void setWriteKey(String writeKey) {
		this.writeKey = writeKey;
	}
	
	@DynamoDBIgnore
	public Set<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(Set<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	@DynamoDBRangeKey(attributeName="Writable")
	public boolean isWrite() {
	  return write;
	}
  public void setWrite(boolean write) {
    this.write = write;
  }
}
