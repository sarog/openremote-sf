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
					
			self.groupController = groupControllerParam;
			NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
			int i = 0;
			int selected = i;
			for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
				
				UIViewController *itemController = [[UIViewController alloc] init];
				itemController.view = groupController.view;
				itemController.tabBarItem.title = tabBarItem.tabBarItemName;
				UIImage *image = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:tabBarItem.tabBarItemImage.src]];
				itemController.tabBarItem.image = image;
				
				[viewControllers addObject:itemController];
				
				if (tabBarItem.navigate && groupController.group.groupId == tabBarItem.navigate.toGroup) {
					selected = i;
				}
				i++;
			}
			self.viewControllers = viewControllers;
			//set selected index after viewControllers have been added, or it won't work
			//otherwise must set selected index in viewDidLoad
			[self setSelectedIndex:selected];
		}
	}
	return self;
	
}

- (void)updateGroupController:(GroupController *)groupControllerParam {
	self.groupController = groupControllerParam;
	NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
	for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
		UIViewController *itemController = [[UIViewController alloc] init];
		itemController.view = groupController.view;
		itemController.tabBarItem.title = tabBarItem.tabBarItemName;
		UIImage *image = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:tabBarItem.tabBarItemImage.src]];
		itemController.tabBarItem.image = image;
		
		[viewControllers addObject:itemController];
	}
	self.viewControllers = viewControllers;
}

#pragma mark Delegate method of UITabBarController
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
	NSString *selectedViewControllerTitle = viewController.tabBarItem.title;
	for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
		if ([selectedViewControllerTitle isEqualToString:tabBarItem.tabBarItemName]) {
			if (tabBarItem.navigate) {
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
