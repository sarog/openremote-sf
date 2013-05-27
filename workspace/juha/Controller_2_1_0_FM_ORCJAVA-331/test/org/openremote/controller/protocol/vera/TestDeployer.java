package org.openremote.controller.protocol.vera;

import java.util.List;
import java.util.Map;

import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.deployer.ModelBuilder;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.BeehiveCommandCheckService;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;

public class TestDeployer extends Deployer {

   public TestDeployer(String serviceName, StatusCache deviceStateCache, ControllerConfiguration controllerConfig,
         BeehiveCommandCheckService beehiveCommandCheckService, Map<String, ModelBuilder> builders)
         throws InitializationException {
      
      super(serviceName, deviceStateCache, controllerConfig, beehiveCommandCheckService, builders);
   }

   public List<DiscoveredDeviceDTO> getDiscoveredDevicesToAnnounce() {
      return super.discoveredDevicesToAnnounce;
   }
}
