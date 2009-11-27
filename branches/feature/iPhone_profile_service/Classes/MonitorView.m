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

#import "MonitorView.h"
#import	"Monitor.h"
#import "PollingStatusParserDelegate.h"
#import "FileUtils.h"
#import "DirectoryDefinition.h"
#import "NotificationConstant.h"
#import "StringUtils.h"

@interface MonitorView(Private)
- (void) setImage:(NSString *)urlString;
- (void) setNormalStringStatus:(NSString *)normalString;
- (void) initImageButton;
- (void) clearImageButton;
@end

@implementation MonitorView

@synthesize imageButton;

#pragma mark Overridden methods

// This method is abstract method of direct superclass ControlView's.
// So, this method must be overridden in subclass.
- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *delegate = (PollingStatusParserDelegate *)[notification object];
	NSString *newStatus = [delegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",control.controlId]];
	// If the newStatus is a URL string.
	if ([[newStatus lowercaseString] hasPrefix:@"http://"]) {
		NSLog(@"Got URL status string %@ for Monitor.", newStatus);
		NSLog(@"Downloading image with URL string %@ for Monitor.", newStatus);
		[FileUtils downloadFromURL:newStatus path:[DirectoryDefinition imageCacheFolder]];
		[self setImage:newStatus];
	} else {
		NSLog(@"Got normal status string for Monitor.");
		[self setNormalStringStatus:newStatus];
	}
}

// This method is abstract method of indirect superclass UIView's.
- (void)layoutSubviews {
	[self initImageButton];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,control.controlId] object:nil];
	
}

#pragma mark Private methods implementation

- (void) initImageButton {
	if (imageButton) {
		[imageButton removeFromSuperview];
		[imageButton release];
	}
	imageButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
	[imageButton setFrame:[self bounds]];
	[self addSubview:imageButton];

}

- (void) clearImageButton {
	if (imageButton) {
		[imageButton setTitle:@"" forState:UIControlStateNormal];
		[imageButton setBackgroundImage:nil forState:UIControlStateNormal];
		[imageButton setBackgroundColor:nil];
		[imageButton setImage:nil forState:UIControlStateNormal];
	}
}

- (void) setImage:(NSString *)urlString {
	
	NSLog(@"Begin: Update image with updated status for Monitor.");
	NSString *imageFileName = [StringUtils parsefileNameFromString:urlString];
	NSLog(@"Image Name in setImage---------------%@", imageFileName);
	UIImage *newStateImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:imageFileName]];
	NSLog(@"new StateImage is : %@", newStateImage);
	NSLog(@"new stateimage width is : %d", newStateImage.size.width);
	NSLog(@"new stateimage height is : %d", newStateImage.size.height);
	[self clearImageButton];
	if (newStateImage) {
		[imageButton setImage:newStateImage forState:UIControlStateNormal];
	} else {
		[imageButton setBackgroundColor:[UIColor blackColor]];
	}
	NSLog(@"End: Update image with updated status for Monitor.");
	
}

- (void) setNormalStringStatus:(NSString *)normalString {
	NSLog(@"Begin: Update string status for Monitor.");
	[self clearImageButton];
	[imageButton setBackgroundColor:[UIColor blackColor]];
	[imageButton setTitle:[NSString stringWithUTF8String:[normalString UTF8String]] forState:UIControlStateNormal];
	NSLog(@"End: Update string status for Monitor.");
}

#pragma mark Dealloc method
- (void)dealloc {
	[imageButton release];
	[super dealloc];
}

@end
