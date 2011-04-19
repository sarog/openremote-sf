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
  private String item;

  public StateDefinition(PanelState.StateType type, String item)
  {
    this.type = type;
    this.item = item;
  }

  public PanelState.StateType getType()
  {
    return type;
  }

  public String getItem()
  {
    return item;
  }

  @Override
  public String toString()
  {
    return "[StateDefinition: type=" + type + ", item=" + item + "]";
  }
}
