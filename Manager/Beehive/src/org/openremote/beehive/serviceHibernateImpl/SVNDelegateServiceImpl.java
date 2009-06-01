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
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.Progress;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.ChangeCount;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.DifferenceModel;
import org.openremote.beehive.repo.LIRCEntry;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.SVNClientFactory;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.repo.LogMessage.ChangePath;
import org.openremote.beehive.utils.FileUtil;
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
   private ModelService modelService;
   private ISVNClientAdapter svnClient = SVNClientFactory.getSVNClient();
   private static Map<String, Object> fileLocks = new HashMap<String, Object>();   
   public Configuration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
   
   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }

   /**
    * {@inheritDoc}
    * @throws SVNException 
    */
   public void commit(String[] paths, String message, String username) throws SVNException {
      svnClient.setUsername(username);
      int totalPath = paths.length;
      File[] files = new File[totalPath];
      if (totalPath > 0) {
         try {
            for (int i = 0; i < totalPath; i++) {
               String[] arr = paths[i].split("\\|");
               String path = arr[0];
               String status = arr[1];
               files[i] = new File(configuration.getWorkCopyDir() + path);

               if (status != null) {
                  if (status.equals(Actions.UNVERSIONED.toString())) {
                     if (files[i].isDirectory()) {
                        svnClient.addDirectory(files[i], false);
                     } else {
                        svnClient.addFile(files[i]);
                     }
                  }
                  if(status.equals(Actions.MISSING.toString())){
                     svnClient.update(files[i], SVNRevision.HEAD, true);
                     svnClient.remove(new File[] { files[i] }, true);
                  }

               } else {
                  logger.info("The file of " + files[i] + " is not exist!");
               }
            }
            if (configuration.getWorkCopyDir().startsWith("/")) {    //linux
               svnClient.commit(files, message, true);
            } else {       //windows
               int mod = 500;
               int commitTimes = totalPath / mod + 1;
               int lastCommitCount = totalPath % mod;
               for (int i = 0; i < commitTimes; i++) {
                  if (i == commitTimes - 1) {
                     File[] subFiles = new File[lastCommitCount];
                     System.arraycopy(files, i * mod, subFiles, 0, lastCommitCount);
                     svnClient.commit(subFiles, message, true);
                  } else {
                     File[] subFiles = new File[mod];
                     System.arraycopy(files, i * mod, subFiles, 0, mod);
                     svnClient.commit(subFiles, message, true);
                  }
               }
            }
         } catch (SVNClientException e) {
            logger.error("The svnClientException!", e);
            throw new SVNException("The svnClient cause Exception",e);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void copyFromScrapToWC(String srcPath, String destPath) {
      File tempDir = new File(srcPath);
      File workDir = new File(destPath);
      copyDirectory(tempDir, workDir);
      FileUtil.writeStringToFile(configuration.getScrapDir()+File.separator+"copyProgress.txt", "Check completed!");
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
               if(!(tempName.equals("progress.txt") || tempName.equals("copyProgress.txt"))){
                  String workName = tempName.substring(0, tempName.lastIndexOf("."));
                  File workFile = new File(workDir, File.separator + workName);
                  copyFile(tempName, subFile, workFile);                  
               }
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
   public DiffResult diff(String url, long oldRevision, long newRevision) {
      DiffResult dr = new DiffResult();
      String uuid = UUID.randomUUID().toString();
      File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);
      try {
         SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir() + url);
         svnClient
               .diff(svnUrl, new SVNRevision.Number(oldRevision), new SVNRevision.Number(newRevision), tempFile, false);
         String strDiff = FileUtil.readFileToString(tempFile).toString();
         tempFile.delete();
         InputStream leftIS = svnClient.getContent(svnUrl, new SVNRevision.Number(oldRevision));
         String left = StringUtil.readStringInInputStream(leftIS).toString();

         InputStream rightIS = svnClient.getContent(svnUrl, new SVNRevision.Number(newRevision));
         String right = StringUtil.readStringInInputStream(rightIS).toString();

         DifferenceModel diff = new DifferenceModel(strDiff);
         dr.setLeft(diff.getLeftLines(left));
         dr.setRight(diff.getRightLines(right));
         ChangeCount changeCount = new ChangeCount(diff.getAddedItemsCount(), diff.getModifiedItemsCount(), diff.getDeletedItemsCount());
         dr.setChangeCount(changeCount);
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
   public List<LIRCEntry> getList(String url, long revision) {
      List<LIRCEntry> entryList = new ArrayList<LIRCEntry>();
      try {
         long headRevision = getHeadRevision();
         ISVNDirEntry[] list = svnClient.getList(new SVNUrl(configuration.getSvnDir() + url), new SVNRevision.Number(revision), false);
         for (ISVNDirEntry dirEntry : list) {
            LIRCEntry entry = new LIRCEntry();
            entry.setPath(dirEntry.getPath());
            entry.setVersion(dirEntry.getLastChangedRevision().getNumber());
            entry.setAuthor(dirEntry.getLastCommitAuthor());
            entry.setDate(dirEntry.getLastChangedDate());
            if(dirEntry.getLastChangedRevision().getNumber() == headRevision){
               entry.setHeadversion(true);
            }
            entry.setSize(dirEntry.getSize());
            if (dirEntry.getNodeKind().equals(SVNNodeKind.FILE)) {
               entry.setFile(true);
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
         logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir() + url), SVNRevision.HEAD,
               new SVNRevision.Number(1));
         for (ISVNLogMessage logMessage : logs) {
            LogMessage lm = new LogMessage();
            lm.setRevision(logMessage.getRevision().toString());
            lm.setAuthor(logMessage.getAuthor());
            lm.setDate(logMessage.getDate());
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
   public void rollback(String path, long revision) {
      // svnClient.setUsername(username);
      File file = new File(configuration.getWorkCopyDir() + path);
      try {
         if (file.isFile()) {
            InputStream is = svnClient.getContent(new SVNUrl(configuration.getSvnDir() + path), new SVNRevision.Number(
                  revision));
            FileUtil.createFile(is, file);
         } else {
            revert(path, true);
            deleteFile(file);
            String uuid = UUID.randomUUID().toString();
            File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);

            svnClient.doExport(new SVNUrl(configuration.getSvnDir() + path), tempFile,
                  new SVNRevision.Number(revision), true);
            FileUtils.copyDirectory(tempFile, file);
            FileUtils.deleteDirectory(tempFile);
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
               addFileRecursively(ds, state.getFile(), workDir);
            } else {
               String relativePath = state.getFile().getPath().replace(workDir, "").replaceAll("\\\\", "/");
               if (SVNStatusKind.DELETED == state.getTextStatus()) {
                  Element e = ds.new Element(relativePath, Actions.DELETEED);
                  ds.addElement(e);
               }else if (SVNStatusKind.MODIFIED == state.getTextStatus()) {
                  Element e = ds.new Element(relativePath, Actions.MODIFY);
                  ds.addElement(e);
               }else if(SVNStatusKind.ADDED == state.getTextStatus()){
                  Element e1 = ds.new Element(relativePath, Actions.ADDED);
                  ds.addElement(e1);
               }else if(SVNStatusKind.MISSING == state.getTextStatus()){
                  Element e = ds.new Element(relativePath, Actions.MISSING);
                  ds.addElement(e);
               }
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
         String progressFile = configuration.getScrapDir()+File.separator+"copyProgress.txt";
         String modelPath = modelFile.getPath().substring(configuration.getScrapDir().length(),modelFile.getPath().lastIndexOf("."));
         if (!workFile.getParentFile().exists()) {
            workFile.getParentFile().mkdirs();
         }
         String actionType = " N ";
         if (workFile.exists()) {
            String strDate = tempName.substring(tempName.lastIndexOf(".") + 1);
            Date tempDate = StringUtil.String2Date(strDate, "dd-MMM-yyyy kk-mm", Locale.ENGLISH);
            ISVNInfo parentFileInfo = svnClient.getInfoFromWorkingCopy(workFile.getParentFile());
            if(parentFileInfo.getLastChangedDate()!=null){
               ISVNInfo svnInfo = svnClient.getInfoFromWorkingCopy(workFile);
               if (svnInfo.getLastChangedDate() != null && tempDate.compareTo(svnInfo.getLastChangedDate()) > 0) {
                  FileUtil.copyFile(modelFile, workFile);
                  actionType = " M ";
               }            
            }else if (tempDate.compareTo(new Date(workFile.lastModified())) > 0) {
               FileUtil.copyFile(modelFile, workFile);
               actionType = " M ";
            } 
         } else {
            FileUtil.copyFile(modelFile, workFile);
            actionType = " A ";
         }
         FileUtil.writeStringToFile(progressFile, " ["+StringUtil.systemTime()+"] "+actionType + modelPath);
      } catch (SVNClientException e) {
         logger.error("SvnClient.getInfo touch off the SVNClientException," +
         		" This may occur by the fileName not case sensitive!",e);
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
    * This method is add unVersion or or directory to the diffStatus
    * 
    */
   private void addFileRecursively(DiffStatus ds, File file, String workDir) {
      if (file.isDirectory()) {
         Element e1 = ds.new Element(file.getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.UNVERSIONED);
         ds.addElement(e1);
         for (File subFile : file.listFiles()) {
            addFileRecursively(ds, subFile, workDir);
         }
      } else {
         Element e1 = ds.new Element(file.getPath().replace(workDir, "").replaceAll("\\\\", "/"), Actions.UNVERSIONED);
         ds.addElement(e1);
      }
   }
   
   /**
    * {@inheritDoc}
    */
   public boolean isBlankSVN(){
      boolean isBlank = false;
      try {
         SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir());
         if (svnClient.getList(svnUrl, SVNRevision.HEAD, true).length == 0){
            isBlank = true;
         }
      } catch (MalformedURLException e) {
         logger.error("Judge svn repo whether blank, create svnUrl error!", e);
      } catch (SVNClientException e) {
         logger.error("Judge svn repo whether blank, get list error!", e);
      }
      return isBlank;
   }

   /**
    * {@inheritDoc}
    */
   public long getHeadRevision() {
      ISVNInfo svnInfo = null;
      try {
         svnInfo = svnClient.getInfo(new SVNUrl(configuration.getSvnDir()));
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (SVNClientException e) {
         e.printStackTrace();
      }
      if (svnInfo != null) {
         return svnInfo.getLastChangedRevision().getNumber();
      }
      return 0;
   }
   
   /**
    * {@inheritDoc}
    */
   public LogMessage getHeadLog(String path) {
      LogMessage headLog = new LogMessage();
      try {
         ISVNLogMessage[] logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir()+path), SVNRevision.HEAD,SVNRevision.HEAD, new SVNRevision.Number(1) , true, false, 1);
         headLog.setRevision(logs[0].getRevision().toString());
         headLog.setAuthor(logs[0].getAuthor());
         headLog.setDate(logs[0].getDate());
         headLog.setComment(logs[0].getMessage());
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (SVNClientException e) {
         e.printStackTrace();
      }
      return headLog;
   }
   
   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public List<String> getFileContent(String path, long revision) {
      List<String> lines = new ArrayList<String>();
      try {
         SVNRevision svnrevision = SVNRevision.HEAD;
         if(revision != 0){
            svnrevision = new SVNRevision.Number(revision);
         }
         InputStream is = svnClient.getContent(new File(configuration.getWorkCopyDir() + path), svnrevision);
         lines = IOUtils.readLines(is);         
      } catch (SVNClientException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return lines;
   }
   
   /**
    * {@inheritDoc}
    */
   public LogMessage getLogByRevision(String path, long revision) {
      LogMessage logMessage = new LogMessage();
      try {
         ISVNLogMessage[] logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir()+path), new SVNRevision.Number(revision),new SVNRevision.Number(revision), new SVNRevision.Number(1) , true, false, 1);
         logMessage.setRevision(logs[0].getRevision().toString());
         logMessage.setAuthor(logs[0].getAuthor());
         logMessage.setDate(logs[0].getDate());
         logMessage.setComment(logs[0].getMessage());
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (SVNClientException e) {
         e.printStackTrace();
      }
      return logMessage;
   }
   
   /**
    * {@inheritDoc}
    */
   public Progress getCopyProgress() {
      File progressFile = new File(configuration.getScrapDir()+File.separator+"copyProgress.txt");
      return FileUtil.getProgressFromFile(progressFile, "Check completed!", modelService.count());
   }
   
   
}
