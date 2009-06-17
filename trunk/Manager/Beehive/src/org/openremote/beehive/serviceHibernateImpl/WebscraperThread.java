/**
 * 
 */
package org.openremote.beehive.serviceHibernateImpl;

import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.spring.SpringContext;

/**
 * @author Tomsky
 *
 */
public class WebscraperThread implements Runnable {
   
   private WebscraperService scraperService = (WebscraperService) SpringContext.getInstance().getBean("scraperService");;
   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   public void run() {
       scraperService.scrapeFiles();
   }

}
