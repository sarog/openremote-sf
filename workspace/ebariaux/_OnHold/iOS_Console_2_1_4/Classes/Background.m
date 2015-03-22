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
#import "Background.h"
#import "XMLEntity.h"

@implementation Background

- (id)initWithRelativePosition:(NSString *)relativePositionString fillScreen:(BOOL)fillScreenTag
{
    self = [super init];
    if (self) {
        self.backgroundImageRelativePosition = relativePositionString;
        self.isBackgroundImageAbsolutePosition = NO;
        self.fillScreen = fillScreenTag;
    }
    return self;
}

- (id)initWithAbsolutePositionLeft:(int)leftPos top:(int)topPos fillScreen:(BOOL)fillScreenTag
{
    self = [super init];
    if (self) {
        self.backgroundImageAbsolutePositionLeft = leftPos;
        self.backgroundImageAbsolutePositionTop = topPos;
        self.isBackgroundImageAbsolutePosition = YES;
        self.fillScreen = fillScreenTag;
    }
    return self;
}

#pragma mark constructor
#pragma mark dealloc

-(void) dealloc {
	self.backgroundImageRelativePosition = nil;
	self.backgroundImage = nil;
	[super dealloc];
}

@synthesize backgroundImageAbsolutePositionLeft, backgroundImageAbsolutePositionTop, isBackgroundImageAbsolutePosition, backgroundImageRelativePosition, fillScreen, backgroundImage;

@end