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
#import "ORWebView.h"
#import "Web.h"
#import "NSStringAdditions.h"
#import "PollingStatusParserDelegate.h"
#import "Web.h"

@interface ORWebView ()

@property (nonatomic, retain) NSString *oldStatus;

- (void)loadRequestForURL:(NSString *)url;

@end

@implementation ORWebView

@synthesize defaultWebView;
@synthesize oldStatus;

- (void) initView
{
	Web *webModel = (Web *)self.component;

	defaultWebView = [[UIWebView alloc] initWithFrame:self.bounds];
	
    [self loadRequestForURL:webModel.src];
	[self addSubview:defaultWebView];
}

- (void)dealloc
{
    [defaultWebView release];
    self.oldStatus = nil;
    [super dealloc];
}

- (void)loadRequestForURL:(NSString *)url
{
	Web *webModel = (Web *)self.component;
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
	
	// If a username if provided in the config, use that for authentication
	if (webModel.username != nil && ![@"" isEqualToString:webModel.username]) {
		NSData *authData = [[NSString stringWithFormat:@"%@:%@", webModel.username, webModel.password] dataUsingEncoding:NSUTF8StringEncoding];
		NSString *authString = [NSString base64StringFromData:authData length:[authData length]];
		authString = [NSString stringWithFormat: @"Basic %@", authString];
		[request setValue:authString forHTTPHeaderField:@"Authorization"];
	}
	
	[defaultWebView loadRequest:request];    
}

- (void)setPollingStatus:(NSNotification *)notification
{    
    // TODO EBR : check / test, it seems this method is called multiple times for a single sensor update
    
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Web *)self.component).sensor.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
//	NSLog(@"new status is : %@", newStatus);
    if (![self.oldStatus isEqualToString:newStatus]) {
        self.oldStatus = newStatus;
        [self loadRequestForURL:newStatus];
    }
}

@end
