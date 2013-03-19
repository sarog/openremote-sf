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
#import "LocalSensor.h"

@interface LocalSensor ()

@property (nonatomic, copy, readwrite) NSString *className;
@property (nonatomic, copy, readwrite) NSString *methodName;
@property (nonatomic, readwrite) NSUInteger refreshRate;

@end

@implementation LocalSensor

@synthesize className, methodName, refreshRate;

- (id)initWithId:(int)anId className:(NSString *)aClassName methodName:(NSString *)aMethodName refreshRate:(NSNumber *)aRefreshRate
{
    self = [super init];
    if (self) {
        self.componentId = anId;
        self.className = aClassName;
        self.methodName = aMethodName;
        self.refreshRate  = (aRefreshRate?[aRefreshRate intValue]:5000); // Default to 5 sec
    }
    return self;
}

- (void)dealloc
{
    self.className = nil;
    self.methodName = nil;
	[super dealloc];
}

@end
