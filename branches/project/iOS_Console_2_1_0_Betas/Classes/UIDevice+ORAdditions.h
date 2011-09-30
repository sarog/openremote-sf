//
//  UIDevice+ORAdditions.h
//  openremote
//
//  Created by Eric Bariaux on 30/09/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIDevice (UIDevice_ORAdditions)

+ (BOOL)or_isDeviceOrientationLandscape:(UIDeviceOrientation)orientation;
+ (BOOL)or_isDeviceOrientationLandscape;

@end
