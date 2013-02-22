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
#import "ImageParser.h"
#import "Image.h"
#import "SensorLinkParser.h"
#import "LabelDeferredBinding.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "SensorState.h"
#import "Sensor.h"
#import "XMLEntity.h"

@interface ImageParser ()

@property (nonatomic, retain, readwrite) Image *image;

@end
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
@implementation ImageParser

- (void)dealloc
{
    self.image = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK]; 
        Image *tmp = [[Image alloc] initWithId:[[attributeDict objectForKey:ID] intValue] src:[attributeDict objectForKey:SRC] style:[attributeDict objectForKey:STYLE]];
        self.image = tmp;
        [tmp release];
        [aRegister.definition addImageName:[attributeDict objectForKey:SRC]];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	if ([elementName isEqualToString:INCLUDE] && [LABEL isEqualToString:[attributeDict objectForKey:TYPE]]) {
        // This is a reference to another element, will be resolved later, put a standby in place for now
        LabelDeferredBinding *standby = [[LabelDeferredBinding alloc] initWithBoundComponentId:[[attributeDict objectForKey:REF] intValue] enclosingObject:self.image];
        standby.definition = self.depRegister.definition;
        [self.depRegister addDeferredBinding:standby];
        [standby release];
	}
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        self.image.sensor = parser.sensor;
        
        
        // TODO: why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in self.image.sensor.states) {
			[self.depRegister.definition addImageName:state.value];
		}
    }
}

@synthesize image;

@end