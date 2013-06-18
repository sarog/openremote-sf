/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.IDUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ImageSourceTest {

  @Test
  public void testImageSourcesEqual() {
    ImageSource imageSource1 = new ImageSource("Image");
    imageSource1.setOid(IDUtil.nextID());
    
    ImageSource imageSource2 = new ImageSource("Image");
    imageSource2.setOid(IDUtil.nextID());
    
    Assert.assertEquals(imageSource1, imageSource2, "Expected the ImageSources to be equal");

    // ImageSources with different ids are considered to be equal if sources are equal
    imageSource2.setOid(IDUtil.nextID());
    Assert.assertEquals(imageSource1, imageSource2, "Expected the ImageSources to be equal");
  }
  
  @Test
  public void testImageSourcesNotEquals() {
    ImageSource imageSource1 = new ImageSource("Image");
    imageSource1.setOid(IDUtil.nextID());
    
    ImageSource imageSource2 = new ImageSource("Image");
    imageSource2.setOid(IDUtil.nextID());
    
    Assert.assertEquals(imageSource1, imageSource2, "Expected the ImageSources to be equal");
    
    imageSource2.setSrc("Image 2");
    Assert.assertFalse(imageSource1.equals(imageSource2), "Expected the ImageSources to be different, src is different");
  }

}
