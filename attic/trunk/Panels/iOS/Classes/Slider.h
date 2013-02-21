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
#import <Foundation/Foundation.h>
#import "SensorComponent.h"
#import "Image.h"

/**
 * Stores model data about slider parsed from "slider" element in panel.xml.
 * XML fragment example:
 * <slider id="60" thumbImage="thumbImage.png">
 *    <min value="0" image="mute.png" trackImage="red.png"/>
 *    <max value="100" image="loud.png" trackImage="green.png"/>
 *    <link type="sensor" ref="60" />
 * </slider>
 */
@interface Slider : SensorComponent <NSXMLParserDelegate> {
	Image *thumbImage;
	BOOL vertical;
	BOOL passive;
	float minValue;
	float maxValue;
	Image *minImage;
	Image *minTrackImage;
	Image *maxImage;
	Image *maxTrackImage;
}

@property(nonatomic, readonly) Image *thumbImage;
@property(nonatomic, readonly) BOOL vertical;
@property(nonatomic, readonly) BOOL passive;
@property(nonatomic, readonly) float minValue;
@property(nonatomic, readonly) float maxValue;
@property(nonatomic, readonly) Image *minImage;
@property(nonatomic, readonly) Image *minTrackImage;
@property(nonatomic, readonly) Image *maxImage;
@property(nonatomic, readonly) Image *maxTrackImage;

@end
