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
#import "ImageParser.h"
#import "Image.h"
#import "SensorLinkParser.h"
#import "LabelStandby.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "SensorState.h"
#import "XMLEntity.h"

@implementation ImageParser

@synthesize image;

- (void)dealloc
{
    [image release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK]; 
        image = [[Image alloc] initWithId:[[attributeDict objectForKey:ID] intValue] src:[attributeDict objectForKey:SRC] style:[attributeDict objectForKey:STYLE]];
        [aRegister.definition addImageName:[attributeDict objectForKey:SRC]];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	if ([elementName isEqualToString:INCLUDE] && [LABEL isEqualToString:[attributeDict objectForKey:TYPE]]) {
        // This is a reference to another element, will be resolved later, put a standby in place for now
        LabelStandby *standby = [[LabelStandby alloc] initWithLabelId:[[attributeDict objectForKey:REF] intValue] enclosingImage:image];
        standby.definition = self.depRegister.definition;
        [self.depRegister addStandbyToResolve:standby];
        [standby release];
	}
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        image.sensor = parser.sensor;
        
        
        // TODO: why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in image.sensor.states) {
			[self.depRegister.definition addImageName:state.value];
		}
    }
}

@end
