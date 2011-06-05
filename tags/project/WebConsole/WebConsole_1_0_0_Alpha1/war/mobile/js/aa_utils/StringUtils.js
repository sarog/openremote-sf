/**
 * This utils is responsible for dealing with string.
 *
 * author: handy.wang 2010-07-14
 */

/**
 * Trim the both sides of string.
 */
String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
}

/**
 * Trim the left side of string. 
 */
String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
}

/**
 * Trim the right side of string.
 */
String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
}