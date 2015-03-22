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
#import "Component.h"

@class LocalCommand;

@interface LocalSensor : Component

@property (nonatomic, copy, readonly) NSString *name;
@property (nonatomic, copy, readonly) NSString *type; // TODO enum
@property (nonatomic, retain) LocalCommand *command;

- (id)initWithId:(int)anId name:(NSString *)sensorName type:(NSString *)sensorType;

@end
