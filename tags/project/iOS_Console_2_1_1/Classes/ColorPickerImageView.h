//
//  ColorPickerImageView.h
//  ColorPicker
//
//  ColorPicker is free and can be used in anything without restriction. 
//  Feel free to copy, redistribute, sell as you see fit in source code or compiled form.
//
//  Created by markj on 3/6/09.
//  Copyright 2009 Mark Johnson. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol PickedColorDelegate
	- (void) pickedColor:(UIColor*)color;
@end

@interface ColorPickerImageView : UIImageView {
	UIColor* lastColor;
	id pickedColorDelegate;
}

@property (nonatomic, retain) UIColor* lastColor;
@property (nonatomic, retain) id pickedColorDelegate;


- (UIColor*) getPixelColorAtLocation:(CGPoint)point;
- (CGContextRef) createARGBBitmapContextFromImage:(CGImageRef)inImage;


@end
