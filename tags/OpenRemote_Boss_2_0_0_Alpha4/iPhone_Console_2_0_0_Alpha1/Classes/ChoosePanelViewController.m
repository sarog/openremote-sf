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

#import "ChoosePanelViewController.h"
#import "URLConnectionHelper.h"
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"

@implementation ChoosePanelViewController


- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		panels = [[NSMutableArray alloc] init];
		chosenPanel = [[AppSettingsDefinition getPanelIdentityDic] objectForKey:@"identity"];
		NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition panelsRESTUrl]];
		NSURL *url = [[NSURL alloc]initWithString:location];
		
		//assemble put request 
		NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
		[request setURL:url];
		[request setHTTPMethod:@"POST"];
		
		URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
		
		[location release];
		[url	 release];
		[request release];
		[connection autorelease];	
		
		
	}
	return self;
}


#pragma mark Table view methods

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
		return @"Choose Your Panel Identity:";
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
	return @"UI differs according to different panel identity.";
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return panels.count;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	static NSString *panelCellIdentifier = @"panelCell";
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:panelCellIdentifier] autorelease];
	}
	
	panelCell.textLabel.text = [panels objectAtIndex:indexPath.row];
	panelCell.selectionStyle = UITableViewCellSelectionStyleNone;
	
	if ([panelCell.textLabel.text isEqualToString:chosenPanel]) {
		panelCell.accessoryType = UITableViewCellAccessoryCheckmark;
	}
	return panelCell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	if (currentSelectedPanelIndex) {
		UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedPanelIndex];
		if (cell.accessoryType  == oldCell.accessoryType) {
			return;
		}
		if (oldCell.accessoryType == UITableViewCellAccessoryCheckmark) {
			oldCell.accessoryType = UITableViewCellAccessoryNone;
		} 
	} 
	if (cell.accessoryType == UITableViewCellAccessoryNone) {
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
		[[AppSettingsDefinition getPanelIdentityDic] setObject:cell.textLabel.text forKey:@"identity"];
	} 
	
	currentSelectedPanelIndex = indexPath;
	
	[AppSettingsDefinition writeToFile];
	[self.navigationController popViewControllerAnimated:YES];
}


- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		NSString *errorMessage = nil;
		switch (statusCode) {
			case 404:
				errorMessage = [NSString stringWithString:@"The command was sent to an invalid URL."];
				break;
			case 500:
				errorMessage = [NSString stringWithString:@"Error in controller. Please check controller log."];
				break;
			case 503:
				errorMessage = [NSString stringWithString:@"Controller is not currently available."];
				break;
			case 401:
				errorMessage = [NSString stringWithString:@"User credential is required."];
				break;
			case 428:
				errorMessage = [NSString stringWithString:@"No panel identity found. Please check your panel.xml"];
				break;
		} 
		
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Unknown error occured , satus code is %d",statusCode];
		}
		[ViewHelper showAlertViewWithTitle:@"Request Failed" Message:errorMessage];
		
	} 
	
}

- (void)updateTableView {
	UITableView *tv = (UITableView *)self.view;
	[tv beginUpdates];
	
	NSArray *newArray = nil;
	newArray = panels;
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j = 0; j < newArray.count; j++){
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:0]];
	}
	[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
	
	[tv endUpdates];
	
	[insertIndexPaths release];
}


#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	
}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[self updateTableView];
	[xmlParser release];
	[result release];
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}


#pragma mark delegate method of NSXMLParser
//when find a panel start we get its *name* attribute as logical identity
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"panel"]) {
		NSLog(@"panel logical id : %@",[attributeDict valueForKey:@"name"]);
		[panels addObject:[attributeDict valueForKey:@"name"]]; 
	}
}


- (void)dealloc {
	[panels release];
	[currentSelectedPanelIndex release];
	[chosenPanel release];
	
	[super dealloc];
}


@end

