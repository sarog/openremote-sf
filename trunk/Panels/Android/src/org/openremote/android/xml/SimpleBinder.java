/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
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
package org.openremote.android.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Because Android does not include any kind of Binding and you're stuck doing DOM or SAX or StAX, we are forced to do our own.
 * SimpleBinder is tiny and does the simplest thing.  If you're looking for a JAXB alternative, this is not it.  It uses simple
 * reflection on simple classes with simple name correllations.  The rules are...simple.  ClassName == ElementName, set = AttributeName
 * unless it ends with an "s".  All collections (for now) are assumed to be lists.  i.e.  
 * 
 * <openremote>                                   - root element, we ignore the heck out of this and do nothing of importance with it
 *  <activity id="blaId" name="blaName">          - Activity.java, id is setId(string), name is setName(string)
 *   <screen id="screenId" name="screenName">     - Activity.setScreens(List<Screen>) cases us to construct a list of Screens and bind Screen.java to them
 *    <buttons>                                   - "Buttons" is passed in as an IgnoreNode so we ignore it and bind its children (we don't ignore the children)
 *      <button id="blaId" x="1">                 - Button.java, setId(string), setX(int)
 *    <buttons>
 *   </screen>
 *  </sctivity>
 * </openremote>
 * 
 * No effort has been made to make this a complete or extensive binding class, it is only what is necessary to parse the OpenRemote definition file.
 * 
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 *
 */
public class SimpleBinder {
    private String root;
    @SuppressWarnings("unchecked")
    private List<Class> classes;
    private List<String> ignoreNodes;

    /**
     * Construct an instance of SimpleBinder, you can reuse these if you have
     * some reason to.
     * 
     * @param root
     *            root node element name, we could check this, right now we set
     *            it and do nothing with it.
     * @param classes
     *            classes to bind, intended to be a collection of bind classes
     * @param ignoreNodes
     *            List of strings containing the name of each node we want to
     *            ignore for binding, instead we bind the ignore node's children
     */
    @SuppressWarnings("unchecked")
    public SimpleBinder(String root, List<Class> classes,
            List<String> ignoreNodes) {
        this.root = root;
        this.classes = classes;
        this.ignoreNodes = ignoreNodes;
    }

    /**
     * The action is here, we bind things and return them in a list, recursion
     * is used for collections. Various exceptions are thrown due to parser, IO,
     * or class reflection binding issues.
     * 
     * @param stream
     *            containing the XML file we're binding
     * @return collection of classes we bound, instances of things in the
     *         classes list passed in to the constructor
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public List bindStuff(InputStream stream)
            throws ParserConfigurationException, SAXException, IOException,
            IllegalAccessException, InstantiationException,
            IllegalArgumentException, InvocationTargetException {
        Log.d(this.toString(), "binding stuff with root " + root);
        List objs = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(stream);
        Element root = dom.getDocumentElement();
        for (Class clazz : classes) {

            String nodeName = ("" + clazz.getSimpleName().charAt(0))
                    .toLowerCase()
                    + clazz.getSimpleName().substring(1);
            // Log.d(this.toString(),"binding class "+clazz.getSimpleName()+" as "+nodeName);
            NodeList nodeList = root.getElementsByTagName(nodeName);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Object o = bindClass(clazz, node);
                objs.add(o);
            }
        }

        return objs;
    }

    /**
     * Bind classes from a parent node's children. The tclass parameter is what
     * we want to bind, the parent contains the nodes we want to bind. All types
     * not matching the class are ignored. By match we mean the Class.getName()
     * with an initial lower case.
     * 
     * @param tclass
     * @param parent
     * @return List of bound objects
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private List bindClasses(Class tclass, Node parent)
            throws IllegalArgumentException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        String name = tclass.getSimpleName();
        name = ("" + name.charAt(0)).toLowerCase() + name.substring(1);
        // Log.d(this.toString(),"bindclasses "+tclass.getName()+" for parent "+parent.getNodeName()+
        // " for the node "+name);
        List list = new ArrayList();
        NodeList nodes = parent.getChildNodes();
        // Log.d(this.toString(),"the parent in bindClasses had "+nodes.getLength()+" children");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            // Log.d(this.toString(),
            // "the node had a name "+node.getNodeName()+" which match to "+name+" is "+name.equals(node.getNodeName()));
            if (node.getNodeName().equals(name)) {
                Object obj = bindClass(tclass, node);
                list.add(obj);
            } else if (ignoreNodes.contains(node.getNodeName())) { // if it is
                                                                   // one we're
                                                                   // supposed
                                                                   // to ignore
                                                                   // we go down
                                                                   // a level in
                                                                   // the tree
                nodes = node.getChildNodes(); // we don't want this, we want its
                                              // children
                i = -1; // so it will be 0 after the iteration
            }
        }
        return list;
    }

    /**
     * Bind an individual class from an individual node. This also binds any
     * children which have a corresponding collection setter by calling back to
     * bindClasses(..)
     * 
     * @param clazz
     * @param node
     * @return Object which is actually an instance of clazz
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private Object bindClass(Class clazz, Node node)
            throws IllegalAccessException, InstantiationException,
            IllegalArgumentException, InvocationTargetException {
        // Log.d(this.toString(),"binding class "+clazz.getName()+":"+node.getNodeName());
        Method[] methods = clazz.getMethods();
        Object o = clazz.newInstance();
        NamedNodeMap attrs = node.getAttributes();
        // Log.d(this.toString(),"the "+node.getNodeName()+" node had "+node.getAttributes().getLength()+" attributes and the class "+clazz.getName()+" had "+methods.length+" methods");
        for (Method method : methods) {
            if (method.getName().startsWith("set")
                    && !method.getName().endsWith("s")) {
                String name = method.getName().substring(4);
                name = ("" + method.getName().charAt(3)).toLowerCase() + name;
                // Log.d(this.toString(),"getting attribute "+name);
                if (attrs.getNamedItem(name) != null) { // if there is a value
                                                        // to bind
                    String val = attrs.getNamedItem(name).getNodeValue();
                    Class parm = method.getParameterTypes()[0];
                    String type = parm.getName();
                    // Log.d(this.toString(), "method is of type: "+type);
                    if (type.equals(String.class.getName())) {
                        // Log.d(this.toString(),
                        // "binding attribute as String");
                        method.invoke(o, new Object[] { val });
                    } else if (type.equals(int.class.getName())
                            || type.equals(Integer.class.getName())) {
                        // Log.d(this.toString(),
                        // "binding attribute as Integer");
                        method.invoke(o, new Object[] { new Integer(val) });
                    }
                }
            } else if (method.getName().startsWith("set")
                    && method.getName().endsWith("s")) {
                // Log.d(this.toString(), "binding a collection for " +
                // method.getName());
                Type type = method.getGenericParameterTypes()[0];
                if (type instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    Class tclass = (Class) (ptype.getActualTypeArguments()[0]);
                    List list = bindClasses(tclass, node);
                    method.invoke(o, new Object[] { list });
                }
            }
        }
        return o;
    }
}
