package org.openremote.controller.protocol.lutron;

import org.junit.Assert;
import org.junit.Test;

public class LutronHomeWorksAddressTest {

	@Test
	public void testAddressFormattingBasic() throws InvalidLutronHomeWorksAddressException {

		Assert.assertEquals("[01:06:01:03:02]", new LutronHomeWorksAddress("[1:6:1:3:2]").toString());
		Assert.assertEquals("[01:04:01:02]", new LutronHomeWorksAddress("1:4:1:2").toString());
		
	}
}
