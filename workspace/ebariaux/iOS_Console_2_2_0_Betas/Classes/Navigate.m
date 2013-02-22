/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "Navigate.h"

@interface Navigate ()

@property (nonatomic, readwrite) BOOL isPreviousScreen;
@property (nonatomic, readwrite) BOOL isNextScreen;
@property (nonatomic, readwrite) BOOL isBack;
@property (nonatomic, readwrite) BOOL isSetting;
@property (nonatomic, readwrite) BOOL isLogin;
@property (nonatomic, readwrite) BOOL isLogout;

@end

@implementation Navigate

- (id)initWithToScreen:(int)screenId toGroup:(int)groupId isPreviousScreen:(BOOL)isPreviousScreenFlag isNextScreen:(BOOL)isNextScreenFlag isSetting:(BOOL)isSettingFlag isBack:(BOOL)isBackFlag isLogin:(BOOL)isLoginFlag isLogout:(BOOL)isLogoutFlag
{
	if (self = [super init]) {
		self.toScreen = screenId;
        self.toGroup = groupId;
        self.isPreviousScreen = isPreviousScreenFlag;
        self.isNextScreen = isNextScreenFlag;
        self.isSetting = isSettingFlag;
        self.isBack = isBackFlag;
        self.isLogin = isLoginFlag;
        self.isLogout = isLogoutFlag;
	}
	return self;
}

@synthesize toScreen, toGroup, isPreviousScreen, isNextScreen, isSetting, isBack, isLogin, isLogout, fromGroup, fromScreen;

@end