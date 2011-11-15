
// Sample code originally posted on 
// http://stackoverflow.com/questions/392464/any-base64-library-on-iphone-sdk


#import <Foundation/Foundation.h>

/**
 * Base64 encoding util.
 */
@interface NSString (NSStringAdditions)

/**
 * Encode data with base64 algorithm.
 */
+ (NSString *) base64StringFromData: (NSData *)data length: (int)length;


@end
