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
#import "XMLEntity.h"
#import "Image.h"

/**
 * Background stores informations parsed from background element in panel.xml.
 * XML fragment example:
 * <background fillScreen="true">
 *    <image src="living_colors_320.png" />
 * </background>
 */
@interface Background : XMLEntity <NSXMLParserDelegate> {
	int backgroundImageAbsolutePositionLeft;
	int backgroundImageAbsolutePositionTop;
	BOOL isBackgroundImageAbsolutePosition;
	NSString *backgroundImageRelativePosition;
	BOOL fillScreen;
	Image *backgroundImage;
}

@property(nonatomic, readwrite) int backgroundImageAbsolutePositionLeft;
@property(nonatomic, readwrite) int backgroundImageAbsolutePositionTop;
@property(nonatomic, readwrite) BOOL isBackgroundImageAbsolutePosition;
@property(nonatomic, retain) NSString *backgroundImageRelativePosition;
@property(nonatomic, readwrite) BOOL fillScreen;
@property(nonatomic, retain) Image *backgroundImage;

@end
