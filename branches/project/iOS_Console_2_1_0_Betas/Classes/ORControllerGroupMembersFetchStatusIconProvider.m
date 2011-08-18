//
//  ORControllerGroupMembersFetchStatusIconProvider.m
//  openremote
//
//  Created by Eric Bariaux on 03/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerGroupMembersFetchStatusIconProvider.h"

@implementation ORControllerGroupMembersFetchStatusIconProvider

+ (UIView *)viewForGroupMembersFetchStatus:(ORControllerGroupMembersFetchStatus)status;
{
    switch (status) {
        case GroupMembersFetching:
        {
            UIActivityIndicatorView *aiv = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            [aiv startAnimating];
            return [aiv autorelease];
        }
        case GroupMembersFetchSucceeded:
        {
            return [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ControllerOK"]] autorelease];
        }
        case GroupMembersFetchFailed:
        {
            return [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ControllerNOK"]] autorelease];
        }
        case GroupMembersFetchRequiresAuthentication:
        {
            return [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ControllerRequiresAuthentication"]] autorelease];
        }
        default:
            return nil;
    }
}

@end
