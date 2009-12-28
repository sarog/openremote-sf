/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.openremote.beehive.serviceHibernateImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.RemoteSection;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.LircConfFile;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.UpdatedFile;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.StringUtil;

/**
 * {@inheritDoc}
 * 
 * @author allen.wei
 */
public class ModelServiceImpl extends BaseAbstractService<Model> implements ModelService {
   private static Logger logger = Logger.getLogger(ModelServiceImpl.class.getName());
   private Configuration configuration;
   private SVNDelegateService svnDelegateService;

   public SVNDelegateService getSvnDelegateService() {
      return svnDelegateService;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

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

   public ModelDTO loadModelById(long modelId) {
      ModelDTO modelDTO = new ModelDTO();
      try {
         BeanUtils.copyProperties(modelDTO, loadById(modelId));
      } catch (IllegalAccessException e) {
         // TODO handle exception
         e.printStackTrace();
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
   public File exportFile(long id) {
      Model model = genericDAO.loadById(Model.class, id);
      String path = model.filePath();
      String filePath = StringUtil.appendFileSeparator(new Configuration().getDownloadDir()) + path;
      FileUtil.writeFile(filePath, model.allSectionText());
      return new File(path);
   }

   /**
    * {@inheritDoc}
    */
   public String downloadFile(long id) {
      Model model = genericDAO.loadById(Model.class, id);
      String path = model.filePath();
      String filePath = StringUtil.appendFileSeparator(configuration.getDownloadDir()) + path;
      FileUtil.writeFile(filePath, model.allSectionText());
      return path;
   }

   /**
    * {@inheritDoc}
    */
   public InputStream exportStream(long id) {
      return new ByteArrayInputStream(exportText(id).getBytes());
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    * @throws SVNException 
    */
   public void update(String[] paths, String message, String username) throws SVNException {
      List<UpdatedFile> updatedFiles = svnDelegateService.commit(paths, message, username);
      for (UpdatedFile updatedFile : updatedFiles) {
         String[] arr = FileUtil.splitPath(updatedFile.getFile());
         if (updatedFile.getStatus() == Actions.MODIFY) {
            String modelName = arr[arr.length - 1];
            Model model = genericDAO.getByNonIdField(Model.class, "fileName", modelName);
            String vendorName = model.getVendor().getName();
            deleteModel(modelName);
            add(FileUtil.readStream(updatedFile.getFile().getAbsolutePath()), vendorName, modelName);
         } else if (updatedFile.getStatus() == Actions.ADD) {
            if (updatedFile.getFile().isFile()) {
               String vendorName = arr[arr.length - 2];
               String modelName = arr[arr.length - 1];
               add(FileUtil.readStream(updatedFile.getFile().getAbsolutePath()), vendorName, modelName);
            } else {
               String vendorName = arr[arr.length - 1];
               Vendor vendor = new Vendor();
               vendor.setName(vendorName);
               genericDAO.save(vendor);
            }
         } else if (updatedFile.getStatus() == Actions.DELETE) {
            String name = arr[arr.length - 1];
            if (updatedFile.isDir()) {
               deleteVendor(name);
            } else {
               deleteModel(name);
            }
         }
      }
   }

   private void deleteVendor(String vendorName) {
      Vendor vendor = genericDAO.getByNonIdField(Vendor.class, "name", vendorName);
      if (vendor != null) {
         genericDAO.delete(vendor);
      }
   }

   private void deleteModel(String modelName) {
      Model model = genericDAO.getByNonIdField(Model.class, "fileName", modelName);
      if (model != null) {
         genericDAO.delete(model);
      }
   }

   /**
    * {@inheritDoc}
    * @throws SVNException 
    */
   public void rollback(String path, int revision, String username) throws SVNException {
      svnDelegateService.rollback(path, revision);
      DiffStatus diffStatus = svnDelegateService.getDiffStatus(path);
      String[] paths = new String[diffStatus.getDiffStatus().size()];
      String workDir = new File(configuration.getWorkCopyDir()).getPath();
      for (int i = 0; i < diffStatus.getDiffStatus().size(); i++) {
         paths[i] = diffStatus.getDiffStatus().get(i).getPath().replace(workDir, "");

      }
      this.update(paths, "rollback to revision " + revision, username);
   }

}
