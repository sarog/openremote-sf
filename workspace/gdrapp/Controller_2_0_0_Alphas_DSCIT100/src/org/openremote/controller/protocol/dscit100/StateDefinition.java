/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

/**
 * @author Greg Rapp
 * 
 */
public class StateDefinition
{
  private PanelState.StateType type;
  private String target;

  public StateDefinition(PanelState.StateType type, String target)
  {
    this.type = type;
    this.target = target;
  }

  public PanelState.StateType getType()
  {
    return type;
  }

  public String getTarget()
  {
    return target;
  }

  @Override
  public String toString()
  {
    return "[StateDefinition: type=" + type + ", target=" + target + "]";
  }
}
