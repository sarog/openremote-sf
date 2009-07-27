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

#import "ActivitiesController.h"
#import "Definition.h"
#import "Screen.h"
#import "ScreenViewController.h"
#import "Activity.h"
#import "DirectoryDefinition.h"
#import "PaginationController.h"
#import "LightsController.h"
#import "AccelerometerController.h"
#import "NotificationConstant.h"
#import "AppSettingController.h"

@interface ActivitiesController (Private)
- (void)hideSettingsView;
- (void)refreshView;
- (void)clearView;
@end

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
		UIBarButtonItem *settingButton = [[UIBarButtonItem alloc] initWithTitle:@"Settings" style:UIBarButtonItemStylePlain target:self action:@selector(showSettingsView)];
		self.navigationItem.leftBarButtonItem = settingButton;
		[settingButton release];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView) name:NotificationRefreshAcitivitiesView object:nil];	
	}
	return self;
}
																			
/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/
- (void)refreshView {
	NSLog(@"reload activity controller.");
	activities = [[Definition sharedDefinition] activities];
	[self.tableView  reloadData];
}
- (void)clearView {
	NSLog(@"clear view activity controller.");
//	[[[Definition sharedDefinition] activities] removeAllObjects];
//	activities = [[Definition sharedDefinition] activities];
//	[self.tableView  reloadData];
}

- (void)showSettingsView {
	AppSettingController *settingController = [[AppSettingController alloc]init];
	UINavigationController *settingNavController = [[UINavigationController alloc] initWithRootViewController:settingController];
	[self presentModalViewController:settingNavController animated:YES];
	[settingController release];
	[settingNavController release];
}																											

- (void)dealloc {
	[activities release];
	[super dealloc];
}


#pragma mark UITableViewDataSource implementation
- (NSInteger)tableView:(UITableView *)table numberOfRowsInSection:(NSInteger)section
{
    return activities.count;
//	return activities.count+2;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TextAndImage"];
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"TextAndImage"] autorelease];
		cell.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	}
	
//	if (indexPath.row == activities.count) {
//		cell.text = @"Control The Light(Mockup)";
//		cell.image = [UIImage imageNamed:@"lightIcon.png"];
//	} else if (indexPath.row == activities.count + 1) {
//		cell.text = @"Control the AirConditioner(Mockup)";
//		cell.image = [UIImage imageNamed:@"AirConditionerIcon.png"];
//	} else {
		Activity *currentActivity = [activities objectAtIndex:indexPath.row ];
		cell.text = currentActivity.name;
		cell.image = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[activities objectAtIndex:indexPath.row] icon]]] autorelease];
//	}
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{	
//	NSLog(@"(indexPath.row is %d,activities.count is %d" ,indexPath.row ,activities.count);
//	if (indexPath.row == activities.count) {
//		LightsController *lightsController = [[LightsController alloc] init];
//		[lightsController setTitle:@"Control The Light(Mockup)"];
//		[[self navigationController] pushViewController:lightsController animated:YES];
//		[lightsController release];
//	} else if (indexPath.row == activities.count + 1) {
//		AccelerometerController *accelerometerController =[[AccelerometerController alloc] init];
//		[accelerometerController setTitle:@"Control the AirConditioner(Mockup)"];
//		[[self navigationController] pushViewController:accelerometerController animated:YES];
//		[accelerometerController release];
//	} else {
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
//	}
	
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
																			
																		



@end
