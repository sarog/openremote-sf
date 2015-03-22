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
#import "ImageSubController.h"
#import "Image.h"
#import "Label.h"
#import "DirectoryDefinition.h"
#import "SensorStatusCache.h"
#import "SensorState.h"
#import "Sensor.h"
#import "ORImageView.h"
#import "UIColor+ORAdditions.h"

@interface ImageSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) Image *image;

@end

@implementation ImageSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        ORImageView *imageView = [[ORImageView alloc] initWithFrame:CGRectZero];        
        UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:self.image.src]];
        imageView.image.image = uiImage;
        [uiImage release];
        imageView.label.font = [UIFont fontWithName:@"Arial" size:self.image.label.fontSize];
        imageView.label.textColor = [UIColor or_ColorWithRGBString:[self.image.label.color substringFromIndex:1]];
        self.view = imageView;
        [imageView release];
    }
    return self;
}

- (Image *)image
{
    return (Image *)self.component;
}

- (void)setPollingStatus:(NSNotification *)notification
{
	SensorStatusCache *statusCache = (SensorStatusCache *)[notification object];
	int sensorId = self.image.sensorId;
	NSString *newStatus = [statusCache valueForSensorId:sensorId];
    
    ORImageView *imageView = (ORImageView *)self.view;
    // If no state matching update is found in our states definition, fallback to display the label (if one is defined)
	
    NSString *stateValue = [self.image.sensor stateValueForName:newStatus];
    if (stateValue) {
        UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:stateValue]];
        imageView.image.image = uiImage;
        [uiImage release];
        [imageView showImage];
    } else {
        if (self.image.label) {
            [imageView showLabel];
            stateValue = [self.image.label.sensor stateValueForName:newStatus];
            if (stateValue) {
                imageView.label.text = stateValue;
            } else {
                imageView.label.text = newStatus;
            }
        }
    }
}

@synthesize view;

@end