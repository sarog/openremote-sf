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


#import "AppDelegate.h"
#import "Activity.h"
#import "ScreenViewController.h"
#import "ActivitiesController.h"
#import "InitViewController.h"
#import "UpdateController.h"
#import "ViewHelper.h"

@interface AppDelegate (Private)
- (void)updateDidFinished;
@end

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    // Override point for customization after application launch
	window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	[window makeKeyAndVisible];
	
	defaultView = [[UIView alloc] initWithFrame:CGRectMake(window.bounds.origin.x, window.bounds.origin.y+20, window.bounds.size.height, window.bounds.size.width) ];
	[window addSubview:defaultView];
	
	initViewController = [[InitViewController alloc] init];
	
	[defaultView addSubview:initViewController.view];
	
	updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
}

- (void)didUpadted {
	[self updateDidFinished];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[ViewHelper showAlertViewWithTitle:@"Warning" Message:[errorMessage stringByAppendingString:@" Using cached content."]];
	[self updateDidFinished];
}


- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	[initViewController.view removeFromSuperview];
	ActivitiesController *activityController = [[ActivitiesController alloc] init];
	[activityController setTitle:@"Activities"];
	navigationController = [[UINavigationController alloc] initWithRootViewController:activityController];
	[window addSubview:navigationController.view];
	[activityController release];
}


- (void)dealloc {
	[updateController release];
	[defaultView release];
	[navigationController release];
	[window release];
    [super dealloc];
	
}


@end
