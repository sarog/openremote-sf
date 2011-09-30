//
//  UIDevice+ORAdditions.m
//  openremote
//
//  Created by Eric Bariaux on 30/09/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "UIDevice+ORAdditions.h"

@implementation UIDevice (UIDevice_ORAdditions)

+ (BOOL)or_isDeviceOrientationLandscape:(UIDeviceOrientation)orientation
{
    return (orientation != UIDeviceOrientationUnknown && orientation != UIDeviceOrientationPortrait && orientation != UIDeviceOrientationPortraitUpsideDown);
}

+ (BOOL)or_isDeviceOrientationLandscape
{
    return [self or_isDeviceOrientationLandscape:[[UIDevice currentDevice] orientation]];
}

@end
