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

#import "InitView.h"

@implementation InitView


- (id)initWithOrientation:(BOOL)isLandscapeOrientation {
    if (self = [super initWithFrame:[self bounds]]) {
			isLandscape = isLandscapeOrientation;			
    }
    return self;
}

- (void)layoutSubviews {
	background = [[UIImageView alloc] init];
	[background setImage:[UIImage imageNamed:@"loading.png"]];
	[background setFrame:self.bounds];
	logo = [[UIImageView alloc] init];
	[logo setImage:[UIImage imageNamed:@"global.logo.png"]];
	label = [[UILabel alloc] init];
	[Definition sharedDefinition].loading = label;
	[label setText:@"loading ... please wait."];
	[label setBackgroundColor:[UIColor clearColor]];
	[label setTextColor:[UIColor darkTextColor]];
	[label setFont:[UIFont boldSystemFontOfSize:14]];
	[label setTextAlignment:UITextAlignmentCenter];
	//[label setShadowColor:[UIColor blackColor]];
	//	[label setShadowColor:[UIColor lightGrayColor]];
	//	[label setShadowOffset:CGSizeMake(1,1)];
	
	version = [[UILabel alloc] init];
	NSLog(@"version is %@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]);
	version.text = [NSString stringWithFormat:@"v%@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
	[version setBackgroundColor:[UIColor clearColor]];
	[version setTextColor:[UIColor blackColor]];
	[version setFont:[UIFont boldSystemFontOfSize:14]];
	[version setTextAlignment:UITextAlignmentCenter];
	//[version setShadowColor:[UIColor grayColor]];
	loading = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
	
	[self addSubview:background];
	[self addSubview:logo];
	[self addSubview:loading];
	[self addSubview:label];
	[self addSubview:version];
	
	[logo setFrame:CGRectMake(isLandscape ? 129 : 49, isLandscape ? 100 : 184, 222, 33)];
	[version setFrame:CGRectMake(0, isLandscape ? 150 : 320, self.frame.size.width, 20)];
	[loading setFrame:CGRectMake((self.frame.size.width - 30)/2, isLandscape ? 250 : 410, 30, 30)];
	[loading sizeToFit];
	[loading startAnimating];
	
	[label setFrame:CGRectMake(0, isLandscape ? 300 : 450, self.frame.size.width, 20)];
	
}


- (void)dealloc {
	[background release];
	[logo release];
	[loading release];
	[label release];
	[version release];
	
	[super dealloc];
}


@end
