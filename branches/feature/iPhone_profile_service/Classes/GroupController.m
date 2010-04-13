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

#import "GroupController.h"
#import "NotificationConstant.h"
#import "PaginationController.h"
#import "Screen.h"
#import "ScreenViewController.h"


@interface GroupController (Private)

- (NSMutableArray *)initScreenViewControllers:(NSArray *)screens;
- (void)showErrorView;

@end



@implementation GroupController

@synthesize group;

- (id)initWithGroup:(Group *)newGroup {
	if (self = [super init]) {
		if (newGroup) {
			group = [newGroup retain];// must retain newGroup here!!!
			[self setTitle:group.name];
		}
		[[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
		currentOrientation = [[UIDevice currentDevice] orientation];
	}
	return self;
}

- (UIInterfaceOrientation)getCurrentOrientation {
	return currentOrientation;
}

- (void)setNewOrientation:(UIInterfaceOrientation)newOrientation {
	if (currentOrientation != newOrientation) {
		currentOrientation = newOrientation;
		[self willRotateToInterfaceOrientation:newOrientation duration:1.0f];
	}
}

- (int)groupId {
	return group.groupId;
}

- (NSMutableArray *)initScreenViewControllers:(NSArray *)screens {
	NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
	
	for (Screen *screen in screens) {
		NSLog(@"screen = %@", screen.name);
		ScreenViewController *viewController = [[ScreenViewController alloc]init];
		[viewController setScreen:screen];
		[viewControllers addObject:viewController];
		[viewController release];
	}
	return viewControllers;
}

- (PaginationController *)currentPaginationController {	
	return paginationController;
}

- (void)showPortrait {
	if ([group getPortraitScreens].count > 0) {
		[paginationController release];
		paginationController = nil;
		paginationController = [[PaginationController alloc] init];
		NSMutableArray *viewControllers = [self initScreenViewControllers:[group getPortraitScreens]];
		[paginationController setViewControllers:viewControllers isLandscape:NO];
		[viewControllers release];
		[self setView:paginationController.view];
	} else {
		[self showErrorView];
	}
}

- (void)showLandscape {
	if ([group getLandscapeScreens].count > 0) {
		[paginationController release];
		paginationController = nil;
		paginationController = [[PaginationController alloc] init];
		NSMutableArray *viewControllers = [self initScreenViewControllers:[group getLandscapeScreens]];
		[paginationController setViewControllers:viewControllers isLandscape:YES];
		[viewControllers release];
		[self setView:paginationController.view];
	} else {
		[self showErrorView];
	}
}
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	[super viewDidLoad];
	[self.navigationController setNavigationBarHidden:YES];
	[self showPortrait];
}

- (void)showErrorView {
	errorViewController = [[ErrorViewController alloc] 
												 initWithErrorTitle:@"No Screen Found" 
												 message:@"Please associate screens with group or reset setting."];
	[self setView:errorViewController.view];	
}

- (ScreenViewController *)currentScreenViewController {
	return [[self currentPaginationController] currentScreenViewController]; 
}

- (Screen *)currentScreen {
	return [self currentScreenViewController].screen;
}

- (int)currentScreenId {
	return [self currentScreen].screenId;
}

- (void)startPolling {
	if ([self currentPaginationController].viewControllers.count > 0) {
		[[self currentScreenViewController] startPolling];
		NSLog(@"start polling screen_id=%d",[self currentScreenId]);
	}
}

- (void)stopPolling {
	for (ScreenViewController *svc in [self currentPaginationController].viewControllers) {
		NSLog(@"stop polling screen_id=%d",svc.screen.screenId);
		[svc stopPolling];
	}
}

- (BOOL)switchToScreen:(int)screenId {
	NSLog(@"to screen %d", screenId);
	return [[self currentPaginationController] switchToScreen:screenId];
}

- (BOOL)previousScreen {
	return [[self currentPaginationController] previousScreen];
}

- (BOOL)nextScreen {
	return [[self currentPaginationController] nextScreen];
}

- (void)performGesture:(Gesture *)gesture {
	return [[self currentScreenViewController] performGesture:gesture];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return [[self currentScreen] inverseScreenId] > 0;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	int inverseScreenId = [self currentScreen].inverseScreenId;
	NSLog(@"switch screen from %d - > %d", [self currentScreenId], inverseScreenId);
	currentOrientation = toInterfaceOrientation;
	
	if (UIInterfaceOrientationIsPortrait(currentOrientation)) {
		[self showPortrait];
		[self switchToScreen:inverseScreenId];
	} else {
		[self showLandscape];
		[self switchToScreen:inverseScreenId];
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
	[paginationController release];
	[errorViewController release];
	//[group release];
	
	[super dealloc];
}


@end
