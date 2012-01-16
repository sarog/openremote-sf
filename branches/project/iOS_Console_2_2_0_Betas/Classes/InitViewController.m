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
#import "InitViewController.h"
#import "Definition.h"

@implementation InitViewController

- (id)init {
	int isIPad = [UIScreen mainScreen].bounds.size.width == 768;
	if (self = [super  initWithNibName:isIPad ? @"InitViewController~iPad" : @"InitViewController~iPhone" bundle:nil]) {
		
	}
	return self;
}

- (void)viewDidLoad {
	NSString *v = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"];
	NSLog(@"version is %@", v);
	version.text = [NSString stringWithFormat:@"v %@", v];
	//[[Definition sharedDefinition] setLoading:label];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}

- (void)dealloc {
	[label release];
	[version release];
	
	[super dealloc];
}


@end
