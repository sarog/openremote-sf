//
//  CheckNetworkStaffException.m
//  openremote
//
//  Created by finalist on 5/31/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "CheckNetworkStaffException.h"


@implementation CheckNetworkStaffException

@synthesize title,message;

+(CheckNetworkStaffException *)exceptionWithTitle:(NSString *)t message:(NSString *)msg {
	CheckNetworkStaffException *e = [[CheckNetworkStaffException alloc] initWithName:@"checkNetworkStaffException" reason:@"Check Network Fail" userInfo:nil];
	e.title = t;
	e.message = msg;
	[e autorelease];
	return e;
}
@end
