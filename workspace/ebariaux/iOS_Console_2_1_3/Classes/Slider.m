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
#import "Slider.h"
#import "Image.h"

@interface Slider ()

@property(nonatomic, retain, readwrite) Image *thumbImage;
@property(nonatomic, readwrite) BOOL vertical;
@property(nonatomic, readwrite) BOOL passive;

@end

@implementation Slider

- (id)initWithId:(int)anId vertical:(BOOL)verticalFlag passive:(BOOL)passiveFlag thumbImageSrc:(NSString *)thumbImageSrc
{
    self = [super init];
    if (self) {
        self.componentId = anId;
        self.vertical = verticalFlag;
        self.passive = passiveFlag;
        Image *tmp = [[Image alloc] init];
        self.thumbImage = tmp;
        [tmp release];
		self.thumbImage.src = thumbImageSrc;
		// Set default values for bounds, in case they're not provided in panel.xml
		self.minValue = 0.0;
		self.maxValue = 100.0;
    }
    return self;
}

- (void)dealloc
{
    self.thumbImage = nil;
    self.minImage = nil;
    self.minTrackImage = nil;
    self.maxImage = nil;
    self.maxTrackImage = nil;
	[super dealloc];
}

@synthesize thumbImage, vertical, passive, minValue, maxValue, minImage, minTrackImage, maxImage, maxTrackImage;

@end