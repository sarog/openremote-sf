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

#import <Foundation/Foundation.h>
#import	"User.h"
#import "sqlite3.h"
#import "GroupMember.h"


@interface DataBaseService : NSObject {
	sqlite3 *openDatabase;
}

// Class method for get singleton instance.
+ (DataBaseService *)sharedDataBaseService;

// Class method for get singleton instance in unit test environment.
+ (DataBaseService *)sharedDataBaseServiceForTest;

// Find the loast logined user from user table.
- (User *) findLastLoginUser;

// Find a user with the primary key username from Users table.
- (User *) findUserByUsername:(NSString *)username;

// Insert a new user into user table.
- (void) insertUser:(User*)user;

// Clean the Users table data.
- (void) deleteAllUsers;

- (void) saveCurrentUser;

- (void) initLastLoginUser;

- (void) insertGroupMember:(GroupMember *)groupMember;

- (NSMutableArray *) findAllGroupMembers;

- (void) deleteAllGroupMembers;

@property (nonatomic, readwrite) sqlite3 *openDatabase;

@end
