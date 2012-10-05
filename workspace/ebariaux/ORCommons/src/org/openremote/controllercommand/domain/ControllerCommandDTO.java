package org.openremote.controllercommand.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ControllerCommandDTO implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 6492038876605245825L;
  
  /**
   * The type of command. This is required because not every command needs a subclass
   * from which we can infer the type from.
   */
  public enum Type {
     UPLOAD_LOGS, UPDATE_CONTROLLER, INITIATE_PROXY, DOWNLOAD_DESIGN, UNLINK_CONTROLLER;

     /**
      * Gets a label for this type, this is useful for exporting. Label format is
      * name().replace("_", "-").toLowerCase().
      */
      public String getLabel() {
         return name().replace("_", "-").toLowerCase();
      }
      
      @Override
      public String toString() {
        return getLabel();
      }

      /**
       * Gets a type from a label, this is useful for importing. Label format is
       * name().replace("_", "-").toLowerCase().
       */
      public static Type fromLabel(String label) {
         return valueOf(label.replace("-", "_").toUpperCase());
      }
  }
  
  /** The id */
  private Long oid;
  
  /** The type of this command as string */
  private String commandType;
  
  /** The type of this command as enum */
  private Type commandTypeEnum;
  
  /** The command specific parameter which are needed to execute the command */
  private Map<String, String> commandParameter;
  
  public ControllerCommandDTO()
  {
    this.commandParameter = new HashMap<String, String>();
  }

  public void addParameter(String paramName, String paramValue) {
    this.commandParameter.put(paramName, paramValue);
  }

  public String getCommandType()
  {
    return commandType;
  }

  public void setCommandType(String commandType)
  {
    this.commandType = commandType;
    this.commandTypeEnum = Type.fromLabel(commandType);
  }

  public Map<String, String> getCommandParameter()
  {
    return commandParameter;
  }

  public void setCommandParameter(Map<String, String> commandParameter)
  {
    this.commandParameter = commandParameter;
  }

  public Type getCommandTypeEnum()
  {
    return commandTypeEnum;
  }

  public void setCommandTypeEnum(Type commandTypeEnum)
  {
    this.commandTypeEnum = commandTypeEnum;
  }

  public Long getOid()
  {
    return oid;
  }

  public void setOid(Long oid)
  {
    this.oid = oid;
  }
  
  
  
}
