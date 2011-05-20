/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2011, OpenRemote Inc.
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

#import "ORControllerProxy.h"
#import "Component.h"

@implementation ORControllerProxy

- (ORControllerCommandSender *)sendCommand:(NSString *)command forComponent:(Component *)component delegate:(NSObject <ORControllerCommandSenderDelegate> *)delegate
{    
    ORControllerCommandSender *commandSender = [[ORControllerCommandSender alloc] initWithCommand:command component:component];
    commandSender.delegate = delegate;
    [commandSender send];
    return [commandSender autorelease];
}

- (ORControllerPollingSender *)requestStatusForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerPollingSender *pollingSender = [[ORControllerPollingSender alloc] initWithIds:ids];
    pollingSender.delegate = delegate;
    [pollingSender requestStatus];
    return [pollingSender autorelease];
}

- (ORControllerPollingSender *)requestPollingForIds:(NSString *)ids delegate:(NSObject <ORControllerPollingSenderDelegate> *)delegate
{
    ORControllerPollingSender *pollingSender = [[ORControllerPollingSender alloc] initWithIds:ids];
    pollingSender.delegate = delegate;
    [pollingSender poll];
    return [pollingSender autorelease];
}

- (ORControllerPanelsFetcher *)fetchPanelsWithDelegate:(NSObject <ORControllerPanelsFetcherDelegate> *)delegate
{
    ORControllerPanelsFetcher *panelsFetcher = [[ORControllerPanelsFetcher alloc] init];
    panelsFetcher.delegate = delegate;
    [panelsFetcher fetch];
    return [panelsFetcher autorelease];
}

@end