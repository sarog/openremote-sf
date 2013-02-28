/**
 * 
 */
package org.openremote.beehive.service;

import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.spring.SpringContext;

/**
 * @author Tomsky
 *
 */
public class WebscraperTest extends TestBase{
	private WebscraperService service = (WebscraperService) SpringContext
    .getInstance().getBean("scraperService");
	/**
	 * @param args
	 */
	public void testScrapFiles() {
		service.scraperFiles();
	}

}
