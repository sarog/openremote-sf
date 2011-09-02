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
#import "LocalTask.h"

@implementation LocalTask

@synthesize className, methodName, frequency;

- (id)initWithId:(int)anId className:(NSString *)aClassName methodName:(NSString *)aMethodName frequency:(NSNumber *)aFrequency
{
    self = [super init];
    if (self) {
        componentId = anId;
        className = [aClassName retain];
        methodName = [aMethodName retain];
        frequency  = (aFrequency?[aFrequency intValue]:5000); // Default to 5 sec
    }
    return self;
}

- (void)dealloc {
	[className release];
	[methodName release];
	[super dealloc];
}

@end
