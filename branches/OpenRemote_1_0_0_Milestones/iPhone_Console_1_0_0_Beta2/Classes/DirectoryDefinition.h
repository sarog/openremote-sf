//
//  DirectoryDefinition.h
//  openremote
//
//  Created by finalist on 2/24/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface DirectoryDefinition : NSObject {

}

+ (NSString *)cacheFolder;
+ (NSString *)imageCacheFolder;
+ (NSString *)xmlCacheFolder;

@end
