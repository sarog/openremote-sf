//
//  ColorPickerImageView.m
//  ColorPicker
//
//  ColorPicker is free and can be used in anything without restriction. 
//  Feel free to copy, redistribute, sell as you see fit in source code or compiled form.
//
//  Created by markj on 3/6/09.
//  Copyright 2009 Mark Johnson. All rights reserved.
//

#import "ColorPickerImageView.h"
#import <CoreGraphics/CoreGraphics.h>
#import <QuartzCore/CoreAnimation.h>

#define MIN_VALID_MOVE_DISTANCE 2

@interface ColorPickerImageView()

@property (nonatomic, assign) CGPoint touchBeginPoint;
@property (nonatomic, assign) BOOL movingTag;

- (void)pickColorWithTouches:(NSSet *)touches andEvent:(UIEvent*)event;
- (CGFloat)distanceBetweenTwoPoints:(CGPoint)fromPoint toPoint:(CGPoint)toPoint;

@end

@implementation ColorPickerImageView

- (id)initWithImage:(UIImage *)image
{
    self = [super initWithImage:image];
    if (self) {
        self.userInteractionEnabled = YES;
    }
    return self;
}

- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"ColorPikcerView began event......");
//	[self enableScrollView:NO];
	
	UITouch* touch = [touches anyObject];
	self.touchBeginPoint = [touch previousLocationInView:self];
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"ColorPikcerView moving event......");
	
	self.movingTag = YES;
	UITouch* touch = [touches anyObject];
	CGPoint currentPoint = [touch locationInView:self];
	CGFloat distance = [self distanceBetweenTwoPoints:self.touchBeginPoint toPoint:currentPoint];
	if (distance > MIN_VALID_MOVE_DISTANCE) {
		[self pickColorWithTouches:touches andEvent:event];
		self.touchBeginPoint = currentPoint;
		NSLog(@"The distance %f of moving is greater than %d, so command will be sent.", distance, MIN_VALID_MOVE_DISTANCE);
	} else {
		NSLog(@"The distance %f of moving is less than %d, so command won't be sent.", distance, MIN_VALID_MOVE_DISTANCE);
	}
}

- (void) touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event {
	NSLog(@"ColorPikcerView end event......");
	if (self.movingTag == NO) {
		[self pickColorWithTouches:touches andEvent:event];
	}
	self.movingTag = NO;
//	[self enableScrollView:YES];
}

- (CGFloat)distanceBetweenTwoPoints:(CGPoint)fromPoint toPoint:(CGPoint)toPoint {
	float x = toPoint.x - fromPoint.x;
	float y = toPoint.y - fromPoint.y;
	return sqrt(x * x + y * y);
}

- (void) pickColorWithTouches:(NSSet *)touches andEvent:(UIEvent*)event {
	UITouch* touch = [touches anyObject];
	CGPoint point = [touch locationInView:self]; //where image was tapped
	if ([self pointInside: point withEvent: event]) {
		self.lastColor = [self getPixelColorAtLocation:point]; 
		NSLog(@"color %@",lastColor);
		[pickedColorDelegate pickedColor:(UIColor*)self.lastColor];
	}
}

- (UIColor*) getPixelColorAtLocation:(CGPoint)point {
	UIColor* color = nil;
	CGImageRef inImage = self.image.CGImage;
	// Create off screen bitmap context to draw the image into. Format ARGB is 4 bytes for each pixel: Alpa, Red, Green, Blue
	CGContextRef cgctx = [self createARGBBitmapContextFromImage:inImage];
	if (cgctx == NULL) { return nil; /* error */ }
	
    size_t w = CGImageGetWidth(inImage);
	size_t h = CGImageGetHeight(inImage);
	CGRect rect = {{0,0},{w,h}}; 
	
	// Draw the image to the bitmap context. Once we draw, the memory 
	// allocated for the context for rendering will then contain the 
	// raw image data in the specified color space.
	CGContextDrawImage(cgctx, rect, inImage); 
	
	// Now we can get a pointer to the image data associated with the bitmap
	// context.
	unsigned char* data = CGBitmapContextGetData (cgctx);
	if (data != NULL) {
		//offset locates the pixel in the data from x,y. 
		//4 for 4 bytes of data per pixel, w is width of one row of data.
		int offset = 4*((w*round(point.y))+round(point.x));
		int alpha =  data[offset]; 
		int red = data[offset+1]; 
		int green = data[offset+2]; 
		int blue = data[offset+3]; 
		NSLog(@"offset: %i colors: RGB A %i %i %i  %i",offset,red,green,blue,alpha);
		color = [UIColor colorWithRed:(red/255.0f) green:(green/255.0f) blue:(blue/255.0f) alpha:(alpha/255.0f)];
	}
	
	// When finished, release the context
	CGContextRelease(cgctx);
	// Free image data memory for the context
	if (data) { free(data); }
	
	return color;
}

- (CGContextRef) createARGBBitmapContextFromImage:(CGImageRef) inImage {
	
	CGContextRef    context = NULL;
	CGColorSpaceRef colorSpace;
	void *          bitmapData;
	int             bitmapByteCount;
	int             bitmapBytesPerRow;
	
	// Get image width, height. We'll use the entire image.
	size_t pixelsWide = CGImageGetWidth(inImage);
	size_t pixelsHigh = CGImageGetHeight(inImage);
	
	// Declare the number of bytes per row. Each pixel in the bitmap in this
	// example is represented by 4 bytes; 8 bits each of red, green, blue, and
	// alpha.
	bitmapBytesPerRow   = (pixelsWide * 4);
	bitmapByteCount     = (bitmapBytesPerRow * pixelsHigh);
	
	// Use the generic RGB color space.
	colorSpace = CGColorSpaceCreateDeviceRGB();

	if (colorSpace == NULL)
	{
		fprintf(stderr, "Error allocating color space\n");
		return NULL;
	}
	
	// Allocate memory for image data. This is the destination in memory
	// where any drawing to the bitmap context will be rendered.
	bitmapData = malloc( bitmapByteCount );
	if (bitmapData == NULL) 
	{
		fprintf (stderr, "Memory not allocated!");
		CGColorSpaceRelease( colorSpace );
		return NULL;
	}
	
	// Create the bitmap context. We want pre-multiplied ARGB, 8-bits 
	// per component. Regardless of what the source image format is 
	// (CMYK, Grayscale, and so on) it will be converted over to the format
	// specified here by CGBitmapContextCreate.
	context = CGBitmapContextCreate (bitmapData,
									 pixelsWide,
									 pixelsHigh,
									 8,      // bits per component
									 bitmapBytesPerRow,
									 colorSpace,
									 kCGImageAlphaPremultipliedFirst);
	if (context == NULL)
	{
		free (bitmapData);
		fprintf (stderr, "Context not created!");
	}
	
	// Make sure and release colorspace before returning
	CGColorSpaceRelease( colorSpace );
	
	return context;
}

@synthesize lastColor;
@synthesize pickedColorDelegate;
@synthesize touchBeginPoint;
@synthesize movingTag;

@end