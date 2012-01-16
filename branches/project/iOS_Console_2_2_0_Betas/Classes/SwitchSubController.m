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
#import "SwitchSubController.h"
#import "Switch.h"
#import "Image.h"
#import "DirectoryDefinition.h"
#import "PollingStatusParserDelegate.h"
#import "Sensor.h"
#import "NotificationConstant.h"

@interface SwitchSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) Switch *sswitch;
@property (nonatomic) BOOL canUseImage;
@property (nonatomic) BOOL isOn;
@property (nonatomic, retain) UIImage *onUIImage;
@property (nonatomic, retain) UIImage *offUIImage;

- (void)setOn:(BOOL)on;
- (void)stateChanged:(id)sender;

@end

@implementation SwitchSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        [button addTarget:self action:@selector(stateChanged:) forControlEvents:UIControlEventTouchUpInside];
        
        NSString *onImage = self.sswitch.onImage.src;
        NSString *offImage = self.sswitch.offImage.src;
        self.canUseImage = onImage && offImage;

        if (self.canUseImage) {
            self.onUIImage = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:onImage]] autorelease];
            self.offUIImage = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:offImage]] autorelease];
            [button.imageView setContentMode:UIViewContentModeCenter];
        } else {
            UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:29];
            [button setBackgroundImage:buttonImage forState:UIControlStateNormal];
            button.titleLabel.font = [UIFont boldSystemFontOfSize:13];
            button.titleLabel.lineBreakMode = UILineBreakModeTailTruncation;
        }
        self.view = button;
        [self setOn:NO];
        
        int sensorId = ((SensorComponent *)self.component).sensorId;
        if (sensorId > 0 ) {
            [[NSNotificationCenter defaultCenter] removeObserver:self name:[NSString stringWithFormat:NotificationPollingStatusIdFormat, sensorId] object:nil];
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat, sensorId] object:nil];
        }
    }    
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.onUIImage = nil;
    self.offUIImage = nil;
    [super dealloc];
}

- (Switch *)sswitch
{
    return (Switch *)self.component;
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = self.sswitch.sensor.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d", sensorId]];
	if ([[newStatus uppercaseString] isEqualToString:@"ON"]) {
		[self setOn:YES];
	} else if ([[newStatus uppercaseString] isEqualToString:@"OFF"]) {
		[self setOn:NO];
	} 
}

// Update the UI of swith view with polling status.
- (void)setOn:(BOOL)on
{
	if (on) {
		self.isOn = YES;
		if (self.canUseImage) {
			[(UIButton *)self.view setImage:self.onUIImage forState:UIControlStateNormal];		
		} else {
			[(UIButton *)self.view setTitle:@"ON" forState:UIControlStateNormal];			
		}
	} else {
        NSLog(@"%@", self.view);
		self.isOn = NO;
		if (self.canUseImage) {
			[(UIButton *)self.view setImage:self.offUIImage forState:UIControlStateNormal];			
		} else {
			[(UIButton *)self.view setTitle:@"OFF" forState:UIControlStateNormal];
		}		
	}
}

// Send boolean control command to remote controller server.
- (void)stateChanged:(id)sender
{
	if (self.isOn) {
		[self sendCommandRequest:@"OFF"];
	} else {		
		[self sendCommandRequest:@"ON"];
	} 
}

@synthesize view;
@synthesize sswitch;
@synthesize canUseImage, isOn;
@synthesize onUIImage, offUIImage;

@end