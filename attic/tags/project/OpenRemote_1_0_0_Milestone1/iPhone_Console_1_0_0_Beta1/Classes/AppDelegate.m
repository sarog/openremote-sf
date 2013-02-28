//
//  openremoteAppDelegate.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright finalist 2009. All rights reserved.
//

#import "AppDelegate.h"
#import "Activity.h"
#import "Screen.h"
#import "Control.h"
#import "ScreenViewController.h"
#import "ServerDefinition.h"
#import "Definition.h"
#import "ActivitiesController.h"

@interface AppDelegate (Private)

- (void)updateDidFinished;
- (void)stopLoadingAndShowViews;

@end

@implementation AppDelegate


- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    // Override point for customization after application launch
	window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	[window makeKeyAndVisible];
			
	//Shows loading view
	loadingView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
	[loadingView sizeToFit];
	[loadingView setFrame:CGRectMake(window.bounds.size.width / 2 - loadingView.frame.size.width / 2, window.bounds.size.height / 2 - loadingView.frame.size.height / 2, loadingView.frame.size.height, loadingView.frame.size.width)];
	[window addSubview:loadingView];
	
	[loadingView startAnimating];
	
	[[Definition sharedDefinition] update];
	
	
	//add a observer to defination upload task 
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDidFinished) name:DefinationUpdateDidFinishedNotification object:[Definition sharedDefinition]];	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDidFinished) name:DefinationNeedNotUpdate object:[Definition sharedDefinition]];	

}

//- (void)updateDidFinished {
//	tabbarController = [[UITabBarController alloc] init];
//	
//	// Get array of screens
//	// Build array of UIViewControllers for each screen
//	NSMutableArray  *viewControllers = [[NSMutableArray alloc] init];
//	NSArray *screens = [[Definition sharedDefinition] screens];
//	for (Screen *screen in screens) {
//		ScreenViewController *viewController = [[ScreenViewController alloc]init];
//		[viewController setScreen:screen];
//		[viewControllers addObject:viewController];
//		[viewController release];
//	}
//	[tabbarController setViewControllers:viewControllers];
//	[viewControllers release];
//	if  (loadingView) {
//		[self	 stopLoadingAndShowViews];
//	}
//	
//	[window addSubview:tabbarController.view];
//	
//	
//}

- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	ActivitiesController *activityController = [[ActivitiesController alloc] init];
	[activityController setTitle:@"Activities"];

	navigationController = [[UINavigationController alloc] initWithRootViewController:activityController];
	[activityController release];
	[window addSubview:navigationController.view];

	if  (loadingView) {
		[self	 stopLoadingAndShowViews];
	}
}

- (void)stopLoadingAndShowViews {
	[loadingView stopAnimating];
	[loadingView release];
	loadingView = nil;
}


- (void)dealloc {
	[window release];
	[navigationController release];
    [super dealloc];
	
}


@end
