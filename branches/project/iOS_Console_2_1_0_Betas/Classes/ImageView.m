/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "ImageView.h"
#import "Image.h"
#import "DirectoryDefinition.h"
#import "ClippedUIImage.h"
#import "UIViewUtil.h"
#import "Definition.h"
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"
#import "SensorState.h"
#import "UIColor+ORAdditions.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface ImageView(Private)
-(Image *) initImageModelWithLabel;
-(void) clearSubviews;
-(void) renderIncludedLabel:(Image *)imageModel newStatus:(NSString *)newStatus;
@end

@implementation ImageView

@synthesize defaultImageView, removeSubviewsTag, changeView;

#pragma mark Overrided methods of superclass(SensoryView)

- (void) initView {
	Image *imageModel = (Image *)component;//[self initImageModelWithLabel];
	if (!removeSubviewsTag) {
		UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:imageModel.src]];
		defaultImageView = [[UIImageView alloc] initWithFrame:self.bounds];
		defaultImageView.image = uiImage;
        [uiImage release];
		//defaultImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:self uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
		[defaultImageView setContentMode:UIViewContentModeTopLeft];
		[self addSubview:defaultImageView];
	}
}

// Override method of sensory view.
- (void) addPollingNotificationObserver {
	Image *imageModel = ((Image *)component);
	int sensorId = imageModel.sensor.sensorId;
	if (sensorId <= 0) {
		sensorId = imageModel.label.sensor.sensorId;
	}
	if (sensorId > 0) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,sensorId] object:nil];
	}
}

// Override method of sensory view.
- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Image *)component).sensor.sensorId;
	if (!(sensorId > 0)) {
		sensorId = ((Image *)component).label.sensor.sensorId;
	}
	
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
	Image *imageModel = ((Image *)component);
	changeView = NO;
	
	// Render image-sensor's state image
	for (SensorState *sensorState in imageModel.sensor.states) {
		if ([[sensorState.name lowercaseString] isEqualToString:[newStatus lowercaseString]]) {
			UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:sensorState.value]];
			//uiImage = [[ClippedUIImage alloc] initWithUIImage:uiImage dependingOnUIView:self imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];
			defaultImageView.image = uiImage;
            [uiImage release];
			changeView = YES;
			break;
		}
	}
	
	// Render included label
	if (!changeView && imageModel.label) {
		[self clearSubviews];
		[self renderIncludedLabel:imageModel newStatus:newStatus];
	}
}

#pragma mark Private methods implementation

-(Image *) initImageModelWithLabel {
	Image *tempImageModel = (Image *)component;
    // TODO EBR review
	for (Label *tempLabel in [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.labels) {
		if (tempImageModel.label.componentId == tempLabel.componentId) {
			tempImageModel.label = tempLabel;
			break;
		}
	}
	return tempImageModel;
}

// Remove all subviews of imageView.
-(void) clearSubviews {
	removeSubviewsTag = YES;
	for(UIView *tempView in self.subviews) {
		[tempView removeFromSuperview];
	}
}

// Render label imageView included. So, imageView's content is depending on labelView and lableView's sensors.
-(void) renderIncludedLabel:(Image *)imageModel newStatus:(NSString *)newStatus {
	UILabel *uiLabel = [[UILabel alloc] initWithFrame:self.bounds];
	[uiLabel setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
	[uiLabel setTextAlignment:UITextAlignmentCenter];
	uiLabel.text = imageModel.label.text;
	uiLabel.font = [UIFont fontWithName:@"Arial" size:imageModel.label.fontSize];
	uiLabel.textColor = [UIColor or_ColorWithRGBString:[imageModel.label.color substringFromIndex:1]];
	
	for (SensorState *sensorState in imageModel.label.sensor.states) {
		if ([[sensorState.name lowercaseString] isEqualToString:[newStatus lowercaseString]]) {
			uiLabel.text = sensorState.value;
			changeView = YES;
			[self addSubview:uiLabel];
			break;
		}
	}
    [uiLabel release];
}

#pragma mark dealloc

- (void) dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
	[defaultImageView release];
	[super dealloc];
}

@end
