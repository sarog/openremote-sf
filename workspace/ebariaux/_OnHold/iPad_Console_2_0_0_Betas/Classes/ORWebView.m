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

#import "ORWebView.h"
#import "Web.h"
#import "NSStringAdditions.h"

@implementation ORWebView

@synthesize defaultWebView;

- (void) initView {
	Web *webModel = (Web *)component;

	defaultWebView = [[UIWebView alloc] initWithFrame:self.bounds];
	
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:webModel.src]];
	
	// If a username if provided in the config, use that for authentication
	if (webModel.username != nil && ![@"" isEqualToString:webModel.username]) {
		NSData *authData = [[NSString stringWithFormat:@"%@:%@", webModel.username, webModel.password] dataUsingEncoding:NSUTF8StringEncoding];
		NSString *authString = [NSString base64StringFromData:authData length:[authData length]];
		authString = [NSString stringWithFormat: @"Basic %@", authString];
		[request setValue:authString forHTTPHeaderField:@"Authorization"];
	}
	
	[defaultWebView loadRequest:request];
	[self addSubview:defaultWebView];
}

@end
