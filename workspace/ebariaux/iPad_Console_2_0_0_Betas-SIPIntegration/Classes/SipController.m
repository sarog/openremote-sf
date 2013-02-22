//
//  SipController.m
//  openremote
//
//  Created by Eric Bariaux on 03/03/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "SipController.h"
#import "AppDelegate.h"
#import "AppSettingsDefinition.h"

@implementation SipController

- (id)init {
	if (self = [super init]) {
		[[NSUserDefaults standardUserDefaults] registerDefaults:[NSDictionary dictionaryWithObjectsAndKeys:
																 @"siphon", @"username",
																 @"siphon", @"authname",
																 [NSNumber numberWithInt:900], @"regTimeout",
																 [NSNumber numberWithBool:TRUE], @"enableGSM",
																 [NSNumber numberWithBool:TRUE], @"enableG711a",
																 [NSNumber numberWithBool:TRUE], @"enableG722",
																 nil]];
		// Start as not connected
		_sip_acc_id = PJSUA_INVALID_ID;

        
        
        
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(definitionDidUpdate) name:DefinitionUpdateDidFinishNotification object:nil];
	}
	return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

- (void)definitionDidUpdate {
    // EBR: temp, need to review settings load mechanism
    NSLog(@"====> Current server URL %@", [AppSettingsDefinition getCurrentServerUrl]);
    NSLog(@"Host %@", [[NSURL URLWithString:[AppSettingsDefinition getCurrentServerUrl]] host]);
    
    [self sipDisconnect];
    // TODO: check that this does un-register in SIP servlet
    
    [[NSUserDefaults standardUserDefaults] setObject:[[NSURL URLWithString:[AppSettingsDefinition getCurrentServerUrl]] host] forKey:@"server"];    
    [self sipConnect];
}


// TODO: must un-register


- (void)processCallState:(NSNotification *)notification {
	NSLog(@"processCallState: %@", notification);
	
	int state = [[[ notification userInfo ] objectForKey: @"State"] intValue];
	
	switch(state)
	{
		
		case PJSIP_INV_STATE_INCOMING:
			[((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"ring" forKey:@"RING"];
			[((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:[[notification userInfo] objectForKey:@"CallID"] forKey:@"CallID"];
			break;
		case PJSIP_INV_STATE_DISCONNECTED:
			[((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"" forKey:@"RING"];
			break;
	}
}

- (void)processRegState:(NSNotification *)notification {
	NSLog(@"processRegState: %@", notification);
}

/* */
- (BOOL)sipStartup
{
	if (_app_config.pool)
		return YES;
	
//	self.networkActivityIndicatorVisible = YES;
	
	if (sip_startup(&_app_config) != PJ_SUCCESS)
	{
//		self.networkActivityIndicatorVisible = NO;
		return NO;
	}
//	self.networkActivityIndicatorVisible = NO;
	
	/** Call management **/
	[[NSNotificationCenter defaultCenter] addObserver:self
											 selector:@selector(processCallState:)
												 name: kSIPCallState object:nil];
	
	/** Registration management */
	[[NSNotificationCenter defaultCenter] addObserver:self
											 selector:@selector(processRegState:)
												 name: kSIPRegState object:nil];
	
	return YES;
}

/* */
- (BOOL)sipDisconnect
{
	if ((_sip_acc_id != PJSUA_INVALID_ID) &&
		(sip_disconnect(&_sip_acc_id) != PJ_SUCCESS))
	{
		return FALSE;
	}
	
	_sip_acc_id = PJSUA_INVALID_ID;
	
	//	isConnected = FALSE;
	
	return TRUE;
}


/* */
- (void)sipCleanup
{
	//[[NSNotificationCenter defaultCenter] removeObserver:self];
	[[NSNotificationCenter defaultCenter] removeObserver:self
													name: kSIPRegState
												  object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self 
													name:kSIPCallState 
												  object:nil];
	[self sipDisconnect];
	
	if (_app_config.pool != NULL)
	{
		sip_cleanup(&_app_config);
	}
}

/* */
- (BOOL)sipConnect
{
	pj_status_t status;
	
	if (![self sipStartup])
		return FALSE;
	
//	if ([self wakeUpNetwork] == NO)
//		return NO;
	
	if (_sip_acc_id == PJSUA_INVALID_ID)
	{
//		self.networkActivityIndicatorVisible = YES;
		if ((status = sip_connect(_app_config.pool, &_sip_acc_id)) != PJ_SUCCESS)
		{
//			self.networkActivityIndicatorVisible = NO;
			return FALSE;
		}
	}
	
	return TRUE;
}

- (app_config_t *)pjsipConfig
{
	return &_app_config;
}


+ (NSString *)getRingSensorValue:(NSMutableDictionary *)context {
	NSString *retValue = [context valueForKey:@"RING"];
	return (retValue)?retValue:@"";
}

+ (void)answerCall:(NSMutableDictionary *)context {
	NSLog(@"Answer call");
	pjsua_call_id call_id = [[context objectForKey:@"CallID"] intValue];
	sip_answer(&call_id);
}

+ (void)hangupCall:(NSMutableDictionary *)context {
	pjsua_call_id call_id = [[context objectForKey:@"CallID"] intValue];
	sip_hangup(&call_id);
	[context removeObjectForKey:@"CallID"];
}

@end
