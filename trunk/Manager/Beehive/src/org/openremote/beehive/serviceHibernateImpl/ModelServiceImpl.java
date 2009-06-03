/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.serviceHibernateImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.RemoteSection;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.file.LircConfFile;
import org.openremote.beehive.utils.FileUtil;

// TODO: Auto-generated Javadoc
/**
 * {@inheritDoc}.
 * 
 * @author allen.wei
 */
public class ModelServiceImpl extends BaseAbstractService<Model> implements ModelService {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(ModelServiceImpl.class.getName());
   
   /** The configuration. */
   private Configuration configuration;

   /**
    * {@inheritDoc}
    */
   public List<ModelDTO> findModelsByVendorName(String vendorName) {
      if (genericDAO.getByNonIdField(Vendor.class, "name", vendorName) == null) {
         return null;
      }
      DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Model.class);
      detachedCriteria.createAlias("vendor", "v").add(Restrictions.eq("v.name", vendorName));
      List<Model> models = genericDAO.findByDetachedCriteria(detachedCriteria);
      List<ModelDTO> modelDTOList = new ArrayList<ModelDTO>();
      for (Model model : models) {
         ModelDTO modelDTO = new ModelDTO();
         try {
            BeanUtils.copyProperties(modelDTO, model);
         } catch (IllegalAccessException e) {
            // TODO handle exception
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            // TODO handle exception
            e.printStackTrace();
         }
         modelDTOList.add(modelDTO);
      }
      return modelDTOList;
   }

   /**
    * {@inheritDoc}
    */
   public List<ModelDTO> findModelsByVendorId(long vendorId) {
      List<ModelDTO> modelDTOList = new ArrayList<ModelDTO>();
      for (Model model : genericDAO.loadById(Vendor.class, vendorId).getModels()) {
         ModelDTO modelDTO = new ModelDTO();
         try {
            BeanUtils.copyProperties(modelDTO, model);
         } catch (IllegalAccessException e) {
            // TODO handle exception
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            // TODO handle exception
            e.printStackTrace();
         }
         modelDTOList.add(modelDTO);
      }
      return modelDTOList;
   }

   /**
    * {@inheritDoc}
    */
   public ModelDTO loadByVendorNameAndModelName(String vendorName, String modelName) {
      Model model = null;
      List<Model> models = genericDAO.findByDetachedCriteria(getDetachedCriteria().createAlias("vendor", "v").add(
            Restrictions.eq("v.name", vendorName)).add(Restrictions.eq("name", modelName)));
      if (models.size() > 0) {
         if (models.size() > 1) {
            logger
                  .warn("There is more than one model named '" + modelName + "' belong to Vendor '" + vendorName + "'.");
         }
         model = models.get(0);
      } else {
         return null;
      }
      ModelDTO modelDTO = new ModelDTO();
      try {
         BeanUtils.copyProperties(modelDTO, model);
      } catch (IllegalAccessException e) {
         // TODO handle exception
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         // TODO handle exception
         e.printStackTrace();
      }
      return modelDTO;
   }

   /* (non-Javadoc)
    * @see org.openremote.beehive.api.service.ModelService#loadModelById(long)
    */
   public ModelDTO loadModelById(long modelId) {
      ModelDTO modelDTO = new ModelDTO();
      try {
         BeanUtils.copyProperties(modelDTO, loadById(modelId));
      } catch (IllegalAccessException e) {
         logger.error("", e);
      } catch (InvocationTargetException e) {
         // TODO handle exception
         e.printStackTrace();
      }
      return modelDTO;
   }

   /**
    * {@inheritDoc}
    */
   public void add(FileInputStream fis, String vendorName, String modelName) {
      Model model = createModel(findVendor(vendorName), modelName);
      List<RemoteSection> remoteSectionList = LircConfFile.getRemoteSectionList(fis);
      if (remoteSectionList.size() > 0) {
         String comment = remoteSectionList.get(0).getModel().getComment();
         model.setComment(comment);
         if (model.getName().isEmpty()) {
            String name = remoteSectionList.get(0).getRemoteOptions().get(0).getValue();
            model.setName(name);
         }
         genericDAO.merge(model);
      }
      for (RemoteSection remoteSection : remoteSectionList) {
         remoteSection.setModel(model);
         genericDAO.save(remoteSection);
      }
   }
   
   /**
    * Merge.
    * 
    * @param fis
    *           the fis
    * @param model
    *           the model
    */
   public void merge(FileInputStream fis, long id){
      List<RemoteSection> remoteSectionList = LircConfFile.getRemoteSectionList(fis);
      Model model = loadById(id);
      genericDAO.deleteAll(model.getRemoteSections());
      model.setRemoteSections(null);
      if (remoteSectionList.size() > 0) {
         String comment = remoteSectionList.get(0).getModel().getComment();
         model.setComment(comment);
         genericDAO.merge(model);
      }
      for (RemoteSection remoteSection : remoteSectionList) {
         remoteSection.setModel(model);
         genericDAO.save(remoteSection);
      }
   }

   /**
    * Find vendor.
    * 
    * @param vendorName
    *           the vendor name
    * 
    * @return the vendor
    */
   private Vendor findVendor(String vendorName) {
      Vendor vendor = genericDAO.getByNonIdField(Vendor.class, "name", vendorName);
      if (vendor == null) {
         Vendor newVendor = new Vendor();
         newVendor.setName(vendorName);
         genericDAO.save(newVendor);
         return newVendor;
      }
      return vendor;
   }

   /**
    * Creates the model.
    * 
    * @param vendor
    *           the vendor
    * @param modelName
    *           the model name
    * 
    * @return the model
    */
   private Model createModel(Vendor vendor, String modelName) {
      Model targetModel = null;
      targetModel = new Model();
      targetModel.setFileName(modelName);
      targetModel.setVendor(vendor);
      vendor.getModels().add(targetModel);
      genericDAO.save(targetModel);
      return targetModel;
   }

   /**
    * {@inheritDoc}
    */
   public String exportText(long id) {
      Model model = genericDAO.loadById(Model.class, id);
      return model.allSectionText();
   }

   /**
    * {@inheritDoc}
    */
   public InputStream exportStream(long id) {
      return new ByteArrayInputStream(exportText(id).getBytes());
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration
    *           the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteByName(String modelName) {
      Model model = genericDAO.getByNonIdField(Model.class, "fileName", modelName);
      if (model != null) {
         genericDAO.delete(model);
      }
   }

   /**
    * {@inheritDoc}
    * 
    */
   public int count() {
      return genericDAO.loadAll(Model.class).size();
   }
   
   /**
    * {@inheritDoc}
    * 
    */
   public boolean isFile(String path){
      return new File(configuration.getWorkCopyDir()+path).isFile();
   }
   
   /**
    * {@inheritDoc}
    * 
    */
   public Model findByFileName(String fileName){
      return genericDAO.getByNonIdField(Model.class, "fileName", fileName);
   }

   /**
    * {@inheritDoc}
    * 
    */
   public void syncWith(File file) {
      if(file.isDirectory() || FileUtil.isIgnored(file)){
         return;
      }
      boolean isDeleted = !file.exists();
      String[] arr = FileUtil.splitPath(file);
      String vendorName = arr[arr.length - 2];
      if(vendorName.equals("ovara")){
         vendorName = arr[arr.length - 3];
      }
      String modelName = arr[arr.length - 1];
      Model model = findByFileName(modelName);
      if(isDeleted){
         return;
      }
      if (model != null){
         merge(FileUtil.readStream(file.getAbsolutePath()),model.getOid());
      }else{
         FileInputStream fis = null;
         try {
            fis = new FileInputStream(file);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
         add(fis, vendorName, modelName);
      }
   }

   @Override
   public String downloadFile(long id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File exportFile(long id) {
      // TODO Auto-generated method stub
      return null;
   }
}
