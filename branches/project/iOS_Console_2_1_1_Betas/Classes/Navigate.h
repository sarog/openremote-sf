/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import <Foundation/Foundation.h>

@interface Navigate : NSObject

@property (nonatomic, readwrite, assign) int toScreen;
@property (nonatomic, readwrite, assign) int toGroup;
@property (nonatomic, readonly) BOOL isPreviousScreen;
@property (nonatomic, readonly) BOOL isNextScreen;
@property (nonatomic, readonly) BOOL isBack;
@property (nonatomic, readonly) BOOL isSetting;
@property (nonatomic, readonly) BOOL isLogin;
@property (nonatomic, readonly) BOOL isLogout;
@property (nonatomic, readwrite, assign) int fromGroup;
@property (nonatomic, readwrite, assign) int fromScreen;

- (id)initWithToScreen:(int)screenId toGroup:(int)groupId isPreviousScreen:(BOOL)isPreviousScreenFlag isNextScreen:(BOOL)isNextScreenFlag isSetting:(BOOL)isSettingFlag isBack:(BOOL)isBackFlag isLogin:(BOOL)isLoginFlag isLogout:(BOOL)isLogoutFlag;

@end
