package org.openremote.beehive.service;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.spring.SpringContext;

public class ModelServiceTest extends TestBase {

    private ModelService service = (ModelService) SpringContext
            .getInstance().getBean("modelService");

//	public void testAddLircConfFile() {
//		StopWatch watch = new StopWatch();
//		watch.start();
//		service.add(FileUtil.readStream("C:\\remotes\\3m\\MP8640"),"3m","MP8640");//single section
//		service.add(FileUtil.readStream("C:\\remotes\\sigma_designs\\lircd.conf.realmagic"),"sigma_designs","lircd.conf.realmagic");//multi section
//		service.add(FileUtil.readStream("C:\\remotes\\zenith\\ZN110"),"zenith","ZN110");//raw code
//		watch.stop();
//		System.out.println(watch.getTime());
//	}

//    public void tes1tScrap() {
////		LircConfFileScraper.scrapDir("C:\\remotes");
//    }
    
    public void testExportText(){
//    	System.out.println(service.exportText(1L));
    }
    
    public void testExportFile(){
//    	System.out.println(service.exportText(1L));
    }
    
    public void testUpdate(){
//    	String[] paths = {"/3m"}; 
//    	try {
//         service.update(paths, "update directory", "username");
//      } catch (SVNException e) {
//         e.printStackTrace();
//      }
    }
    
    public void testRollback(){
//    	String path = "/";
//    	service.rollback(path, 101, "tomsky");
    }
}
