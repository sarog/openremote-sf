//
//  NSURLHelper.h
//  openremote
//
//  Created by Eric Bariaux on 02/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSURLHelper : NSObject

+ (NSURL *)parseControllerURL:(NSString *)urlString;

@end
