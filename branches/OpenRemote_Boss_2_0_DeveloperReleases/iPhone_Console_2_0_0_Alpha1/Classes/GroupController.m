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



@implementation GroupController

@synthesize group;

- (id)initWithGroup:(Group *)newGroup {
	if (self = [super init]) {
		if (newGroup) {
			group = [newGroup retain];// must retain newGroup here!!!
			[self setTitle:group.name];
		}
		
		paginationController = [[PaginationController alloc] init];
				
	}
	return self;
}

- (int)groupId {
	return group.groupId;
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	[super viewDidLoad];
	[self.navigationController setNavigationBarHidden:YES];
	
	NSArray *screens = [group screens];
	if (screens.count > 0) {
		// Get array of screens
		// Build array of UIViewControllers for each screen
		NSMutableArray  *viewControllers = [[NSMutableArray alloc] init];
		
		for (Screen *screen in screens) {
			NSLog(@"screen = %@", screen.name);
			ScreenViewController *viewController = [[ScreenViewController alloc]init];
			[viewController setScreen:screen];
			[viewControllers addObject:viewController];
			[viewController release];
		}
		[paginationController setViewControllers:viewControllers];
		//[paginationController setTitle:[group name]];
		[self setView:paginationController.view];
		[viewControllers release];
	
	} else {
		errorViewController = [[ErrorViewController alloc] initWithErrorTitle:@"No Screen Found" message:@"Please associate screens with group or reset setting."];
		[self setView:errorViewController.view];	
	}
	//[paginationController release];	
	
}

- (void)startPolling {
	if (paginationController.viewControllers.count > 0) {
		ScreenViewController *svc = (ScreenViewController *)[paginationController.viewControllers objectAtIndex:0];
		[svc startPolling];
	}
}

- (void)stopPolling {
	for (ScreenViewController *svc in paginationController.viewControllers) {
		NSLog(@"stop polling %d",svc.screen.screenId);
		[svc stopPolling];
	}
}

- (BOOL)switchToScreen:(int)screenId {
	return [paginationController switchToScreen:screenId];
}

- (BOOL)previousScreen {
	return [paginationController previousScreen];
}

- (BOOL)nextScreen {
	return [paginationController nextScreen];
}

- (int)currentScreenId {
	return ((ScreenViewController *)[paginationController.viewControllers objectAtIndex:paginationController.selectedIndex]).screen.screenId;
}

- (void)performGesture:(Gesture *)gesture {
	return [(ScreenViewController *)[paginationController.viewControllers objectAtIndex:paginationController.selectedIndex] performGesture:gesture];
}

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

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
