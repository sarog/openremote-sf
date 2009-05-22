/**
 * 
 */
package org.openremote.beehive.file;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.UpdatedFile;
import org.openremote.beehive.serviceHibernateImpl.GenericDAO;
import org.openremote.beehive.utils.FileUtil;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * @author Tomsky
 *
 */
public class LircSynchronizer extends ApplicationObjectSupport{
   
   public void update(String[] paths, String message, String username) throws SVNException{
      VendorService vendorService = (VendorService) getApplicationContext().getBean("vendorService");
      ModelService modelService = (ModelService) getApplicationContext().getBean("modelService");
      SVNDelegateService svnDelegateService = (SVNDelegateService) getApplicationContext().getBean("svnDelegateService");
      GenericDAO genericDAO = (GenericDAO) getApplicationContext().getBean("genericDAO");
      List<UpdatedFile> updatedFiles = svnDelegateService.commit(paths, message, username);
      StopWatch watch = new StopWatch();
      
      for (int i = 0; i < updatedFiles.size(); i++) {
         watch.start();
         UpdatedFile updatedFile = updatedFiles.get(i);
         String[] arr = FileUtil.splitPath(updatedFile.getFile());
         if (updatedFile.getStatus() == Actions.MODIFY) {
            String modelName = arr[arr.length - 1];
            Model model = genericDAO.getByNonIdField(Model.class, "fileName", modelName);
            modelService.merge(FileUtil.readStream(updatedFile.getFile().getAbsolutePath()),model);
         } else if (updatedFile.getStatus() == Actions.ADD) {
            if (updatedFile.getFile().isFile() && !FileUtil.isIgnored(updatedFile.getFile())) {
               String vendorName = arr[arr.length - 2];
               String modelName = arr[arr.length - 1];
               System.out.println(updatedFile.getFile().getAbsolutePath());
               Model model = genericDAO.getByNonIdField(Model.class, "fileName", modelName);
               if(model != null){
                  modelService.merge(FileUtil.readStream(updatedFile.getFile().getAbsolutePath()),model);
//                  genericDAO.delete(model);
               }else{
                  modelService.add(FileUtil.readStream(updatedFile.getFile().getAbsolutePath()), vendorName, modelName);                                 
               }
            } else {
               String vendorName = arr[arr.length - 1];
               Vendor tempVendor = genericDAO.getByNonIdField(Vendor.class, "name", vendorName);
               if(tempVendor == null){
                  Vendor vendor = new Vendor();
                  vendor.setName(vendorName);
                  genericDAO.save(vendor);                  
               }
            }
         } else if (updatedFile.getStatus() == Actions.DELETE) {
            String name = arr[arr.length - 1];
            if (updatedFile.isDir()) {
               vendorService.deleteByName(name);
            } else {
               modelService.deleteByName(name);
            }
         }
         if((i+1)%100 == 0){
            genericDAO.flush();
            System.out.println("=========================when i="+i+", the genericDAO flush at "+new Date(System.currentTimeMillis())+"==========");
         }
         watch.stop();
         System.out.println(watch.getTime());
         watch.reset();
      }
   }
}
