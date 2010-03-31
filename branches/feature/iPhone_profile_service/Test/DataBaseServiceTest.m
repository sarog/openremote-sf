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

#import "DataBaseServiceTest.h"
#import "DataBaseService.h"
#import "User.h"
#import "GroupMember.h"

@interface DataBaseTest (Private)
- (void) insertUser;
- (void) removeAllUsers;
@end


@implementation DataBaseTest

#if USE_APPLICATION_UNIT_TEST     // all code under test is in the iPhone Application

- (void) testAppDelegate {
    
    id yourApplicationDelegate = [[UIApplication sharedApplication] delegate];
    STAssertNotNil(yourApplicationDelegate, @"UIApplication failed to find the AppDelegate");
    
}

#else                           // all code under test must be linked into the Unit Test bundle

/* The setUp method is called automatically before each test-case method (methods whose name starts with 'test').
 */
- (void) setUp {
	if (!dbService) {
		dbService = [DataBaseService sharedDataBaseServiceForTest];
	}
	[self removeAllUsers];
	[self insertUser];
	NSLog(@"%@ setUp", self.name);
}

/* The tearDown method is called automatically after each test-case method (methods whose name starts with 'test').
 */
- (void) tearDown {
	[self removeAllUsers];
	NSLog(@"%@ tearDown", self.name);
}

- (void) testFindLastLoginUser {
	User *user = [dbService findLastLoginUser];
	STAssertNotNil(user, @"", @"");
	STAssertTrue([user.username isEqualToString:@"handy"],@"but %@", user.username);
	STAssertTrue([user.password isEqualToString:@"2222222"],@"but %@", user.password);	
	[user release];
}

- (void) testFindUserByUsername {
	User *user = [dbService findUserByUsername:@"handy"];
	STAssertNotNil(user, @"", @"");
	STAssertTrue([user.username isEqualToString:@"handy"],@"but %@", user.username);
	STAssertTrue([user.password isEqualToString:@"2222222"],@"but %@", user.password);	
	[user release];
}

- (void) insertUser {
	User *user = [[User alloc] initWithUsernameAndPassword:@"handy" password:@"2222222"];
	[dbService insertUser:user];
	user = [dbService findUserByUsername:@"handy"];
	STAssertTrue([user.username isEqualToString:@"handy"],@"but %@", user.username);
	STAssertTrue([user.password isEqualToString:@"2222222"],@"but %@", user.password);	
	[user release];
}

- (void) removeAllUsers {
	[dbService deleteAllUsers];
	User *user = [dbService findLastLoginUser];
	STAssertNil(user, @"", @"");
	[user release];
}

- (void) testFindAllGroupMembers {
	GroupMember *groupMember = [[GroupMember alloc] initWithUrl:@"http://192.168.1.101/controller/"];
	[dbService insertGroupMember:groupMember];
	NSMutableArray *groupMembers = [dbService findAllGroupMembers];
	STAssertTrue(groupMembers.count > 0, @"expected size great than 0, but size is %d", groupMembers.count);
	[dbService deleteAllGroupMembers];
}

- (void) testDeleteAllGroupMembers {
	[dbService deleteAllGroupMembers];
	NSMutableArray *groupMembers = [dbService findAllGroupMembers];
	STAssertTrue(groupMembers.count == 0, @"expected size great than 0, but size is %d", groupMembers.count);
	
}

- (void) testInsertGroupMember {
	[dbService deleteAllGroupMembers];
	GroupMember *groupMember = [[GroupMember alloc] initWithUrl:@"http://192.168.1.102/controller/"];
	[dbService insertGroupMember:groupMember];	
	NSMutableArray *groupMembers = [dbService findAllGroupMembers];
	STAssertTrue(groupMembers.count > 0, @"expected size great than 0, but size is %d", groupMembers.count);
	[dbService deleteAllGroupMembers];
}


#endif
@end
