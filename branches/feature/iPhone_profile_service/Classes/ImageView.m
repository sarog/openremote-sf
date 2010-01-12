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

#import "ImageView.h"
#import "Image.h"
#import "DirectoryDefinition.h"
#import "ClippedUIImage.h"
#import "UIViewUtil.h"
#import "Definition.h"
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"
#import "SensorState.h"

@interface ImageView(Private)
-(Image *) initImageModelWithLabel;
@end

@implementation ImageView

@synthesize defaultImageView;

#pragma mark Overrided methods of superclass(SensoryView)

- (void) initView {
	Image *imageModel = [self initImageModelWithLabel];
	UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:imageModel.src]];
	defaultImageView = [[UIImageView alloc] initWithFrame:self.bounds];
	defaultImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:self uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	[defaultImageView setContentMode:UIViewContentModeTopLeft];
	[self addSubview:defaultImageView];
}

- (void) addPollingNotificationObserver {
	Image *imageModel = ((Image *)component);
	int sensorId = imageModel.sensor.sensorId;
	NSLog(@"image sensor id is : %d", sensorId);
	if (!(sensorId > 0)) {
		sensorId = imageModel.label.sensor.sensorId;
	}
	NSLog(@"label sensor id is : %d", sensorId);
	if (sensorId > 0) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,sensorId] object:nil];
	}
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Image *)component).sensor.sensorId;
	if (!(sensorId > 0)) {
		sensorId = ((Image *)component).label.sensor.sensorId;
	}
	NSLog(@"SetPollingStatus, sensor id is : %d", sensorId);
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
	NSLog(@"new status is : %@", newStatus);
	
	Image *imageModel = ((Image *)component);
	BOOL changeView = NO;
	
	NSLog(@"sensor states count is %d", imageModel.sensor.states.count);
	// Render sensor's state image
	for (SensorState *sensorState in imageModel.sensor.states) {
		NSLog(@"sensorState.name is %@", sensorState.name);
		NSLog(@"newStatus is %@", newStatus);
		if ([[sensorState.name lowercaseString] isEqualToString:[newStatus lowercaseString]]) {
			UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:sensorState.value]];
			defaultImageView.image = uiImage;
			changeView = YES;
			break;
		}
	}
	
	// Render included label
	if (!changeView) {
		NSLog(@"++++++++++++++++++++SetPollingStatus, sensor id is : %d", sensorId);
		NSLog(@"++++++++++++++++++++new status is : %@", newStatus);
		NSLog(@"++++++++++++++++++++included label sensor states count is  : %d", imageModel.label.sensor.states.count);
		UILabel *uiLabel = [[UILabel alloc] initWithFrame:self.bounds];
		uiLabel.text = newStatus;
	}
	
	// Render the default image
	if (!changeView) {
	}	
}

#pragma mark Private methods implementation

-(Image *) initImageModelWithLabel {
	Image *tempImageModel = (Image *)component;
	for (Label *tempLabel in [Definition sharedDefinition].labels) {
		if (tempImageModel.label.componentId == tempLabel.componentId) {
			tempImageModel.label = tempLabel;
			break;
		}
	}
	return tempImageModel;
}

#pragma mark dealloc

- (void) dealloc {
	[defaultImageView release];
	[super dealloc];
}

@end
