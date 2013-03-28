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
#import "SliderParser.h"
#import "Slider.h"
#import "Image.h"
#import "Sensor.h"
#import "SensorLinkParser.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "SensorState.h"
#import "XMLEntity.h"

@interface SliderParser ()

@property (nonatomic, retain, readwrite) Slider *slider;

@end

/**
 * Stores model data about slider parsed from "slider" element in panel.xml.
 * XML fragment example:
 * <slider id="60" thumbImage="thumbImage.png">
 *    <min value="0" image="mute.png" trackImage="red.png"/>
 *    <max value="100" image="loud.png" trackImage="green.png"/>
 *    <link type="sensor" ref="60" />
 * </slider>
 */
@implementation SliderParser

- (void)dealloc
{
    self.slider = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK]; 
        Slider *tmp = [[Slider alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                   vertical:[@"true" isEqualToString:[[attributeDict objectForKey:VERTICAL] lowercaseString]]
                                    passive:[@"true" isEqualToString:[[attributeDict objectForKey:PASSIVE] lowercaseString]]
                              thumbImageSrc:[attributeDict objectForKey:THUMB_IMAGE]];
        [self.depRegister.definition addImageName:[attributeDict objectForKey:THUMB_IMAGE]];
        self.slider = tmp;
        [tmp release];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	Image *img = [[Image alloc] init];
	img.src = [attributeDict objectForKey:IMAGE];
    [self.depRegister.definition addImageName:img.src];
	Image *trackImg = [[Image alloc] init];
	trackImg.src = [attributeDict objectForKey:TRACK_IMAGE];
    [self.depRegister.definition addImageName:trackImg.src];
	
	if ([elementName isEqualToString:MIN_VALUE]) {
		self.slider.minValue = [[attributeDict objectForKey:VALUE] floatValue];				
		self.slider.minImage = img;
		self.slider.minTrackImage = trackImg;
	} else if ([elementName isEqualToString:MAX_VALUE]) {
		self.slider.maxValue = [[attributeDict objectForKey:VALUE] floatValue];
		self.slider.maxImage = img;
		self.slider.maxTrackImage = trackImg;
	}
    [img release];
    [trackImg release];
	
	[super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        self.slider.sensor = parser.sensor;
        
        
        // TODO: why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in self.slider.sensor.states) {
			[self.depRegister.definition addImageName:state.value];
		}
    }
}

@synthesize slider;

@end