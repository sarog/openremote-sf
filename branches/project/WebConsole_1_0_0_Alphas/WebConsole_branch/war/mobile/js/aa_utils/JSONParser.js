/**
 * It's responsible for extracting json-formed data into model instances, these json-formed data is from controller server.
 * The design of this util referenced delegate design pattern and the mechanism of NSParser in CocoaTouch of apple.
 *
 * Now, please follow the steps I will present to use this JSONParser util.
 * 1) Construct a JSONParser instance as following :
 *      var jsonParser = new JSONParser(jsonData, delegate);
 *    , but you may be confused with the parameters each named "jsonData" and "delegate". 
 *    Please take it easy, I will explain them immediately.
 *    Parameter "jsonData" is javascript object data from some datasource provider whereever you can imagine,
 *                         however, the "jsonData" of webconsole got is from RESTful services of controller server provided.
 *    Parameter "delegate" is instance of some class's and do something jsonparser want the instance do. 
 *                         So, we call the methods intance object did is "delegate methods" in term. The delegate methods of jsonParser
 *                         will call are two and each is "didParse(jsonParser, nodeName, properties)" and "didParseFinished()".
 *                         These delegate methods must be declared with pulic scope or jsonParser can't call them.
 *                         The delegate method "didParse(jsonParser, nodeName, properties)" is called during parsing json object
 *                         and pass node name of node(in xml term), properties of node. The delegate method "didParseFinished()"
 *                         is called when the parsing process finish.
 * 2) Start parsing process for JSON data. the code as following:
 *      jsonParser.startParse();
 *    , assume variable "jsonParser" is defined in previous.
 * 3) Waiting until parsing finish. However, I think developer must do some valuable things in the two delegate methods I mentioned
 *    in step 1).
 *
 * So, I must admit I am stammer if you are still don't know how to use it， but you can reference the classes such as UpdateController and PollingHelper.
 *
 * author: handy.wang 2010-07-15
 */
JSONParser = (function() {
 
 /**
  * Constructor with two parameters.
  * The meaning of two parameters is explained in previous header comments.
  */
 return function(jsonDataParam, delegateParam) {
   
   var self = this;
   var jsonData = jsonDataParam;
   self.delegate = delegateParam;
   
   /**
    * Start parsing process
    */
   this.startParse = function() {
     recursiveParse(null, jsonData);
     self.delegate.didParseFinished();
   };
   
   /**
    * Set the delegate instance manually, so, program can change the delegate during parsing process.
    */
   this.setDelegate = function(delegateParam) {
     self.delegate = delegateParam;
   };
   
   /**
    * This method is parsing process core with recursive algorithm。 
    */
   function recursiveParse(nodeName, jsonData) {
     var properties = {};
     var isLeaf = true;
     for (var key in jsonData) {
       var value = jsonData[key];
       var valueObj = Object.prototype.toString.apply(value);
       if (valueObj === "[object String]" || valueObj === "[object Number]" || valueObj === "[object Boolean]") {         
         properties[key] = value;
       } else {         
         isLeaf = false;        
       }
     }
     if (nodeName != null) {
       self.delegate.didParse(self, nodeName, properties);         
     }
     
     if (isLeaf) {
       return;
     }

     for (var key in jsonData) {
       var value = jsonData[key];
       var valueObj = Object.prototype.toString.apply(value);
       if (valueObj === "[object String]" || valueObj === "[object Number]" || valueObj === "[object Boolean]") {
         continue;
       }else if (Object.prototype.toString.apply(value) === "[object Array]") {
         for(var index in value) {
           var oldDelegate = self.delegate;
           recursiveParse(key, value[index]);
           self.setDelegate(oldDelegate);
         }
       } else {
         var oldDelegate = self.delegate;
         recursiveParse(key, value);
         self.setDelegate(oldDelegate);
       }
     }
   }
   
 };
})();