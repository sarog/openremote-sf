/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import <UIKit/UIKit.h>
#import "TabBar.h"
#import "GroupController.h"

/**
 * It's responsible for rendering tabbar and tabbar items with groupControllers.
 */
@interface TabBarController : UITabBarController <UITabBarControllerDelegate, UITableViewDelegate> {
	TabBar *customziedTabBar;
	GroupController *groupController;
	BOOL isMoreViewShown;
}

/**
 * Construct tabBarController with groupController and tabbar.
 * The parameter tabBar can be localTabbar and globalTabBar.
 */
- (id) initWithGroupController:(GroupController *)groupControllerParam tabBar:(TabBar *)tabBar;

/**
 * Update groupController tabBarController contained with specified parameter.
 */
- (void)updateGroupController:(GroupController *)groupControllerParam;

/**
 * Refresh tabbar items manually while groupController tabBarController contained changinng.
 */
- (void)updateTabItems;

@property (nonatomic, retain) TabBar *customziedTabBar;
@property (nonatomic, retain) GroupController *groupController;

@end
