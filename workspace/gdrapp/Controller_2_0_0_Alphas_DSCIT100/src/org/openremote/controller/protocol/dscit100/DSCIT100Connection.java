/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

/**
 * @author Greg Rapp
 * 
 */
public interface DSCIT100Connection
{

  public void send(ExecuteCommand command);

  public void send(Packet packet);

  public boolean isConnected();

  public void close();

  public PanelState.State getState(StateDefinition stateDefinition);

  public String getAddress();
}
