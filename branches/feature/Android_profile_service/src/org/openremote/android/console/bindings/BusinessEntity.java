package org.openremote.android.console.bindings;

import java.io.Serializable;

import org.openremote.android.console.Constants;
import org.w3c.dom.Node;

public abstract class BusinessEntity implements Serializable {
   private static final long serialVersionUID = Constants.BINDING_VERSION;

   public abstract String getElementName();
   
   public abstract void initWithXML(Node node);
   
}
