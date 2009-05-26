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
package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.LIRCEntry;
import org.openremote.beehive.repo.LogMessage;

/**
 * The Interface SVNDelegateService.
 * 
 * @author Tomsky
 */
public interface SVNDelegateService {
   
   /**
    * Commit.
    * 
    * @param paths the array is a set of relative path with workCopy,and begin with a fileSeparator
    * @param message the message
    * @param username the username
    * 
    * @return List<UpdatedFile>
    * 
    * @throws SVNException the SVN exception
    */
   void commit(String[] paths, String message, String username) throws SVNException;

   /**
    * This method is show a file's difference between head revision with workCopy.
    * 
    * @param pth the pth
    * 
    * @return DiffResult
    */
   DiffResult diff(String pth);

   /**
    * This method is show a file's difference between previous revision with specify revision.
    * 
    * @param url the url
    * @param oldRevision the old revision
    * @param newRevision the new revision
    * 
    * @return DiffResult
    */
   DiffResult diff(String url, long oldRevision, long newRevision);

   /**
    * Get the specify url's logs, which contain all history.
    * 
    * @param url which is a relative url with svnDirectory, and begin with a fileSeparator
    * 
    * @return List<LogMessage>
    */
   List<LogMessage> getLogs(String url);

   /**
    * Exports a clean directory tree from the repository specified by srcUrl, at revision revision.
    * 
    * @param srcUrl which is a relative url with svnDirectory, and begin with a fileSeparator
    * @param destPath which is a absolute path
    * @param revision the revision
    * @param force the force
    */
   void doExport(String srcUrl, String destPath, int revision, boolean force);

   /**
    * This method is getting a layer of dirEntry with the head revision.
    * 
    * @param url which is a relative url with svnDirectory, and begin with a fileSeparator
    * @param revision the revision
    * 
    * @return List<LIRCEntry>
    */
   List<LIRCEntry> getList(String url, long revision);

   /**
    * revert a file or a directory to head revision.
    * 
    * @param path which is an relative path of workCopy,and is head with a fileSeparator
    * @param recurse the recurse
    */
   void revert(String path, boolean recurse);

   /**
    * this method is for rollback a file or a directory from the head revision to a previous revision, but not
    * immediately commit.
    * 
    * @param path which is an relative path of workCopy,and is head with a fileSeparator
    * @param revision the revision
    */
   void rollback(String path, long revision);

   /**
    * sync the workCopy configuration files with scrapDirectory.
    * 
    * @param srcPath scrapDirectory, is an absolute path
    * @param destPath workCopyDirectory, is an absolute path
    */
   void copyFromScrapToWC(String srcPath, String destPath);

   /**
    * Copy from upload to wc.
    * 
    * @param srcPath the src path
    * @param destPath the dest path
    */
   void copyFromUploadToWC(String srcPath, String destPath);

   /**
    * delete a file from repository.
    * 
    * @param filePath which is an relative path of workCopy,which is head with a fileSeparator
    * @param username the username
    */
   void deleteFileFromRepo(String filePath, String username);

   /**
    * cancel the currency operation.
    */
   void cancelOperation();

   /**
    * Gets the differences of specify path's status which workCopy compare with the head revision.
    * 
    * @param path which is an relative path of workCopy,and is head with a fileSeparator
    * 
    * @return DiffStatus
    */
   DiffStatus getDiffStatus(String path);
   
   /**
    * Judge the SVN repo whether blank.
    * 
    * @return true, if checks if is blank svn
    */
   boolean isBlankSVN();
   
   /**
    * Gets the head revision.
    * 
    * @return the head revision
    */
   long getHeadRevision();
   
   /**
    * Gets the head log.
    * 
    * @param path the path
    * 
    * @return the head log
    */
   LogMessage getHeadLog(String path);
   
   /**
    * Gets the file content.
    * 
    * @param path the path
    * @param revision the revision
    * 
    * @return the file content
    */
   List<String> getFileContent(String path, long revision);
   
   /**
    * Gets the log by revision.
    * 
    * @param path the path
    * @param revision the revision
    * 
    * @return the log by revision
    */
   LogMessage getLogByRevision(String path, long revision);
}
