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
#import "SensorComponent.h"

@class Image;

@interface Slider : SensorComponent

@property(nonatomic, retain, readonly) Image *thumbImage;
@property(nonatomic, readonly) BOOL vertical;
@property(nonatomic, readonly) BOOL passive;
@property(nonatomic, assign) float minValue;
@property(nonatomic, assign) float maxValue;
@property(nonatomic, retain) Image *minImage;
@property(nonatomic, retain) Image *minTrackImage;
@property(nonatomic, retain) Image *maxImage;
@property(nonatomic, retain) Image *maxTrackImage;

- (id)initWithId:(int)anId vertical:(BOOL)verticalFlag passive:(BOOL)passiveFlag thumbImageSrc:(NSString *)thumbImageSrc;

@end
