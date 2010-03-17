/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


#import <UIKit/UIKit.h>
#import "Screen.h"
#import "Group.h"
#import "Tabbar.h"

#define TWICE 2

@interface Definition : NSObject {		
	BOOL isUpdating;
	NSDate *lastUpdateTime;
	NSMutableArray *groups;
	NSMutableArray *screens;
	NSMutableArray *labels;
	TabBar *tabBar;
	NSMutableArray *imageNames;
	NSInvocationOperation *updateOperation;
	NSOperationQueue *updateOperationQueue; 
	UILabel *loading;
	NSString *username;
	NSString *password;
}

+ (Definition *)sharedDefinition;
- (void)update;
- (BOOL)isDataReady;
- (void)useLocalCacheDirectly;
- (void)clearPanelXMLData;
- (void)addImageName:(NSString *)imageName;
- (Group *)findGroupById:(int)groupId;
- (Screen *)findScreenById:(int)screenId;
- (void)addGroup:(Group *)group;
- (void)addScreen:(Screen *)screen;
- (void) addLabel:(Label *)label;
- (Label *)findLabelById:(int)labelId;

@property (nonatomic,readonly) BOOL isUpdating;
@property (nonatomic,readonly) NSDate *lastUpdateTime;
@property (nonatomic,readonly) NSMutableArray *groups;
@property (nonatomic,readonly) NSMutableArray *screens;
@property (nonatomic,retain) NSMutableArray *labels;
@property (nonatomic,retain) TabBar *tabBar;
@property (nonatomic,readonly) NSMutableArray *imageNames;
@property (nonatomic,retain) UILabel *loading;
@property	(nonatomic,copy) NSString *username;
@property	(nonatomic,copy) NSString *password;

@end
