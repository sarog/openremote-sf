//
//  AppSettingsDefinition.h
//  openremote
//
//  Created by finalist on 5/15/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ServerAutoDiscoveryController.h"



@interface AppSettingsDefinition : NSObject {

}
+ (void)reloadData;
+ (NSMutableArray *)getAppSettings;
+(NSMutableDictionary *)getSectionWithIndex:(int)index;
+ (NSString *)getSectionHeaderWithIndex:(int)index;
+ (NSString *)getSectionFooterWithIndex:(int)index;
+ (NSMutableDictionary *)getAutoDiscoveryDic;
+ (BOOL)isAutoDiscoveryEnable;
+ (void)setAutoDiscovery:(BOOL)on;
+ (NSMutableArray *)getAutoServers;
+ (NSMutableArray *)getCustomServers;
+ (NSString *)getCurrentServerUrl;
+ (BOOL)readServerUrlFromFile;
+ (void)setCurrentServerUrl:(NSString *)url;
+ (void)addAutoServer:(NSDictionary *)server;
+ (void)writeToFile;
+ (void)removeAllAutoServer;
+ (void)writeToFile;

@end
