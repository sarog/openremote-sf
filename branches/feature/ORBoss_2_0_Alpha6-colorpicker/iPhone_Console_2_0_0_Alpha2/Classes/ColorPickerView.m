/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import "ColorPickerView.h"
#import "ColorPicker.h"
#import "DirectoryDefinition.h"

@implementation ColorPickerView

- (void)initView {	
	ColorPicker *colorPicker = (ColorPicker *)component;
	uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:colorPicker.image.src]];
	imageView = [[ColorPickerImageView alloc] initWithImage:uiImage];
	imageView.pickedColorDelegate = self;
	[imageView setFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y, uiImage.size.width, uiImage.size.height)];
	[self addSubview:imageView];
}

// Send picker command with color value to controller server.
- (void) pickedColor:(UIColor*)color {
	const CGFloat *c = CGColorGetComponents(color.CGColor);
	NSLog(@"color=%@",color);
	NSLog(@"color R=%0.0f",c[0]*255);
	NSLog(@"color G=%0.0f",c[1]*255);
	NSLog(@"color B=%0.0f",c[2]*255);
	[self sendCommandRequest:[NSString stringWithFormat:@"%02x%02x%02x", (int)(c[0]*255), (int)(c[1]*255), (int)(c[2]*255)]];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"ColorPikcerView moving event......");
}

- (void) touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event {
	NSLog(@"ColorPikcerView end event......");
	[imageView touchesEnded:touches withEvent:event];
}

- (void)dealloc {
	[imageView release];
	[uiImage release];
	
	[super dealloc];
}


@end
