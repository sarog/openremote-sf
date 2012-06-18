package org.openremote.controller.protocol.enocean.datatype;

import java.math.BigDecimal;

public interface Scale
{
  BigDecimal scale(byte[] rawData, int scale);
}
