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


#import "InitViewController.h"
#import "NotificationConstant.h"
#import "AppSettingController.h"
#import "DirectoryDefinition.h"

@interface InitViewController (Private)

@end

@implementation InitViewController

@synthesize label;

- (id)init {
	if (self = [super  initWithNibName:@"InitViewController" bundle:nil]) {
		
		label = [[UILabel alloc] initWithFrame:CGRectMake(0, 430, 320, 20)];
		[label setText:@"loading ... please wait."];
		[label setBackgroundColor:[UIColor clearColor]];
		[label setTextColor:[UIColor darkTextColor]];
		[label setFont:[UIFont boldSystemFontOfSize:14]];
		[label setTextAlignment:UITextAlignmentCenter];
		//[label setShadowColor:[UIColor blackColor]];
		//	[label setShadowColor:[UIColor lightGrayColor]];
		//	[label setShadowOffset:CGSizeMake(1,1)];
		loadding = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
		[loadding sizeToFit];
		[loadding setFrame:CGRectMake(140,390,30,30)];
		
		version = [[UILabel alloc] initWithFrame:CGRectMake(140, 300, 30, 20)];
		NSLog(@"version is %@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]);
		version.text = [NSString stringWithFormat:@"v%@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
		[version setBackgroundColor:[UIColor clearColor]];
		[version setTextColor:[UIColor blackColor]];
		[version setFont:[UIFont boldSystemFontOfSize:14]];
		[version setTextAlignment:UITextAlignmentCenter];
		//[version setShadowColor:[UIColor grayColor]];
		
		
		[self.view addSubview:loadding];
		[self.view  addSubview:label];
		[self.view  addSubview:version];
		[loadding startAnimating];
	}
	return self;
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	[loadding release];
	[label release];
	[version release];
    [super dealloc];
}


@end
