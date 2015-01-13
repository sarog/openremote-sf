package org.openremote.controller.rest;

import org.openremote.controller.service.DeployerService;
import org.openremote.controller.spring.SpringContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeployerRESTServlet extends RESTAPI
{

  private static DeployerService statusCommandService = (DeployerService) SpringContext.getInstance().getBean("deployerService");


  @Override protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
  {
    statusCommandService.deploy();
  }
}
