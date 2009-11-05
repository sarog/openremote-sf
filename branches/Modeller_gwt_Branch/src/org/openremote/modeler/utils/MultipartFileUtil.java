package org.openremote.modeler.utils;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public final class MultipartFileUtil {

   private static final Logger LOGGER = Logger.getLogger(MultipartFileUtil.class);
   
   @SuppressWarnings("unchecked")
   public static MultipartFile getMultipartFileFromRequest(HttpServletRequest request, String fileFieldName) {
      MultipartFile multipartFile = null;
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List items = null;
      try {
         items = upload.parseRequest(request);
      } catch (FileUploadException e) {
         LOGGER.error("get InputStream from httpServletRequest error.", e);
         e.printStackTrace();
      }
      if (items == null) {
         return null;
      }
      Iterator it = items.iterator();
      FileItem fileItem = null;
      while (it.hasNext()) {
         fileItem = (FileItem) it.next();
         if (!fileItem.isFormField() && fileFieldName != null && fileFieldName.equals(fileItem.getFieldName())) {
            break;
         }
      }
      if (fileItem != null) {
         multipartFile = new CommonsMultipartFile(fileItem);
      }
      return multipartFile;
   }
}
