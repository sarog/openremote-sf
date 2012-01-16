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
#import "SipController.h"
#import "AppDelegate.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

#include "linphonecore_utils.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#import <AVFoundation/AVAudioSession.h>
#import <AudioToolbox/AudioToolbox.h>

static LinphoneCore* theLinphoneCore=nil;

extern void libmsilbc_init();
#ifdef HAVE_AMR
extern void libmsamr_init();
#endif

@interface SipController ()

- (void)doLinphoneConfiguration:(NSNotification *)notification;

@end

@implementation SipController

- (id)init
{
    self = [super init];
	if (self) {
		[[NSUserDefaults standardUserDefaults] registerDefaults:[NSDictionary dictionaryWithObjectsAndKeys:
																 @"panel", @"username_preference",
                                                                 
                                                                 
                                                                 // TODO: try sending current IP instead
                                                                 @"openremote.org", @"domain_preference",
                                                                 
                                                                 
                                                                 
																 [NSNumber numberWithBool:TRUE], @"check_config_disable_preference",
																 [NSNumber numberWithBool:FALSE], @"outbound_proxy_preference",
                                                                 [NSNumber numberWithBool:TRUE], @"gsm_8k_preference",
																 [NSNumber numberWithBool:TRUE], @"pcmu_preference",
																 [NSNumber numberWithBool:TRUE], @"pcma_preference",
																 nil]];

        [self startLibLinphone];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(definitionDidUpdate) name:DefinitionUpdateDidFinishNotification object:nil];
	}
	return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

- (void)definitionDidUpdate
{
    // EBR: temp, need to review settings load mechanism
    NSLog(@"====> Current server URL %@", [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.primaryURL);
    NSLog(@"Host %@", [[NSURL URLWithString:[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.primaryURL] host]);
    
    // TODO: un-register ?
    
    [[NSUserDefaults standardUserDefaults]
            setObject:[[NSURL URLWithString:[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.primaryURL] host]
               forKey:@"proxy_preference"];

    [self doLinphoneConfiguration:nil];
}

-(void) onCall:(LinphoneCall *)currentCall stateChanged:(LinphoneCallState)new_state withMessage:(const char *)message
{
	const char* lUserNameChars=linphone_address_get_username(linphone_call_get_remote_address(currentCall));
	NSString* lUserName = lUserNameChars?[[[NSString alloc] initWithCString:lUserNameChars encoding:NSASCIIStringEncoding] autorelease]:@"Unknown";
	const char* lDisplayNameChars =  linphone_address_get_display_name(linphone_call_get_remote_address(currentCall));
	NSString* lDisplayName = lDisplayNameChars?[[[NSString alloc] initWithCString:lDisplayNameChars encoding:NSASCIIStringEncoding] autorelease]:@"";
	
	switch (new_state) {
			
		case LinphoneCallIncomingReceived:
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"Incoming" forKey:@"SIP_CallStatus"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:lUserName forKey:@"SIP_UserName"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:lDisplayName forKey:@"SIP_DisplayName"];
			break;
			
		case LinphoneCallOutgoingInit: 
			break;
			
		case LinphoneCallConnected:
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"Connected" forKey:@"SIP_CallStatus"];
			break;
			
		case LinphoneCallError: { 
			/*
			 NSString* lTitle= state->message!=nil?[NSString stringWithCString:state->message length:strlen(state->message)]: @"Error";
			 NSString* lMessage=lTitle;
			 */
			NSString* lMessage;
			NSString* lTitle;
			LinphoneProxyConfig* proxyCfg;	
			//get default proxy
			linphone_core_get_default_proxy([SipController getLc],&proxyCfg);
			if (proxyCfg == nil) {
				lMessage=@"Please make sure your device is connected to the internet and double check your SIP account configuration in the settings.";
			} else {
				lMessage=[NSString stringWithFormat : @"Cannot call %@",lUserName];
                
			}
			
			if (message!=nil){
				lMessage=[NSString stringWithFormat : @"%@\nReason was: %s",lMessage, message];
			}
			lTitle=@"Call failed";
			
			UIAlertView* error = [[UIAlertView alloc] initWithTitle:lTitle
															message:lMessage 
														   delegate:nil 
												  cancelButtonTitle:@"Dismiss" 
												  otherButtonTitles:nil];
			[error show];
            [error release];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"Error" forKey:@"SIP_CallStatus"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext removeObjectForKey:@"SIP_UserName"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext removeObjectForKey:@"SIP_DisplayName"];

			break;
		}
		case LinphoneCallEnd:
			[((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext setObject:@"End" forKey:@"SIP_CallStatus"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext removeObjectForKey:@"SIP_UserName"];
            [((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext removeObjectForKey:@"SIP_DisplayName"];
			break;
		default:
			break;
	}
	
}

-(void) onRegister:(LinphoneCore *)lc cfg:(LinphoneProxyConfig*) cfg state:(LinphoneRegistrationState) state message:(const char*) message
{
//	LinphoneAddress* lAddress = linphone_address_new(linphone_proxy_config_get_identity(cfg));
//	NSString* lUserName = linphone_address_get_username(lAddress)? [[NSString alloc] initWithCString:linphone_address_get_username(lAddress) ]:@"";
//	NSString* lDisplayName = linphone_address_get_display_name(lAddress)? [[NSString alloc] initWithCString:linphone_address_get_display_name(lAddress) ]:@"";
//	NSString* lDomain = [[NSString alloc] initWithCString:linphone_address_get_domain(lAddress)];
//	linphone_address_destroy(lAddress);
	
	if (state == LinphoneRegistrationOk) {
//		[[(SipController*)linphone_core_get_user_data(lc) registrationDelegate] displayRegisteredFromUI:nil
//                                                                                                  forUser:lUserName
//                                                                                          withDisplayName:lDisplayName
//                                                                                                 onDomain:lDomain ];
	} else if (state == LinphoneRegistrationProgress) {
//		[registrationDelegate displayRegisteringFromUI:mCurrentViewController
//                                               forUser:lUserName
//                                       withDisplayName:lDisplayName
//                                              onDomain:lDomain ];
		
	} else if (state == LinphoneRegistrationCleared || state == LinphoneRegistrationNone) {
//		[registrationDelegate displayNotRegisteredFromUI:mCurrentViewController];
	} else 	if (state == LinphoneRegistrationFailed ) {
		NSString* lErrorMessage=nil;
		if (linphone_proxy_config_get_error(cfg) == LinphoneReasonBadCredentials) {
			lErrorMessage = @"Bad credentials, check your account settings";
		} else if (linphone_proxy_config_get_error(cfg) == LinphoneReasonNoResponse) {
			lErrorMessage = @"SIP server unreachable";
		} 
//		[registrationDelegate displayRegistrationFailedFromUI:mCurrentViewController
//                                                      forUser:lUserName
//                                              withDisplayName:lDisplayName
//                                                     onDomain:lDomain
//                                                    forReason:lErrorMessage];
		
		if (lErrorMessage != nil 
						&& linphone_proxy_config_get_error(cfg) != LinphoneReasonNoResponse) { //do not report network connection issue on registration
			//default behavior if no registration delegates
			
			UIAlertView* error = [[UIAlertView alloc]	initWithTitle:@"Registration failure"
															message:lErrorMessage
														   delegate:nil 
												  cancelButtonTitle:@"Continue" 
												  otherButtonTitles:nil ,nil];
			[error show];
            [error release];
		}
	}
}

+ (BOOL)isMuted
{
    @try {
        return linphone_core_is_mic_muted([SipController getLc]);
    } @catch(NSException* e) {
		//not ready yet
		return NO;
	}
}

+ (void)muteOn:(NSMutableDictionary *)context
{
    linphone_core_mute_mic([SipController getLc], true);
}

+ (void)muteOff:(NSMutableDictionary *)context
{
	linphone_core_mute_mic([SipController getLc], false);
}

+ (void)muteToggle:(NSMutableDictionary *)context
{
    [self isMuted]?[self muteOff:context]:[self muteOn:context];
}

+ (NSString *)getRingSensorValue:(NSMutableDictionary *)context
{
	return ([@"Incoming" isEqualToString:[context valueForKey:@"SIP_CallStatus"]])?@"Ring":@"";
}

+ (NSString *)getMuteSensorValue:(NSMutableDictionary *)context
{
    return [self isMuted]?@"On":@"Off";
}

+ (void)answerCall:(NSMutableDictionary *)context
{
	NSLog(@"Answer call");
    linphone_core_accept_call([SipController getLc],linphone_core_get_current_call([SipController getLc]));	
}

+ (void)hangupCall:(NSMutableDictionary *)context
{
    linphone_core_terminate_call ([SipController getLc],linphone_core_get_current_call([SipController getLc]));
}

#pragma mark LinphoneManager methods

+ (LinphoneCore *)getLc
{
	if (theLinphoneCore==nil) {
		@throw([NSException exceptionWithName:@"LinphoneCoreException" reason:@"Linphone core not initialized yet" userInfo:nil]);
	}
	return theLinphoneCore;
}

//generic log handler for debug version
static void linphone_iphone_log_handler(int lev, const char *fmt, va_list args)
{
	NSString* format = [[NSString alloc] initWithCString:fmt encoding:[NSString defaultCStringEncoding]];
    NSLogv(format,args);
    [format release];
}

//Error/warning log handler 
static void linphone_iphone_log(struct _LinphoneCore * lc, const char * message)
{
	NSString* log = [NSString stringWithCString:message encoding:[NSString defaultCStringEncoding]]; 
	NSLog(log,NULL);
}

//status 
static void linphone_iphone_display_status(struct _LinphoneCore * lc, const char * message)
{
//	[(LinphoneManager*)linphone_core_get_user_data(lc)  displayStatus:[[NSString alloc] initWithCString:message encoding:[NSString defaultCStringEncoding]]];
}

static void linphone_iphone_call_state(LinphoneCore *lc, LinphoneCall* call, LinphoneCallState state,const char* message)
{
	/*LinphoneCallIdle,
	 LinphoneCallIncomingReceived,
	 LinphoneCallOutgoingInit,
	 LinphoneCallOutgoingProgress,
	 LinphoneCallOutgoingRinging,
	 LinphoneCallOutgoingEarlyMedia,
	 LinphoneCallConnected,
	 LinphoneCallStreamsRunning,
	 LinphoneCallPausing,
	 LinphoneCallPaused,
	 LinphoneCallResuming,
	 LinphoneCallRefered,
	 LinphoneCallError,
	 LinphoneCallEnd,
	 LinphoneCallPausedByRemote
	 */
	[(SipController*)linphone_core_get_user_data(lc) onCall:call stateChanged:state withMessage:message];
}

static void linphone_iphone_registration_state(LinphoneCore *lc, LinphoneProxyConfig* cfg, LinphoneRegistrationState state,const char* message)
{
	[(SipController*)linphone_core_get_user_data(lc) onRegister:lc cfg:cfg state:state message:message];
}

static LinphoneCoreVTable linphonec_vtable = {
	.show =NULL,
	.call_state_changed =(LinphoneCallStateCb)linphone_iphone_call_state,
	.registration_state_changed = linphone_iphone_registration_state,
	.notify_recv = NULL,
	.new_subscription_request = NULL,
	.auth_info_requested = NULL,
	.display_status = linphone_iphone_display_status,
	.display_message=linphone_iphone_log,
	.display_warning=linphone_iphone_log,
	.display_url=NULL,
	.text_received=NULL,
	.dtmf_received=NULL
};

- (void)configurePayloadType:(const char *)type fromPrefKey:(NSString *)key withRate:(int)rate
{
	if ([[NSUserDefaults standardUserDefaults] boolForKey:key]) { 		
		PayloadType* pt;
		if((pt = linphone_core_find_payload_type(theLinphoneCore,type,rate))) {
			linphone_core_enable_payload_type(theLinphoneCore,pt, TRUE);
		}
	} 
}

- (void)doLinphoneConfiguration:(NSNotification *)notification
{
	ms_message("Configuring Linphone");
	if (theLinphoneCore==nil) {
		ms_warning("cannot configure linphone beacause not initialized yet");
		return;
	}
	if ([[NSUserDefaults standardUserDefaults] boolForKey:@"debugenable_preference"]) {
		//redirect all traces to the iphone log framework
		linphone_core_enable_logs_with_cb((OrtpLogFunc)linphone_iphone_log_handler);
	}
	else {
		linphone_core_disable_logs();
	}
	NSString* transport = [[NSUserDefaults standardUserDefaults] stringForKey:@"transport_preference"];
	
	LCSipTransports transportValue;
	if (transport!=nil) {
		if (linphone_core_get_sip_transports(theLinphoneCore, &transportValue)) {
			ms_error("cannot get current transport");	
		}
		if ([transport isEqualToString:@"tcp"]) {
			if (transportValue.tcp_port == 0) transportValue.tcp_port=transportValue.udp_port;
			transportValue.udp_port=0;
		} else if ([transport isEqualToString:@"udp"]){
			if (transportValue.udp_port == 0) transportValue.udp_port=transportValue.tcp_port;
			transportValue.tcp_port=0;
		} else {
			ms_error("unexpected trasnport [%s]",[transport cStringUsingEncoding:[NSString defaultCStringEncoding]]);
		}
		if (linphone_core_set_sip_transports(theLinphoneCore, &transportValue)) {
			ms_error("cannot set transport");	
		}
	}
	
	// Set audio assets
	NSBundle* myBundle = [NSBundle mainBundle];
	const char*  lRing = [[myBundle pathForResource:@"oldphone-mono"ofType:@"wav"] cStringUsingEncoding:[NSString defaultCStringEncoding]];
	linphone_core_set_ring(theLinphoneCore, lRing );
	const char*  lRingBack = [[myBundle pathForResource:@"ringback"ofType:@"wav"] cStringUsingEncoding:[NSString defaultCStringEncoding]];
	linphone_core_set_ringback(theLinphoneCore, lRingBack);
 	

	//configure sip account
	
	//madatory parameters
	NSString* username = [[NSUserDefaults standardUserDefaults] stringForKey:@"username_preference"];
	NSString* domain = [[NSUserDefaults standardUserDefaults] stringForKey:@"domain_preference"];
	NSString* accountPassword = [[NSUserDefaults standardUserDefaults] stringForKey:@"password_preference"];
	bool configCheckDisable = [[NSUserDefaults standardUserDefaults] boolForKey:@"check_config_disable_preference"];
	bool isOutboundProxy= [[NSUserDefaults standardUserDefaults] boolForKey:@"outbound_proxy_preference"];
		
	//clear auth info list
	linphone_core_clear_all_auth_info(theLinphoneCore);
	//clear existing proxy config
	linphone_core_clear_proxy_config(theLinphoneCore);

	if (username && [username length] >0 && domain && [domain length]>0) {
		const char* identity = [[NSString stringWithFormat:@"sip:%@@%@",username,domain] cStringUsingEncoding:[NSString defaultCStringEncoding]];
		const char* password = [accountPassword cStringUsingEncoding:[NSString defaultCStringEncoding]];
		
		NSString* proxyAddress = [[NSUserDefaults standardUserDefaults] stringForKey:@"proxy_preference"];
		if ((!proxyAddress | [proxyAddress length] <1 ) && domain) {
			proxyAddress = [NSString stringWithFormat:@"sip:%@",domain] ;
		} else {
			proxyAddress = [NSString stringWithFormat:@"sip:%@",proxyAddress] ;
		}
		
		const char* proxy = [proxyAddress cStringUsingEncoding:[NSString defaultCStringEncoding]];
		
		NSString* prefix = [[NSUserDefaults standardUserDefaults] stringForKey:@"prefix_preference"];
		//possible valid config detected
		LinphoneProxyConfig* proxyCfg;	
		proxyCfg = linphone_proxy_config_new();
		
		// add username password
		LinphoneAddress *from = linphone_address_new(identity);
		LinphoneAuthInfo *info;
		if (from !=0){
			info=linphone_auth_info_new(linphone_address_get_username(from),NULL,password,NULL,NULL);
			linphone_core_add_auth_info(theLinphoneCore,info);
		}
		linphone_address_destroy(from);
		
		// configure proxy entries
		linphone_proxy_config_set_identity(proxyCfg,identity);
		linphone_proxy_config_set_server_addr(proxyCfg,proxy);
		linphone_proxy_config_enable_register(proxyCfg,true);
		
		if (isOutboundProxy)
			linphone_proxy_config_set_route(proxyCfg,proxy);
		
		if ([prefix length]>0) {
			linphone_proxy_config_set_dial_prefix(proxyCfg, [prefix cStringUsingEncoding:[NSString defaultCStringEncoding]]);
		}
		linphone_proxy_config_set_dial_escape_plus(proxyCfg,TRUE);
		
		linphone_core_add_proxy_config(theLinphoneCore,proxyCfg);
		//set to default proxy
		linphone_core_set_default_proxy(theLinphoneCore,proxyCfg);		
	} else {
		if (configCheckDisable == false ) {
			UIAlertView* error = [[UIAlertView alloc]	initWithTitle:@"Warning"
															message:@"It seems you have not configured any proxy server from settings" 
														   delegate:self
												  cancelButtonTitle:@"Continue"
												  otherButtonTitles:@"Never remind",nil];
			[error show];
            [error release];
		}
	}		
	
	//Configure Codecs
	
	PayloadType *pt;
	//get codecs from linphonerc	
	const MSList *audioCodecs=linphone_core_get_audio_codecs(theLinphoneCore);
	const MSList *elem;
	//disable all codecs
	for (elem=audioCodecs;elem!=NULL;elem=elem->next){
		pt=(PayloadType*)elem->data;
		linphone_core_enable_payload_type(theLinphoneCore,pt,FALSE);
	}
	
	//read codecs from setting  bundle and enable them one by one
	[self configurePayloadType:"speex" fromPrefKey:@"speex_16k_preference" withRate:16000];
	[self configurePayloadType:"speex" fromPrefKey:@"speex_8k_preference" withRate:8000];
    [self configurePayloadType:"AMR" fromPrefKey:@"amr_8k_preference" withRate:8000];
	[self configurePayloadType:"GSM" fromPrefKey:@"gsm_8k_preference" withRate:8000];
	[self configurePayloadType:"iLBC" fromPrefKey:@"ilbc_preference" withRate:8000];
	[self configurePayloadType:"PCMU" fromPrefKey:@"pcmu_preference" withRate:8000];
	[self configurePayloadType:"PCMA" fromPrefKey:@"pcma_preference" withRate:8000];
	
	UIDevice* device = [UIDevice currentDevice];
	bool backgroundSupported = false;
	if ([device respondsToSelector:@selector(isMultitaskingSupported)])
		backgroundSupported = [device isMultitaskingSupported];
	
	if (backgroundSupported) {
		isbackgroundModeEnabled = [[NSUserDefaults standardUserDefaults] boolForKey:@"backgroundmode_preference"];
	} else {
		isbackgroundModeEnabled=false;
	}
}

- (void)destroyLibLinphone
{
	[mIterateTimer invalidate]; 
	if (theLinphoneCore != nil) { //just in case application terminate before linphone core initialization
		linphone_core_destroy(theLinphoneCore);
		theLinphoneCore = nil;        
    }    
}

//**********************BG mode management*************************///////////
- (void)enterBackgroundMode
{	
	struct addrinfo hints;
	struct addrinfo *res=NULL;
	int err;
	
	LinphoneProxyConfig* proxyCfg;
	LinphoneAddress *addr;
	linphone_core_get_default_proxy(theLinphoneCore, &proxyCfg);	
	linphone_core_stop_dtmf_stream(theLinphoneCore);
	
	if (isbackgroundModeEnabled && proxyCfg) {
		//For registration register
		linphone_core_refresh_registers(theLinphoneCore);
		
		//wait for registration answer
		int i=0;
		while (!linphone_proxy_config_is_registered(proxyCfg) && i++<40 ) {
			linphone_core_iterate(theLinphoneCore);
			usleep(100000);
		}
		//register keepalive
		if ([[UIApplication sharedApplication] setKeepAliveTimeout:600/*(NSTimeInterval)linphone_proxy_config_get_expires(proxyCfg)*/ 
														   handler:^{
															   ms_warning("keepalive handler");
															   if (theLinphoneCore == nil) {
																   ms_warning("It seam that Linphone BG mode was deacticated, just skipping");
																   return;
															   }
															   //kick up network cnx, just in case
//															   [self kickOffNetworkConnection];
															   linphone_core_refresh_registers(theLinphoneCore);
															   linphone_core_iterate(theLinphoneCore);
														   }
			 ]) {
			ms_warning("keepalive handler succesfully registered"); 
		} else {
			ms_warning("keepalive handler cannot be registered");
		}
		LCSipTransports transportValue;
		if (linphone_core_get_sip_transports(theLinphoneCore, &transportValue)) {
			ms_error("cannot get current transport");	
		}
		
		if (mReadStream == nil && transportValue.udp_port>0) { //only for udp
			int sipsock = linphone_core_get_sip_socket(theLinphoneCore);	
			//disable keepalive handler
			linphone_core_enable_keep_alive(theLinphoneCore, false);
			const char *port;
			addr=linphone_address_new(linphone_proxy_config_get_addr(proxyCfg));
			memset(&hints,0,sizeof(hints));
			hints.ai_family=linphone_core_ipv6_enabled(theLinphoneCore) ? AF_INET6 : AF_INET;
			port=linphone_address_get_port(addr);
			if (port==NULL) port="5060";
			err=getaddrinfo(linphone_address_get_domain(addr),port,&hints,&res);
			if (err!=0){
				ms_error("getaddrinfo() failed for %s: %s",linphone_address_get_domain(addr),gai_strerror(err));
				linphone_address_destroy(addr);
				return;
			}
			err=connect(sipsock,res->ai_addr,res->ai_addrlen);
			if (err==-1){
				ms_error("Connect failed: %s",strerror(errno));
			}
			freeaddrinfo(res);
			
			CFStreamCreatePairWithSocket(NULL, (CFSocketNativeHandle)sipsock, &mReadStream,nil);
			
			if (!CFReadStreamSetProperty(mReadStream, kCFStreamNetworkServiceType, kCFStreamNetworkServiceTypeVoIP)) {
				ms_error("cannot set service type to voip for read stream");
			}
			
			
			if (!CFReadStreamOpen(mReadStream)) {
				ms_error("cannot open read stream");
			}		
		}
	}
	else {
		ms_warning("Entering lite bg mode");
		[self destroyLibLinphone];
	}	
}


//scheduling loop
-(void) iterate
{
	linphone_core_iterate(theLinphoneCore);
}

/*************
 *lib linphone init method
 */
- (void)startLibLinphone
{	
	//get default config from bundle
	NSBundle* myBundle = [NSBundle mainBundle];
	NSString* factoryConfig = [myBundle pathForResource:@"linphonerc"ofType:nil] ;
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *confiFileName = [[paths objectAtIndex:0] stringByAppendingString:@"/.linphonerc"];
    
    // TODO: handle connectivity correctly
	connectivity=wifi;

	signal(SIGPIPE, SIG_IGN);
	//log management	
	
	if ([[NSUserDefaults standardUserDefaults] boolForKey:@"debugenable_preference"]) {
		//redirect all traces to the iphone log framework
		linphone_core_enable_logs_with_cb((OrtpLogFunc)linphone_iphone_log_handler);
	}
	else {
		linphone_core_disable_logs();
	}
	
	libmsilbc_init();
	
#ifdef HAVE_AMR
    libmsamr_init(); //load amr plugin if present from the liblinphone sdk
#endif	/*
* Initialize linphone core
*/
	
	theLinphoneCore = linphone_core_new (&linphonec_vtable
										 , [confiFileName cStringUsingEncoding:[NSString defaultCStringEncoding]]
										 , [factoryConfig cStringUsingEncoding:[NSString defaultCStringEncoding]]
										 ,self);
	
	[[NSUserDefaults standardUserDefaults] synchronize];//sync before loading config 
	[ self doLinphoneConfiguration:nil];
/*
	[[NSNotificationCenter defaultCenter]	addObserver:self
											 selector:@selector(doLinphoneConfiguration:)
												 name:NSUserDefaultsDidChangeNotification object:nil];
*/
	
	//initial state is network off should be done as soon as possible
//	linphone_core_set_network_reachable(theLinphoneCore,false);
    // do not care about network connectivity for now and pretend we're reachable
    linphone_core_set_network_reachable(theLinphoneCore, true);
    
	// start scheduler
	mIterateTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 
													 target:self 
												   selector:@selector(iterate) 
												   userInfo:nil 
													repeats:YES];
	//init audio session
	AVAudioSession *audioSession = [AVAudioSession sharedInstance];
	BOOL bAudioInputAvailable= [audioSession inputIsAvailable];
	
	if(!bAudioInputAvailable){
		UIAlertView* error = [[UIAlertView alloc]	initWithTitle:@"No microphone"
														message:@"You need to plug a microphone to your device to use this application." 
													   delegate:nil 
											  cancelButtonTitle:@"Ok" 
											  otherButtonTitles:nil ,nil];
		[error show];
        [error release];
	}
	/*IOS specific*/
	linphone_core_start_dtmf_stream(theLinphoneCore);
}

- (void)becomeActive
{
	if (theLinphoneCore == nil) {
		//back from standby and background mode is disabled
		[self	startLibLinphone];		
	} else {
		ms_message("becoming active, make sure we are registered");
		linphone_core_start_dtmf_stream(theLinphoneCore);
		linphone_core_refresh_registers(theLinphoneCore);//just to make sure REGISTRATION is up to date
	}
	
	LCSipTransports transportValue;
	if (linphone_core_get_sip_transports(theLinphoneCore, &transportValue)) {
		ms_error("cannot get current transport");	
	}
	if (transportValue.udp_port != 0) {
		//enable sip keepalive 
		linphone_core_enable_keep_alive(theLinphoneCore, true);
	}
	if (mReadStream !=nil) {
		//unconnect
		int socket = linphone_core_get_sip_socket(theLinphoneCore);
		struct sockaddr hints;
		memset(&hints,0,sizeof(hints));
		hints.sa_family=AF_UNSPEC;
		connect(socket,&hints,sizeof(hints));
		CFReadStreamClose(mReadStream);
		CFRelease(mReadStream);
		mReadStream=nil;
	}
}

@synthesize connectivity;

@end