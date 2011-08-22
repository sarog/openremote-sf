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
#import "SensorComponent.h"
#import "Label.h"

/**
 * Stores image src and label model and parsed from element image in panel.xml.
 * XML fragment example:
 * <image id="60"  src = "b.png" style="">
 *    <link type="sensor" ref="1001">
 *       <state name="on" value="on.png" />
 *       <state name="off" value="off.png" />
 *    </link>
 *    <include type="label" ref="64" />
 * </image>
 */
@interface Image : SensorComponent {
	NSString *src;
	NSString *style;
	Label *label;
}

@property (nonatomic, readwrite, copy) NSString *src;
@property (nonatomic, readwrite, copy) NSString *style;
@property (nonatomic, readwrite, retain) Label *label;

- (id)initWithId:(int)anId src:(NSString *)srcValue style:(NSString *)styleValue;

@end
