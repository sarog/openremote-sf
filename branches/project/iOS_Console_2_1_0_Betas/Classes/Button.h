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
#import "Control.h"
#import "Navigate.h"
#import "Image.h"

/**
 * Button stores informations parsed from button element in panel.xml.
 * XML fragment example:
 * <button id="59" name="A" repeat="false" hasControlCommand="false">
 *    <default>
 *       <image src="a.png" />
 *    </default>
 *    <pressed>
 *       <image src="b.png" />
 *    </pressed>
 *    <navigate toScreen="19" />
 * </button>
 */
@interface Button : Control <NSXMLParserDelegate> {
	
	NSString *name;
	Image *defaultImage;
	Image *pressedImage;
	BOOL repeat;
    NSUInteger repeatDelay;
    BOOL hasPressCommand;
    BOOL hasShortReleaseCommand;
    BOOL hasLongPressCommand;
    BOOL hasLongReleaseCommand;
    NSUInteger longPressDelay;
	Navigate *navigate;
	
	NSString *subElememntNameOfBackground;

}

@property (nonatomic, readonly) NSString *name;
@property (nonatomic, readonly) Image *defaultImage;
@property (nonatomic, readonly) Image *pressedImage;
@property (nonatomic, readonly) BOOL repeat;
@property (nonatomic, readonly) NSUInteger repeatDelay;
@property (nonatomic, readonly) BOOL hasPressCommand;
@property (nonatomic, readonly) BOOL hasShortReleaseCommand;
@property (nonatomic, readonly) BOOL hasLongPressCommand;
@property (nonatomic, readonly) BOOL hasLongReleaseCommand;
@property (nonatomic, readonly) NSUInteger longPressDelay;
@property (nonatomic, readonly) Navigate *navigate;
@property (nonatomic, readonly) NSString *subElememntNameOfBackground;

@end
