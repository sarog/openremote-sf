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

#import "TabBarController.h"
#import "TabBarItem.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "DirectoryDefinition.h"

@implementation TabBarController

@synthesize customziedTabBar, groupController;

- (id) initWithGroupController:(GroupController *)groupControllerParam tabBar:(TabBar *)tabBar {
	if (self = [super initWithNibName:nil bundle:nil]) {
		if (tabBar) {
			customziedTabBar = tabBar;
			
			self.delegate = self;
			[self.view setFrame:CGRectMake(0, 0, 320, 460)];			
			self.groupController = groupControllerParam;
			NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
			for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
				UIViewController *itemController = [[UIViewController alloc] init];
				itemController.view = groupController.view;
				NSLog(@"before title is : %@", [tabBarItem tabBarItemName]);
				itemController.tabBarItem.title = tabBarItem.tabBarItemName;
				UIImage *image = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:tabBarItem.tabBarItemImage.src]];
				itemController.tabBarItem.image = image;
				NSLog(@"after title is : %@", itemController.tabBarItem.title);
				
				[viewControllers addObject:itemController];
			}
			self.viewControllers = viewControllers;
		}
	}
	return self;
	
}

#pragma mark Delegate method of UITabBarController
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
	NSString *selectedViewControllerTitle = viewController.tabBarItem.title;
	for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
		NSLog(@"tabBarItem name is %@", tabBarItem.tabBarItemName);
		NSLog(@"selectedViewControllerTitle is %@", selectedViewControllerTitle);
		if ([selectedViewControllerTitle isEqualToString:tabBarItem.tabBarItemName]) {
			if (tabBarItem.navigate) {
				NSLog(@"navigate isPreviousScreen is: %d", tabBarItem.navigate.isPreviousScreen);
				NSLog(@"navigate isNextScreen is: %d", tabBarItem.navigate.isNextScreen);
				NSLog(@"navigate isSetting is: %d", tabBarItem.navigate.isSetting);
				[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:tabBarItem.navigate];
			}
		}
	}
}

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[groupController release];
	[customziedTabBar release];
    [super dealloc];
}


@end
