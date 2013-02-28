//
//  ServerDefinition.h
//  openremote
//
//  Created by finalist on 2/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ServerDefinition : NSObject {
	
}

+ (NSString *)sampleXmlUrl;
+ (NSString *)imageUrl;
+ (NSString *)eventHandleRESTUrl;
+ (NSString *)serverUrl;
+ (void)registerDefaultsFromSettingsBundle;
@end
