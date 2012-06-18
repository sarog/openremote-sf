
package org.openremote.controller.protocol.enocean.datatype;

import java.math.BigDecimal;

public abstract class AbstractScale implements Scale
{
  @Override public abstract BigDecimal scale(byte[] rawData, int scale);
}
