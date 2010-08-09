/**
 * This class is responsible for storing info about user.
 * auther: handy.wang 2010-07-09
 */
 Constants = (function(){
   return {
     CONTROLLER_SERVERS : "controllerServers",
     CURRENT_SERVER : "currentServer",
     LAST_FOOT_PRINT : "lastFootPrint",
     GROUP_MEMBERS : "groupMembers",
     GROUP_MEMBER_URL : "@url",
     COLOR_BLACK : "#000000",
     COLOR_WHITE : "#FFFFFF",
     COLOR_RED : "#FF0000",
     REPEAT_CMD_INTERVAL : 300,
     HTTP_SUCCESS_CODE : "200",
     UNKNOWN_ERROR_MESSAGE : "Unknown error from controller",
     STATUS_CHANGE_NOTIFICATION : "statusChangeNotification",
     REFRESH_VIEW_NOTIFICATION : "refreshViewNotification",
     NAVIGATION_NOTIFICATION : "navigationNotification",
     UNAUTHORIZED : "401",
     TIME_OUT : "504",
     CONTROLLER_CONFIG_CHANGED : "506",
     
     STATUS : "status",
     STATUS_VALUE : "#text",
     
     BASE_MODEL : "basemodel",
     
     SCREEN : "screen",
     
     BACKGROUND : "background",
     BG_IMAGE_RELATIVE : "@relative",
     BG_IMAGE_ABSOLUTE : "@absolute",
     FILL_SCREEN : "@fillScreen",
     
     IMAGE : "image",
     SRC : "@src",
     
     LAYOUT_MODEL : "layoutModel",
     
     ABSOLUTE : "absolute",
     LEFT : "@left",
     TOP : "@top",
     WIDTH : "@width",
     HEIGHT : "@height",
     
     GRID : "grid",
     ROWS : "@rows",
     COLS : "@cols",
     GRID_CELL : "cell",
     GRID_CELL_X : "@x",
     GRID_CELL_Y : "@y",
     GRID_CELL_ROWSPAN : "@rowspan",
     GRID_CELL_COLSPAN : "@colspan",
     
     GROUP : "group",
     
     BUTTON : "button",
     HAS_CONTROL_COMMAND : "@hasControlCommand",
     IS_COMMAND_REPEATED : "@repeat",
     DEFAULT : "default",
     PRESSED : "pressed",
     
     NAVIGATE : "navigate",
     TO_SCREEN : "@toScreen",
     TO_GROUP : "@toGroup",
     TO : "@to",
     PREVIOUS_SCREEN : "previousscreen",
     NEXT_SCREEN : "nextscreen",
     SETTING : "setting",
     BACK : "back",
     LOGIN : "login",
     LOGOUT : "logout",
     
     LABEL : "label",
     FONT_SIZE : "@fontSize",
     COLOR : "@color",
     TEXT : "@text",
     
     SWITCH : "switch",
     LINK : "link",
     STATE : "state",
     SENSOR : "sensor",
     SENSOR_STATE : "sensorState",
     ON :"on",
     OFF : "off",
     
     SLIDER : "slider",
     IS_VERTICAL : "@vertical",
     IS_PASSIVE : "@passive",
     THUMB_IMAGE : "@thumbImage",
     MAX : "max",
     MIN : "min",
     SLIDER_IMAGE : "@image",
     SLIDER_TRACK_IMAGE : "@trackImage",
     
     ID : "@id",
     NAME : "@name",
     VALUE : "@value",
     INCLUDE : "include",
     TYPE : "@type",
     REF : "@ref",
     TAB_BAR : "tabbar",
     TAB_BAR_ITEM : "item"
   };
 })();