/**
 * 
 */
package org.openremote.beehive.service;

import java.io.File;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.spring.SpringContext;

/**
 * @author Tomsky
 * 
 */
public class SVNDelegateServiceTest extends TestBase {
	private SVNDelegateService svnDelegateService = (SVNDelegateService) SpringContext
			.getInstance().getBean("svnDelegateService");

	public void testCopyFromScrapToWC() {
//		svnDelegateService.copyFromScrapToWC("D:/tremote", "D:/tst2");
	}
	
	public void testCommit(){
//		String[] paths = {"/audiovox/Sirius_PNP2"};
//		svnDelegateService.commit(paths, "modify /audiovox/Sirius_PNP2", "admin");
		
//		String path = "/";
//		DiffStatus diffStatus = svnDelegateService.getDiffStatus(path);
//		String[] paths = new String[diffStatus.getDiffStatus().size()];
//		String workDir = new File("c:/workCOpy").getPath();
//		for (int i = 0; i < diffStatus.getDiffStatus().size(); i++) {
//			paths[i] = diffStatus.getDiffStatus().get(i).getPath().replace(workDir, "");			
//		}
//		svnDelegateService.commit(paths, "commit all changes", "admin");
	}
	
	public void testRevert(){
//		String path = "/orava/RC_5219";
//		svnDelegateService.revert(path, false);
	}
	
	public void testDoExport(){
//		String srcUrl = "/sky";
//		String destPath = "d:/tst2";
//		svnDelegateService.doExport(srcUrl, destPath, 26, true);
	}
	
	public void testRollback(){
//		String path = "/";
//		svnDelegateService.rollback(path, 104);
	}
	
	public void testGetList(){
//		String url = "/orava";
//		List<LIRCEntryDTO>  ls = svnDelegateService.getList(url, 28);
//		for (LIRCEntryDTO entryDTO : ls) {
//			System.out.println("path: " + entryDTO.getPath()+ " version: " + entryDTO.getVersion());
//			if(entryDTO.isFile()){
//				System.out.println(entryDTO.getContent());
//			}
//		}
	}
	
	public void testGetLogs(){
//		String path = "/sky";
//		List<LogMessage> lms = svnDelegateService.getLogs(path);
//		for (LogMessage log : lms) {
//			System.out.print(log.getRevision()+" , "+log.getAuthor()+" , "+log.getComment()+" actions= ");
//			for (Character action : log.getActions()) {
//				System.out.print(action+" ");
//			}
//			System.out.println();
//			for (ChangePath ch : log.getChangePaths()) {
//				System.out.println(ch.getPath()+", "+ch.getAction());
//			}
//		}
	}
	public void testCopyFromUpload(){
//		String srcPath = "d:/sky/Rev4";
//		String destPath = "/sky/Rev4";
//		svnDelegateService.copyFromUploadToWC(srcPath, destPath);		
	}
	
	public void testDeleteFile(){
//		String path = "/sky/Rev4";
//		svnDelegateService.deleteFileFromRepo(path, "test");
	}
	
	/**
	 * 
	 * this is test the workCopy with head version
	 */
	public void testDiff(){
//		String url = "/sky/Rev4";
//		DiffResult dr = svnDelegateService.diff(url, true);
//		List<Line> leftLines = dr.getLeft();
//		if(leftLines.isEmpty()){
//			System.out.println("--------------------------------");
//		}else{
//			for (Line leftLine : leftLines) {
//				System.out.println(leftLine.getImage()+": "+leftLine.getLine());
//			}
//		}
//		
//		List<Line> rightLines = dr.getRight();
//		if(rightLines.isEmpty()){
//			System.out.println("++++++++++++++++++++++++++++++++");
//			
//		}else{
//			for (Line rightLine : rightLines) {
//				System.out.println(rightLine.getImage()+": "+rightLine.getLine());
//			}
//		}
	}
	
	public void testGetdiffStatus(){
//		String path = "/";
//		DiffStatus ds = svnDelegateService.getDiffStatus(path);
//		for (Element e : ds.getDiffStatus()) {
//			System.out.println(e.getPath()+": " + e.getImage());
//		}
	}
}
