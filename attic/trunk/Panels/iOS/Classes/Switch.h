/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "SensorComponent.h"
#import "Image.h"

/**
 * Stores model data about switch parsed from "swith" element in panel.xml.
 * XML fragment example:
 * <switch id="60" >
 *    <link type="sensor" ref="60">
 *       <state name="on" value="c.png" />
 *       <state name="off" value="d.png" />
 *    </link>
 * </switch>
 */
@interface Switch : SensorComponent <NSXMLParserDelegate> {

	Image *onImage;
	Image *offImage;

}

@property (nonatomic,readonly)Image *onImage; 
@property (nonatomic,readonly)Image *offImage; 


@end
