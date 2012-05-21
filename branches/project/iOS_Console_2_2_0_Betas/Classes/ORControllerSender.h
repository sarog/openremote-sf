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
#import <Foundation/Foundation.h>

@class ORController;

/**
 * Parent class for all "commands" towards the controller
 */
@interface ORControllerSender : NSObject

- (id)initWithController:(ORController *)aController;

- (void)send;
- (void)cancel;

/**
 * Indicates whether or not the sender should execute at this time.
 * If it does not, it'll stay in the queue and will be offered to execute at a later stage.
 * This allows command to check if the current "environment" meets to required condition for execution.
 * For instance, it can check that the controller has obtained all the required information (e.g. capabilities).
 */
- (BOOL)shouldExecuteNow;

@end