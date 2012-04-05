package org.openremote.modeler.irfileparser;

import org.openremote.ir.domain.CodeSetInfo;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * allows to exchange xcfFileParser.IRCommand necessary information with the client side
 * @author wbalcaen
 *
 */
public class IRCommandInfo extends org.openremote.ir.domain.IRCommandInfo implements BeanModelTag {

   private static final long serialVersionUID = 1L;

  public IRCommandInfo() {
    super();
  }

  public IRCommandInfo(String name, String code, String originalCode, String comment, CodeSetInfo codeSet) {
    super(name, code, originalCode, comment, codeSet);
  }

}
