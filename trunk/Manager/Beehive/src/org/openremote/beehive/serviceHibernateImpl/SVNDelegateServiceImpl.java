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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.ChangeCount;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.DifferenceModel;
import org.openremote.beehive.repo.LIRCEntry;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.UpdatedFile;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.repo.LogMessage.ChangePath;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.SVNClientFactory;
import org.openremote.beehive.utils.StringUtil;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * SVNDelegateServiceImpl wrap all svn operations
 * @author Tomsky
 * 
 */
public class SVNDelegateServiceImpl extends BaseAbstractService<Vendor> implements SVNDelegateService {
   private static Logger logger = Logger.getLogger(SVNDelegateServiceImpl.class.getName());
   private Configuration configuration;
   private ISVNClientAdapter svnClient = SVNClientFactory.getSVNClient();
   private static Map<String, Object> fileLocks = new HashMap<String, Object>();

   public Configuration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   public List<UpdatedFile> commit(String[] paths, String message, String username) {
      svnClient.setUsername(username);
      List<UpdatedFile> updatedFiles = new ArrayList<UpdatedFile>();
      File[] files = new File[paths.length];
      if (paths.length > 0) {
         try {
            for (int i = 0; i < paths.length; i++) {
               files[i] = new File(configuration.getWorkCopyDir() + paths[i]);

               ISVNStatus[] fileStatus = svnClient.getStatus(files[i], false, false);
               if (fileStatus.length > 0) {
                  if (SVNStatusKind.UNVERSIONED == fileStatus[0].getTextStatus()) {
                     UpdatedFile addedFile = new UpdatedFile(fileStatus[0].getFile(), Actions.ADD);
                     if (fileStatus[0].getFile().isDirectory()) {
                        svnClient.addDirectory(fileStatus[0].getFile(), true);
                     } else {
                        svnClient.addFile(fileStatus[0].getFile());
                     }
                     updatedFiles.add(addedFile);
                  } else if (SVNStatusKind.DELETED == fileStatus[0].getTextStatus()
                        || SVNStatusKind.MISSING == fileStatus[0].getTextStatus()) {
                     UpdatedFile deletedFile = new UpdatedFile(fileStatus[0].getFile(), Actions.DELETE);
                     if (SVNStatusKind.MISSING == fileStatus[0].getTextStatus()) {
                        svnClient.remove(new File[] { fileStatus[0].getFile() }, true);
                     }
                     updatedFiles.add(deletedFile);
                  } else if (SVNStatusKind.MODIFIED == fileStatus[0].getTextStatus()) {
                     UpdatedFile modifiedFile = new UpdatedFile(fileStatus[0].getFile(), Actions.MODIFY);
                     updatedFiles.add(modifiedFile);
                  }
               } else {
                  logger.info("The file of " + files[i] + " is not exist!");
               }
            }
            svnClient.commit(files, message, true);
         } catch (SVNClientException e) {
            logger.error("The svnClientException!", e);
         }
      }
      return updatedFiles;
   }

   /**
    * {@inheritDoc}
    */
   public void copyFromScrapToWC(String srcPath, String destPath) {
      File tempDir = new File(srcPath);
      File workDir = new File(destPath);
      copyDirectory(tempDir, workDir);
      // for (File vendorDir : tempDir.listFiles()) {
      // if(vendorDir.isDirectory()){
      // for (File modelFile : vendorDir.listFiles()) {
      // if (modelFile.isFile()) {
      // String tempName = modelFile.getName();
      // String workName = tempName.substring(0, tempName.lastIndexOf("."));
      // File workFile = new File(workDir,vendorDir.getName()+File.separator+workName);
      // copyFile(tempName,modelFile,workFile);
      // } else {
      // for (File subModelFile : modelFile.listFiles()) {
      // String tempName = subModelFile.getName();
      // String workName = tempName.substring(0, tempName.lastIndexOf("."));
      // File workFile = new
      // File(workDir,vendorDir.getName()+File.separator+modelFile.getName()+File.separator+workName);
      // copyFile(tempName,subModelFile,workFile);
      // }
      // }
      // }
      // }
      // }
      try {
         if (SVNStatusKind.UNVERSIONED.equals(svnClient.getSingleStatus(workDir).getTextStatus().toString())) {
            // File repo = new File("d:/svn-repos");
            // repo.mkdirs();
            // svnClient.createRepository(repo, ISVNClientAdapter.REPOSITORY_BDB);
            SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir());
            svnClient.mkdir(svnUrl, true, "create beehive/trunk");
            svnClient.doImport(workDir, svnUrl, "import beehive to trunk", true);
            FileUtil.deleteDirectory(workDir);
            workDir.mkdirs();
            svnClient.checkout(svnUrl, workDir, SVNRevision.HEAD, true);
         }
      } catch (SVNClientException e) {
         logger.error("svnClient getSingleStatus failed!", e);
      } catch (MalformedURLException e) {
         logger.error("initiliaze svnUrl of trunk failed!", e);
      }
      logger.info("Success copy scrap files to workCopy " + destPath);
   }

   private void copyDirectory(File tempDir, File workDir) {
      if (tempDir.isDirectory()) {
         for (File subFile : tempDir.listFiles()) {
            if (subFile.isDirectory()) {
               File workFile = new File(workDir, File.separator + subFile.getName());
               copyDirectory(subFile, workFile);
            } else if (subFile.isFile()) {
               String tempName = subFile.getName();
               String workName = tempName.substring(0, tempName.lastIndexOf("."));
               File workFile = new File(workDir, File.separator + workName);
               copyFile(tempName, subFile, workFile);
            }
         }
      } else if (tempDir.isFile()) {
         String tempName = tempDir.getName();
         String workName = tempName.substring(0, tempName.lastIndexOf("."));
         File workFile = new File(workDir, File.separator + workName);
         copyFile(tempName, tempDir, workFile);

      }
   }

   /**
    * {@inheritDoc}
    */
   public void copyFromUploadToWC(String srcPath, String destPath) {
      File srcFile = new File(srcPath);
      File destFile = new File(configuration.getWorkCopyDir() + destPath);
      if (!fileLocks.containsKey(destPath)) {
         fileLocks.put(destPath, new Object());
      }
      synchronized (fileLocks.get(destPath)) {
         if (destFile.exists()) {
            destFile.delete();
         }
         FileUtil.copyFile(srcFile, destFile);
         logger.info("Copy file " + srcPath + " to " + destFile.getPath());
      }
   }

   /**
    * {@inheritDoc}
    */
   public DiffResult diff(String path) {
      DiffResult dr = new DiffResult();
      String uuid = UUID.randomUUID().toString();
      File file = new File(configuration.getWorkCopyDir() + path);
      File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);
      if(!file.isDirectory()){
         try {
            svnClient.diff(file, tempFile, true);
            String strDiff = FileUtils.readFileToString(tempFile, "UTF8");
            tempFile.delete();
            InputStream is = svnClient.getContent(file, SVNRevision.HEAD);
            String left = StringUtil.readStringInInputStream(is).toString();
            ISVNStatus[] status = svnClient.getStatus(file, false, true);
            String right = null;
            if (SVNStatusKind.NORMAL.equals(status[0].getTextStatus())) {
               dr.setLeft(null);
               dr.setRight(null);
            } else if (SVNStatusKind.UNVERSIONED.equals(status[0].getTextStatus())) {
               dr.setLeft(null);
               right =FileUtil.readFileToString(file).toString();
               dr.setRight(DifferenceModel.getUntouchedLines(right));
            } else if (SVNStatusKind.DELETED.equals(status[0].getTextStatus())
                  || SVNStatusKind.MISSING.equals(status[0].getTextStatus())) {
               dr.setLeft(DifferenceModel.getUntouchedLines(left));
               dr.setRight(null);
            } else {
               DifferenceModel diff = new DifferenceModel(strDiff);
               right =FileUtil.readFileToString(file).toString();
               dr.setLeft(diff.getLeftLines(left));
               dr.setRight(diff.getRightLines(right));
               ChangeCount changeCount= new ChangeCount(diff.getAddedItemsCount(),diff.getModifiedItemsCount(),diff.getDeletedItemsCount());
               dr.setChangeCount(changeCount);
            }
         } catch (IOException e) {
            logger.error("The IOException!", e);
         } catch (SVNClientException e) {
            logger.error("The SVNClientException!", e);
         }         
      }
      return dr;

   }

   /**
    * {@inheritDoc}
    */
   public DiffResult diff(String url, int revision, Character action) {
      DiffResult dr = new DiffResult();
      String uuid = UUID.randomUUID().toString();
      File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);
      try {
         SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir() + url);
         svnClient
               .diff(svnUrl, new SVNRevision.Number(revision - 1), new SVNRevision.Number(revision), tempFile, false);
         String strDiff = FileUtil.readFileToString(tempFile).toString();
         tempFile.delete();
         InputStream leftIS = svnClient.getContent(svnUrl, new SVNRevision.Number(revision - 1));
         String left = StringUtil.readStringInInputStream(leftIS).toString();

         InputStream rightIS = svnClient.getContent(svnUrl, new SVNRevision.Number(revision));
         String right = StringUtil.readStringInInputStream(rightIS).toString();

         if (Actions.ADD.equals(action)) {
            dr.setLeft(null);
            dr.setRight(DifferenceModel.getUntouchedLines(right));
         } else if (Actions.DELETE.equals(action)) {
            dr.setLeft(DifferenceModel.getUntouchedLines(left));
            dr.setRight(null);
         } else {
            DifferenceModel diff = new DifferenceModel(strDiff);
            dr.setLeft(diff.getLeftLines(left));
            dr.setRight(diff.getRightLines(right));
         }
      } catch (IOException e) {
         logger.error("The IOException!", e);
      } catch (SVNClientException e) {
         logger.error("The SVNClientException!", e);
      }
      return dr;
   }

   /**
    * {@inheritDoc}
    */
   public void doExport(String srcUrl, String destPath, int revision, boolean force) {
      try {
         svnClient.doExport(new SVNUrl(configuration.getSvnDir() + srcUrl), new File(destPath), new SVNRevision.Number(
               revision), force);
      } catch (SVNClientException e) {
         logger.error("Error when export form " + srcUrl + " to " + destPath, e);
      } catch (MalformedURLException e) {
         logger.error("Failed to create svnUrl!", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<LIRCEntry> getList(String url, int revision) {
      List<LIRCEntry> entryList = new ArrayList<LIRCEntry>();
      try {
         ISVNDirEntry[] list = svnClient.getList(new SVNUrl(configuration.getSvnDir() + url), SVNRevision.HEAD, false);
         for (ISVNDirEntry dirEntry : list) {
            LIRCEntry entry = new LIRCEntry();
            entry.setPath(dirEntry.getPath());
            entry.setVersion(new Integer(dirEntry.getLastChangedRevision().toString()));
            entry.setAuthor(dirEntry.getLastCommitAuthor());
            if (dirEntry.getNodeKind().equals(SVNNodeKind.FILE)) {
               entry.setFile(true);
               InputStream is = svnClient.getContent(new SVNUrl(configuration.getSvnDir() + url + File.separator
                     + dirEntry.getPath()), dirEntry.getLastChangedRevision());
               StringBuffer strBuffer = StringUtil.readStringInInputStream(is);
               entry.setContent(strBuffer.toString());
            } else {
               entry.setFile(false);
            }
            entryList.add(entry);
         }
      } catch (MalformedURLException e) {
         logger.error("The MalformedURLException!", e);
      } catch (SVNClientException e) {
         logger.error("The SVNClientException!", e);
      }
      return entryList;
   }

   /**
    * {@inheritDoc}
    */
   public List<LogMessage> getLogs(String url) {
      List<LogMessage> lms = new ArrayList<LogMessage>();

      try {
         ISVNLogMessage[] logs;
         logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir() + url), new SVNRevision.Number(1),
               SVNRevision.HEAD);
         for (ISVNLogMessage logMessage : logs) {
            LogMessage lm = new LogMessage();
            lm.setRevision(logMessage.getRevision().toString());
            lm.setAuthor(logMessage.getAuthor());
            lm.setComment(logMessage.getMessage());
            for (ISVNLogMessageChangePath change : logMessage.getChangedPaths()) {
               ChangePath cp = lm.new ChangePath(change.getPath(), change.getAction());
               lm.addChangePath(cp);
            }
            lms.add(lm);
         }
      } catch (MalformedURLException e) {
         logger.error("The MalformedURLException!", e);
      } catch (SVNClientException e) {
         logger.error("The SVNClientException!", e);
      }
      return lms;
   }

   /**
    * {@inheritDoc}
    */
   public void revert(String path, boolean recurse) {
      File file = new File(configuration.getWorkCopyDir() + path);
      try {
         svnClient.revert(file, recurse);
      } catch (SVNClientException e) {
         logger.error("The file " + path + " can't revert!", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void rollback(String path, int revision) {
      // svnClient.setUsername(username);
      File file = new File(configuration.getWorkCopyDir() + path);
      try {
         if (file.isFile()) {
            InputStream is = svnClient.getContent(new SVNUrl(configuration.getSvnDir() + path), new SVNRevision.Number(
                  revision));
            FileUtil.createFile(is, file);
            // svnClient.commit(new File[]{file}, "rollback "+ file.getPath() + " to version " + revision, recurse);
         } else {
            revert(path, true);
            deleteFile(file);
            String uuid = UUID.randomUUID().toString();
            File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);

            svnClient.doExport(new SVNUrl(configuration.getSvnDir() + path), tempFile,
                  new SVNRevision.Number(revision), true);
            FileUtils.copyDirectory(tempFile, file);
            FileUtils.deleteDirectory(tempFile);
            // this.commit(new String[]{path}, "rollback to version of " + revision, username);
         }
      } catch (MalformedURLException e) {
         logger.error("The MalformedURLException!", e);
      } catch (SVNClientException e) {
         logger.error("The SVNClientException!", e);
      } catch (IOException e) {
         logger.error("The IOException!", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void deleteFileFromRepo(String filePath, String username) {
      File path = new File(configuration.getWorkCopyDir() + filePath);
      svnClient.setUsername(username);
      try {
         svnClient.revert(path, true);
         if (path.exists()) {
            svnClient.remove(new File[] { path }, true);
            // svnClient.commit(new File[] { path }, "remove the file "
            // + path.getPath(), true);
         }
      } catch (SVNClientException e) {
         e.printStackTrace();
      }

   }

   public void cancelOperation() {
      try {
         svnClient.cancelOperation();
      } catch (SVNClientException e) {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   public DiffStatus getDiffStatus(String path) {
	  String workDir = new File(configuration.getWorkCopyDir()).getPath();
      File filePath = new File(configuration.getWorkCopyDir() + path);
      DiffStatus ds = new DiffStatus();
      try {
         ISVNStatus[] status = svnClient.getStatus(filePath, true, false);
         for (ISVNStatus state : status) {
            if (SVNStatusKind.UNVERSIONED == state.getTextStatus()) {
               addFile(ds, state.getFile(), workDir);
            } else if (SVNStatusKind.DELETED == state.getTextStatus() || SVNStatusKind.MISSING == state.getTextStatus()) {
               Element e = ds.new Element(state.getFile().getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.DELETE);
               ds.addElement(e);
            } else if (SVNStatusKind.MODIFIED == state.getTextStatus()) {
               Element e = ds.new Element(state.getFile().getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.MODIFY);
               ds.addElement(e);
            }
         }
      } catch (SVNClientException e) {
         logger.error("The SVNClientException!", e);
      }
      return ds;
   }

   /**
    * This method is used for copy a scrapFile to workCopy after compare the date
    * 
    */
   private void copyFile(String tempName, File modelFile, File workFile) {
      try {
         if (!workFile.getParentFile().exists()) {
            workFile.getParentFile().mkdirs();
         }
         if (workFile.exists()) {
            String strDate = tempName.substring(tempName.lastIndexOf(".") + 1);
            Date tempDate = StringUtil.String2Date(strDate, "dd-MMM-yyyy kk-mm", Locale.ENGLISH);
            ISVNInfo svnInfo = svnClient.getInfo(workFile);
            if (svnInfo.getLastChangedDate() != null) {
               if (tempDate.compareTo(svnInfo.getLastChangedDate()) > 0) {
                  FileUtil.copyFile(modelFile, workFile);
               }
            } else if (tempDate.compareTo(new Date(workFile.lastModified())) > 0) {
               FileUtil.copyFile(modelFile, workFile);
            }
         } else {
            FileUtil.copyFile(modelFile, workFile);
         }
      } catch (SVNClientException e) {
         logger
               .error(
                     "SvnClient.getInfo touch off the SVNClientException, This may occur by the fileName not case sensitive!",
                     e);
      }
   }

   /**
    * This method is used for delete the file or directory in workCopy except the ".svn"
    * 
    */
   private void deleteFile(File path) {
      for (File file : path.listFiles()) {
         if (file.isFile()) {
            file.delete();
         }
         if (file.isDirectory() && !file.getName().equals(".svn")) {
            deleteFile(file);
         }
      }
   }

   /**
    * This method is add unVersion file or directory to the diffStatus
    * 
    */
   private void addFile(DiffStatus ds, File file, String workDir) {
      if (file.isDirectory()) {
         Element e1 = ds.new Element(file.getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.ADD);
         ds.addElement(e1);
         for (File subFile : file.listFiles()) {
            addFile(ds, subFile, workDir);
         }
      } else {
         Element e1 = ds.new Element(file.getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.ADD);
         ds.addElement(e1);
      }
   }

}
