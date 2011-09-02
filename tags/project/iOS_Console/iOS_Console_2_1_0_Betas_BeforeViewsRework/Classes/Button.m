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
#import "Button.h" 

@implementation Button

@synthesize defaultImage, pressedImage, name, navigate;
@synthesize repeat, repeatDelay, hasPressCommand, hasShortReleaseCommand, hasLongPressCommand, hasLongReleaseCommand, longPressDelay;

- (id)initWithId:(int)anId name:(NSString *)aName repeat:(BOOL)repeatFlag repeatDelay:(int)aRepeatDelay hasPressCommand:(BOOL)hasPressCommandFlag hasShortReleaseCommand:(BOOL)hasShortReleaseCommandFlag hasLongPressCommand:(BOOL)hasLongPressCommandFlag hasLongReleaseCommand:(BOOL)hasLongReleaseCommandFlag longPressDelay:(int)aLongPressDelay
{
    self = [super init];
	if (self) {
        componentId = anId;
        name = [aName copy];
        repeat = repeatFlag;
        repeatDelay = MAX(100, aRepeatDelay);
        hasPressCommand = hasPressCommandFlag;
        hasShortReleaseCommand = hasShortReleaseCommandFlag;
        hasLongPressCommand = hasLongPressCommandFlag;
        hasLongReleaseCommand = hasLongReleaseCommandFlag;
        longPressDelay = MAX(250, aLongPressDelay);
        if (hasLongPressCommand || hasLongReleaseCommand) {
            repeat = NO;
        }
    }
    return self;
}

- (void)dealloc
{
	[defaultImage release];
	[pressedImage release];
	[navigate release];
	[name release];
	[super dealloc];
}

@end