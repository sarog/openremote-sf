package org.openremote.beehive;

public class Configuration {

	public String downloadDir;
	public String uploadDir;
	public String downloadUrlRoot;
	public String scrapDir;
	
	public String getDownloadDir() {
		return downloadDir;
	}
	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}
	public String getUploadDir() {
		return uploadDir;
	}
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
	public String getDownloadUrlRoot() {
		return downloadUrlRoot;
	}
	public void setDownloadUrlRoot(String downloadUrlRoot) {
		this.downloadUrlRoot = downloadUrlRoot;
	}
	public String getScrapDir() {
		return scrapDir;
	}
	public void setScrapDir(String scrapDir) {
		this.scrapDir = scrapDir;
	}
	

}
