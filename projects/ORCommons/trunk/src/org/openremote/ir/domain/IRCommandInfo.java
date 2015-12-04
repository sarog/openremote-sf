/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * allows to exchange xcfFileParser.IRCommand necessary information with the client side
 * @author wbalcaen
 *
 */
public class IRCommandInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String code;
  private String originalCode;
  private String comment;
  private CodeSetInfo codeSet;

  public IRCommandInfo() {
  }

  public IRCommandInfo(String name, String code, String originalCode, String comment, CodeSetInfo codeSet) {
    setName(name);
    setCode(code);
    setOriginalCode(originalCode);
    setComment(comment);
    setCodeSet(codeSet);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getOriginalCode() {
    return originalCode;
  }

  public void setOriginalCode(String originalCode) {
    this.originalCode = originalCode;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public CodeSetInfo getCodeSet() {
    return codeSet;
  }

  public void setCodeSet(CodeSetInfo codeSet) {
    this.codeSet = codeSet;
  }

}
