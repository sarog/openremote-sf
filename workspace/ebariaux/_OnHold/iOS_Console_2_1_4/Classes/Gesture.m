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
#import "Gesture.h"

@interface Gesture ()

@property (nonatomic, readwrite) GestureSwipeType swipeType;
@property (nonatomic, readwrite) BOOL hasControlCommand;

@end

@implementation Gesture

- (id)initWithId:(int)anId swipeType:(GestureSwipeType)type hasControlCommand:(BOOL)hasControlCommandFlag
{
    self = [self initWithGestureSwipeType:type];
    if (self) {
        self.componentId = anId;
        self.hasControlCommand = hasControlCommandFlag;
    }
    return self;
}

- (id)initWithGestureSwipeType:(GestureSwipeType)type {
	if (self = [super init]) {
		self.swipeType = type;
	}
	return self;
}

- (id)initWithGestureSwipeType:(GestureSwipeType)type orientation:(UIInterfaceOrientation)orientation{
	if (self = [super init]) {
		switch (orientation) {
			case UIInterfaceOrientationPortrait:
				self.swipeType = type;
				break;
			case UIInterfaceOrientationLandscapeLeft:
				self.swipeType = (type + 1) % 4;
				break;
			case UIInterfaceOrientationLandscapeRight:
				self.swipeType = (type - 1 + 4)  % 4;
				break;
			case UIInterfaceOrientationPortraitUpsideDown:
				self.swipeType = (type + 2) % 4;
				break;	
			default:
				self.swipeType = type;
				break;
		}		
	}
	return self;
}

- (NSString *)toString {
	switch (self.swipeType) {
		case GestureSwipeTypeTopToBottom:
			return @"top to bottom";
		case GestureSwipeTypeBottomToTop:
			return @"bottom to top";
		case GestureSwipeTypeLeftToRight:
			return @"left to right";
		case GestureSwipeTypeRightToLeft:
			return @"right to left";
	}
	return nil;
}

@synthesize swipeType, hasControlCommand, navigate;

@end