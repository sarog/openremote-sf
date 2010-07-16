/**
 * This class is responsible for maintenance data within cookie.
 * auther: handy.wang 2010-07-09
 */
CookieUtils = (function() {
  return {
    setCookie : function(name, jsonObj) {
      var stringValue = JSON.stringify(jsonObj, null);
      var expires = new Date(3000,01,01);
      document.cookie = name + "=" + escape(stringValue) + "; path=/; expires=" + expires.toGMTString();
    },
    getCookie : function(name) {
    	// Firstly split cookie up into name/value pairs
    	// NOTE: document.cookie only returns name=value, not the other components
    	var a_all_cookies = document.cookie.split( ';' );
    	var a_temp_cookie = '';
    	var cookie_name = '';
    	var cookie_value = '';
    	var b_cookie_found = false; // set boolean true/false default false

    	for ( i = 0; i < a_all_cookies.length; i++ ) {
    		// Split apart each name=value pair
    		a_temp_cookie = a_all_cookies[i].split( '=' );

    		// And trim left/right whitespace while we're at it
    		cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');

    		// if the extracted name matches passed name
    		if (cookie_name == name) {
    			b_cookie_found = true;
    			// we need to handle case where cookie has no value but exists (no = sign, that is):
    			if ( a_temp_cookie.length > 1 ) {
    				cookie_value = unescape( a_temp_cookie[1].replace(/^\s+|\s+$/g, '') );
    			}
    			// note that in cases where cookie is initialized but no value, null is returned
    			return JSON.parse(cookie_value, null);
    			break;
    		}
    		a_temp_cookie = null;
    		cookie_name = '';
    	}
    	if (!b_cookie_found ) {
    		return null;
    	}
    }
  };
})();