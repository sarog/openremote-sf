/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.cbus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.protocol.knx.ip.KnxIpException;

/**
 * A utility class to enable cross-referencing of CBus addresses
 * 
 * @param <KeyType>
 *           The type for all the keys in the map
 *           
 * @param <ValueType>
 *           The type of all the values in the map
 * 
 * @author Jamie Turner
 */
public class BiDirectionalMap<KeyType, ValueType>{
   private Map<KeyType, ValueType> keyToValueMap = new ConcurrentHashMap<KeyType, ValueType>();
   private Map<ValueType, KeyType> valueToKeyMap = new ConcurrentHashMap<ValueType, KeyType>();

   /**
    * Insert or replace a key-value pair
    * 
    * @param key
    *           The key for the value.
    * @param value
    *           The value.
    */
   synchronized public void put(KeyType key, ValueType value){
       keyToValueMap.put(key, value);
       valueToKeyMap.put(value, key);
   }

   
   /**
    * Remove a key-value pair based on the key.
    * 
    * @param key
    *           The key of the key-value pair to remove.
    *           
    * @return The value that was removed.
    */
   synchronized public ValueType removeByKey(KeyType key){
       ValueType removedValue = keyToValueMap.remove(key);
       valueToKeyMap.remove(removedValue);
       return removedValue;
   }

   
   /**
    * Remove a key-value pair based on the value.
    *  
    * @param value
    *           The value of the key-value pair to remove.
    * 
    * @return The key that was removed.
    */
   synchronized public KeyType removeByValue(ValueType value){
       KeyType removedKey = valueToKeyMap.remove(value);
       keyToValueMap.remove(removedKey);
       return removedKey;
   }
   
   /**
    * Check whether the map contains the specified key.
    * 
    * @param key
    *           The key
    *           
    * @return true if the key exists, false if not
    */
   public boolean containsKey(KeyType key){
       return keyToValueMap.containsKey(key);
   }

   
   /**
    * Check whether the map contains the specified value.
    * 
    * @param value
    *           The value
    *           
    * @return true if the value exists, false if not
    */
   public boolean containsValue(ValueType value){
       return keyToValueMap.containsValue(value);
   }

   
   /**
    * Get the key based on the value
    * 
    * @param value
    *           The value
    *           
    * @return The key associated with the value
    *       
    */
   public KeyType getKey(ValueType value){
       return valueToKeyMap.get(value);
   }

   
   /**
    * Get the value based on the key
    * 
    * @param key
    *           The key
    * 
    * @return The value associated with the key
    */
   public ValueType get(KeyType key){
       return keyToValueMap.get(key);
   }
   
   
   /**
    * Get the number of key-value pairs in the map.
    * 
    * @return The number of key-value pairs in the map.
    */
   public int size()
   {
      return keyToValueMap.size();
   }
}
