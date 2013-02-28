//
//  ActivitiesController.m
//  openremote
//
//  Created by finalist on 2/26/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ActivitiesController.h"
#import "Definition.h"
#import "Screen.h"
#import "ScreenViewController.h"
#import "Activity.h"
#import "DirectoryDefinition.h"
#import "PaginationController.h"
#import "LightsController.h"
#import "AccelerometerController.h"


@implementation ActivitiesController

/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
    }
    return self;
}
*/

- (id)init {
	if (self == [super initWithStyle:UITableViewStyleGrouped]) {
		activities = [[Definition sharedDefinition] activities];
	}
	return self;
}

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/

#pragma mark UITableViewDataSource implementation
- (NSInteger)tableView:(UITableView *)table numberOfRowsInSection:(NSInteger)section
{
//    return activities.count;
	return activities.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TextAndImage"];
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"TextAndImage"] autorelease];
		cell.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	}
	
	Activity *currentActivity = [activities objectAtIndex:indexPath.row ];
	//for mock light
	if (currentActivity.activityId == 3) {
		cell.text = currentActivity.name;
		cell.image = [UIImage imageNamed:@"lightIcon.png"];
	} else if (currentActivity.activityId == 4) {
		cell.text = currentActivity.name;
		cell.image = [UIImage imageNamed:@"AirConditionerIcon.png"];
	} else {
		cell.text = currentActivity.name;
		cell.image = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[activities objectAtIndex:indexPath.row] icon]]] autorelease];
	}
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Create a view controller with the title as its navigation title and push it.
	Activity *currentActivity = [activities objectAtIndex:indexPath.row ];
	
	if (currentActivity.activityId == 3) {
		LightsController *lightsController = [[LightsController alloc] init];
		[lightsController setTitle:currentActivity.name];
		[[self navigationController] pushViewController:lightsController animated:YES];
		[lightsController release];
	} else if (currentActivity.activityId == 4) {
		AccelerometerController *accelerometerController =[[AccelerometerController alloc] init];
		[accelerometerController setTitle:currentActivity.name];
		[[self navigationController] pushViewController:accelerometerController animated:YES];
		[accelerometerController release];
	} else {
		PaginationController *paginationController = [[PaginationController alloc] init];
			
		// Get array of screens
		// Build array of UIViewControllers for each screen
		NSMutableArray  *viewControllers = [[NSMutableArray alloc] init];
		Activity *selectedActivity = [activities objectAtIndex:indexPath.row] ;
		NSArray *screens = [selectedActivity screens];
		for (Screen *screen in screens) {
			ScreenViewController *viewController = [[ScreenViewController alloc]init];
			[viewController setScreen:screen];
			[viewControllers addObject:viewController];
			[viewController release];
		}
		[paginationController setViewControllers:viewControllers];
		[paginationController setTitle:[selectedActivity name]];
		[viewControllers release];
		
		[[self navigationController] pushViewController:paginationController animated:YES];
		[paginationController release];	
	}
	
}

- (UITableViewCellAccessoryType)tableView:(UITableView *)tableView accessoryTypeForRowWithIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewCellAccessoryDisclosureIndicator;
}

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	[super viewDidLoad];
}
*/


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return NO;
}



- (void)dealloc {
	[activities release];
	[super dealloc];
}


@end
