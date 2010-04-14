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
		}
//		[[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
//		currentOrientation = [[UIDevice currentDevice] orientation];
		currentOrientation = UIInterfaceOrientationPortrait;
	}
	return self;
}

- (id)initWithGroup:(Group *)newGroup orientation:(UIInterfaceOrientation)thatOrientation {
	if (self = [super init]) {
		if (newGroup) {
			group = [newGroup retain];// must retain newGroup here!!!
		}
		currentOrientation = thatOrientation;
	}
	return self;
}

- (UIInterfaceOrientation)getCurrentOrientation {
	return currentOrientation;
}

- (void)setNewOrientation:(UIInterfaceOrientation)newOrientation {
	[self willRotateToInterfaceOrientation:newOrientation duration:0];
}

- (BOOL)isOrientationLandscape {
	return UIInterfaceOrientationIsLandscape(currentOrientation);
}

- (CGRect)getFullFrame {
	CGRect frame = self.view.frame;
	CGSize size = [UIScreen mainScreen].bounds.size;
	BOOL isLandscape = [self isOrientationLandscape];
	frame.size.height = isLandscape ? size.width : size.height;
	frame.size.width = isLandscape ? size.height : size.width;
	return frame;
}

- (int)groupId {
	return group.groupId;
}

- (NSMutableArray *)initScreenViewControllers:(NSArray *)screens {
	NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
	
	for (Screen *screen in screens) {
		NSLog(@"init screen = %@", screen.name);
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
	NSLog(@"show portrait");
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
	NSLog(@"show landscape");
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
	if (UIInterfaceOrientationIsPortrait(currentOrientation)) {
		NSLog(@"view did load show portrait");
		[self showPortrait];
	} else {
		NSLog(@"view did load show landscape");
		[self showLandscape];
	}
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
		NSLog(@"start polling screen_id = %d",[self currentScreenId]);
	}
}

- (void)stopPolling {
	for (ScreenViewController *svc in [self currentPaginationController].viewControllers) {
		NSLog(@"stop polling screen_id = %d",svc.screen.screenId);
		[svc stopPolling];
	}
}

- (BOOL)switchToScreen:(int)screenId {
	NSLog(@"switch to screen %d", screenId);
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
	return currentOrientation != interfaceOrientation && [[self currentScreen] inverseScreenId] > 0;
}


- (void)printOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	switch (toInterfaceOrientation) {
		case UIInterfaceOrientationPortrait:
			NSLog(@"is portrait");
			break;
		case UIInterfaceOrientationLandscapeLeft:
			NSLog(@"is landscape left");
			break;
		case UIInterfaceOrientationLandscapeRight:
			NSLog(@"is landscape right");
			break;
		case UIInterfaceOrientationPortraitUpsideDown:
			NSLog(@"is portrait upsidedown");
			break;
		default:
			break;
	}
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	[self printOrientation:currentOrientation];
	[self printOrientation:toInterfaceOrientation];
	if (toInterfaceOrientation == currentOrientation) {
		NSLog(@"same orientation");
		return;
	} else {
		NSLog(@"diff orientation");
		
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
