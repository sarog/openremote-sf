//
//  ORControllerGroupMembersFetchStatusIconProvider.h
//  openremote
//
//  Created by Eric Bariaux on 03/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ORController.h"

@interface ORControllerGroupMembersFetchStatusIconProvider : NSObject

+ (UIView *)viewForGroupMembersFetchStatus:(ORControllerGroupMembersFetchStatus)status;
                                            
@end
