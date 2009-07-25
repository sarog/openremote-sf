//
//  openremoteAppDelegate.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright finalist 2009. All rights reserved.
//

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
