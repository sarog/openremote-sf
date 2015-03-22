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
- (void)detectDeviceOrientation;
- (void)printOrientation:(UIInterfaceOrientation)toInterfaceOrientation;

@end



@implementation GroupController

@synthesize group;

- (id)initWithGroup:(Group *)newGroup {
	if (self = [super init]) {
		if (newGroup) {
			group = [newGroup retain];// must retain newGroup here!!!
		}
		[self detectDeviceOrientation];
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

- (BOOL)isOrientationLandscapeWithScreenId:(int)screenId {
	for (int i = 0; i< group.screens.count; i++) {
		Screen *s = (Screen *)[group.screens objectAtIndex:i];
		if (s.screenId == screenId) {
			return s.landscape;
		}
	}
	return NO;
}

// Detect the current orientation of handset.
- (void)detectDeviceOrientation {
	[[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
	currentOrientation = [[UIDevice currentDevice] orientation];
	
	if (currentOrientation == UIDeviceOrientationUnknown) {
		currentOrientation = UIInterfaceOrientationPortrait;
		NSLog(@"it's using simulator, set portrait by default");
	}
	
	if (currentOrientation == UIDeviceOrientationFaceUp || 
			currentOrientation == UIDeviceOrientationFaceDown) {
		currentOrientation = UIInterfaceOrientationPortrait;
		NSLog(@"it's facing up/down, set portrait by default");
	}
}

- (UIInterfaceOrientation)getCurrentOrientation {
	return currentOrientation;
}

- (void)setNewOrientation:(UIInterfaceOrientation)newOrientation {
	[self willRotateToInterfaceOrientation:newOrientation duration:0];
}

// Check if current orientation is landscape.
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

- (BOOL)hasNoViewInThatOrientation:(BOOL)isLandscape {
	if (isLandscape) {
		return [group getLandscapeScreens].count == 0;
	} else {
		return [group getPortraitScreens].count == 0;
	}
	return NO;
}

- (int)groupId {
	return group.groupId;
}

// Initializes screenViewController array of groupController with screen model array.
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

// Get the paginationController of groupController.
- (PaginationController *)currentPaginationController {	
	return [self isOrientationLandscape] ? landscapePaginationController : portraitPaginationController;
}

// Show the view of specified orientation depending on the parameter isLandScape specified.
- (void)showLandscapeOrientation:(BOOL)isLandscape {
	
	NSArray *screens = isLandscape ? [group getLandscapeScreens] : [group getPortraitScreens];
	if (screens.count == 0) {
		[self showErrorView];
		return;
	}
	
	if (isLandscape) {
		if (landscapePaginationController == nil) {
			landscapePaginationController = [[PaginationController alloc] init];
			NSMutableArray *viewControllers = [self initScreenViewControllers:screens];
			[landscapePaginationController setViewControllers:viewControllers isLandscape:isLandscape];
			[viewControllers release];
		}
		[self setView:landscapePaginationController.view];
		[[portraitPaginationController currentScreenViewController] stopPolling];
		[[landscapePaginationController currentScreenViewController] startPolling];
	} else {
		if (portraitPaginationController == nil) {
			portraitPaginationController = [[PaginationController alloc] init];
			NSMutableArray *viewControllers = [self initScreenViewControllers:screens];
			[portraitPaginationController setViewControllers:viewControllers isLandscape:isLandscape];
			[viewControllers release];
		}
		[self setView:portraitPaginationController.view];
		[[landscapePaginationController currentScreenViewController] stopPolling];
		[[portraitPaginationController currentScreenViewController] startPolling];
	}
	
}

// Show portrait orientation view.
- (void)showPortrait {
	NSLog(@"show portrait");
	[self showLandscapeOrientation:NO];
}

// Show landscape orientation view.
- (void)showLandscape {
	NSLog(@"show landscape");
	[self showLandscapeOrientation:YES];
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	[super viewDidLoad];
	[self.navigationController setNavigationBarHidden:YES];
	if (UIInterfaceOrientationIsPortrait(currentOrientation)) {
		NSLog(@"view did load show portrait");
		[self showPortrait];
	} else if (UIInterfaceOrientationIsLandscape(currentOrientation)) {
		NSLog(@"view did load show landscape");
		[self showLandscape];
	} 
}

// Show error view if some error occured.
- (void)showErrorView {
	errorViewController = [[ErrorViewController alloc] initWithErrorTitle:@"No Screen Found" message:@"Please associate screens with this group of this orientation."];
	[errorViewController.view setFrame:[self getFullFrame]];
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

//only if the following conditions happen, should we rotate.
// 1) error view
// 2) device consistent orientation changes between up/down or left/right
// 3) current group has two-orientation (portrait/landscape) views 
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	if (errorViewController.view == self.view) {
		return YES;
	}
	if (UIInterfaceOrientationIsPortrait(currentOrientation) == UIInterfaceOrientationIsPortrait(interfaceOrientation)) {
		NSLog(@"group consistent orientation ");
		return YES;
	}
	if ([self currentScreenId] > 0) {
		NSLog(@"group opposite orientation ");
		return [[self currentScreen] inverseScreenId] > 0;
	} else {
		NSLog(@"group single orientation ");
		return ![self hasNoViewInThatOrientation:UIInterfaceOrientationIsLandscape(interfaceOrientation)];
	}
	
	return NO;
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
			NSLog(@"is unknown orientation, it's using simulator");
			break;
	}
}

- (void)transformToOrientation:(UIInterfaceOrientation)thatOrientation {
	currentOrientation = thatOrientation;
	if (UIInterfaceOrientationIsPortrait(thatOrientation)) {
		[self showPortrait];
	} else {
		[self showLandscape];
	}
}

// Override this method for enable view rotating.
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	[self printOrientation:currentOrientation];
	[self printOrientation:toInterfaceOrientation];
	if (UIInterfaceOrientationIsPortrait(toInterfaceOrientation) == UIInterfaceOrientationIsPortrait(currentOrientation)) {
		NSLog(@"same orientation");
		return;
	} else {
		NSLog(@"diff orientation");
		
		int inverseScreenId = -1;
		if ([self currentScreenId] > 0) {
			inverseScreenId = [self currentScreen].inverseScreenId;
		}
		NSLog(@"inverseScreenId=%d", inverseScreenId);
		if (inverseScreenId == 0) {
			return;
		}
		currentOrientation = toInterfaceOrientation;
		
		if (UIInterfaceOrientationIsPortrait(currentOrientation)) {
			[self showPortrait];
		} else {
			[self showLandscape];
		}
		
		if (inverseScreenId > 0) {
			NSLog(@"switch screen from %d - > %d", [self currentScreenId], inverseScreenId);
			[self switchToScreen:inverseScreenId];
		} 

	}
	[errorViewController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
}

- (void)dealloc {
	[landscapePaginationController release];
	[portraitPaginationController release];
	[errorViewController release];
	
	[super dealloc];
}


@end
