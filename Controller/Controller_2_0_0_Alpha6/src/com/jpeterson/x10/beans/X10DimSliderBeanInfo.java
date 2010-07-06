/*
 * Copyright (C) 1999  Jesse E. Peterson
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *
 */

package com.jpeterson.x10.beans;

import java.beans.*;

public class X10DimSliderBeanInfo extends SimpleBeanInfo
{
    // return the property descriptors
    public PropertyDescriptor[] getPropertyDescriptors()
    {
	try {
	    // create a descriptor for the deviceCode property
	    PropertyDescriptor pd1 = new PropertyDescriptor("deviceCode",
							    X10DimSlider.class);

	    // create a descriptor for the houseCode property
	    PropertyDescriptor pd2 = new PropertyDescriptor("houseCode",
							    X10DimSlider.class);

	    // specify the property editor for houseCode
	    pd2.setPropertyEditorClass(HouseCodeEditor.class);

	    // create an array of descriptors and return it to the caller
	    PropertyDescriptor[] pda = { pd1, pd2 };

	    return(pda);
	}
	catch (Exception e) {
	    return(null);
	}
    }
}
