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
#import "Sensor.h"
#import "Label.h"

@implementation Image

- (id)initWithId:(int)anId src:(NSString *)srcValue style:(NSString *)styleValue
{
    self = [super init];
    if (self) {
        self.componentId = anId;
        self.src = srcValue;
        self.style = styleValue;
    }
    return self;
}

- (void)dealloc
{
	self.src = nil;
    self.style = nil;
    self.label = nil;
	[super dealloc];
}

- (int)sensorId
{
    int sid = self.sensor.sensorId;
    return (sid > 0)?sid:self.label.sensorId;
}

@synthesize src, style, label;

- (void)setSrc:(NSString *)imgSrc
{
    if (imgSrc != src) {
        [src release];
        src = [imgSrc retain];
        // TODO EBR review, if we don't need that, we can use the synthesized setter, no need for this
        //        [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition addImageName:src];
    }
}

@end