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
#import "GroupController.h"
#import "NotificationConstant.h"
#import "PaginationController.h"
#import "Screen.h"
#import "ScreenViewController.h"
#import "UIDevice+ORAdditions.h"

@interface GroupController ()

- (NSArray *)viewControllersForScreens:(NSArray *)screens;
- (void)showErrorView;

@property (assign) PaginationController *currentPaginationController;

@end

@implementation GroupController

- (id)initWithGroup:(Group *)newGroup
{
    self = [super init];
	if (self) {
		self.group = newGroup;
	}
	return self;
}

- (void)dealloc
{
	[landscapePaginationController release];
	[portraitPaginationController release];
	[errorViewController release];
    self.group = nil;
	
	[super dealloc];
}

- (CGRect)getFullFrame {
	CGRect frame = self.view.frame;
	CGSize size = [UIScreen mainScreen].bounds.size;
	BOOL isLandscape = [UIDevice or_isDeviceOrientationLandscape];
	frame.size.height = isLandscape ? size.width : size.height;
	frame.size.width = isLandscape ? size.height : size.width;
	return frame;
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
		[self showErrorView];
		return;
	}
	
	if (isLandscape) {
		if (landscapePaginationController == nil) {
			landscapePaginationController = [[PaginationController alloc] init];
			[landscapePaginationController setViewControllers:[self viewControllersForScreens:screens] isLandscape:isLandscape];
		}
        self.currentPaginationController = landscapePaginationController;
		[self setView:landscapePaginationController.view];
		[[portraitPaginationController currentScreenViewController] stopPolling];
		[[landscapePaginationController currentScreenViewController] startPolling];
	} else {
		if (portraitPaginationController == nil) {
			portraitPaginationController = [[PaginationController alloc] init];
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
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(orientationChanged:)
                                                 name:UIDeviceOrientationDidChangeNotification
                                               object:nil];        

    
    if ([UIDevice or_isDeviceOrientationLandscape]) {
		NSLog(@"view did load show landscape");
		[self showLandscape];
    } else {
		NSLog(@"view did load show portrait");
		[self showPortrait];
	}
}

- (void)viewDidUnload
{
    [[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
    [super viewDidUnload];
}

- (void)setupRotation:(UIDeviceOrientation)orientation
{
    CGAffineTransform myTransform = CGAffineTransformIdentity;
    switch (orientation) {
        case UIDeviceOrientationPortraitUpsideDown:
            myTransform = CGAffineTransformMakeRotation(M_PI);
            break;
        case UIDeviceOrientationLandscapeLeft:
            myTransform = CGAffineTransformMakeRotation(M_PI / 2);
            break;
        case UIDeviceOrientationLandscapeRight:
            myTransform = CGAffineTransformMakeRotation(-M_PI /2);
            break;
        default:
            break;
    }
    
    self.view.transform = myTransform;
    self.view.bounds = [self getFullFrame];
}

- (void)orientationChanged:(NSNotification *)notification
{
    UIDeviceOrientation deviceOrientation = [UIDevice currentDevice].orientation;
    
    NSLog(@"Going to device orientation %d", deviceOrientation);
    
    
    if ([UIDevice or_isDeviceOrientationLandscape:deviceOrientation]) {
        if ([self currentScreen].landscape) {
            // Orientation matches, can set the transform
            [self setupRotation:deviceOrientation];
        } else {
            int inverseScreenId = [self currentScreen].inverseScreenId;
            if (inverseScreenId != 0) {
                [self showLandscape];
                [self setupRotation:deviceOrientation];
                [[self currentPaginationController] switchToScreen:inverseScreenId];
            }
        }        
    } else {
        if (![self currentScreen].landscape) {
            // Orientation matches, can set the transform
            [self setupRotation:deviceOrientation];
        } else {
            int inverseScreenId = [self currentScreen].inverseScreenId;
            if (inverseScreenId != 0) {
                [self showPortrait];
                [self setupRotation:deviceOrientation];
                [[self currentPaginationController] switchToScreen:inverseScreenId];
            }
        }
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	if (errorViewController.view == self.view) {
		return YES;
	}
    return interfaceOrientation == UIInterfaceOrientationPortrait;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	[errorViewController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
}

@synthesize group;
@synthesize currentPaginationController;

@end