/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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

@interface Button ()

@property (nonatomic, copy, readwrite) NSString *name;
@property (nonatomic, readwrite) BOOL repeat;
@property (nonatomic, readwrite) NSUInteger repeatDelay;
@property (nonatomic, readwrite) BOOL hasPressCommand;
@property (nonatomic, readwrite) BOOL hasShortReleaseCommand;
@property (nonatomic, readwrite) BOOL hasLongPressCommand;
@property (nonatomic, readwrite) BOOL hasLongReleaseCommand;
@property (nonatomic, readwrite) NSUInteger longPressDelay;

@end

@implementation Button

- (id)initWithId:(int)anId name:(NSString *)aName repeat:(BOOL)repeatFlag repeatDelay:(int)aRepeatDelay hasPressCommand:(BOOL)hasPressCommandFlag hasShortReleaseCommand:(BOOL)hasShortReleaseCommandFlag hasLongPressCommand:(BOOL)hasLongPressCommandFlag hasLongReleaseCommand:(BOOL)hasLongReleaseCommandFlag longPressDelay:(int)aLongPressDelay
{
    self = [super init];
	if (self) {
        self.componentId = anId;
        self.name = aName;
        self.repeat = repeatFlag;
        self.repeatDelay = MAX(100, aRepeatDelay);
        self.hasPressCommand = hasPressCommandFlag;
        self.hasShortReleaseCommand = hasShortReleaseCommandFlag;
        self.hasLongPressCommand = hasLongPressCommandFlag;
        self.hasLongReleaseCommand = hasLongReleaseCommandFlag;
        self.longPressDelay = MAX(250, aLongPressDelay);
        if (self.hasLongPressCommand || self.hasLongReleaseCommand) {
            self.repeat = NO;
        }
    }
    return self;
}

- (void)dealloc
{
	self.defaultImage = nil;
    self.pressedImage = nil;
    self.navigate = nil;
    self.name = nil;
	[super dealloc];
}

@synthesize defaultImage, pressedImage, name, navigate;
@synthesize repeat, repeatDelay, hasPressCommand, hasShortReleaseCommand, hasLongPressCommand, hasLongReleaseCommand, longPressDelay;

@end