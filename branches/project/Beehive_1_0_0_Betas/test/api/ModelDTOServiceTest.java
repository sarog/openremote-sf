package api;

import junit.framework.TestCase;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.spring.SpringContext;

/**
 * @author allen.wei 2009-2-17
 */
public class ModelDTOServiceTest extends TestCase {

    private ModelService service = (ModelService) SpringContext
            .getInstance().getBean("modelService");

    public void testExportTest() {
        System.out.println(service.exportText(1));
    }
}
