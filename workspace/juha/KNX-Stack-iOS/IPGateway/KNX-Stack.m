#import <Foundation/Foundation.h>
#import "KNXtest.h"

int main (int argc, const char * argv[])
{
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];

    // start a test connection
    NSLog(@"Test KNX connection");

	KNXtest *testConnection=[[KNXtest alloc] init];
	[testConnection searchGateway];

//	[testConnection connectTo:@"192.168.0.14"];	// my gateway - don't spread the address around :-)

	// clean up
	[pool drain];
    return 0;
}
