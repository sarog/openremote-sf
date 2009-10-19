//
//  FileUtils.h
//  openremote
//
//  Created by finalist on 2/24/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

// DENNIS: Maybe you can add these helper methods as an Objective-C category to the NSFileManager class? (Make sure to use a precise and unique name for your methods.)

@interface FileUtils : NSObject {
}

+ (void) downloadFromURL:(NSString *) url  path:(NSString *)p;
+ (void)deleteFolderWithPath:(NSString *) path;
@end
