package org.openremote.beehive.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.domain.RemoteOption;
import org.openremote.beehive.spring.SpringContext;
import org.openremote.beehive.utils.FileUtil;

/**
 * Tool class for scraping the LIRC configuration file
 * 
 * @author Dan 2009-2-16
 *
 */
public class LircConfFileScraper {
	
	private static ModelService modelService = (ModelService) SpringContext
	.getInstance().getBean("modelService");

	
	/*public static void scrap(String strPath) { 
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        if (files == null) {
        	return; 
        }
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
            	scrap(files[i].getAbsolutePath()); 
            } else { 
            	new LircConfFile(files[i]).discoverOptions();
            } 
        } 
    }*/
	
	/*public static void scrapDir(String strPath) { 
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        if (files == null) {
        	return; 
        }
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
            	scrapDir(files[i].getAbsolutePath()); 
            } else {
            	String path = files[i].getAbsolutePath();
            	if(FileUtil.isImage(files[i])){
            		continue;
            	}
            	System.out.println(path);
            	String[] arr = path.split("\\\\");
            	String vendorName = arr[arr.length - 2];
            	String modelName = arr[arr.length - 1];
            	lircConfFileService.add(FileUtil.readStream(files[i].getAbsolutePath()),vendorName,modelName);
            } 
        } 
    }*/
	/**
	 * Scraps a directory in file system containing LIRC configuration files.
	 */
	public static void scrapDir(String strPath) {
		File dir = new File(strPath);
		for (File vendorDir : dir.listFiles()) {
			if(vendorDir.isDirectory()){
				for (File modelFile : vendorDir.listFiles()) {
					String[] arr = FileUtil.splitPath(modelFile);
					String vendorName = arr[arr.length - 2];
					String modelName = arr[arr.length - 1];
					if (modelFile.isDirectory()) {
						for (File subModelFile : modelFile.listFiles()) {
							arr = FileUtil.splitPath(subModelFile);
							modelName = arr[arr.length - 1];
							importFile(vendorName, modelName, subModelFile);
						}
					} else {
	//					System.out.println(path);
						importFile(vendorName, modelName, modelFile);
					}
				}
			}
		}		
	}



	private static void importFile(String vendorName, String modelName,
			File subModel) {
		if (!FileUtil.isIgnored(subModel)) {
			modelService.add(FileUtil.readStream(subModel
					.getAbsolutePath()), vendorName, modelName);
		}
	}
	
	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        System.out.println("Enter your lircd.conf path:");
        try {
			str = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RemoteOption.reset();
		LircConfFileScraper.scrapDir(str);
		RemoteOption.print();
	}
	
}
