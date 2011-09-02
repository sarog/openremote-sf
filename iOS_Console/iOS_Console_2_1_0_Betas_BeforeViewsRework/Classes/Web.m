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
#import "Web.h"

/**
 * The Web class represents an element displaying web content on the panel. 
 */
@implementation Web

@synthesize src, username, password;

- (id)initWithId:(int)anId src:(NSString *)aSrc username:(NSString *)aUsername password:(NSString *)aPassword
{
    self = [super init];
    if (self) {
        componentId = anId;
        src = [aSrc copy];
        username = [aUsername copy];
        password = [aPassword copy];
    }
    return self;
}

- (void)dealloc {
	[src release];
	[username release];
	[password release];
    [super dealloc];
}

@end
