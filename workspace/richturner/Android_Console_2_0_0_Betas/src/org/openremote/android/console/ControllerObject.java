package org.openremote.android.console;


//This is for showing the View only
public class ControllerObject {
   
    private String controllerName;
    private String failoverForcontroller;
    private String group;
    private boolean auto;
    private boolean isControllerUp;
    private boolean isSelected;
    private boolean availabilityCheckDone;
    
    public ControllerObject(String controllerName){
    	this.controllerName	=controllerName;    
    	isSelected			=false;
    }
    
/*    //for database
    public ControllerObject(String controllerName, String group, int auto, int up, int selected){
    	this.controllerName=controllerName;
    	this.group			=group;
    	this.auto			=(auto!=0);
    	this.isControllerUp	=(up!=0);
    	this.isSelected		=(selected!=0);
    }*/
    
    //for failover stuff need failover urls
    public ControllerObject(String controllerName, String group, int auto, int up, int selected, String failoverFrom){
    	this.controllerName=controllerName;
    	this.group			=group;
    	this.auto			=(auto!=0);
    	this.isControllerUp	=(up!=0);
    	this.isSelected		=(selected!=0);
    	this.failoverForcontroller=failoverFrom;
    }
    
   public boolean isIs_Selected(){
	   return isSelected;
   }
   
   public void setIs_Selected(boolean selected){
	   isSelected=selected;
   }
   
   public String getFailoverFor(){
	   return failoverForcontroller;
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
    
    public void setAvailabilityCheckDone() {
    	availabilityCheckDone = true;
    }
    
    public boolean isAvailabilityCheckDone() {
    	return availabilityCheckDone;
    }
    
    //the controller needs to keep this for immediatr fetch
 //   public ControllerObject[] getFailoverControllers(){
    	
//    }
}