package org.openremote.controller.protocol.domintell;

import org.openremote.controller.protocol.domintell.model.DomintellModule;

@SuppressWarnings("serial")
public class DomintellModuleException extends Exception {

   private String moduleType;
   private DomintellAddress address;
   private Class<? extends DomintellModule> moduleClass;
   
   public DomintellModuleException(String message, String moduleType, DomintellAddress address, Class<? extends DomintellModule> moduleClass, Throwable cause) {
      super(message, cause);
      this.moduleType = moduleType;
      this.address = address;
      this.moduleClass = moduleClass;
   }

   public String getModuleType() {
      return moduleType;
   }

   public DomintellAddress getAddress() {
      return address;
   }

   public Class<? extends DomintellModule> getModuleClass() {
      return moduleClass;
   }
   
}
