/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "GroupController.h"
#import "NotificationConstant.h"
#import "PaginationController.h"
#import "Screen.h"
#import "ScreenViewController.h"
#import "UIScreen+ORAdditions.h"
#import "UIImage+ORAdditions.h"

@interface GroupController ()

- (NSArray *)viewControllersForScreens:(NSArray *)screens;
- (void)showErrorView;
- (void)fixGeometryForInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation;

@property (assign) PaginationController *currentPaginationController;
@property (assign) UIViewController *parentViewController;

@property (retain) UIView *maskView;

@end

@implementation GroupController

- (id)initWithGroup:(Group *)newGroup parentViewController:(UIViewController *)aVC
{
    self = [super init];
	if (self) {
		self.group = newGroup;
        self.parentViewController = aVC;
	}
	return self;
}

- (void)dealloc
{
	[landscapePaginationController release];
	[portraitPaginationController release];
	[errorViewController release];
    self.group = nil;
    self.parentViewController = nil;
	self.maskView = nil;
	[super dealloc];
}

- (void)debugLogGeometry
{
    NSLog(@"========================================");
    NSLog(@"view hierarchy %@ %@ %@ %@", self.view, self.view.superview, self.view.superview.superview, self.view.superview.superview);
    OR_LogAffineTransform(@"View transform", self.view.transform);
    OR_LogPoint(@"View center", self.view.center);
    OR_LogRect(@"View bounds", self.view.bounds);
    OR_LogRect(@"View frame", self.view.frame);
    OR_LogAffineTransform(@"Superview transform", self.view.superview.transform);
    OR_LogPoint(@"Superview center", self.view.superview.center);
    OR_LogRect(@"Superview bounds", self.view.superview.bounds);
    OR_LogRect(@"Superview frame", self.view.superview.frame);
    OR_LogAffineTransform(@"Window transform", self.view.window.transform);
    OR_LogPoint(@"Window center", self.view.window.center);
    OR_LogRect(@"Window bounds", self.view.window.bounds);
    OR_LogRect(@"Window frame", self.view.window.frame);
    NSLog(@"========================================");
}


- (CGRect)getFullFrame
{
    return [UIScreen or_fullFrameForInterfaceOrientation:self.parentViewController.interfaceOrientation];
}

- (int)groupId {
	return self.group.groupId;
}

// Returns an array of ScreenViewControllers for the given Screen objects
- (NSArray *)viewControllersForScreens:(NSArray *)screens {
	NSMutableArray *viewControllers = [NSMutableArray arrayWithCapacity:[screens count]];
	
	for (Screen *screen in screens) {
		NSLog(@"init screen = %@", screen.name);
		ScreenViewController *viewController = [[ScreenViewController alloc]init];
		[viewController setScreen:screen];
		[viewControllers addObject:viewController];
		[viewController release];
	}
	return [NSArray arrayWithArray:viewControllers];
}

// Show the view of specified orientation depending on the parameter isLandScape specified.
- (void)showLandscapeOrientation:(BOOL)isLandscape {
	
	NSArray *screens = isLandscape ? [self.group getLandscapeScreens] : [self.group getPortraitScreens];

	if (screens.count == 0) {
        // Check if other orientation has screens
        isLandscape = !isLandscape;
        screens = isLandscape ? [self.group getLandscapeScreens] : [self.group getPortraitScreens];
        if ([screens count] == 0) {
            // Still no luck, error
            [self showErrorView];
            return;
        }
	}
	
	if (isLandscape) {
		if (landscapePaginationController == nil) {
			landscapePaginationController = [[PaginationController alloc] initWithGroup:self.group];
			[landscapePaginationController setViewControllers:[self viewControllersForScreens:screens] isLandscape:isLandscape];
		}
        self.currentPaginationController = landscapePaginationController;
		[self setView:landscapePaginationController.view];

        // By setting view above, on 1st time, view bounds got reset to "portrait". Force it back to "landscape"
        self.view.bounds = [UIScreen or_fullFrameForInterfaceOrientation:UIInterfaceOrientationLandscapeLeft];
        
		[[portraitPaginationController currentScreenViewController] stopPolling];
		[[landscapePaginationController currentScreenViewController] startPolling];
	} else {
		if (portraitPaginationController == nil) {
			portraitPaginationController = [[PaginationController alloc] initWithGroup:self.group];
			[portraitPaginationController setViewControllers:[self viewControllersForScreens:screens] isLandscape:isLandscape];
		}
        self.currentPaginationController = portraitPaginationController;
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

- (void)viewDidLoad {
	[super viewDidLoad];
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
	[self.navigationController setNavigationBarHidden:YES];

    if (UIInterfaceOrientationIsLandscape(self.parentViewController.interfaceOrientation)) {
		NSLog(@"view did load show landscape");
		[self showLandscape];
    } else {
		NSLog(@"view did load show portrait");
		[self showPortrait];
	}
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self fixGeometryForInterfaceOrientation:self.parentViewController.interfaceOrientation];
}

- (void)viewDidUnload
{
    [[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
    [super viewDidUnload];
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	if (errorViewController.view == self.view) {
		return YES;
	}
    return NO;
}

- (void)fixGeometryForInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    CGAffineTransform myTransform = CGAffineTransformIdentity;
    switch (interfaceOrientation) {
        case UIInterfaceOrientationPortrait:
            if ([self currentScreen].landscape) myTransform = CGAffineTransformMakeRotation(-M_PI_2);
            break;
        case UIInterfaceOrientationPortraitUpsideDown:
            if ([self currentScreen].landscape) myTransform = CGAffineTransformMakeRotation(M_PI_2);
            break;
        case UIInterfaceOrientationLandscapeLeft:
            if (![self currentScreen].landscape) myTransform = CGAffineTransformMakeRotation(M_PI_2);
            break;
        case UIInterfaceOrientationLandscapeRight:
            if (![self currentScreen].landscape) myTransform = CGAffineTransformMakeRotation(-M_PI_2);
            break;
        default:
            break;
    }
    
    self.view.transform = myTransform;
    self.view.bounds = [UIScreen or_fullFrameForLandscapeOrientation:[self currentScreen].landscape];
    self.view.center = UIInterfaceOrientationIsPortrait(interfaceOrientation)?self.view.superview.center:CGPointMake(self.view.superview.center.y, self.view.superview.center.x);    
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    UIImageView *tmpView = [[UIImageView alloc] initWithImage:[UIImage or_screenshotForWindow:self.view.window]];
    self.maskView = tmpView;
    [tmpView release];
    [self.view.window addSubview:self.maskView];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    if (UIInterfaceOrientationIsLandscape(self.parentViewController.interfaceOrientation)) {
        if (![self currentScreen].landscape) {
            // Going to a landscape version and not currently showing a landscape screen
            int inverseScreenId = [self currentScreen].inverseScreenId;
            // If there is a landscape version of the screen, install that one
            if (inverseScreenId != 0) {
                [self showLandscape];
                [[self currentPaginationController] switchToScreen:inverseScreenId];
            }
        }        
    } else {
        if ([self currentScreen].landscape) {
            // Going to portrait and not currently showing a portrait screen
            int inverseScreenId = [self currentScreen].inverseScreenId;
            if (inverseScreenId != 0) {
                // If there is a portrait version of the screen, install that one
                [self showPortrait];
                [[self currentPaginationController] switchToScreen:inverseScreenId];
            }
        }
    }
    [self fixGeometryForInterfaceOrientation:self.parentViewController.interfaceOrientation];
    [self.maskView removeFromSuperview];
}

@synthesize group;
@synthesize currentPaginationController;
@synthesize parentViewController;
@synthesize maskView;

@end