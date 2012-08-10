/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.domain;

import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.shared.PropertyChangeListener;
import org.openremote.modeler.shared.PropertyChangeSupport;

import flexjson.JSON;

/**
 * Define a absolute position and size in screen, store a uiComponent.
 */
public class Absolute extends BusinessEntity implements PositionableAndSizable {

   private static final long serialVersionUID = -114340249340271840L;

   protected transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

   /** The left. */
   private int left;
   
   /** The top. */
   private int top;
   
   /** The width. */
   private int width;
   
   /** The height. */
   private int height;
   
   /** The ui component is store in the absolute. */
   private UIComponent uiComponent;
   
   /** The absolute is the absoluteLayoutContainer's model,  have this property can manage absoluteLayoutContainer's size. */
   private transient AbsoluteLayoutContainer belongsTo= null;
   
   public Absolute() {
      super();
   }
   
   public Absolute(long id) {
      super(id);
   }
   
   /**
    * Gets the left.
    * 
    * @return the left
    */
   public int getLeft() {
      return left;
   }
   
   /**
    * Gets the top.
    * 
    * @return the top
    */
   public int getTop() {
      return top;
   }
   
   /**
    * Gets the width.
    * 
    * @return the width
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Gets the height.
    * 
    * @return the height
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Sets the left.
    * 
    * @param left the new left
    */
   public void setLeft(int left) {
     int oldLeft = this.left;
     this.left = left;
     this.pcSupport.firePropertyChange("left", oldLeft, this.left);
   }
   
   /**
    * Sets the top.
    * 
    * @param top the new top
    */
   public void setTop(int top) {
     int oldTop = this.top;
      this.top = top;
      this.pcSupport.firePropertyChange("top", oldTop, this.top);
   }
   
   /**
    * Sets the width.
    * 
    * @param width the new width
    */
   public void setWidth(int width) {
     int oldWidth = this.width;
      this.width = width;
      this.pcSupport.firePropertyChange("width", oldWidth, this.width);
   }
   
   /**
    * Sets the height.
    * 
    * @param height the new height
    */
   public void setHeight(int height) {
     int oldHeight = this.height;
      this.height = height;
      this.pcSupport.firePropertyChange("height", oldHeight, this.height);
   }

   /**
    * Gets the ui control.
    * 
    * @return the ui control
    */
   public UIComponent getUiComponent() {
      return uiComponent;
   }

   /**
    * Sets the ui control.
    * 
    * @param uiControl the new ui control
    */
   public void setUiComponent(UIComponent uiComponent) {
      this.uiComponent = uiComponent;
   }
   
   public void setSize(int width, int height) {
     setWidth(width);
     setHeight(height);
   }
   
   public void setPosition(int left, int top) {
     setLeft(left);
     setTop(top);
   }

   @JSON(include=false)
   public AbsoluteLayoutContainer getBelongsTo() {
      return belongsTo;
   }

   public void setBelongsTo(AbsoluteLayoutContainer belongsTo) {
      this.belongsTo = belongsTo;
   }
   
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
     this.pcSupport.addPropertyChangeListener(propertyName, listener);
   }
   
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
     this.pcSupport.removePropertyChangeListener(propertyName, listener);
   }
   
}
