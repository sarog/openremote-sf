/**
 * 
 */
package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.IconDTO;

/**
 * @author Tomsky 2009-4-20
 *
 */
public interface IconService {
   
   /**
    * Gets all <icon>IconDTOs</icon> belongs to certain name
    * 
    * @param name
    * @return list of IconDTOs
    */
   List<IconDTO> findIconsByName(String name);
   
   /**
    * Loads all <icon>IconDTOs</icon>
    * 
    * @return list of IconDTOs
    */
   List<IconDTO> loadAllIcons();
}
