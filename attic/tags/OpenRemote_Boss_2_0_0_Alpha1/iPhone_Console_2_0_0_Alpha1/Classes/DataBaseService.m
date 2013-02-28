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

#import "DataBaseService.h"
#import "ViewHelper.h"
#import "Definition.h"

@interface DataBaseService (Private)
// Init DatabaseService with database file path.
- (id) initWithDatabasePath:(NSString *)databasePath;
@end

static DataBaseService *myInstance = nil;

@implementation DataBaseService

@synthesize openDatabase;

// Init DatabaseService with database file path.
- (id) initWithDatabasePath:(NSString *)databasePath {
	if (myInstance != nil) {
		[self release];
		[NSException raise:@"singletonClassError" format:@" Don't init singleton class DataBaseService."];
	} else if (self = [super init]) {
		myInstance = self;
		if(sqlite3_open([databasePath UTF8String], &openDatabase) !=SQLITE_OK) {
			NSLog(0, @"Failed to open database. %@", sqlite3_errmsg(openDatabase));
		}
	}
	return myInstance;
}

// Class method for get singleton instance.
+ (DataBaseService *) sharedDataBaseService {
	NSString *dbFilePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"database.db"];
	@synchronized (self) {
		if (myInstance == nil) {
			NSFileManager *fileManager = [NSFileManager defaultManager];
			BOOL success = [fileManager fileExistsAtPath:dbFilePath];			
			if(!success) {				
				[ViewHelper showAlertViewWithTitle:@"" Message:@"Couldn't find the database file."];
				return nil;
			}
			[[DataBaseService alloc] initWithDatabasePath:dbFilePath];
		}
	}
	return myInstance;
}

+ (DataBaseService *) sharedDataBaseServiceForTest {
	NSString *dbFilePath = @"database.db";
	@synchronized (self) {
		if (myInstance == nil) {
			NSFileManager *fileManager = [NSFileManager defaultManager];
			BOOL success = [fileManager fileExistsAtPath:dbFilePath];			
			if(!success) {				
				[ViewHelper showAlertViewWithTitle:@"" Message:@"Couldn't find the database file."];
				return nil;
			}
			[[DataBaseService alloc] initWithDatabasePath:dbFilePath];
		}
	}
	return myInstance;
}

// Find the loast logined user from user table.
- (User *) findLastLoginUser {	
	NSMutableArray *users = [[NSMutableArray alloc] init];
	const char *sqlStatement = "select * from users";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		while (sqlite3_step(compiledStatement) == SQLITE_ROW) {
			User *user;
			const unsigned char *usernameC = sqlite3_column_text(compiledStatement, 0);
			const unsigned char *passwordC = sqlite3_column_text(compiledStatement, 1);
			if (usernameC == NULL || passwordC == NULL) {
				user = [[User alloc] initWithUsernameAndPassword:nil password:nil];
			} else {
				NSString *usernameResult = [NSString stringWithUTF8String:(char *)usernameC];
				NSString *passwordResult = [NSString stringWithUTF8String:(char *)passwordC];
				user = [[User alloc] initWithUsernameAndPassword:usernameResult password:passwordResult];
			}
			[users addObject:user];				
			[user release];
		}
	} else {
		NSLog(0, @"Prepare sqlStatement error: %@", sqlite3_errmsg(openDatabase));
	}
	sqlite3_reset(compiledStatement);
	sqlite3_finalize(compiledStatement);
	if ([users count] >= 1) {
		return [users objectAtIndex:0];
	} else {
		return nil;
	}
	
	
}

// Find a user with the primary key username from Users table.
- (User *) findUserByUsername:(NSString *)username {
	User *user;
	const char *sqlStatement = "select username, password from users where username =@username";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		sqlite3_bind_text(compiledStatement, 1, [username UTF8String], -1, SQLITE_TRANSIENT);
		if (sqlite3_step(compiledStatement) == SQLITE_ROW) {
			NSString *usernameResult = [NSString stringWithUTF8String:(char *)sqlite3_column_text(compiledStatement, 0)];
			NSString *passwordResult = [NSString stringWithUTF8String:(char *)sqlite3_column_text(compiledStatement, 1)];
			NSLog(@"username: %@, password: %@", usernameResult, passwordResult);
			user = [[User alloc] initWithUsernameAndPassword:usernameResult password:passwordResult];
		} else {
			user = nil;
			NSLog(0, @"Error while findUserByUsername. '%s'", sqlite3_errmsg(openDatabase));
		}
		sqlite3_reset(compiledStatement);
		sqlite3_finalize(compiledStatement);
	}
	return user;
}

// Insert a new user into users table.
- (void) insertUser:(User*)user {
	const char *sqlStatement = "insert into users values(@username,@password)";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		sqlite3_bind_text(compiledStatement, 1, [user.username UTF8String], -1, SQLITE_TRANSIENT);
		sqlite3_bind_text(compiledStatement, 2, [user.password UTF8String], -1, SQLITE_TRANSIENT);
		if(SQLITE_DONE != sqlite3_step(compiledStatement)) {
			NSLog(0, @"Error while inserting user. '%s'", sqlite3_errmsg(openDatabase));
		} else {
			//SQLite provides a method to get the last primary key inserted by using sqlite3_last_insert_rowid
			//NSString *lastPrimayKeyInUsersTable = sqlite3_last_insert_rowid(openDatabase);
			//NSLog(@"Last primary key is : %@", lastPrimayKeyInUsersTable);
		}
		sqlite3_reset(compiledStatement);
	}
	sqlite3_finalize(compiledStatement);
}

// Clean the Users table data.
- (void) deleteAllUsers {
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	[userDefaults setObject:nil	forKey:@"password"];
//	const char *sqlStatement = "delete from users";
//	sqlite3_stmt *compiledStatement;
//	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
//		if(SQLITE_DONE != sqlite3_step(compiledStatement)) {
//			//NSLog(@"Error while deleteAllUsers. '%s'", sqlite3_errmsg(openDatabase));
//		}
//	}
//	sqlite3_reset(compiledStatement);
//	sqlite3_finalize(compiledStatement);
}

- (void) saveCurrentUser{
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	[userDefaults setObject:[Definition sharedDefinition].username forKey:@"username"];
	[userDefaults setObject:[Definition sharedDefinition].password forKey:@"password"];
}

- (void) initLastLoginUser {
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	NSString *username = [userDefaults objectForKey:@"username"];
	NSString *password = [userDefaults objectForKey:@"password"];

	[Definition sharedDefinition].username = username;
	[Definition sharedDefinition].password = password;
}

// Insert a new groupmember into group_members table.
- (void) insertGroupMember:(GroupMember *)groupMember {
	const char *sqlStatement = "insert into group_members values(@url,@age)";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		sqlite3_bind_text(compiledStatement, 1, [groupMember.url UTF8String], -1, SQLITE_TRANSIENT);
		sqlite3_bind_double(compiledStatement, 2, [groupMember.age timeIntervalSince1970]);
		if(SQLITE_DONE != sqlite3_step(compiledStatement)) {
			NSLog(0, @"Error while inserting user. '%s'", sqlite3_errmsg(openDatabase));
		}
		sqlite3_reset(compiledStatement);
	}
	sqlite3_finalize(compiledStatement);
}

// Find all groupmembers.
- (NSMutableArray *) findAllGroupMembers {
	NSMutableArray *groupMembers = [[NSMutableArray alloc] init];
	const char *sqlStatement = "select * from group_members";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		while (sqlite3_step(compiledStatement) == SQLITE_ROW) {
			NSString *url = [NSString stringWithUTF8String:(char *)sqlite3_column_text(compiledStatement, 0)];
			NSDate *age = [NSDate dateWithTimeIntervalSince1970:sqlite3_column_double(compiledStatement, 1)];
			GroupMember *groupMember = [[GroupMember alloc] init];
			groupMember.url = url;
			groupMember.age = age;
			[groupMembers addObject:groupMember];
		}
		sqlite3_reset(compiledStatement);
		sqlite3_finalize(compiledStatement);
	}
	return groupMembers;
}

// Delete all the groupmembers.
- (void) deleteAllGroupMembers {
	const char *sqlStatement = "delete from group_members";
	sqlite3_stmt *compiledStatement;
	if(sqlite3_prepare_v2(openDatabase, sqlStatement, -1, &compiledStatement, NULL) ==SQLITE_OK) {
		if(SQLITE_DONE != sqlite3_step(compiledStatement)) {
			NSLog(@"Error while deleteAllGroupMembers.");
		}
	}
	sqlite3_reset(compiledStatement);
	sqlite3_finalize(compiledStatement);
}

- (void) dealloc {
	sqlite3_close(openDatabase);
	
	[super dealloc];
}

@end
