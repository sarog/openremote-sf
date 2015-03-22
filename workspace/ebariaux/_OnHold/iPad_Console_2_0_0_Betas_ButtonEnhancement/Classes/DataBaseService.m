/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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
	NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
	NSString *dbFilePath = [thisBundle pathForResource:@"database" ofType:@"db"];
	NSLog(@"dbFilePath=%@", dbFilePath);
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

// Clean the Users table data.
- (void) deleteAllUsers {
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	[userDefaults setObject:nil	forKey:@"password"];
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

- (void) dealloc {
	sqlite3_close(openDatabase);
	
	[super dealloc];
}

@end
