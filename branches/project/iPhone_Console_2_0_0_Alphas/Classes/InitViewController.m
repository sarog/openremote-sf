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


#import "InitViewController.h"
#import "NotificationConstant.h"
#import "AppSettingController.h"
#import "DirectoryDefinition.h"

@interface InitViewController (Private)

- (void)detectDeviceOrientation;

@end

@implementation InitViewController

- (id)init {
	if (self = [super  init]) {
		[self detectDeviceOrientation];
		
	}
	return self;
}

- (void)createView {
	BOOL isLandscape = UIInterfaceOrientationIsLandscape(currentOrientation);
	InitView *view = [[InitView alloc] initWithOrientation:isLandscape];
	[self setView:view];
	CGSize size = [UIScreen mainScreen].bounds.size;
	
	CGFloat frameWidth = isLandscape ? size.height : size.width;
	CGFloat frameHeight = isLandscape ? size.width : size.height;
	[self.view setFrame:CGRectMake(0, 0, frameWidth, frameHeight)];
}

- (void)loadView {
	[self createView];
}

- (void)detectDeviceOrientation {
	[[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
	currentOrientation = [[UIDevice currentDevice] orientation];
	
	if (currentOrientation == UIDeviceOrientationUnknown) {
		currentOrientation = UIInterfaceOrientationPortrait;
		NSLog(@"it's using simulator, set portrait by default");
	}
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	currentOrientation = toInterfaceOrientation;
	[self createView];
}



@end
