/**
 *  Colloquy Project - A Mac OS X Internet Chat Client 
 *  Copyright (C) Colloquy <http://colloquy.info/index.html>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * http://www.cocoadev.com/index.pl?NotificationsAcrossThreads
 */

#import "NSNotificationAdditions.h"
#import <pthread.h>

@implementation NSNotificationCenter (NSNotificationCenterAdditions)
- (void) postNotificationOnMainThread:(NSNotification *) notification
{
  if( pthread_main_np() ) return [self postNotification:notification];
  [self postNotificationOnMainThread:notification waitUntilDone:NO];
}

- (void) postNotificationOnMainThread:(NSNotification *) notification waitUntilDone:(BOOL) wait
{
  if( pthread_main_np() ) return [self postNotification:notification];
  [[self class] performSelectorOnMainThread:@selector( _postNotification: ) withObject:notification waitUntilDone:wait];
}

+ (void) _postNotification:(NSNotification *) notification
{
  [[self defaultCenter] postNotification:notification];
}

- (void) postNotificationOnMainThreadWithName:(NSString *) name object:(id) object
{
  if( pthread_main_np() ) return [self postNotificationName:name object:object userInfo:nil];
  [self postNotificationOnMainThreadWithName:name object:object userInfo:nil waitUntilDone:NO];
}

- (void) postNotificationOnMainThreadWithName:(NSString *) name object:(id) object userInfo:(NSDictionary *) userInfo
{
  if( pthread_main_np() ) return [self postNotificationName:name object:object userInfo:userInfo];
  [self postNotificationOnMainThreadWithName:name object:object userInfo:userInfo waitUntilDone:NO];
}

- (void) postNotificationOnMainThreadWithName:(NSString *) name object:(id) object userInfo:(NSDictionary *) userInfo waitUntilDone:(BOOL) wait
{
  if( pthread_main_np() ) return [self postNotificationName:name object:object userInfo:userInfo];

  NSMutableDictionary *info = [[NSMutableDictionary allocWithZone:nil] initWithCapacity:3];
  if( name ) [info setObject:name forKey:@"name"];
  if( object ) [info setObject:object forKey:@"object"];
  if( userInfo ) [info setObject:userInfo forKey:@"userInfo"];

  [[self class] performSelectorOnMainThread:@selector( _postNotificationName: ) withObject:info waitUntilDone:wait];
}

+ (void) _postNotificationName:(NSDictionary *) info
{
  NSString *name = [info objectForKey:@"name"];
  id object = [info objectForKey:@"object"];
  NSDictionary *userInfo = [info objectForKey:@"userInfo"];

  [[self defaultCenter] postNotificationName:name object:object userInfo:userInfo];

  [info release];
}
@end
