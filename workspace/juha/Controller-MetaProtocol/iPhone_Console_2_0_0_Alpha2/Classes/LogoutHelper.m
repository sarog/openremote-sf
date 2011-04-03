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

#import "LogoutHelper.h"
#import "URLConnectionHelper.h"
#import "Definition.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "DataBaseService.h"
#import "ViewHelper.h"
#import "ControllerException.h"

@interface LogoutHelper (Private)



@end


@implementation LogoutHelper


- (void)requestLogout {
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition logoutUrl]];
	NSURL *url = [[NSURL alloc]initWithString:location];
	
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
	
}

- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		switch (statusCode) {
			case UNAUTHORIZED://logout succuessful 
				NSLog(@"%@ logged out successfully", [Definition sharedDefinition].username);
				[ViewHelper showAlertViewWithTitle:@"" Message:[NSString stringWithFormat:@"%@ logged out successfully", [Definition sharedDefinition].username]];
				[Definition sharedDefinition].password = nil;
				DataBaseService *dbService = [DataBaseService sharedDataBaseService];			
				[dbService deleteAllUsers];
				return;
		} 
		
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	

	} 
	
}


#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {

}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}


@end
