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

#import "ButtonView.h"
#import "Control.h"
#import "DirectoryDefinition.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"

@interface ButtonView (Private) 
- (void)createButton;
- (void)controlButtonUp:(id)sender;
- (void)sendRequest;

@end

@implementation ButtonView




- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
			[self setBackgroundColor:[UIColor blackColor]];
	
    }
    return self;
}

// Set control and add button in this view according to control
- (void)setControl:(Control *)c {
	[c retain];
	[control release];
	control = c;
	
	[self createButton];
}

//Create button according to control and add tap event
- (void)createButton {
	if (button != nil) {
		[button release];
		button = nil;
	}
	button = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
	
		

	[button addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpInside];
	
	[self addSubview:button];
	
}



- (void) controlButtonUp:(id)sender {
	

		[self	sendRequest];
	
}





//override layoutSubviews method of UIView 
- (void)layoutSubviews {	
	/*[button setFrame:[self bounds]];
	if (control.icon && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:control.icon]]) {
		icon = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:control.icon]];
//		if (icon.size.width > self.bounds.size.width || icon.size.height > self.bounds.size.height) {
//			CGSize size = CGSizeMake(0,0);
//			if ((icon.size.width -  self.bounds.size.width) > (icon.size.height - self.bounds.size.height)) {
//				size = CGSizeMake(self.bounds.size.width, icon.size.height * ((icon.size.width -  self.bounds.size.width) /icon.size.width ));
//			} else {
//				size = CGSizeMake(icon.size.width * ((icon.size.height -  self.bounds.size.height) /icon.size.height ), self.bounds.size.height);
//			}
//			NSLog(@"CGSize width = %d,height = %d",size.width,size.height);
//			UIGraphicsBeginImageContext(size);
//			
//			CGContextRef context = UIGraphicsGetCurrentContext();
//			CGContextTranslateCTM(context, 0.0, size.height);
//			CGContextScaleCTM(context, 1.0, -1.0);
//			
//			CGContextDrawImage(context, CGRectMake(0.0f, 0.0f, size.width, size.height), icon.CGImage);
//			
//			UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
//			
//			UIGraphicsEndImageContext();
//			
//			
//			[button setImage:scaledImage forState:UIControlStateNormal];
//		} else {
			[button setImage:icon forState:UIControlStateNormal];
//		}

		
	} else {
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateNormal];
		
		buttonImage = [[UIImage imageNamed:@"buttonHighlighted.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateHighlighted];
		
		[button setFont:[UIFont boldSystemFontOfSize:18]];
		[button setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
		[button setTitleShadowOffset:CGSizeMake(0, -2)];
		[button setTitle:control.label forState:UIControlStateNormal];
	}*/
	
}




- (void)dealloc {
	[uiImage  release];
	[uiImagePressed release];
	[buttonTimer release];

	[button release];
	
  [super dealloc];
}


@end
