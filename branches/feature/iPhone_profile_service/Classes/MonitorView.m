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
- (void) initMonitorContent;
- (void) refreshMonitorContent:(NSString *)newStatus;
- (void) refreshMonitorImageURL:(NSString *)imageURL;
- (void) refreshMonitorText:(NSString *)text;
- (void) clearMonitorContent;
@end

@implementation MonitorView

@synthesize monitorContent;

#pragma mark Overridden methods

// This method is abstract method of direct superclass ControlView's.
// So, this method must be overridden in subclass.
- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *delegate = (PollingStatusParserDelegate *)[notification object];
	NSString *newStatus = [delegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",control.controlId]];
	[self refreshMonitorContent:newStatus];
}

// This method is abstract method of indirect superclass UIView's.
- (void)layoutSubviews {
	[self initMonitorContent];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,control.controlId] object:nil];
	
}

#pragma mark Private methods implementation

- (void) initMonitorContent {
	monitorContent = [[UIView alloc] initWithFrame:[self bounds]];
	[self addSubview:monitorContent];
}

- (void) refreshMonitorContent:(NSString *)newStatus {
	// The newStatus is a image URL string.
	if ([[newStatus lowercaseString] hasPrefix:@"http://"]) {
		[self refreshMonitorImageURL:newStatus];
	} 
	// The newStatus is a normal string.
	else {
		[self refreshMonitorText:newStatus];
	}
}

- (void) refreshMonitorImageURL:(NSString *)imageURL {
	[self clearMonitorContent];
	[FileUtils downloadFromURL:imageURL path:[DirectoryDefinition imageCacheFolder]];
	NSString *imageFileName = [StringUtils parsefileNameFromString:imageURL];
	UIImage *imageContent = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:imageFileName]];
	if (imageContent) {
		self.monitorContent = [[UIImageView alloc] initWithFrame:[self bounds]];
		[(UIImageView *)monitorContent setImage:imageContent];
		[(UIImageView *)monitorContent sizeToFit];
		[monitorContent setBackgroundColor:nil];
	} else {
		self.monitorContent = [[UILabel alloc] initWithFrame:[self bounds]];
		[monitorContent setBackgroundColor:[UIColor grayColor]];
	}
	[self addSubview:monitorContent];
}

- (void) refreshMonitorText:(NSString *)text {
	[self clearMonitorContent];
	self.monitorContent = [[UILabel alloc] initWithFrame:[self bounds]];
	[(UILabel *)monitorContent setTextAlignment:UITextAlignmentCenter];
	[monitorContent setBackgroundColor:[UIColor grayColor]];
	[(UILabel *)monitorContent setText:[NSString stringWithUTF8String:[text UTF8String]]];
	[self addSubview:monitorContent];
}

- (void) clearMonitorContent {
	for (UIView *view in self.subviews) {
		[view removeFromSuperview];
	}
}

#pragma mark Dealloc method
- (void)dealloc {
	if (monitorContent) {
		[monitorContent release];
	}
	[super dealloc];
}

@end
