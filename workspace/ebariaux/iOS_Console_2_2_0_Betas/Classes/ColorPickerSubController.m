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
#import "ColorPickerSubController.h"
#import "ColorPicker.h"
#import "Image.h"
#import "ColorPickerImageView.h"
#import "DirectoryDefinition.h"

@interface ColorPickerSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) ColorPicker *colorPicker;

@end

@implementation ColorPickerSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:self.colorPicker.image.src]];
        ColorPickerImageView *imageView = [[ColorPickerImageView alloc] initWithImage:uiImage];
        imageView.pickedColorDelegate = self;
        self.view = imageView;
        [imageView release];
        [uiImage release];
    }
    return self;
}

- (ColorPicker *)colorPicker
{
    return (ColorPicker *)self.component;
}

// Send picker command with color value to controller server.
- (void)pickedColor:(UIColor*)color
{
	const CGFloat *c = CGColorGetComponents(color.CGColor);
	NSLog(@"color=%@",color);
	NSLog(@"color R=%0.0f",c[0]*255);
	NSLog(@"color G=%0.0f",c[1]*255);
	NSLog(@"color B=%0.0f",c[2]*255);
	[self sendCommandRequest:[NSString stringWithFormat:@"%02x%02x%02x", (int)(c[0]*255), (int)(c[1]*255), (int)(c[2]*255)]];
}

@synthesize view;

@end