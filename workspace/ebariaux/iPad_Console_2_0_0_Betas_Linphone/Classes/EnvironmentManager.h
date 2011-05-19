//
//  EnvironmentManager.h
//  openremote
//
//  Created by Eric Bariaux on 18/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface EnvironmentManager : NSObject {
    
}

+ (NSString *)getGraphURL:(NSMutableDictionary *)context;

@end
