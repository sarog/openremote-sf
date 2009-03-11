/**
 * 
 */
package org.openremote.beehive.serviceHibernateImpl;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.domain.Vendor;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;

/**
 * @author Tomsky
 *
 */
public class WebscraperServiceImpl extends BaseAbstractService<Vendor> implements
		WebscraperService {

	private static Logger logger = Logger.getLogger(WebscraperServiceImpl.class.getName());
	private Configuration configuration;
	

	public Configuration getConfiguration() {
		return configuration;
	}


	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}


	public void scraperFiles() {
		try {			
            ScraperConfiguration config =
                    new ScraperConfiguration(getClass().getResource("/remotes.xml").getPath());
            Scraper scraper = new Scraper(config, configuration.getScrapDir());
            scraper.setDebug(true);            
//            long startTime = System.currentTimeMillis();
            scraper.execute();
//            System.out.println("time elapsed:"+ (System.currentTimeMillis() - startTime));
        } catch (FileNotFoundException e) {
        	logger.error(getClass().getResource("/remotes.xml").getPath()+" not found");
        }
	}
}
