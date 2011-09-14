package org.openremote.android.console;


//This is for showing the View only
public class ControllerObject {
   
    private String controllerName;
    private String group;
    private boolean auto;
    private boolean isControllerUp;
    private boolean isSelected;
    
    
    public ControllerObject(String controllerName){
    	this.controllerName	=controllerName;    
    	isSelected			=false;
    }
    
    //for database
    public ControllerObject(String controllerName, String group, int auto, int up, int selected){
    	this.controllerName=controllerName;
    	this.group			=group;
    	this.auto			=(auto!=0);
    	this.isControllerUp	=(up!=0);
    	this.isSelected		=(selected!=0);
    }
    
   public boolean isIs_Selected(){
	   return isSelected;
   }
   
   public void setIs_Selected(boolean selected){
	   isSelected=selected;
   }
   
    public String getControllerName() {
        return controllerName;
    }
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
    
    public boolean isAuto() {
        return auto;
    }
    public void setAuto(boolean auto) {
        this.auto = auto;
    }
    
    public boolean isControllerUp() {
        return isControllerUp;
    }
    public void setIsControllerUp(boolean isControllerUp) {
        this.isControllerUp = isControllerUp;
    }
}