//
//  ViewHelper.m
//  openremote
//
//  Created by finalist on 4/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ViewHelper.h"


@implementation ViewHelper

+(void) showAlertViewWithTitle:(NSString *)title Message:(NSString *)message  {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert autorelease];
}
@end
