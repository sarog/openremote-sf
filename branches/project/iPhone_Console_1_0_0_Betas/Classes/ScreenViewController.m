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


#import "ScreenViewController.h"
#import "ScreenView.h"


@implementation ScreenViewController

@synthesize screen;

//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
//    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
//        // Custom initialization
//    }
//    return self;
//}

- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	
	[self setTitle:screen.name];
	
}

// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	ScreenView *view = [[ScreenView alloc] init];
	
	//set Screen in ScreeenView
	[view setScreen:screen];
	[self setView:view];
	[view release];
}


/*
// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/



@end
