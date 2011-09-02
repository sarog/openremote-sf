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
#import "Image.h"
#import "Definition.h"
#import "SensorState.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"

@implementation Image

@synthesize src, style, label;

- (id)initWithId:(int)anId src:(NSString *)srcValue style:(NSString *)styleValue
{
    self = [super init];
    if (self) {
        componentId = anId;
        src = [srcValue copy];
        style = [styleValue copy];
    }
    return self;
}

- (void)setSrc:(NSString *)imgSrc {
    if (imgSrc != src) {
        [src release];
        src = [imgSrc retain];
        // TODO EBR review
//        [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition addImageName:src];
    }
}

- (void)dealloc {
	[src release];
	[style release];
	[label release];
	[super dealloc];
}

@end
