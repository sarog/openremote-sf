/**
 * This class is responsible for storing info about user.
 * auther: handy.wang 2010-07-09
 */
 Constants = (function(){
   return {
     CONTROLLER_SERVERS : "controllerServers",
     CURRENT_SERVER : "currentServer",
     LAST_GROUP_ID_WHEN_QUIT : "lastGroupId",
     COLOR_BLACK : "#000000",
     COLOR_WHITE : "#FFFFFF",
     COLOR_RED : "#FF0000",
     
     BASE_MODEL : "basemodel",
     
     SCREEN : "screen",
     
     BACKGROUND : "background",
     BG_IMAGE_RELATIVE : "@relative",
     BG_IMAGE_ABSOLUTE : "@absolute",
     FILL_SCREEN : "@fillScreen",
     
     LABEL : "label",
     
     BUTTON : "button",
     DEFAULT : "default",
     PRESSES : "pressed",
     NAVIGATE : "navigate",
     HAS_CONTROL_COMMAND : "@hasControlCommand",
     IS_COMMAND_REPEATED : "@repeat",
     
     SWITCH : "switch",
     SLIDER : "slider",
     
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
     
     ID : "@id",
     NAME : "@name",
     INCLUDE : "include",
     TYPE : "@type",
     REF : "@ref",
     TAB_BAR : "tabbar"
   };
 })();