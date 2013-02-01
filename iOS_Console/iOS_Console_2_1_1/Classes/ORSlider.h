/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

#import <UIKit/UIKit.h>

@interface ORSlider : UIControl {
    @package
    float _value;
    float _minValue;
    float _maxValue;
    
}

@property (nonatomic) float value;                                 // default 0.0. this value will be pinned to min/max
@property (nonatomic) float minimumValue;                          // default 0.0. the current value may change if outside new min value
@property (nonatomic) float maximumValue;                          // default 1.0. the current value may change if outside new max value

@property (nonatomic, retain) UIImage *minimumValueImage;          // default is nil. image that appears to left of control (e.g. speaker off)
@property (nonatomic, retain) UIImage *maximumValueImage;          // default is nil. image that appears to right of control (e.g. speaker max)

@property (nonatomic, retain) UIImage *minimumTrackImage;
@property (nonatomic, retain) UIImage *maximumTrackImage;

@property (nonatomic, retain) UIImage *thumbImage;

@property(nonatomic,getter=isContinuous) BOOL continuous;        // if set, value change events are generated any time the value changes due to dragging. default = YES

@end