/**
 * 
 */
package org.openremote.beehive.serviceHibernateImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.IconDTO;
import org.openremote.beehive.api.service.IconService;
import org.openremote.beehive.domain.Icon;
import org.springframework.beans.BeanUtils;

/**
 * @author Tomsky
 *
 */
public class IconServiceImpl extends BaseAbstractService<Icon> implements IconService {
   
   private static Logger logger = Logger.getLogger(IconServiceImpl.class.getName());
   private Configuration configuration;
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.beehive.api.service.IconService#findIconsByName(java.lang.String)
    */
   @Override
   public List<IconDTO> findIconsByName(String name) {
//      if(genericDAO.getByNonIdField(Icon.class, "name", name) == null){
//         return loadAllIcons();
//      }
      
      DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Icon.class);
      detachedCriteria.add(Restrictions.eq("name", name.toLowerCase()));
      List<Icon> icons = genericDAO.findByDetachedCriteria(detachedCriteria);
      List<IconDTO> iconDTOList = new ArrayList<IconDTO>();
      String iconDir = configuration.getIconsDir();
      for (Icon icon : icons) {
         IconDTO iconDTO = new IconDTO();
         BeanUtils.copyProperties(icon, iconDTO);
         iconDTO.setFileName(iconDir+icon.getFileName());
         iconDTOList.add(iconDTO);
      }
      logger.info("Get list of icons by label of "+name.toLowerCase());
      return iconDTOList;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.beehive.api.service.IconService#loadAllIcons()
    */
   @Override
   public List<IconDTO> loadAllIcons() {
      List<Icon> icons = genericDAO.loadAll(Icon.class);
      List<IconDTO> iconDTOList = new ArrayList<IconDTO>();
      String iconDir = configuration.getIconsDir();
      for (Icon icon : icons) {
         IconDTO iconDTO = new IconDTO();
         BeanUtils.copyProperties(icon, iconDTO);
         iconDTO.setFileName(iconDir+icon.getFileName());
         iconDTOList.add(iconDTO);
      }
      logger.info("Get the list of all icons");
      return iconDTOList;
   }

}
