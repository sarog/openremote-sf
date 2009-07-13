jQuery.validator.addMethod("isImage", function(value, element) {
    if(value==""){
        return false;
    }
    return this.optional(element) || /.+?\.(png|gif|jpg)/.test(value); 
}, "Please select a gif, jpg or png type image."); 