package org.openremote.controller.service.impl;

import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerService;

public class DeployerServiceImpl implements DeployerService
{

  private Deployer deployer;

  public DeployerServiceImpl(Deployer deployer)
  {
    this.deployer = deployer;
  }

  @Override public void deploy()
  {
    try
    {
      deployer.softRestart();
    }

    catch (ControllerDefinitionNotFoundException e)
    {

    }
  }
}
