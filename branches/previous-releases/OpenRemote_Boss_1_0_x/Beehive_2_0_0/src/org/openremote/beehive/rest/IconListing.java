/**
 * 
 */
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.IconDTO;

/**
 * In order to let rest service to serialize list of icon
 * 
 * @author Tomsky 2009-4-21
 *
 */
@XmlRootElement(name = "icons")
public class IconListing {
   
   private List<IconDTO> icons = new ArrayList<IconDTO>();
   
   public IconListing() {
   }
   
   public IconListing(List<IconDTO> icons) {
      this.icons = icons;
   }
   
   @XmlElement(name = "icon")
   public List<IconDTO> getIcons() {
      return icons;
   }
   
   public void setIcons(List<IconDTO> icons) {
      this.icons = icons;
   }
}
