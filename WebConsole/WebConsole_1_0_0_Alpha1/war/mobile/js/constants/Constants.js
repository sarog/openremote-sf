/**
 * This class is responsible for storing constants webconsole app uses.
 *
 * author: handy.wang 2010-07-09
 */
 Constants = (function(){
   return {
     /** Key of controller server array in cookie */
     CONTROLLER_SERVERS : "controllerServers",
     
     /** Key of current controller server object in cookie */
     CURRENT_SERVER : "currentServer",
     
     /** Key of last group and screen object in cookie */
     LAST_FOOT_PRINT : "lastFootPrint",
     
     /** Key of group member array in cookie */
     GROUP_MEMBERS : "groupMembers",
     
     GROUP_MEMBER_URL : "url",
     COLOR_BLACK : "#000000",
     COLOR_WHITE : "#FFFFFF",
     COLOR_RED : "#FF0000",
     
     /** Milliseconds interval for repeated command. */
     REPEAT_CMD_INTERVAL : 300,
     UNKNOWN_ERROR_MESSAGE : "Unknown error from controller",
     
     /** Notification type of status change */
     STATUS_CHANGE_NOTIFICATION : "statusChangeNotification",
     
     /** Notification type of refreshing view */
     REFRESH_VIEW_NOTIFICATION : "refreshViewNotification",
     
     /** Notification type of navigate */
     NAVIGATION_NOTIFICATION : "navigationNotification",
     
     /** Status code of unuthorized http basic authentication */
     UNAUTHORIZED : "401",
     
     /** Status code of request timeout */
     TIME_OUT : "504",
     
     /** Status code of controller server being refreshing */
     CONTROLLER_CONFIG_CHANGED : "506",
     
     /** Status code of successful request */
     HTTP_SUCCESS_CODE : "200",
     
     STATUS : "status",
     STATUS_VALUE : "content",
     
     BASE_MODEL : "basemodel",
     
     SCREEN : "screen",
     
     BACKGROUND : "background",
     BG_IMAGE_RELATIVE : "relative",
     BG_IMAGE_ABSOLUTE : "absolute",
     FILL_SCREEN : "fillScreen",
     
     IMAGE : "image",
     SRC : "src",
     
     LAYOUT_MODEL : "layoutModel",
     
     ABSOLUTE : "absolute",
     LEFT : "left",
     TOP : "top",
     WIDTH : "width",
     HEIGHT : "height",
     
     GRID : "grid",
     ROWS : "rows",
     COLS : "cols",
     GRID_CELL : "cell",
     GRID_CELL_X : "x",
     GRID_CELL_Y : "y",
     GRID_CELL_ROWSPAN : "rowspan",
     GRID_CELL_COLSPAN : "colspan",
     
     GROUP : "group",
     
     BUTTON : "button",
     HAS_CONTROL_COMMAND : "hasControlCommand",
     IS_COMMAND_REPEATED : "repeat",
     DEFAULT : "default",
     PRESSED : "pressed",
     
     NAVIGATE : "navigate",
     TO_SCREEN : "toScreen",
     TO_GROUP : "toGroup",
     TO : "to",
     PREVIOUS_SCREEN : "previousscreen",
     NEXT_SCREEN : "nextscreen",
     SETTING : "setting",
     BACK : "back",
     LOGIN : "login",
     LOGOUT : "logout",
     
     LABEL : "label",
     FONT_SIZE : "fontSize",
     COLOR : "color",
     TEXT : "text",
     
     SWITCH : "switch",
     LINK : "link",
     STATE : "state",
     SENSOR : "sensor",
     SENSOR_STATE : "sensorState",
     ON :"on",
     OFF : "off",
     
     SLIDER : "slider",
     IS_VERTICAL : "vertical",
     IS_PASSIVE : "passive",
     THUMB_IMAGE : "thumbImage",
     MAX : "max",
     MIN : "min",
     SLIDER_IMAGE : "image",
     SLIDER_TRACK_IMAGE : "trackImage",
     
     ID : "id",
     NAME : "name",
     VALUE : "value",
     INCLUDE : "include",
     TYPE : "type",
     REF : "ref",
     TAB_BAR : "tabbar",
     TAB_BAR_ITEM : "item"
   };
 })();