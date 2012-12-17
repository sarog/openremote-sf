//
//  ORSlider.h
//  openremote
//
//  Created by Eric Bariaux on 31/10/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

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