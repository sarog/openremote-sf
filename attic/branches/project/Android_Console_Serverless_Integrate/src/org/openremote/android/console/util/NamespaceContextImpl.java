/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.android.console.util;


import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/*
 * An implementation class for NamespaceContext
 * 
 */
public class NamespaceContextImpl implements NamespaceContext {

    Map<String, String> map = new HashMap<String, String>();
    
    public NamespaceContextImpl() {
        setNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
        setNamespaceURI(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        setNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }
    
    public void setNamespaceURI(String prefix, String uri) {
        map.put(prefix, uri);
    }
    
    public String getNamespaceURI(String prefix) {
        return map.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        
        Set<Map.Entry<String, String>>set = map.entrySet();
        for (Map.Entry<String, String>item : set) {
            if (namespaceURI.equals(item.getValue())) {
                return item.getKey();
            }
        }
        return XMLConstants.NULL_NS_URI;
    }

    public Iterator getPrefixes(String namespaceURI) {
        
        Set<String> prefixes = new HashSet<String>();
        
        Set<Map.Entry<String, String>>set = map.entrySet();
        for (Map.Entry<String, String>item : set) {
            if (namespaceURI.equals(item.getValue())) {
                prefixes.add(item.getKey());
            }
        }
        
        return Collections.unmodifiableCollection(prefixes).iterator();
    }

}