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


@interface TabBarController (Private)

- (void)updateTabItems;
- (void)returnToContentView;

@end

@implementation TabBarController

@synthesize customziedTabBar, groupController;

- (id) initWithGroupController:(GroupController *)groupControllerParam tabBar:(TabBar *)tabBar {
	if (self = [super initWithNibName:nil bundle:nil]) {
		if (tabBar) {
			customziedTabBar = [tabBar retain];
			isMoreViewShown = NO;
			self.delegate = self;
			[groupControllerParam retain];
			self.groupController = groupControllerParam;
			[self.view setFrame:[groupController getFullFrame]];
			
			self.moreNavigationController.navigationBar.hidden = YES;
			UITableView *tableView = (UITableView *)self.moreNavigationController.topViewController.view;
			[tableView setDelegate:self];
			
			[self updateTabItems];
		}
	}
	return self;
	
}

// Reselect the tabbar item of tabbar controller with groupController reference after selecting a item in more tableView.
- (void)returnToContentView {
	if (groupController && self.viewControllers.count > 0) {
		[self setSelectedViewController:groupController];
		isMoreViewShown = NO;
	}
}

- (void)updateTabItems {
	[self.view setFrame:[groupController getFullFrame]];
	NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
	int i = 0;
	int selected = i;
	BOOL selectedIndexFound = NO;
	for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
		UIViewController *itemController = [[UIViewController alloc] init];
		itemController.view = groupController.view;
		itemController.tabBarItem.title = tabBarItem.tabBarItemName;
		UIImage *image = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:tabBarItem.tabBarItemImage.src]];
		itemController.tabBarItem.image = image;
		[viewControllers addObject:itemController];
		
		if (tabBarItem.navigate && groupController.group.groupId == tabBarItem.navigate.toGroup) {
			if (selectedIndexFound == NO && tabBarItem.navigate.toScreen == [groupController currentScreenId]) {
				selected = i;
				selectedIndexFound = YES;
			}	
		}
		i++;
	}
	
	self.viewControllers = viewControllers;
	
	// no custom view, this disable 'Edit' button in 'More' table view
	self.customizableViewControllers = nil; 
	
	//set selected index after viewControllers have been added, or it won't work
	//otherwise must set selected index in viewDidLoad
	if (selectedIndexFound == NO) {
		i = 0;
		for (TabBarItem *tabBarItem in customziedTabBar.tabBarItems) {
			if (tabBarItem.navigate && groupController.group.groupId == tabBarItem.navigate.toGroup) {
				selected = i;
				selectedIndexFound = YES;
				break;
			}
			i++;
		}
		if (selectedIndexFound == NO) {
			selected = NSNotFound;
		}
	}
	[self setSelectedIndex:selected];
}

- (void)updateGroupController:(GroupController *)groupControllerParam {
	[groupControllerParam retain];
	self.groupController = groupControllerParam;
	[self.view setFrame:[groupController getFullFrame]];
	[self updateTabItems];
}

#pragma mark Delegate method of UITabBarController
- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
	NSLog(@"tabbar selectedIndex = %d", self.selectedIndex);
	
	//if the selected view controller is currently the 'More' navigation controller
	if (self.selectedIndex == NSNotFound) {
		if (isMoreViewShown) {
			[self returnToContentView];
		} else {
			isMoreViewShown = YES;
		}

		return;
	}

	TabBarItem *tabBarItem = [customziedTabBar.tabBarItems objectAtIndex:self.selectedIndex];
	if (tabBarItem && tabBarItem.navigate) {
		[self returnToContentView];
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:tabBarItem.navigate];
		isMoreViewShown = NO;
	} else if (tabBarItem && !tabBarItem.navigate) {
		[self returnToContentView];
	}
	
}

#pragma mark Delegate method of 'More' UITableView
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self returnToContentView];
	int visibleTabbarItemCount = customziedTabBar.tabBarItems.count - [tableView visibleCells].count;
	TabBarItem *tabBarItem = [customziedTabBar.tabBarItems objectAtIndex:indexPath.row + visibleTabbarItemCount];
	if (tabBarItem.navigate) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:tabBarItem.navigate];
	}
}

- (void)dealloc {
	[groupController release];
	[customziedTabBar release];
	
	[super dealloc];
}


@end
